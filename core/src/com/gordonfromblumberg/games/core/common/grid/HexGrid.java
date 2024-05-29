package com.gordonfromblumberg.games.core.common.grid;

import com.gordonfromblumberg.games.core.common.graph.Graph;

import java.util.Iterator;

public class HexGrid implements Graph, Iterable<HexRow> {
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
    int minX, maxX;
    private int iteratorIndex = 0;

    public HexGrid(int hexWidth, int hexHeight, float incline, int rowCount, int layerCount) {
        this.hexWidth = hexWidth;
        this.hexHeight = hexHeight;
        this.incline = incline;
        this.dy = hexHeight - incline;
        this.yIntersection = incline / this.dy;
        this.rows = new HexRow[rowCount];
        this.layerCount = layerCount;
    }

    public HexRow[] getRows() {
        return rows;
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

    public int getMinX() {
        return minX;
    }

    public int getMaxX() {
        return maxX;
    }

    public int getMaxY() {
        return rows.length - 1;
    }

    private final Iterator<HexRow> iterator = new Iterator<>() {
        @Override
        public boolean hasNext() {
            return iteratorIndex < rows.length;
        }

        @Override
        public HexRow next() {
            return rows[iteratorIndex++];
        }
    };

    @Override
    public Iterator<HexRow> iterator() {
        iteratorIndex = 0;
        return iterator;
    }

    void calcBounds() {
        int minX = rows[0].minX, maxX = rows[0].maxX;
        for (HexRow row : rows) {
            if (row.minX < minX) minX = row.minX;
            if (row.maxX > maxX) maxX = row.maxX;
        }
        this.minX = minX;
        this.maxX = maxX;
    }

    private float dist2(Hex hex, float x, float y) {
        float dx = getWorldX(hex) - x;
        float dy = getWorldY(hex) - y;
        return dx * dx + dy * dy;
    }
}
