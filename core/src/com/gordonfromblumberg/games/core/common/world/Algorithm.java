package com.gordonfromblumberg.games.core.common.world;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Array;

public interface Algorithm {
    float getStepDelayCoef();
    boolean step(MainWorld world);
    void reset();
    String toString();
    Array<AlgorithmParam> getParams();

    default void save(String commonPrefix, Preferences prefs) {
        String prefix = commonPrefix + "." + this + ".";
        for (AlgorithmParam param : getParams()) {
            param.save(prefix, prefs);
        }
    }

    default void load(String commonPrefix, Preferences prefs) {
        String prefix = commonPrefix + "." + this + ".";
        for (AlgorithmParam param : getParams()) {
            param.load(prefix, prefs);
        }
    }
}
