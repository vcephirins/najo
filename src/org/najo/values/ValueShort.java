/**
 * 
 */
package org.najo.values;

import org.najo.NajoException;

import enums.TypeValue;

/**
 * ValueShort.java. <p>
 * @author  Vincent Cephirins
 * @version 1.0, 1 d�c. 2010
 * <li>Creation</>
 */

public class ValueShort extends ValueInteger {

    /**
     * Constructeur.<p>
     * @param val
     * @throws NajoException
     */
    public ValueShort(Short val) throws NajoException {
        super(new Integer(val));
    }

    /**
     * Constructeur.<p>
     * @param val
     * @throws NajoException
     */
    public ValueShort(Integer val) throws NajoException {
        super(val);
    }

    /**
     * Constructeur.<p>
     * @param val
     * @throws NajoException
     */
    public ValueShort(String val) throws NajoException {
        super(val);
    }

    /**
     * Constructeur.<p>
     * @param val
     * @throws NajoException
     */
    public ValueShort(Value val) throws NajoException {
        super(val);
    }

    public void setValue(Integer val) throws NajoException {
        value = val;
        this.type = TypeValue.SHORT;
        this.dim = 1;
        this.length = 2;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(Value val) {
        return (((Short )value.shortValue()).compareTo((Short) val.toShort().shortValue()));
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return (value == null)? TypeValue.NULL.toString() : ((Short )value.shortValue()).toString();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString(String)
     */
    @Override
    public String toString(String format) {
        if (value == null) return Value.VALUE_NULL.toString();
        return String.format(format, value.shortValue());
    }
}
