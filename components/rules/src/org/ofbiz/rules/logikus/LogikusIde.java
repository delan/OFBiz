package org.ofbiz.rules.logikus;


// import com.sun.java.swing.*;
// import com.sun.java.swing.border.*;
import javax.swing.*;
import java.awt.*;
import org.ofbiz.rules.utensil.*;


/**
 * <p><b>Title:</b> Logikus IDE
 * <p><b>Description:</b> None
 * <p>Copyright (c) 1999 Steven J. Metsker.
 * <p>Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
 *
 * <p>Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *
 * <p>The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 * <p>THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * <br>
 * <p>This class provides an interactive development environment
 * for Logikus.
 * <p>
 * This class contains just the Swing components, and
 * delegates responsibility for the interaction of these
 * components to a LogikusMediator object.
 *
 * @author Steven J. Metsker
 * @version 1.0
 */

public class LogikusIde {
    protected LogikusMediator mediator;

    protected JTextArea programArea;
    protected JTextArea resultsArea;
    protected JTextArea queryArea;

    protected JButton proveNextButton;
    protected JButton proveRestButton;
    protected JButton haltButton;
    protected JButton clearProgramButton;
    protected JButton clearResultsButton;

    /**
     * Creates and returns the box that contains all of the IDE's
     * buttons.
     */
    protected Box buttonBox() {
        Box b = Box.createHorizontalBox();

        b.add(proveButtonPanel());
        b.add(Box.createHorizontalGlue());
        b.add(clearButtonPanel());
        return b;
    }

    /**
     * Creates and returns the panel that contains the IDE's
     * clear buttons.
     */
    protected JPanel clearButtonPanel() {
        JPanel p = new JPanel();

        p.setBorder(SwingUtensil.ideTitledBorder("Clear"));
        p.add(clearProgramButton());
        p.add(clearResultsButton());
        return p;
    }

    /**
     * The button that clears the results area.
     */
    protected JButton clearProgramButton() {
        if (clearProgramButton == null) {
            clearProgramButton = new JButton("Program");
            clearProgramButton.addActionListener(mediator());
            clearProgramButton.setFont(SwingUtensil.ideFont());
        }
        return clearProgramButton;
    }

    /**
     * The button that clears the results area.
     */
    protected JButton clearResultsButton() {
        if (clearResultsButton == null) {
            clearResultsButton = new JButton("Results");
            clearResultsButton.addActionListener(mediator());
            clearResultsButton.setFont(SwingUtensil.ideFont());
        }
        return clearResultsButton;
    }

    /**
     * The button that halts the proof thread.
     */
    protected JButton haltButton() {
        if (haltButton == null) {
            haltButton = new JButton("Halt");
            haltButton.setEnabled(false);
            haltButton.addActionListener(mediator());
            haltButton.setFont(SwingUtensil.ideFont());
        }
        return haltButton;
    }

    /**
     * Launch a Logikus interactive development environment.
     */
    public static void main(String[] args) {
        SwingUtensil.launch(new LogikusIde().mainPanel(), "Logikus");
    }

    /**
     * Builds and returns the panel that contains all the
     * components of the IDE.
     */
    protected JPanel mainPanel() {
        JPanel p = SwingUtensil.textPanel(
                "Program",
                programArea(),
                new Dimension(600, 300),
                new Dimension(600, 150));

        JPanel q = SwingUtensil.textPanel(
                "Query",
                queryArea(),
                new Dimension(600, 60),
                new Dimension(600, 60));

        JPanel r = SwingUtensil.textPanel(
                "Results",
                resultsArea(),
                new Dimension(600, 150),
                new Dimension(600, 75));

        JSplitPane inner = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT, false, q, r);
        JSplitPane outer = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT, false, p, inner);

        inner.setDividerSize(3);
        outer.setDividerSize(3);

        JPanel whole = new JPanel();

        whole.setLayout(new BorderLayout());
        whole.add(outer, "Center");
        whole.add(buttonBox(), "South");
        return whole;
    }

    /**
     * The object that controls or "mediates" the interaction
     * of this development environment's Swing components.
     */
    protected LogikusMediator mediator() {
        if (mediator == null) {
            mediator = new LogikusMediator();
            mediator.initialize(
                proveNextButton(),
                proveRestButton(),
                haltButton(),
                clearProgramButton(),
                clearResultsButton(),
                programArea(),
                resultsArea(),
                queryArea());
        }
        return mediator;
    }

    /**
     * The program text area.
     */
    protected JTextArea programArea() {
        if (programArea == null) {
            programArea = SwingUtensil.ideTextArea();
        }
        return programArea;
    }

    /**
     * Creates and returns the panel that contains the IDE's
     * proof buttons.
     */
    protected JPanel proveButtonPanel() {
        JPanel p = new JPanel();

        p.setBorder(SwingUtensil.ideTitledBorder("Prove"));
        p.add(proveNextButton());
        p.add(proveRestButton());
        p.add(haltButton());
        return p;
    }

    /**
     * The button that starts the proof thread.
     */
    protected JButton proveNextButton() {
        if (proveNextButton == null) {
            proveNextButton = new JButton("Next");
            proveNextButton.addActionListener(mediator());
            proveNextButton.setFont(SwingUtensil.ideFont());
        }
        return proveNextButton;
    }

    /**
     * The button that starts the proof thread, asking it to
     * find all remaining proofs.
     */
    protected JButton proveRestButton() {
        if (proveRestButton == null) {
            proveRestButton = new JButton("Rest");
            proveRestButton.addActionListener(mediator());
            proveRestButton.setFont(SwingUtensil.ideFont());
        }
        return proveRestButton;
    }

    /**
     * The query text area.
     */
    protected JTextArea queryArea() {
        if (queryArea == null) {
            queryArea = SwingUtensil.ideTextArea();
        }
        return queryArea;
    }

    /**
     * The results text area.
     */
    protected JTextArea resultsArea() {
        if (resultsArea == null) {
            resultsArea = SwingUtensil.ideTextArea();
        }
        return resultsArea;
    }
}
