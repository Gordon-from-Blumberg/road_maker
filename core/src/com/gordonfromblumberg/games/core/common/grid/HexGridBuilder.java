package com.gordonfromblumberg.games.core.common.grid;

public class HexGridBuilder {
    public static HexGrid rect(int width, int height, int hexWidth, int hexHeight, float incline, int layerCount) {
        final HexGrid hexGrid = new HexGrid(hexWidth, hexHeight, incline, height, layerCount);
        final int maxX = width - 1;
        for (int j = 0; j < height; ++j) {
            HexRow row = new HexRow(j, 0, maxX);
            hexGrid.rows[j] = row;

            for (int i = 0; i < width; ++i) {
                Hex hex = new Hex(i, j, layerCount);
                row.hexes.put(i, hex);
            }
        }

        return hexGrid;
    }
}
