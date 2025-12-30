package ru.jetlabs.core.objects.entities;

import ru.jetlabs.core.objects.components.armor.ArmorMaterial;

public class ShipPresets {

    public static final ShipConfig LIGHT_FIGHTER = new ShipConfig(
            "Light Fighter",
            50,
            3.6 * 3.2,
            true,
            100,
            2,
            0.1,
            0.5,
            300_000_000,
            1.0,
            500,    // radarRange
            800,    // passiveRadarRange
            new ArmorMaterial[]{ArmorMaterial.STEEL, ArmorMaterial.PLASTEEL}
    );

    public static final ShipConfig HEAVY_FIGHTER = new ShipConfig(
            "Heavy Fighter",
            80,
            5.0 * 4.0,
            true,
            150,
            3,
            0.15,
            0.7,
            500_000_000,
            1.5,
            700,    // radarRange
            1000,   // passiveRadarRange
            new ArmorMaterial[]{ArmorMaterial.STEEL, ArmorMaterial.DURASTEEL, ArmorMaterial.PLASTEEL}
    );

    public static final ShipConfig FRIGATE = new ShipConfig(
            "Frigate",
            120,
            6.0 * 5.0,
            true,
            200,
            4,
            0.2,
            0.8,
            800_000_000,
            2.0,
            1000,   // radarRange
            1500,   // passiveRadarRange
            new ArmorMaterial[]{ArmorMaterial.TITANIUM_COMPOSITE, ArmorMaterial.DURASTEEL, ArmorMaterial.CERAMIC_COMPOSITE}
    );

    public static final ShipConfig DESTROYER = new ShipConfig(
            "Destroyer",
            200,
            8.0 * 6.0,
            true,
            300,
            5,
            0.3,
            1.0,
            1_500_000_000,
            3.0,
            1500,   // radarRange
            2000,   // passiveRadarRange
            new ArmorMaterial[]{ArmorMaterial.NANO_CARBON, ArmorMaterial.TITANIUM_COMPOSITE, ArmorMaterial.DURASTEEL}
    );

    public static final ShipConfig SCOUT = new ShipConfig(
            "Scout",
            30,
            2.5 * 2.5,
            true,
            80,
            1.5,
            0.08,
            0.4,
            200_000_000,
            0.5,
            400,    // radarRange (маленький активный)
            1200,   // passiveRadarRange (большой пассивный)
            new ArmorMaterial[]{ArmorMaterial.STEEL}
    );

    public static final ShipConfig CARGO_SHIP = new ShipConfig(
            "Cargo Ship",
            150,
            4.0 * 3.0,
            false,
            0,
            0,
            0,
            0,
            0,
            0,
            300,    // radarRange (базовый радар)
            500,    // passiveRadarRange
            new ArmorMaterial[]{ArmorMaterial.STEEL, ArmorMaterial.STEEL}
    );
}
