/**
 * 
 */
package desc;

import java.util.ArrayList;

/**
 * ListTypeDef.java.
 * <p>
 * 
 * @author Vincent Cephirins
 * @version 1.0, 10 avr. 2013 <li>Creation</>
 */

public class ListTypeDef extends ArrayList<TypeDef> {
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
    public ListTypeDef() {
        super();
    }

    /**
     * Constructeur.
     * <p>
     * 
     * @param name
     */
    public ListTypeDef(int initialCapacity) {
        super(initialCapacity);
    }
}
