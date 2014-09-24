/**
 * 
 */
package org.najo.Nodes;

import java.util.ArrayList;


/**
 * ListNodes.java. <p>
 * @author  Vincent Cephirins
 * @version 1.0, 15 nov. 2010
 * <li>Creation</>
 */

public class ListNodes extends ArrayList<Node> {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private String name = "john doe";
    
    /**
     * Constructeur.<p>
     * @param name
     */
    public ListNodes(String name) {
        super();
        if (name != null) this.name = name;
    }
    
    /**
     * Constructeur.<p>
     * @param name
     */
    public ListNodes(String name, int initialCapacity) {
        super(initialCapacity);
        if (name != null) this.name = name;
    }
    
    /**
     * @return Returns the name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }
}
