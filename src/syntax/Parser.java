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



package syntax;



//#line 26 "syntax.y"
  import java.io.*;
  
  import org.najo.enums.TypeNode;
  import org.najo.enums.TypeValue;
  import org.najo.enums.TypeMath;
  import org.najo.enums.TypeCond;

  import org.najo.applications.Najo;
  import org.najo.Nodes.INode;
  import org.najo.Nodes.Node;
  import org.najo.Nodes.NodeAlias;
  import org.najo.Nodes.NodeValue;
  import org.najo.Nodes.NodeMath;
  import org.najo.Nodes.NodeCond;
  import org.najo.Nodes.ListNodes;
  
//#line 34 "Parser.java"




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
public final static short QUIT=258;
public final static short INTEGER=259;
public final static short LONG=260;
public final static short DOUBLE=261;
public final static short FLOAT=262;
public final static short HEXA=263;
public final static short IDENT=264;
public final static short STRING=265;
public final static short DATE_ISO_A=266;
public final static short DATE_ISO_B=267;
public final static short FMTASCII=268;
public final static short BOOL_TRUE=269;
public final static short BOOL_FALSE=270;
public final static short ON=271;
public final static short OFF=272;
public final static short NULL=273;
public final static short ERROR=274;
public final static short SET=275;
public final static short PRINT=276;
public final static short FOR=277;
public final static short AS=278;
public final static short WITH=279;
public final static short DEFINE=280;
public final static short SELECT=281;
public final static short WHERE=282;
public final static short FROM=283;
public final static short GROUP_BY=284;
public final static short HAVING=285;
public final static short BREAK=286;
public final static short ROWID=287;
public final static short ROWNUM=288;
public final static short FORMAT=289;
public final static short BIN=290;
public final static short ASCII=291;
public final static short SEPARATED_BY=292;
public final static short EQUAL=293;
public final static short LE=294;
public final static short GE=295;
public final static short NE=296;
public final static short EQEQ=297;
public final static short AND=298;
public final static short OR=299;
public final static short NOT=300;
public final static short TRACE=301;
public final static short DEBUG=302;
public final static short BREAK_ON_ERROR=303;
public final static short GENERATE=304;
public final static short METADATA=305;
public final static short EOF=306;
public final static short HELP=307;
public final static short SHOW=308;
public final static short TYPES_ALL=309;
public final static short TYPE_DEX=310;
public final static short LIBRARIES=311;
public final static short LIBRARIE=312;
public final static short FUNCTIONS=313;
public final static short FUNCTION=314;
public final static short MUNAIRE=315;
public final static short YYERRCODE=256;
final static short yylhs[] = {                           -1,
    0,    2,    2,    2,    2,    3,    3,    3,    3,    3,
    3,    4,    4,    4,    5,    5,    5,    9,    9,    7,
    8,   10,   20,   20,    6,   11,   21,   21,   22,   22,
   23,   23,   24,   24,   25,   25,   26,   26,   26,   26,
   32,   37,   37,   37,   37,   37,   37,   35,   35,   33,
   33,   36,   36,   34,   34,   38,   29,   29,   29,   29,
   29,   29,   27,   27,   28,   28,   31,   31,   31,   31,
   31,   31,   31,   31,   31,   31,   30,   30,   30,   30,
   30,   30,   30,   30,   30,   30,   30,   30,   39,   39,
   39,   39,   39,   39,   39,   39,   12,   13,   13,   13,
   13,   14,   15,   15,   16,   17,   17,   19,   19,    1,
   18,
};
final static short yylen[] = {                            2,
    1,    2,    3,    2,    3,    1,    1,    1,    1,    1,
    1,    3,    3,    3,    2,    2,    2,    1,    1,    3,
    3,    3,    0,    2,    2,    7,    0,    2,    0,    4,
    0,    3,    0,    2,    1,    3,    1,    3,    4,    6,
    5,    1,    3,    3,    5,    1,    3,    1,    3,    0,
    1,    1,    3,    0,    1,    4,    1,    1,    1,    3,
    3,    3,    1,    3,    3,    3,    2,    3,    3,    3,
    3,    3,    3,    3,    3,    3,    1,    1,    1,    1,
    3,    3,    3,    3,    3,    3,    2,    3,    1,    1,
    1,    1,    1,    1,    1,    1,    1,    1,    1,    1,
    1,    1,    1,    1,    1,    1,    1,    1,    1,    1,
    1,
};
final static short yydefred[] = {                         0,
    0,   19,    0,    0,    0,    0,   18,    0,    0,    0,
    0,    6,    7,    8,    9,   10,   11,    0,    4,    0,
    0,    0,   98,   99,  101,  100,  105,    0,  102,  103,
  104,  106,  107,  111,  110,   79,   80,    0,    0,    0,
   96,   91,   90,   92,   93,   95,   94,   89,   59,    0,
    0,    0,   78,   77,    0,   63,    0,    0,   25,   15,
   16,   17,    0,    0,    2,    0,    0,  108,  109,   12,
   13,   14,    0,    0,   67,    0,   87,    0,    0,    0,
    0,   20,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
   22,    5,    3,    0,   21,    0,    0,   88,   76,    0,
    0,   34,   62,    0,    0,   65,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,   66,   69,
    0,    0,   64,    0,   56,    0,    0,    0,    0,    0,
   35,   55,   52,    0,   51,   48,    0,   43,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,   36,
    0,    0,   53,   45,   49,    0,   39,    0,    0,   26,
    0,    0,    0,   40,    0,    0,
};
final static short yydgoto[] = {                          9,
   41,   10,   11,   12,   13,   14,   15,   16,   17,   18,
   67,   42,   43,   44,   45,   46,   47,   48,   70,  101,
  153,  162,  170,   82,  140,  141,   55,   49,   50,   74,
   58,    0,  146,  143,  147,  144,  112,   53,   54,
};
final static short yysindex[] = {                      -240,
  -44,    0, -212,  100,  100,  100,    0, -176,    0,  -93,
  -39,    0,    0,    0,    0,    0,    0, -248,    0, -210,
 -210, -210,    0,    0,    0,    0,    0,   -1,    0,    0,
    0,    0,    0,    0,    0,    0,    0,  100,  377,  100,
    0,    0,    0,    0,    0,    0,    0,    0,    0,  -33,
   99, -257,    0,    0,  -25,    0,   99, -257,    0,    0,
    0,    0,   -8,   24,    0,  100, -184,    0,    0,    0,
    0,    0,  100,  111,    0,  377,    0,   87,  -32, -205,
  100,    0, -154,  377,  377,  377,  377,  377,  377,  377,
  377,  377,  377,  377,  377, -150,  100,  100,  100,  100,
    0,    0,    0,  -30,    0,   40,   70,    0,    0, -147,
 -204,    0,    0,   99, -257,    0,   22,   22,   22,   22,
   22,   22,  113,  113,   41,   41,   41,   41,    0,    0,
 -165,   93,    0, -122,    0, -112, -111, -103, -186,  -43,
    0,    0,    0,  122,    0,    0,  -41,    0,  -96,  -92,
  100, -122, -110, -112,  -88, -111,  -90, -122, -200,    0,
  100, -100,    0,    0,    0,  -74,    0, -272,  -80,    0,
 -122,  100,  100,    0, -200, -200,
};
final static short yyrindex[] = {                         0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,  192,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,  -37,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,  144,
  155,  177,    0,    0,  -77,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,  144,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,  147,
  149,    0,    0,  184,  186,    0,  179,  196,  233,  257,
  281,  327,  135,  157,  -13,   11,   35,   59,    0,    0,
  126,  -72,    0,    0,    0,   16,  -42,    0,  219,    4,
    0,    0,    0,  151,    0,    0,  152,    0,    0,    0,
    0,    0,   49,   16,    0,  -42,  220,    0,   28,    0,
    0,  -47,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,   50,  -21,
};
final static short yygindex[] = {                         0,
    0,    0,  202,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,   17,    0,    0,    0,    0,  101,    0,
    0,    0,    0,  159,    0,   72,  123,   39,  -23,  613,
  509,    0,   73,   77,    0,    0,    0,    0,    0,
};
final static int YYTABLESIZE=713;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         97,
  152,   50,  156,   97,   97,   97,   97,   97,  109,   97,
   81,   31,  172,   81,   19,    1,   50,    2,  100,   65,
   96,   97,   97,   83,   97,   97,   98,   83,   83,   83,
   83,   83,   66,   83,    3,    4,    5,   32,   73,    6,
   97,   98,  104,   56,   59,   83,   83,   84,   83,  106,
  102,   84,   84,   84,   84,   84,   97,   84,   95,   54,
   68,   69,   27,   92,   90,    7,   91,    8,   93,   84,
   84,   85,   84,  137,   54,   85,   85,   85,   85,   85,
  135,   85,  103,   81,  110,  111,   28,  138,   20,   21,
   22,  149,  150,   85,   85,   86,   85,   97,   98,   86,
   86,   86,   86,   86,   80,   86,   95,   29,   30,  116,
  108,   92,   90,  129,   91,   94,   93,   86,   86,  113,
   86,   71,   72,   95,   60,   61,   62,  108,   92,   90,
  136,   91,   97,   93,   94,   95,  100,   56,  133,   40,
   92,   90,   29,   91,   39,   93,   88,   95,   89,   95,
  139,  142,   92,   90,   92,   91,  145,   93,   88,   93,
   89,  148,   63,   94,    2,  154,   68,  157,  139,   68,
   88,  158,   89,  161,  167,   81,  164,   81,   81,   81,
   94,    3,    4,    5,   68,  169,    6,  174,  166,  171,
  173,    1,   94,   81,   81,   57,   81,   82,   57,   82,
   82,   82,   33,   23,   94,   46,   94,   42,   24,   47,
   44,   64,    7,   57,    8,   82,   82,   58,   82,   74,
   58,  132,   74,  160,   60,  105,   61,   60,  165,   61,
  163,    0,    0,    0,    0,   58,   75,   74,  151,   75,
   97,   31,   60,    0,   61,   97,   97,   97,   97,   50,
  155,   97,  134,   99,   75,   80,   97,   97,   97,   97,
   97,   97,   37,   38,   83,   97,   98,   32,    0,   83,
   83,   83,   83,   72,    0,   83,   72,   37,   38,    0,
   83,   83,   83,   83,   83,   83,    0,   27,   84,   27,
    0,   72,   27,   84,   84,   84,   84,   73,    0,   84,
   73,    0,    0,    0,   84,   84,   84,   84,   84,   84,
    0,   28,   85,   28,    0,   73,   28,   85,   85,   85,
   85,   70,    0,   85,   70,    0,    0,    0,   85,   85,
   85,   85,   85,   85,   29,   30,   86,   29,   30,   70,
    0,   86,   86,   86,   86,    0,    0,   86,    0,    0,
    0,    0,   86,   86,   86,   86,   86,   86,   23,   24,
   25,   26,   27,   28,   29,   30,   31,   71,   32,   33,
   71,    0,   34,   35,    0,    0,   83,    0,    0,    0,
   84,   85,   86,   87,    0,   71,   36,   37,    0,    0,
    0,    0,   84,   85,   86,   87,    0,    0,    0,   38,
    0,    0,    0,   68,   84,   85,   86,   87,   68,   68,
   68,   68,   81,    0,   68,    0,   76,   81,   81,   81,
   81,   39,    0,   81,   68,    0,    0,    0,   81,   81,
   81,   81,   81,   81,   82,    0,    0,   57,    0,   82,
   82,   82,   82,   57,    0,   82,    0,    0,    0,    0,
   82,   82,   82,   82,   82,   82,   74,    0,    0,   58,
    0,   74,   74,   74,   74,   58,   60,   74,   61,    0,
    0,    0,   60,   75,   61,    0,   74,   74,   75,   75,
   75,   75,    0,    0,   75,    0,    0,    0,    0,    0,
    0,    0,    0,   75,   75,    0,    0,    0,    0,    0,
   37,   38,   37,   38,   37,   38,    0,   37,   38,    0,
   72,    0,   52,    0,    0,   72,   72,   72,   72,    0,
    0,   72,    0,    0,    0,    0,    0,    0,    0,    0,
   72,   72,    0,    0,   73,    0,    0,    0,    0,   73,
   73,   73,   73,    0,    0,   73,   75,    0,   79,    0,
    0,    0,    0,    0,   73,   73,    0,    0,   70,    0,
    0,    0,    0,   70,   70,   70,   70,    0,    0,   70,
    0,    0,    0,    0,   52,    0,    0,    0,   70,   70,
    0,   52,    0,    0,    0,    0,    0,    0,    0,  115,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,   71,  130,  131,    0,    0,   71,
   71,   71,   71,    0,    0,   71,   51,   57,   57,    0,
    0,    0,    0,    0,   71,   71,    0,    0,    0,    0,
    0,    0,    0,    0,    0,   23,   24,   25,   26,   27,
   28,   29,   30,   31,    0,   32,   33,    0,    0,   34,
   35,   77,   78,    0,    0,    0,    0,    0,    0,  159,
    0,    0,    0,   36,   37,    0,    0,    0,    0,  168,
    0,    0,    0,    0,    0,    0,    0,    0,   51,    0,
  175,  176,    0,    0,    0,   51,    0,    0,  107,    0,
    0,    0,    0,  114,    0,    0,  117,  118,  119,  120,
  121,  122,  123,  124,  125,  126,  127,  128,    0,    0,
    0,   57,   57,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         37,
   44,   44,   44,   41,   42,   43,   44,   45,   41,   47,
   44,   59,  285,   44,   59,  256,   59,  258,   44,   59,
  278,   59,   60,   37,   62,  298,  299,   41,   42,   43,
   44,   45,  281,   47,  275,  276,  277,   59,   40,  280,
  298,  299,   66,    5,    6,   59,   60,   37,   62,   73,
   59,   41,   42,   43,   44,   45,   94,   47,   37,   44,
  271,  272,   59,   42,   43,  306,   45,  308,   47,   59,
   60,   37,   62,  278,   59,   41,   42,   43,   44,   45,
   41,   47,   59,   44,  290,  291,   59,  292,  301,  302,
  303,  278,  279,   59,   60,   37,   62,  298,  299,   41,
   42,   43,   44,   45,  289,   47,   37,   59,   59,  264,
   41,   42,   43,  264,   45,   94,   47,   59,   60,   81,
   62,   21,   22,   37,  301,  302,  303,   41,   42,   43,
  278,   45,  298,   47,   94,   37,   44,   99,  100,   40,
   42,   43,  265,   45,   45,   47,   60,   37,   62,   37,
  134,  264,   42,   43,   42,   45,  268,   47,   60,   47,
   62,  265,  256,   94,  258,   44,   41,  264,  152,   44,
   60,  264,   62,  284,  158,   41,  265,   43,   44,   45,
   94,  275,  276,  277,   59,  286,  280,  171,  279,  264,
  271,    0,   94,   59,   60,   41,   62,   41,   44,   43,
   44,   45,   59,  281,   94,   59,   94,   59,  281,   59,
   59,   10,  306,   59,  308,   59,   60,   41,   62,   41,
   44,   99,   44,  152,   41,   67,   41,   44,  156,   44,
  154,   -1,   -1,   -1,   -1,   59,   41,   59,  282,   44,
  278,  289,   59,   -1,   59,  283,  284,  285,  286,  292,
  292,  289,  283,  279,   59,  289,  294,  295,  296,  297,
  298,  299,   44,   44,  278,  298,  299,  289,   -1,  283,
  284,  285,  286,   41,   -1,  289,   44,   59,   59,   -1,
  294,  295,  296,  297,  298,  299,   -1,  284,  278,  286,
   -1,   59,  289,  283,  284,  285,  286,   41,   -1,  289,
   44,   -1,   -1,   -1,  294,  295,  296,  297,  298,  299,
   -1,  284,  278,  286,   -1,   59,  289,  283,  284,  285,
  286,   41,   -1,  289,   44,   -1,   -1,   -1,  294,  295,
  296,  297,  298,  299,  286,  286,  278,  289,  289,   59,
   -1,  283,  284,  285,  286,   -1,   -1,  289,   -1,   -1,
   -1,   -1,  294,  295,  296,  297,  298,  299,  259,  260,
  261,  262,  263,  264,  265,  266,  267,   41,  269,  270,
   44,   -1,  273,  274,   -1,   -1,  278,   -1,   -1,   -1,
  294,  295,  296,  297,   -1,   59,  287,  288,   -1,   -1,
   -1,   -1,  294,  295,  296,  297,   -1,   -1,   -1,  300,
   -1,   -1,   -1,  278,  294,  295,  296,  297,  283,  284,
  285,  286,  278,   -1,  289,   -1,   40,  283,  284,  285,
  286,   45,   -1,  289,  299,   -1,   -1,   -1,  294,  295,
  296,  297,  298,  299,  278,   -1,   -1,  283,   -1,  283,
  284,  285,  286,  289,   -1,  289,   -1,   -1,   -1,   -1,
  294,  295,  296,  297,  298,  299,  278,   -1,   -1,  283,
   -1,  283,  284,  285,  286,  289,  283,  289,  283,   -1,
   -1,   -1,  289,  278,  289,   -1,  298,  299,  283,  284,
  285,  286,   -1,   -1,  289,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,  298,  299,   -1,   -1,   -1,   -1,   -1,
  282,  282,  284,  284,  286,  286,   -1,  289,  289,   -1,
  278,   -1,    4,   -1,   -1,  283,  284,  285,  286,   -1,
   -1,  289,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
  298,  299,   -1,   -1,  278,   -1,   -1,   -1,   -1,  283,
  284,  285,  286,   -1,   -1,  289,   38,   -1,   40,   -1,
   -1,   -1,   -1,   -1,  298,  299,   -1,   -1,  278,   -1,
   -1,   -1,   -1,  283,  284,  285,  286,   -1,   -1,  289,
   -1,   -1,   -1,   -1,   66,   -1,   -1,   -1,  298,  299,
   -1,   73,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   81,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,  278,   97,   98,   -1,   -1,  283,
  284,  285,  286,   -1,   -1,  289,    4,    5,    6,   -1,
   -1,   -1,   -1,   -1,  298,  299,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,  259,  260,  261,  262,  263,
  264,  265,  266,  267,   -1,  269,  270,   -1,   -1,  273,
  274,   39,   40,   -1,   -1,   -1,   -1,   -1,   -1,  151,
   -1,   -1,   -1,  287,  288,   -1,   -1,   -1,   -1,  161,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   66,   -1,
  172,  173,   -1,   -1,   -1,   73,   -1,   -1,   76,   -1,
   -1,   -1,   -1,   81,   -1,   -1,   84,   85,   86,   87,
   88,   89,   90,   91,   92,   93,   94,   95,   -1,   -1,
   -1,   99,  100,
};
}
final static short YYFINAL=9;
final static short YYMAXTOKEN=315;
final static String yyname[] = {
"end-of-file",null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,"'%'",null,null,"'('","')'","'*'","'+'",
"','","'-'",null,"'/'",null,null,null,null,null,null,null,null,null,null,null,
"';'","'<'",null,"'>'",null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,"'['",null,"']'","'^'",null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,"NL","QUIT","INTEGER","LONG","DOUBLE",
"FLOAT","HEXA","IDENT","STRING","DATE_ISO_A","DATE_ISO_B","FMTASCII",
"BOOL_TRUE","BOOL_FALSE","ON","OFF","NULL","ERROR","SET","PRINT","FOR","AS",
"WITH","DEFINE","SELECT","WHERE","FROM","GROUP_BY","HAVING","BREAK","ROWID",
"ROWNUM","FORMAT","BIN","ASCII","SEPARATED_BY","EQUAL","LE","GE","NE","EQEQ",
"AND","OR","NOT","TRACE","DEBUG","BREAK_ON_ERROR","GENERATE","METADATA","EOF",
"HELP","SHOW","TYPES_ALL","TYPE_DEX","LIBRARIES","LIBRARIE","FUNCTIONS",
"FUNCTION","MUNAIRE",
};
final static String yyrule[] = {
"$accept : racine",
"racine : list_commandes",
"list_commandes : commande ';'",
"list_commandes : list_commandes commande ';'",
"list_commandes : error ';'",
"list_commandes : list_commandes error ';'",
"commande : set",
"commande : show",
"commande : define",
"commande : print",
"commande : request",
"commande : quit",
"set : SET TRACE on_off",
"set : SET DEBUG on_off",
"set : SET BREAK_ON_ERROR on_off",
"show : SHOW TRACE",
"show : SHOW DEBUG",
"show : SHOW BREAK_ON_ERROR",
"quit : EOF",
"quit : QUIT",
"print : PRINT list_expr opt_format",
"request : for select opt_format",
"for : FOR list_alias opt_with",
"opt_with :",
"opt_with : WITH list_alias",
"define : DEFINE alias",
"select : SELECT list_expr FROM list_file opt_where opt_group_by opt_break_on",
"opt_where :",
"opt_where : WHERE expr_cond",
"opt_group_by :",
"opt_group_by : GROUP_BY expr_cond HAVING expr_cond",
"opt_break_on :",
"opt_break_on : BREAK ON expr_cond",
"opt_format :",
"opt_format : FORMAT list_fmt",
"list_file : file",
"list_file : list_file ',' file",
"file : string",
"file : string AS IDENT",
"file : string WITH IDENT string",
"file : string AS IDENT WITH IDENT string",
"interval : '[' expr ',' expr ']'",
"list_fmt : ASCII",
"list_fmt : ASCII SEPARATED_BY STRING",
"list_fmt : ASCII AS list_fmt_ascii",
"list_fmt : ASCII AS list_fmt_ascii SEPARATED_BY STRING",
"list_fmt : BIN",
"list_fmt : BIN AS list_fmt_bin",
"list_fmt_ascii : fmt_ascii",
"list_fmt_ascii : list_fmt_ascii ',' fmt_ascii",
"fmt_ascii :",
"fmt_ascii : FMTASCII",
"list_fmt_bin : fmt_bin",
"list_fmt_bin : list_fmt_bin ',' fmt_bin",
"fmt_bin :",
"fmt_bin : IDENT",
"function : IDENT '(' list_expr ')'",
"list_expr : expr",
"list_expr : expr_cond",
"list_expr : alias",
"list_expr : list_expr ',' expr",
"list_expr : list_expr ',' expr_cond",
"list_expr : list_expr ',' alias",
"list_alias : alias",
"list_alias : list_alias ',' alias",
"alias : expr AS IDENT",
"alias : expr_cond AS IDENT",
"expr_cond : NOT expr_cond",
"expr_cond : expr_cond OR expr_cond",
"expr_cond : expr_cond AND expr_cond",
"expr_cond : expr '<' expr",
"expr_cond : expr '>' expr",
"expr_cond : expr NE expr",
"expr_cond : expr EQEQ expr",
"expr_cond : expr LE expr",
"expr_cond : expr GE expr",
"expr_cond : '(' expr_cond ')'",
"expr : value",
"expr : function",
"expr : ROWID",
"expr : ROWNUM",
"expr : expr '+' expr",
"expr : expr '-' expr",
"expr : expr '*' expr",
"expr : expr '/' expr",
"expr : expr '^' expr",
"expr : expr '%' expr",
"expr : '-' expr",
"expr : '(' expr ')'",
"value : null",
"value : number",
"value : ident",
"value : string",
"value : date",
"value : bool",
"value : hexa",
"value : _error",
"ident : IDENT",
"number : INTEGER",
"number : LONG",
"number : FLOAT",
"number : DOUBLE",
"string : STRING",
"date : DATE_ISO_A",
"date : DATE_ISO_B",
"hexa : HEXA",
"bool : BOOL_TRUE",
"bool : BOOL_FALSE",
"on_off : ON",
"on_off : OFF",
"_error : ERROR",
"null : NULL",
};

