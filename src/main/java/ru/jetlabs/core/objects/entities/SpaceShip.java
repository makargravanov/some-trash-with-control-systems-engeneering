package ru.jetlabs.core.objects.entities;

import ru.jetlabs.core.objects.Actor;
import ru.jetlabs.core.objects.Ship;
import ru.jetlabs.core.objects.components.Component;
import ru.jetlabs.core.objects.components.armor.ArmorMaterial;
import ru.jetlabs.core.objects.components.armor.ArmorMesh;
import ru.jetlabs.core.objects.components.armor.DamageResult;
import ru.jetlabs.core.objects.components.engines.Engine;
import ru.jetlabs.core.util.structures.Vector2d;

public class SpaceShip extends Actor implements Ship {
    public Engine engine;
    public ArmorMesh armor;

    public SpaceShip(double x, double y, Engine engine, ArmorMaterial armorMaterial, int armorDepth) {
        super(x, y, 10, engine.thrust(), calculateRadius(engine.getSize()));
        int armorSize = (int) Math.ceil(2 * Math.PI * radius);
        this.armor = new ArmorMesh(armorSize, armorDepth, armorMaterial);
        mass = calculateMass((Component) engine);
    }

    public SpaceShip(double x, double y, Engine engine, ArmorMaterial... armorMaterials) {
        super(x, y, 10, engine.thrust(), calculateRadius(engine.getSize()));
        int armorSize = (int) Math.ceil(2 * Math.PI * radius);
        this.armor = new ArmorMesh(armorSize, armorMaterials);
        mass = calculateMass((Component) engine);
    }

    @Override
    public void calculateMaxThrust() {
    }

    public void update(double deltaTime) {
        if (target == null && velocityX != 0 && velocityY != 0) {
            coord.setX(coord.getX() + velocityX * deltaTime);
            coord.setY(coord.getY() + velocityY * deltaTime);
            return;
        }else if(target==null){
            return;
        }

        Vector2d oldPos = coord.copy();
        double dx = target.getX() - coord.getX();
        double dy = target.getY() - coord.getY();
        double distance = Math.hypot(dx, dy);

        double speed = Math.hypot(velocityX, velocityY);
        double brakingDistance = (speed * speed) / (2 * (thrust / mass));
        boolean shouldBrake = distance <= brakingDistance;

        double desiredAx, desiredAy;
        if (shouldBrake) {
            if (speed > 0) {
                desiredAx = (-velocityX / speed) * (thrust / mass);
                desiredAy = (-velocityY / speed) * (thrust / mass);
            } else {
                desiredAx = 0;
                desiredAy = 0;
            }
        } else {
            double adaptiveKp = kP * (1 + distance / 1000.0);
            desiredAx = (dx * adaptiveKp) - (velocityX * kD);
            desiredAy = (dy * adaptiveKp) - (velocityY * kD);
        }

        double maxAccel = thrust / mass;
        double desiredAccel = Math.hypot(desiredAx, desiredAy);
        if (desiredAccel > maxAccel) {
            double scale = maxAccel / desiredAccel;
            desiredAx *= scale;
            desiredAy *= scale;
        }

        velocityX += desiredAx * deltaTime;
        velocityY += desiredAy * deltaTime;
        coord.setX(coord.getX() + velocityX * deltaTime);
        coord.setY(coord.getY() + velocityY * deltaTime);

        if (distance < 0.1 && velocityX < 0.01 && velocityY < 0.01) {
            target = null;
            velocityX = 0;
            velocityY = 0;
        }

        for (int i = 0; i < actors.size(); i++) {
            var actor = actors.get(i);
            if (actor == this) {
                continue;
            }
            if (checkIntersection(oldPos, coord, actor.coord, actor.radius)) {
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

                // 4. Если объекты удаляются друг от друга, пропускаем (столкновение уже обработано)
                if (velocityAlongNormal > 0) {
                    continue;
                }

                // 5. Коэффициент упругости (1.0 = абсолютно упругий, 0.0 = абсолютно неупругий)
                double restitution = 0.8;

                // 6. Импульс столкновения
                double impulse = -(1 + restitution) * velocityAlongNormal;
                impulse /= (1 / this.mass + 1 / actor.mass); // Учитываем массы (если есть)

                if (actor.target == null) {
                    actor.target = actor.coord.copy();
                }

                applyKineticDamage(actor.mass,velocityAlongNormal);
                actor.applyKineticDamage(mass, velocityAlongNormal);

                // 7. Применяем импульс к скоростям
                this.velocityX -= impulse * nx / this.mass;
                this.velocityY -= impulse * ny / this.mass;
                actor.velocityX += impulse * nx / actor.mass;
                actor.velocityY += impulse * ny / actor.mass;
            }
        }

    }

    public void applyKineticDamage(double mass, double speed) {
        DamageResult damageResult = this.armor.applyKineticDamage(mass, speed);
        double energy = damageResult.energy();
        if(energy>0){
            destroy();
        }
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


    public static double calculateRadius(double totalSize) {
        return Math.sqrt(totalSize + 5) / Math.PI;
    }

    public double calculateMass(Component... components) {
        double totalMass = 50;
        for (Component c : components) {
            totalMass += c.mass();
        }
        return totalMass + armor.mass();
    }
}
