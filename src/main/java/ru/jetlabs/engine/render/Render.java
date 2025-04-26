package ru.jetlabs.engine.render;

import ru.jetlabs.engine.environment.Level;
import ru.jetlabs.engine.objects.Actor;
import ru.jetlabs.engine.objects.components.engines.impls.IdealEngine;
import ru.jetlabs.engine.objects.entities.SpaceShip;
import ru.jetlabs.engine.util.DPoint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

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


    private int framesCount = 0;
    private double timeAccumulator = 0;
    private int fps = 0;


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
            Font labelFont = new Font("Arial", Font.BOLD, 14);
            g2d.setFont(labelFont);

            for (Actor actor : level.getActors()) {
                DPoint coord = actor.getCoord();
                int x = (int) ((coord.getX() - offsetX) * scale);
                int y = (int) ((coord.getY() - offsetY) * scale);

                double radius = actor.radius();
                double screenDiameter = 2 * radius * scale;
                int minDiameter = 6;
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

                g2d.setColor(Color.WHITE);
                g2d.fillOval(x - actualDiameter / 2, y - actualDiameter / 2, actualDiameter, actualDiameter);

                String label = String.format("%s: %s", actor.name(), formatSpeed(actor.speed()));
                FontMetrics metrics = g2d.getFontMetrics();
                int labelWidth = metrics.stringWidth(label);
                int labelX = x - labelWidth / 2;
                int labelY = y + actualDiameter / 2 + metrics.getHeight() + 5;

                g2d.setColor(Color.GREEN);
                g2d.drawString(label, labelX, labelY);
            }

            int panelWidth = panel.getWidth();
            int panelHeight = panel.getHeight();
            if (panelWidth > 0 && panelHeight > 0) {
                double visibleWidth = panelWidth / scale;
                double visibleHeight = panelHeight / scale;

                String scaleText = formatScale(visibleWidth, visibleHeight);

                g2d.setColor(Color.WHITE);
                FontMetrics metrics = g2d.getFontMetrics();
                int textX = panelWidth - metrics.stringWidth(scaleText) - 15;
                int textY = panelHeight - metrics.getHeight() + metrics.getAscent() - 15;

                g2d.drawString(scaleText, textX, textY);
            }
        } finally {
            g2d.dispose();
        }
    }

    public static String formatScale(double visibleWidth, double visibleHeight) {
        String[] units = {"м", "км", "тыс. км", "млн км", "млрд км"};
        double[] thresholds = {1e3, 1e6, 1e9, 1e12};

        String formattedWidth = convertMeasurement(visibleWidth, units, thresholds);
        String formattedHeight = convertMeasurement(visibleHeight, units, thresholds);

        return String.format("Масштаб: %s : %s", formattedWidth, formattedHeight);
    }

    private static String convertMeasurement(double value, String[] units, double[] thresholds) {
        int unitIndex = 0;
        while (unitIndex < thresholds.length && Math.abs(value) >= thresholds[unitIndex]) {
            unitIndex++;
        }
        double divider = (unitIndex > 0) ? thresholds[unitIndex - 1] : 1;
        double convertedValue = value / divider;

        return String.format(Locale.US, "%.1f %s", convertedValue, units[unitIndex]);
    }

    private String formatSpeed(double speedMps) {
        String[] units = {"m/s", "km/s", "тыс. км/s", "млн км/s", "млрд км/s"};
        double[] thresholds = {1e3, 1e6, 1e9, 1e12};

        int unitIndex = 0;
        while (unitIndex < thresholds.length && Math.abs(speedMps) >= thresholds[unitIndex]) {
            unitIndex++;
        }

        double convertedSpeed = speedMps / (unitIndex > 0 ? thresholds[unitIndex-1] : 1);
        return String.format(Locale.US, "%.2f %s", convertedSpeed, units[unitIndex]);
    }

    public JComponent getPanel() {
        return panel;
    }

    public void repaint() {
        panel.repaint();
    }

    public static void main(String[] args) {
        Level level = new Level();
        level.addActor(new SpaceShip(0,0, new IdealEngine(10)));

        JFrame frame = new JFrame("Level Render");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Render render = new Render(level);
        frame.add(render.getPanel());

        frame.setSize(800, 600);
        frame.setVisible(true);
        frame.setResizable(false);
    }
}