//#line 414 "syntax.y"

  private Yylex lexer;
  private Najo najo;
  private ParserError parserError = null;
  private PileLifo pileSyntax = new PileLifo(ParserSyntax.RACINE);
  
  private Boolean breakOnError = true;
  
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

  public Parser(Najo najo, Reader r) throws Exception {
    this.najo = najo;
    lexer = new Yylex(r, this);
  }

  public static Parser compile(Najo najo, Reader r) throws Exception {
    // parse reader entry
    Parser yyparser = new Parser(najo, r);
    yyparser.yydebug = najo.isDebugParsing();

    yyparser.yyparse();
    
    return yyparser;
  }

  public void yyerror (String error) {
     error = ((ParserSyntax) pileSyntax.pop()).definition();
     parserError = lexer.yyerror(error);
  }

  private void showError() {
    /* Mise à jour du literal */
    parserError.setLiteral(lexer.getLiteral());
    
    /* Affichage de l'erreur */
    System.out.println(parserError.getError());
    
    /* Sortie du parser */
    if (breakOnError) najo.bye(0);
  }
  
  public PileLifo getPile() {
     return pileSyntax;
  }
//#line 618 "Parser.java"
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
//#line 89 "syntax.y"
{ }
break;
case 2:
//#line 93 "syntax.y"
{ }
break;
case 3:
//#line 94 "syntax.y"
{ }
break;
case 4:
//#line 95 "syntax.y"
{ showError(); }
break;
case 5:
//#line 96 "syntax.y"
{ showError(); }
break;
case 6:
//#line 100 "syntax.y"
{ pileSyntax.pop(); najo.trace((Node)val_peek(0).obj);}
break;
case 7:
//#line 101 "syntax.y"
{ najo.trace((Node)val_peek(0).obj);}
break;
case 8:
//#line 102 "syntax.y"
{ najo.trace((Node)val_peek(0).obj);}
break;
case 9:
//#line 103 "syntax.y"
{
 	Node print = new Node(TypeNode.PRINT, (ListNodes)val_peek(0).obj);
 	najo.trace(print); 
 	najo.execute(lexer.getLiteral(), print);
 	}
