package ru.jetlabs.core.objects.components.engines.impls;

import ru.jetlabs.core.objects.components.Component;
import ru.jetlabs.core.objects.entities.Shell;
import ru.jetlabs.core.objects.entities.SpaceShip;
import ru.jetlabs.core.util.structures.Vector2d;

public class IdealKineticGun extends Component {
    public double caliber;
    public double shellLength;
    public double power;  // Энергия в джоулях (предполагаем, что это энергия конденсатора)
    public double cooldown;  // Задержка между выстрелами в секундах
    public long lastShotTime;  // Время последнего выстрела (миллисекунды)
    public SpaceShip owner;

    public IdealKineticGun(double mass,
                           double size,
                           double caliber,
                           double shellLength,
                           double power,
                           double cooldown) {
        super(mass, size);
        this.caliber = caliber;
        this.shellLength = shellLength;
        this.power = power;
        this.cooldown = cooldown;
    }

    public IdealKineticGun(double mass, double hp, double size) {
        super(mass, hp, size);
    }

    public Shell strike(Vector2d target) {
        long currentTime = System.currentTimeMillis();
        if (cooldown > 0 && currentTime - lastShotTime < cooldown * 1000) {
            return null;  // Кулдаун ещё не прошёл
        }

        Shell shell = new Shell(
                owner.coord.getX(),
                owner.coord.getY(),
                new Vector2d(0, 0),  // Вектор скорости будет вычислен
                shellLength,
                caliber,
                owner
        );

        double shellMass = shell.mass;

        // Рассчитываем скорость снаряда (v = sqrt(2 * power / mass))
        double velocityValue = Math.sqrt(2 * power / shellMass);

        Vector2d direction = new Vector2d(
                target.getX() - owner.coord.getX(),
                target.getY() - owner.coord.getY()
        ).normalize();

        Vector2d velocity = direction.multiply(velocityValue);

        shell.velocityY = owner.velocityY + velocity.getY();
        shell.velocityX = owner.velocityX + velocity.getX();

        lastShotTime = currentTime;
        return shell;
    }

    // Стрельба по bearing относительно heading корабля
    public Shell strikeByBearing(double bearing, double shipHeading) {
        double absoluteAngle = shipHeading + bearing;
        return strikeByAngle(absoluteAngle);
    }

    // Стрельба по абсолютному углу
    public Shell strikeByAngle(double absoluteAngle) {
        long currentTime = System.currentTimeMillis();
        if (cooldown > 0 && currentTime - lastShotTime < cooldown * 1000) {
            return null;
        }

        Shell shell = new Shell(
                owner.coord.getX(),
                owner.coord.getY(),
                new Vector2d(0, 0),
                shellLength,
                caliber,
                owner
        );

        double shellMass = shell.mass;
        double velocityValue = Math.sqrt(2 * power / shellMass);

        // Направление из угла
        Vector2d direction = new Vector2d(
            Math.cos(absoluteAngle),
            Math.sin(absoluteAngle)
        );

        Vector2d velocity = direction.multiply(velocityValue);

        shell.velocityX = owner.velocityX + velocity.getX();
        shell.velocityY = owner.velocityY + velocity.getY();

        lastShotTime = currentTime;
        return shell;
    }
}
