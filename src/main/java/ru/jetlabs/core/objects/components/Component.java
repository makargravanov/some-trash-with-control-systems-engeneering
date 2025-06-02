package ru.jetlabs.core.objects.components;


import java.io.Serializable;

public abstract class Component implements Serializable {
    protected double mass;
    protected double hp;
    protected double size;

    public Component(double mass, double size) {
        this.mass = mass;
        this.hp = 50;
        this.size = size;
    }
    public Component(double mass, double hp, double size) {
        this.mass = mass;
        this.hp = hp;
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