break;
case 10:
//#line 108 "syntax.y"
{
 	Node request = new Node(TypeNode.REQUEST, (ListNodes)val_peek(0).obj);
 	najo.trace(request); 
 	najo.execute(lexer.getLiteral(), request);
 	}
break;
case 11:
//#line 113 "syntax.y"
{ najo.bye(0);}
break;
case 12:
//#line 117 "syntax.y"
{ 
      najo.setTrace((Boolean) najo.getValue((Node) val_peek(0).obj));
      yyval.obj = new Node(TypeNode.SET, "TRACE", (Node)val_peek(0).obj);
      }
break;
case 13:
//#line 121 "syntax.y"
{
      yydebug = true;
      yyval.obj = new Node(TypeNode.SET, "DEBUG", (Node)val_peek(0).obj);
      }
break;
case 14:
//#line 125 "syntax.y"
{
      breakOnError = ((Boolean )najo.getValue((Node) val_peek(0).obj));
      yyval.obj = new Node(TypeNode.SET, "BREAK_ON_ERROR", (Node)val_peek(0).obj);
      }
break;
case 15:
//#line 132 "syntax.y"
{ yyval.obj = 0;}
break;
case 16:
//#line 133 "syntax.y"
{ yyval.obj = 0;}
break;
case 17:
//#line 134 "syntax.y"
{ yyval.obj = 0;}
break;
case 18:
//#line 138 "syntax.y"
{}
break;
case 19:
//#line 139 "syntax.y"
{}
break;
case 20:
//#line 144 "syntax.y"
{                  /* optionnel */
      pileSyntax.pop();
      ListNodes list = new ListNodes("print", 2);
      list.add(new Node(TypeNode.LIST_COLUMN, (ListNodes)val_peek(1).obj));
      list.add((Node)val_peek(0).obj);
      yyval.obj = list;
      }
