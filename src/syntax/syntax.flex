package syntax;

%%

%byaccj
%unicode
%ignorecase

%line
%column

%{
  private Parser yyparser;
  private StringBuilder string = new StringBuilder();
  
  private int tokenpos = 0;
  private int numline = 1;
  private StringBuilder literal = new StringBuilder(200);
  
  private String nextToken = null;
  
	private void setTokenpos(int tokenpos) {
	    this.tokenpos = tokenpos;
	}
	
	public int getTokenpos() {
	    return tokenpos;
	}
	
	private void setNumline(int numline) {
	    this.numline = numline;
	}
	
	public int getNumline() {
	    return numline;
	}
	
	private void setLiteral(String literal) {
	    if (literal == null)
	       this.literal = new StringBuilder(200);
	    else
	       this.literal = new StringBuilder(literal);
	}
	
	public String getLiteral() {
	    return literal.toString();
	}

    public void addYytext(String yytext) {
        literal.append(yytext);
        nextToken = yytext; // Sauvegarde du token en cours
    }

    public void initCmd() {
       yyline=0;
       yycolumn=0;
       setNumline(0);
       setTokenpos(0);
       setLiteral(null);
       yyparser.getPile().popAll();
    }
    
  private void saveToken(String mot) {
     setNumline(yyline+1);
     setTokenpos(yycolumn+1);
     addYytext(mot);
     }
     
  private int token(int tok) {
     saveToken(yytext());
     return(tok);
     }
     
  private int token(int tok, ParserSyntax syntax) {
     yyparser.getPile().push(syntax);
     return(token(tok));
     }
     
  public ParserError yyerror(String error) {
       return new ParserError(getNumline(), getTokenpos(), getLiteral(), error);
  }
  
  public Yylex(java.io.Reader r, Parser yyparser) {
    this(r);
    this.yyparser = yyparser;
  }
  
%}

NL  = \n|\r|\r\n
BLANK = NL | [ \t\f]

/* comments */
CMT = {CommentC} | {CommentEol} | {CommentJava}

