package com.gordonfromblumberg.games.core.common.world;

public class MainWorldParams {
    public static final int DEFAULT_CITY_COUNT = 5;
    public static final int DEFAULT_OBSTACLE_LEVEL = 1;

    GridShape shape = GridShape.RECT;
    int width;
    int height;
    int cityCount = DEFAULT_CITY_COUNT;
    int obstacleLevel = DEFAULT_OBSTACLE_LEVEL;

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

    public int getCityCount() {
        return cityCount;
    }

    public void setCityCount(int cityCount) {
        this.cityCount = cityCount;
    }

    public int getObstacleLevel() {
        return obstacleLevel;
    }

    public void setObstacleLevel(int obstacleLevel) {
        this.obstacleLevel = obstacleLevel;
    }

    public enum GridShape {
        RECT,
        HEX
    }
}
