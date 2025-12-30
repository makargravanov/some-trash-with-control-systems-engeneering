package ru.jetlabs.core.objects.entities;

import ru.jetlabs.core.objects.components.armor.ArmorMaterial;
import ru.jetlabs.core.objects.components.engines.Engine;
import ru.jetlabs.core.objects.components.engines.impls.IdealEngine;
import ru.jetlabs.core.objects.components.engines.impls.IdealKineticGun;
import ru.jetlabs.core.objects.sensors.ActiveRadar;
import ru.jetlabs.core.objects.sensors.PassiveRadar;
import ru.jetlabs.core.objects.sensors.SensorSystem;

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
        double radarRange,           // NEW
        double passiveRadarRange,    // NEW
        ArmorMaterial[] armorMaterials
) {
    public Engine createEngine() {
        return new IdealEngine(engineSize);
    }

    public IdealKineticGun createGun() {
        if (!hasGun) return null;
        return new IdealKineticGun(gunMass, gunSize, gunCaliber, gunShellLength, gunPower, gunCooldown);
    }

    public SensorSystem createSensorSystem() {
        SensorSystem system = new SensorSystem(50, 5);
        system.addSensor(new ActiveRadar(radarRange, Math.PI / 3));  // 60° FOV
        system.addSensor(new PassiveRadar(passiveRadarRange, Math.PI / 2));
        return system;
    }

    public SpaceShip createShip(double x, double y) {
        SpaceShip ship = new SpaceShip(x, y, createEngine(), createGun(), armorMaterials);
        ship.sensorSystem = createSensorSystem();
        return ship;
    }
}