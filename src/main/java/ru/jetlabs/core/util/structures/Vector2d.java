package ru.jetlabs.core.util.structures;

import java.util.Objects;

public class Vector2d {
    private double x;
    private double y;

    public Vector2d(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector2d setX(double x) {
        this.x = x;
        return this;
    }

    public Vector2d setY(double y) {
        this.y = y;
        return this;
    }

    public double getX() { return x; }
    public double getY() { return y; }

    public Vector2d copy(){
        return new Vector2d(x,y);
    }

    @Override
    public String toString() {
        return "{"+x+":"+y+"}";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Vector2d vector2d = (Vector2d) o;
        return Double.compare(x, vector2d.x) == 0 && Double.compare(y, vector2d.y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}