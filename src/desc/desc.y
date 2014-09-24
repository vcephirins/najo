/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Copyright (C) 2001 Gerwin Klein <lsf@jflex.de>                          *
 * All rights reserved.                                                    *
 *                                                                         *
 * This is a modified version of the example from                          *
 *   http://www.lincom-asg.com/~rjamison/byacc/                            *
 *                                                                         *
 * Thanks to Larry Bell and Bob Jamison for suggestions and comments.      *
 *                                                                         *
 * This program is free software; you can redistribute it and/or modify    *
 * it under the terms of the GNU General Public License. See the file      *
 * COPYRIGHT for more information.                                         *
 *                                                                         *
 * This program is distributed in the hope that it will be useful,         *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of          *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the           *
 * GNU General Public License for more details.                            *
 *                                                                         *
 * You should have received a copy of the GNU General Public License along *
 * with this program; if not, write to the Free Software Foundation, Inc., *
 * 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA                 *
 *                                                                         *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

%{
  import java.io.*;
  import java.lang.Integer;
  
  import desc.ParserError;  
  import desc.ParserSyntax;
  import desc.Desc;

  import desc.TypeDef;
  import desc.ListTypeDef;
  import desc.FileDef;
  import desc.LineDef;
  import desc.ListLineDef;
  import desc.ColDef;
  import desc.ListColDef;
  import desc.ColDef.EnumType;
  import desc.Option;
  import desc.Option.EnumOption;
  
%}

%token NL          /* newline  */

%token  TYPEDEF FILEDEF LINEDEF COLDEF
%token  AS REPEAT UNSIGNED SIZE TRUE FALSE
%token  EOF

%token   <sval> NUMBER IDENT HEXA SEPARATED_BY
%token   <sval> BIN ASCII G_BIN G_ASCII G_BIG_ENDIAN G_LITTLE_ENDIAN

%token   <ival> T_BOOL T_BYTE T_INTEGER T_FLOAT T_DOUBLE T_STRING T_DATE_ISO T_OBJECT

%start   racine

%type <obj> racine list_typedef typedef filedef list_linedef linedef list_coldef coldef
%type <obj> list_option_filedef option_filedef
%type <obj> list_option_linedef option_linedef
%type <obj> list_option_coldef option_coldef
%type <ival> repeat size number bool mode endian
%type <obj> type

%%

racine :
   list_typedef  /* optionnel */
   filedef       /* optionnel */
   list_linedef
   EOF  		{ desc = new Desc((ListTypeDef) $1, (FileDef) $2, (ListLineDef) $3); }
 | error { yyerror(""); }
 | error ';' { yyerror(""); }
;

list_typedef :
   /* empty */
   {$$ = null;}
 | typedef {
       ListTypeDef listTypeDef = new ListTypeDef(5);
       listTypeDef.add((TypeDef)$1);
       $$ = listTypeDef;} 
 | list_typedef typedef {((ListTypeDef)$1).add((TypeDef)$2); $$ = $1;}
;

typedef :
 TYPEDEF IDENT AS list_coldef {
       TypeDef typeDef = new TypeDef($2, (ListColDef) $4);
       $$ = typeDef;} 
;

filedef :
   /* empty */
   {$$ = null;}
 | FILEDEF IDENT list_option_filedef {
       FileDef fileDef = new FileDef($2, (ListOption) $3);
       $$ = fileDef;} 
;

list_linedef :
 linedef {
       ListLineDef listLineDef = new ListLineDef(5);
       listLineDef.add((LineDef)$1);
       $$ = listLineDef;} 
 | list_linedef linedef {((ListLineDef)$1).add((LineDef)$2); $$ = $1;}
;

linedef :
   LINEDEF IDENT list_option_linedef list_coldef {
       LineDef lineDef = new LineDef($2, (ListOption) $3, (ListColDef) $4);
       $$ = lineDef;} 
;
  
list_coldef :
 coldef {
       ListColDef listColDef = new ListColDef(20);
       listColDef.add((ColDef)$1);
       $$ = listColDef;} 
 | list_coldef coldef {((ListColDef)$1).add((ColDef)$2); $$ = $1;}
;

