package com.gordonfromblumberg.games.core.common.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.gordonfromblumberg.games.core.common.Main;
import com.gordonfromblumberg.games.core.common.animation.AnimatedParameterFloat;
import com.gordonfromblumberg.games.core.common.animation.GbAnimation;
import com.gordonfromblumberg.games.core.common.screens.FBORenderer;
import com.gordonfromblumberg.games.core.common.model.GameObject;

import java.util.Iterator;

public class GameWorldRenderer extends FBORenderer {
    private static final Color TEMP_COLOR = new Color();
    private static final Vector3 tempVec3 = new Vector3();

    private final GameWorld world;
    private final Batch batch;
    private final Rectangle worldArea = new Rectangle();
    private IsometricTiledMapRenderer mapRenderer;
    private final ShapeRenderer shapeRenderer = new ShapeRenderer();
    private final Matrix3 viewToWorld = new Matrix3();
    private final Matrix3 worldToView = new Matrix3();

    final Array<ClickPoint> clickPoints = new Array<>();

    private final Color pauseColor = Color.GRAY;
    TextureRegion background;

    public GameWorldRenderer(GameWorld world, Batch batch) {
        super();
        Gdx.app.log("INIT", "GameWorldRenderer constructor");
        this.batch = batch;
        this.world = world;
    }

    public void initialize() {
        Gdx.app.log("INIT", "GameWorldRenderer init");
        final AssetManager assets = Main.getInstance().assets();

        background = assets
                .get("image/texture_pack.atlas", TextureAtlas.class)
                .findRegion("background");

        this.mapRenderer = new IsometricTiledMapRenderer(world.map, batch);
        TiledMapTileLayer l = (TiledMapTileLayer) world.map.getLayers().get("map");
        viewport.getCamera().position.set(l.getWidth() * l.getTileWidth() / 2f, 0, 0);
        viewToWorld.set(new float[] {
                 1.0f / l.getTileWidth(),  1.0f / l.getTileWidth(),  0.0f,
                -1.0f / l.getTileHeight(), 1.0f / l.getTileHeight(), 0.0f,
                 0.5f,                    -0.5f,                     1.0f
        });
        worldToView.set(viewToWorld).inv();
    }

    @Override
    public void render(float dt) {
        updateCamera();

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
            batch.end();

            shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(0f, 1f, 0f, 1f);
            final Iterator<ClickPoint> it = clickPoints.iterator();
            while (it.hasNext()) {
                ClickPoint cp = it.next();
                worldToView(tempVec3.set(cp.x, cp.y, 1));
                cp.animation.update(dt);
                final float circleMul = cp.getCircle();
                Gdx.app.log("RENDER", "render click at " + cp.x + ", " + cp.y + ", mul = " + circleMul);
                final float clickWidth = circleMul * ClickPoint.WIDTH;
                final float clickHeight = circleMul * ClickPoint.HEIGHT;
                shapeRenderer.ellipse(cp.x - clickWidth / 2, cp.y - clickHeight / 2, clickWidth, clickHeight);

                if (cp.animation.isFinished()) {
                    it.remove();
                    cp.release();
                }
            }
            shapeRenderer.end();

            batch.begin();
        }

        if (world.paused) {
            world.pauseText.draw(batch);
            batch.setColor(origColor);
        }

        batch.end();
    }

    /**
     * Transforms viewport coordinates to logical world
     */
    public void viewToWorld(float x, float y, Vector3 out) {
        out.set(x, y, 1f).mul(viewToWorld);
    }

    /**
     * Transforms logical world to viewport coordinates
     */
    public void worldToView(Vector3 coords) {
        coords.z = 1.0f;
        coords.mul(worldToView);
    }

    void click(float x, float y) {
        worldToView(tempVec3.set(x, y, 1));
        ClickPoint cp = ClickPoint.getInstance();
        clickPoints.add(cp.init(tempVec3.x, tempVec3.y));
    }

    private void updateCamera() {
        float cameraSpeed = 8 * camera.zoom;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
            camera.translate(-cameraSpeed, 0);
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
            camera.translate(cameraSpeed, 0);
//        if (Gdx.input.isKeyPressed(Input.Keys.UP))
//            camera.translate(0, cameraSpeed);
//        if (Gdx.input.isKeyPressed(Input.Keys.DOWN))
//            camera.translate(0, -cameraSpeed);
    }
}
