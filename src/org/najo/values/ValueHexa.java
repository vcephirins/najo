/**
 * 
 */
package org.najo.values;

import org.free.toolboxz.date.JulianDate;
import org.najo.enums.TypeCond;
import org.najo.enums.TypeMath;
import org.najo.enums.TypeValue;
import org.najo.exceptions.NajoException;
import org.najo.exceptions.NajoMessages;

/**
 * ValueHexa.java. <p>
 * @author  Vincent Cephirins
 * @version 1.0, 1 d�c. 2010
 * <li>Creation</>
 */

public class ValueHexa extends Value {

    protected String value;       // Value

    /**
     * Constructeur.<p>
     * @param val
     * @throws NajoException
     */
    public ValueHexa(String val) throws NajoException {
        setValue(val);
    }

    /**
     * Constructeur.<p>
     * @param val
     * @throws NajoException
     */
    public ValueHexa(Value val) throws NajoException {
        setValue(val);
    }

    public void setValue(String val) throws NajoException {
        value = val;
        this.type = TypeValue.STRING;
        this.dim = 1;
        this.length = val.length();
    }
    
    public void setValue(Value val) throws NajoException {
        /* Conversion into internal type */
        setValue(val.toString());
    }
    
    public String getVal() {
        return value;
    }

    /* (non-Javadoc)
     * @see org.najo.Value#toOnOff()
     */
    @Override
    public Boolean toOnOff() {
        if (value == null) return null;
        return value.equalsIgnoreCase("on");
    }

    /* (non-Javadoc)
     * @see org.najo.Value#toBool()
     */
    @Override
    public Boolean toBool() {
        if (value == null) return null;
        return value.equalsIgnoreCase("true");
    }

    /* (non-Javadoc)
     * @see org.najo.Value#toByte()
     */
    @Override
    public Byte toByte() {
        if (value == null) return null;
        return new Byte(value);
    }

    /* (non-Javadoc)
     * @see org.najo.Value#toShort()
     */
    @Override
    public Integer toShort() {
        if (value == null) return null;
        return new Integer(value);
    }

    /* (non-Javadoc)
     * @see org.najo.Value#toInteger()
     */
    @Override
    public Integer toInteger() {
        if (value == null) return null;
        return new Integer(value);
    }

    /* (non-Javadoc)
     * @see org.najo.Value#toLong()
     */
    @Override
    public Long toLong() {
        if (value == null) return null;
        return new Long(value);
    }

    /* (non-Javadoc)
     * @see org.najo.Value#toFloat()
     */
    @Override
    public Float toFloat() {
        if (value == null) return null;
        return new Float(value);
    }

    /* (non-Javadoc)
     * @see org.najo.Value#toDouble()
     */
    @Override
    public Double toDouble() {
        if (value == null) return null;
        return new Double(value);
    }

    /* (non-Javadoc)
     * @see org.najo.Value#toChar()
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
                result = new ValueHexa(value + val.toInteger());
                break;
            case LONG:
                result = new ValueHexa(value + val.toLong());
                break;
            case FLOAT:
                result = new ValueFloat(value + val.toFloat());
                break;
            case DOUBLE:
                result = new ValueDouble(value + val.toDouble());
                break;
            case OBJECT:
                String mess = NajoMessages.getMessage("exception.value.incompatible", this.type, val.type);
                result = new ValueError(mess);
                break;
            case ERROR:
                result = val;
                break;
            case NULL:
                result = Value.VALUE_NULL;
                break;
			case DATE:
				break;
			default:
				break;
            }
            break;
        default:
            throw new NajoException("exception.nodeType.notFound", type);
        }

        return result;
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
