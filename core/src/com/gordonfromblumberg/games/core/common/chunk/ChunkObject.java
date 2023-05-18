package com.gordonfromblumberg.games.core.common.chunk;

import com.badlogic.gdx.utils.Pool;

public class ChunkObject<T> implements Pool.Poolable {
    float x, y;
    T object;

    ChunkObject() {}

    ChunkObject<T> set(float x, float y, T object) {
        this.x = x;
        this.y = y;
        this.object = object;
        return this;
    }

    @Override
    public void reset() {
        x = 0;
        y = 0;
        object = null;
    }
}
