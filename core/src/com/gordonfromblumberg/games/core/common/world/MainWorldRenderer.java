package com.gordonfromblumberg.games.core.common.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.gordonfromblumberg.games.core.common.factory.AbstractFactory;
import com.gordonfromblumberg.games.core.common.graph.Edge;
import com.gordonfromblumberg.games.core.common.grid.Hex;
import com.gordonfromblumberg.games.core.common.grid.HexGrid;
import com.gordonfromblumberg.games.core.common.grid.HexRow;
import com.gordonfromblumberg.games.core.common.utils.ConfigManager;

public class MainWorldRenderer extends WorldRenderer<MainWorld> {
    static final float hexWidth;
    static final float hexWidthHalf;
    static final float hexHeight;
    static final float hexHeightHalf;
    static final float hexIncline;
    static final float hexDy;
    static final Color emptyHexColor = new Color(Color.GREEN).mul(0.7f);
    static final Color cityColor = new Color(Color.CORAL);
    static final Color obstacleColor = new Color(Color.WHITE).mul(0.2f);
    static final Color roadColor = new Color(.65f, .5f, .1f, 1f);

    private static final Vector2 vec2 = new Vector2();

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
            resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            world.gridCreated = false;
        }

        super.render(dt);

        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(emptyHexColor);
        if (world.grid != null) {
            for (HexRow row : world.grid) {
                for (Hex hex : row) {
                    drawHexFilled(hex);
                }
            }
            for (HexRow row : world.grid) {
                for (Hex hex : row) {
                    drawRoads(hex);
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

        if (world.grid != null) {
            world.getAlgorithm().renderer().render(world.getAlgorithm(), world.grid, shapeRenderer);
        }
    }

    private void drawHexFilled(Hex hex) {
        final HexGrid grid = world.grid;
        final float hexX = grid.getWorldX(hex);
        final float hexY = grid.getWorldY(hex);
        String hexType = hex.getTile(0);
        if (hexType == null) {
            shapeRenderer.setColor(emptyHexColor);
        } else if (hexType.equals(HexType.CITY)) {
            shapeRenderer.setColor(cityColor);
        } else {
            shapeRenderer.setColor(obstacleColor);
        }
        shapeRenderer.triangle(hexX, hexY, hexX + hexWidthHalf, hexY - hexDy, hexX + hexWidthHalf, hexY + hexDy);
        shapeRenderer.triangle(hexX, hexY, hexX + hexWidthHalf, hexY + hexDy, hexX, hexY + hexHeightHalf);
        shapeRenderer.triangle(hexX, hexY, hexX, hexY + hexHeightHalf, hexX - hexWidthHalf, hexY + hexDy);
        shapeRenderer.triangle(hexX, hexY, hexX - hexWidthHalf, hexY + hexDy, hexX - hexWidthHalf, hexY - hexDy);
        shapeRenderer.triangle(hexX, hexY, hexX - hexWidthHalf, hexY - hexDy, hexX, hexY - hexHeightHalf);
        shapeRenderer.triangle(hexX, hexY, hexX, hexY - hexHeightHalf, hexX + hexWidthHalf, hexY - hexDy);
    }

    private void drawRoads(Hex hex) {
        final HexGrid grid = world.grid;
        final float hexX = grid.getWorldX(hex);
        final float hexY = grid.getWorldY(hex);
        final float defaultWeight = world.params.defaultWeight;
        final float roadWeight = world.params.roadWeight;
        shapeRenderer.setColor(roadColor);
        for (int i = 0; i < 3; ++i) {
            Edge<Hex> next = grid.next(hex, i);
            if (next != null) {
                float weight = next.getWeight();
                if (weight == defaultWeight)
                    continue;

                float nextX = grid.getWorldX(next.getNode());
                float nextY = grid.getWorldY(next.getNode());
                float halfWidth = MathUtils.map(
                        defaultWeight, roadWeight,
                        0, .3f * MainWorld.hexWidth / 2, weight);
                vec2.set(nextX, nextY).sub(hexX, hexY).setLength(halfWidth);
                shapeRenderer.circle(hexX, hexY, halfWidth);
                shapeRenderer.circle(nextX, nextY, halfWidth);
                shapeRenderer.triangle(
                        hexX + vec2.y, hexY - vec2.x,
                        nextX - vec2.y, nextY + vec2.x,
                        hexX - vec2.y, hexY + vec2.x);
                shapeRenderer.triangle(
                        hexX + vec2.y, hexY - vec2.x,
                        nextX + vec2.y, nextY - vec2.x,
                        nextX - vec2.y, nextY + vec2.x);
            }
        }
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

        int screenWidth = width - config.getInteger("ui.width");
        setWorldSize();
        viewport.update(screenWidth, height, false);
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
