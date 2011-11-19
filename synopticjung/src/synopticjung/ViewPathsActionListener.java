package synopticjung;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import edu.uci.ics.jung.visualization.picking.PickedState;

import synoptic.model.EventNode;
import synoptic.model.Partition;
import synoptic.model.Transition;
import synoptic.model.interfaces.INode;
import synoptic.model.interfaces.ITransition;

/**
 * Responsible for responding to user selection of paths from the view-paths
 * window.
 */
public class ViewPathsActionListener implements ActionListener {
    JungGui jungGui;

    public ViewPathsActionListener(JungGui jungGui) {
        this.jungGui = jungGui;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        PickedState<INode<Partition>> pState = jungGui.vizViewer
                .getPickedVertexState();

        Set<INode<Partition>> pickedVertices = pState.getPicked();
        if (pickedVertices.isEmpty()) {
            JOptionPane
                    .showMessageDialog(jungGui.frame, "No vertices selected");
        } else {
            List<Set<Integer>> partitionIDs = new ArrayList<Set<Integer>>();
            Set<Integer> temp;
            for (INode<Partition> v : pickedVertices) {
                temp = new HashSet<Integer>();
                for (EventNode event : ((Partition) v).getEventNodes()) {
                    if (event.getTraceID() != 0) {
                        temp.add(event.getTraceID());
                    }
                }
                if (!temp.isEmpty()) {
                    partitionIDs.add(temp);
                }
            }
            if (partitionIDs.isEmpty()) {
                JOptionPane.showMessageDialog(jungGui.frame,
                        "No events observed in the selected vertices");
            } else {
                Set<Integer> intersectionOfIDs = partitionIDs.get(0);
                for (int i = 1; i < partitionIDs.size(); i++) {
                    intersectionOfIDs.retainAll(partitionIDs.get(i));
                }

                // Now intersectionOfIDs is a set intersection of the
                // traceIDs for the current selected vertices
                if (intersectionOfIDs.isEmpty()) {
                    JOptionPane.showMessageDialog(jungGui.frame,
                            "No traces observed through all selected vertices");
                } else {
                    JFrame optionsFrame = new JFrame("Possible traces");
                    JLabel message = new JLabel("Select path to view",
                            SwingConstants.CENTER);
                    JPanel panel = new JPanel();
                    final Map<Integer, Set<ITransition<Partition>>> paths = new HashMap<Integer, Set<ITransition<Partition>>>();
                    // System.out.println(intersectionOfIDs);
                    for (Partition p : jungGui.pGraph.getDummyInitialNodes()) {
                        for (EventNode event : p.getEventNodes()) {
                            for (Transition<EventNode> t : event
                                    .getTransitions()) {
                                int traceID = t.getTarget().getTraceID();
                                if (intersectionOfIDs.contains(traceID)) {
                                    Set<ITransition<Partition>> curPath = new HashSet<ITransition<Partition>>();
                                    ITransition<Partition> trans = p
                                            .getTransition(t.getTarget()
                                                    .getParent(), t
                                                    .getRelation());
                                    curPath.add(trans);
                                    traverse(t.getTarget(), curPath);
                                    paths.put(traceID, curPath);
                                }
                            }
                        }
                    }
                    ButtonGroup traceButtonGroup = new ButtonGroup();
                    for (final Integer trace : intersectionOfIDs) {
                        JRadioButton button = new JRadioButton("Trace ID "
                                + trace);
                        button.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent event) {
                                jungGui.displayPath(paths.get(trace));
                            }
                        });
                        panel.add(button);
                        traceButtonGroup.add(button);
                        optionsFrame.addWindowListener(new WindowAdapter() {
                            @Override
                            public void windowClosing(WindowEvent ev) {
                                jungGui.displayPath(null);
                            }
                        });
                        optionsFrame
                                .setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                        optionsFrame.add(message, BorderLayout.NORTH);
                        optionsFrame.add(panel, BorderLayout.SOUTH);
                        optionsFrame.pack();
                        optionsFrame.setVisible(true);
                    }
                }
            }
        }
    }

    void traverse(EventNode event, Set<ITransition<Partition>> path) {
        // Should be a single transition for totally ordered logs
        for (Transition<EventNode> transition : event.getTransitions()) {
            path.add(event.getParent().getTransition(
                    transition.getTarget().getParent(),
                    transition.getRelation()));
            traverse(transition.getTarget(), path);
        }
    }
}