/**
 * 
 */
package desc;

/**
 * LineDef.java.
 * <p>
 * 
 * @author Vincent Cephirins
 * @version 1.0, 10 avr. 2013 <li>Creation</>
 */

public class LineDef {

    private String ident;
    private ListOption listOption;
    private ListColDef listColDef;

    /**
     * Constructeur.
     * <p>
     */
    public LineDef(String ident, ListOption listOption, ListColDef listColDef) {
        this.ident = ident;
        this.listOption = listOption;
        this.listColDef = listColDef;
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

    /**
     * @return Returns the listColDef.
     */
    public ListColDef getListColDef() {
        return listColDef;
    }

}
