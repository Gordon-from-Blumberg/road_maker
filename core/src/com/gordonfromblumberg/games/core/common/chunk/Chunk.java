package com.gordonfromblumberg.games.core.common.chunk;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

import java.util.Iterator;

public class Chunk<T> implements Iterable<ChunkObject<T>> {
    private static final Pool<ChunkObject> chunkObjectPool = new Pool<ChunkObject>() {
        @Override
        protected ChunkObject newObject() {
            return new ChunkObject<>();
        }
    };

    final Array<ChunkObject<T>> objects = new Array<>();
    final Rectangle bounds = new Rectangle();

    Chunk(int x, int y, float width, float height) {
        bounds.set(x * width, y * height, width, height);
    }

    void addObject(T object, float x, float y) {
        objects.add(chunkObjectPool.obtain().set(x, y, object));
    }

    void removeObject(T object) {
        Iterator<ChunkObject<T>> it = objects.iterator();
        while (it.hasNext()) {
            ChunkObject<T> chunkObject = it.next();
            if (chunkObject.object == object) {
                chunkObjectPool.free(chunkObject);
                it.remove();
                break;
            }
        }
    }

    @Override
    public Iterator<ChunkObject<T>> iterator() {
        return objects.iterator();
    }
}
