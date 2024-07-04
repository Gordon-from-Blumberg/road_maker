package com.gordonfromblumberg.games.core.common.screens;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Disableable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.gordonfromblumberg.games.core.common.factory.AbstractFactory;
import com.gordonfromblumberg.games.core.common.ui.FloatChangeableLabel;
import com.gordonfromblumberg.games.core.common.ui.IntChangeableLabel;
import com.gordonfromblumberg.games.core.common.ui.UIUtils;
import com.gordonfromblumberg.games.core.common.ui.UpdatableLabel;
import com.gordonfromblumberg.games.core.common.utils.Assets;
import com.gordonfromblumberg.games.core.common.utils.ConfigManager;
import com.gordonfromblumberg.games.core.common.world.*;

import java.util.function.IntConsumer;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public class MainUIRenderer extends WorldUIRenderer<MainWorld> {
    private static final float FIELD_WIDTH = 60f;

    private final Table algorithmParamsTable;

    public MainUIRenderer(SpriteBatch batch, MainWorld world, Supplier<Vector3> viewCoords) {
        super(batch, world, viewCoords);

        final ConfigManager config = AbstractFactory.getInstance().configManager();

        rootTable.add().expandX();

        final Skin skin = Assets.manager().get("ui/uiskin.json", Skin.class);

        Table table = UIUtils.createTable(skin);
        table.setBackground(skin.getDrawable("default-round-large"));
        table.defaults().pad(2f);
        table.columnDefaults(0).align(Align.right);
        table.columnDefaults(1).align(Align.left);

        final MainWorldParams params = world.getParams();
        IntChangeableLabel heightLabel = sizeLabel(skin, params::setHeight, params.getHeight());
        table.add("Grid shape");
        table.add(shapeSelector(skin, heightLabel)).fillX();

        table.row();
        table.add("Width");
        table.add(sizeLabel(skin, params::setWidth, params.getWidth()));

        table.row();
        table.add("Height");
        table.add(heightLabel);

        table.row();
        table.add("City count");
        table.add(cityCountLabel(skin, params::setCityCount, params.getCityCount()));

        table.row();
        table.add("Obstacles level");
        table.add(obstacleLevelLabel(skin, params::setObstacleLevel, params.getObstacleLevel()));

        table.row();
        table.add("Default weight");
        table.add(weightLabel(skin, world::setDefaultWeight, (int) params.getDefaultWeight()));

        table.row();
        table.add("Road weight");
        table.add(weightLabel(skin, world::setRoadWeight, (int) params.getRoadWeight()));

        table.row();
        table.add(generateButton(skin)).colspan(2).align(Align.center).padTop(5f).padBottom(15f);

        table.row();
        table.add("Steps per second");
        table.add(stepsLabel(skin, world::setStepsPerSec, world.getStepsPerSec()));

        table.row();
        table.add("Algorithm");
        table.add(algorithmSelector(skin));

        table.row();
        table.add(runButton(skin)).colspan(2).align(Align.center).padTop(5f).padBottom(15f);

        table.row();
        table.add("Algorithm parameters").colspan(2).align(Align.center);

        algorithmParamsTable = UIUtils.createTable(skin);
        algorithmParamsTable.defaults().pad(2f);
        algorithmParamsTable.columnDefaults(0).align(Align.right);
        algorithmParamsTable.columnDefaults(1).align(Align.left);

        table.row();
        fillAlgorithmParamsTable(world.getAlgorithm().getParams(), skin);
        table.add(algorithmParamsTable).colspan(2).align(Align.center);

        table.row().expandY().align(Align.bottom);
        table.add("Status");
        table.add(new UpdatableLabel(skin, () -> !world.isRunning()
                ? "Not started" : world.isFinished()
                ? "Finished" : "In progress"));

        rootTable.add(table).fillY().expandY();
    }

    private IntChangeableLabel sizeLabel(Skin skin, IntConsumer onChangeListener, int value) {
        IntChangeableLabel sizeLabel = new IntChangeableLabel(skin, onChangeListener);
        sizeLabel.setMinValue(5);
        sizeLabel.setMaxValue(255);
        sizeLabel.setValue(value);
        sizeLabel.setStep(5);
        sizeLabel.setFieldWidth(FIELD_WIDTH);
        return sizeLabel;
    }

    private IntChangeableLabel cityCountLabel(Skin skin, IntConsumer onChangeListener, int value) {
        IntChangeableLabel label = new IntChangeableLabel(skin, onChangeListener);
        label.setMinValue(3);
        label.setMaxValue(16);
        label.setValue(value);
        label.setStep(1);
        label.setFieldWidth(FIELD_WIDTH);
        return label;
    }

    private IntChangeableLabel obstacleLevelLabel(Skin skin, IntConsumer onChangeListener, int value) {
        IntChangeableLabel label = new IntChangeableLabel(skin, onChangeListener);
        label.setMinValue(0);
        label.setMaxValue(3);
        label.setValue(value);
        label.setStep(1);
        label.setFieldWidth(FIELD_WIDTH);
        return label;
    }

    private IntChangeableLabel weightLabel(Skin skin, IntConsumer onChangeListener, int value) {
        IntChangeableLabel label = new IntChangeableLabel(skin, onChangeListener);
        label.setMinValue(1);
        label.setMaxValue(10);
        label.setValue(value);
        label.setStep(1);
        label.setFieldWidth(FIELD_WIDTH);
        return label;
    }

    private IntChangeableLabel stepsLabel(Skin skin, IntConsumer onChangeListener, int value) {
        IntChangeableLabel label = new IntChangeableLabel(skin, onChangeListener);
        label.setMinValue(5);
        label.setMaxValue(60);
        label.setValue(value);
        label.setStep(5);
        label.setFieldWidth(FIELD_WIDTH);
        return label;
    }

    private SelectBox<MainWorldParams.GridShape> shapeSelector(Skin skin, Disableable heightLabel) {
        SelectBox<MainWorldParams.GridShape> box = new SelectBox<>(skin);
        box.setItems(MainWorldParams.GridShape.RECT);
        box.setSelected(world.getParams().getShape());
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

    private SelectBox<Algorithm> algorithmSelector(Skin skin) {
        SelectBox<Algorithm> box = new SelectBox<>(skin);
        box.setItems(world.algorithms);
        box.setSelected(world.getAlgorithm());
        box.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SelectBox<Algorithm> selectBox = (SelectBox<Algorithm>) actor;
                Algorithm selected = selectBox.getSelected();
                world.setAlgorithm(selected);
                fillAlgorithmParamsTable(selected.getParams(), skin);
            }
        });

        return box;
    }

    private void fillAlgorithmParamsTable(Array<AlgorithmParam> params, Skin skin) {
        algorithmParamsTable.clear();

        for (AlgorithmParam param : params) {
            algorithmParamsTable.row();
            algorithmParamsTable.add(param.getName());
            Actor component = param.createComponent(skin);
            if (component instanceof IntChangeableLabel icl) {
                icl.setFieldWidth(FIELD_WIDTH);
            }
            if (component instanceof FloatChangeableLabel fcl) {
                fcl.setFieldWidth(FIELD_WIDTH);
            }
            algorithmParamsTable.add(component);
        }
    }

    private TextButton generateButton(Skin skin) {
        return UIUtils.textButton("Generate grid", skin, world::createGrid, null);
    }

    private TextButton runButton(Skin skin) {
        return UIUtils.textButton("Run algorithm", skin, world::run, null);
    }
}
