%{ 
   #include "dex.h" 
   #include "y.tab.h" 

   #ifdef ECHO
   #undef ECHO
   #endif

   #define ECHO {if(gpcDebug) dexLogTrace(1,"%s",yytext);}
   #define POS {tokenpos += yyleng;}

   static int bAnalyseDemarre = 1;   /* Positionne l'etat de demarrage de l'analyse */
%}

STRING (\"[^\"\n]*[\"\n])
IDENT ([a-zA-Z_][a-zA-Z0-9_\.]*)
HEXA (0[xX][a-fA-F0-9]+)
NUMBER ([0-9]+)
REAL ((([0-9]+\.)|([0-9]*\.[0-9]+))([eE][-+]?[0-9]+)?)

DATE_ISO_A (\"(([0-9]{4})[-/]([0-9]{2})[-/]([0-9]{2}))([ Tt]([0-9]{2}):([0-9]{2}):([0-9]{2})(\.[0-9]+)?Z?)?\")
DATE_ISO_B (\"(([0-9]{4})[-/]([0-9]{3}))[ Tt](([0-9]{2}):([0-9]{2}):([0-9]{2})(\.[0-9]+)?Z?)\")

FFFMT ([fF|eE|gG][0-9]+[\.][0-9]+)
FIFMT ([iI][0-9]+)
FAFMT ([aA][0-9]+)
FBLANK ([0-9]+[xX])
FCTRL ([tT][0-9]+)

%s DEMARRE
%s FMT
%s AFMT
%s BFMT
%s SFMT

%a 8000     /* number of transitions */
%p 10000    /* number of positions */
%o 10000    /* size of the output array */

%%

%{
   if(bAnalyseDemarre) {
      BEGIN DEMARRE;
      bAnalyseDemarre = 0;
      }
%}

<DEMARRE>^.*$   {
      numligne = 1;tokenpos = 0;  /* Initialisation */
      ECHO;                   /* Affiche la premiere ligne */
      BEGIN INITIAL;          /* Passe en mode normal */
      yyless(0);              /* Démarre le traitement */
      };

\n.*            {numligne++;tokenpos = 0; /* re-init de la position du token */
                 ECHO;yyless(1); /* Affiche la ligne avant traitement */};

">="			{POS;return(SUPEQUAL);};
"<="			{POS;return(INFEQUAL);};
"!="			{POS;return(NOTEQUAL);};
"=="			{POS;return(EQUAL);};

[sS][eE][tT]					{POS;return(SET);};
[sS][sH][oO][wW]				{POS;return(SHOW);};

[fF][oO][rR]					{POS;return(FOR);};
[aA][sS]						{POS;return(AS);};
[tT][yY][pP][eE]				{POS;return(TYPE_DEX);};
[wW][iI][tT][hH]				{POS;return(WITH);}; 
[dD][eE][fF][iI][nN][eE]		{POS;return(DEFINE);}; 
[lL][iI][sS][tT][eE]1[dD]		{POS;return(LISTE_1D);}; 
[lL][iI][sS][tT][eE]2[dD]		{POS;return(LISTE_2D);}; 
[lL][iI][sS][tT][eE]3[dD]		{POS;return(LISTE_3D);}; 
[gG][rR][oO][uU][pP][ \t]+[bB][yY] {POS;return(GROUP_BY);}; 
[hH][aA][vV][iI][nN][gG]		{POS;return(HAVING);}; 
[sS][eE][lL][eE][cC][tT] 		{POS;return(SELECT);}; 
[wW][hH][eE][rR][eE] 			{POS;return(WHERE);}; 
[aA][nN][dD]					{POS;return(AND);};
[oO][rR]			            {POS;return(OR);};
[nN][oO][tT]					{POS;return(NOT);};
[tT][rR][uU][eE]				{POS;return(BOOL_TRUE);};
[fF][aA][lL][sS][eE]			{POS;return(BOOL_FALSE);};
[fF][rR][oO][mM]				{POS;return(FROM);};

[gG][eE][nN][eE][rR][aA][tT][eE]  {POS;return(GENERATE);};
[mM][eE][tT][aA][dD][aA][tT][aA]  {POS;return(METADATA);};

[bB][rR][eE][aA][kK]			{POS;return(BREAK);};
[oO][nN]			            {POS;return(ON);};
[rR][oO][wW][iI][dD]			{POS;return(ROWID);};
[rR][oO][wW][nN][uU][mM]		{POS;return(ROWNUM);};

[fF][oO][rR][mM][aA][tT]		{BEGIN FMT; POS;return(FORMAT);};

<FMT>[aA][sS][cC][iI][iI]		{BEGIN AFMT;  POS;return(ASCII_DEX);};
<AFMT>{FFFMT}|{FIFMT}|{FAFMT} { 
   /* format ascii fortran  */
   if(yyleng > kiFMT) {yynerrs++;yyerrorDex(150);}
	else strcpy (yylval.acFmt,yytext);
	POS;return (FMTASCII);
	};


<AFMT>[sS][eE][pP][aA][rR][aA][tT][eE][dD][ \t]+[bB][yY]	{BEGIN INITIAL; POS;return(SEPARATED_BY);};
<AFMT>"<<EOF>>" {BEGIN INITIAL;};

<FMT>[bB][iI][nN]([aA][rR][yY])?   {BEGIN BFMT; POS;return(BIN);};
<BFMT>"<<EOF>>" {BEGIN INITIAL;};


{IDENT}	{
   if(yyleng > kiIDENT) {yynerrs++;yyerrorDex(151);}
	else strcpy (yylval.acIdent,yytext);
	POS;return (IDENT);
	};

{HEXA}	{
   if(yyleng > kiSTRING) {yynerrs++;yyerrorDex(153);}
	else strcpy (yylval.acString,yytext);
	POS;return (HEXA);
	};

{NUMBER} {
   if(yyleng > kiNOMBRE) {yynerrs++;yyerrorDex(152);}
	else strcpy (yylval.acNombre,yytext);
	POS;return ((yyleng > 9)? DOUBLE : INTEGER);
	};

{REAL} {
   if(yyleng > kiNOMBRE) {yynerrs++;yyerrorDex(152);}
	else strcpy (yylval.acNombre,yytext);
	POS;return (DOUBLE);
	};

{DATE_ISO_A} {   
   if((yyleng - 2) > kiSTRING) {yynerrs++;yyerrorDex(153);}
	else strcpy (yylval.acString, yytext + 1);

   /* Suppression des guillemets de debut et fin */
   if(yylval.acString[yyleng - 2] != '"') {yynerrs++;yyerrorDex(154);}
   else  yylval.acString[yyleng - 2] = '\0';

	POS;return (DATE_ISO_A);
	};

{DATE_ISO_B} {   
   if((yyleng - 2) > kiSTRING) {yynerrs++;yyerrorDex(153);}
	else strcpy (yylval.acString, yytext + 1);

   /* Suppression des guillemets de debut et fin */
   if(yylval.acString[yyleng - 2] != '"') {yynerrs++;yyerrorDex(154);}
   else  yylval.acString[yyleng - 2] = '\0';

	POS;return (DATE_ISO_B);
	};

{STRING}  {
   if(yytext[yyleng - 2] == '\\') {
      /* caractere d'echappement */
      yyless(yyleng - 1);  /* renvoie le dernier guillemet */
      yymore();            /* concatene la suite */
   }
   else {
      char *pStr = NULL;

      if((yyleng - 2) > kiSTRING) {yynerrs++;yyerrorDex(153);}
      else strcpy (yylval.acString, yytext + 1);

      /* Suppression des guillemets de debut et fin */
      if(yylval.acString[yyleng - 2] != '"') {yynerrs++;yyerrorDex(154);}
      else yylval.acString[yyleng - 2] = '\0';

      /* Suppression des caracteres d'echapements */
      pStr = yylval.acString;
      while(*pStr){if(*pStr == '\\') strcpy(pStr, pStr + 1); pStr++;}

      POS;return (STRING);
      }
   };

[ \t\f]+		{POS; /* Ignore blanc */}
\/\/.*     		{POS; /* Ignore Commentaire */}


.				{POS;return (yytext[0]);};

%%
