package ru.jetlabs.engine.objects;

import ru.jetlabs.engine.util.DPoint;

public class Actor {
    private final DPoint coord;
    private DPoint target;
    private double mass;    // масса в кг
    private double thrust;  // тяга в Н
    private double velocityX;     // скорость по X (м/с)
    private double velocityY;     // скорость по Y (м/с)
    private double kP = 10;
    private double kD = 15;
    private double radius = 10;

    public Actor(double x, double y, double mass, double thrust) {
        this.coord = new DPoint(x, y);
        this.mass = mass;
        this.thrust = thrust;
        this.velocityX = 0;
        this.velocityY = 0;
    }

    public Actor(double x, double y, double mass, double thrust, double radius) {
        this.coord = new DPoint(x, y);
        this.mass = mass;
        this.thrust = thrust;
        this.velocityX = 0;
        this.velocityY = 0;
        this.radius = radius;
    }

    public DPoint getCoord() { return coord; }

    public static double calculateRadius(double totalSize) {
        return Math.sqrt(totalSize+10)/Math.PI;
    }

    public void setTarget(double x, double y) {
        target = new DPoint(x, y);

        double dx = x - coord.getX();
        double dy = y - coord.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        System.out.println(distance);
        kD = Math.sqrt(distance/100);
        System.out.println(kD);
    }

    public void destroy(){}
    public void hit(DPoint hitCoord){}

    public void update(double deltaTime) {
        if (target == null) return;
        DPoint oldCoord = coord.copy();
        // Рассчитываем вектор направления к цели
        double dx = target.getX() - coord.getX();
        double dy = target.getY() - coord.getY();

        double desiredAx = (dx * kP) - (velocityX * kD);
        double desiredAy = (dy * kP) - (velocityY * kD);
        double maxAccel = thrust / mass;
        double desiredAccel = Math.hypot(desiredAx, desiredAy);

        if (desiredAccel > maxAccel) {
            double scale = maxAccel / desiredAccel;
            desiredAx *= scale;
            desiredAy *= scale;
        }

        // Обновление скорости
        double newVelocityX = velocityX + desiredAx * deltaTime;
        double newVelocityY = velocityY + desiredAy * deltaTime;

        // Рассчитываем новую позицию
        DPoint newCoord = new DPoint(
                coord.getX() + newVelocityX * deltaTime,
                coord.getY() + newVelocityY * deltaTime
        );

        // Проверяем пересечение пути с окружностью цели
        if (checkIntersection(oldCoord, newCoord, target, 10)) {
            handleCollision(oldCoord, newCoord, target, 10);
            target = null;
            velocityX = 0;
            velocityY = 0;
        } else {
          coord.setX(newCoord.getX());
          coord.setY(newCoord.getY());
          velocityX = newVelocityX;
          velocityY = newVelocityY;
        }
    }

    private boolean checkIntersection(DPoint a, DPoint b, DPoint center, double radius) {
        double distA = Math.hypot(a.getX() - center.getX(), a.getY() - center.getY());
        if (distA <= radius) return true;

        double distB = Math.hypot(b.getX() - center.getX(), b.getY() - center.getY());
        if (distB <= radius) return true;

        double dx = b.getX() - a.getX();
        double dy = b.getY() - a.getY();
        double acx = center.getX() - a.getX();
        double acy = center.getY() - a.getY();

        double A = dx * dx + dy * dy;
        if (A == 0) return false;

        double B = 2 * (dx * acx + dy * acy);
        double C = acx * acx + acy * acy - radius * radius;

        double discriminant = B * B - 4 * A * C;
        if (discriminant < 0) return false;

        discriminant = Math.sqrt(discriminant);
        double t1 = (-B - discriminant) / (2 * A);
        double t2 = (-B + discriminant) / (2 * A);

        return (t1 >= 0 && t1 <= 1) || (t2 >= 0 && t2 <= 1);
    }

    private void handleCollision(DPoint oldPos, DPoint newPos, DPoint target, double radius) {
        double ax = oldPos.getX();
        double ay = oldPos.getY();
        double bx = newPos.getX();
        double by = newPos.getY();
        double cx = target.getX();
        double cy = target.getY();

        double dx = bx - ax;
        double dy = by - ay;
        double acx = cx - ax;
        double acy = cy - ay;

        double A = dx * dx + dy * dy;
        double B = 2 * (dx * acx + dy * acy);
        double C = acx * acx + acy * acy - radius * radius;

        double discriminant = B * B - 4 * A * C;
        discriminant = Math.sqrt(discriminant);

        double t1 = (-B - discriminant) / (2 * A);
        double t2 = (-B + discriminant) / (2 * A);

        double t = 1;
        if (t1 >= 0 && t1 <= 1) t = Math.min(t1, t);
        if (t2 >= 0 && t2 <= 1) t = Math.min(t2, t);

        double hitX = ax + dx * t;
        double hitY = ay + dy * t;
        coord.setX(hitX);
        coord.setY(hitY);
    }

    private void handleCollision(DPoint oldPos, DPoint newPos, Actor target) {
        double ax = oldPos.getX();
        double ay = oldPos.getY();
        double bx = newPos.getX();
        double by = newPos.getY();
        double cx = target.coord.getX();
        double cy = target.coord.getY();

        double dx = bx - ax;
        double dy = by - ay;
        double acx = cx - ax;
        double acy = cy - ay;

        double A = dx * dx + dy * dy;
        double B = 2 * (dx * acx + dy * acy);
        double C = acx * acx + acy * acy - target.radius * target.radius;

        double discriminant = B * B - 4 * A * C;
        discriminant = Math.sqrt(discriminant);

        double t1 = (-B - discriminant) / (2 * A);
        double t2 = (-B + discriminant) / (2 * A);

        double t = 1;
        if (t1 >= 0 && t1 <= 1) t = Math.min(t1, t);
        if (t2 >= 0 && t2 <= 1) t = Math.min(t2, t);

        double hitX = ax + dx * t;
        double hitY = ay + dy * t;
        coord.setX(hitX);
        coord.setY(hitY);
    }
}