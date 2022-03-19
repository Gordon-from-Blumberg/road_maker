package com.gordonfromblumberg.games.core.common.world;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.gordonfromblumberg.games.core.common.Main;
import com.gordonfromblumberg.games.core.common.factory.AbstractFactory;
import com.gordonfromblumberg.games.core.common.model.GameObject;

public class GameWorldRenderer {
    private static final Color TEMP_COLOR = new Color();
    private final GameWorld world;
    private Viewport viewport;
    private final Rectangle worldArea = new Rectangle();

    private final Color pauseColor = Color.GRAY;
    private final Color tempClr1 = new Color(), tempClr2 = new Color();

    private NinePatch background;

    public GameWorldRenderer(GameWorld world) {
        this.world = world;
    }

    public void initialize(Viewport viewport, float width, float height) {
        final AssetManager assets = Main.getInstance().assets();

        this.viewport = viewport;
        worldArea.setSize(width, height);

        background = new NinePatch(
                assets.get("image/texture_pack.atlas", TextureAtlas.class)
                        .findRegion("world-background"),
                1, 1, 1, 1
        );
    }

    public void render(Batch batch) {
        final Color origColor = TEMP_COLOR.set(batch.getColor());
        if (world.paused)
            batch.setColor(pauseColor);

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
    }
}
