package com.gordonfromblumberg.games.core.common.animation;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.gordonfromblumberg.games.core.common.utils.Poolable;

public class GbAnimation implements Poolable {
    private static final Pool<GbAnimation> pool = new Pool<GbAnimation>() {
        @Override
        protected GbAnimation newObject() {
            return new GbAnimation();
        }
    };

    private final Array<AnimatedParameterFloat> floats = new Array<>();

    private float duration; // in seconds
    private float runningTime;
    private float process;
    private boolean infinite;
    private boolean forwardAndBack;

    private GbAnimation() {}

    public static GbAnimation getInstance() {
        return pool.obtain();
    }

    public GbAnimation addFloat(AnimatedParameterFloat parameterFloat) {
        floats.add(parameterFloat);
        return this;
    }

    public GbAnimation addFloat(int index, AnimatedParameterFloat parameterFloat) {
        floats.insert(index, parameterFloat);
        return this;
    }

    public void update(float dt) {
        runningTime += dt;
        if (infinite && runningTime > duration) {
            runningTime -= duration;
        }
        process = runningTime / duration;
        if (forwardAndBack) {
            process = process < 0.5f ? 2 * process : 2 - 2 * process;
        }
    }

    public float getFloat(int index) {
        return floats.get(index).getValue(process);
    }

    public boolean isFinished() {
        return runningTime >= duration;
    }

    public GbAnimation duration(float duration) {
        this.duration = duration;
        return this;
    }

    public GbAnimation infinite(boolean infinite) {
        this.infinite = infinite;
        return this;
    }

    public GbAnimation forwardAndBack(boolean forwardAndBack) {
        this.forwardAndBack = forwardAndBack;
        return this;
    }

    @Override
    public void release() {
        pool.free(this);
    }

    @Override
    public void reset() {
        floats.clear();
        duration = 0;
        runningTime = 0;
        process = 0;
    }
}
