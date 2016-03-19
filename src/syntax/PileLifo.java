/**
 * 
 */
package syntax;

import java.util.ArrayList;
import java.util.List;

import syntax.ParserSyntax;

/**
 * PileLifo.java.
 * <p>
 * 
 * @author Vincent Cephirins
 * @version 1.0, 13 mar. 2016 <li>Creation</>
 */
public class PileLifo {

	private Object defaultValue = null;
	private List<Object> pile = new ArrayList<Object>();
	
    /**
     * Constructeur.
     * <p>
     */
    public PileLifo() {
    	this(null);
    }

    public PileLifo(Object value) {
    	defaultValue = value;
    }

    /**
     * Ajoute une valeur dans la pile.
     * 
     * @param value Syntaxe à ajouter dans la file.
     */
    public void push(Object value) {
		// System.out.println("push : " + ((ParserSyntax) value));
    	pile.add(value);
	}

    /**
     * Dépile la dernière valeur de la pile.
     * 
     */
	public Object pop() {
		try {
			// System.out.println("pop : " + ((ParserSyntax) pile.get(pile.size() - 1)));
			return pile.remove(pile.size() - 1);
		} catch (IndexOutOfBoundsException iobe) {
			// Ignore les erreurs de dépilement
			return defaultValue;
		}
	}
	
    /**
     * lit sans retirer la dernière valeur de la pile.
     * 
     */
	public Object get() {
		try {
			return pile.get(pile.size() - 1);
		} catch (IndexOutOfBoundsException iobe) {
			// Ignore les erreurs de dépilement
			return defaultValue;
		}
	}
	
	/**
	 * Vide la pile.
	 */
	public void popAll() {
		while (pile.size() > 0) {
			pop();
		}
		// pile.clear();
	}
}
