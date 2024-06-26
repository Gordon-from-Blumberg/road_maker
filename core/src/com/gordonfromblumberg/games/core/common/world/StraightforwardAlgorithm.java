package com.gordonfromblumberg.games.core.common.world;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.gordonfromblumberg.games.core.common.graph.Dijkstra;
import com.gordonfromblumberg.games.core.common.grid.Hex;
import com.gordonfromblumberg.games.core.common.utils.ValueMapComparator;

import java.util.Comparator;

@SuppressWarnings("unchecked")
public class StraightforwardAlgorithm implements Algorithm {
    private static StraightforwardAlgorithm instance;
    private static final Dijkstra<Hex> dijkstra = new Dijkstra<>();
    private static final ObjectMap<Hex, Float> valueMap = new ObjectMap<>();
    private static final Comparator<Hex> cityComparator = new ValueMapComparator<>(valueMap);
    private static final Array<Hex> array = new Array<>();

    private final ObjectMap<Hex, Array<Hex>> cityMap = new ObjectMap<>();
    private final Array<AlgorithmParam> params = new Array<>();

    private int cityIdx = 0;
    private int roadIdx = 0;

    private StraightforwardAlgorithm(MainWorld world) {
        params.add(new AlgorithmParam(
                "Mode",
                Mode.ONE_PASS,
                (skin, valueConsumer) -> {
                    SelectBox<Mode> box = new SelectBox<>(skin);
                    box.setItems(Mode.values());
                    box.setSelected(getMode());
                    box.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                            SelectBox<Mode> selectBox = (SelectBox<Mode>) actor;
                            valueConsumer.accept(selectBox.getSelected());
                            world.reset();
                        }
                    });
                    return box;
                }
        ));
    }

    public static StraightforwardAlgorithm instance(MainWorld world) {
        if (instance == null) instance = new StraightforwardAlgorithm(world);
        return instance;
    }

    @Override
    public boolean step(MainWorld world) {
        if (cityMap.isEmpty())
            init(world);

        if (cityIdx == cityMap.size)
            return true;

        Hex city = world.cities.get(cityIdx++);
        Array<Hex> otherCities = cityMap.get(city);

        array.clear();
        dijkstra.findPath(world.grid, city, otherCities.get(roadIdx), array);
        float defaultWeight = world.getParams().defaultWeight;
        float roadWeight = world.getParams().roadWeight;

        Mode mode = (Mode) params.get(0).getValue();
        switch (mode) {
            case ONE_PASS -> {
                for (int i = 0, n = array.size - 1; i < n; ++i) {
                    world.grid.setWeight(array.get(i), array.get(i + 1), roadWeight);
                }
            }
            case TWO_PASS -> {
                float halfRoadWeight = (defaultWeight + roadWeight) / 2;
                for (int i = 0, n = array.size - 1; i < n; ++i) {
                    float weight = world.grid.getWeight(array.get(i), array.get(i + 1));
                    if (weight == defaultWeight)
                        world.grid.setWeight(array.get(i), array.get(i + 1), halfRoadWeight);
                    else if (weight == halfRoadWeight)
                        world.grid.setWeight(array.get(i), array.get(i + 1), roadWeight);
                }
            }
        }

        if (cityIdx == cityMap.size) {
            if (++roadIdx == cityIdx - 1) {
                return true;
            }
            cityIdx = 0;
        }
        return false;
    }

    private void init(MainWorld world) {
        for (Hex city : world.cities) {
            Array<Hex> otherCities = new Array<>(world.cities);
            otherCities.removeValue(city, true);
            valueMap.clear();
            dijkstra.markNodes(world.grid, city, valueMap, otherCities);
            otherCities.sort(cityComparator);
            cityMap.put(city, otherCities);
        }
    }

    @Override
    public Array<AlgorithmParam> getParams() {
        return params;
    }

    @Override
    public float getStepDelayCoef() {
        return 1f;
    }

    private Mode getMode() {
        return (Mode) params.get(0).getValue();
    }

    @Override
    public void reset() {
        cityIdx = 0;
        roadIdx = 0;
        cityMap.clear();
    }

    @Override
    public String toString() {
        return "Straightforward";
    }

    private enum Mode {
        ONE_PASS("One pass"),
        TWO_PASS("Two pass");

        private final String name;

        Mode(String name) {
            this.name = name;
        }


        @Override
        public String toString() {
            return name;
        }
    }
}
