/**
 * 
 */
package org.najo.values;

import org.free.toolboxz.date.DateException;
import org.free.toolboxz.date.JulianDate;
import org.najo.NajoException;

import enums.TypeCond;
import enums.TypeMath;

/**
 * ValueDate.java. <p>
 * @author  Vincent Cephirins
 * @version 1.0, 17 déc. 2012
 * <li>Creation</>
 */

public class ValueDate extends Value {

    protected JulianDate value;       // Value

    /**
     * Constructeur.<p>
     * @throws DateException 
     */
    public ValueDate() throws DateException {
        value = new JulianDate();
    }

    /**
     * Constructeur.<p>
     * @param val
     * @throws DateException 
     */
    public ValueDate(String val) throws DateException {
        value = new JulianDate();
    }

    /* (non-Javadoc)
     * @see org.najo.values.IValue#setValue(java.lang.String)
     */
    @Override
    public void setValue(String val) throws NajoException {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.najo.values.IValue#setValue(org.najo.values.Value)
     */
    @Override
    public void setValue(Value val) throws NajoException {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.najo.values.IValue#getVal()
     */
    @Override
    public Object getVal() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.najo.values.IValue#toOnOff()
     */
    @Override
    public Boolean toOnOff() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.najo.values.IValue#toBool()
     */
    @Override
    public Boolean toBool() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.najo.values.IValue#toByte()
     */
    @Override
    public Byte toByte() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.najo.values.IValue#toShort()
     */
    @Override
    public Integer toShort() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.najo.values.IValue#toInteger()
     */
    @Override
    public Integer toInteger() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.najo.values.IValue#toLong()
     */
    @Override
    public Long toLong() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.najo.values.IValue#toFloat()
     */
    @Override
    public Float toFloat() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.najo.values.IValue#toDouble()
     */
    @Override
    public Double toDouble() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.najo.values.IValue#toChar()
     */
    @Override
    public Character toChar() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.najo.values.IValue#toDate()
     */
    @Override
    public JulianDate toDate() throws NajoException {
        return value;
    }

    /* (non-Javadoc)
     * @see org.najo.values.IValue#toString(java.lang.String)
     */
    @Override
    public String toString(String format) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.najo.values.IValue#exprMath(enums.TypeMath, org.najo.values.Value)
     */
    @Override
    public Value exprMath(TypeMath operator, Value val) throws NajoException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.najo.values.IValue#exprCond(enums.TypeCond, org.najo.values.Value)
     */
    @Override
    public Value exprCond(TypeCond cond, Value val) throws NajoException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(Value o) {
        // TODO Auto-generated method stub
        return 0;
    }

}
