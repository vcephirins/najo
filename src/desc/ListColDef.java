/**
 * 
 */
package desc;

import java.util.ArrayList;

/**
 * ListColDef.java.
 * <p>
 * 
 * @author Vincent Cephirins
 * @version 1.0, 8 avr. 2013 <li>Creation</>
 */

public class ListColDef extends ArrayList<ColDef> {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructeur.
     * <p>
     * 
     * @param name
     */
    public ListColDef() {
        super();
    }

    /**
     * Constructeur.
     * <p>
     * 
     * @param name
     */
    public ListColDef(int initialCapacity) {
        super(initialCapacity);
    }
}
