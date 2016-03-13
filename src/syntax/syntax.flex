package syntax;

import org.najo.Najo;

%%

%byaccj
%unicode
%ignorecase

%line
%column

%{
  private Parser yyparser;
  private StringBuilder string = new StringBuilder();
  
  private void saveToken() {
     yyparser.setNumline(yyline+1);
     yyparser.setTokenpos(yycolumn+1);
     yyparser.addYytext(yytext());
     }
     
  private int token(int tok) {
     saveToken();
     return(tok);
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

"set"                  	{return token(Parser.SET);}
"show"              	{return token(Parser.SHOW);}
"print"              	{yyparser.setParserSyntax(ParserSyntax.PRINT); return token(Parser.PRINT);}

"for"                   {return token(Parser.FOR);}
"as"                    {yyparser.setParserSyntax(ParserSyntax.ALIAS); return token(Parser.AS);}
"type"              	{return token(Parser.TYPE_DEX);}
"with"              	{return token(Parser.WITH);}
"define"      			{return token(Parser.DEFINE);}
"group[ \t]+by" 		{return token(Parser.GROUP_BY);}
"having"      			{return token(Parser.HAVING);}
"select"     			{return token(Parser.SELECT);}
"where"        			{return token(Parser.WHERE);}
"and"                   {return token(Parser.AND);}
"or"                    {return token(Parser.OR);}
"not"                   {return token(Parser.NOT);}
"true"              	{return token(Parser.BOOL_TRUE);}
"false"          		{return token(Parser.BOOL_FALSE);}
"from"              	{return token(Parser.FROM);}

"generate"  			{return token(Parser.GENERATE);}
"metadata"  			{return token(Parser.METADATA);}

"break"         		{return token(Parser.BREAK);}
"on"                    {return token(Parser.ON);}
"off"                    {return token(Parser.OFF);}
"rowid"         		{return token(Parser.ROWID);}
"rownum"     			{return token(Parser.ROWNUM);}
"format"    			{yybegin(FORMAT); return token(Parser.FORMAT);}
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
		
	
	 \"                   { saveToken(); string.setLength(0); yybegin(STRING); }

}

<STRING> {
  \"                             { yybegin(YYINITIAL); 
                                   yyparser.yylval = new ParserVal(string.toString());
                                   return token(Parser.STRING); }
  [^\n\r\"\\]+                   { saveToken(); string.append( yytext() ); }
  \\t                            { saveToken(); string.append('\t'); }
  \\n                            { saveToken(); string.append('\n'); }
  \\r                            { saveToken(); string.append('\r'); }
  \\\"                           { saveToken(); string.append('\"'); }
  \\                             { saveToken(); string.append('\\'); }
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
<YYINITIAL, FORMAT, AFMT, BFMT> {CMT}            {saveToken(); /* Comment */}
<YYINITIAL, FORMAT, AFMT, BFMT> {BLANK}          {saveToken(); /* blank */}
<YYINITIAL, FORMAT, AFMT, BFMT> {NL}             {saveToken(); /* newline */}

/* default case */
. 			{ return token(yycharat(0)); }
<<EOF>>     { return token(Parser.EOF); }


