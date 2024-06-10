package com.gordonfromblumberg.games.core.common.world;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.gordonfromblumberg.games.core.common.graph.Dijkstra;
import com.gordonfromblumberg.games.core.common.grid.Hex;
import com.gordonfromblumberg.games.core.common.utils.ValueMapComparator;

import java.util.Comparator;

public class StraightforwardAlgorithm implements Algorithm {
    private static final StraightforwardAlgorithm instance = new StraightforwardAlgorithm();
    private static final Dijkstra<Hex> dijkstra = new Dijkstra<>();
    private static final ObjectMap<Hex, Float> valueMap = new ObjectMap<>();
    private static final Comparator<Hex> cityComparator = new ValueMapComparator<>(valueMap);
    private static final Array<Hex> array = new Array<>();

    private final ObjectMap<Hex, Array<Hex>> cityMap = new ObjectMap<>();

    private int cityIdx = 0;
    private int roadIdx = 0;

    private StraightforwardAlgorithm() { }

    public static StraightforwardAlgorithm instance() {
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
        float roadWeight = world.getParams().roadWeight;

        for (int i = 0, n = array.size - 1; i < n; ++i) {
            world.grid.setWeight(array.get(i), array.get(i + 1), roadWeight);
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
    public float getStepDelayCoef() {
        return 1f;
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
}
