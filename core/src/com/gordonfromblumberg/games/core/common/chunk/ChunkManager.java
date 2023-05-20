package com.gordonfromblumberg.games.core.common.chunk;

import com.badlogic.gdx.math.Rectangle;

public class ChunkManager<T> {
    private final Chunk<T>[][] chunks;
    private final float chunkWidth;
    private final float chunkHeight;
    private final Rectangle world = new Rectangle();
    private final int chunkGridWidth;
    private final int chunkGridHeight;

    public ChunkManager(float worldX, float worldY, float worldWidth, float worldHeight, float desiredChunkSize) {
        this.world.set(worldX, worldY, worldWidth, worldHeight);
        this.chunkGridWidth = Math.round(worldWidth / desiredChunkSize);
        this.chunkGridHeight = Math.round(worldHeight / desiredChunkSize);
        this.chunkWidth = worldWidth / this.chunkGridWidth;
        this.chunkHeight = worldHeight / this.chunkGridHeight;

        this.chunks = new Chunk[this.chunkGridWidth][this.chunkGridHeight];
        for (int i = 0; i < chunkGridWidth; ++i) {
            for (int j = 0; j < chunkGridHeight; ++j) {
                this.chunks[i][j] = new Chunk<>(i, j, this.chunkWidth, this.chunkHeight, this.world);
            }
        }
    }

    public void addObject(T object, float x, float y) {
        getChunk(x, y).addObject(object, x, y);
    }

    public void removeObject(T object, float x, float y) {
        getChunk(x, y).removeObject(object);
    }

    // todo remove and use iterator instead
    public Chunk<T>[][] getChunks() {
        return chunks;
    }

    private Chunk<T> getChunk(float x, float y) {
        int chunkX = (int) ((x - world.x) / chunkWidth);
        int chunkY = (int) ((y - world.y) / chunkHeight);
        return chunks[chunkX][chunkY];
    }
}
