package ru.jetlabs;

import ru.jetlabs.develop.helpers.EnergyCalculator;
import ru.jetlabs.develop.helpers.RadiusCalculator;
import ru.jetlabs.develop.helpers.ShellMassCalculator;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

        EnergyCalculator.calculate(
                ShellMassCalculator.calculateMass(0.05,0.4),
                30000);
        EnergyCalculator.calculate(
                0.0061,
                315);
        RadiusCalculator.getCircleLength(RadiusCalculator.getRadius(11.52));
    }
}