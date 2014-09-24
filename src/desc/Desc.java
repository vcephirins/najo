/**
 * 
 */
package desc;

/**
 * Desc.java.
 * <p>
 * 
 * @author Vincent Cephirins
 * @version 1.0, 14 avr. 2013 <li>Creation</>
 */

public class Desc {

    private ListTypeDef listTypeDef;
    private FileDef fileDef;
    private ListLineDef listLindeDef;

    /**
     * Constructeur.
     * <p>
     */
    public Desc(ListTypeDef listTypeDef, FileDef fileDef, ListLineDef listLineDef) {
        this.listTypeDef = listTypeDef;
        this.fileDef = fileDef;
        this.listLindeDef = listLineDef;
    }

    /**
     * @return Returns the listTypeDef.
     */
    public ListTypeDef getListTypeDef() {
        return listTypeDef;
    }

    /**
     * @return Returns the fileDef.
     */
    public FileDef getFileDef() {
        return fileDef;
    }

    /**
     * @return Returns the listLindeDef.
     */
    public ListLineDef getListLindeDef() {
        return listLindeDef;
    }

}
