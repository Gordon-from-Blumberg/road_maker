package com.gordonfromblumberg.games.core.common.graph;

public interface Edge {
    Node getNode();

    default float getWeight() {
        return 1f;
    }
}
