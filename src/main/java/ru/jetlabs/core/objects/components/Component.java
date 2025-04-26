package ru.jetlabs.core.objects.components;



public abstract class Component {
    protected double mass;
    protected double hp;
    protected double size;
    protected ComponentType type;

    public Component(double mass, double size, ComponentType type) {
        this.mass = mass;
        hp = 50;
        this.size = size;
        this.type = type;
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

    public ComponentType type(){
        return this.type;
    }
}
