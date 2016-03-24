/**
 * 
 */
package org.najo.Nodes;

import org.najo.exceptions.NajoException;
import org.najo.values.Value;
import org.najo.values.ValueNull;
import org.najo.values.ValueString;

import enums.TypeCond;
import enums.TypeNode;

/**
 * NodeCond.java. <p>
 * @author  Vincent Cephirins
 * @version 1.0, 16 déc. 2012
 * <li>Creation</>
 */

public class NodeCond extends Node {

    private TypeCond typeCond; // type de condition

    /**
     * Constructeur.<p>
     * @param type
     * @param val
     */
    public NodeCond(TypeCond type, Node child) {
        super(TypeNode.TYPE_COND, (String) null);
        this.child = ((Node) child);
        this.child.setParent(this);
    }

    /**
     * Constructeur.
     * <p>
     * 
     * @param type
     * @param name
     * @throws NajoException
     */
    public NodeCond(TypeCond type, final Node child1, final Node child2) {
        super(TypeNode.TYPE_COND, null, child1, child2);
        this.typeCond = type;
        this.calculated = false;
    }

    public TypeCond getTypeCond() {
        return typeCond;
    }

    /*
     * (non-Javadoc)
     * @see org.najo.INode#expr()
     */
    @Override
    public Value expr() throws NajoException {
        Value result = value;

        switch (typeCond) {
        case AND:
        case EQEQ:
        case GE:
        case LE:
        case LESS:
        case NE:
        case OR:
        case SUP:
            result = getChildren(0).getValue().exprCond(typeCond, getChildren(1).getValue());
            break;
        case NOT:
            result = getChild().getValue().exprCond(typeCond, null);
            break;
//        default:
//         throw new NajoException("exception.nodeType.notFound", type);
        }

        return result;
    }

    /**
     * Retourne les informations du node.
     */
    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        try {
            value = getValue(); // Declenche le calcul de la valeur
            if (value instanceof ValueString) return String.format("[%s] = \"%s\"", typeCond.toString(), value.toString());
            else
                if (value instanceof ValueNull) return String.format("[%s]", type.toString());
                else return String.format("[%s] = %s", typeCond.toString(), value.toString());
        }
        catch (NajoException e) {
            return e.getMessage();
        }
    }
}
