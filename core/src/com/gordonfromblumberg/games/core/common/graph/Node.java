package com.gordonfromblumberg.games.core.common.graph;

import com.badlogic.gdx.utils.Array;

public interface Node {
    void next(Array<Edge> out);
    void prev(Array<Edge> out);
}
