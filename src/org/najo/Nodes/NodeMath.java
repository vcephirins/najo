/**
 * 
 */
package org.najo.Nodes;

import org.najo.enums.TypeMath;
import org.najo.enums.TypeNode;
import org.najo.exceptions.NajoException;
import org.najo.values.Value;
import org.najo.values.ValueNull;
import org.najo.values.ValueString;

/**
 * NodeMath.java. <p>
 * @author  Vincent Cephirins
 * @version 1.0, 1 déc. 2012
 * <li>Creation</>
 */

public class NodeMath extends Node {

    private TypeMath typeMath; // typeMath du node

    /**
     * Constructeur.<p>
     * @param type
     * @param val
     * @param child
     */
    public NodeMath(TypeMath type, Node child) {
        super(TypeNode.TYPE_MATH, (String) null);
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
    public NodeMath(TypeMath type, final Node child1, final Node child2) {
        super(TypeNode.TYPE_MATH, null, child1, child2);
        this.typeMath = type;
        this.calculated = false;
    }

    public TypeMath getTypeMath() {
        return typeMath;
    }

    /*
     * (non-Javadoc)
     * @see org.najo.INode#expr()
     */
    @Override
    public Value expr() throws NajoException {
        Value result = value;

        switch (typeMath) {
        case PLUS:
        case MINUS:
        case MULTIPLE:
        case DIVISE:
        case EXPONENT:
        case MODULO:
            result = getChildren(0).getValue().exprMath(typeMath, getChildren(1).getValue());
            break;
        case MUNAIRE:
            result = getChildren(0).getValue().exprMath(typeMath, null);
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
            TypeMath typeMath = getTypeMath();
            if (value instanceof ValueString) return String.format("[%s] = \"%s\"", typeMath.toString(), value.toString());
            else
                if (value instanceof ValueNull) return String.format("[%s]", type.toString());
                else return String.format("[%s] = %s", typeMath.toString(), value.toString());
        }
        catch (NajoException e) {
            return e.getMessage();
        }
    }
}
