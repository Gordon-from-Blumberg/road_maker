package com.gordonfromblumberg.games.core.common.world;

import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector3;
import com.gordonfromblumberg.games.core.common.log.LogManager;
import com.gordonfromblumberg.games.core.common.log.Logger;
import com.gordonfromblumberg.games.core.common.screens.AbstractRenderer;

public class WorldRenderer<T extends World> extends AbstractRenderer {
    private static final Logger log = LogManager.create(WorldRenderer.class);

    protected final T world;

    protected final Matrix3 viewToWorld = new Matrix3();
    protected final Matrix3 worldToView = new Matrix3();

    public WorldRenderer(T world) {
        super();
        log.info("WorldRenderer constructor for " + getClass().getSimpleName());
        this.world = world;
    }

    @Override
    public void render(float dt) {

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
}
