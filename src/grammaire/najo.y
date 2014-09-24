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
  
  import enums.Syntax;
  import enums.TypeNode;
  import enums.TypeValue;
  import enums.TypeMath;
  import enums.TypeCond;

  import org.najo.Najo;
  import org.najo.Nodes.INode;
  import org.najo.Nodes.Node;
  import org.najo.Nodes.NodeAlias;
  import org.najo.Nodes.NodeValue;
  import org.najo.Nodes.NodeMath;
  import org.najo.Nodes.NodeCond;
  import org.najo.Nodes.ListNodes;
  
%}

%token NL          /* newline  */
%token QUIT        /* exit  */

%token   <sval> INTEGER LONG DOUBLE FLOAT HEXA
%token   <sval> IDENT STRING DATE_ISO_A DATE_ISO_B
%token   <sval> FMTASCII
%token   <sval> BOOL_TRUE BOOL_FALSE ON OFF NULL ERROR 

%token  SET PRINT FOR AS WITH DEFINE SELECT WHERE
%token  FROM GROUP_BY HAVING BREAK ROWID ROWNUM
%token  FORMAT BIN ASCII SEPARATED_BY
%token  EQUAL LE GE NE EQEQ AND OR NOT
%token  TRACE DEBUG

%token  GENERATE METADATA EOF
%token  HELP SET SHOW TYPES_ALL TYPE_DEX LIBRARIES LIBRARIE FUNCTIONS FUNCTION

%token   '<' '>' ',' '+' '-' '*' '/' '^' '(' ')' ';' '%'
	
%left OR
%left AND
%left '<' '>' LE GE NE EQEQ 
%left '-' '+'
%left   '*' '/' '%'
%right '^'         /* exponentiation        */
%left NOT
%nonassoc  MUNAIRE

%start   list_commande

%type <obj> _error list_commande commande
%type <obj> set show print request quit
%type <obj> for select
%type <obj> ident number string date hexa bool null on_off
%type <obj> opt_with opt_define opt_where opt_group_by opt_break_on opt_format
%type <obj> list_file file
%type <obj> list_alias alias list_expr expr expr_cond  
%type <obj> interval list_interval
%type <obj> fmt_ascii fmt_bin list_fmt_ascii list_fmt_bin list_fmt
%type <obj> function value 
%type <obj> quit 

%%

list_commande :
 commande {najo.showError(); najo.newRequest();} 
 | list_commande commande {najo.showError(); najo.newRequest();}
;

commande :
   set      		{if (najo.isTrace()) ((Node)$1).display(1);}
 | set ';'          {if (najo.isTrace()) ((Node)$1).display(1);}
 | show             {if (najo.isTrace()) ((Node)$1).display(1);}
 | show ';'         {if (najo.isTrace()) ((Node)$1).display(1);}
 | print     		{
 	Node print = new Node(TypeNode.PRINT, (ListNodes)$1);
 	if (najo.isTrace()) print.display(1); 
 	najo.stackRequest(print);
 	najo.showError();
 	najo.execute();
 	}
 | request  		{
 	Node request = new Node(TypeNode.REQUEST, (ListNodes)$1);
 	if (najo.isTrace()) request.display(1); 
 	najo.stackRequest(request);
 	najo.showError();
 	najo.execute();
 	}
 | quit             {najo.bye(0);}
 | error { najo.setError(""); }
 | error ';' {}
  
set :
   SET TRACE on_off { $$ = $3; najo.setTrace((Boolean) najo.getValue((Node) $3));}
 | SET DEBUG on_off { yydebug = true; $$ = $3;}
 | SET error on_off { najo.setError(Syntax.SET_ON.toString()); }
;

show :
   SHOW TRACE { $$ = 0;}
 | SHOW DEBUG { $$ = 0;}
 | SHOW error { najo.setError(Syntax.SHOW.toString()); }
;

quit :
   EOF {}
 | QUIT {}
 | QUIT ';' {}
;

