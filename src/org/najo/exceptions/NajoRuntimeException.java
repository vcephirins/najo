/**
 * 
 */
package org.najo.exceptions;

/**
 * Class d'encapsulation d'exception evitant les déclarations throws. <p>
 * @author  Vincent Cephirins
 * @version 1.0, 4 nov. 2012
 * <li>Creation</>
 */
public class NajoRuntimeException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructeur.<p>
     * Interdit l'implémentation simple.
     */
    @SuppressWarnings("unused")
    private NajoRuntimeException() {
    }

    /**
     * Constructeur.<p>
     * @param message
     * Interdit l'implémentation simple.
     */
    @SuppressWarnings("unused")
    private NajoRuntimeException(String message) {
        super(message);
    }

    /**
     * Constructeur.<p>
     * @param message Information d'erreur
     * @param cause
     */
    public NajoRuntimeException(String message, NajoException cause) {
        super(message, cause);
    }

    /**
     * Constructeur.<p>
     * @param cause
     */
    public NajoRuntimeException(NajoException cause) {
        super(cause);
    }

    /* (non-Javadoc)
     * @see java.lang.Throwable#getCause()
     */
    @Override
    public NajoException getCause() {
        return ((NajoException) super.getCause());
    }
}
