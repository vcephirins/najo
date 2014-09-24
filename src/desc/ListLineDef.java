/**
 * 
 */
package desc;

import java.util.ArrayList;

/**
 * ListLineDef.java.
 * <p>
 * 
 * @author Vincent Cephirins
 * @version 1.0, 10 avr. 2013 <li>Creation</>
 */

public class ListLineDef extends ArrayList<LineDef> {
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
    public ListLineDef() {
        super();
    }

    /**
     * Constructeur.
     * <p>
     * 
     * @param name
     */
    public ListLineDef(int initialCapacity) {
        super(initialCapacity);
    }
}
