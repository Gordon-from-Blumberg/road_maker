package com.gordonfromblumberg.games.core.common.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.gordonfromblumberg.games.core.common.factory.AbstractFactory;
import com.gordonfromblumberg.games.core.common.grid.Hex;
import com.gordonfromblumberg.games.core.common.grid.HexRow;
import com.gordonfromblumberg.games.core.common.utils.ConfigManager;

public class MainWorldRenderer extends WorldRenderer<MainWorld> {
    static final float hexWidth;
    static final float hexWidthHalf;
    static final float hexHeight;
    static final float hexHeightHalf;
    static final float hexIncline;
    static final float hexDy;
    static final Color hexColor = new Color(Color.GREEN).mul(0.8f);

    static {
        final ConfigManager config = AbstractFactory.getInstance().configManager();
        hexWidth = config.getFloat("hexWidth");
        hexWidthHalf = hexWidth / 2;
        hexHeight = config.getFloat("hexHeight");
        hexHeightHalf = hexHeight / 2;
        hexIncline = config.getFloat("hexIncline");
        hexDy = hexHeightHalf - hexIncline;
    }

    private final ShapeRenderer shapeRenderer = new ShapeRenderer();

    public MainWorldRenderer(MainWorld world) {
        super(world);
    }

    @Override
    public void render(float dt) {
        if (world.gridCreated) {
            setWorldSize();
            world.gridCreated = false;
        }

        super.render(dt);

        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(hexColor);
        if (world.grid != null) {
            for (HexRow row : world.grid) {
                for (Hex hex : row) {
                    drawHexFilled(hex);
                }
            }
            shapeRenderer.end();

            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.GRAY);
            for (HexRow row : world.grid) {
                for (Hex hex : row) {
                    drawHex(hex);
                }
            }
        }
        shapeRenderer.end();
    }

    private void drawHexFilled(Hex hex) {
        final float x = world.grid.getWorldX(hex);
        final float y = world.grid.getWorldY(hex);
        shapeRenderer.triangle(x, y, x + hexWidthHalf, y - hexDy, x + hexWidthHalf, y + hexDy);
        shapeRenderer.triangle(x, y, x + hexWidthHalf, y + hexDy, x, y + hexHeightHalf);
        shapeRenderer.triangle(x, y, x, y + hexHeightHalf, x - hexWidthHalf, y + hexDy);
        shapeRenderer.triangle(x, y, x - hexWidthHalf, y + hexDy, x - hexWidthHalf, y - hexDy);
        shapeRenderer.triangle(x, y, x - hexWidthHalf, y - hexDy, x, y - hexHeightHalf);
        shapeRenderer.triangle(x, y, x, y - hexHeightHalf, x + hexWidthHalf, y - hexDy);
    }

    private void drawHex(Hex hex) {
        final float x = world.grid.getWorldX(hex);
        final float y = world.grid.getWorldY(hex);
        shapeRenderer.line(x + hexWidthHalf, y - hexDy, x + hexWidthHalf, y + hexDy);
        shapeRenderer.line(x + hexWidthHalf, y + hexDy, x, y + hexHeightHalf);
        shapeRenderer.line(x, y + hexHeightHalf, x - hexWidthHalf, y + hexDy);
        shapeRenderer.line(x - hexWidthHalf, y + hexDy, x - hexWidthHalf, y - hexDy);
        shapeRenderer.line(x - hexWidthHalf, y - hexDy, x, y - hexHeightHalf);
        shapeRenderer.line(x, y - hexHeightHalf, x + hexWidthHalf, y - hexDy);
    }

    @Override
    public void resize(int width, int height) {
        final ConfigManager config = AbstractFactory.getInstance().configManager();

        int screenWidth = Gdx.graphics.getWidth() - config.getInteger("ui.width");
        int screenHeight = Gdx.graphics.getHeight();
        setWorldSize();
        viewport.update(screenWidth, screenHeight, false);
    }

    private void setWorldSize() {
        float worldWidth = getWorldWidth();
        float worldHeight = getWorldHeight();
        viewport.setWorldSize(worldWidth, worldHeight);
        camera.position.set(worldWidth / 2 - hexWidthHalf, worldHeight / 2 - hexHeightHalf, 0);
    }

    private float getWorldWidth() {
        return world.grid != null ? (world.grid.getMaxX() - world.grid.getMinX() + 1.5f) * hexWidth : 600;
    }

    private float getWorldHeight() {
        return world.grid != null ? (world.grid.getMaxY() + 1) * (hexHeight - hexIncline) + hexIncline : 600;
    }

    @Override
    protected Viewport createViewport(Camera camera) {
        final ConfigManager config = AbstractFactory.getInstance().configManager();
        return new FitViewport(
                config.getFloat("worldWidth"),
                config.getFloat("worldHeight"),
                camera
        );
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }
}
