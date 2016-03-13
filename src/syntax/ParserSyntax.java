/**
 * 
 */
package syntax;


/**
 * Syntax.java. <p>
 * @author  Vincent Cephirins
 * @version 1.0, 18 nov. 2010
 * <li>Creation</>
 */

public enum ParserSyntax {
        NOT_DEFINED(""),
        ALIAS("<expr> AS <ident>"),
        FOR("FOR [alias file:]<column> AS <alias> [TYPE <type> [flag]] [, ...]"),
        WITH("WITH <expr> AS <alias> [, ...]"), 
        DEFINE("DEFINE <ident> AS <alias> [TYPE <type> [flag]]"),
        COLUMNS("<expr> [AS <alias>] [, ...]"),
        METADATA("GENERATE METADATA <expr> [, ... ]"),
        FROM("FROM <filename> [AS <alias>] [WITH <ident> <string>] [, ...]", WITH), 
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
        COMMANDE("HELP, SET, SHOW, PRINT, SELECT, QUIT"),
        RACINE("COMMANDE", COMMANDE); 

        private String syntax;
        private ParserSyntax[] relations;

        ParserSyntax(String syntax, ParserSyntax... relations) {
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
         * @return la syntaxe avec toutes les définitions relatives
         */
        public String definition() {
        	StringBuilder result = new StringBuilder(50 * relations.length);
            result.append(syntax);
            for (ParserSyntax relation : relations) {
                result.append("\n   <").append(relation.name().toLowerCase()).append("> : ");
                result.append(relation.definition());
            }
            return result.toString();
        }

        /**
         * Affiche sur la console le message
         */
        public void display() {
            System.out.println("=>" + syntax);
        }
}
