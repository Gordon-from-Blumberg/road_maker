package com.gordonfromblumberg.games.core.game_template;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.gordonfromblumberg.games.core.common.factory.AbstractFactory;
import com.gordonfromblumberg.games.core.common.log.LogManager;
import com.gordonfromblumberg.games.core.common.log.Logger;
import com.gordonfromblumberg.games.core.common.ui.SaveLoadWindow;
import com.gordonfromblumberg.games.core.common.utils.Assets;
import com.gordonfromblumberg.games.core.common.utils.ConfigManager;
import com.gordonfromblumberg.games.core.common.world.WorldUIRenderer;

import java.util.function.Supplier;

public class TemplateUIRenderer extends WorldUIRenderer<TemplateWorld> {
    private static final Logger log = LogManager.create(TemplateUIRenderer.class);
    private static final String DEFAULT_SAVE_DIR = "saves";
    private static final String SAVE_EXTENSION = "dat";

    private SaveLoadWindow saveWindow, loadWindow;

    public TemplateUIRenderer(SpriteBatch batch, TemplateWorld world, Supplier<Vector3> viewCoords) {
        super(batch, world, viewCoords);
        log.info("TemplateUIRenderer constructor");

        init();
    }

    private void init() {
        final ConfigManager configManager = AbstractFactory.getInstance().configManager();
        final AssetManager assets = Assets.manager();
        final Skin uiSkin = assets.get("ui/uiskin.json", Skin.class);

        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.F5 && (loadWindow == null || !loadWindow.isVisible())) {
                    if (saveWindow == null) {
                        saveWindow = createSaveLoadWindow(false, uiSkin);
                        saveWindow.open(bb -> {
                            bb.put((byte) 1);
                            bb.putChar('H');
                            bb.putChar('i');
                            bb.putLong(System.currentTimeMillis());
                        });
                    } else {
                        saveWindow.toggle();
                    }
                    return true;
                } else if (keycode == Input.Keys.F6 && (saveWindow == null || !saveWindow.isVisible())) {
                    if (loadWindow == null) {
                        loadWindow = createSaveLoadWindow(true, uiSkin);
                        loadWindow.open(bb -> {
                            while (bb.hasRemaining()) {
                                log.debug(String.valueOf(bb.get()));
                            }
                        });
                    } else {
                        loadWindow.toggle();
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private SaveLoadWindow createSaveLoadWindow(boolean load, Skin skin) {
        ConfigManager config = AbstractFactory.getInstance().configManager();
        SaveLoadWindow window = new SaveLoadWindow(
                stage,
                skin,
                config.getString("saves.dir", DEFAULT_SAVE_DIR),
                SAVE_EXTENSION,
                load
        );

        window.setWidth(config.getFloat("ui.saveload.width"));
        window.setHeight(config.getFloat("ui.saveload.height"));

        return window;
    }
}
