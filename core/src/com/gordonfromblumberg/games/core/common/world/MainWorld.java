package com.gordonfromblumberg.games.core.common.world;

import com.gordonfromblumberg.games.core.common.grid.HexGrid;

public class MainWorld extends World {
    final MainWorldParams params = new MainWorldParams();
    private HexGrid grid;

    public MainWorldParams getParams() {
        return params;
    }
}
