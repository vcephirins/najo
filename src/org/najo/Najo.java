package org.najo;

import grammaire.Parser;

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
    private int tokenpos = 0;
    private int numline = 1;
    private StringBuffer literal = new StringBuffer(200);
    private String nextToken;

    private boolean trace = false;
    private boolean debugParsing = false;

    // Requete courante
    private Request request;

    // Historisation des requetes
    private int limitRequests = 20;
    private List<Request> requests = new ArrayList<Request>(limitRequests);

    // requete en error
    private RequestError requestError = null;

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
    public void stackRequest(Node racine) {
        try {
            Request req = new Request(literal.toString(), racine);
            requests.add(req); // historisation
            if (request == null) request = req;
        }
        catch (NajoException ne) {
            throw new NajoRuntimeException(ne);
        }
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
     * @return the tokenpos.
     */
    public int getTokenpos() {
        return tokenpos;
    }

    /**
     * @param tokenpos The tokenpos to set.
     */
    public void setTokenpos(int tokenpos) {
        this.tokenpos = tokenpos;
    }

    /**
     * @return the numline.
     */
    public int getNumline() {
        return numline;
    }

    /**
     * @param numline The numline to set.
     */
    public void setNumline(int numline) {
        this.numline = numline;
    }

    /**
     * @return the literal request.
     */
    public String getLiteral() {
        return literal.toString();
    }

    /**
     * Add the token text in literal request.
     * 
     * @param yytext The text of token to add.
     */
    public void addYytext(String yytext) {
        literal.append(yytext);
        nextToken = yytext; // Sauvegarde du token en cours
    }

    /**
     * reset environment for new request.
     */
    public void newRequest() {
        // Reset literal and request error
        literal.setLength(0);
        requestError = null;

        // Nouvelle requete ?
        if (nextToken != null) literal.append(nextToken);
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
     * Memorise le contexte de l'erreur.
     * <p>
     * 
     * @param info message complémentaire sur le contexte de l'erreur.
     */
    public void setError(String info) {
        requestError = new RequestError(numline, tokenpos, literal, info);
    }

    /**
     * Affiche l'erreur de syntaxe.
     * <p>
     */
    public void showError() {
        if (requestError != null) {
            System.out.println(requestError.getError());
            throw new NajoRuntimeException(new NajoException("exception.request.syntax"));
        }
    }

    /**
     * Calcul les valeurs de chaque noeud et affiche le résultat.
     * <p>
     * 
     * @throws NajoException
     */
    public void execute() {
        try {
            System.out.println(request.getLiteral());
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

            if (indMand != nbMandatoryArgs) { throw new NajoException(new NajoException("exception.navajo.syntax"),
                "exception.invalidParameter", ""); }

            // Creation d'une instance
            Najo najo = new Najo();

            if (interactive) {
                // parse standard input
                System.out.print("Cmd> ");
                Parser.compile(najo, new InputStreamReader(System.in));
            }
            else {
                try {
                    // parse a file
                    Parser.compile(najo, new FileReader(fileRequest));
                }
                catch (FileNotFoundException e) {
                    throw new NajoException(e, "exception.file.openFile", fileRequest);
                }
            }
        }
        catch (NajoRuntimeException ne) {
            ne.getCause().printMessages();
        }
        catch (NajoException ne) {
            ne.printMessages();
        }
        catch (Exception e) {
            // Cas anormal, affichage complet de la trace
            e.printStackTrace();
        }
    }
}
