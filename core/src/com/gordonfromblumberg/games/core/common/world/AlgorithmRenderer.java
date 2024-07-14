package com.gordonfromblumberg.games.core.common.world;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.gordonfromblumberg.games.core.common.grid.HexGrid;

public interface AlgorithmRenderer {
    void render(Algorithm algorithm, HexGrid grid, ShapeRenderer shapeRenderer);
}
