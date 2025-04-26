package ru.jetlabs.core.objects.components.engines.impls;

import ru.jetlabs.core.objects.components.Component;
import ru.jetlabs.core.objects.components.ComponentType;
import ru.jetlabs.core.objects.components.engines.NoFuelEngine;

public class IdealEngine extends Component implements NoFuelEngine{
    private double thrust;

    public IdealEngine(double size) {
        super(size*50, size, ComponentType.ENGINE);
        thrust = size*5000;
    }

    @Override
    public void calculateThrust() {
    }

    @Override
    public double thrust() {
        return thrust;
    }

    @Override
    public double getSize() {
        return super.size();
    }


}
