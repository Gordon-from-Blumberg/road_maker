package com.gordonfromblumberg.games.core.common.graph;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pools;

import java.util.PriorityQueue;

public class Dijkstra {
    private final ObjectMap<Node, Float> map = new ObjectMap<>();
    private final PriorityQueue<Node> queue = new PriorityQueue<>((n1, n2) -> {
        float d = map.get(n1) - map.get(n2);
        if (d > 0) return 1;
        else if (d < 0) return -1;
        else return 0;
    });

    @SuppressWarnings("unchecked")
    public void findPath(Graph graph, Node start, Node target, Array<Node> out) {
        final Array<Edge> neibs = (Array<Edge>) Pools.obtain(Array.class);

        map.put(start, 0f);
        queue.add(start);
        while (!queue.isEmpty()) {
            Node node = queue.poll();
            if (node == target) {
                retrievePath(target, out);
                break;
            }

            float nodeValue = map.get(node);
            neibs.clear();
            graph.next(node, neibs);

            for (Edge edge : neibs) {
                Node neib = edge.getNode();
                float neibValue = nodeValue + edge.getWeight();
                if (!map.containsKey(neib)) {
                    map.put(neib, neibValue);
                    queue.add(neib);
                } else if (map.get(neib) > neibValue) {
                    map.put(neib, neibValue);
                    queue.remove(neib);
                    queue.add(neib);
                }
            }
        }

        neibs.clear();
        Pools.free(neibs);
        clear();
    }

    @SuppressWarnings("unchecked")
    private void retrievePath(Node target, Array<Node> out) {
        Array<Edge> edges = Pools.obtain(Array.class);
        out.add(target);

        Node node = target;
        while (map.get(node) > 0) {
            node.prev(edges);
            Node n = null;
            float min = Float.POSITIVE_INFINITY;
            for (Edge edge : edges) {
                float v = map.get(edge.getNode());
                if (v < min) {
                    min = v;
                    n = edge.getNode();
                }
            }

            edges.clear();
            out.add(n);
            node = n;
        }

        Pools.free(edges);
    }

    private void clear() {
        queue.clear();
        map.clear();
    }
}