break;
case 21:
//#line 156 "syntax.y"
{                 /* optionnel */
      pileSyntax.pop();
      ListNodes list = new ListNodes("request", 3);
      list.add(new Node(TypeNode.FOR, (ListNodes)val_peek(2).obj));
      list.add(new Node(TypeNode.SELECT, (ListNodes)val_peek(1).obj));
      list.add((Node)val_peek(0).obj);
      yyval.obj = list;
      }
break;
case 22:
//#line 168 "syntax.y"
{                /* optionnel */
      pileSyntax.pop();
      ListNodes list = new ListNodes("for", 3);
      list.add(new Node(TypeNode.LIST_ALIAS, (ListNodes)val_peek(1).obj));
      list.add((Node)val_peek(0).obj);
      yyval.obj = list;
      }
break;
case 23:
//#line 179 "syntax.y"
{yyval.obj = INode.NODE_NULL;}
break;
case 24:
//#line 180 "syntax.y"
{
     pileSyntax.pop();
     yyval.obj = new Node(TypeNode.WITH, (ListNodes)val_peek(0).obj);
     }
break;
case 25:
//#line 187 "syntax.y"
{yyval.obj = new Node(TypeNode.DEFINE, "", (Node)val_peek(0).obj);}
break;
case 26:
//#line 197 "syntax.y"
{pileSyntax.pop();
       ListNodes list = new ListNodes("for", 3);
       list.add(new Node(TypeNode.LIST_COLUMN, (ListNodes)val_peek(5).obj));
       list.add(new Node(TypeNode.FROM, (ListNodes)val_peek(3).obj));
       list.add((Node)val_peek(2).obj);
       list.add((Node)val_peek(1).obj);
       list.add((Node)val_peek(0).obj);
       yyval.obj = list;
       }