CommentC   = "/*" [^*] ~"*/" | "/*" "*"+ "/"
CommentEol     = "//" [^\r\n]* {NL}
CommentJava = "/**" {CommentContent} "*"+ "/"
CommentContent       = ( [^*] | \*+ [^/*] )*

/* data types */
IDENT = ([:jletter:][:jletterdigit:]*)
HEXA = (0[xX][a-fA-F0-9]+)
INTEGER = ([0-9]+)
LONG = ({INTEGER}[lL])
REAL = ((([0-9]+\.)|([0-9]*\.[0-9]+))([eE][-+]?[0-9]+)?)
FLOAT = (({INTEGER}|{REAL})[fF])
DATE_ISO_A = (\"[0-9]{4}[-/][0-9]{2}[-/][0-9]{2}([ Tt][0-9]{2}:[0-9]{2}:[0-9]{2}(\.[0-9]+)?Z?)?\")
DATE_ISO_B = (\"[0-9]{4}[-/][0-9]{3}([ Tt][0-9]{2}:[0-9]{2}:[0-9]{2}(\\.[0-9]+)?Z?)?\")

SEPARATED_BY = (separated[ \t]+by)

/* Fortran formats */
FFFMT = ([fF|eE|gG][0-9]+[\.][0-9]+)
FIFMT = ([iI][0-9]+)
FAFMT = ([aA][0-9]+)
FBLANK = ([0-9]+[xX])
FCTRL = ([tT][0-9]+)

/* States */
%s STRING, FORMAT, AFMT, BFMT

%%

<YYINITIAL> {

">="   {return token(Parser.GE);}
"<="   {return token(Parser.LE);}
"!="   {return token(Parser.NE);}
"=="   {return token(Parser.EQEQ);}

/* Exit command */
"quit" | "exit"         {return token(Parser.QUIT);}
"null"   				{return token(Parser.NULL); }
"trace"   				{return token(Parser.TRACE); }
"debug"   				{return token(Parser.DEBUG); }
"break_on_error"   		{return token(Parser.BREAK_ON_ERROR); }

/* Groupes principaux d'une commande */
"set"                  	{initCmd(); return token(Parser.SET, ParserSyntax.SET_ON);}
"show"              	{initCmd(); return token(Parser.SHOW, ParserSyntax.SHOW);}
"for"                   {initCmd(); return token(Parser.FOR, ParserSyntax.SELECT);}
"print"              	{initCmd(); return token(Parser.PRINT, ParserSyntax.PRINT);}

/* sous groupes avec syntaxe */
"as"                    {return token(Parser.AS, ParserSyntax.ALIAS);}
"with"              	{return token(Parser.WITH, ParserSyntax.WITH);}
"define"      			{return token(Parser.DEFINE, ParserSyntax.DEFINE);}
"group[ \t]+by" 		{return token(Parser.GROUP_BY, ParserSyntax.GROUP_BY);}
"from"              	{return token(Parser.FROM, ParserSyntax.FROM);}
"where"        			{return token(Parser.WHERE, ParserSyntax.WHERE);}

/* Sous groupes simples */
"having"      			{return token(Parser.HAVING);}
"type"              	{return token(Parser.TYPE_DEX);}
"and"                   {return token(Parser.AND);}
"or"                    {return token(Parser.OR);}
"not"                   {return token(Parser.NOT);}
"true"              	{return token(Parser.BOOL_TRUE);}
"false"          		{return token(Parser.BOOL_FALSE);}

"generate"  			{return token(Parser.GENERATE, ParserSyntax.GENERATE_METADATA);}
"metadata"  			{return token(Parser.METADATA);}

"break"         		{return token(Parser.BREAK);}
"on"                    {return token(Parser.ON);}
"off"                    {return token(Parser.OFF);}
"rowid"         		{return token(Parser.ROWID);}
"rownum"     			{return token(Parser.ROWNUM);}
"format"    			{yybegin(FORMAT); return token(Parser.FORMAT, ParserSyntax.FORMAT);}
}

<YYINITIAL> {

    /* Valeurs */
	{IDENT}   {
	    yyparser.yylval = new ParserVal(yytext());
	    return token(Parser.IDENT); 
	   }
	
	{HEXA}	{
	    yyparser.yylval = new ParserVal(yytext());
	    return token(Parser.HEXA); 
		}
	
	{INTEGER} {
	    yyparser.yylval = new ParserVal(yytext());
	    return token(Parser.INTEGER); 
		}
	
	{LONG} {
	    /* Suppression du Caractere [lL] en fin de chaine */ 
	    yyparser.yylval = new ParserVal(yytext().substring(0, yylength() - 1));
	    return token(Parser.LONG); 
		}
	
	{FLOAT} {
	    yyparser.yylval = new ParserVal(yytext());
	    return token(Parser.FLOAT); 
		}
	
	{REAL} {
	    yyparser.yylval = new ParserVal(yytext());
	    return token(Parser.DOUBLE); 
		}
	
	{DATE_ISO_A} {   
	    /* Suppression des guillemets de debut et fin */
	    String value = yytext().substring(1, yylength() - 1);
	    yyparser.yylval = new ParserVal(yytext());
	
		return token(Parser.DATE_ISO_A);
		}
	
	{DATE_ISO_B} {   
	    /* Suppression des guillemets de debut et fin */
	    String value = yytext().substring(1, yylength() - 1);
	    yyparser.yylval = new ParserVal(yytext());
	
		return token(Parser.DATE_ISO_B);
		}
		
	
	 \"                   { saveToken(yytext()); string.setLength(0); yybegin(STRING); }

}

<STRING> {
  \"                             { yybegin(YYINITIAL); 
                                   yyparser.yylval = new ParserVal(string.toString());
                                   return token(Parser.STRING); }
  [^\n\r\"\\]+                   { saveToken(yytext()); string.append( yytext() ); }
  \\t                            { saveToken(yytext()); string.append('\t'); }
  \\n                            { saveToken(yytext()); string.append('\n'); }
  \\r                            { saveToken(yytext()); string.append('\r'); }
  \\\"                           { saveToken(yytext()); string.append('\"'); }
  \\                             { saveToken(yytext()); string.append('\\'); }
}

<FORMAT> {
   "ascii"    		        {yybegin(AFMT); return token(Parser.ASCII);}
   "bin" | "binary"         {yybegin(BFMT); return token(Parser.BIN);}
}

<AFMT> {
   "as"            { return token(Parser.AS);}
   {FFFMT}|{FIFMT}|{FAFMT} { 
      /* format ascii fortran  */
      yyparser.yylval = new ParserVal(yytext());
      return token(Parser.FMTASCII);
      }
}

<BFMT> {
   "as"            { return token(Parser.AS);}
   {IDENT}   {
	    yyparser.yylval = new ParserVal(yytext());
	    return token(Parser.IDENT); 
	   }
}

<FORMAT, AFMT, BFMT> {
   {SEPARATED_BY}  { yybegin(YYINITIAL);return token(Parser.SEPARATED_BY);}
   ';'             { yybegin(YYINITIAL);return token(yycharat(0)); }
   <<EOF>>         { yybegin(YYINITIAL);return token(Parser.EOF); }
}

/* Ignore */
<YYINITIAL, FORMAT, AFMT, BFMT> {CMT}            {saveToken(""); /* Comment */}
<YYINITIAL, FORMAT, AFMT, BFMT> {BLANK}          {saveToken(yytext()); /* blank */}
<YYINITIAL, FORMAT, AFMT, BFMT> {NL}             {saveToken(yytext()); /* newline */}

/* default case */
. 			{ return token(yycharat(0)); }
<<EOF>>     { return token(Parser.EOF); }


