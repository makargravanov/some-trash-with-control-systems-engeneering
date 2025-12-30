package ru.jetlabs.core.objects.sensors;

import ru.jetlabs.core.objects.Actor;
import ru.jetlabs.core.util.structures.Vector2d;

import java.util.List;

public interface Sensor {
    List<Contact> scan(List<Actor> world, Vector2d myPos, double myHeading);

    boolean isDetected(Actor target, Vector2d myPos);

    double getMaxRange();
}
