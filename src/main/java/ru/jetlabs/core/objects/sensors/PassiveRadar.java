package ru.jetlabs.core.objects.sensors;

import ru.jetlabs.core.objects.Actor;
import ru.jetlabs.core.objects.entities.SpaceShip;
import ru.jetlabs.core.util.structures.Vector2d;

import java.util.ArrayList;
import java.util.List;

public class PassiveRadar implements Sensor {
    private final double maxRange;
    private final double beamWidth;
    private final double noiseAngle;

    public PassiveRadar(double maxRange, double beamWidth) {
        this.maxRange = maxRange;
        this.beamWidth = beamWidth;
        this.noiseAngle = 0.03;  // чуть больше шума чем у активного
    }

    @Override
    public List<Contact> scan(List<Actor> world, Vector2d myPos, double myHeading) {
        List<Contact> contacts = new ArrayList<>();

        for (Actor target : world) {
            // Только излучающие цели!
            if (!isEmitting(target)) continue;

            double dist = myPos.distanceTo(target.coord);
            if (dist > maxRange) continue;

            double angleToTarget = Math.atan2(
                target.coord.getY() - myPos.getY(),
                target.coord.getX() - myPos.getX()
            );
            double angleDiff = normalizeAngle(angleToTarget - myHeading);

            if (Math.abs(angleDiff) > beamWidth / 2) continue;

            // Пассивный: только bearing, без distance!
            contacts.add(new Contact(
                target.hashCode(),
                null,  // distance UNKNOWN
                normalizeAngle(myHeading + angleDiff),
                0.7,   // ниже confidence чем у активного
                System.currentTimeMillis(),
                ContactType.PASSIVE
            ));
        }

        return contacts;
    }

    private boolean isEmitting(Actor target) {
        // Простая проверка: у корабля есть активный радар
        if (target instanceof SpaceShip ship) {
            return ship.sensorSystem != null && ship.sensorSystem.hasActiveRadarOn();
        }
        return false;
    }

    @Override
    public boolean isDetected(Actor target, Vector2d myPos) {
        return isEmitting(target) && myPos.distanceTo(target.coord) <= maxRange;
    }

    @Override
    public double getMaxRange() {
        return maxRange;
    }

    private double normalizeAngle(double a) {
        while (a > Math.PI) a -= 2 * Math.PI;
        while (a < -Math.PI) a += 2 * Math.PI;
        return a;
    }
}
