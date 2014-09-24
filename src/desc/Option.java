/**
 * 
 */
package desc;

/**
 * Option.java.
 * <p>
 * 
 * @author Vincent Cephirins
 * @version 1.0, 7 avr. 2013 <li>Creation</>
 */

public class Option {

    public enum EnumOption {
        MODE, ENDIAN, REPEAT, UNSIGNED;
    }

    private EnumOption option;
    private int value;

    /**
     * Constructeur.
     * <p>
     */
    public Option(EnumOption option, int value) {
        this.option = option;
        this.value = value;
    }

    /**
     * @return Returns the value.
     */
    public int getValue() {
        return value;
    }

    /**
     * @param value The value to set.
     */
    public void setValue(int value) {
        this.value = value;
    }

    /**
     * @return Returns the option.
     */
    public EnumOption getOption() {
        return option;
    }
}
