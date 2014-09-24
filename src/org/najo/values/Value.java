/**
 * 
 */
package org.najo.values;

import org.free.toolboxz.date.JulianDate;
import org.free.toolboxz.enumPatterns.Patterns;
import org.free.toolboxz.exceptions.Messages;
import org.free.toolboxz.exceptions.NotFoundException;
import org.najo.NajoException;

import enums.TypeCond;
import enums.TypeValue;

/**
 * Value.java.
 * <p>
 * 
 * @author Vincent Cephirins
 * @version 1.0, 15 nov. 2010 <li>Creation</>
 */

public abstract class Value implements IValue, Comparable<Value> {
    TypeValue type = null; // value type
    long length = 0; // length of the value
    long dim = 0; // Dimension of the value (array)

    // Creation des constantes generales
    public static final ValueNull VALUE_NULL = new ValueNull();

    /*
     * (non-Javadoc)
     * @see org.najo.IValue#getType()
     */
    @Override
    public TypeValue getType() {
        return type;
    }

    /*
     * (non-Javadoc)
     * @see org.najo.IValue#getLength()
     */
    @Override
    public long getLength() {
        return length;
    }

    /*
     * (non-Javadoc)
     * @see org.najo.IValue#getDim()
     */
    @Override
    public long getDim() {
        return dim;
    }

    public static String getFmtFortranConv(String value) throws NajoException {

        if (value == null || value.length() == 0) return null;

        String precision;
        StringBuffer buffer = new StringBuffer();

        try {
            /* Analyse de la chaine */
            switch (value.charAt(0)) {
            case 'a':
            case 'A':
                /* Recupere la precision */
                precision = Patterns.INTEGER.find(value);
                buffer.append(String.format("%%%s.%1$ss", precision));
                break;
            case 'i':
            case 'I':
                /* Recupere la precision */
                precision = Patterns.INTEGER.find(value);
                buffer.append(String.format("%%%sd", precision));
                break;
            case 'e':
            case 'f':
            case 'g':
            case 'E':
            case 'F':
            case 'G':
                /* Recupere la precision */
                // sscanf(value, "%c%d.%d", &type, &longueur, &precision);
                // snprintf(buffer, kiBUFFER, "%%%d.%d%c",
                // longueur, precision, (type == 'F')? 'f' : type);
                char c = value.charAt(0);
                precision = Patterns.DOUBLE.find(value);
                buffer.append(String.format("%%%s%c", precision, (c == 'F')? 'f' : c));
                break;
            default:
                throw new NajoException("exception.value.format", value);
            }

        }
        catch (NumberFormatException e) {
            throw new NajoException(e, "exception.value.format", value);
        }
        catch (NotFoundException e) {
            throw new NajoException("exception.value.format", value);
        }

        return (buffer.toString());
    }

    Value exprCond(TypeCond cond, Boolean thisval, Boolean val, TypeValue type) throws NajoException {
        Value result = Value.VALUE_NULL;
        
        switch (cond) {
        case AND:
            result = new ValueBool(thisval && val);
            break;
        case OR:
            result = new ValueBool(thisval || val);
            break;
        case EQEQ:
            result = new ValueBool(thisval == val);
            break;
        case NE:
            result = new ValueBool(thisval != val);
            break;
        case NOT:
            result = new ValueBool(!thisval);
            break;
        case GE:
        case SUP:
        case LE:
        case LESS:
            String mess = Messages.getInstance().getMessage("exception.value.operator", cond.toString(), this.type,
                type);
            result = new ValueError(mess);
            break;
        }

        return result;
    }

    Value exprCond(TypeCond cond, Byte thisval, Byte val, TypeValue type) throws NajoException {
        Value result = Value.VALUE_NULL;
        
        switch (cond) {
        case EQEQ:
            result = new ValueBool(thisval == val);
            break;
        case NE:
            result = new ValueBool(thisval != val);
            break;
        case GE:
            result = new ValueBool(thisval >= val);
            break;
        case SUP:
            result = new ValueBool(thisval > val);
            break;
        case LE:
            result = new ValueBool(thisval <= val);
            break;
        case LESS:
            result = new ValueBool(thisval < val);
            break;
        case AND:
        case OR:
        case NOT:
            String mess = Messages.getInstance().getMessage("exception.value.operator", cond.toString(), this.type,
                type);
            result = new ValueError(mess);
            break;
        }

        return result;
    }

