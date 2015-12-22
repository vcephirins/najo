/**
 * 
 */
package org.najo.values;

import org.free.toolboxz.date.DateException;
import org.free.toolboxz.date.JulianDate;
import org.free.toolboxz.exceptions.Messages;
import org.najo.NajoException;

import enums.TypeCond;
import enums.TypeMath;
import enums.TypeValue;

/**
 * ValueByte.java. <p>
 * @author  Vincent Cephirins
 * @version 1.0, 1 d�c. 2010
 * <li>Creation</>
 */

public class ValueByte extends Value {

    protected Byte value;       // Value

    /**
     * Constructeur.<p>
     * @param val
     * @throws NajoException
     */
    public ValueByte(Byte val) throws NajoException {
        setValue(val);
    }

    /**
     * Constructeur.<p>
     * @param val
     * @throws NajoException
     */
    public ValueByte(Integer val) throws NajoException {
        setValue(val.byteValue());
    }

    /**
     * Constructeur.<p>
     * @param val
     * @throws NajoException
     */
    public ValueByte(String val) throws NajoException {
        setValue(val);
    }

    /**
     * Constructeur.<p>
     * @param val
     * @throws NajoException
     */
    public ValueByte(Value val) throws NajoException {
        setValue(val);
    }

    public void setValue(Byte val) throws NajoException {
        value = val;
        this.type = TypeValue.BYTE;
        this.dim = 1;
        this.length = 1;
    }
    
    public void setValue(String val) throws NajoException {
        setValue((val == null)? null : new Byte(val));
    }
    
    public void setValue(Value val) throws NajoException {
        /* Conversion into internal type */
        setValue(val.toByte());
    }
    
    public Byte getVal() {
        return value;
    }

    /* (non-Javadoc)
     * @see org.najo.Value#toOnOff()
     */
    @Override
    public Boolean toOnOff() {
        if (value == null) return null;
        return (value == 1)? true : false;
    }

    /* (non-Javadoc)
     * @see org.najo.Value#toBool()
     */
    @Override
    public Boolean toBool() {
        if (value == null) return null;
        return (value == 1)? true : false;
    }

    /* (non-Javadoc)
     * @see org.najo.Value#toByte()
     */
    @Override
    public Byte toByte() {
        return value;
    }

    /* (non-Javadoc)
     * @see org.najo.Value#toShort()
     */
    @Override
    public Integer toShort() {
        if (value == null) return null;
        return value.intValue();
    }

    /* (non-Javadoc)
     * @see org.najo.Value#toInteger()
     */
    @Override
    public Integer toInteger() {
        if (value == null) return null;
        return value.intValue();
    }

    /* (non-Javadoc)
     * @see org.najo.Value#toLong()
     */
    @Override
    public Long toLong() {
        if (value == null) return null;
        return value.longValue();
    }

    /* (non-Javadoc)
     * @see org.najo.Value#toFloat()
     */
    @Override
    public Float toFloat() {
        if (value == null) return null;
        return value.floatValue();
    }

    /* (non-Javadoc)
     * @see org.najo.Value#toDouble()
     */
    @Override
    public Double toDouble() {
        if (value == null) return null;
        return value.doubleValue();
    }

    /* (non-Javadoc)
     * @see org.najo.Value#toChar()
     */
    @Override
    public Character toChar() {
        if (value == null) return null;
        return ((char )value.byteValue());
    }

    /* (non-Javadoc)
     * @see org.najo.values.IValue#toDate()
     */
    @Override
    public JulianDate toDate() throws NajoException {
        try {
            return new JulianDate(value);
        }
        catch (DateException e) {
            throw new NajoException(e);
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return (value == null)? TypeValue.NULL.toString() : value.toString();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString(String)
     */
    @Override
    public String toString(String format) {
        if (value == null) return TypeValue.NULL.toString();
        return String.format(format, value);
    }
    
    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(Value val) {
        return (value.compareTo(val.toByte()));
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
            result = exprCond(cond, toBool(), val.toBool(), val.type);
            break;
        case BYTE:
            result = exprCond(cond, value, val.toByte(), val.type);
            break;
        case SHORT:
        case INTEGER:
        case CHAR:
        case STRING:
        case HEXA:
            result = exprCond(cond, toInteger(), val.toInteger(), val.type);
            break;
        case LONG:
            result = exprCond(cond, toLong(), val.toLong(), val.type);
            break;
        case FLOAT:
            result = exprCond(cond, toFloat(), val.toFloat(), val.type);
            break;
        case DOUBLE:
            result = exprCond(cond, toDouble(), val.toDouble(), val.type);
            break;
        case NULL:
            result = new ValueBool(false);
            break;
        default:
            String mess = Messages.getInstance().getMessage("exception.value.operator", cond.toString(), this.type,
                val.type);
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
        Value result = Value.VALUE_NULL;

        switch (operator) {
        case PLUS:
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
                result = new ValueByte(value + val.toByte());
                break;
            case DATE:
            case OBJECT:
                String mess = Messages.getInstance().getMessage("exception.value.operator", this.type, val.type);
                result = new ValueError(mess);
                break;
            case ERROR:
                result = val;
                break;
            case NULL:
                result = Value.VALUE_NULL;
                break;
            }
            break;
        default:
            throw new NajoException("exception.nodeType.notFound", operator);
        }

        return result;
    }
}