break;
case 27:
//#line 210 "syntax.y"
{yyval.obj = INode.NODE_NULL;}
break;
case 28:
//#line 212 "syntax.y"
{ pileSyntax.pop(); yyval.obj = new Node(TypeNode.WHERE,"",(Node)val_peek(0).obj);}
break;
case 29:
//#line 217 "syntax.y"
{yyval.obj = INode.NODE_NULL;}
break;
case 30:
//#line 219 "syntax.y"
{pileSyntax.pop(); yyval.obj = new Node(TypeNode.GROUP_BY,"",(Node)val_peek(2).obj,(Node)val_peek(0).obj); }
break;
case 31:
//#line 224 "syntax.y"
{yyval.obj = INode.NODE_NULL;}
break;
case 32:
//#line 226 "syntax.y"
{yyval.obj = new Node(TypeNode.BREAK_ON,"",(Node)val_peek(0).obj);}
break;
case 33:
//#line 231 "syntax.y"
{yyval.obj = INode.NODE_NULL;}
break;
case 34:
//#line 233 "syntax.y"
{pileSyntax.pop(); yyval.obj = new Node(TypeNode.FORMAT, "", (Node)val_peek(0).obj);}
break;
case 35:
//#line 238 "syntax.y"
{ListNodes list = new ListNodes("list", 20);
       list.add((Node)val_peek(0).obj);
       yyval.obj = list;}
