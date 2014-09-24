/**
 * 
 */
package desc;

/**
 * ColDef.java.
 * <p>
 * 
 * @author Vincent Cephirins
 * @version 1.0, 8 avr. 2013 <li>Creation</>
 */

public class ColDef {

    private String ident;
    private EnumType type;
    private ListOption listOption;

    public enum EnumType {
        BOOL, BYTE, INTEGER, FLOAT, DOUBLE, STRING, DATE_ISO, OBJECT;
    }

    /**
     * Constructeur.
     * <p>
     */
    public ColDef(String ident, EnumType type) {
        this.ident = ident;
        this.type = type;
        this.listOption = new ListOption(5);
    }

    /**
     * Constructeur.
     * <p>
     */
    public ColDef(String ident, EnumType type, ListOption listOption) {
        this.ident = ident;
        this.type = type;
        this.listOption = listOption;
    }

    /**
     * @return Returns the ident.
     */
    public String getIdent() {
        return ident;
    }

    /**
     * @return Returns the type.
     */
    public EnumType getType() {
        return type;
    }

    /**
     * @return Returns the listOption.
     */
    public ListOption getListOption() {
        return listOption;
    }
}
