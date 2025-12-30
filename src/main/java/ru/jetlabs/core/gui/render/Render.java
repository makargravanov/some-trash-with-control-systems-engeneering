package ru.jetlabs.core.gui.render;

import ru.jetlabs.core.environment.Level;
import ru.jetlabs.core.gui.*;
import ru.jetlabs.core.objects.Actor;
import ru.jetlabs.core.objects.components.armor.ArmorMesh;
import ru.jetlabs.core.objects.entities.ShipPresets;
import ru.jetlabs.core.objects.entities.Shell;
import ru.jetlabs.core.objects.entities.SpaceShip;
import ru.jetlabs.core.gui.render.formatters.Formatters;
import ru.jetlabs.core.util.structures.Vector2d;
import ru.jetlabs.develop.helpers.ArmorMeshView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Set;

public class Render {
    private final JPanel panel;
    private final Level level;
    private Actor selectedActor;
    private final GroupSelection groupSelection;

    private ArmorMeshView armorMeshView;
    private RadarContactPanel radarPanel;
    private ShipCreationDialog createDialog;

    private double scale = 1.0;
    private double offsetX = 0;
    private double offsetY = 0;
    private Point lastDragPoint;
    private long lastUpdateTime = System.currentTimeMillis();
    private int framesCount = 0;
    private double timeAccumulator = 0;
    private int fps = 0;

    private Frame mainFrame;

