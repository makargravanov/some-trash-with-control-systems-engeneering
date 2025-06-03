package ru.jetlabs.develop.helpers;

public class RadiusCalculator {

    public static double getRadius(double size){
        System.out.println("Радиус = " + Math.sqrt(size+5)/Math.PI);
        return Math.sqrt((size+5)/Math.PI);
    }

    public static double getCircleLength(double radius){
        double circumference = 2 * Math.PI * radius;
        System.out.println("Длина окружности = " + circumference);
        return circumference;
    }
}
