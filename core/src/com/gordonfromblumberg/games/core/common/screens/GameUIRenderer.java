package com.gordonfromblumberg.games.core.common.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.gordonfromblumberg.games.core.common.utils.CoordsConverter;
import com.gordonfromblumberg.games.core.common.world.GameWorld;

public class GameUIRenderer extends UIRenderer {
    private GameWorld world;

    private final Vector3 GAME_VIEW_COORDS = new Vector3();
    private CoordsConverter toGameViewConverter;

    public GameUIRenderer(Viewport viewport, Stage stage, GameWorld world, CoordsConverter toGameViewConverter) {
        super(viewport, stage);

        Gdx.app.log("INIT", "GameUIRenderer constructor");

        this.world = world;
        this.toGameViewConverter = toGameViewConverter;
    }

    @Override
    public void render(float dt) {
        super.render(dt);
    }
}
