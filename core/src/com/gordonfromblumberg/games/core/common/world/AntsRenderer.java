package com.gordonfromblumberg.games.core.common.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.gordonfromblumberg.games.core.common.grid.Hex;
import com.gordonfromblumberg.games.core.common.grid.HexGrid;

public class AntsRenderer implements AlgorithmRenderer {
    private static final Color ANT_COLOR = new Color(Color.RED);
    private static final Array<Hex> ants = new Array<>();

    @Override
    public void render(Algorithm algorithm, HexGrid grid, ShapeRenderer shapeRenderer) {
        final AntsAlgorithm antsAlgorithm = (AntsAlgorithm) algorithm;

        shapeRenderer.setColor(ANT_COLOR);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        ants.clear();
        antsAlgorithm.getAnts(ants);
        float radius = grid.getHexWidth() * 0.25f;
        for (Hex antHex : ants) {
            shapeRenderer.circle(grid.getWorldX(antHex), grid.getWorldY(antHex), radius);
        }
        shapeRenderer.end();
    }
}
