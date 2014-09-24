/**
 * 
 */
package desc;

/**
 * TypeDef.java.
 * <p>
 * 
 * @author Vincent Cephirins
 * @version 1.0, 8 avr. 2013 <li>Creation</>
 */

public class TypeDef {

    private String ident;
    private ListColDef listColDef;

    /**
     * Constructeur.
     * <p>
     */
    public TypeDef(String ident) {
        this.ident = ident;
        this.listColDef = new ListColDef(10);
    }

    /**
     * Constructeur.
     * <p>
     */
    public TypeDef(String ident, ListColDef listColDef) {
        this.ident = ident;
        this.listColDef = listColDef;
    }

    /**
     * @return Returns the ident.
     */
    public String getIdent() {
        return ident;
    }

    /**
     * @return Returns the listColDef.
     */
    public ListColDef getListColDef() {
        return listColDef;
    }
}
