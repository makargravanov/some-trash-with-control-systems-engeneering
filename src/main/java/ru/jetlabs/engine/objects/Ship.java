package ru.jetlabs.engine.objects;

import ru.jetlabs.engine.objects.components.Component;

public interface Ship {

    void calculateMaxThrust();
    double calculateMass(Component... components);
}
