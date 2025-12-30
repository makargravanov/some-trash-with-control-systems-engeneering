package ru.jetlabs.core.objects.sensors;

import ru.jetlabs.core.objects.Actor;
import ru.jetlabs.core.util.structures.Vector2d;

import java.util.ArrayList;
import java.util.List;

public class ActiveRadar implements Sensor {
    private final double maxRange;
    private final double beamWidth;      // radians, полный конус
    private final double noiseDistance;  // метры
    private final double noiseAngle;     // radians
    private boolean active = true;       // можно выключать

    public ActiveRadar(double maxRange, double beamWidth) {
        this.maxRange = maxRange;
        this.beamWidth = beamWidth;
        this.noiseDistance = maxRange * 0.02;  // 2% ошибки
        this.noiseAngle = 0.02;                // ~1 градус
    }

    @Override
    public List<Contact> scan(List<Actor> world, Vector2d myPos, double myHeading) {
        List<Contact> contacts = new ArrayList<>();

        for (Actor target : world) {
            // Шаг 1: проверка дистанции (быстро!)
            double dist = myPos.distanceTo(target.coord);
            if (dist > maxRange || dist < 1) continue;

            // Шаг 2: проверка угла (в пределах beamWidth?)
            double angleToTarget = Math.atan2(
                target.coord.getY() - myPos.getY(),
                target.coord.getX() - myPos.getX()
            );
            double angleDiff = normalizeAngle(angleToTarget - myHeading);

            if (Math.abs(angleDiff) > beamWidth / 2) continue;

            // Шаг 3: создаём контакт с шумом
            double noisyDist = dist + (Math.random() - 0.5) * noiseDistance * 2;
            double noisyAngle = angleDiff + (Math.random() - 0.5) * noiseAngle * 2;

            contacts.add(new Contact(
                target.hashCode(),  // Простой ID
                noisyDist,
                normalizeAngle(myHeading + noisyAngle),
                Math.max(0, 1 - dist / maxRange),  // confidence падает с расстоянием
                System.currentTimeMillis(),
                ContactType.ACTIVE
            ));
        }

        return contacts;
    }

    @Override
    public boolean isDetected(Actor target, Vector2d myPos) {
        return myPos.distanceTo(target.coord) <= maxRange;
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
