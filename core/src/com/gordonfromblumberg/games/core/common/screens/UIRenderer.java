package com.gordonfromblumberg.games.core.common.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.gordonfromblumberg.games.core.common.debug.DebugOptions;
import com.gordonfromblumberg.games.core.common.factory.AbstractFactory;
import com.gordonfromblumberg.games.core.common.log.LogManager;
import com.gordonfromblumberg.games.core.common.log.Logger;
import com.gordonfromblumberg.games.core.common.ui.UIUtils;
import com.gordonfromblumberg.games.core.common.ui.UIViewport;
import com.gordonfromblumberg.games.core.common.utils.ConfigManager;

public class UIRenderer extends AbstractRenderer {
    private static final Logger log = LogManager.create(UIRenderer.class);

    protected Stage stage;
    protected Table rootTable;

    protected float minWidth;
    protected float minHeight;

    public UIRenderer(SpriteBatch batch) {
        super();

        log.info("UIRenderer constructor for class " + getClass().getSimpleName());

        this.centerCamera = true;
        this.stage = new Stage(viewport, batch);
        if (DebugOptions.DEBUG_UI) {
            stage.setDebugAll(true);
        }

        Table table = UIUtils.createTable();
        table.setFillParent(true);
        this.rootTable = table;
        this.stage.addActor(table);
    }

    @Override
    public void render(float dt) {
        super.render(dt);

        stage.act();
        stage.draw();
    }

    @Override
    protected Viewport createViewport(Camera camera) {
        final ConfigManager configManager = AbstractFactory.getInstance().configManager();
        minWidth = configManager.getFloat("minUiWidth");
        minHeight = configManager.getFloat("minUiHeight");
        return new UIViewport(minWidth, minHeight, camera);
    }

    public void addListener(InputListener listener) {
        stage.addListener(listener);
    }

    public void setAsInputProcessor() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
