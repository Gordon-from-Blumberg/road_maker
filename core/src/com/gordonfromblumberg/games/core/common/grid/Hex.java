package com.gordonfromblumberg.games.core.common.grid;

public class Hex {
    public final int x, y;
    final String[] tiles;

    Hex(int x, int y, int layerCount) {
        this.x = x;
        this.y = y;
        this.tiles = new String[layerCount];
    }
}
