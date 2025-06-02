package ru.jetlabs.core.objects.components.armor;

public class Armor {
    public double mass; // кг
    public double hp; // целостность в %
    public ArmorMaterial material;

    public Armor(ArmorMaterial material) {
        this.material = material;
        this.mass = material.mass;
        this.hp = 60_000_000;
    }
}