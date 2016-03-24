/**
 * 
 */
package org.najo.values;

import org.free.toolboxz.date.JulianDate;
import org.najo.exceptions.NajoException;

import enums.TypeCond;
import enums.TypeMath;
import enums.TypeValue;

/**
 * ValueFmtAscii.java. <p>
 * @author  Vincent Cephirins
 * @version 1.0, 18 nov. 2012
 * <li>Creation</>
 */

public class ValueFmtAscii extends Value {

    protected String value;       // Value
    protected String valueConv;     // Value convertie
    
    /**
     * Constructeur.<p>
     * @param val
     * @throws NajoException
     */
    public ValueFmtAscii(String val) throws NajoException {
        setValue(val);
    }

    /**
     * Constructeur.<p>
     * @param val
     * @throws NajoException
     */
    public ValueFmtAscii(Value val) throws NajoException {
        setValue(val);
    }

    /* (non-Javadoc)
     * @see org.najo.values.IValue#setValue(java.lang.String)
     */
    @Override
    public void setValue(String val) throws NajoException {
        value = val;
        this.type = TypeValue.STRING;
        this.dim = 1;
        this.length = val.length();
        
        // Conversion au format interne
        this.valueConv = Value.getFmtFortranConv(val);
    }
    
    /* (non-Javadoc)
     * @see org.najo.values.IValue#setValue(org.najo.values.Value)
     */
    @Override
    public void setValue(Value val) throws NajoException {
        /* Conversion into internal type */
        setValue(val.toString());
    }
    
    /* (non-Javadoc)
     * @see org.najo.values.IValue#getVal()
     */
    @Override
    public String getVal() {
        return value;
    }

    /**
     * @return converted format.
     */
    public String getValConv() {
        return valueConv;
    }

    /* (non-Javadoc)
     * @see org.najo.values.IValue#toOnOff()
     */
    @Override
    public Boolean toOnOff() {
        if (value == null) return null;
        return (value != null)? true : false;
    }

    /* (non-Javadoc)
     * @see org.najo.values.IValue#toBool()
     */
    @Override
    public Boolean toBool() {
        if (value == null) return null;
        return (value != null)? true : false;
    }

    /* (non-Javadoc)
     * @see org.najo.values.IValue#toByte()
     */
    @Override
    public Byte toByte() {
        if (value == null) return null;
        return new Byte(value);
    }

    /* (non-Javadoc)
     * @see org.najo.values.IValue#toShort()
     */
    @Override
    public Integer toShort() {
        if (value == null) return null;
        return new Integer(value);
    }

    /* (non-Javadoc)
     * @see org.najo.values.IValue#toInteger()
     */
    @Override
    public Integer toInteger() {
        if (value == null) return null;
        return new Integer(value);
    }

    /* (non-Javadoc)
     * @see org.najo.values.IValue#toLong()
     */
    @Override
    public Long toLong() {
        if (value == null) return null;
        return new Long(value);
    }

    /* (non-Javadoc)
     * @see org.najo.values.IValue#toFloat()
     */
    @Override
    public Float toFloat() {
        if (value == null) return null;
        return new Float(value);
    }

    /* (non-Javadoc)
     * @see org.najo.values.IValue#toDouble()
     */
    @Override
    public Double toDouble() {
        if (value == null) return null;
        return new Double(value);
    }

    /* (non-Javadoc)
     * @see org.najo.values.IValue#toChar()
     */
    @Override
    public Character toChar() {
        if (value == null) return null;
        return new Character(value.charAt(0));
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(Value val) {
        return (value.compareTo(val.toString()));
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return (value == null)? TypeValue.NULL.toString() : value;
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
        return Value.VALUE_NULL;
    }

	@Override
	public JulianDate toDate() throws NajoException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Value exprCond(TypeCond cond, Value val) throws NajoException {
		// TODO Auto-generated method stub
		return null;
	}

}
