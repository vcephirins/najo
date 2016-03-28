/**
 * 
 */
package org.najo.values;

import org.free.toolboxz.date.JulianDate;
import org.najo.enums.TypeCond;
import org.najo.enums.TypeMath;
import org.najo.enums.TypeValue;
import org.najo.exceptions.NajoException;

/**
 * ValueNull.java. <p>
 * @author  Vincent Cephirins
 * @version 1.0, 1 d�c. 2010
 * <li>Creation</>
 */

public class ValueNull extends Value {

    /**
     * Constructeur.<p>
     * @throws NajoException 
     */
    public ValueNull() {
        try {
            setValue();
        }
        catch (NajoException e) {
            e.printMessages();
        }
    }

    /**
     * Constructeur.<p>
     * @param type
     * @param value
     * @throws NajoException
     */
    public void setValue() throws NajoException {
            this.type = TypeValue.NULL;
            this.dim = 0;
            this.length = 0;
    }
    
    /* (non-Javadoc)
     * @see org.najo.Value#setValue(java.lang.String)
     */
    @Override
    public void setValue(String val) throws NajoException {
        setValue();
    }

    /* (non-Javadoc)
     * @see org.najo.Value#setValue(org.najo.Value)
     */
    @Override
    public void setValue(Value val) throws NajoException {
        setValue();
    }

    /* (non-Javadoc)
     * @see org.najo.Value#getVal()
     */
    @Override
    public ValueNull getVal() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.najo.Value#toOnOff()
     */
    @Override
    public Boolean toOnOff() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.najo.Value#toBool()
     */
    @Override
    public Boolean toBool() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.najo.Value#toInteger()
     */
    @Override
    public Byte toByte() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.najo.Value#toShort()
     */
    @Override
    public Integer toShort() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.najo.Value#toInteger()
     */
    @Override
    public Integer toInteger() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.najo.Value#toLong()
     */
    @Override
    public Long toLong() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.najo.Value#toFloat()
     */
    @Override
    public Float toFloat() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.najo.Value#toDouble()
     */
    @Override
    public Double toDouble() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.najo.Value#toChar()
     */
    @Override
    public Character toChar() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.najo.Value#toDate()
     */
    @Override
    public JulianDate toDate() {
        return null;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return TypeValue.NULL.toString();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString(String)
     */
    @Override
    public String toString(String format) {
        return TypeValue.NULL.toString();
    }
    
    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(Value val) {
        return -1;
    }

    /*
     * (non-Javadoc)
     * @see org.najo.values.IValue#exprCond(enums.TypeCond, org.najo.values.Value)
     */
    @Override
    public Value exprCond(TypeCond cond, Value val) throws NajoException {
        return this;
    }

    /*
     * (non-Javadoc)
     * @see org.najo.values.IValue#exprMath(enums.TypeMath, org.najo.values.Value)
     */
    @Override
    public Value exprMath(TypeMath operator, Value val) throws NajoException {
        return this;
    }
}
