package ru.jetlabs.core.objects.components.armor;

import java.math.BigDecimal;

public record DamageResult(
        double energy
) {

    public String energyStr(){
        return new BigDecimal(Double.toString(energy)).toPlainString();
    }
}
