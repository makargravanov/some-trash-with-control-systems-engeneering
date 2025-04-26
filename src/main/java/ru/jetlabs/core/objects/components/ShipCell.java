package ru.jetlabs.core.objects.components;

public class ShipCell {
    private Component component;

    public ShipCell(Component component){
        this.component = component;
    }

    public Component component() {
        return component;
    }

    public ShipCell setComponent(Component component) {
        this.component = component;
        return this;
    }
}
