package com.gordonfromblumberg.games.core.common.world;

public class StraightforwardAlgorithm implements Algorithm {
    private static final StraightforwardAlgorithm instance = new StraightforwardAlgorithm();

    private StraightforwardAlgorithm() { }

    public static StraightforwardAlgorithm instance() {
        return instance;
    }

    @Override
    public boolean step(MainWorld world) {
        return false;
    }

    @Override
    public float getStepDelayCoef() {
        return 0;
    }

    @Override
    public String toString() {
        return "Straightforward";
    }
}
