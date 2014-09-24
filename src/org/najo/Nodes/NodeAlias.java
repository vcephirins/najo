/**
 * 
 */
package org.najo.Nodes;

import enums.TypeNode;

/**
 * NodeAlias.java. <p>
 * @author  Vincent Cephirins
 * @version 1.0, 15 déc. 2012
 * <li>Creation</>
 */

public class NodeAlias extends Node {
    ListNodes idents = new ListNodes("idents");

    /**
     * Constructeur.<p>
     * @param type
     * @param val
     * @param child
     */
    public NodeAlias(TypeNode type, String val, Node child) {
        super(type, val, child);
    }

    public void addIdent(Node ident) {
        idents.add(ident);
        ident.resetCalculated();
    }

    /* (non-Javadoc)
     * @see org.najo.Nodes.Node#resetCalculated()
     */
    @Override
    public void resetCalculated() {
        super.resetCalculated();
        
        // Force le calcul des référents (récursif)
        for (Node ident : idents) {
            ident.resetCalculated();
        }
    }
}
