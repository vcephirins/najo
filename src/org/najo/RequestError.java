/**
 * 
 */
package org.najo;

/**
 * RequestError.java. <p>
 * @author  Vincent Cephirins
 * @version 1.0, 17 nov. 2012
 * <li>Creation</>
 */

public class RequestError {

    // Token en erreur
    private int numline = 0;
    private int tokenpos = 0;
    private StringBuffer literal = null;
    private String info = null;
    
    /**
     * Constructeur.<p>
     * Interdit l'implémentation.
     */
    @SuppressWarnings("unused")
    private RequestError() {
    }

    /**
     * Constructeur.<p>
     * @param tokenpos
     * @param numline
     * @param literal
     * @param info
     */
    public RequestError(int numline, int tokenpos, StringBuffer literal, String info) {
        this.numline = numline;
        this.tokenpos = tokenpos;
        this.literal = literal;
        this.info = info;
    }


    /**
     * Affiche l'erreur si exitante et provoque l'arrêt.<p>
     * @return true si erreur.
     */
    public String getError() {
        String pos = String.format(String.format("%%%ds", tokenpos), "^");
        return String.format("Error at line %d\n%s\n%s\n%s\n", numline, literal.toString(), pos, info);
    }
    
}
