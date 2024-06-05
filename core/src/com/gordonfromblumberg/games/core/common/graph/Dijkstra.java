package com.gordonfromblumberg.games.core.common.graph;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pools;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

@SuppressWarnings("unchecked")
public class Dijkstra<T extends Node> {
    private static final Predicate<Node> falseCondition = node -> false;

    private final ObjectMap<T, Float> map = new ObjectMap<>();
    private final NodeComparator<T> nodeComparator = new NodeComparator<>(map);
    private final PriorityQueue<T> queue = new PriorityQueue<>(nodeComparator);

    private final PathRetriever<T> pathRetriever = new PathRetriever<>();
    private final TargetCondition<T> targetCondition = new TargetCondition<>();
    private final BiConsumer<Graph<T>, ObjectMap<T, Float>> nothing = (g, m) -> {};

    public void findPath(Graph<T> graph, T start, T target, Array<T> out) {
        algorithm(graph, start, map, pathRetriever.set(target, out), targetCondition.set(target));
    }

    public void markNodes(Graph<T> graph, T start, ObjectMap<T, Float> out) {
        algorithm(graph, start, out, nothing, (Predicate<T>) falseCondition);
    }

    void algorithm(Graph<T> graph, T start, ObjectMap<T, Float> map,
                   BiConsumer<Graph<T>, ObjectMap<T, Float>> action,
                   Predicate<T> breakCondition) {

        final Array<Edge<T>> neibs = Pools.obtain(Array.class);

        map.put(start, 0f);
        nodeComparator.setValueMap(map);
        queue.add(start);

        while (!queue.isEmpty()) {
            T node = queue.poll();
            if (breakCondition.test(node)) {
                action.accept(graph, map);
                break;
            }

            float nodeValue = map.get(node);
            neibs.clear();
            graph.next(node, neibs);

            for (Edge<T> edge : neibs) {
                T neib = edge.getNode();
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

    private void clear() {
        queue.clear();
        map.clear();
    }

    private static class NodeComparator<T extends Node> implements Comparator<T> {
        private ObjectMap<T, Float> valueMap;

        NodeComparator(ObjectMap<T, Float> valueMap) {
            this.valueMap = valueMap;
        }

        @Override
        public int compare(T n1, T n2) {
            float d = valueMap.get(n1) - valueMap.get(n2);
            if (d > 0) return 1;
            else if (d < 0) return -1;
            else return 0;
        }

        void setValueMap(ObjectMap<T, Float> valueMap) {
            this.valueMap = valueMap;
        }
    }

    private static class PathRetriever<T extends Node> implements BiConsumer<Graph<T>, ObjectMap<T, Float>> {
        T target;
        Array<T> out;

        @Override
        public void accept(Graph<T> graph, ObjectMap<T, Float> map) {
            Array<Edge<T>> edges = Pools.obtain(Array.class);
            out.add(target);

            T node = target;
            while (map.get(node) > 0) {
                graph.prev(node, edges);
                T n = null;
                float min = Float.POSITIVE_INFINITY;
                for (Edge<T> edge : edges) {
                    if (map.containsKey(edge.getNode())) {
                        float v = map.get(edge.getNode()) + edge.getWeight();
                        if (v < min) {
                            min = v;
                            n = edge.getNode();
                        }
                    }
                }

                edges.clear();
                out.add(n);
                node = n;
            }

            Pools.free(edges);
            out.reverse();
        }

        PathRetriever<T> set(T target, Array<T> out) {
            this.target = target;
            this.out = out;
            return this;
        }
    }

    private static class TargetCondition<T extends Node> implements Predicate<T> {
        private T target;

        @Override
        public boolean test(T t) {
            return t == target;
        }

        TargetCondition<T> set(T target) {
            this.target = target;
            return this;
        }
    }
}
