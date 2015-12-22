package org.najo;

import org.free.toolboxz.exceptions.LoggerException;

import com.spinn3r.log5j.Logger;

/**
 * Exception standard.
 * <p>
 * @author Vincent Cephirins
 * @version 1.0, 20/01/2009
 * <li>Creation</li>
 */
public class NajoException extends LoggerException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    // Initialise le logger (voir log4j.xml).
    private static final Logger LOGGER = Logger.getLogger();

    /**
     * Constructeur
     * 
     * @param ident l'identifiant de l'error � afficher
     * @param arguments les arguments du message d'erreur.
     */
    public NajoException(String ident, Object... arguments) {
        super(ident, arguments);
        logger = LOGGER;
    }

    /**
     * Constructeur
     * 
     * @param e La cause de l'exception
     */
    public NajoException(Exception e) {
        super(e);
        logger = LOGGER;
    }

    /**
     * Constructeur
     * 
     * @param e La cause de l'exception
     * @param ident l'identifiant de l'error � afficher
     * @param arguments les arguments du message d'erreur.
     */
    public NajoException(Exception e, String ident, Object... arguments) {
        super(e, ident, arguments);
        logger = LOGGER;
    }

}
