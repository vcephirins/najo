/**
 * 
 */
package org.najo.Nodes;

import java.util.Collection;
import java.util.TreeSet;

import org.najo.enums.TypeNode;

/**
 * IndexNode.java. <p>
 * @author  Vincent Cephirins
 * @version 1.0, 13 déc. 2012
 * <li>Creation</>
 */

public class IndexNode extends TreeSet<Node> {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    String id;
    Node testNode = new Node(TypeNode.NULL, "");

    /**
     * Constructeur.<p>
     * @param id
     */
    public IndexNode(String id) {
        super();
        this.id = id;
    }

    /**
     * Constructeur.<p>
     * @param id
     */
    public IndexNode(String id, Collection <? extends Node> c) {
        super();
        this.id = id;
        this.addAll(c);
    }

    public Node search(String name) {
        testNode.setName(name);
        Node result = ceiling(testNode);
        return (result != null && result.equals(testNode))? result : null;
    }
}
