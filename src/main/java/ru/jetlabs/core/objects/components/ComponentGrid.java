package ru.jetlabs.core.objects.components;

import java.io.Serializable;

public class ComponentGrid implements Serializable {
    private final Component[] components;

    public int width;
    public int height;

    public ComponentGrid(int rows, int columns) {
        this.components = new Component[rows * columns];
        this.width = columns;
        this.height = rows;
    };

    Component getComponent(int row, int column) {
        if (row < 0 || row >= height || column < 0 || column >= width) {
            return null;
        }
        return components[row * width + column];
    }

    void addComponent(int row, int column,Component component) {
        if (row >= 0 && row < height && column >= 0 && column < width) {
            components[row * width + column] = component;
        }
    }
};