package com.gordonfromblumberg.games.core.common.world;

import com.gordonfromblumberg.games.core.common.factory.AbstractFactory;
import com.gordonfromblumberg.games.core.common.grid.HexGrid;
import com.gordonfromblumberg.games.core.common.grid.HexGridBuilder;
import com.gordonfromblumberg.games.core.common.utils.ConfigManager;

public class MainWorld extends World {
    static final int hexWidth;
    static final int hexHeight;
    static final float hexIncline;

    static {
        final ConfigManager config = AbstractFactory.getInstance().configManager();
        hexWidth = config.getInteger("hexWidth");
        hexHeight = config.getInteger("hexHeight");
        hexIncline = config.getFloat("hexIncline");
    }

    final MainWorldParams params = new MainWorldParams();
    HexGrid grid;
    boolean gridCreated = false;

    public MainWorldParams getParams() {
        return params;
    }

    public void createGrid() {
        grid = HexGridBuilder.start()
                .rect(params.width,params.height)
                .hexParams(hexWidth, hexHeight, hexIncline)
                .weight(5)
                .build();
        gridCreated = true;
    }
}
