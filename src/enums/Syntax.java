/**
 * 
 */
package enums;


/**
 * Syntax.java. <p>
 * @author  Vincent Cephirins
 * @version 1.0, 18 nov. 2010
 * <li>Creation</>
 */

public enum Syntax {
        NOT_DEFINED(""),
        ALIAS("<expr> AS <ident>"),
        FOR("FOR [ident:]<ident> AS <ident> [TYPE <type> [flag]] [, ...]"),
        WITH("WITH <expr> AS <ident> [, ...]"), 
        DEFINE("DEFINE <ident> AS <ident> [TYPE <type> [flag]]"),
        COLUMNS("<expr> AS <ident> [, ...]"),
        METADATA("GENERATE METADATA <expr> [, ... ]"),
        FROM("FROM <string> [AS <ident>] [WITH <ident> <string>] [, ...]", WITH), 
        WHERE("WHERE <condition> [AND|OR <condition>] [, ...]"),
        GROUP_BY("GROUP BY <condition> [AND|OR <condition>] [, ...] HAVING <condition> [AND | OR <condition>] [, ...]"), 
        BREAK_ON("BREAK ON <condition> [AND|OR <condition>] [, ...]"),
        FFORTRAN("ex: A20, I4, F12.8, E6.3, 5X, t2 ..."),
        FMT_BINARY("FORMAT BINARY [AS <ident> [, ...]]"),
        FMT_ASCII("FORMAT ASCII [AS <ffortran> [, ...]] [SEPARATED BY <string>]", FFORTRAN),
        FORMAT("FORMAT <fmt_binary>|<fmt_ascii>", FMT_BINARY, FMT_ASCII),
        GENERATE_METADATA("FOR <columns> [with] [define] GENERATE METADATA <columns> <from> [where] [group by] [break on] [format]"), 
        SELECT("FOR <columns> [with] [define] SELECT <columns> <from> [where] [group by] [break on] [format] ;", COLUMNS, WITH, DEFINE, WHERE, GROUP_BY, BREAK_ON, FORMAT), 
        PRINT("PRINT <columns> [format] ;", COLUMNS, FORMAT), 
        SET_ON("SET [TRACE|DEBUG] [on|off]"), 
        SHOW("SHOW [TRACE|DEBUG] | <ident>"), 
        COMMANDE("HELP, SET, SHOW, PRINT, SELECT"); 

        private String syntax;
        private Syntax[] relations;

        Syntax(String syntax, Syntax... relations) {
            this.syntax = syntax;
            this.relations = relations;
        }

        /* (non-Javadoc)
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
            for (Syntax relation : relations) {
                result.append("\n   <").append(relation.name().toLowerCase()).append("> : ").append(relation.definition());
            }
            return result.toString();
        }

        /**
         * Affiche sur la console le message
         */
        public void display() {
            System.out.println("=>" + syntax);;
        }
}
