/**
 * 
 */
package org.najo.values;

import org.najo.NajoException;

import enums.TypeValue;

/**
 * ValueOnOff.java. <p>
 * @author  Vincent Cephirins
 * @version 1.0, 1 déc. 2010
 * <li>Creation</>
 */

public class ValueOnOff extends ValueBool {

    /**
     * Constructeur.<p>
     * @param val
     * @throws NajoException 
     */
    public ValueOnOff(Boolean val) throws NajoException {
        super(val);
    }

    /**
     * Constructeur.<p>
     * @param val
     * @throws NajoException 
     */
    public ValueOnOff(String val) throws NajoException {
        super(val);
    }

    public void setValue(Boolean val) throws NajoException {
        value = val;
        this.type = TypeValue.ON_OFF;
        this.dim = 1;
        this.length = 1;
    }
    
    public void setValue(String val) throws NajoException {
        setValue((val == null)? null : new Boolean(val.equalsIgnoreCase("ON")));
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return ((value == null)? Value.VALUE_NULL.toString() : (value)? "ON" : "OFF");
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString(String)
     */
    @Override
    public String toString(String format) {
        if (value == null) return Value.VALUE_NULL.toString();
        return String.format(format, (value)? "ON" : "OFF");
    }
}
