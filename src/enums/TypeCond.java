/**
 * 
 */
package enums;

/**
 * TypeCond.java. <p>
 * @author  Vincent Cephirins
 * @version 1.0, 16 déc. 2012
 * <li>Creation</>
 */

public enum TypeCond {
    LESS("<"),
    SUP("+"),
    LE("<="),
    EQEQ("=="),
    GE(">="),
    NE("!="),
    NOT("NOT"),
    OR("OR"),
    AND("AND");
    
    private String symbol;

    TypeCond(String symbol) {
        this.symbol = symbol;
    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    /**
     * @return la condition symbolique
     */
    @Override
    public String toString() {
        return symbol;
    }
}
