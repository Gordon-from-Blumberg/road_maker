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
    SQRT3 {
        @Override
        public float calculate(float t) {
            return (float) Math.pow(t, 1.0 / 3);
        }
    },
    QUADRATIC {
        @Override
        public float calculate(float t) {
            return t * t;
        }
    },
    CUBIC {
        @Override
        public float calculate(float t) {
            return t * t * t;
        }
    }
    ;

    public abstract float calculate(float t);
}
