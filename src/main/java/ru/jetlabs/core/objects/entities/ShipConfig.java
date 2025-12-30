package ru.jetlabs.core.objects.entities;

import ru.jetlabs.core.objects.components.armor.ArmorMaterial;
import ru.jetlabs.core.objects.components.engines.Engine;
import ru.jetlabs.core.objects.components.engines.impls.IdealEngine;
import ru.jetlabs.core.objects.components.engines.impls.IdealKineticGun;

public record ShipConfig(
        String name,
        double baseMass,
        double engineSize,
        boolean hasGun,
        double gunMass,
        double gunSize,
        double gunCaliber,
        double gunShellLength,
        double gunPower,
        double gunCooldown,
        ArmorMaterial[] armorMaterials
) {
    public Engine createEngine() {
        return new IdealEngine(engineSize);
    }

    public IdealKineticGun createGun() {
        if (!hasGun) return null;
        return new IdealKineticGun(gunMass, gunSize, gunCaliber, gunShellLength, gunPower, gunCooldown);
    }

    public SpaceShip createShip(double x, double y) {
        return new SpaceShip(x, y, createEngine(), createGun(), armorMaterials);
    }
}