package ru.jetlabs.core.gui.render.formatters;

import java.util.Locale;

public class Formatters {
    public static String formatScale(double visibleWidth, double visibleHeight) {
        String[] units = {"м", "км", "тыс. км", "млн км", "млрд км"};
        double[] thresholds = {1e3, 1e6, 1e9, 1e12};

        String formattedWidth = convertMeasurement(visibleWidth, units, thresholds);
        String formattedHeight = convertMeasurement(visibleHeight, units, thresholds);

        return String.format("Масштаб: %s : %s", formattedWidth, formattedHeight);
    }

    public static String convertMeasurement(double value, String[] units, double[] thresholds) {
        int unitIndex = 0;
        while (unitIndex < thresholds.length && Math.abs(value) >= thresholds[unitIndex]) {
            unitIndex++;
        }
        double divider = (unitIndex > 0) ? thresholds[unitIndex - 1] : 1;
        double convertedValue = value / divider;

        return String.format(Locale.US, "%.1f %s", convertedValue, units[unitIndex]);
    }

    public static String formatSpeed(double speedMps) {
        String[] units = {"m/s", "km/s", "тыс. км/s", "млн км/s", "млрд км/s"};
        double[] thresholds = {1e3, 1e6, 1e9, 1e12};

        int unitIndex = 0;
        while (unitIndex < thresholds.length && Math.abs(speedMps) >= thresholds[unitIndex]) {
            unitIndex++;
        }

        double convertedSpeed = speedMps / (unitIndex > 0 ? thresholds[unitIndex-1] : 1);
        return String.format(Locale.US, "%.2f %s", convertedSpeed, units[unitIndex]);
    }
}
