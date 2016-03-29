/**
 * 
 */
package syntax;

import org.najo.exceptions.NajoMessages;

/**
 * ParserError.java.
 * <p>
 * 
 * @author Vincent Cephirins
 * @version 1.0, 17 nov. 2012 <li>Creation</>
 */

public class ParserError {

    // Token en erreur
    private int numline = 0;
    private int tokenpos = 0;
    private String literal = null;
    private String info = null;

    /**
     * Constructeur.
     * <p>
     * Interdit l'implémentation.
     */
    @SuppressWarnings("unused")
    private ParserError() {
    }

    /**
     * Constructeur.
     * <p>
     * 
     * @param tokenpos
     * @param numline
     * @param literal
     * @param info
     */
    public ParserError(int numline, int tokenpos, String literal, String info) {
        this.numline = numline;
        this.tokenpos = tokenpos;
        this.literal = literal;
        this.info = info;
    }

    /**
	 * @return the numline
	 */
	public int getNumline() {
		return numline;
	}

	/**
	 * @return the tokenpos
	 */
	public int getTokenpos() {
		return tokenpos;
	}

	/**
	 * @return the literal
	 */
	public String getLiteral() {
		return literal;
	}

	/**
	 * @param literal the literal to set
	 */
	public void setLiteral(String literal) {
		this.literal = literal;
	}

	/**
	 * @return the info
	 */
	public String getInfo() {
		return info;
	}

	/**
     * Retourne l'erreur.
     * <p>
     * 
     * @return l'erreur.
     */
    public String getError() {
        String pos = String.format(String.format("%%%ds", tokenpos), "^");
        return NajoMessages.getMessage("exception.request.at", numline, literal, pos, info);
//        return String.format("Error at line %d\n%s\n%s\n%s\n", numline, literal, pos, info);
    }

}
