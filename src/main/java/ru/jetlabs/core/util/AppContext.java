package ru.jetlabs.core.util;

import ru.jetlabs.core.util.data.AssetManager;
import ru.jetlabs.core.util.data.GridSavesManager;

public abstract class AppContext {
    private final static AssetManager assetManager = new AssetManager();
    private final static GridSavesManager GRID_SAVES_MANAGER = new GridSavesManager();



}
