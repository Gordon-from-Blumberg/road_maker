package com.gordonfromblumberg.games.core.common.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.gordonfromblumberg.games.core.common.Main;
import com.gordonfromblumberg.games.core.common.factory.AbstractFactory;
import com.gordonfromblumberg.games.core.common.ui.UIUtils;
import com.gordonfromblumberg.games.core.common.ui.UpdatableLabel;
import com.gordonfromblumberg.games.core.common.utils.ConfigManager;
import com.gordonfromblumberg.games.core.common.utils.CoordsConverter;
import com.gordonfromblumberg.games.core.common.world.GameWorld;

import java.util.function.Consumer;

public class GameUIRenderer extends UIRenderer {
    private GameWorld world;

    private final Vector3 GAME_VIEW_COORDS = new Vector3();
    private final Vector3 CLICK_COORDS = new Vector3();
    private final WorldCameraParams WORLD_CAMERA_PARAMS = new WorldCameraParams();
    private final CoordsConverter toGameView;
    private final CoordsConverter toGameWorld;
    private Label screenCoord, viewCoord, worldCoord;
    private final Consumer<WorldCameraParams> worldCameraParamsGetter;

    public GameUIRenderer(SpriteBatch batch, GameWorld world,
                          CoordsConverter toGameView, CoordsConverter toGameWorld,
                          Consumer<WorldCameraParams> worldCameraParamsGetter) {
        super(batch);

        Gdx.app.log("INIT", "GameUIRenderer constructor");

        this.world = world;
        this.toGameView = toGameView;
        this.toGameWorld = toGameWorld;
        this.worldCameraParamsGetter = worldCameraParamsGetter;

        init();
    }

    void click(int button, float screenX, float screenY) {
        screenCoord.setText(screenX + ", " + screenY);
        toGameView.convert(screenX, screenY, CLICK_COORDS);
        toGameWorld.convert(CLICK_COORDS.x, CLICK_COORDS.y, CLICK_COORDS);
        worldCoord.setText(CLICK_COORDS.x + ", " + CLICK_COORDS.y);
    }

    private void init() {
        final ConfigManager configManager = AbstractFactory.getInstance().configManager();
        final AssetManager assets = Main.getInstance().assets();
        final Skin uiSkin = assets.get("ui/uiskin.json", Skin.class);

        if (Main.DEBUG) {
            rootTable.add(createCoordsDebugTable(uiSkin))
                    .left().top().expand();
        } else {
            rootTable.add();
        }
    }

    private Table createCoordsDebugTable(Skin uiSkin) {
        final Table table = UIUtils.createTable();
        table.add(new Label("Camera pos", uiSkin));
        table.add(new UpdatableLabel(uiSkin, () -> WORLD_CAMERA_PARAMS.position.x + ", " + WORLD_CAMERA_PARAMS.position.y));

        table.row();
        table.add(new Label("Zoom", uiSkin));
        table.add(new UpdatableLabel(uiSkin, () -> WORLD_CAMERA_PARAMS.zoom));

        table.row();
        table.add(new Label("Screen", uiSkin));
        table.add(screenCoord = new Label("", uiSkin));

        table.row();
        table.add(new Label("Viewport", uiSkin));
        table.add(viewCoord = new Label("", uiSkin));

        table.row();
        table.add(new Label("World", uiSkin));
        table.add(worldCoord = new Label("", uiSkin));
        return table;
    }

    @Override
    public void render(float dt) {
        toGameView.convert(Gdx.input.getX(), Gdx.input.getY(), GAME_VIEW_COORDS);
        worldCameraParamsGetter.accept(WORLD_CAMERA_PARAMS);

        super.render(dt);
    }

    static class WorldCameraParams {
        final Vector3 position = new Vector3();
        float zoom;
    }
}
