/**
 * 
 */
package org.najo.values;

import org.free.toolboxz.exceptions.Messages;
import org.najo.NajoException;

import enums.TypeMath;
import enums.TypeValue;

/**
 * ValueDouble.java. <p>
 * @author  Vincent Cephirins
 * @version 1.0, 1 d�c. 2010
 * <li>Creation</>
 */

public class ValueDouble extends Value {

    protected Double value;       // Value

    /**
     * Constructeur.<p>
     * @param val
     * @throws NajoException
     */
    public ValueDouble(Double val) throws NajoException {
        setValue(val);
    }

    /**
     * Constructeur.<p>
     * @param val
     * @throws NajoException
     */
    public ValueDouble(String val) throws NajoException {
        setValue(val);
    }

    /**
     * Constructeur.<p>
     * @param val
     * @throws NajoException
     */
    public ValueDouble(Value val) throws NajoException {
        setValue(val);
    }

    public void setValue(Double val) throws NajoException {
        value = val;
        this.type = TypeValue.DOUBLE;
        this.dim = 1;
        this.length = 8;
    }
    
    public void setValue(String val) throws NajoException {
        setValue((val == null)? null : new Double(val));
    }
    
    public void setValue(Value val) throws NajoException {
        /* Conversion into internal type */
        setValue(val.toDouble());
    }
    
    public Double getVal() {
        return value;
    }

    /* (non-Javadoc)
     * @see org.najo.Value#toOnOff()
     */
    @Override
    public Boolean toOnOff() {
        if (value == null) return null;
        return (value == 1.0)? true: false;
    }

    /* (non-Javadoc)
     * @see org.najo.Value#toBool()
     */
    @Override
    public Boolean toBool() {
        if (value == null) return null;
        return (value == 1.0)? true: false;
    }

    /* (non-Javadoc)
     * @see org.najo.Value#toByte()
     */
    @Override
    public Byte toByte() {
        if (value == null) return null;
        return value.byteValue();
    }

    /* (non-Javadoc)
     * @see org.najo.Value#toShort()
     */
    @Override
    public Short toShort() {
        if (value == null) return null;
        return value.shortValue();
    }

    /* (non-Javadoc)
     * @see org.najo.Value#toInteger()
     */
    @Override
    public Integer toInteger() {
        if (value == null) return null;
        return (value.intValue());
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
        return value;
    }

    /* (non-Javadoc)
     * @see org.najo.Value#toChar()
     */
    @Override
    public Character toChar() {
        if (value == null) return null;
        return ((char )value.doubleValue());
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(Value val) {
        return (value.compareTo(val.toDouble()));
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

    /*
     * (non-Javadoc)
     * @see org.najo.values.IValue#exprMath(enums.TypeNode, org.najo.values.Value)
     */
    @Override
    public Value exprMath(TypeMath type, Value val) throws NajoException {
        Value result = Value.VALUE_NULL;

        switch (type) {
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
                result = new ValueDouble(value + val.toDouble());
                break;
            case DATETIME:
            case DATE_BCD:
            case DATE_CAL_A:
            case DATE_CAL_B:
            case DATE_CCS:
            case DATE_CCSDS:
            case DATE_CDS:
            case DATE_CNES_DEC:
            case DATE_CNES_INT:
            case DATE_CUC:
            case DATE_ISO_A:
            case DATE_ISO_B:
            case DATE_UNIX:
            case OBJECT:
                String mess = Messages.getInstance().getMessage("exception.value.incompatible", this.type, val.type);
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
            throw new NajoException("exception.nodeType.notFound", type);
        }

        return result;
    }
}