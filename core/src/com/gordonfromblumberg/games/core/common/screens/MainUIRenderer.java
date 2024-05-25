package com.gordonfromblumberg.games.core.common.screens;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Disableable;
import com.badlogic.gdx.utils.Align;
import com.gordonfromblumberg.games.core.common.ui.IntChangeableLabel;
import com.gordonfromblumberg.games.core.common.ui.UIUtils;
import com.gordonfromblumberg.games.core.common.utils.Assets;
import com.gordonfromblumberg.games.core.common.world.MainWorld;
import com.gordonfromblumberg.games.core.common.world.MainWorldParams;
import com.gordonfromblumberg.games.core.common.world.WorldUIRenderer;

import java.util.function.IntConsumer;
import java.util.function.Supplier;

public class MainUIRenderer extends WorldUIRenderer<MainWorld> {

    public MainUIRenderer(SpriteBatch batch, MainWorld world, Supplier<Vector3> viewCoords) {
        super(batch, world, viewCoords);

        rootTable.add().expandX();

        final Skin skin = Assets.manager().get("ui/uiskin.json", Skin.class);

        Table table = UIUtils.createTable(skin);
        table.defaults().pad(2f);
        table.columnDefaults(0).align(Align.right);
        table.columnDefaults(1).align(Align.left);

        IntChangeableLabel heightLabel = sizeLabel(skin, v -> world.getParams().setHeight(v));
        table.add("Grid shape");
        table.add(shapeSelector(skin, heightLabel));

        table.row();
        table.add("Width");
        table.add(sizeLabel(skin, v -> world.getParams().setWidth(v)));

        table.row();
        table.add("Height");
        table.add(heightLabel);

        rootTable.add(table).width(300);

        rootTable.row().expandY();
    }

    private IntChangeableLabel sizeLabel(Skin skin, IntConsumer onChangeListener) {
        IntChangeableLabel sizeLabel = new IntChangeableLabel(skin, onChangeListener);
        sizeLabel.setMinValue(5);
        sizeLabel.setMaxValue(255);
        sizeLabel.setValue(20);
        sizeLabel.setStep(5);
        sizeLabel.setFieldWidth(45);
        return sizeLabel;
    }

    private SelectBox<MainWorldParams.GridShape> shapeSelector(Skin skin, Disableable heightLabel) {
        SelectBox<MainWorldParams.GridShape> box = new SelectBox<>(skin);
        box.setItems(MainWorldParams.GridShape.values());
        box.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SelectBox<MainWorldParams.GridShape> selectBox = (SelectBox<MainWorldParams.GridShape>) actor;
                MainWorldParams.GridShape shape = selectBox.getSelected();
                world.getParams().setShape(shape);
                heightLabel.setDisabled(shape == MainWorldParams.GridShape.HEX);
            }
        });

        return box;
    }
}
