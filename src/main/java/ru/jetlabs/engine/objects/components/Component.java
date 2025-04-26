package ru.jetlabs.engine.objects.components;

public abstract class Component {
    protected double mass;
    protected double hp;
    protected double size;

    public Component(double mass, double size) {
        this.mass = mass;
        hp = 50;
        this.size = size;
    }

    public double mass() {
        return mass;
    }

    public Component setMass(double mass) {
        this.mass = mass;
        return this;
    }

    public double hp() {
        return hp;
    }

    public Component setHp(double hp) {
        this.hp = hp;
        return this;
    }

    public double size() {
        return size;
    }
}
