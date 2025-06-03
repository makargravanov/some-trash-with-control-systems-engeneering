package ru.jetlabs.core.objects.components.engines.impls;

import ru.jetlabs.core.objects.components.Component;
import ru.jetlabs.core.objects.entities.Shell;
import ru.jetlabs.core.objects.entities.SpaceShip;
import ru.jetlabs.core.util.structures.Vector2d;

public class IdealKineticGun extends Component {
    public double caliber;
    public double shellLength;
    public double power;  // Энергия в джоулях (предполагаем, что это энергия конденсатора)
    public SpaceShip owner;

    public IdealKineticGun(double mass,
                           double size,
                           double caliber,
                           double shellLength,
                           double power) {
        super(mass, size);
        this.caliber = caliber;
        this.shellLength = shellLength;
        this.power = power;
    }

    public IdealKineticGun(double mass, double hp, double size) {
        super(mass, hp, size);
    }

    public Shell strike(Vector2d target) {
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

        shell.velocityY = velocity.getY();
        shell.velocityX = velocity.getX();

        return shell;
    }
}
