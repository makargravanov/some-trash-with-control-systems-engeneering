package ru.jetlabs.core.objects.entities;

import ru.jetlabs.core.objects.Actor;
import ru.jetlabs.core.objects.Ship;
import ru.jetlabs.core.objects.components.Component;
import ru.jetlabs.core.objects.components.engines.Engine;

public class SpaceShip extends Actor implements Ship {
    public Engine engine;

    public SpaceShip(double x, double y, Engine engine) {
        super(x, y, calculateMass((Component) engine), engine.thrust(), calculateRadius(engine.getSize()));
    }

    @Override
    public void calculateMaxThrust() {
    }


    public static double calculateRadius(double totalSize) {
        return Math.sqrt(totalSize+10)/Math.PI;
    }

    public static double calculateMass(Component... components){
        double totalMass = 50;
        for (Component c : components){
            totalMass+=c.mass();
        }
        return totalMass;
    }
}
