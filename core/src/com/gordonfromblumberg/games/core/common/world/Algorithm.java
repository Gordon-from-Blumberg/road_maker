package com.gordonfromblumberg.games.core.common.world;

import com.badlogic.gdx.utils.Array;

public interface Algorithm {
    float getStepDelayCoef();
    boolean step(MainWorld world);
    void reset();
    String toString();
    Array<AlgorithmParam> getParams();
}
