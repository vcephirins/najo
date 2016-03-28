/**
 * 
 */
package org.najo;

import java.io.FileReader;

import org.najo.enums.TypeEntry;

/**
 * Entry.java.
 * <p>
 * 
 * @author Vincent Cephirins
 * @version 1.0, 15 nov. 2010 <li>Creation</>
 */

public class Entry {
    int idFile; // Handle table
    String nameFile; // Nom interne du fichier (Alias)
    String pathFile; // Chemin d'acces au fichier des donnees
    String pathDesc; // Chemin d'acces au fichier de description
    TypeEntry type; // Type du fichier
    FileReader file; // Handle du fichier des donnees
    long rowid; // Memorise la position de la ligne courante

}
