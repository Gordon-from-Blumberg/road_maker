package com.gordonfromblumberg.games.core.common.animation;

import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.Pool;
import com.gordonfromblumberg.games.core.common.utils.Poolable;

public class AnimatedParameterFloat implements Poolable {
    private static final Pool<AnimatedParameterFloat> pool = new Pool<AnimatedParameterFloat>() {
        @Override
        protected AnimatedParameterFloat newObject() {
            return new AnimatedParameterFloat();
        }
    };

    private final FloatArray stopValues = new FloatArray(true, 8);
    private final FloatArray stopPositions = new FloatArray(true, 8);
    private TimingFunction timingFunction;

    private AnimatedParameterFloat() {}

    public static AnimatedParameterFloat getInstance() {
        return pool.obtain();
    }

    public AnimatedParameterFloat addStop(float position, float value) {
        for (int i = 0, size = stopValues.size; i < size; ++i) {
            if (position < stopPositions.get(i)) {
                stopPositions.insert(i, position);
                stopValues.insert(i, value);
                return this;
            }
        }
        stopPositions.add(position);
        stopValues.add(value);
        return this;
    }

    public AnimatedParameterFloat timingFunction(TimingFunction timingFunction) {
        this.timingFunction = timingFunction;
        return this;
    }

    public float getValue(float t) {
        if (timingFunction != null) {
            t = timingFunction.calculate(t);
        }
        if (stopValues.isEmpty()) {
            return t;
        }
        if (t <= stopPositions.get(0)) {
            return stopValues.get(0);
        }
        final int size = stopValues.size;
        if (t >= stopPositions.get(size - 1)) {
            return stopValues.get(size - 1);
        }
        for (int i = 0; i < size; ++i) {
            final float curPos = stopPositions.get(i);
            final float nextPos = stopPositions.get(i + 1);
            if (t >= curPos && t <= nextPos) {
                final float alfa = (t - curPos) / (nextPos - curPos);
                final float curValue = stopValues.get(i);
                final float nextValue = stopValues.get(i + 1);
                return curValue + alfa * (nextValue - curValue);
            }
        }

        throw new IllegalStateException("Not found value for t = " + t);
    }

    @Override
    public void release() {
        pool.free(this);
    }

    @Override
    public void reset() {
        stopValues.clear();
        stopPositions.clear();
    }
}
