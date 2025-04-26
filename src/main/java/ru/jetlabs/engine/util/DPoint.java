package ru.jetlabs.engine.util;

public class DPoint {
    private double x;
    private double y;

    public DPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public DPoint setX(double x) {
        this.x = x;
        return this;
    }

    public DPoint setY(double y) {
        this.y = y;
        return this;
    }

    public double getX() { return x; }
    public double getY() { return y; }

    public DPoint copy(){
        return new DPoint(x,y);
    }

    @Override
    public String toString() {
        return "{"+x+":"+y+"}";
    }
}