print :
   PRINT list_expr 
   opt_format                   /* optionnel */
   ';' 
      {ListNodes list = new ListNodes("print", 2);
       list.add(new Node(TypeNode.LIST_COLUMN, (ListNodes)$2));
       list.add((Node)$3);
       $$ = list;}
 | PRINT error { najo.setError(Syntax.PRINT.definition()); $$ = new ListNodes("print", 0); }
;

request :
   for
   select
   opt_format                   /* optionnel */
   ';' 
      {ListNodes list = new ListNodes("request", 3);
       list.add(new Node(TypeNode.FOR, (ListNodes)$1));
       list.add(new Node(TypeNode.SELECT, (ListNodes)$2));
       list.add((Node)$3);
       $$ = list;}
 ;

for :
   FOR list_alias  
   opt_with                    /* optionnel */
   opt_define ';'              /* optionnel */
      {ListNodes list = new ListNodes("for", 3);
       list.add(new Node(TypeNode.LIST_ALIAS, (ListNodes)$2));
       list.add((Node)$3);
       list.add((Node)$4);
       $$ = list;}
;

opt_with :
   /* empty */
     {$$ = INode.NODE_NULL;}
 | WITH list_alias
     {$$ = new Node(TypeNode.WITH, (ListNodes)$2);}
;

opt_define :
   /* empty */
     {$$ = INode.NODE_NULL;}
 | DEFINE list_interval
      {$$ = new Node(TypeNode.DEFINE, (ListNodes)$2);}
;
	   
select	: 
   /* Syntaxe obligatoire : SELECT ... FROM ... WHERE */
   SELECT list_expr
   FROM list_file
   opt_where                    /* optionnel */
   opt_group_by                 /* optionnel */ 
   opt_break_on                 /* optionnel */
      {ListNodes list = new ListNodes("for", 3);
       list.add(new Node(TypeNode.LIST_COLUMN, (ListNodes)$2));
       list.add(new Node(TypeNode.FROM, (ListNodes)$4));
       list.add((Node)$5);
       list.add((Node)$6);
       list.add((Node)$7);
       $$ = list;}
 ;

opt_where :
   /* empty */
      {$$ = INode.NODE_NULL;}
 | WHERE expr_cond
      {$$ = new Node(TypeNode.WHERE,"where",(Node)$2);}
;
	   
opt_group_by :
   /* empty */
     {$$ = INode.NODE_NULL;}
 | GROUP_BY expr_cond HAVING expr_cond
     {$$ = new Node(TypeNode.GROUP_BY,"group_by",(Node)$2,(Node)$4); }
;
	   
opt_break_on :
   /* empty */
     {$$ = INode.NODE_NULL;}
 | BREAK ON expr_cond
     {$$ = new Node(TypeNode.BREAK_ON,"break_on",(Node)$3);}
;
	   
opt_format :
   /* empty */
     {$$ = INode.NODE_NULL;}
 | FORMAT list_fmt
     {$$ = new Node(TypeNode.FORMAT, "format", (Node)$2);}
 | FORMAT error { najo.setError(Syntax.FORMAT.toString()); $$ = INode.NODE_NULL; }
;

list_file :
   file 
      {ListNodes list = new ListNodes("list", 20);
       list.add((Node)$1);
       $$ = list;}
 | list_file ',' file 
      {((ListNodes)$1).add((Node)$3); $$ = $1;}
;

file :
   string {$$ = new Node(TypeNode.FILE,"file",(Node)$1);}
 | string AS IDENT {$$ = new Node(TypeNode.ALIAS_FILE,$3,(Node)$1);}
 | string WITH IDENT string 
    {$$ = new Node(TypeNode.FILE,"file",(Node)$1,
             new Node(TypeNode.TYPE_FILE,$3,(Node)$4));}
 | string AS IDENT WITH IDENT string 
    {$$ = new Node(TypeNode.ALIAS_FILE,$3,(Node)$1,
             new Node(TypeNode.TYPE_FILE, $5,(Node)$6));}
;
	   
list_interval : 
   interval 
      {ListNodes list = new ListNodes("list", 20);
       list.add((Node)$1);
        $$ = list;}
 | list_interval ',' interval 
      {((ListNodes)$1).add((Node)$3);
       $$ = $1;} 
;

