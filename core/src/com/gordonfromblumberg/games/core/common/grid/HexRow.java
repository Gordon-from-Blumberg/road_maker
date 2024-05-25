package com.gordonfromblumberg.games.core.common.grid;

import com.badlogic.gdx.utils.IntMap;

public class HexRow {
    private final int y;
    final int minX;
    final int maxX;
    final IntMap<Hex> hexes;

    HexRow(int y, int minX, int maxX) {
        this.y = y;
        this.minX = minX;
        this.maxX = maxX;
        this.hexes = new IntMap<>(maxX - minX + 1);
    }
}
