package com.gordonfromblumberg.games.core.common.world;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectIntMap;
import com.badlogic.gdx.utils.Pool;
import com.gordonfromblumberg.games.core.common.graph.Dijkstra;
import com.gordonfromblumberg.games.core.common.graph.Edge;
import com.gordonfromblumberg.games.core.common.grid.Hex;
import com.gordonfromblumberg.games.core.common.grid.HexGrid;
import com.gordonfromblumberg.games.core.common.ui.FloatChangeableLabel;
import com.gordonfromblumberg.games.core.common.ui.IntChangeableLabel;
import com.gordonfromblumberg.games.core.common.utils.Poolable;
import com.gordonfromblumberg.games.core.common.utils.RandomGen;

import java.util.Iterator;
import java.util.Objects;

public class AntsAlgorithm implements Algorithm {
    private static final String ANT_COUNT = "Ant count";
    private static final String WEIGHT_CHANGE = "Weight change";
    private static final String ROTATE_CHANCE = "Rotate chance";
    private static final String LIFE_TURNS = "Life turns";
    private static AntsAlgorithm instance;

    private static final Dijkstra<Hex> dijkstra = new Dijkstra<>();
    private static final Array<Hex> path = new Array<>();
    private static final Pool<LifeMapKey> lifeMapKeyPool = new Pool<>() {
        @Override
        protected LifeMapKey newObject() {
            return new LifeMapKey();
        }
    };

    private final Array<AlgorithmParam> params = new Array<>();
    private final Array<Ant> ants = new Array<>();
    private final ObjectIntMap<LifeMapKey> lifeMap = new ObjectIntMap<>();

    private int turn;

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
        params.add(new AlgorithmParam(
                LIFE_TURNS,
                Integer.class,
                50,
                (skin, value, valueConsumer) -> {
                    IntChangeableLabel label = new IntChangeableLabel(skin, valueConsumer::accept);
                    label.setMinValue(20);
                    label.setMaxValue(250);
                    label.setValue((int) value);
                    label.setStep(5);
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

            LifeMapKey mapKey = getLifeMapKey(ant.hex, next, world.grid);
            if (lifeMap.containsKey(mapKey)) {
                lifeMap.put(mapKey, turn);
                mapKey.release();
            } else {
                lifeMap.put(mapKey, turn);
            }

            ant.hex = next;

            if (ant.hex == ant.goal) ant.goal = null;
        }

        final float defaultWeight = world.getParams().defaultWeight;
        final int turnToDie = turn - (int) getParamValue(LIFE_TURNS);
        Iterator<ObjectIntMap.Entry<LifeMapKey>> itr = lifeMap.iterator();
        while (itr.hasNext()) {
            ObjectIntMap.Entry<LifeMapKey> entry = itr.next();
            if (entry.value <= turnToDie) {
                float weight = world.grid.getWeight(entry.key.from, entry.key.to);
                weight = Math.min(weight + weightChange, defaultWeight);
                if (MathUtils.isEqual(weight, defaultWeight)) {
                    weight = defaultWeight;
                    itr.remove();
                } else {
                    lifeMap.put(entry.key, turn);
                }
                world.grid.setWeight(entry.key.from, entry.key.to, weight);
            }
        }

        ++turn;
        return false;
    }

    @Override
    public void reset() {
        for (Ant ant : ants) {
            ant.hex = null;
            ant.goal = null;
        }
        turn = 0;
    }

    @Override
    public Array<AlgorithmParam> getParams() {
        return params;
    }

    @Override
    public String toString() {
        return "Ants";
    }

    private int getAntCount() {
        return (int) getParamValue(ANT_COUNT);
    }

    private float getWeightChange() {
        return (float) getParamValue(WEIGHT_CHANGE);
    }

    Object getParamValue(String name) {
        for (AlgorithmParam param : params) {
            if (name.equals(param.getName())) {
                return param.getValue();
            }
        }
        throw new IllegalStateException("Unknown param: " + name);
    }

    private LifeMapKey getLifeMapKey(Hex current, Hex next, HexGrid grid) {
        LifeMapKey key = lifeMapKeyPool.obtain();
        return grid.getDir(current, next) < 3
                ? key.set(current, next)
                : key.set(next, current);
    }

    static class Ant {
        Hex hex;
        Hex goal;
    }

    static class LifeMapKey implements Poolable {
        private Hex from;
        private Hex to;

        LifeMapKey set(Hex from, Hex to) {
            this.from = from;
            this.to = to;
            return this;
        }

        @Override
        public void release() {
            lifeMapKeyPool.free(this);
        }

        @Override
        public void reset() {
            from = null;
            to = null;
        }

        @Override
        public int hashCode() {
            return Objects.hash(from, to);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof LifeMapKey other)) return false;
            return this.from == other.from && this.to == other.to;
        }
    }
}
