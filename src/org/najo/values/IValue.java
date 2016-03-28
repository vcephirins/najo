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
 * Toto.java. <p>
 * @author  Vincent Cephirins
 * @version 1.0, 3 nov. 2012
 * <li>Creation</>
 */

public interface IValue {
    /**
     * @return Returns the type.
     */
    public abstract TypeValue getType();

    /**
     * @return Returns the length.
     */
    public abstract long getLength();

    /**
     * @return Returns the dim.
     */
    public abstract long getDim();

    public abstract void setValue(String val) throws NajoException;

    public abstract void setValue(Value val) throws NajoException;

    public abstract Object getVal();

    public abstract Boolean toOnOff();

    public abstract Boolean toBool();

    public abstract Byte toByte();

    public abstract Integer toShort();

    public abstract Integer toInteger();

    public abstract Long toLong();

    public abstract Float toFloat();

    public abstract Double toDouble();

    public abstract Character toChar();

    public abstract JulianDate toDate() throws NajoException;

    public abstract String toString();

    /**
     * @param format to format
     * @return string value as formatted string.
     */
    public abstract String toString(String format);

    public abstract Value exprMath(TypeMath operator, Value val) throws NajoException;

    public abstract Value exprCond(TypeCond cond, Value val) throws NajoException;
}
