package ru.jetlabs.core.util.structures;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        DPoint dPoint = (DPoint) o;
        return Double.compare(x, dPoint.x) == 0 && Double.compare(y, dPoint.y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}