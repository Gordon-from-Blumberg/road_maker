package com.gordonfromblumberg.games.core.common.world;

import com.badlogic.gdx.utils.Array;
import com.gordonfromblumberg.games.core.common.graph.Dijkstra;
import com.gordonfromblumberg.games.core.common.grid.Hex;
import com.gordonfromblumberg.games.core.common.ui.FloatChangeableLabel;
import com.gordonfromblumberg.games.core.common.ui.IntChangeableLabel;

public class AntsAlgorithm implements Algorithm {
    private static AntsAlgorithm instance;
    private static final Dijkstra<Hex> dijkstra = new Dijkstra<>();

    private final Array<AlgorithmParam> params = new Array<>();
    private final Array<Ant> ants = new Array<>();

    public AntsAlgorithm(MainWorld world) {
        params.add(new AlgorithmParam(
                "Ant count",
                world.params.getCityCount() / 2,
                (skin, value, valueConsumer) -> {
                    IntChangeableLabel label = new IntChangeableLabel(skin, valueConsumer::accept);
                    label.setMinValue(3);
                    label.setMaxValue(32);
                    label.setValue((int) value);
                    label.setStep(1);
                    return label;
                }
        ));
        params.add(new AlgorithmParam(
                "Weight change",
                0.2f,
                (skin, value, valueConsumer) -> {
                    FloatChangeableLabel label = new FloatChangeableLabel(skin, valueConsumer::accept);
                    label.setMinValue(0.1f);
                    label.setMaxValue(1f);
                    label.setValue((float) value);
                    label.setStep(0.1f);
                    return label;
                }
        ));
    }

    public static AntsAlgorithm instance(MainWorld world) {
        if (instance == null) instance = new AntsAlgorithm(world);
        return instance;
    }

    @Override
    public float getStepDelayCoef() {
        return 0.3f;
    }

    @Override
    public boolean step(MainWorld world) {
        return false;
    }

    @Override
    public void reset() {

    }

    @Override
    public Array<AlgorithmParam> getParams() {
        return params;
    }

    @Override
    public String toString() {
        return "Ants";
    }

    static class Ant {
        Hex hex;
        Hex goal;
    }
}
