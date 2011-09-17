package synoptic.model;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import synoptic.algorithms.graph.TransitiveClosure;

public class ChainsTraceGraph extends TraceGraph<StringEventType> {
    static Event initEvent = Event.newInitialStringEvent();
    static Event termEvent = Event.newTerminalStringEvent();

    /**
     * Maintains a map of trace id to the set of initial nodes in the trace.
     */
    private final Map<Integer, EventNode> traceIdToInitNodes = new LinkedHashMap<Integer, EventNode>();

    public ChainsTraceGraph(Collection<EventNode> nodes) {
        super(nodes);
    }

    public ChainsTraceGraph() {
        super();
    }

    public void tagTerminal(EventNode terminalNode, String relation) {
        // add(terminalNode);
        createIfNotExistsDummyTerminalNode(termEvent, relation);
        super.tagTerminal(terminalNode, relation);
    }

    public void tagInitial(EventNode initialNode, String relation) {
        // add(initialNode);
        createIfNotExistsDummyInitialNode(initEvent, relation);
        super.tagInitial(initialNode, relation);
        traceIdToInitNodes.put(initialNode.getTraceID(), initialNode);
    }

    /**
     * Returns the number of trace ids that are immediately reachable from the
     * initNode -- this is useful for PO traces since the number of transitions
     * from the initial node is not necessarily the number of traces since it
     * might be connected to two nodes in the same trace (that were concurrent
     * at start).
     */
    public int getNumTraces() {
        return traceIdToInitNodes.size();
    }

    /**
     * Transitive closure construction for a ChainsTraceGraph is simple: iterate
     * through each chain independently and add all successors of a node in a
     * chain to it's transitive closure set. <br/>
     * <br/>
     * NOTE: a major assumption of this code is that although there are multiple
     * relations, the graph remains a linear chain.
     */
    public TransitiveClosure getTransitiveClosure(String relation) {
        TransitiveClosure transClosure = new TransitiveClosure(relation);
        List<EventNode> prevNodes = new LinkedList<EventNode>();
        for (EventNode firstNode : traceIdToInitNodes.values()) {
            EventNode curNode = firstNode;

            while (!curNode.isTerminal()) {
                while (curNode.getTransitions(relation).size() != 0) {
                    for (EventNode prevNode : prevNodes) {
                        transClosure.addReachable(prevNode, curNode);
                    }
                    prevNodes.add(curNode);
                    curNode = curNode.getTransitions(relation).get(0)
                            .getTarget();
                }

                if (!curNode.isTerminal()) {
                    for (EventNode prevNode : prevNodes) {
                        transClosure.addReachable(prevNode, curNode);
                    }
                }

                prevNodes.clear();

                if (!curNode.isTerminal()) {
                    curNode = curNode.getTransitions().get(0).getTarget();
                }
            }
        }
        return transClosure;
    }

    // Used by tests only (so that DAGWalking invariant miner can operate on
    // ChainsTraceGraph)
    public Map<Integer, Set<EventNode>> getTraceIdToInitNodes() {
        Map<Integer, Set<EventNode>> map = new LinkedHashMap<Integer, Set<EventNode>>();
        for (Integer k : traceIdToInitNodes.keySet()) {
            Set<EventNode> set = new LinkedHashSet<EventNode>();
            set.add(traceIdToInitNodes.get(k));
            map.put(k, set);
        }
        return map;
    }

}