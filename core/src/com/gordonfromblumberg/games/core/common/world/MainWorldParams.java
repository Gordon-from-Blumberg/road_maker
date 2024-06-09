package com.gordonfromblumberg.games.core.common.world;

import com.badlogic.gdx.Preferences;

public class MainWorldParams {
    public static final int DEFAULT_CITY_COUNT = 5;
    public static final int DEFAULT_OBSTACLE_LEVEL = 1;

    GridShape shape = GridShape.RECT;
    int width;
    int height;
    int cityCount = DEFAULT_CITY_COUNT;
    int obstacleLevel = DEFAULT_OBSTACLE_LEVEL;
    float defaultWeight = MainWorld.defaultWeight;
    float roadWeight = MainWorld.roadWeight;

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

    public void save(Preferences prefs) {
        prefs.putString("shape", shape.name());
        prefs.putInteger("width", width);
        prefs.putInteger("height", height);
        prefs.putInteger("cityCount", cityCount);
        prefs.putInteger("obstacleLevel", obstacleLevel);
        prefs.putFloat("defaultWeight", defaultWeight);
        prefs.putFloat("roadWeight", roadWeight);
    }

    public void load(Preferences prefs) {
        shape = GridShape.valueOf(prefs.getString("shape", shape.name()));
        width = prefs.getInteger("width", width);
        height = prefs.getInteger("height", height);
        cityCount = prefs.getInteger("cityCount", cityCount);
        obstacleLevel = prefs.getInteger("obstacleLevel", obstacleLevel);
        defaultWeight = prefs.getFloat("defaultWeight", defaultWeight);
        roadWeight = prefs.getFloat("roadWeight", roadWeight);
    }
}
