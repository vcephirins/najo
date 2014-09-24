//### This file created by BYACC 1.8(/Java extension  1.15)
//### Java capabilities added 7 Jan 97, Bob Jamison
//### Updated : 27 Nov 97  -- Bob Jamison, Joe Nieten
//###           01 Jan 98  -- Bob Jamison -- fixed generic semantic constructor
//###           01 Jun 99  -- Bob Jamison -- added Runnable support
//###           06 Aug 00  -- Bob Jamison -- made state variables class-global
//###           03 Jan 01  -- Bob Jamison -- improved flags, tracing
//###           16 May 01  -- Bob Jamison -- added custom stack sizing
//###           04 Mar 02  -- Yuval Oren  -- improved java performance, added options
//###           14 Mar 02  -- Tomas Hurka -- -d support, static initializer workaround
//### Please send bug reports to tom@hukatronic.cz
//### static char yysccsid[] = "@(#)yaccpar	1.8 (Berkeley) 01/20/90";



package desc;



//#line 26 "desc.y"
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
  
//#line 36 "Parser.java"




public class Parser
{

boolean yydebug;        //do I want debug output?
int yynerrs;            //number of errors so far
int yyerrflag;          //was there an error?
int yychar;             //the current working character

//########## MESSAGES ##########
//###############################################################
// method: debug
//###############################################################
void debug(String msg)
{
  if (yydebug)
    System.out.println(msg);
}

//########## STATE STACK ##########
final static int YYSTACKSIZE = 600;  //maximum stack size
int statestk[] = new int[YYSTACKSIZE]; //state stack
int stateptr;
int stateptrmax;                     //highest index of stackptr
int statemax;                        //state when highest index reached
//###############################################################
// methods: state stack push,pop,drop,peek
//###############################################################
final void state_push(int state)
{
  try {
		stateptr++;
		statestk[stateptr]=state;
	 }
	 catch (ArrayIndexOutOfBoundsException e) {
     int oldsize = statestk.length;
     int newsize = oldsize * 2;
     int[] newstack = new int[newsize];
     System.arraycopy(statestk,0,newstack,0,oldsize);
     statestk = newstack;
     statestk[stateptr]=state;
  }
}
final int state_pop()
{
  return statestk[stateptr--];
}
final void state_drop(int cnt)
{
  stateptr -= cnt; 
}
final int state_peek(int relative)
{
  return statestk[stateptr-relative];
}
//###############################################################
// method: init_stacks : allocate and prepare stacks
//###############################################################
final boolean init_stacks()
{
  stateptr = -1;
  val_init();
  return true;
}
//###############################################################
// method: dump_stacks : show n levels of the stacks
//###############################################################
void dump_stacks(int count)
{
int i;
  System.out.println("=index==state====value=     s:"+stateptr+"  v:"+valptr);
  for (i=0;i<count;i++)
    System.out.println(" "+i+"    "+statestk[i]+"      "+valstk[i]);
  System.out.println("======================");
}


//########## SEMANTIC VALUES ##########
//public class ParserVal is defined in ParserVal.java


String   yytext;//user variable to return contextual strings
ParserVal yyval; //used to return semantic vals from action routines
ParserVal yylval;//the 'lval' (result) I got from yylex()
ParserVal valstk[];
int valptr;
//###############################################################
// methods: value stack push,pop,drop,peek.
//###############################################################
void val_init()
{
  valstk=new ParserVal[YYSTACKSIZE];
  yyval=new ParserVal();
  yylval=new ParserVal();
  valptr=-1;
}
void val_push(ParserVal val)
{
  if (valptr>=YYSTACKSIZE)
    return;
  valstk[++valptr]=val;
}
ParserVal val_pop()
{
  if (valptr<0)
    return new ParserVal();
  return valstk[valptr--];
}
void val_drop(int cnt)
{
int ptr;
  ptr=valptr-cnt;
  if (ptr<0)
    return;
  valptr = ptr;
}
ParserVal val_peek(int relative)
{
int ptr;
  ptr=valptr-relative;
  if (ptr<0)
    return new ParserVal();
  return valstk[ptr];
}
final ParserVal dup_yyval(ParserVal val)
{
  ParserVal dup = new ParserVal();
  dup.ival = val.ival;
  dup.dval = val.dval;
  dup.sval = val.sval;
  dup.obj = val.obj;
  return dup;
}
//#### end semantic value section ####
public final static short NL=257;
public final static short TYPEDEF=258;
public final static short FILEDEF=259;
public final static short LINEDEF=260;
public final static short COLDEF=261;
public final static short AS=262;
public final static short REPEAT=263;
public final static short UNSIGNED=264;
public final static short SIZE=265;
public final static short TRUE=266;
public final static short FALSE=267;
public final static short EOF=268;
public final static short NUMBER=269;
public final static short IDENT=270;
public final static short HEXA=271;
public final static short SEPARATED_BY=272;
public final static short BIN=273;
public final static short ASCII=274;
public final static short G_BIN=275;
public final static short G_ASCII=276;
public final static short G_BIG_ENDIAN=277;
public final static short G_LITTLE_ENDIAN=278;
public final static short T_BOOL=279;
public final static short T_BYTE=280;
public final static short T_INTEGER=281;
public final static short T_FLOAT=282;
public final static short T_DOUBLE=283;
public final static short T_STRING=284;
public final static short T_DATE_ISO=285;
public final static short T_OBJECT=286;
public final static short YYERRCODE=256;
final static short yylhs[] = {                           -1,
    0,    0,    0,    1,    1,    1,    2,    3,    3,    4,
    4,    5,    6,    6,    7,    7,    8,    8,    8,    9,
    9,   10,   10,   10,   11,   11,   12,   12,   12,   13,
   13,   13,   13,   20,   20,   20,   20,   20,   20,   20,
   20,   18,   18,   19,   19,   15,   15,   14,   14,   16,
   17,   17,
};
final static short yylen[] = {                            2,
    4,    1,    2,    0,    1,    2,    4,    0,    3,    1,
    2,    4,    1,    2,    5,    4,    0,    1,    2,    1,
    1,    0,    1,    2,    1,    1,    0,    1,    2,    1,
    1,    1,    1,    1,    1,    2,    1,    1,    2,    1,
    2,    1,    1,    1,    1,    0,    2,    0,    2,    1,
    1,    1,
};
final static short yydefred[] = {                         0,
    0,    0,    0,    0,    5,    3,    0,    0,    6,    0,
    0,    0,    0,    0,   10,    0,    0,   13,   42,   43,
   44,   45,    0,   18,   20,   21,    0,    1,   11,    0,
   14,   19,    0,   23,   25,   26,    0,   34,   35,    0,
   37,   38,    0,   40,    0,    0,    0,   24,    0,    0,
   36,   39,   41,    0,   33,    0,   28,   32,   30,   31,
   15,   50,   47,   49,   29,
};
final static short yydgoto[] = {                          3,
    4,    5,   10,   14,   15,   17,   18,   23,   24,   33,
   34,   56,   57,   58,   51,   63,    0,   25,   26,   46,
};
final static short yysindex[] = {                      -245,
  -58, -247,    0, -230,    0,    0, -248, -225,    0, -189,
 -186, -197, -194, -256,    0, -188, -186,    0,    0,    0,
    0,    0, -197,    0,    0,    0, -197,    0,    0, -246,
    0,    0, -251,    0,    0,    0, -187,    0,    0, -181,
    0,    0, -181,    0, -181, -258, -186,    0, -178, -183,
    0,    0,    0, -183,    0, -258,    0,    0,    0,    0,
    0,    0,    0,    0,    0,
};
final static short yyrindex[] = {                      -203,
   87,    0,    0, -172,    0,    0,    0,    0,    0,    0,
    0, -171,    0,    0,    0,    0, -210,    0,    0,    0,
    0,    0, -170,    0,    0,    0, -169,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0, -217,
    0,    0, -217,    0, -217, -206, -253,    0, -195,    0,
    0,    0,    0,    0,    0, -191,    0,    0,    0,    0,
    0,    0,    0,    0,    0,
};
final static short yygindex[] = {                         0,
    0,   89,    0,    0,   77,   61,  -17,    0,   72,    0,
   63,    0,   41,   49,   29,   45,    0,  -25,  -24,    0,
};
final static int YYTABLESIZE=99;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         31,
    6,   35,   36,   13,   54,   55,   12,   35,   36,   16,
    1,   28,    2,   11,   12,   37,   19,   20,   21,   22,
   59,   60,    7,   19,   20,   21,   22,    2,    8,   31,
   59,   60,   38,   39,   40,   41,   42,   43,   44,   45,
   46,   46,   46,   46,   12,   46,   46,    7,    7,    7,
   46,   27,   27,   27,   27,    4,    4,   46,   46,   46,
   46,   27,   48,   48,   48,   48,   16,   16,   16,   16,
   13,   52,   48,   53,   16,   27,   16,   19,   20,   21,
   22,   30,   49,   50,   54,   62,    2,    8,   17,    9,
   29,   22,    9,   47,   32,   48,   65,   61,   64,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         17,
   59,   27,   27,  260,  263,  264,  260,   33,   33,  261,
  256,  268,  258,  262,  268,  262,  275,  276,  277,  278,
   46,   46,  270,  275,  276,  277,  278,  258,  259,   47,
   56,   56,  279,  280,  281,  282,  283,  284,  285,  286,
  258,  259,  260,  261,  270,  263,  264,  258,  259,  260,
  268,  258,  259,  260,  261,  259,  260,  275,  276,  277,
  278,  268,  258,  259,  260,  261,  258,  259,  260,  261,
  260,   43,  268,   45,  261,  270,  268,  275,  276,  277,
  278,  270,  270,  265,  263,  269,    0,  260,  260,  260,
   14,  261,    4,   33,   23,   33,   56,   49,   54,
};
}
final static short YYFINAL=3;
final static short YYMAXTOKEN=286;
final static String yyname[] = {
"end-of-file",null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,"';'",null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,"NL","TYPEDEF","FILEDEF","LINEDEF","COLDEF","AS","REPEAT",
"UNSIGNED","SIZE","TRUE","FALSE","EOF","NUMBER","IDENT","HEXA","SEPARATED_BY",
"BIN","ASCII","G_BIN","G_ASCII","G_BIG_ENDIAN","G_LITTLE_ENDIAN","T_BOOL",
"T_BYTE","T_INTEGER","T_FLOAT","T_DOUBLE","T_STRING","T_DATE_ISO","T_OBJECT",
};
final static String yyrule[] = {
"$accept : racine",
"racine : list_typedef filedef list_linedef EOF",
"racine : error",
"racine : error ';'",
"list_typedef :",
"list_typedef : typedef",
"list_typedef : list_typedef typedef",
"typedef : TYPEDEF IDENT AS list_coldef",
"filedef :",
"filedef : FILEDEF IDENT list_option_filedef",
"list_linedef : linedef",
"list_linedef : list_linedef linedef",
"linedef : LINEDEF IDENT list_option_linedef list_coldef",
"list_coldef : coldef",
"list_coldef : list_coldef coldef",
"coldef : COLDEF IDENT AS IDENT repeat",
"coldef : COLDEF IDENT type list_option_coldef",
"list_option_filedef :",
"list_option_filedef : option_filedef",
"list_option_filedef : list_option_filedef option_filedef",
"option_filedef : mode",
"option_filedef : endian",
"list_option_linedef :",
"list_option_linedef : option_linedef",
"list_option_linedef : list_option_linedef option_linedef",
"option_linedef : mode",
"option_linedef : endian",
"list_option_coldef :",
"list_option_coldef : option_coldef",
"list_option_coldef : list_option_coldef option_coldef",
"option_coldef : mode",
"option_coldef : endian",
"option_coldef : repeat",
"option_coldef : UNSIGNED",
"type : T_BOOL",
"type : T_BYTE",
"type : T_INTEGER size",
"type : T_FLOAT",
"type : T_DOUBLE",
"type : T_STRING size",
"type : T_DATE_ISO",
"type : T_OBJECT size",
"mode : G_BIN",
"mode : G_ASCII",
"endian : G_BIG_ENDIAN",
"endian : G_LITTLE_ENDIAN",
"size :",
"size : SIZE number",
"repeat :",
"repeat : REPEAT number",
"number : NUMBER",
"bool : TRUE",
"bool : FALSE",
};

