package ru.jetlabs.develop.helpers;

public class ShellMassCalculator {
    private static final double DENSITY = 19300.0;

    public static double calculateMass(double diameterMeters, double lengthMeters) {
        double radius = diameterMeters / 2.0;
        double volume = Math.PI * radius * radius * lengthMeters;
        System.out.println("Масса стержня: " + volume * DENSITY + " кг");
        return volume * DENSITY;
    }
}
