package com.gordonfromblumberg.games.core.common.world;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.gordonfromblumberg.games.core.common.Main;
import com.gordonfromblumberg.games.core.common.factory.AbstractFactory;
import com.gordonfromblumberg.games.core.common.screens.FBORenderer;
import com.gordonfromblumberg.games.core.common.model.GameObject;

public class GameWorldRenderer extends FBORenderer {
    private static final Color TEMP_COLOR = new Color();
    private final GameWorld world;
    private final Batch batch;
    private Viewport viewport;
    private final Rectangle worldArea = new Rectangle();
    private IsometricTiledMapRenderer mapRenderer;

    private final Color pauseColor = Color.GRAY;
    private final Color tempClr1 = new Color(), tempClr2 = new Color();
    TextureRegion background;

    public GameWorldRenderer(GameWorld world, Batch batch, Viewport viewport) {
        super(viewport);

        this.batch = batch;
        this.world = world;
    }

    public void initialize(Viewport viewport, float width, float height) {
        final AssetManager assets = Main.getInstance().assets();

        this.viewport = viewport;
        worldArea.setSize(width, height);

        background = assets
                .get("image/texture_pack.atlas", TextureAtlas.class)
                .findRegion("background");

        this.mapRenderer = new IsometricTiledMapRenderer(world.map, batch);
    }

    @Override
    public void render(float dt) {
        batch.begin();
        final Color origColor = TEMP_COLOR.set(batch.getColor());
        if (world.paused) {
            batch.setColor(pauseColor);
        }

//        batch.draw(background, 0, 0);

        mapRenderer.setView((OrthographicCamera) viewport.getCamera());
        mapRenderer.renderTileLayer((TiledMapTileLayer) world.map.getLayers().get(0));

        if (world.paused) {
            for (GameObject gameObject : world.getGameObjects()) {
                gameObject.getSprite().setColor(pauseColor);
                gameObject.render(batch);
                gameObject.getSprite().setColor(Color.WHITE);
            }
        } else {
            for (GameObject gameObject : world.getGameObjects()) {
                gameObject.render(batch);
            }
        }

        if (world.paused) {
            world.pauseText.draw(batch);
            batch.setColor(origColor);
        }

        batch.end();
    }
}