//#line 219 "desc.y"

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
//#line 344 "Parser.java"
//###############################################################
// method: yylexdebug : check lexer state
//###############################################################
void yylexdebug(int state,int ch)
{
String s=null;
  if (ch < 0) ch=0;
  if (ch <= YYMAXTOKEN) //check index bounds
     s = yyname[ch];    //now get it
  if (s==null)
    s = "illegal-symbol";
  debug("state "+state+", reading "+ch+" ("+s+")");
}





//The following are now global, to aid in error reporting
int yyn;       //next next thing to do
int yym;       //
int yystate;   //current parsing state from state table
String yys;    //current token string


//###############################################################
// method: yyparse : parse input and execute indicated items
//###############################################################
int yyparse()
{
boolean doaction;
  init_stacks();
  yynerrs = 0;
  yyerrflag = 0;
  yychar = -1;          //impossible char forces a read
  yystate=0;            //initial state
  state_push(yystate);  //save it
  val_push(yylval);     //save empty value
  while (true) //until parsing is done, either correctly, or w/error
    {
    doaction=true;
    if (yydebug) debug("loop"); 
    //#### NEXT ACTION (from reduction table)
    for (yyn=yydefred[yystate];yyn==0;yyn=yydefred[yystate])
      {
      if (yydebug) debug("yyn:"+yyn+"  state:"+yystate+"  yychar:"+yychar);
      if (yychar < 0)      //we want a char?
        {
        yychar = yylex();  //get next token
        if (yydebug) debug(" next yychar:"+yychar);
        //#### ERROR CHECK ####
        if (yychar < 0)    //it it didn't work/error
          {
          yychar = 0;      //change it to default string (no -1!)
          if (yydebug)
            yylexdebug(yystate,yychar);
          }
        }//yychar<0
      yyn = yysindex[yystate];  //get amount to shift by (shift index)
      if ((yyn != 0) && (yyn += yychar) >= 0 &&
          yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
        {
        if (yydebug)
          debug("state "+yystate+", shifting to state "+yytable[yyn]);
        //#### NEXT STATE ####
        yystate = yytable[yyn];//we are in a new state
        state_push(yystate);   //save it
        val_push(yylval);      //push our lval as the input for next rule
        yychar = -1;           //since we have 'eaten' a token, say we need another
        if (yyerrflag > 0)     //have we recovered an error?
           --yyerrflag;        //give ourselves credit
        doaction=false;        //but don't process yet
        break;   //quit the yyn=0 loop
        }

    yyn = yyrindex[yystate];  //reduce
    if ((yyn !=0 ) && (yyn += yychar) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
      {   //we reduced!
      if (yydebug) debug("reduce");
      yyn = yytable[yyn];
      doaction=true; //get ready to execute
      break;         //drop down to actions
      }
    else //ERROR RECOVERY
      {
      if (yyerrflag==0)
        {
        yyerror("syntax error");
        yynerrs++;
        }
      if (yyerrflag < 3) //low error count?
        {
        yyerrflag = 3;
        while (true)   //do until break
          {
          if (stateptr<0)   //check for under & overflow here
            {
            yyerror("stack underflow. aborting...");  //note lower case 's'
            return 1;
            }
          yyn = yysindex[state_peek(0)];
          if ((yyn != 0) && (yyn += YYERRCODE) >= 0 &&
                    yyn <= YYTABLESIZE && yycheck[yyn] == YYERRCODE)
            {
            if (yydebug)
              debug("state "+state_peek(0)+", error recovery shifting to state "+yytable[yyn]+" ");
            yystate = yytable[yyn];
            state_push(yystate);
            val_push(yylval);
            doaction=false;
            break;
            }
          else
            {
            if (yydebug)
              debug("error recovery discarding state "+state_peek(0)+" ");
            if (stateptr<0)   //check for under & overflow here
              {
              yyerror("Stack underflow. aborting...");  //capital 'S'
              return 1;
              }
            state_pop();
            val_pop();
            }
          }
        }
      else            //discard this token
        {
        if (yychar == 0)
          return 1; //yyabort
        if (yydebug)
          {
          yys = null;
          if (yychar <= YYMAXTOKEN) yys = yyname[yychar];
          if (yys == null) yys = "illegal-symbol";
          debug("state "+yystate+", error recovery discards token "+yychar+" ("+yys+")");
          }
        yychar = -1;  //read another
        }
      }//end error recovery
    }//yyn=0 loop
    if (!doaction)   //any reason not to proceed?
      continue;      //skip action
    yym = yylen[yyn];          //get count of terminals on rhs
    if (yydebug)
      debug("state "+yystate+", reducing "+yym+" by rule "+yyn+" ("+yyrule[yyn]+")");
    if (yym>0)                 //if count of rhs not 'nil'
      yyval = val_peek(yym-1); //get current semantic value
    yyval = dup_yyval(yyval); //duplicate yyval if ParserVal is used as semantic value
    switch(yyn)
      {
//########## USER-SUPPLIED ACTIONS ##########
case 1:
//#line 72 "desc.y"
{ desc = new Desc((ListTypeDef) val_peek(3).obj, (FileDef) val_peek(2).obj, (ListLineDef) val_peek(1).obj); }
break;
case 2:
//#line 73 "desc.y"
{ yyerror(""); }
break;
case 3:
//#line 74 "desc.y"
{ yyerror(""); }
break;
case 4:
//#line 79 "desc.y"
{yyval.obj = null;}
break;
case 5:
//#line 80 "desc.y"
{
       ListTypeDef listTypeDef = new ListTypeDef(5);
       listTypeDef.add((TypeDef)val_peek(0).obj);
       yyval.obj = listTypeDef;}
break;
case 6:
//#line 84 "desc.y"
{((ListTypeDef)val_peek(1).obj).add((TypeDef)val_peek(0).obj); yyval.obj = val_peek(1).obj;}
break;
case 7:
//#line 88 "desc.y"
{
       TypeDef typeDef = new TypeDef(val_peek(2).sval, (ListColDef) val_peek(0).obj);
       yyval.obj = typeDef;}
break;
case 8:
//#line 95 "desc.y"
{yyval.obj = null;}
break;
case 9:
//#line 96 "desc.y"
{
       FileDef fileDef = new FileDef(val_peek(1).sval, (ListOption) val_peek(0).obj);
       yyval.obj = fileDef;}
break;
case 10:
//#line 102 "desc.y"
{
       ListLineDef listLineDef = new ListLineDef(5);
       listLineDef.add((LineDef)val_peek(0).obj);
       yyval.obj = listLineDef;}
break;
case 11:
//#line 106 "desc.y"
{((ListLineDef)val_peek(1).obj).add((LineDef)val_peek(0).obj); yyval.obj = val_peek(1).obj;}
break;
case 12:
//#line 110 "desc.y"
{
       LineDef lineDef = new LineDef(val_peek(2).sval, (ListOption) val_peek(1).obj, (ListColDef) val_peek(0).obj);
       yyval.obj = lineDef;}
break;
case 13:
//#line 116 "desc.y"
{
       ListColDef listColDef = new ListColDef(20);
       listColDef.add((ColDef)val_peek(0).obj);
       yyval.obj = listColDef;}
break;
case 14:
//#line 120 "desc.y"
{((ListColDef)val_peek(1).obj).add((ColDef)val_peek(0).obj); yyval.obj = val_peek(1).obj;}
break;
case 15:
//#line 124 "desc.y"
{
/*       ColDef colDef = new ColDef((String)$2, (EnumType)$3, (ListOption) $4);
       $$ = colDef;*/}
break;
case 16:
//#line 127 "desc.y"
{
       ColDef colDef = new ColDef((String)val_peek(2).sval, (EnumType)val_peek(1).obj, (ListOption) val_peek(0).obj);
       yyval.obj = colDef;}
break;
case 17:
//#line 134 "desc.y"
{yyval.obj = null;}
break;
case 18:
//#line 135 "desc.y"
{
       ListOption listOptionFileDef = new ListOption(5);
       listOptionFileDef.add((Option)val_peek(0).obj);
       yyval.obj = listOptionFileDef;}
break;
case 19:
//#line 139 "desc.y"
{((ListOption)val_peek(1).obj).add((Option)val_peek(0).obj); yyval.obj = val_peek(1).obj;}
break;
case 20:
//#line 142 "desc.y"
{yyval.obj = new Option(EnumOption.MODE, val_peek(0).ival);}
break;
case 21:
//#line 143 "desc.y"
{yyval.obj = new Option(EnumOption.ENDIAN, val_peek(0).ival);}
break;
case 22:
//#line 148 "desc.y"
{yyval.obj = null;}
break;
case 23:
//#line 149 "desc.y"
{
       ListOption listOptionLineDef = new ListOption(5);
       listOptionLineDef.add((Option)val_peek(0).obj);
       yyval.obj = listOptionLineDef;}
break;
case 24:
//#line 153 "desc.y"
{((ListOption)val_peek(1).obj).add((Option)val_peek(0).obj); yyval.obj = val_peek(1).obj;}
break;
case 25:
//#line 156 "desc.y"
{yyval.obj = new Option(EnumOption.MODE, val_peek(0).ival);}
break;
case 26:
//#line 157 "desc.y"
{yyval.obj = new Option(EnumOption.ENDIAN, val_peek(0).ival);}
break;
case 27:
//#line 162 "desc.y"
{yyval.obj = null;}
break;
case 28:
//#line 163 "desc.y"
{
       ListOption listOptionColDef = new ListOption(5);
       listOptionColDef.add((Option)val_peek(0).obj);
       yyval.obj = listOptionColDef;}
break;
case 29:
//#line 167 "desc.y"
{((ListOption)val_peek(1).obj).add((Option)val_peek(0).obj); yyval.obj = val_peek(1).obj;}
break;
case 30:
//#line 170 "desc.y"
{yyval.obj = new Option(EnumOption.MODE, val_peek(0).ival);}
break;
case 31:
//#line 171 "desc.y"
{yyval.obj = new Option(EnumOption.ENDIAN, val_peek(0).ival);}
break;
case 32:
//#line 172 "desc.y"
{yyval.obj = new Option(EnumOption.REPEAT, val_peek(0).ival);}
break;
case 33:
//#line 173 "desc.y"
{yyval.obj = new Option(EnumOption.UNSIGNED, 1);}
break;
case 34:
//#line 177 "desc.y"
{yyval.obj = EnumType.BOOL;}
break;
case 35:
//#line 178 "desc.y"
{yyval.obj = EnumType.BYTE;}
break;
case 36:
//#line 179 "desc.y"
{yyval.obj = EnumType.INTEGER;}
break;
case 37:
//#line 180 "desc.y"
{yyval.obj = EnumType.FLOAT;}
break;
case 38:
//#line 181 "desc.y"
{yyval.obj = EnumType.DOUBLE;}
break;
case 39:
//#line 182 "desc.y"
{yyval.obj = EnumType.STRING;}
break;
case 40:
//#line 183 "desc.y"
{yyval.obj = EnumType.DATE_ISO;}
break;
case 41:
//#line 184 "desc.y"
{yyval.obj = EnumType.OBJECT;}
break;
case 42:
//#line 188 "desc.y"
{yyval.ival = 0;}
break;
case 43:
//#line 189 "desc.y"
{yyval.ival = 1;}
break;
case 44:
//#line 193 "desc.y"
{yyval.ival = 0;}
break;
case 45:
//#line 194 "desc.y"
{yyval.ival = 1;}
break;
case 46:
//#line 199 "desc.y"
{yyval.ival = 4;}
break;
case 47:
//#line 201 "desc.y"
{yyval.ival = val_peek(0).ival;}
break;
case 48:
//#line 205 "desc.y"
{yyval.ival = 1;}
break;
case 49:
//#line 206 "desc.y"
{yyval.ival = val_peek(0).ival;}
break;
case 50:
//#line 210 "desc.y"
{yyval.ival = Integer.parseInt(val_peek(0).sval); }
break;
case 51:
//#line 214 "desc.y"
{yyval.ival = 1; }
break;
case 52:
//#line 215 "desc.y"
{yyval.ival = 0; }
break;
//#line 729 "Parser.java"
//########## END OF USER-SUPPLIED ACTIONS ##########
    }//switch
    //#### Now let's reduce... ####
    if (yydebug) debug("reduce");
    state_drop(yym);             //we just reduced yylen states
    yystate = state_peek(0);     //get new state
    val_drop(yym);               //corresponding value drop
    yym = yylhs[yyn];            //select next TERMINAL(on lhs)
    if (yystate == 0 && yym == 0)//done? 'rest' state and at first TERMINAL
      {
      if (yydebug) debug("After reduction, shifting from state 0 to state "+YYFINAL+"");
      yystate = YYFINAL;         //explicitly say we're done
      state_push(YYFINAL);       //and save it
      val_push(yyval);           //also save the semantic value of parsing
      if (yychar < 0)            //we want another character?
        {
        yychar = yylex();        //get next character
        if (yychar<0) yychar=0;  //clean, if necessary
        if (yydebug)
          yylexdebug(yystate,yychar);
        }
      if (yychar == 0)          //Good exit (if lex returns 0 ;-)
         break;                 //quit the loop--all DONE
      }//if yystate
    else                        //else not done yet
      {                         //get next state and push, for next yydefred[]
      yyn = yygindex[yym];      //find out where to go
      if ((yyn != 0) && (yyn += yystate) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yystate)
        yystate = yytable[yyn]; //get new state
      else
        yystate = yydgoto[yym]; //else go to new defred
      if (yydebug) debug("after reduction, shifting from state "+state_peek(0)+" to state "+yystate+"");
      state_push(yystate);     //going again, so push state & val...
      val_push(yyval);         //for next action
      }
    }//main loop
  return 0;//yyaccept!!
}
//## end of method parse() ######################################



//## run() --- for Thread #######################################
/**
 * A default run method, used for operating this parser
 * object in the background.  It is intended for extending Thread
 * or implementing Runnable.  Turn off with -Jnorun .
 */
public void run()
{
  yyparse();
}
//## end of method run() ########################################



//## Constructors ###############################################
/**
 * Default constructor.  Turn off with -Jnoconstruct .

 */
public Parser()
{
  //nothing to do
}


/**
 * Create a parser, setting the debug to true or false.
 * @param debugMe true for debugging, false for no debug.
 */
public Parser(boolean debugMe)
{
  yydebug=debugMe;
}
//###############################################################



}
//################### END OF CLASS ##############################
