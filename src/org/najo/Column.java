/**
 * 
 */
package org.najo;

import org.najo.Nodes.INode;
import org.najo.values.Value;

import enums.TypeEntry;

/**
 * Column.java. <p>
 * @author  Vincent Cephirins
 * @version 1.0, 15 nov. 2010
 * <li>Creation</>
 */

public class Column {
    String name; /* Alias */
    String fullname; /* Nom complet */
    long pos; /* Position dans le record */
    int precision; /* Precision */
    TypeEntry entry; /* table source */
    int length; /* longeur de la valeur */
    Value value; /* valeur */
    INode node; /* node de l'arbre ou se trouve la colonne */
}
