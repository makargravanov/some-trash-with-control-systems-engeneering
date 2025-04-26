package ru.jetlabs.engine.render;

import ru.jetlabs.engine.environment.Level;
import ru.jetlabs.engine.objects.Actor;
import ru.jetlabs.engine.util.DPoint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

public class Render {
    private final JPanel panel;
    private final Level level;
    private Actor selectedActor;

    private double scale = 1.0;
    private double offsetX = 0;
    private double offsetY = 0;
    private Point lastDragPoint;

    private final Map<KeyStroke, Boolean> keys = new HashMap<>();

    private long lastUpdateTime = System.currentTimeMillis();

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

        Timer animationTimer = new Timer(4, e -> {
            long currentTime = System.currentTimeMillis();
            double deltaTime = (currentTime - lastUpdateTime) / 1000.0;
            lastUpdateTime = currentTime;

            level.updateActors(deltaTime);
            repaint();
        });
        animationTimer.start();
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
            DPoint coord = actor.getCoord();
            double dx = coord.getX() - worldX;
            double dy = coord.getY() - worldY;
            if (dx*dx + dy*dy <= selectionRadius*selectionRadius) {
                selectedActor = actor;
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

    private void drawLevel(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        try {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            for (Actor actor : level.getActors()) {
                DPoint coord = actor.getCoord();
                int x = (int) ((coord.getX() - offsetX) * scale);
                int y = (int) ((coord.getY() - offsetY) * scale);

                if (actor == selectedActor) {
                    g2d.setColor(new Color(255, 101, 101, 150));
                    g2d.fillOval(x - 6, y - 6, 12, 12);
                    g2d.setColor(Color.RED);
                    g2d.setStroke(new BasicStroke(2f));
                    g2d.drawOval(x - 5, y - 5, 10, 10);
                }
                g2d.setColor(Color.WHITE);
                g2d.fillOval(x - 3, y - 3, 6, 6);
            }
        } finally {
            g2d.dispose();
        }
    }

    public JComponent getPanel() {
        return panel;
    }

    public void repaint() {
        panel.repaint();
    }

    public static void main(String[] args) {
        Level level = new Level();
        level.addActor(new Actor(0, 0, 10.0, 5000.0));

        JFrame frame = new JFrame("Level Render");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Render render = new Render(level);
        frame.add(render.getPanel());

        frame.setSize(800, 600);
        frame.setVisible(true);
        frame.setResizable(false);
    }
}