interval : 
   '[' expr ',' expr ']'
      {$$ = new Node(TypeNode.INTERVAL,"interval",(Node)$2,(Node)$4);} 
;

list_fmt: 
   ASCII {$$ = new Node(TypeNode.LIST_FMT_ASCII,"default");}
 | ASCII SEPARATED_BY STRING {$$ = new Node(TypeNode.LIST_FMT_ASCII_SEP,$3);}
 | ASCII AS list_fmt_ascii {$$ = new Node(TypeNode.LIST_FMT_ASCII, (ListNodes)$3);}
 | ASCII AS list_fmt_ascii SEPARATED_BY STRING 
      {$$ = new Node(TypeNode.LIST_FMT_ASCII_SEP, $5, 
               new Node(TypeNode.LIST_FMT_ASCII, (ListNodes)$3));}
 | BIN {$$ = new Node(TypeNode.LIST_FMT_BIN,"default");}
 | BIN AS list_fmt_bin {$$ = new Node(TypeNode.LIST_FMT_BIN, (ListNodes)$3);}
;

list_fmt_ascii: 
   fmt_ascii 
      {ListNodes list = new ListNodes("list", 20);
       list.add((Node)$1);
       $$ = list;}
 | list_fmt_ascii ',' fmt_ascii 
      {((ListNodes)$1).add((Node)$3);
       $$ = $1;}
;

fmt_ascii: 
   /* empty : le node doit etre creer pour eviter une fin de liste */
      {$$ = INode.NODE_NULL;} 
 | FMTASCII {$$ = new Node(TypeNode.FMT_ASCII,$1);} 
 | error { najo.setError(Syntax.FFORTRAN.toString()); $$ = INode.NODE_NULL; }
;

list_fmt_bin: 
   fmt_bin 
      {ListNodes list = new ListNodes("list", 20); 
       list.add((Node)$1); 
       $$ = list;}
 | list_fmt_bin ',' fmt_bin 
      {((ListNodes)$1).add((Node)$3);
       $$ = $1;}
;

fmt_bin: 
   /* empty : le node doit etre creer pour eviter une fin de liste */
      {$$ = INode.NODE_NULL;} 
 | IDENT {$$ = new Node(TypeNode.FMT_BIN,$1);} 
;

function :  
   IDENT '(' list_expr ')' 
      {$$ = new Node(TypeNode.FUNCTION, $1, 
          new Node(TypeNode.LIST_EXPR, (ListNodes)$3));}
;

list_expr : 
   expr      {ListNodes list = new ListNodes("list", 20); list.add((Node)$1); $$ = list;} 
 | expr_cond {ListNodes list = new ListNodes("list", 20); list.add((Node)$1); $$ = list;} 
 | alias       {ListNodes list = new ListNodes("list", 20); list.add((Node)$1); $$ = list;} 
 | list_expr ',' expr      {((ListNodes)$1).add((Node)$3); $$ = $1;}
 | list_expr ',' expr_cond {((ListNodes)$1).add((Node)$3); $$ = $1;}
 | list_expr ',' alias       {((ListNodes)$1).add((Node)$3); $$ = $1;}
;

list_alias : 
   alias 
      {ListNodes list = new ListNodes("list", 20);
       list.add((Node)$1);
       $$ = list;} 
 | list_alias ',' alias {((ListNodes)$1).add((Node)$3); $$ = $1;}
;

alias : 
   expr AS IDENT      {$$ = new NodeAlias(TypeNode.ALIAS, $3, (Node)$1);} 
 | expr_cond AS IDENT {$$ = new NodeAlias(TypeNode.ALIAS, $3, (Node)$1);} 
;

