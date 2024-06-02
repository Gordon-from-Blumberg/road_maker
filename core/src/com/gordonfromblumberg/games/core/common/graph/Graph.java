package com.gordonfromblumberg.games.core.common.graph;

import com.badlogic.gdx.utils.Array;

public interface Graph {
    default void next(Node node, Array<Edge> out) {
        node.next(out);
    }
    default void prev(Node node, Array<Edge> out) {
        node.prev(out);
    }
}
