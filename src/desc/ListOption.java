/**
 * 
 */
package desc;

import java.util.ArrayList;

/**
 * ListOption.java.
 * <p>
 * 
 * @author Vincent Cephirins
 * @version 1.0, 15 nov. 2010 <li>Creation</>
 */

public class ListOption extends ArrayList<Option> {
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
    public ListOption() {
        super();
    }

    /**
     * Constructeur.
     * <p>
     * 
     * @param name
     */
    public ListOption(int initialCapacity) {
        super(initialCapacity);
    }
}
