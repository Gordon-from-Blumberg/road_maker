package com.gordonfromblumberg.games.core.common.graph;

import com.badlogic.gdx.utils.Array;

public interface Node {
    void getRelated(Array<Edge> out);
}
