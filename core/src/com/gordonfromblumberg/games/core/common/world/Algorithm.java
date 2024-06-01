package com.gordonfromblumberg.games.core.common.world;

public interface Algorithm {
    float getStepDelayCoef();
    boolean step(MainWorld world);
    String toString();
}
