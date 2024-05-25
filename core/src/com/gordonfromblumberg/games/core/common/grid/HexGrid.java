package com.gordonfromblumberg.games.core.common.grid;

import com.gordonfromblumberg.games.core.common.graph.Graph;

public class HexGrid implements Graph {
    static final float xIntersection = 0.5f;
    final int hexWidth;
    final int hexHeight;
    final float incline;
    /**
     * Distance by 'y' between hex centres in related rows
     */
    final float dy;
    final float yIntersection;

    final HexRow[] rows;
    final int layerCount;

    public HexGrid(int hexWidth, int hexHeight, float incline, int rowCount, int layerCount) {
        this.hexWidth = hexWidth;
        this.hexHeight = hexHeight;
        this.incline = incline;
        this.dy = hexHeight - incline;
        this.yIntersection = incline / this.dy;
        this.rows = new HexRow[rowCount];
        this.layerCount = layerCount;
    }

    public Hex findHex(float x, float y) {
        int minX, maxX, minY, maxY;

        float kx = (x + hexWidth / 2f) / hexWidth;
        if (kx - (maxX = (int)kx) < xIntersection)
            minX = maxX - 1;
        else
            minX = maxX;

        float ky = (y + hexHeight / 2f) / dy;
        if (ky - (maxY = (int)ky) < yIntersection)
            minY = maxY - 1;
        else
            minY = maxY;

        Hex hex = null;
        float minD2 = hexHeight * 5;
        float d2 = minD2;
        for (int j = minY; j >= 0 && j <= maxY && j < rows.length; ++j) {
            HexRow row = rows[j];
            for (int i = minX; i <= maxX; ++i) {
                Hex curHex = row.hexes.get(i);
                if (curHex != null && ((d2 = dist2(curHex, x, y)) < minD2) || hex == null) {
                    hex = curHex;
                    minD2 = d2;
                }
            }
        }

        return hex;
    }

    public float getWorldX(Hex hex) {
        final float halfWidth = hexWidth / 2f;
        return hex.x * hexWidth + hex.y % 2 * halfWidth;
    }

    public float getWorldY(Hex hex) {
        return hex.y * dy;
    }

    private float dist2(Hex hex, float x, float y) {
        float dx = getWorldX(hex) - x;
        float dy = getWorldY(hex) - y;
        return dx * dx + dy * dy;
    }
}
