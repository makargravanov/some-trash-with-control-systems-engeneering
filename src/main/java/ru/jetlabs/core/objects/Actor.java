package ru.jetlabs.core.objects;

import ru.jetlabs.core.Player;
import ru.jetlabs.core.environment.Level;
import ru.jetlabs.core.objects.components.Component;
import ru.jetlabs.core.objects.entities.Shell;
import ru.jetlabs.core.util.structures.Vector2d;

import java.util.List;

public class Actor {
    public Level level;
    public List<Actor> actors;
    public final Vector2d coord;
    public Vector2d target;
    public double mass;    //кг
    protected double thrust;  //Н
    public double velocityX = 0;     // скорость по X (м/с)
    public double velocityY = 0;     // скорость по Y (м/с)
    protected double kP = 3;
    protected double kD = 15;
    public double radius = 10;
    public String name = "Корабль 1";
    public Player owner = Player.PLAYER_1;  // Кто управляет
    public double heading = 0;        // radians, 0 = восток
    public double turnRate = 2.0;     // radians/sec

    public Actor(double x, double y, double mass, double thrust) {
        this.coord = new Vector2d(x, y);
        this.mass = mass;
        this.thrust = thrust;
        this.velocityX = 0;
        this.velocityY = 0;
    }

    public Actor(double x, double y, double mass, double thrust, double radius) {
        this.coord = new Vector2d(x, y);
        this.mass = mass;
        this.thrust = thrust;
        this.velocityX = 0;
        this.velocityY = 0;
        this.radius = radius;
    }

    public Shell strike(double x, double y){
        return null;
    }

    public Vector2d getCoord() {
        return coord;
    }

    public void setTarget(double x, double y) {
        System.out.println(radius);
        target = new Vector2d(x, y);

        double dx = x - coord.getX();
        double dy = y - coord.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        kD = Math.sqrt(distance / 20);
    }

    public void setHeading(double targetHeading, double deltaTime) {
        double diff = normalizeAngle(targetHeading - heading);
        double maxTurn = turnRate * deltaTime;
        heading += Math.signum(diff) * Math.min(Math.abs(diff), maxTurn);
    }

    protected double normalizeAngle(double a) {
        while (a > Math.PI) a -= 2 * Math.PI;
        while (a < -Math.PI) a += 2 * Math.PI;
        return a;
    }

    public void destroy() {
        if (level != null) {
            level.removeActor(this);
        }
    }

    public void hit(Vector2d hitCoord) {
    }

    public void update(double deltaTime) {
    }

    private void handleCollision(Vector2d oldPos, Vector2d newPos, Vector2d target, double radius) {
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

    private void handleCollision(Vector2d oldPos, Vector2d newPos, Actor target) {
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

    public void applyKineticDamage(double mass, double speed) {

    }

    public double radius() {
        return radius;
    }

    public double speed() {
        return Math.sqrt(velocityX * velocityX + velocityY * velocityY);
    }

    public String name() {
        return name;
    }
}