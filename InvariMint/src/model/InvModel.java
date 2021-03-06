package model;

import java.util.ArrayList;
import java.util.List;

import synoptic.invariants.BinaryInvariant;
import synoptic.invariants.ITemporalInvariant;
import synoptic.invariants.KTailInvariant;
import synoptic.model.event.EventType;

/**
 * Extends the EncodedAutomaton class to encode a single ITemporalInvariant.
 * 
 */
public class InvModel extends EncodedAutomaton {

    // The invariant represented with this Automaton.
    private ITemporalInvariant inv;

    /**
     * Generates an EncodedAutomaton for the given invariant. Encodes the names
     * of both EventTypes composing the invariant and constructs the Automaton
     * by using those characters in a regex representing the invariant.
     */
    public InvModel(ITemporalInvariant invariant, EventTypeEncodings encodings) {
        super(encodings);

        this.inv = invariant;

        // Construct an encoded regex for the given invariant.
        String re = "";
        if (invariant instanceof BinaryInvariant) {
            BinaryInvariant invar = (BinaryInvariant) inv;

            char first = encodings.getEncoding(invar.getFirst());
            char second = encodings.getEncoding(invar.getSecond());
            re = invar.getRegex(first, second);
        } else if (invariant instanceof KTailInvariant) {
            KTailInvariant tail = ((KTailInvariant) inv);

            List<EventType> tailEvents = tail.getTailEvents();
            List<Character> tailEncodings = new ArrayList<Character>();
            for (EventType event : tailEvents) {
                tailEncodings.add(encodings.getEncoding(event));
            }

            List<EventType> followEvents = tail.getFollowEvents();
            List<Character> followEncodings = new ArrayList<Character>();
            for (EventType event : followEvents) {
                followEncodings.add(encodings.getEncoding(event));
            }

            re = KTailInvariant.getRegex(tailEncodings, followEncodings);
            // logger.fine("Intersecting model with re: " + re);
        }

        super.intersectWithRE(re, "Intersecting model with " + invariant);
    }

    /** Returns this model's invariant. */
    public ITemporalInvariant getInvariant() {
        return inv;
    }
}
