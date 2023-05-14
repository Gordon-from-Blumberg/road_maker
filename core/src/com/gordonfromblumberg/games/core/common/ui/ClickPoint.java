package com.gordonfromblumberg.games.core.common.ui;

import com.badlogic.gdx.utils.Pool;
import com.gordonfromblumberg.games.core.common.animation.AnimatedParameterFloat;
import com.gordonfromblumberg.games.core.common.animation.GbAnimation;
import com.gordonfromblumberg.games.core.common.animation.TimingFunction;
import com.gordonfromblumberg.games.core.common.utils.Poolable;

public class ClickPoint implements Poolable {
    private static final Pool<ClickPoint> pool = new Pool<ClickPoint>() {
        @Override
        protected ClickPoint newObject() {
            return new ClickPoint();
        }
    };
    static final float DURATION = 0.25f;
    public static final float WIDTH = 10f;
    public static final float HEIGHT = WIDTH / 1.5f;
    private static final int CLICK_CIRCLE = 0;
    private static final AnimatedParameterFloat clickCircle = AnimatedParameterFloat.getInstance();

    static {
        clickCircle.addStop(0, 0);
        clickCircle.addStop(1, 1);
        clickCircle.setTimingFunction(TimingFunction.QUADRATIC);
    }

    GbAnimation animation;
    private float x, y; // view coords

    private ClickPoint() {}

    public static ClickPoint getInstance() {
        return pool.obtain();
    }

    public ClickPoint init(float x, float y) {
        this.x = x;
        this.y = y;
        final GbAnimation anim = GbAnimation.getInstance();
        anim.setDuration(DURATION)
            .addFloat(CLICK_CIRCLE, clickCircle);
        this.animation = anim;
        return this;
    }

    public float getCircle() {
        return animation.getFloat(CLICK_CIRCLE);
    }

    public GbAnimation getAnimation() {
        return animation;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    @Override
    public void release() {
        pool.free(this);
    }

    @Override
    public void reset() {
        animation.release();
        animation = null;
        x = y = 0;
    }
}
