package com.gordonfromblumberg.games.core.common.chunk;

public class ChunkManager<T> {
    private final Chunk<T>[][] chunks;
    private final float chunkWidth;
    private final float chunkHeight;
    private final float worldWidth;
    private final float worldHeight;
    private final int chunkGridWidth;
    private final int chunkGridHeight;

    public ChunkManager(float worldWidth, float worldHeight, float desiredChunkSize) {
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.chunkGridWidth = Math.round(worldWidth / desiredChunkSize);
        this.chunkGridHeight = Math.round(worldHeight / desiredChunkSize);
        this.chunkWidth = worldWidth / this.chunkGridWidth;
        this.chunkHeight = worldHeight / this.chunkGridHeight;

        this.chunks = new Chunk[this.chunkGridWidth][this.chunkGridHeight];
        for (int i = 0; i < chunkGridWidth; ++i) {
            for (int j = 0; j < chunkGridHeight; ++j) {
                this.chunks[i][j] = new Chunk<>(i, j, this.chunkWidth, this.chunkHeight);
            }
        }
    }

    public void addObject(T object, float x, float y) {
        getChunk(x, y).addObject(object, x, y);
    }

    public void removeObject(T object, float x, float y) {
        getChunk(x, y).removeObject(object);
    }

    private Chunk<T> getChunk(float x, float y) {
        int chunkX = (int) (x < 0 ? x / chunkWidth - 1 : x / chunkWidth);
        int chunkY = (int) (y < 0 ? y / chunkHeight - 1 : y / chunkHeight);
        return chunks[chunkX][chunkY];
    }
}
