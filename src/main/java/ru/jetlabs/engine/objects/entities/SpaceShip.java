package ru.jetlabs.engine.objects.entities;

import ru.jetlabs.engine.objects.Actor;
import ru.jetlabs.engine.objects.Ship;
import ru.jetlabs.engine.objects.components.Component;
import ru.jetlabs.engine.objects.components.engines.Engine;

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
