package ru.jetlabs.core.gui.render;

import ru.jetlabs.core.environment.Level;
import ru.jetlabs.core.objects.Actor;
import ru.jetlabs.core.objects.components.armor.ArmorMaterial;
import ru.jetlabs.core.objects.components.armor.ArmorMesh;
import ru.jetlabs.core.objects.components.engines.impls.IdealEngine;
import ru.jetlabs.core.objects.components.engines.impls.IdealKineticGun;
import ru.jetlabs.core.objects.entities.Shell;
import ru.jetlabs.core.objects.entities.SpaceShip;
import ru.jetlabs.core.gui.render.formatters.Formatters;
import ru.jetlabs.core.util.structures.Vector2d;
import ru.jetlabs.develop.helpers.ArmorMeshView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;

public class Render {
    private final JPanel panel;
    private final Level level;
    private Actor selectedActor;

    private ArmorMeshView armorMeshView;

    private double scale = 1.0;
    private double offsetX = 0;
    private double offsetY = 0;
    private Point lastDragPoint;
    private long lastUpdateTime = System.currentTimeMillis();
    private int framesCount = 0;
    private double timeAccumulator = 0;
    private int fps = 0;

    public static void main(String[] args) {
        Level level = new Level();
        level.addActor(new SpaceShip(0,0, new IdealEngine(3.6*3.2),
                new IdealKineticGun(100,2,0.1,0.6,300_000_000),
                ArmorMaterial.STEEL, ArmorMaterial.PLASTEEL));

        level.addActor(new SpaceShip(100,100, new IdealEngine(3.6*3.2),
                new IdealKineticGun(100,2,0.1,0.5,300_000_000),
                ArmorMaterial.STEEL, ArmorMaterial.PLASTEEL));
        JFrame frame = new JFrame("Level Render");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Render render = new Render(level);
        frame.add(render.getPanel());

        frame.setSize(800, 600);
        frame.setVisible(true);
        frame.setResizable(false);
    }

    private void drawLevel(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        try {
            setupG2d(g2d);

            for (Actor actor : level.getActors()) {
                RenderActorBody result = renderActorBody(actor,
                        g2d, Color.WHITE, 6);
                renderActorLabel(actor, g2d, result);
            }

            for (Shell actor : level.getShells()) {
                RenderActorBody result = renderActorBody(actor,
                        g2d, new Color(255, 166, 0), 2);
                renderActorLabel(actor, g2d, result);
            }

            if(armorMeshView!=null){
                armorMeshView.repaint();
            }else if(selectedActor!=null&&selectedActor instanceof SpaceShip){
                ArmorMesh armor = ((SpaceShip) selectedActor).armor;
                armorMeshView = new ArmorMeshView(armor);
            }
            renderScalingParamsData(g2d);
        } finally {
            g2d.dispose();
        }
    }


    public Render(Level level) {
        this.level = level;
        this.panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawLevel(g);
            }
        };
        panel.setBackground(new Color(0, 0, 100));
        setupInputListeners();
        initTimer(level);
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
        String label = String.format("%s: %s", actor.name(), Formatters.formatSpeed(actor.speed()));
        FontMetrics metrics = g2d.getFontMetrics();
        int labelWidth = metrics.stringWidth(label);
        int labelX = result.x() - labelWidth / 2;
        int labelY = result.y() + result.actualDiameter() / 2 + metrics.getHeight() + 5;

        g2d.setColor(Color.GREEN);
        g2d.drawString(label, labelX, labelY);
    }

    private RenderActorBody renderActorBody(Actor actor, Graphics2D g2d, Color color, int minDiameter) {
        Vector2d coord = actor.getCoord();
        int x = (int) ((coord.getX() - offsetX) * scale);
        int y = (int) ((coord.getY() - offsetY) * scale);

        double radius = actor.radius();
        double screenDiameter = 2 * radius * scale;
        int actualDiameter = (int) Math.max(minDiameter, screenDiameter);

        if (actor == selectedActor) {
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
        Font labelFont = new Font("Arial", Font.BOLD, 14);
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
                    handleSelection(e.getPoint());
                    lastDragPoint = e.getPoint();
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e) && selectedActor != null) {
                    Point p = e.getPoint();
                    selectedActor.setTarget(
                            p.x / scale + offsetX,
                            p.y / scale + offsetY
                    );
                }else if (SwingUtilities.isMiddleMouseButton(e) && selectedActor != null) {
                    Point p = e.getPoint();
                    level.addShell(selectedActor.strike(
                            p.x / scale + offsetX,
                            p.y / scale + offsetY
                    ));
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
    private void handleSelection(Point clickPoint) {
        double worldX = clickPoint.x / scale + offsetX;
        double worldY = clickPoint.y / scale + offsetY;
        double selectionRadius = 5.0 / scale;

        selectedActor = null;
        for (Actor actor : level.getActors()) {
            Vector2d coord = actor.getCoord();
            double dx = coord.getX() - worldX;
            double dy = coord.getY() - worldY;
            if (dx*dx + dy*dy <= selectionRadius*selectionRadius) {
                selectedActor = actor;
                if(selectedActor instanceof SpaceShip) {
                    if(armorMeshView!=null){
                        armorMeshView.setVisible(false);
                    }
                    armorMeshView = new ArmorMeshView(((SpaceShip) selectedActor).armor);
                }
                break;
            }
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

            framesCount++;
            timeAccumulator += deltaTime;
            if (timeAccumulator >= 1.0) {
                fps = framesCount;
                framesCount = 0;
                timeAccumulator -= 1.0;
                System.out.println("FPS: " + fps+ ", delta time: "+ deltaTime);
            }
            repaint();
        });
        animationTimer.start();
    }
}