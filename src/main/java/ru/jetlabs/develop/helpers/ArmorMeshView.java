package ru.jetlabs.develop.helpers;

import ru.jetlabs.core.objects.components.armor.Armor;
import ru.jetlabs.core.objects.components.armor.ArmorMaterial;
import ru.jetlabs.core.objects.components.armor.ArmorMesh;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ArmorMeshView extends JPanel {
    private static final int CELL_SIZE = 40;
    private static final int PADDING = 5;
    
    private final ArmorMesh mesh;
    private final Map<ArmorMaterial, Color> colorMap;
    private final Font statusFont = new Font("SansSerif", Font.PLAIN, 10);

    public ArmorMeshView(ArmorMesh mesh) {
        this.mesh = mesh;
        setPreferredSize(new Dimension(
                mesh.size * CELL_SIZE + PADDING * 2,
                mesh.depth * CELL_SIZE + PADDING * 2
        ));
        setBackground(Color.DARK_GRAY);
        
        // Инициализация цветов для материалов
        colorMap = new HashMap<>();
        colorMap.put(ArmorMaterial.STEEL, new Color(100, 100, 180));
        colorMap.put(ArmorMaterial.PLASTEEL, new Color(132, 100, 180));
        colorMap.put(ArmorMaterial.DURASTEEL, new Color(175, 100, 180));
        colorMap.put(ArmorMaterial.CERAMIC_COMPOSITE, new Color(100, 180, 161));
        colorMap.put(ArmorMaterial.TITANIUM_COMPOSITE, new Color(100, 180, 123));
        colorMap.put(ArmorMaterial.NANO_CARBON, new Color(157, 180, 100));

        JFrame frame = new JFrame("Armor Mesh Viewer");
        frame.add(this);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        for (int layer = 0; layer < mesh.depth; layer++) {
            for (int pos = 0; pos < mesh.size; pos++) {
                drawCell(g2d, pos, layer);
            }
        }
    }

    private void drawCell(Graphics2D g, int x, int y) {
        int xPos = PADDING + x * CELL_SIZE;
        int yPos = PADDING + y * CELL_SIZE;
        
        Armor cell = mesh.grid[y * mesh.size + x];
        
        // Рисуем разрушенную ячейку
        if (cell == null) {
            g.setColor(new Color(30, 30, 30));
            g.fillRect(xPos, yPos, CELL_SIZE, CELL_SIZE);
            g.setColor(Color.RED);
            g.drawLine(xPos, yPos, xPos + CELL_SIZE, yPos + CELL_SIZE);
            g.drawLine(xPos + CELL_SIZE, yPos, xPos, yPos + CELL_SIZE);
            return;
        }
        
        // Рисуем целую ячейку
        Color baseColor = colorMap.getOrDefault(cell.material, Color.GRAY);
        
        // Градиент в зависимости от прочности
        float durability = (float) (cell.hp / 60_000_000);
        Color cellColor = new Color(
                (int) (baseColor.getRed() * durability),
                (int) (baseColor.getGreen() * durability),
                (int) (baseColor.getBlue() * durability)
        );
        
        g.setColor(cellColor);
        g.fillRect(xPos, yPos, CELL_SIZE, CELL_SIZE);
        g.setColor(Color.BLACK);
        g.drawRect(xPos, yPos, CELL_SIZE, CELL_SIZE);
        
        // Информация о ячейке
        g.setFont(statusFont);
        g.setColor(Color.WHITE);
        String hpText = String.format("%.0f", cell.hp / 1_000_000) + "M";
        g.drawString(hpText, xPos + 2, yPos + 12);
        
        g.setColor(Color.YELLOW);
        String matText = cell.material.name().substring(0, 3);
        g.drawString(matText, xPos + CELL_SIZE - 20, yPos + CELL_SIZE - 5);
    }

    public static void main(String[] args) {
        // Пример использования
        ArmorMesh mesh = new ArmorMesh(
                15,
                ArmorMaterial.CERAMIC_COMPOSITE,
                ArmorMaterial.STEEL,
                ArmorMaterial.STEEL,
                ArmorMaterial.STEEL,
                ArmorMaterial.STEEL
        );

        System.out.println(EnergyCalculator.calculate(3,3000));
        System.out.println(mesh.applyKineticDamage(3,3000).energy());
        
        JFrame frame = new JFrame("Armor Mesh Viewer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new ArmorMeshView(mesh));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}