package ru.jetlabs.core.objects.entities;

import ru.jetlabs.core.objects.Actor;
import ru.jetlabs.core.util.structures.Vector2d;

import java.util.List;

public class Shell extends Actor {
    public List<Shell> shells;
    public final SpaceShip parent;
    private static final double DENSITY = 19300.0;

    public Shell(double x, double y, Vector2d velocity, double len, double diam, SpaceShip parent) {
        super(x, y,
                Math.PI * (diam / 2.0) * (diam / 2.0) * len * DENSITY,
                0,
                (Math.sqrt((len*diam)/Math.PI)));
        velocityX = velocity.getX();
        velocityY = velocity.getY();
        this.parent = parent;
        name = "Снаряд, масса: " + mass;
    }

    public void update(double deltaTime) {
        var oldPos = coord.copy();
        coord.setX(coord.getX() + velocityX * deltaTime);
        coord.setY(coord.getY() + velocityY * deltaTime);

        for (Actor actor : actors) {
            if (actor == parent) {
                continue;
            }
            if (checkIntersection(oldPos, coord, actor.coord, actor.radius)) {
                System.out.println("hit!");
                // 2. Вычисляем нормаль столкновения (направление от this к actor)
                double nx = actor.coord.getX() - coord.getX();
                double ny = actor.coord.getY() - coord.getY();
                double intersectDistance = Math.hypot(nx, ny);
                if (intersectDistance > 0) { // Нормализуем вектор
                    nx /= intersectDistance;
                    ny /= intersectDistance;
                }

                // 3. Вычисляем относительную скорость вдоль нормали
                double relativeVelocityX = actor.velocityX - this.velocityX;
                double relativeVelocityY = actor.velocityY - this.velocityY;
                double velocityAlongNormal = relativeVelocityX * nx + relativeVelocityY * ny;

                // 5. Коэффициент упругости (1.0 = абсолютно упругий, 0.0 = абсолютно неупругий)
                double restitution = 0.8;

                // 6. Импульс столкновения
                double impulse = -(1 + restitution) * velocityAlongNormal;
                impulse /= (1 / this.mass + 1 / actor.mass); // Учитываем массы (если есть)

                if (actor.target == null) {
                    actor.target = actor.coord.copy();
                }


                actor.applyKineticDamage(mass, velocityAlongNormal);

                actor.velocityX += impulse * nx / actor.mass;
                actor.velocityY += impulse * ny / actor.mass;

                System.out.println(actor.velocityX + " " +
                        actor.velocityY);

                destroy();
            }
        }
    }

    public void destroy() {
        shells.remove(this);
    }

    private boolean checkIntersection(Vector2d a, Vector2d b, Vector2d center, double radius) {
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
}
