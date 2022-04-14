package com.gordonfromblumberg.games.core.common.animation;

public enum TimingFunction {
    LINEAR {
        @Override
        public float calculate(float t) {
            return t;
        }
    },
    SQRT {
        @Override
        public float calculate(float t) {
            return (float) Math.sqrt(t);
        }
    },
    QUADRATIC {
        @Override
        public float calculate(float t) {
            return t * t;
        }
    }
    ;

    public abstract float calculate(float t);
}
