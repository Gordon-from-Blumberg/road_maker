package com.gordonfromblumberg.games.core.common.graph;

import com.badlogic.gdx.utils.Array;

public interface Graph {
    default void getRelated(Node node, Array<Edge> out) {
        node.getRelated(out);
    }
}