expr_cond : 
   NOT expr_cond {$$ = new NodeCond(TypeCond.NOT,(Node)$2);}
 | expr_cond OR expr_cond  {$$ = new NodeCond(TypeCond.OR,(Node) $1, (Node) $3);}
 | expr_cond AND expr_cond {$$ = new NodeCond(TypeCond.AND,(Node) $1, (Node) $3);}
 | expr '<' expr  {$$ = new NodeCond(TypeCond.LESS,(Node) $1, (Node) $3);}
 | expr '>' expr  {$$ = new NodeCond(TypeCond.SUP,(Node) $1, (Node) $3);}
 | expr NE expr   {$$ = new NodeCond(TypeCond.NE, (Node) $1, (Node) $3);}
 | expr EQEQ expr {$$ = new NodeCond(TypeCond.EQEQ, (Node) $1, (Node) $3);}
 | expr LE expr   {$$ = new NodeCond(TypeCond.LE, (Node) $1, (Node) $3);}
 | expr GE expr   {$$ = new NodeCond(TypeCond.GE, (Node) $1, (Node) $3);}
 | '(' expr_cond ')' {$$ = $2;}
;

expr :
   value    {$$ = $1;}
 | function {$$ = $1;}
 | ROWID    {$$ = new Node(TypeNode.ROWID, "rowid");}
 | ROWNUM   {$$ = new Node(TypeNode.ROWNUM, "rownum");}
 | expr '+' expr {$$ = new NodeMath(TypeMath.PLUS, (Node) $1, (Node) $3);}
 | expr '-' expr {$$ = new NodeMath(TypeMath.MINUS,(Node) $1, (Node) $3);}
 | expr '*' expr {$$ = new NodeMath(TypeMath.MULTIPLE, (Node) $1, (Node) $3);}
 | expr '/' expr {$$ = new NodeMath(TypeMath.DIVISE, (Node) $1, (Node) $3);}
 | expr '^' expr {$$ = new NodeMath(TypeMath.EXPONENT, (Node) $1, (Node) $3);}
 | expr '%' expr {$$ = new NodeMath(TypeMath.MODULO, (Node) $1, (Node) $3);}
 | '-' expr %prec MUNAIRE {$$ = new NodeMath(TypeMath.MUNAIRE, (Node)$2);}
 | '(' expr ')' {$$ = $2;}
;

value :
   null   {$$ = $1;}
 | number {$$ = $1;}
 | ident  {$$ = $1;}
 | string {$$ = $1;}
 | date   {$$ = $1;}
 | bool   {$$ = $1;}
 | hexa   {$$ = $1;}
 | _error {$$ = $1;}
;

ident :
   IDENT {$$ = new Node(TypeNode.IDENT,$1);}
;

number :
   INTEGER {$$ = new NodeValue(TypeValue.INTEGER,$1); }
 | LONG    {$$ = new NodeValue(TypeValue.LONG,$1); }
 | FLOAT   {$$ = new NodeValue(TypeValue.FLOAT,$1); }
 | DOUBLE  {$$ = new NodeValue(TypeValue.DOUBLE,$1); }
;

string :
   STRING {$$ = new NodeValue(TypeValue.STRING,$1); }
;

date :
   DATE_ISO_A {$$ = new NodeValue(TypeValue.DATE,$1); }
 | DATE_ISO_B {$$ = new NodeValue(TypeValue.DATE,$1); }
;

hexa :
   HEXA {$$ = new NodeValue(TypeValue.HEXA,$1); }
;

bool :
   BOOL_TRUE  {$$ = new NodeValue(TypeValue.BOOL,"true"); }
 | BOOL_FALSE {$$ = new NodeValue(TypeValue.BOOL,"false"); }
;

on_off :
   ON  {$$ = new NodeValue(TypeValue.ON_OFF,"on"); }
 | OFF {$$ = new NodeValue(TypeValue.ON_OFF,"off"); }
;

_error :
   ERROR {$$ = new NodeValue(TypeValue.ERROR,$1); }
;

null :
   NULL {$$ = INode.NODE_NULL;}
;

%%

  private Yylex lexer;
  private Najo najo;
  
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


  public void yyerror (String error) {
     najo.setError(error);
  }


  public Parser(Najo najo, Reader r) throws Exception {
    lexer = new Yylex(najo, r, this);
    this.najo = najo;
  }

  public static void compile(Najo najo, Reader r) throws Exception {
  
    Parser yyparser;
    
    // parse a file
    yyparser = new Parser(najo, r);
    yyparser.yydebug = najo.isDebugParsing();
    yyparser.yyparse();
   
  }