break;
case 36:
//#line 242 "syntax.y"
{((ListNodes)val_peek(2).obj).add((Node)val_peek(0).obj); yyval.obj = val_peek(2).obj;}
break;
case 37:
//#line 246 "syntax.y"
{yyval.obj = new Node(TypeNode.FILE,"",(Node)val_peek(0).obj);}
break;
case 38:
//#line 247 "syntax.y"
{yyval.obj = new Node(TypeNode.ALIAS_FILE,val_peek(0).sval,(Node)val_peek(2).obj);}
break;
case 39:
//#line 249 "syntax.y"
{yyval.obj = new Node(TypeNode.FILE,"",(Node)val_peek(3).obj,
             new Node(TypeNode.TYPE_FILE,val_peek(1).sval,(Node)val_peek(0).obj));}
break;
case 40:
//#line 252 "syntax.y"
{yyval.obj = new Node(TypeNode.ALIAS_FILE,val_peek(3).sval,(Node)val_peek(5).obj,
             new Node(TypeNode.TYPE_FILE, val_peek(1).sval,(Node)val_peek(0).obj));}
break;
case 41:
//#line 258 "syntax.y"
{yyval.obj = new Node(TypeNode.INTERVAL,"",(Node)val_peek(3).obj,(Node)val_peek(1).obj);}
break;
case 42:
//#line 262 "syntax.y"
{yyval.obj = new Node(TypeNode.LIST_FMT_ASCII,"default");}
break;
case 43:
//#line 263 "syntax.y"
{yyval.obj = new Node(TypeNode.LIST_FMT_ASCII_SEP,val_peek(0).sval);}
break;
case 44:
//#line 264 "syntax.y"
{yyval.obj = new Node(TypeNode.LIST_FMT_ASCII, (ListNodes)val_peek(0).obj);}
break;
case 45:
//#line 266 "syntax.y"
{yyval.obj = new Node(TypeNode.LIST_FMT_ASCII_SEP, val_peek(0).sval, 
               new Node(TypeNode.LIST_FMT_ASCII, (ListNodes)val_peek(2).obj));}
