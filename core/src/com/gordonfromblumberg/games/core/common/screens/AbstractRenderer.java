package com.gordonfromblumberg.games.core.common.screens;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.utils.viewport.Viewport;

public abstract class AbstractRenderer implements Renderer {
    protected Camera camera;
    protected Viewport viewport;
    protected boolean centerCamera;

    protected AbstractRenderer(Viewport viewport) {
        this.camera = viewport.getCamera();
        this.viewport = viewport;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, centerCamera);
    }

    @Override
    public Camera getCamera() {
        return camera;
    }

    @Override
    public Viewport getViewport() {
        return viewport;
    }
}