    public static void main(String[] args) {
        Level level = new Level();

        // Используем пресеты для создания кораблей
        level.addActor(ShipPresets.LIGHT_FIGHTER.createShip(0, 0));
        level.addActor(ShipPresets.HEAVY_FIGHTER.createShip(150, 150));

        JFrame frame = new JFrame("Space Combat Simulator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Render render = new Render(level, frame);
        frame.setLayout(new BorderLayout());
        frame.add(render.getPanel(), BorderLayout.CENTER);
        frame.add(render.getSidePanel(), BorderLayout.EAST);

        frame.setSize(1100, 600);
        frame.setVisible(true);
        frame.setResizable(true);
    }

    public Render(Level level, Frame frame) {
        this.level = level;
        this.groupSelection = new GroupSelection();
        this.mainFrame = frame;
        this.panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawLevel(g);
            }
        };
        panel.setBackground(new Color(0, 0, 100));
        setupInputListeners();
        setupKeyboardShortcuts();
        initTimer(level);
    }

    public JComponent getSidePanel() {
        JPanel sidePanel = new JPanel(new BorderLayout(10, 10));
        sidePanel.setPreferredSize(new Dimension(280, 0));
        sidePanel.setBackground(new Color(50, 50, 70));

        // Верхняя панель - выбор игрока
        PlayerSwitcherPanel playerPanel = new PlayerSwitcherPanel(groupSelection);
        sidePanel.add(playerPanel, BorderLayout.NORTH);

        // Центральная панель - радар
        radarPanel = new RadarContactPanel(groupSelection, level);
        sidePanel.add(new JScrollPane(radarPanel), BorderLayout.CENTER);

        // Нижняя панель - кнопки
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        buttonPanel.setBackground(new Color(50, 50, 70));

        JButton createBtn = new JButton("Create Ship (N)");
        createBtn.addActionListener(e -> showCreateDialog());
        buttonPanel.add(createBtn);

        JButton stopBtn = new JButton("Stop (Space)");
        stopBtn.addActionListener(e -> groupSelection.stop());
        buttonPanel.add(stopBtn);

        sidePanel.add(buttonPanel, BorderLayout.SOUTH);

        // Инструкции
        JTextArea helpText = new JTextArea(3, 20);
        helpText.setText("LMB: Select | RMB: Move | MMB: Fire\nCtrl+LMB: Add to selection\nDel: Delete | 1-4: Switch player");
        helpText.setEditable(false);
        helpText.setBackground(new Color(50, 50, 70));
        helpText.setForeground(Color.LIGHT_GRAY);
        sidePanel.add(helpText, BorderLayout.NORTH);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(sidePanel, BorderLayout.NORTH);
        wrapper.add(Box.createVerticalGlue(), BorderLayout.CENTER);
        return wrapper;
    }

    private void showCreateDialog() {
        if (createDialog == null || !createDialog.isDisplayable()) {
            createDialog = new ShipCreationDialog(mainFrame, level, groupSelection,
                this::enterPlacementMode,
                dummy -> repaint());
        }
        createDialog.setVisible(true);
    }

    private void enterPlacementMode() {
        panel.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
    }

    private void exitPlacementMode() {
        panel.setCursor(Cursor.getDefaultCursor());
        if (createDialog != null && createDialog.isDisplayable()) {
            createDialog.cancelPlacementMode();
        }
    }

    private void drawLevel(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        try {
            setupG2d(g2d);

            // Отрисовка акторов
            for (Actor actor : level.getActors()) {
                Color baseColor = actor.owner.color;
                RenderActorBody result = renderActorBody(actor, g2d, baseColor, 6);
                renderActorLabel(actor, g2d, result);
            }

            // Отрисовка снарядов
            for (Shell shell : level.getShells()) {
                RenderActorBody result = renderActorBody(shell, g2d, new Color(255, 166, 0), 2);
                renderActorLabel(shell, g2d, result);
            }

            // Режим размещения - показать курсор
            if (createDialog != null && createDialog.isInPlacementMode()) {
                Point mousePos = panel.getMousePosition();
                if (mousePos != null) {
                    g2d.setColor(new Color(0, 255, 0, 100));
                    g2d.drawOval(mousePos.x - 10, mousePos.y - 10, 20, 20);
                }
            }

            // Обновить радар если есть выбранный корабль
            updateRadarPanel();

            // Броня
            if(armorMeshView!=null){
                armorMeshView.repaint();
            }else if(selectedActor!=null&&selectedActor instanceof SpaceShip){
                ArmorMesh armor = ((SpaceShip) selectedActor).armor;
                armorMeshView = new ArmorMeshView(armor);
            }

            renderScalingParamsData(g2d);
            renderSelectionInfo(g2d);
        } finally {
            g2d.dispose();
        }
    }

    private void updateRadarPanel() {
        SpaceShip refShip = null;
        Set<Actor> selected = groupSelection.getSelected();
        if (!selected.isEmpty()) {
            Actor first = selected.iterator().next();
            if (first instanceof SpaceShip) {
                refShip = (SpaceShip) first;
            }
        }
        radarPanel.setReferenceShip(refShip);
        radarPanel.updateContacts();
    }

    private void renderSelectionInfo(Graphics2D g2d) {
        int count = groupSelection.getSelectedCount();
        if (count > 0) {
            String text = "Selected: " + count + " ship" + (count > 1 ? "s" : "");
            g2d.setColor(Color.YELLOW);
            g2d.drawString(text, 10, 20);
        }
    }

    private void renderScalingParamsData(Graphics2D g2d) {
        int panelWidth = panel.getWidth();
        int panelHeight = panel.getHeight();
        if (panelWidth > 0 && panelHeight > 0) {
            double visibleWidth = panelWidth / scale;
            double visibleHeight = panelHeight / scale;

            String scaleText = Formatters.formatScale(visibleWidth, visibleHeight);

            g2d.setColor(Color.WHITE);
            FontMetrics metrics = g2d.getFontMetrics();
            int textX = panelWidth - metrics.stringWidth(scaleText) - 15;
            int textY = panelHeight - metrics.getHeight() + metrics.getAscent() - 15;

            g2d.drawString(scaleText, textX, textY);
        }
    }

    private static void renderActorLabel(Actor actor, Graphics2D g2d, RenderActorBody result) {
        String label = String.format("%s", actor.name());
        FontMetrics metrics = g2d.getFontMetrics();
        int labelWidth = metrics.stringWidth(label);
        int labelX = result.x() - labelWidth / 2;
        int labelY = result.y() + result.actualDiameter() / 2 + metrics.getHeight() + 5;

        g2d.setColor(actor.owner.color);
        g2d.drawString(label, labelX, labelY);

        // Скорость
        String speedLabel = String.format("%.1f m/s", actor.speed());
        labelWidth = metrics.stringWidth(speedLabel);
        labelX = result.x() - labelWidth / 2;
        labelY += metrics.getHeight();
        g2d.setColor(Color.GREEN);
        g2d.drawString(speedLabel, labelX, labelY);
    }

    private RenderActorBody renderActorBody(Actor actor, Graphics2D g2d, Color color, int minDiameter) {
        Vector2d coord = actor.getCoord();
        int x = (int) ((coord.getX() - offsetX) * scale);
        int y = (int) ((coord.getY() - offsetY) * scale);

        double radius = actor.radius();
        double screenDiameter = 2 * radius * scale;
        int actualDiameter = (int) Math.max(minDiameter, screenDiameter);

        // Подсветка группового выделения
        if (groupSelection.isSelected(actor)) {
            int highlightDiameter = actualDiameter + 8;
            g2d.setColor(new Color(100, 200, 255, 120));
            g2d.fillOval(x - highlightDiameter / 2, y - highlightDiameter / 2, highlightDiameter, highlightDiameter);
            g2d.setColor(Color.CYAN);
            g2d.setStroke(new BasicStroke(2f));
            g2d.drawOval(x - highlightDiameter / 2, y - highlightDiameter / 2, highlightDiameter, highlightDiameter);
        }

        // Подсветка одиночного выделения
        if (actor == selectedActor && !groupSelection.isSelected(actor)) {
            int highlightDiameter = actualDiameter + 6;
            g2d.setColor(new Color(255, 101, 101, 150));
            g2d.fillOval(x - highlightDiameter / 2, y - highlightDiameter / 2, highlightDiameter, highlightDiameter);
            g2d.setColor(Color.RED);
            g2d.setStroke(new BasicStroke(2f));
            int borderDiameter = actualDiameter + 2;
            g2d.drawOval(x - borderDiameter / 2, y - borderDiameter / 2, borderDiameter, borderDiameter);
        }

        g2d.setColor(color);
        g2d.fillOval(x - actualDiameter / 2, y - actualDiameter / 2, actualDiameter, actualDiameter);
        return new RenderActorBody(x, y, actualDiameter);
    }

    private record RenderActorBody(int x, int y, int actualDiameter) {}

    private static void setupG2d(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Font labelFont = new Font("Arial", Font.BOLD, 12);
        g2d.setFont(labelFont);
    }

    public JComponent getPanel() {
        return panel;
    }

    public void repaint() {
        panel.repaint();
    }

    private void setupInputListeners() {
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    // Проверка на размещение корабля
                    if (createDialog != null && createDialog.isInPlacementMode()) {
                        createDialog.placeShipAt(e.getPoint());
                        exitPlacementMode();
                        return;
                    }

                    handleSelection(e.getPoint(), e.isControlDown());
                    lastDragPoint = e.getPoint();
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                // Режим размещения обрабатывается в mousePressed
                if (createDialog != null && createDialog.isInPlacementMode()) return;

                if (SwingUtilities.isRightMouseButton(e)) {
                    Point p = e.getPoint();
                    double wx = p.x / scale + offsetX;
                    double wy = p.y / scale + offsetY;
                    groupSelection.setTarget(wx, wy, level);
                } else if (SwingUtilities.isMiddleMouseButton(e)) {
                    Point p = e.getPoint();
                    double wx = p.x / scale + offsetX;
                    double wy = p.y / scale + offsetY;
                    groupSelection.fireAt(wx, wy, level);
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    handleDragging(e.getPoint());
                }
            }
        };

        panel.addMouseListener(mouseAdapter);
        panel.addMouseMotionListener(mouseAdapter);

        panel.addMouseWheelListener(e -> {
            double zoomFactor = e.getWheelRotation() < 0 ? 1.1 : 0.9;
            Point mousePos = e.getPoint();

            double oldScale = scale;
            scale *= zoomFactor;
            offsetX += (mousePos.x / oldScale - mousePos.x / scale);
            offsetY += (mousePos.y / oldScale - mousePos.y / scale);

            repaint();
        });
    }

    private void setupKeyboardShortcuts() {
        // Global shortcuts
        InputMap im = panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = panel.getActionMap();

        // Space - Stop
        im.put(KeyStroke.getKeyStroke("pressed SPACE"), "stop");
        am.put("stop", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                groupSelection.stop();
            }
        });

        // N - New ship
        im.put(KeyStroke.getKeyStroke("pressed N"), "newShip");
        am.put("newShip", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                showCreateDialog();
            }
        });

        // Delete - Delete selected
        im.put(KeyStroke.getKeyStroke("pressed DELETE"), "delete");
        am.put("delete", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                groupSelection.delete(level);
                repaint();
            }
        });

        // 1-4 - Switch player
        im.put(KeyStroke.getKeyStroke("pressed 1"), "player1");
        am.put("player1", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                groupSelection.setCurrentPlayer(ru.jetlabs.core.Player.PLAYER_1);
            }
        });

        im.put(KeyStroke.getKeyStroke("pressed 2"), "player2");
        am.put("player2", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                groupSelection.setCurrentPlayer(ru.jetlabs.core.Player.PLAYER_2);
            }
        });

        im.put(KeyStroke.getKeyStroke("pressed 3"), "player3");
        am.put("player3", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                groupSelection.setCurrentPlayer(ru.jetlabs.core.Player.PLAYER_3);
            }
        });

        im.put(KeyStroke.getKeyStroke("pressed 4"), "playerNeutral");
        am.put("playerNeutral", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                groupSelection.setCurrentPlayer(ru.jetlabs.core.Player.NEUTRAL);
            }
        });

        // Esc - Cancel placement
        im.put(KeyStroke.getKeyStroke("pressed ESCAPE"), "cancel");
        am.put("cancel", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (createDialog != null && createDialog.isInPlacementMode()) {
                    exitPlacementMode();
                }
                groupSelection.clear();
                repaint();
            }
        });
    }

    private void handleSelection(Point clickPoint, boolean addToSelection) {
        double worldX = clickPoint.x / scale + offsetX;
        double worldY = clickPoint.y / scale + offsetY;
        double selectionRadius = 5.0 / scale;

        Actor clickedActor = null;
        for (Actor actor : level.getActors()) {
            Vector2d coord = actor.getCoord();
            double dx = coord.getX() - worldX;
            double dy = coord.getY() - worldY;
            if (dx*dx + dy*dy <= selectionRadius*selectionRadius) {
                clickedActor = actor;
                break;
            }
        }

        groupSelection.select(clickedActor, addToSelection);
        selectedActor = addToSelection ? selectedActor : clickedActor;

        if (selectedActor instanceof SpaceShip && !groupSelection.isSelected(selectedActor)) {
            if(armorMeshView!=null){
                armorMeshView.setVisible(false);
            }
            armorMeshView = new ArmorMeshView(((SpaceShip) selectedActor).armor);
        }

        repaint();
    }

    private void handleDragging(Point newPoint) {
        if (lastDragPoint != null) {
            double dx = (newPoint.x - lastDragPoint.x) / scale;
            double dy = (newPoint.y - lastDragPoint.y) / scale;
            offsetX -= dx;
            offsetY -= dy;
            repaint();
        }
        lastDragPoint = newPoint;
    }

    private void initTimer(Level level) {
        Timer animationTimer = new Timer(4, e -> {
            long currentTime = System.currentTimeMillis();
            double deltaTime = (currentTime - lastUpdateTime) / 1000.0;
            lastUpdateTime = currentTime;

            level.updateActors(deltaTime);

            // Обновлять transform для диалога
            if (createDialog != null && createDialog.isDisplayable()) {
                createDialog.setViewTransform(scale, offsetX, offsetY);
            }

            framesCount++;
            timeAccumulator += deltaTime;
            if (timeAccumulator >= 1.0) {
                fps = framesCount;
                framesCount = 0;
                timeAccumulator -= 1.0;
            }
            repaint();
        });
        animationTimer.start();
    }
}
