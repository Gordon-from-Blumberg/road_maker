package com.gordonfromblumberg.games.core.common.world;

import com.badlogic.gdx.utils.Array;
import com.gordonfromblumberg.games.core.common.graph.Dijkstra;
import com.gordonfromblumberg.games.core.common.grid.Hex;

public class AntsAlgorithm implements Algorithm {
    private static AntsAlgorithm instance;
    private static final Dijkstra<Hex> dijkstra = new Dijkstra<>();

    private final Array<AlgorithmParam> params = new Array<>();
    private final Array<Ant> ants = new Array<>();

    public AntsAlgorithm(MainWorld world) {
        params.add(new AlgorithmParam());
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

    static class Ant {
        Hex hex;
        Hex goal;
    }
}