break;
case 46:
//#line 268 "syntax.y"
{yyval.obj = new Node(TypeNode.LIST_FMT_BIN,"default");}
break;
case 47:
//#line 269 "syntax.y"
{yyval.obj = new Node(TypeNode.LIST_FMT_BIN, (ListNodes)val_peek(0).obj);}
break;
case 48:
//#line 274 "syntax.y"
{ListNodes list = new ListNodes("list", 20);
       list.add((Node)val_peek(0).obj);
       yyval.obj = list;}
break;
case 49:
//#line 278 "syntax.y"
{((ListNodes)val_peek(2).obj).add((Node)val_peek(0).obj);
       yyval.obj = val_peek(2).obj;}
break;
case 50:
//#line 284 "syntax.y"
{yyval.obj = INode.NODE_NULL;}
break;
case 51:
//#line 285 "syntax.y"
{yyval.obj = new Node(TypeNode.FMT_ASCII,val_peek(0).sval);}
break;
case 52:
//#line 290 "syntax.y"
{ListNodes list = new ListNodes("list", 20); 
       list.add((Node)val_peek(0).obj); 
       yyval.obj = list;}
break;
case 53:
//#line 294 "syntax.y"
{((ListNodes)val_peek(2).obj).add((Node)val_peek(0).obj);
       yyval.obj = val_peek(2).obj;}
break;
case 54:
//#line 300 "syntax.y"
{yyval.obj = INode.NODE_NULL;}
break;
case 55:
//#line 301 "syntax.y"
{yyval.obj = new Node(TypeNode.FMT_BIN,val_peek(0).sval);}
break;
case 56:
//#line 306 "syntax.y"
{yyval.obj = new Node(TypeNode.FUNCTION, val_peek(3).sval, 
          new Node(TypeNode.LIST_EXPR, (ListNodes)val_peek(1).obj));}
break;
case 57:
//#line 311 "syntax.y"
{ListNodes list = new ListNodes("list", 20); list.add((Node)val_peek(0).obj); yyval.obj = list;}
break;
case 58:
//#line 312 "syntax.y"
{ListNodes list = new ListNodes("list", 20); list.add((Node)val_peek(0).obj); yyval.obj = list;}
break;
case 59:
//#line 313 "syntax.y"
{ListNodes list = new ListNodes("list", 20); list.add((Node)val_peek(0).obj); yyval.obj = list;}
break;
case 60:
//#line 314 "syntax.y"
{((ListNodes)val_peek(2).obj).add((Node)val_peek(0).obj); yyval.obj = val_peek(2).obj;}
break;
case 61:
//#line 315 "syntax.y"
{((ListNodes)val_peek(2).obj).add((Node)val_peek(0).obj); yyval.obj = val_peek(2).obj;}
break;
case 62:
//#line 316 "syntax.y"
{((ListNodes)val_peek(2).obj).add((Node)val_peek(0).obj); yyval.obj = val_peek(2).obj;}
break;
case 63:
//#line 321 "syntax.y"
{ListNodes list = new ListNodes("list", 20);
       list.add((Node)val_peek(0).obj);
       yyval.obj = list;}
