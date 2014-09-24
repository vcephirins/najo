/**
 * 
 */
package org.najo.Nodes;

import org.free.toolboxz.date.DateException;
import org.najo.NajoException;
import org.najo.values.Value;
import org.najo.values.ValueBool;
import org.najo.values.ValueByte;
import org.najo.values.ValueChar;
import org.najo.values.ValueDate;
import org.najo.values.ValueDouble;
import org.najo.values.ValueError;
import org.najo.values.ValueFloat;
import org.najo.values.ValueHexa;
import org.najo.values.ValueInteger;
import org.najo.values.ValueLong;
import org.najo.values.ValueNull;
import org.najo.values.ValueOnOff;
import org.najo.values.ValueString;

import enums.TypeNode;
import enums.TypeValue;

/**
 * NodeValue.java. <p>
 * @author  Vincent Cephirins
 * @version 1.0, 1 déc. 2012
 * <li>Creation</>
 */

public class NodeValue extends Node {

    private TypeValue typeValue; // typeValue du node

    /**
     * Constructeur.<p>
     * @param typeValue
     * @param val
     */
    public NodeValue(TypeValue type, String val) {
        super(TypeNode.TYPE_VALUE, val);
        this.typeValue = type;
        this.calculated = true;

        try {
            switch (type) {
            case ERROR:
                this.value = new ValueError(val);
                break;
            case ON_OFF:
                this.value = new ValueOnOff(val);
                break;
            case BOOL:
                this.value = new ValueBool(val);
                break;
            case SHORT:
            case INTEGER:
                this.value = new ValueInteger(val);
                break;
            case LONG:
                this.value = new ValueLong(val);
                break;
            case FLOAT:
                this.value = new ValueFloat(val);
                break;
            case DOUBLE:
                this.value = new ValueDouble(val);
                break;
            case STRING:
                this.value = new ValueString(val);
                break;
            case HEXA:
                this.value = new ValueHexa(val);
                break;
            case BYTE:
                this.value = new ValueByte(val);
                break;
            case CHAR:
                this.value = new ValueChar(val);
                break;
            case DATE:
                try {
                    this.value = new ValueDate(val);
                }
                catch (DateException e) {
                    throw new NajoException(e);
                }
                break;
            case NULL:
                this.value = Value.VALUE_NULL;
                break;
            default:
                break;
            }
        }
        catch (NajoException ne) {
            ne.printMessages();
        }
    }

    /**
     * Constructeur.<p>
     * @param typeValue
     * @param val
     * @param child
     */
    public NodeValue(TypeValue type, String val, Node child) {
        this(type, val);
        this.child = ((Node) child);
        this.child.setParent(this);
    }
    
    public TypeValue getTypeValue() {
        return typeValue;
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
            TypeValue typeValue = value.getType();
            if (value instanceof ValueString) return String.format("[%s] = \"%s\"", typeValue.toString(), value.toString());
            else
                if (value instanceof ValueNull) return String.format("[%s]", type.toString());
                else return String.format("[%s] = %s", typeValue.toString(), value.toString());
        }
        catch (NajoException e) {
            return e.getMessage();
        }
    }
}