    Value exprCond(TypeCond cond, Integer thisval, Integer val, TypeValue type) throws NajoException {
        Value result = Value.VALUE_NULL;
        
        switch (cond) {
        case EQEQ:
            result = new ValueBool(thisval == val);
            break;
        case NE:
            result = new ValueBool(thisval != val);
            break;
        case GE:
            result = new ValueBool(thisval >= val);
            break;
        case SUP:
            result = new ValueBool(thisval > val);
            break;
        case LE:
            result = new ValueBool(thisval <= val);
            break;
        case LESS:
            result = new ValueBool(thisval < val);
            break;
        case AND:
        case OR:
        case NOT:
            String mess = Messages.getInstance().getMessage("exception.value.operator", cond.toString(), this.type,
                type);
            result = new ValueError(mess);
            break;
        }

        return result;
    }

    Value exprCond(TypeCond cond, Long thisval, Long val, TypeValue type) throws NajoException {
        Value result = Value.VALUE_NULL;
        
        switch (cond) {
        case EQEQ:
            result = new ValueBool(thisval == val);
            break;
        case NE:
            result = new ValueBool(thisval != val);
            break;
        case GE:
            result = new ValueBool(thisval >= val);
            break;
        case SUP:
            result = new ValueBool(thisval > val);
            break;
        case LE:
            result = new ValueBool(thisval <= val);
            break;
        case LESS:
            result = new ValueBool(thisval < val);
            break;
        case AND:
        case OR:
        case NOT:
            String mess = Messages.getInstance().getMessage("exception.value.operator", cond.toString(), this.type,
                type);
            result = new ValueError(mess);
            break;
        }

        return result;
    }

    Value exprCond(TypeCond cond, Float thisval, Float val, TypeValue type) throws NajoException {
        Value result = Value.VALUE_NULL;
        
        switch (cond) {
        case EQEQ:
            result = new ValueBool(thisval == val);
            break;
        case NE:
            result = new ValueBool(thisval != val);
            break;
        case GE:
            result = new ValueBool(thisval >= val);
            break;
        case SUP:
            result = new ValueBool(thisval > val);
            break;
        case LE:
            result = new ValueBool(thisval <= val);
            break;
        case LESS:
            result = new ValueBool(thisval < val);
            break;
        case AND:
        case OR:
        case NOT:
            String mess = Messages.getInstance().getMessage("exception.value.operator", cond.toString(), this.type,
                type);
            result = new ValueError(mess);
            break;
        }

        return result;
    }

    Value exprCond(TypeCond cond, Double thisval, Double val, TypeValue type) throws NajoException {
        Value result = Value.VALUE_NULL;
        
        switch (cond) {
        case EQEQ:
            result = new ValueBool(thisval == val);
            break;
        case NE:
            result = new ValueBool(thisval != val);
            break;
        case GE:
            result = new ValueBool(thisval >= val);
            break;
        case SUP:
            result = new ValueBool(thisval > val);
            break;
        case LE:
            result = new ValueBool(thisval <= val);
            break;
        case LESS:
            result = new ValueBool(thisval < val);
            break;
        case AND:
        case OR:
        case NOT:
            String mess = Messages.getInstance().getMessage("exception.value.operator", cond.toString(), this.type,
                type);
            result = new ValueError(mess);
            break;
        }

        return result;
    }

    Value exprCond(TypeCond cond, String thisval, String val, TypeValue type) throws NajoException {
        Value result = Value.VALUE_NULL;
        
        switch (cond) {
        case EQEQ:
            result = new ValueBool(thisval.equals(val));
            break;
        case NE:
            result = new ValueBool(!thisval.equals(val));
            break;
        case GE:
            result = new ValueBool((thisval.compareTo(val) >= 0)? true : false);
            break;
        case SUP:
            result = new ValueBool((thisval.compareTo(val) > 0)? true : false);
            break;
        case LE:
            result = new ValueBool((thisval.compareTo(val) <= 0)? true : false);
            break;
        case LESS:
            result = new ValueBool((thisval.compareTo(val) < 0)? true : false);
            break;
        case AND:
        case OR:
        case NOT:
            String mess = Messages.getInstance().getMessage("exception.value.operator", cond.toString(), this.type,
                type);
            result = new ValueError(mess);
            break;
        }

        return result;
    }

    Value exprCond(TypeCond cond, JulianDate thisval, JulianDate val, TypeValue type) throws NajoException {
        Value result = Value.VALUE_NULL;
        
        switch (cond) {
        case EQEQ:
            result = new ValueBool(thisval == val);
            break;
        case NE:
            result = new ValueBool(thisval != val);
            break;
//        case GE:
//            result = new ValueBool(thisval >= val);
//            break;
//        case SUP:
//            result = new ValueBool(thisval > val);
//            break;
//        case LE:
//            result = new ValueBool(thisval <= val);
//            break;
//        case LESS:
//            result = new ValueBool(thisval < val);
//            break;
        case AND:
        case OR:
        case NOT:
            String mess = Messages.getInstance().getMessage("exception.value.operator", cond.toString(), this.type,
                type);
            result = new ValueError(mess);
            break;
        }

        return result;
    }
}
