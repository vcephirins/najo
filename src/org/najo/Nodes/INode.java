/**
 * 
 */
package org.najo.Nodes;

import org.najo.enums.TypeNode;
import org.najo.exceptions.NajoException;
import org.najo.values.Value;

/**
 * INode.java. <p>
 * @author  Vincent Cephirins
 * @version 1.0, 3 nov. 2012
 * <li>Creation</>
 */

public interface INode {

    // Creation des constantes generales
    public static final NodeNull NODE_NULL = new NodeNull();

    public abstract void setParent(Node parent);

    public abstract void resetCalculated();

    public abstract void setValue(String val) throws NajoException;

    public abstract void setValue(Value val) throws NajoException;

    /**
     * @return Returns the value.
     * @throws NajoException 
     */
    public abstract Value getValue() throws NajoException;

    /**
     * @return Returns the id.
     */
    public abstract long getId();

    /**
     * @return Returns the type.
     */
    public abstract TypeNode getType();

    /**
     * @return Returns the name.
     */
    public abstract String getName();

    /**
     * @return Returns the children.
     */
    public abstract ListNodes getChildren();

    /**
     * Returns one child from children by index.
     * @param pos index of child
     * @return Returns child.
     */
    public abstract Node getChildren(int pos);

    /**
     * @return Returns the child.
     */
    public abstract Node getChild();

    public abstract void display(int level);

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public abstract String toString();

    /**
     * @return value string.
     */
    public abstract String printValue();

    /**
     * @return formatted value string. 
     */
    public abstract String printValue(String format);
    
    /**
     * @return value after to calculate expression.
     * @throws NajoException
     */
    public abstract Value expr() throws NajoException;

}