coldef :
   COLDEF IDENT AS IDENT repeat {
/*       ColDef colDef = new ColDef((String)$2, (EnumType)$3, (ListOption) $4);
       $$ = colDef;*/}
 | COLDEF IDENT type list_option_coldef {
       ColDef colDef = new ColDef((String)$2, (EnumType)$3, (ListOption) $4);
       $$ = colDef;}
;
  
list_option_filedef :
   /* empty */
     {$$ = null;}
 | option_filedef {
       ListOption listOptionFileDef = new ListOption(5);
       listOptionFileDef.add((Option)$1);
       $$ = listOptionFileDef;} 
 | list_option_filedef option_filedef {((ListOption)$1).add((Option)$2); $$ = $1;}
 
 option_filedef :
   mode {$$ = new Option(EnumOption.MODE, $1);}
 | endian {$$ = new Option(EnumOption.ENDIAN, $1);}
;

list_option_linedef :
   /* empty */
     {$$ = null;}
 | option_linedef {
       ListOption listOptionLineDef = new ListOption(5);
       listOptionLineDef.add((Option)$1);
       $$ = listOptionLineDef;} 
 | list_option_linedef option_linedef {((ListOption)$1).add((Option)$2); $$ = $1;}
 
 option_linedef :
   mode {$$ = new Option(EnumOption.MODE, $1);}
 | endian {$$ = new Option(EnumOption.ENDIAN, $1);}
;

list_option_coldef :
   /* empty */
     {$$ = null;}
 | option_coldef {
       ListOption listOptionColDef = new ListOption(5);
       listOptionColDef.add((Option)$1);
       $$ = listOptionColDef;} 
 | list_option_coldef option_coldef {((ListOption)$1).add((Option)$2); $$ = $1;}
 
 option_coldef :
   mode {$$ = new Option(EnumOption.MODE, $1);}
 | endian {$$ = new Option(EnumOption.ENDIAN, $1);}
 | repeat {$$ = new Option(EnumOption.REPEAT, $1);}
 | UNSIGNED {$$ = new Option(EnumOption.UNSIGNED, 1);}
;

type :
 T_BOOL  {$$ = EnumType.BOOL;}
 | T_BYTE   {$$ = EnumType.BYTE;}
 | T_INTEGER size {$$ = EnumType.INTEGER;}
 | T_FLOAT {$$ = EnumType.FLOAT;}
 | T_DOUBLE {$$ = EnumType.DOUBLE;}
 | T_STRING size {$$ = EnumType.STRING;}
 | T_DATE_ISO {$$ = EnumType.DATE_ISO;}
 | T_OBJECT size {$$ = EnumType.OBJECT;}
;

mode : 
   G_BIN {$$ = 0;}
 | G_ASCII {$$ = 1;}
;

endian : 
   G_BIG_ENDIAN {$$ = 0;}
 | G_LITTLE_ENDIAN {$$ = 1;}
;

size :
   /* empty */
     {$$ = 4;}
 | SIZE number
     {$$ = $2;}
     
repeat :
   /* empty */
     {$$ = 1;}
 | REPEAT number  {$$ = $2;}
;

number :
   NUMBER {$$ = Integer.parseInt($1); }
;

bool :
   TRUE  {$$ = 1; }
 | FALSE {$$ = 0; }
;

%%

  private Yylex lexer;
  private ParserError parserError;
  private Desc desc;
  
  private int yylex () {
    int yyl_return = -1;
    try {
      // yylval = new ParserVal(0);
      yyl_return = lexer.yylex();
    }
    catch (IOException e) {
      System.err.println("IO error :"+e);
    }
    return yyl_return;
  }

  public Parser(Reader r) throws Exception {
    lexer = new Yylex(r, this);
  }

  public static void compile(Reader r) throws Exception {
  
    Parser yyparser;
    
    // parse a file
    yyparser = new Parser(r);
    // yyparser.yydebug = true;
    yyparser.yyparse();
   
  }

  public void yyerror (String error) {
        parserError = new ParserError(lexer.getNumline(), lexer.getTokenpos(), lexer.getLiteral(), error);
  }

  public Desc getDesc() {
     return desc;
  }
  
  public ParserError getParserError() {
     return parserError;
  }