break;
case 64:
//#line 324 "syntax.y"
{((ListNodes)val_peek(2).obj).add((Node)val_peek(0).obj); yyval.obj = val_peek(2).obj;}
break;
case 65:
//#line 328 "syntax.y"
{pileSyntax.pop(); yyval.obj = new NodeAlias(TypeNode.ALIAS, val_peek(0).sval, (Node)val_peek(2).obj);}
break;
case 66:
//#line 329 "syntax.y"
{pileSyntax.pop();  yyval.obj = new NodeAlias(TypeNode.ALIAS, val_peek(0).sval, (Node)val_peek(2).obj);}
break;
case 67:
//#line 333 "syntax.y"
{yyval.obj = new NodeCond(TypeCond.NOT,(Node)val_peek(0).obj);}
break;
case 68:
//#line 334 "syntax.y"
{yyval.obj = new NodeCond(TypeCond.OR,(Node) val_peek(2).obj, (Node) val_peek(0).obj);}
break;
case 69:
//#line 335 "syntax.y"
{yyval.obj = new NodeCond(TypeCond.AND,(Node) val_peek(2).obj, (Node) val_peek(0).obj);}
break;
case 70:
//#line 336 "syntax.y"
{yyval.obj = new NodeCond(TypeCond.LESS,(Node) val_peek(2).obj, (Node) val_peek(0).obj);}
break;
case 71:
//#line 337 "syntax.y"
{yyval.obj = new NodeCond(TypeCond.SUP,(Node) val_peek(2).obj, (Node) val_peek(0).obj);}
break;
case 72:
//#line 338 "syntax.y"
{yyval.obj = new NodeCond(TypeCond.NE, (Node) val_peek(2).obj, (Node) val_peek(0).obj);}
break;
case 73:
//#line 339 "syntax.y"
{yyval.obj = new NodeCond(TypeCond.EQEQ, (Node) val_peek(2).obj, (Node) val_peek(0).obj);}
break;
case 74:
//#line 340 "syntax.y"
{yyval.obj = new NodeCond(TypeCond.LE, (Node) val_peek(2).obj, (Node) val_peek(0).obj);}
break;
case 75:
//#line 341 "syntax.y"
{yyval.obj = new NodeCond(TypeCond.GE, (Node) val_peek(2).obj, (Node) val_peek(0).obj);}
break;
case 76:
//#line 342 "syntax.y"
{yyval.obj = val_peek(1).obj;}
break;
case 77:
//#line 346 "syntax.y"
{yyval.obj = val_peek(0).obj;}
break;
case 78:
//#line 347 "syntax.y"
{yyval.obj = val_peek(0).obj;}
break;
case 79:
//#line 348 "syntax.y"
{yyval.obj = new Node(TypeNode.ROWID, "rowid");}
break;
case 80:
//#line 349 "syntax.y"
{yyval.obj = new Node(TypeNode.ROWNUM, "rownum");}
break;
case 81:
//#line 350 "syntax.y"
{yyval.obj = new NodeMath(TypeMath.PLUS, (Node) val_peek(2).obj, (Node) val_peek(0).obj);}
break;
case 82:
//#line 351 "syntax.y"
{yyval.obj = new NodeMath(TypeMath.MINUS,(Node) val_peek(2).obj, (Node) val_peek(0).obj);}
break;
case 83:
//#line 352 "syntax.y"
{yyval.obj = new NodeMath(TypeMath.MULTIPLE, (Node) val_peek(2).obj, (Node) val_peek(0).obj);}
break;
case 84:
//#line 353 "syntax.y"
{yyval.obj = new NodeMath(TypeMath.DIVISE, (Node) val_peek(2).obj, (Node) val_peek(0).obj);}
break;
case 85:
//#line 354 "syntax.y"
{yyval.obj = new NodeMath(TypeMath.EXPONENT, (Node) val_peek(2).obj, (Node) val_peek(0).obj);}
break;
case 86:
//#line 355 "syntax.y"
{yyval.obj = new NodeMath(TypeMath.MODULO, (Node) val_peek(2).obj, (Node) val_peek(0).obj);}
break;
case 87:
//#line 356 "syntax.y"
{yyval.obj = new NodeMath(TypeMath.MUNAIRE, (Node)val_peek(0).obj);}
break;
case 88:
//#line 357 "syntax.y"
{yyval.obj = val_peek(1).obj;}
break;
case 89:
//#line 361 "syntax.y"
{yyval.obj = val_peek(0).obj;}
break;
case 90:
//#line 362 "syntax.y"
{yyval.obj = val_peek(0).obj;}
break;
case 91:
//#line 363 "syntax.y"
{yyval.obj = val_peek(0).obj;}
break;
case 92:
//#line 364 "syntax.y"
{yyval.obj = val_peek(0).obj;}
break;
case 93:
//#line 365 "syntax.y"
{yyval.obj = val_peek(0).obj;}
break;
case 94:
//#line 366 "syntax.y"
{yyval.obj = val_peek(0).obj;}
break;
case 95:
//#line 367 "syntax.y"
{yyval.obj = val_peek(0).obj;}
break;
case 96:
//#line 368 "syntax.y"
{yyval.obj = val_peek(0).obj;}
break;
case 97:
//#line 372 "syntax.y"
{yyval.obj = new Node(TypeNode.IDENT,val_peek(0).sval);}
break;
case 98:
//#line 376 "syntax.y"
{yyval.obj = new NodeValue(TypeValue.INTEGER,val_peek(0).sval); }
break;
case 99:
//#line 377 "syntax.y"
{yyval.obj = new NodeValue(TypeValue.LONG,val_peek(0).sval); }
break;
case 100:
//#line 378 "syntax.y"
{yyval.obj = new NodeValue(TypeValue.FLOAT,val_peek(0).sval); }
break;
case 101:
//#line 379 "syntax.y"
{yyval.obj = new NodeValue(TypeValue.DOUBLE,val_peek(0).sval); }
break;
case 102:
//#line 383 "syntax.y"
{yyval.obj = new NodeValue(TypeValue.STRING,val_peek(0).sval); }
break;
case 103:
//#line 387 "syntax.y"
{yyval.obj = new NodeValue(TypeValue.DATE,val_peek(0).sval); }
break;
case 104:
//#line 388 "syntax.y"
{yyval.obj = new NodeValue(TypeValue.DATE,val_peek(0).sval); }
break;
case 105:
//#line 392 "syntax.y"
{yyval.obj = new NodeValue(TypeValue.HEXA,val_peek(0).sval); }
break;
case 106:
//#line 396 "syntax.y"
{yyval.obj = new NodeValue(TypeValue.BOOL,"true"); }
break;
case 107:
//#line 397 "syntax.y"
{yyval.obj = new NodeValue(TypeValue.BOOL,"false"); }
break;
case 108:
//#line 401 "syntax.y"
{yyval.obj = new NodeValue(TypeValue.ON_OFF,"on"); }
break;
case 109:
//#line 402 "syntax.y"
{yyval.obj = new NodeValue(TypeValue.ON_OFF,"off"); }
break;
case 110:
//#line 406 "syntax.y"
{yyval.obj = new NodeValue(TypeValue.ERROR,val_peek(0).sval); }
break;
case 111:
//#line 410 "syntax.y"
{yyval.obj = INode.NODE_NULL;}
break;
//#line 1272 "Parser.java"
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
