package synoptic.model;

import synoptic.model.interfaces.ITransition;

/**
 * An implementation of a transition.
 * 
 * @author Sigurd Schneider
 * @param <StateType>
 */
public class Transition<StateType> implements ITransition<StateType> {
    protected StateType source;
    protected StateType target;
    protected final String action;
    private int count = 0;

    /**
     * Create a new transition. The action will be interned.
     * 
     * @param source
     *            source node
     * @param target
     *            target node
     * @param action
     *            the label of the transition (will be interned)
     */
    public Transition(StateType source, StateType target, String action) {
        this.source = source;
        this.target = target;
        this.action = action.intern();
    }

    @Override
    public StateType getTarget() {
        return target;
    }

    @Override
    public StateType getSource() {
        return source;
    }

    @Override
    public String getRelation() {
        return action;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (action == null ? 0 : action.hashCode());
        result = prime * result + (source == null ? 0 : source.hashCode());
        result = prime * result + (target == null ? 0 : target.hashCode());
        return result;
    }

    // TODO make use of the fact that action is interned.
    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Transition<StateType> other = (Transition<StateType>) obj;
        if (action == null) {
            if (other.action != null) {
                return false;
            }
        } else if (!action.equals(other.action)) {
            return false;
        }
        if (source == null) {
            if (other.source != null) {
                return false;
            }
        } else if (!source.equals(other.source)) {
            return false;
        }
        if (target == null) {
            if (other.target != null) {
                return false;
            }
        } else if (!target.equals(other.target)) {
            return false;
        }
        return true;
    }

    @Override
    public void setSource(StateType source) {
        this.source = source;
    }

    @Override
    public void setTarget(StateType target) {
        this.target = target;
    }

    @Override
    public void addWeight(int count) {
        this.count += count;
    }

    @Override
    public int getWeight() {
        return count;
    }

    @Override
    public String toStringConcise() {
        return getRelation();
    }
}