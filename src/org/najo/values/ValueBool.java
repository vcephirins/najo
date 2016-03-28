/**
 * 
 */
package org.najo.values;

import org.free.toolboxz.date.JulianDate;
import org.najo.enums.TypeCond;
import org.najo.enums.TypeMath;
import org.najo.enums.TypeNode;
import org.najo.enums.TypeValue;
import org.najo.exceptions.NajoException;
import org.najo.exceptions.NajoMessages;

/**
 * ValueBool.java. <p>
 * @author  Vincent Cephirins
 * @version 1.0, 1 d�c. 2010
 * <li>Creation</>
 */

public class ValueBool extends Value {

    protected Boolean value;       // Value

    /**
     * Constructeur.<p>
     * @param val
     * @throws NajoException
     */
    public ValueBool(Boolean val) throws NajoException {
        setValue(val);
    }

    /**
     * Constructeur.<p>
     * @param val
     * @throws NajoException
     */
    public ValueBool(String val) throws NajoException {
        setValue(val);
    }

    /**
     * Constructeur.<p>
     * @param val
     * @throws NajoException
     */
    public ValueBool(Value val) throws NajoException {
        setValue(val);
    }

    public void setValue(String val) throws NajoException {
        setValue((val == null)? null : new Boolean(val));
    }
    
    public void setValue(Value val) throws NajoException {
        /* Conversion into internal type */
        setValue(val.toBool());
    }
    
    public void setValue(Boolean val) throws NajoException {
        value = val;
        this.type = TypeValue.BOOL;
        this.dim = 1;
        this.length = 1;
    }
    
    /**
     * Return Boolean value.
     */
    @Override
    public Boolean getVal() {
        return value;
    }

    /* (non-Javadoc)
     * @see org.najo.Value#toOnOff()
     */
    @Override
    public Boolean toOnOff() {
        return value;
    }

    /* (non-Javadoc)
     * @see org.najo.Value#toBool()
     */
    @Override
    public Boolean toBool() {
        return value;
    }

    /* (non-Javadoc)
     * @see org.najo.Value#toByte()
     */
    @Override
    public Byte toByte() {
        if (value == null) return null;
        return (byte )(value? 1 : 0);
    }

    /* (non-Javadoc)
     * @see org.najo.Value#toShort()
     */
    @Override
    public Integer toShort() {
        if (value == null) return null;
        return (int )(value? 1 : 0);
    }

    /* (non-Javadoc)
     * @see org.najo.Value#toInteger()
     */
    @Override
    public Integer toInteger() {
        if (value == null) return null;
        return (value? 1 : 0);
    }

    /* (non-Javadoc)
     * @see org.najo.Value#toLong()
     */
    @Override
    public Long toLong() {
        if (value == null) return null;
        return (value? 1L : 0L);
    }

    /* (non-Javadoc)
     * @see org.najo.Value#toFloat()
     */
    @Override
    public Float toFloat() {
        if (value == null) return null;
        return (value? 1.0f : 0.0f);
    }

    /* (non-Javadoc)
     * @see org.najo.Value#toDouble()
     */
    @Override
    public Double toDouble() {
        if (value == null) return null;
        return (value? 1.0 : 0.0);
    }

    /* (non-Javadoc)
     * @see org.najo.Value#toChar()
     */
    @Override
    public Character toChar() {
        if (value == null) return null;
        return (value? '1' : '0');
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return ((value == null)? Value.VALUE_NULL.toString() : (value)? "TRUE" : "FALSE");
    }

     /* (non-Javadoc)
     * @see org.najo.values.IValue#toDate()
     */
    @Override
    public JulianDate toDate() throws NajoException {
        if (value == null) return null;
        throw new NajoException("exception.value.mismatch", this.type, TypeNode.TYPE_DATE);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString(String)
     */
    @Override
    public String toString(String format) {
        if (value == null) return Value.VALUE_NULL.toString();
        return String.format(format, (value)? "TRUE" : "FALSE");
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(Value val) {
        return (value.compareTo(val.toBool()));
    }

    /*
     * (non-Javadoc)
     * @see org.najo.values.IValue#exprCond(enums.TypeCond, org.najo.values.Value)
     */
    @Override
    public Value exprCond(TypeCond cond, Value val) throws NajoException {
        
        if (val.getType() == TypeValue.ERROR) return val;
        Value result = Value.VALUE_NULL;

            switch (val.getType()) {
            case BOOL:
            case ON_OFF:
            case BYTE:
            case SHORT:
            case INTEGER:
            case CHAR:
            case STRING:
            case HEXA:
            case LONG:
            case FLOAT:
            case DOUBLE:
            case OBJECT:
            case DATE:
                result = exprCond(cond, value, val.toBool(), val.type);
                break;
            case NULL:
                result = new ValueBool(false);
                break;
            default:
                String mess = NajoMessages.getMessage("exception.value.operator", cond.toString(), this.type, val.type);
                result = new ValueError(mess);
                break;
            }
        return result;
    }

    /*
     * (non-Javadoc)
     * @see org.najo.values.IValue#exprMath(enums.TypeNode, org.najo.values.Value)
     */
    @Override
    public Value exprMath(TypeMath operator, Value val) throws NajoException {
        if (val.getType() == TypeValue.ERROR) return val;
        String mess = NajoMessages.getMessage("exception.value.operator", operator.toString(), this.type, val.type);
        return new ValueError(mess);
    }
}
