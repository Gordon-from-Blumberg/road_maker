package com.gordonfromblumberg.games.core.common.world;

import com.badlogic.gdx.utils.Array;
import com.gordonfromblumberg.games.core.common.graph.Dijkstra;
import com.gordonfromblumberg.games.core.common.graph.Edge;
import com.gordonfromblumberg.games.core.common.grid.Hex;
import com.gordonfromblumberg.games.core.common.ui.FloatChangeableLabel;
import com.gordonfromblumberg.games.core.common.ui.IntChangeableLabel;
import com.gordonfromblumberg.games.core.common.utils.RandomGen;

public class AntsAlgorithm implements Algorithm {
    private static final String ANT_COUNT = "Ant count";
    private static final String WEIGHT_CHANGE = "Weight change";
    private static final String ROTATE_CHANCE = "Rotate chance";
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
        params.add(new AlgorithmParam(
                ROTATE_CHANCE,
                Float.class,
                0.1f,
                (skin, value, valueConsumer) -> {
                    FloatChangeableLabel label = new FloatChangeableLabel(skin, valueConsumer::accept);
                    label.setMinValue(0.01f);
                    label.setMaxValue(0.5f);
                    label.setValue((float) value);
                    label.setStep(0.01f);
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

        if (countToCreate < -1) {
            ants.removeRange(ants.size + 1 + countToCreate, ants.size - 1);
        }

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

            if (RandomGen.INSTANCE.nextBool((float) getParamValue(ROTATE_CHANCE))) {
                int dir = world.grid.getDir(ant.hex, next);
                Edge<Hex> relatedEdge = RandomGen.INSTANCE.nextBool()
                            ? world.grid.next(ant.hex, (dir + 1) % 6)
                            : world.grid.next(ant.hex, (dir - 1 + 6) % 6);
                if (relatedEdge != null)
                    next = relatedEdge.getNode();
            }

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
        return (int) getParamValue(ANT_COUNT);
    }

    float getWeightChange() {
        return (float) getParamValue(WEIGHT_CHANGE);
    }

    Object getParamValue(String name) {
        for (AlgorithmParam param : params) {
            if (name.equals(param.getName())) {
                return param.getValue();
            }
        }
        return null;
    }

    static class Ant {
        Hex hex;
        Hex goal;
    }
}
