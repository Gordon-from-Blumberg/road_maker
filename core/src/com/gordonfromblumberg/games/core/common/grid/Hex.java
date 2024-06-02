package com.gordonfromblumberg.games.core.common.grid;

import com.badlogic.gdx.utils.Array;
import com.gordonfromblumberg.games.core.common.graph.Edge;
import com.gordonfromblumberg.games.core.common.graph.Node;
import com.gordonfromblumberg.games.core.common.utils.CollectionUtils;

public class Hex implements Node {
    public final int x, y;
    private Object object;
    final String[] tiles;
    final HexEdge[] edges = new HexEdge[6];

    Hex(int x, int y, int layerCount) {
        this.x = x;
        this.y = y;
        this.tiles = new String[layerCount];
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    @Override
    public void next(Array<Edge> out) {
        CollectionUtils.addNonNull(out, edges);
    }

    @Override
    public void prev(Array<Edge> out) {
        CollectionUtils.addNonNull(out, edges);
    }
}
