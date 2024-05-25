package com.gordonfromblumberg.games.core.common.world;

public class MainWorldParams {
    GridShape shape = GridShape.RECT;
    int width;
    int height;

    public GridShape getShape() {
        return shape;
    }

    public void setShape(GridShape shape) {
        this.shape = shape;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public enum GridShape {
        RECT,
        HEX
    }
}
