package ru.jetlabs.develop.helpers;

import java.math.BigDecimal;

public class EnergyCalculator {

    public static double calculate(double mass, double speed) {
        double energy = 0.5 * mass * (speed * speed);
        String energyStr = new BigDecimal(Double.toString(energy)).toPlainString();
        System.out.println("Энергия = " + energyStr + " джоулей");
        return energy;
    }
}
