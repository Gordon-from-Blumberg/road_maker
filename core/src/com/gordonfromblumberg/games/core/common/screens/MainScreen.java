package com.gordonfromblumberg.games.core.common.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.gordonfromblumberg.games.core.common.log.LogManager;
import com.gordonfromblumberg.games.core.common.log.Logger;
import com.gordonfromblumberg.games.core.common.world.MainWorld;
import com.gordonfromblumberg.games.core.common.world.WorldRenderer;
import com.gordonfromblumberg.games.core.common.world.WorldScreen;

public class MainScreen extends WorldScreen<MainWorld> {
    private static final Logger log = LogManager.create(MainScreen.class);

    TextButton textButton;

    public MainScreen(SpriteBatch batch) {
        super(batch, new MainWorld());

        color = new Color(Color.FOREST).mul(0.5f);

        log.debug("Local storage path = " + Gdx.files.getLocalStoragePath());
        log.debug("External storage path = " + Gdx.files.getExternalStoragePath());
    }

    @Override
    protected void update(float delta) {
    }

    @Override
    protected void createWorldRenderer() {
        worldRenderer = new WorldRenderer<>(world);
    }

    @Override
    protected void createUiRenderer() {
        uiRenderer = new MainUIRenderer(batch, world, this::getViewCoords3);



        uiRenderer.rootTable.add(textButton);
    }
}
