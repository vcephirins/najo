/**
 * 
 */
package desc;

/**
 * ParserSyntax.java.
 * <p>
 * 
 * @author Vincent Cephirins
 * @version 1.0, 18 nov. 2010 <li>Creation</>
 */

public enum ParserSyntax {
    // ???
    NOT_DEFINED(""),
    // Options des colonnes
    MODE("ASCII | BIN[ARY]"),
    ENDIAN("BIG | LITTLE"),
    SIZE("[SIZE] <number>"),
    TYPE("BOOL | BYTE | INTEGER size | DOUBLE | STRING size | OBJECT size", SIZE),
    OPT_FILEDEF("[options]"),
    COLDEF("COLDEF <ident> [AS <typedef_ident> [REPEAT <number>]]"),
    COLDEF2("COLDEF <ident> <type> [<mode>] [<endian>] [UNSIGNED] [REPEAT <number>]", TYPE, MODE, ENDIAN),
    LINEDEF("LINEDEF <ident> [<mode>] [<endian>] <coldef> [<coldef> ...]", MODE, ENDIAN, COLDEF, COLDEF2),
    FILEDEF("FILEDEF <ident> [<mode>] [<endian>]", MODE, ENDIAN),
    TYPEDEF("TYPEDEF <ident> AS <coldef> [<coldef> ...]", COLDEF, COLDEF2),
    RACINE("[TYPEDEF <typedef>] [FILEDEF <filedef>] LINEDEF <linedef> [<linedef> ...]", TYPEDEF, FILEDEF, LINEDEF);

    private String syntax;
    private ParserSyntax[] relations;

    ParserSyntax(String syntax, ParserSyntax... relations) {
        this.syntax = syntax;
        this.relations = relations;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    /**
     * @return la syntaxe d'un mot clef
     */
    public String toString() {
        return syntax;
    }

    /**
     * Retourne la syntaxe d'un mot clef et de ses relations
     * 
     * @return la syntaxe avec toutes les définitions relationnelles
     */
    public String definition() {
        StringBuffer result = new StringBuffer(50 * relations.length);
        result.append(syntax);
        for (ParserSyntax relation : relations) {
            result.append("\n   <").append(relation.name().toLowerCase()).append("> : ").append(relation.definition());
        }
        return result.toString();
    }

    /**
     * Affiche sur la console le message
     */
    public void display() {
        System.out.println("=>" + syntax);
        ;
    }
}
