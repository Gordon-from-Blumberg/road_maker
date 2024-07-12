package com.gordonfromblumberg.games.core.common.world;

import com.badlogic.gdx.utils.Array;
import com.gordonfromblumberg.games.core.common.graph.Dijkstra;
import com.gordonfromblumberg.games.core.common.grid.Hex;
import com.gordonfromblumberg.games.core.common.ui.FloatChangeableLabel;
import com.gordonfromblumberg.games.core.common.ui.IntChangeableLabel;
import com.gordonfromblumberg.games.core.common.utils.RandomGen;

public class AntsAlgorithm implements Algorithm {
    private static final String ANT_COUNT = "Ant count";
    private static final String WEIGHT_CHANGE = "Weight change";
    private static AntsAlgorithm instance;

    private static final Dijkstra<Hex> dijkstra = new Dijkstra<>();
    private static final Array<Hex> path = new Array<>();

    private final Array<AlgorithmParam> params = new Array<>();
    private final Array<Ant> ants = new Array<>();

    public AntsAlgorithm(MainWorld world) {
        params.add(new AlgorithmParam(
                ANT_COUNT,
                Integer.class,
                5,
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
                WEIGHT_CHANGE,
                Float.class,
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
        int countToCreate = getAntCount() - ants.size;
        while (countToCreate-- > 0) ants.add(new Ant());

        final float roadWeight = world.getParams().roadWeight;
        final float weightChange = getWeightChange();
        for (Ant ant : ants) {
            if (ant.hex == null) {
                ant.hex = RandomGen.INSTANCE.getRandomItem(world.cities);
            }
            if (ant.goal == null) {
                Hex goal;
                do {
                    goal = RandomGen.INSTANCE.getRandomItem(world.cities);
                } while (goal == ant.hex);
                ant.goal = goal;
            }

            path.clear();
            dijkstra.findPath(world.grid, ant.hex, ant.goal, path);
            Hex next = path.get(1);
            float weight = world.grid.getWeight(ant.hex, next);
            weight = Math.max(weight - weightChange, roadWeight);
            world.grid.setWeight(ant.hex, next, weight);
            ant.hex = next;

            if (ant.hex == ant.goal) ant.goal = null;
        }

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

    int getAntCount() {
        for (AlgorithmParam param : params) {
            if (ANT_COUNT.equals(param.getName())) {
                return (int) param.getValue();
            }
        }
        return 0;
    }

    float getWeightChange() {
        for (AlgorithmParam param : params) {
            if (WEIGHT_CHANGE.equals(param.getName())) {
                return (float) param.getValue();
            }
        }
        return 0f;
    }

    static class Ant {
        Hex hex;
        Hex goal;
    }
}
