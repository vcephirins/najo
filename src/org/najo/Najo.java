package org.najo;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.free.toolboxz.exceptions.Messages;
import org.free.toolboxz.goodies.Proxy;
import org.najo.Nodes.Node;

import com.spinn3r.log5j.Logger;

import syntax.Parser;

public class Najo {

    @SuppressWarnings("unused")
    private static final long serialVersionUID = 1L;

    // Initialise le logger (voir log4j.xml).
    private static final Logger LOGGER = Logger.getLogger();

    // Gestion de la version
    private static final class Version {
        private static final int MAJOR = 1;
        private static final int MINOR = 0;
        private static final int BUILD = 0;

        /**
         * Return version.
         * 
         * @return version "major.minor.build".
         */
        private static String getVersion() {
            return new String(MAJOR + "." + MINOR + "." + BUILD);
        }

        /**
         * Interdit l'instanciation de cette classe.
         */
        private Version() {}
    }

    /**
     * Variables generales
     */

    private boolean trace = false;
    private boolean debugParsing = false;

    // Requete courante
    private Request request;

    // Historisation des requetes
    private int limitRequests = 20;
    private List<Request> requests = new ArrayList<Request>(limitRequests);

    // requete en error
    // private ParserError requestError = null;
    private Parser parser = null;
    
	/**
     * @return Returns the requests.
     */
    public List<Request> getRequests() {
        return requests;
    }

    /**
     * historisation de la requete compilée.
     * <p>
     * 
     * @param racine premier node de la requete.
     */
    private Request stackRequest(String literal, Node racine) {
        try {
        	request = new Request(literal, racine);
            requests.add(request); // historisation
            return request;
        }
        catch (NajoException ne) {
            throw new NajoRuntimeException(ne);
        }
    }

    /**
     * Affichage de la hierachie à partir du noeud donné.
     * <p>
     * 
     * @param racine premier node à afficher..
     */
    public void trace(Node racine) {
    	if (racine != null && isTrace()) racine.display(1);
    }

    /**
     * @return Returns the current request.
     */
    public Request getCurrentRequest() {
        if (request == null) request = requests.get(requests.size() - 1);
        return request;
    }

    /**
     * @return Returns the current request.
     */
    public Request getRequest(int index) {
        if (index < 0) index = 0;
        if (index >= requests.size()) index = requests.size() - 1;
        return request;
    }

    /**
     * set the current request.
     */
    public Request setRequest(int index) {
        if (index < 0) index = 0;
        if (index >= requests.size()) index = requests.size() - 1;
        request = requests.get(index);
        return request;
    }

    /**
     * Retourne la valeur réelle du node.
     * <p>
     * 
     * @param node
     * @return
     */
    public Object getValue(Node node) {
        try {
            return node.getValue().getVal();
        }
        catch (NajoException e) {
            throw new NajoRuntimeException(e);
        }
    }

    /**
     * Calcul les valeurs de chaque noeud et affiche le résultat.
     * <p>
     * 
     * @throws NajoException
     */
    public void execute(String literal, Node racine) {
        try {
        	request = stackRequest(literal, racine);
            System.out.println(literal);
            request.output();
        }
        catch (NajoException ne) {
            throw new NajoRuntimeException(ne);
        }
    }

    /**
     * Push info message in logger.
     * <p>
     * 
     * @param message to log
     */
    public void logInfo(String message) {
        LOGGER.info(message);
    }

    /**
     * Push warning message in logger.
     * <p>
     * 
     * @param message to log
     */
    public void logWarning(String message) {
        LOGGER.warn(message);
    }

    /**
     * @return Returns the trace.
     */
    public boolean isTrace() {
        return trace;
    }

    /**
     * @param trace The trace to set.
     */
    public void setTrace(boolean trace) {
        this.trace = trace;
    }

    /**
     * @param debug The trace to set.
     */
    public void setDebugParsing(boolean debug) {
        this.debugParsing = debug;
    }

    /**
     * @param debug The trace to set.
     */
    public boolean isDebugParsing() {
        return this.debugParsing;
    }

    public void bye(int status) {
        System.exit(status);
    }

    /**
     * Entrée du programme.
     * <p>
     * 
     * @param args
     * @throws NajoException
     */
    public static void main(String[] args) throws NajoException {

        try {

            LOGGER.info("Najo is pending");

            int nbMandatoryArgs = 1;
            int indMand = 0;
            String[] mandatoryArgs = new String[nbMandatoryArgs];
            Proxy proxy = null;
            boolean interactive = false;

            String fileRequest = null;

            // Chargement du fichier des messages applicatifs
            Messages.getInstance().load("messagesException");

            for (int indArg = 0; indArg < args.length; indArg++) {
                if (args[indArg].startsWith("-h") || args[indArg].equals("--help")) { throw new NajoException(
                    "exception.navajo.syntax"); }
                if (args[indArg].startsWith("-v") || args[indArg].equals("--version")) {
                    LOGGER.info("Najo version : " + Version.getVersion());
                    continue;
                }
                if (args[indArg].startsWith("-t") || args[indArg].equals("--trace")) {
                    LOGGER.setLevel(Level.DEBUG);
                    continue;
                }
                if (args[indArg].startsWith("-i") || args[indArg].equals("--interactive")) {
                    // Interactive mode
                    interactive = true;
                    indMand++;
                    continue;
                }
                if (args[indArg].startsWith("-p") || args[indArg].equals("--proxy")) {
                    // configuration du proxy
                    indArg++;
                    String[] proxyArgs = args[indArg].split("[/:@]");

                    switch (proxyArgs.length) {
                    case 2:
                        // host:port
                        proxy = new Proxy(proxyArgs[0], proxyArgs[1]);
                        break;
                    case 3:
                        // Authorization@host:port
                        proxy = new Proxy(proxyArgs[1], proxyArgs[2], proxyArgs[0]);
                        break;
                    case 4:
                        // user/password@host:port
                        proxy = new Proxy(proxyArgs[2], proxyArgs[3], proxyArgs[0], proxyArgs[1]);
                        break;
                    default:
                        throw new NajoException("exception.navajo.syntax");
                    }
                    continue;
                }

                // Arguments obligatoires
                if (indMand < nbMandatoryArgs) {
                    mandatoryArgs[indMand] = args[indArg];
                    fileRequest = args[indArg];
                }
                indMand++;
            }

			if (indMand != nbMandatoryArgs) {
				throw new NajoException(new NajoException("exception.navajo.syntax"), "exception.invalidParameter", "");
			}

			// Creation d'une instance
			Najo najo = new Najo();

			try {
				if (interactive) {
					// parse standard input
					System.out.print("Cmd> ");
					najo.parser = Parser.compile(najo, new InputStreamReader(System.in));
				} else {
					// parse a file
					najo.parser = Parser.compile(najo, new FileReader(fileRequest));
				}
			} catch (FileNotFoundException fne) {
				throw new NajoException(fne, "exception.file.openFile", fileRequest);
			} catch (Exception e) {
				throw new NajoException(e, "exception.request.syntax");
			}
		} catch (NajoRuntimeException ne) {
			ne.getCause().printMessages();
		} catch (NajoException ne) {
			ne.printMessages();
		} catch (Exception e) {
			// Cas anormal, affichage complet de la trace
			e.printStackTrace();
		}
	}
}
