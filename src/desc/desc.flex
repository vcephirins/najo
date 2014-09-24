package desc;

%%

%byaccj
%unicode
%ignorecase

%line
%column

%{
  private Parser yyparser;

  // Variables de contexte  
  private int tokenpos = 0;
  private int numline = 1;
  private StringBuffer literal = new StringBuffer(200);
  private String nextToken;
  
  private void saveToken() {
     // Sauvegarde du contexte
     numline = yyline+1;
     tokenpos = yycolumn+1;
     nextToken = yytext();   // Sauvegarde du token en cours
     literal.append(nextToken);
     }

  private int token(int tok) {
     saveToken();

     return(tok);
     }

  int getNumline() {
     return numline;
  }
          
  int getTokenpos() {
     return tokenpos;
  }
          
  String getLiteral() {
     return literal.toString();
  }
          
  public Yylex(java.io.Reader r, Parser yyparser) {
    this(r);
    this.yyparser = yyparser;
  }
%}

NL  = \n|\r|\r\n
BLANK = NL | [ \t\f]

/* comments */
CMT = {CommentC} | {CommentEol}

CommentC   = "/*" [^*] ~"*/" | "/*" "*"+ "/"
CommentEol     = "(//)|#" [^\r\n]* {NL}

/* data types */
IDENT = ([:jletter:][:jletterdigit:]*)
HEXA = (0[xX][a-fA-F0-9]+)
NUMBER = ([0-9]+)

SEPARATED_BY = "(separated[ \t]+by)"

%%

<YYINITIAL> {

	/* Commandes */
	"typedef"               { return token(Parser.TYPEDEF); }	
	"filedef"               { return token(Parser.FILEDEF); }	
	"linedef"               { return token(Parser.LINEDEF); }	
	"coldef"                { return token(Parser.COLDEF); }	
	
	/* generales */
	"bin" | "binary"        { return token(Parser.G_BIN); }
	"ascii"                 { return token(Parser.G_ASCII); }
	"big_endian"            { return token(Parser.G_BIG_ENDIAN); }
	"little_endian"         { return token(Parser.G_LITTLE_ENDIAN); }
		
	/* col types*/
	"bool"                  { return token(Parser.T_BOOL); }	
	"byte"                  { return token(Parser.T_BYTE); }	
	"integer"               { return token(Parser.T_INTEGER); }	
	"float"                 { return token(Parser.T_FLOAT); }	
	"double"                { return token(Parser.T_DOUBLE); }	
	"string"                { return token(Parser.T_STRING); }
	"date_iso"            { return token(Parser.T_DATE_ISO); }
	"object"                { return token(Parser.T_OBJECT); }
		
	/* Options autres */
	"as"                    { return token(Parser.AS); }	
	"repeat"                { return token(Parser.REPEAT); }	
	"unsigned"              { return token(Parser.UNSIGNED);}

    /* Valeurs */
	"true"              	{return token(Parser.TRUE);}
	"false"          		{return token(Parser.FALSE);}
	"on"                	{return token(Parser.TRUE);}
	"off"           		{return token(Parser.FALSE);}

	{IDENT}   {
	    yyparser.yylval = new ParserVal(yytext());
	    return token(Parser.IDENT); 
	   }
	
	{HEXA}	{
	    yyparser.yylval = new ParserVal(yytext());
	    return token(Parser.HEXA); 
		}
	
	{NUMBER} {
	    yyparser.yylval = new ParserVal(yytext());
	    return token(Parser.NUMBER); 
		}

	{SEPARATED_BY} {
	    yyparser.yylval = new ParserVal(yytext());
	    return token(Parser.SEPARATED_BY);
		}

    /* Ignore */
    {CMT}            {saveToken(); /* Comment */}
    {BLANK}          {saveToken(); /* blank */}
    {NL}             {saveToken(); /* newline */}

}

/* default case */
. 			{ return token(yycharat(0)); }
<<EOF>>     { return token(Parser.EOF); }


