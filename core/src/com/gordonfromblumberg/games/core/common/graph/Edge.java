package com.gordonfromblumberg.games.core.common.graph;

public interface Edge {
    Node getNode();

    default int getWeight() {
        return 1;
    }
}
