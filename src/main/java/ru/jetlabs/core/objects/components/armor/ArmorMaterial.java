package ru.jetlabs.core.objects.components.armor;

class BaseParam {
    public final static double baseMass = 7800; // kg
}

public enum ArmorMaterial {
    STEEL(
            BaseParam.baseMass,
            1.0,
            0.6
    ),
    PLASTEEL(
            BaseParam.baseMass * 0.85,
            0.8,
            0.7
    ),
    DURASTEEL(
            BaseParam.baseMass * 0.70,
            1.1,
            0.55
    ),
    CERAMIC_COMPOSITE(
            BaseParam.baseMass * 0.60,
            2.0,
            0.1
    ),
    TITANIUM_COMPOSITE(
            BaseParam.baseMass * 0.65,
            1.0,
            0.5
    ),
    NANO_CARBON(
            BaseParam.baseMass * 0.40,
            1.5,
            0.2
    );
    public final double mass;
    /**
     * Хрупкость.
     * Отвечает за то, насколько легко повредить материал.
     * Более хрупкий материал можно повредить меньшей энергией
     * Достаточно хрупкий материал будет легко уничтожить
     * Но! Хрупкий материал дробит снаряд, сильно снижая его энергию, при разрушении.
     * Хрупкий материал почти не передаёт энергию соседям, при этом, разрушаясь, поглощает больше энергии,
     * не передавая остаток соседям.
     * Низкая хрупкость даёт более вязкий материал. Его проще пробить, но он сам получает меньше урона
     * Также работает, как множитель наносимого урона
     */
    public final double fragility;
    /**
     * Процент энергии, отдаваемой боковым соседям
     */
    public final double transferEnergy;
    //public final double strength;
    //public final double youngModulus; // in Pa
    //public final double specificHeat; // in J/kg·K
    //public final double latentHeat;   // in J/kg
    //public final double meltingPoint;
    //public final double thermalConductivity;

    ArmorMaterial(double mass, double fragility, double transferEnergy) {
        this.mass = mass;
        this.fragility = fragility;
        this.transferEnergy = transferEnergy;
    }
}