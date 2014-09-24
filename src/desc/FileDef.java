/**
 * 
 */
package desc;

/**
 * FileDef.java.
 * <p>
 * 
 * @author Vincent Cephirins
 * @version 1.0, 10 avr. 2013 <li>Creation</>
 */

public class FileDef {

    private String ident;
    private ListOption listOption;

    /**
     * Constructeur.
     * <p>
     */
    public FileDef(String ident, ListOption listOption) {
        this.ident = ident;
        this.listOption = listOption;
    }

    /**
     * @return Returns the ident.
     */
    public String getIdent() {
        return ident;
    }

    /**
     * @return Returns the listOption.
     */
    public ListOption getListOption() {
        return listOption;
    }
}
