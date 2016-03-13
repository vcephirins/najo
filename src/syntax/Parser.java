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
public final static short GENERATE=303;
public final static short METADATA=304;
public final static short EOF=305;
public final static short HELP=306;
public final static short SHOW=307;
public final static short TYPES_ALL=308;
public final static short TYPE_DEX=309;
public final static short LIBRARIES=310;
public final static short LIBRARIE=311;
public final static short FUNCTIONS=312;
public final static short FUNCTION=313;
public final static short MUNAIRE=314;
public final static short YYERRCODE=256;
final static short yylhs[] = {                           -1,
    0,    2,    2,    3,    3,    3,    3,    3,    3,    3,
    4,    4,    5,    5,    8,    8,    8,    6,    7,    9,
   19,   19,   20,   20,   10,   21,   21,   22,   22,   23,
   23,   24,   24,   25,   25,   26,   26,   26,   26,   33,
   33,   32,   38,   38,   38,   38,   38,   38,   36,   36,
   34,   34,   37,   37,   35,   35,   39,   29,   29,   29,
   29,   29,   29,   27,   27,   28,   28,   31,   31,   31,
   31,   31,   31,   31,   31,   31,   31,   30,   30,   30,
   30,   30,   30,   30,   30,   30,   30,   30,   30,   40,
   40,   40,   40,   40,   40,   40,   40,   11,   12,   12,
   12,   12,   13,   14,   14,   15,   16,   16,   18,   18,
    1,   17,
};
final static short yylen[] = {                            2,
    1,    1,    2,    1,    2,    1,    2,    1,    1,    1,
    3,    3,    2,    2,    1,    1,    2,    4,    4,    5,
    0,    2,    0,    2,    7,    0,    2,    0,    4,    0,
    3,    0,    2,    1,    3,    1,    3,    4,    6,    1,
    3,    5,    1,    3,    3,    5,    1,    3,    1,    3,
    0,    1,    1,    3,    0,    1,    4,    1,    1,    1,
    3,    3,    3,    1,    3,    3,    3,    2,    3,    3,
    3,    3,    3,    3,    3,    3,    3,    1,    1,    1,
    1,    3,    3,    3,    3,    3,    3,    2,    3,    1,
    1,    1,    1,    1,    1,    1,    1,    1,    1,    1,
    1,    1,    1,    1,    1,    1,    1,    1,    1,    1,
    1,    1,
};
final static short yydefred[] = {                         0,
    0,    0,    0,    0,   15,    0,    0,    0,    2,    0,
    0,    8,    9,   10,    0,   17,    0,    0,   99,  100,
  102,  101,  106,    0,  103,  104,  105,  107,  108,  112,
  111,   80,   81,    0,    0,    0,   97,   92,   91,   93,
   94,   96,   95,   90,   60,    0,    0,    0,   79,   78,
    0,   64,    0,    0,   13,   14,    3,    5,    7,    0,
    0,  109,  110,   11,   12,    0,    0,   68,    0,   88,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,   89,   77,
    0,    0,   33,   63,    0,    0,   18,   66,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
   67,   70,    0,    0,   65,    0,    0,    0,   19,   57,
    0,    0,    0,    0,   40,    0,   20,    0,    0,   34,
   56,   53,    0,   52,   49,    0,   44,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,   41,    0,
    0,    0,   35,    0,    0,   54,   46,   50,    0,    0,
   38,    0,    0,   25,   42,    0,    0,    0,   39,    0,
    0,
};
final static short yydgoto[] = {                          7,
   37,    8,    9,   10,   11,   12,   13,   14,   15,   61,
   38,   39,   40,   41,   42,   43,   44,   64,   94,  127,
  154,  165,  174,   75,  139,  140,   51,   45,   46,   67,
   48,  135,  136,  145,  142,  146,  143,  103,   49,   50,
};
final static short yysindex[] = {                      -195,
  -30, -242,  -40,  -40,    0, -217,    0, -195,    0,  -19,
  -15,    0,    0,    0, -233,    0, -184, -184,    0,    0,
    0,    0,    0,   12,    0,    0,    0,    0,    0,    0,
    0,    0,    0,  -40,  357,  -40,    0,    0,    0,    0,
    0,    0,    0,    0,    0,  -14,   -4, -252,    0,    0,
  -34,    0,   -4, -252,    0,    0,    0,    0,    0,  -40,
 -235,    0,    0,    0,    0,  -40,  120,    0,  357,    0,
    8,  -35, -186,  -40,    2, -193,  357,  357,  357,  357,
  357,  357,  357,  357,  357,  357,  357,  357, -175,  -40,
  -40,  -40,  -40, -171,  -42,   37,   42,   96,    0,    0,
 -167, -250,    0,    0,   -4, -252,    0,    0,  439,  439,
  439,  439,  439,  439,   25,   25,   23,   23,   23,   23,
    0,    0, -174,   79,    0,   40,   75, -130,    0,    0,
 -128, -126, -125,  357,    0,  111,    0, -172,  -36,    0,
    0,    0,  114,    0,    0,  -20,    0,  452,   40, -105,
 -104,  -40, -130, -120, -128,  -99, -126,  357,    0, -109,
 -130, -178,    0,  -40, -114,    0,    0,    0,  461,  -90,
    0, -262,  -96,    0,    0, -130,  -40,  -40,    0, -178,
 -178,
};
final static short yyrindex[] = {                         0,
    1,    0,    0,    0,    0,    0,    0,  176,    0,    4,
    7,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,  -28,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,  118,  142,  303,    0,    0,
  -48,    0,    0,    0,    0,    0,    0,    0,    0,    0,
  118,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,  125,    0,    0,    0,    0,    0,    0,
  132,  133,    0,    0,  391,  415,    0,    0,  169,  194,
  254,  278,  377,  404,  144,  316,   32,   56,   85,  109,
    0,    0,  429,   19,    0,    0,    0,    0,    0,    0,
  -24,  -41,    0,    0,    0,  134,    0,  229,   49,    0,
    0,    0,  135,    0,    0,  136,    0,    0,    0,    0,
    0,    0,    0,  -47,  -24,    0,  -41,    0,    0,  241,
    0,  102,    0,    0,   -2,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,  -37,
    5,
};
final static short yygindex[] = {                         0,
    0,    0,  171,    0,    0,    0,    0,    0,    0,    0,
    0,    0,   20,    0,    0,    0,    0,  179,    0,    0,
    0,    0,    0,  137,    0,   46,  108,   21,  -39,  665,
  604,   58,    0,   48,   53,    0,    0,    0,    0,    0,
};
final static int YYTABLESIZE=823;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         36,
   16,   74,   51,    4,   35,  100,    6,  153,   98,   93,
   21,   28,   98,   98,   98,   98,   98,   51,   98,   55,
   95,   29,  177,  157,   52,   89,   97,  132,   16,   74,
   98,   98,   88,   98,   55,   90,   91,   85,   83,   58,
   84,  133,   86,   59,   88,   90,   91,   60,   99,   85,
   83,   66,   84,   73,   86,   81,   30,   82,   17,   18,
  107,   88,    1,   31,   98,   98,   85,   81,   84,   82,
  108,   86,   84,   84,   84,   84,   84,   22,   84,    2,
    3,    4,  130,   55,   56,   74,   62,   63,  121,   87,
   84,   84,   85,   84,  104,  129,   85,   85,   85,   85,
   85,   87,   85,  101,  102,  150,  151,   26,  126,    5,
  131,    6,   52,  125,   85,   85,   87,   85,   87,   90,
   91,   86,   93,   90,   84,   86,   86,   86,   86,   86,
  134,   86,   88,  137,   25,  141,   99,   85,   83,  147,
   84,  144,   86,   86,   86,   87,   86,  138,   85,   87,
   87,   87,   87,   87,  149,   87,   88,  155,  160,  161,
   27,   85,   83,  164,   84,  167,   86,   87,   87,  170,
   87,  173,  138,  176,  178,    1,   32,   86,   57,   81,
  171,   82,   58,   23,   82,   58,   82,   82,   82,   87,
   47,   43,   24,   48,   45,  179,   65,   96,  163,  124,
   58,   87,   82,   82,  168,   82,  159,  166,    0,   75,
    0,    0,   75,   87,    0,    0,    0,    0,   19,   20,
   21,   22,   23,   24,   25,   26,   27,   75,   28,   29,
    0,   21,   30,   31,   76,    0,   82,   76,   28,    0,
  128,   28,    0,    0,   92,  152,   32,   33,   29,   98,
   51,   29,   76,    0,   98,   98,   98,   98,   16,   34,
   98,    4,   90,   91,    6,   98,   98,   98,   98,   98,
   98,  156,   36,   76,   73,   16,   16,   16,    4,    4,
    4,    6,    6,    6,   37,    0,   30,   36,    0,   77,
   78,   79,   80,   31,   73,    0,    0,   73,   22,   37,
    0,   77,   78,   79,   80,   16,    0,   16,    4,   84,
    4,    6,   73,    6,   84,   84,   84,   84,   74,    0,
   84,   74,    0,    0,    0,   84,   84,   84,   84,   84,
   84,    0,   26,   85,   26,    0,   74,   26,   85,   85,
   85,   85,    0,   59,   85,    0,   59,    0,    0,   85,
   85,   85,   85,   85,   85,    0,   83,    0,   83,   83,
   83,   59,   86,    0,    0,    0,    0,   86,   86,   86,
   86,    0,    0,   86,   83,   83,    0,   83,   86,   86,
   86,   86,   86,   86,    0,   27,   87,   27,    0,    0,
   27,   87,   87,   87,   87,    0,   69,   87,    0,    0,
    0,   35,   87,   87,   87,   87,   87,   87,   83,    0,
    0,    0,    0,   77,   78,   79,   80,   71,    0,    0,
   71,   82,    0,    0,   58,    0,   82,   82,   82,   82,
   58,   61,   82,    0,   61,   71,    0,   82,   82,   82,
   82,   82,   82,    0,   72,    0,   75,   72,    0,   61,
    0,   75,   75,   75,   75,   62,    0,   75,   62,    0,
    0,    0,   72,    0,    0,    0,   75,   75,    0,   69,
    0,   76,   69,   62,    0,   88,   76,   76,   76,   76,
   85,   83,   76,   84,    0,   86,    0,   69,   88,    0,
    0,   76,   76,   85,   83,  158,   84,   88,   86,    0,
    0,    0,   85,   83,    0,   84,    0,   86,    0,    0,
   36,    0,   36,    0,   36,    0,    0,   36,    0,    0,
    0,    0,   37,    0,   37,    0,   37,    0,    0,   37,
    0,   73,   87,    0,    0,    0,   73,   73,   73,   73,
    0,    0,   73,    0,    0,   87,    0,    0,    0,    0,
    0,   73,   73,  175,   87,   74,    0,    0,    0,    0,
   74,   74,   74,   74,    0,    0,   74,    0,    0,    0,
    0,    0,    0,    0,    0,   74,   74,    0,    0,    0,
    0,    0,    0,    0,    0,   59,    0,    0,    0,    0,
    0,   59,    0,   83,    0,    0,    0,    0,   83,   83,
   83,   83,    0,    0,   83,    0,    0,   54,    0,   83,
   83,   83,   83,   83,   83,   19,   20,   21,   22,   23,
   24,   25,   26,   27,    0,   28,   29,    0,    0,   30,
   31,    0,    0,    0,    0,    0,    0,   68,    0,   72,
    0,    0,    0,   32,   33,    0,    0,    0,    0,    0,
    0,    0,    0,    0,   71,    0,    0,    0,    0,   71,
   71,   71,   71,    0,    0,   71,    0,   47,   53,    0,
    0,    0,    0,   61,   71,   71,    0,  106,    0,   61,
    0,   72,    0,    0,    0,    0,   72,   72,   72,   72,
    0,    0,   72,  122,  123,   54,   54,   62,    0,   70,
   71,   72,   72,   62,    0,    0,   69,    0,    0,    0,
    0,   69,   69,   69,   69,    0,    0,   69,    0,    0,
    0,    0,    0,    0,   47,    0,    0,   69,    0,    0,
   47,    0,    0,   98,    0,    0,    0,    0,  105,    0,
    0,  109,  110,  111,  112,  113,  114,  115,  116,  117,
  118,  119,  120,    0,    0,  162,   53,   53,    0,    0,
    0,    0,    0,    0,    0,    0,    0,  172,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
  180,  181,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,  148,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,  169,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         40,
    0,   44,   44,    0,   45,   41,    0,   44,   37,   44,
   59,   59,   41,   42,   43,   44,   45,   59,   47,   44,
   60,   59,  285,   44,    4,  278,   66,  278,   59,   44,
   59,   60,   37,   62,   59,  298,  299,   42,   43,   59,
   45,  292,   47,   59,   37,  298,  299,  281,   41,   42,
   43,   40,   45,  289,   47,   60,   59,   62,  301,  302,
   59,   37,  258,   59,   93,   94,   42,   60,   37,   62,
  264,   47,   41,   42,   43,   44,   45,   59,   47,  275,
  276,  277,   41,  301,  302,   44,  271,  272,  264,   94,
   59,   60,   37,   62,   74,   59,   41,   42,   43,   44,
   45,   94,   47,  290,  291,  278,  279,   59,  280,  305,
  278,  307,   92,   93,   59,   60,   94,   62,   94,  298,
  299,   37,   44,  298,   93,   41,   42,   43,   44,   45,
   91,   47,   37,   59,  265,  264,   41,   42,   43,  265,
   45,  268,   47,   59,   60,   37,   62,  128,   93,   41,
   42,   43,   44,   45,   44,   47,   37,   44,  264,  264,
   59,   42,   43,  284,   45,  265,   47,   59,   60,  279,
   62,  286,  153,  264,  271,    0,   59,   93,    8,   60,
  161,   62,   41,   59,   41,   44,   43,   44,   45,   94,
   59,   59,   59,   59,   59,  176,   18,   61,  153,   92,
   59,   93,   59,   60,  157,   62,  149,  155,   -1,   41,
   -1,   -1,   44,   94,   -1,   -1,   -1,   -1,  259,  260,
  261,  262,  263,  264,  265,  266,  267,   59,  269,  270,
   -1,  280,  273,  274,   41,   -1,   93,   44,  286,   -1,
  283,  289,   -1,   -1,  279,  282,  287,  288,  286,  278,
  292,  289,   59,   -1,  283,  284,  285,  286,  258,  300,
  289,  258,  298,  299,  258,  294,  295,  296,  297,  298,
  299,  292,   44,  278,  289,  275,  276,  277,  275,  276,
  277,  275,  276,  277,   44,   -1,  289,   59,   -1,  294,
  295,  296,  297,  289,   41,   -1,   -1,   44,  280,   59,
   -1,  294,  295,  296,  297,  305,   -1,  307,  305,  278,
  307,  305,   59,  307,  283,  284,  285,  286,   41,   -1,
  289,   44,   -1,   -1,   -1,  294,  295,  296,  297,  298,
  299,   -1,  284,  278,  286,   -1,   59,  289,  283,  284,
  285,  286,   -1,   41,  289,   -1,   44,   -1,   -1,  294,
  295,  296,  297,  298,  299,   -1,   41,   -1,   43,   44,
   45,   59,  278,   -1,   -1,   -1,   -1,  283,  284,  285,
  286,   -1,   -1,  289,   59,   60,   -1,   62,  294,  295,
  296,  297,  298,  299,   -1,  284,  278,  286,   -1,   -1,
  289,  283,  284,  285,  286,   -1,   40,  289,   -1,   -1,
   -1,   45,  294,  295,  296,  297,  298,  299,   93,   -1,
   -1,   -1,   -1,  294,  295,  296,  297,   41,   -1,   -1,
   44,  278,   -1,   -1,  283,   -1,  283,  284,  285,  286,
  289,   41,  289,   -1,   44,   59,   -1,  294,  295,  296,
  297,  298,  299,   -1,   41,   -1,  278,   44,   -1,   59,
   -1,  283,  284,  285,  286,   41,   -1,  289,   44,   -1,
   -1,   -1,   59,   -1,   -1,   -1,  298,  299,   -1,   41,
   -1,  278,   44,   59,   -1,   37,  283,  284,  285,  286,
   42,   43,  289,   45,   -1,   47,   -1,   59,   37,   -1,
   -1,  298,  299,   42,   43,   44,   45,   37,   47,   -1,
   -1,   -1,   42,   43,   -1,   45,   -1,   47,   -1,   -1,
  282,   -1,  284,   -1,  286,   -1,   -1,  289,   -1,   -1,
   -1,   -1,  282,   -1,  284,   -1,  286,   -1,   -1,  289,
   -1,  278,   94,   -1,   -1,   -1,  283,  284,  285,  286,
   -1,   -1,  289,   -1,   -1,   94,   -1,   -1,   -1,   -1,
   -1,  298,  299,   93,   94,  278,   -1,   -1,   -1,   -1,
  283,  284,  285,  286,   -1,   -1,  289,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,  298,  299,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,  283,   -1,   -1,   -1,   -1,
   -1,  289,   -1,  278,   -1,   -1,   -1,   -1,  283,  284,
  285,  286,   -1,   -1,  289,   -1,   -1,    4,   -1,  294,
  295,  296,  297,  298,  299,  259,  260,  261,  262,  263,
  264,  265,  266,  267,   -1,  269,  270,   -1,   -1,  273,
  274,   -1,   -1,   -1,   -1,   -1,   -1,   34,   -1,   36,
   -1,   -1,   -1,  287,  288,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,  278,   -1,   -1,   -1,   -1,  283,
  284,  285,  286,   -1,   -1,  289,   -1,    3,    4,   -1,
   -1,   -1,   -1,  283,  298,  299,   -1,   74,   -1,  289,
   -1,  278,   -1,   -1,   -1,   -1,  283,  284,  285,  286,
   -1,   -1,  289,   90,   91,   92,   93,  283,   -1,   35,
   36,  298,  299,  289,   -1,   -1,  278,   -1,   -1,   -1,
   -1,  283,  284,  285,  286,   -1,   -1,  289,   -1,   -1,
   -1,   -1,   -1,   -1,   60,   -1,   -1,  299,   -1,   -1,
   66,   -1,   -1,   69,   -1,   -1,   -1,   -1,   74,   -1,
   -1,   77,   78,   79,   80,   81,   82,   83,   84,   85,
   86,   87,   88,   -1,   -1,  152,   92,   93,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,  164,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
  177,  178,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  134,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,  158,
};
}
final static short YYFINAL=7;
final static short YYMAXTOKEN=314;
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
"AND","OR","NOT","TRACE","DEBUG","GENERATE","METADATA","EOF","HELP","SHOW",
"TYPES_ALL","TYPE_DEX","LIBRARIES","LIBRARIE","FUNCTIONS","FUNCTION","MUNAIRE",
};
final static String yyrule[] = {
"$accept : racine",
"racine : list_commandes",
"list_commandes : commande",
"list_commandes : list_commandes commande",
"commande : set",
"commande : set ';'",
"commande : show",
"commande : show ';'",
"commande : print",
"commande : request",
"commande : quit",
"set : SET TRACE on_off",
"set : SET DEBUG on_off",
"show : SHOW TRACE",
"show : SHOW DEBUG",
"quit : EOF",
"quit : QUIT",
"quit : QUIT ';'",
"print : PRINT list_expr opt_format ';'",
"request : for select opt_format ';'",
"for : FOR list_alias opt_with opt_define ';'",
"opt_with :",
"opt_with : WITH list_alias",
"opt_define :",
"opt_define : DEFINE list_interval",
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
"list_interval : interval",
"list_interval : list_interval ',' interval",
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

//#line 411 "syntax.y"

  private Yylex lexer;
  private Najo najo;
  private ParserError parserError = null;
  private ParserSyntax parserSyntax = ParserSyntax.RACINE;;
  
  private int tokenpos = 0;
  private int numline = 1;
  private StringBuilder literal = new StringBuilder(200);
  private String nextToken = null;
  
	public void setTokenpos(int tokenpos) {
	    this.tokenpos = tokenpos;
	}
	
	public int getTokenpos() {
	    return tokenpos;
	}
	
	public void setNumline(int numline) {
	    this.numline = numline;
	}
	
	public int getNumline() {
	    return numline;
	}
	
	public void setLiteral(StringBuilder literal) {
	    this.literal = literal;
	}
	
	public String getLiteral() {
	    return literal.toString();
	}

    public void addYytext(String yytext) {
        literal.append(yytext);
        nextToken = yytext; // Sauvegarde du token en cours
    }

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
//     if (error == null) error = "";
//     error = error + "\n" + parserSyntax.definition();
     error = parserSyntax.definition();
     parserError = new ParserError(numline, tokenpos, literal.toString(), error);
  }

  public ParserError getParserError() {
     return parserError;
  }
  
  public void setParserSyntax(ParserSyntax parserSyntax) {
     this.parserSyntax = parserSyntax;
  }

  public ParserSyntax getParserSyntax() {
     return parserSyntax;
  }
//#line 673 "Parser.java"
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
//#line 94 "syntax.y"
{ }
break;
case 4:
//#line 99 "syntax.y"
{najo.trace((Node)val_peek(0).obj);}
break;
case 5:
//#line 100 "syntax.y"
{najo.trace((Node)val_peek(1).obj);}
break;
case 6:
//#line 101 "syntax.y"
{najo.trace((Node)val_peek(0).obj);}
break;
case 7:
//#line 102 "syntax.y"
{najo.trace((Node)val_peek(1).obj);}
break;
case 8:
//#line 103 "syntax.y"
{
 	Node print = new Node(TypeNode.PRINT, (ListNodes)val_peek(0).obj);
 	najo.trace(print); 
 	najo.execute(getLiteral(), print);
 	}
break;
case 9:
//#line 108 "syntax.y"
{
 	Node request = new Node(TypeNode.REQUEST, (ListNodes)val_peek(0).obj);
 	najo.trace(request); 
 	najo.execute(getLiteral(), request);
 	}
break;
case 10:
//#line 113 "syntax.y"
{najo.bye(0);}
break;
case 11:
//#line 117 "syntax.y"
{ yyval.obj = val_peek(0).obj; najo.setTrace((Boolean) najo.getValue((Node) val_peek(0).obj));}
break;
case 12:
//#line 118 "syntax.y"
{ yydebug = true; yyval.obj = val_peek(0).obj;}
break;
case 13:
//#line 122 "syntax.y"
{ yyval.obj = 0;}
break;
case 14:
//#line 123 "syntax.y"
{ yyval.obj = 0;}
break;
case 15:
//#line 127 "syntax.y"
{}
break;
case 16:
//#line 128 "syntax.y"
{}
break;
case 17:
//#line 129 "syntax.y"
{}
break;
case 18:
//#line 136 "syntax.y"
{ListNodes list = new ListNodes("print", 2);
       list.add(new Node(TypeNode.LIST_COLUMN, (ListNodes)val_peek(2).obj));
       list.add((Node)val_peek(1).obj);
       yyval.obj = list;}
break;
case 19:
//#line 147 "syntax.y"
{ListNodes list = new ListNodes("request", 3);
       list.add(new Node(TypeNode.FOR, (ListNodes)val_peek(3).obj));
       list.add(new Node(TypeNode.SELECT, (ListNodes)val_peek(2).obj));
       list.add((Node)val_peek(1).obj);
       yyval.obj = list;}
break;
case 20:
//#line 158 "syntax.y"
{ListNodes list = new ListNodes("for", 3);
       list.add(new Node(TypeNode.LIST_ALIAS, (ListNodes)val_peek(3).obj));
       list.add((Node)val_peek(2).obj);
       list.add((Node)val_peek(1).obj);
       yyval.obj = list;}
break;
case 21:
//#line 167 "syntax.y"
{yyval.obj = INode.NODE_NULL;}
break;
case 22:
//#line 169 "syntax.y"
{yyval.obj = new Node(TypeNode.WITH, (ListNodes)val_peek(0).obj);}
break;
case 23:
//#line 174 "syntax.y"
{yyval.obj = INode.NODE_NULL;}
break;
case 24:
//#line 176 "syntax.y"
{yyval.obj = new Node(TypeNode.DEFINE, (ListNodes)val_peek(0).obj);}
break;
case 25:
//#line 186 "syntax.y"
{ListNodes list = new ListNodes("for", 3);
       list.add(new Node(TypeNode.LIST_COLUMN, (ListNodes)val_peek(5).obj));
       list.add(new Node(TypeNode.FROM, (ListNodes)val_peek(3).obj));
       list.add((Node)val_peek(2).obj);
       list.add((Node)val_peek(1).obj);
       list.add((Node)val_peek(0).obj);
       yyval.obj = list;}
break;
case 26:
//#line 197 "syntax.y"
{yyval.obj = INode.NODE_NULL;}
break;
case 27:
//#line 199 "syntax.y"
{yyval.obj = new Node(TypeNode.WHERE,"where",(Node)val_peek(0).obj);}
break;
case 28:
//#line 204 "syntax.y"
{yyval.obj = INode.NODE_NULL;}
break;
case 29:
//#line 206 "syntax.y"
{yyval.obj = new Node(TypeNode.GROUP_BY,"group_by",(Node)val_peek(2).obj,(Node)val_peek(0).obj); }
break;
case 30:
//#line 211 "syntax.y"
{yyval.obj = INode.NODE_NULL;}
break;
case 31:
//#line 213 "syntax.y"
{yyval.obj = new Node(TypeNode.BREAK_ON,"break_on",(Node)val_peek(0).obj);}
break;
case 32:
//#line 218 "syntax.y"
{yyval.obj = INode.NODE_NULL;}
break;
case 33:
//#line 220 "syntax.y"
{yyval.obj = new Node(TypeNode.FORMAT, "format", (Node)val_peek(0).obj);}
break;
case 34:
//#line 225 "syntax.y"
{ListNodes list = new ListNodes("list", 20);
       list.add((Node)val_peek(0).obj);
       yyval.obj = list;}
break;
case 35:
//#line 229 "syntax.y"
{((ListNodes)val_peek(2).obj).add((Node)val_peek(0).obj); yyval.obj = val_peek(2).obj;}
break;
case 36:
//#line 233 "syntax.y"
{yyval.obj = new Node(TypeNode.FILE,"file",(Node)val_peek(0).obj);}
break;
case 37:
//#line 234 "syntax.y"
{yyval.obj = new Node(TypeNode.ALIAS_FILE,val_peek(0).sval,(Node)val_peek(2).obj);}
break;
case 38:
//#line 236 "syntax.y"
{yyval.obj = new Node(TypeNode.FILE,"file",(Node)val_peek(3).obj,
             new Node(TypeNode.TYPE_FILE,val_peek(1).sval,(Node)val_peek(0).obj));}
break;
case 39:
//#line 239 "syntax.y"
{yyval.obj = new Node(TypeNode.ALIAS_FILE,val_peek(3).sval,(Node)val_peek(5).obj,
             new Node(TypeNode.TYPE_FILE, val_peek(1).sval,(Node)val_peek(0).obj));}
break;
case 40:
//#line 245 "syntax.y"
{ListNodes list = new ListNodes("list", 20);
       list.add((Node)val_peek(0).obj);
        yyval.obj = list;}
break;
case 41:
//#line 249 "syntax.y"
{((ListNodes)val_peek(2).obj).add((Node)val_peek(0).obj);
       yyval.obj = val_peek(2).obj;}
break;
case 42:
//#line 255 "syntax.y"
{yyval.obj = new Node(TypeNode.INTERVAL,"interval",(Node)val_peek(3).obj,(Node)val_peek(1).obj);}
break;
case 43:
//#line 259 "syntax.y"
{yyval.obj = new Node(TypeNode.LIST_FMT_ASCII,"default");}
break;
case 44:
//#line 260 "syntax.y"
{yyval.obj = new Node(TypeNode.LIST_FMT_ASCII_SEP,val_peek(0).sval);}
break;
case 45:
//#line 261 "syntax.y"
{yyval.obj = new Node(TypeNode.LIST_FMT_ASCII, (ListNodes)val_peek(0).obj);}
break;
case 46:
//#line 263 "syntax.y"
{yyval.obj = new Node(TypeNode.LIST_FMT_ASCII_SEP, val_peek(0).sval, 
               new Node(TypeNode.LIST_FMT_ASCII, (ListNodes)val_peek(2).obj));}
break;
case 47:
//#line 265 "syntax.y"
{yyval.obj = new Node(TypeNode.LIST_FMT_BIN,"default");}
break;
case 48:
//#line 266 "syntax.y"
{yyval.obj = new Node(TypeNode.LIST_FMT_BIN, (ListNodes)val_peek(0).obj);}
break;
case 49:
//#line 271 "syntax.y"
{ListNodes list = new ListNodes("list", 20);
       list.add((Node)val_peek(0).obj);
       yyval.obj = list;}
break;
case 50:
//#line 275 "syntax.y"
{((ListNodes)val_peek(2).obj).add((Node)val_peek(0).obj);
       yyval.obj = val_peek(2).obj;}
break;
case 51:
//#line 281 "syntax.y"
{yyval.obj = INode.NODE_NULL;}
break;
case 52:
//#line 282 "syntax.y"
{yyval.obj = new Node(TypeNode.FMT_ASCII,val_peek(0).sval);}
break;
case 53:
//#line 287 "syntax.y"
{ListNodes list = new ListNodes("list", 20); 
       list.add((Node)val_peek(0).obj); 
       yyval.obj = list;}
break;
case 54:
//#line 291 "syntax.y"
{((ListNodes)val_peek(2).obj).add((Node)val_peek(0).obj);
       yyval.obj = val_peek(2).obj;}
break;
case 55:
//#line 297 "syntax.y"
{yyval.obj = INode.NODE_NULL;}
break;
case 56:
//#line 298 "syntax.y"
{yyval.obj = new Node(TypeNode.FMT_BIN,val_peek(0).sval);}
break;
case 57:
//#line 303 "syntax.y"
{yyval.obj = new Node(TypeNode.FUNCTION, val_peek(3).sval, 
          new Node(TypeNode.LIST_EXPR, (ListNodes)val_peek(1).obj));}
break;
case 58:
//#line 308 "syntax.y"
{ListNodes list = new ListNodes("list", 20); list.add((Node)val_peek(0).obj); yyval.obj = list;}
break;
case 59:
//#line 309 "syntax.y"
{ListNodes list = new ListNodes("list", 20); list.add((Node)val_peek(0).obj); yyval.obj = list;}
break;
case 60:
//#line 310 "syntax.y"
{ListNodes list = new ListNodes("list", 20); list.add((Node)val_peek(0).obj); yyval.obj = list;}
break;
case 61:
//#line 311 "syntax.y"
{((ListNodes)val_peek(2).obj).add((Node)val_peek(0).obj); yyval.obj = val_peek(2).obj;}
break;
case 62:
//#line 312 "syntax.y"
{((ListNodes)val_peek(2).obj).add((Node)val_peek(0).obj); yyval.obj = val_peek(2).obj;}
break;
case 63:
//#line 313 "syntax.y"
{((ListNodes)val_peek(2).obj).add((Node)val_peek(0).obj); yyval.obj = val_peek(2).obj;}
break;
case 64:
//#line 318 "syntax.y"
{ListNodes list = new ListNodes("list", 20);
       list.add((Node)val_peek(0).obj);
       yyval.obj = list;}
break;
case 65:
//#line 321 "syntax.y"
{((ListNodes)val_peek(2).obj).add((Node)val_peek(0).obj); yyval.obj = val_peek(2).obj;}
break;
case 66:
//#line 325 "syntax.y"
{yyval.obj = new NodeAlias(TypeNode.ALIAS, val_peek(0).sval, (Node)val_peek(2).obj);}
break;
case 67:
//#line 326 "syntax.y"
{yyval.obj = new NodeAlias(TypeNode.ALIAS, val_peek(0).sval, (Node)val_peek(2).obj);}
break;
case 68:
//#line 330 "syntax.y"
{yyval.obj = new NodeCond(TypeCond.NOT,(Node)val_peek(0).obj);}
break;
case 69:
//#line 331 "syntax.y"
{yyval.obj = new NodeCond(TypeCond.OR,(Node) val_peek(2).obj, (Node) val_peek(0).obj);}
break;
case 70:
//#line 332 "syntax.y"
{yyval.obj = new NodeCond(TypeCond.AND,(Node) val_peek(2).obj, (Node) val_peek(0).obj);}
break;
case 71:
//#line 333 "syntax.y"
{yyval.obj = new NodeCond(TypeCond.LESS,(Node) val_peek(2).obj, (Node) val_peek(0).obj);}
break;
case 72:
//#line 334 "syntax.y"
{yyval.obj = new NodeCond(TypeCond.SUP,(Node) val_peek(2).obj, (Node) val_peek(0).obj);}
break;
case 73:
//#line 335 "syntax.y"
{yyval.obj = new NodeCond(TypeCond.NE, (Node) val_peek(2).obj, (Node) val_peek(0).obj);}
break;
case 74:
//#line 336 "syntax.y"
{yyval.obj = new NodeCond(TypeCond.EQEQ, (Node) val_peek(2).obj, (Node) val_peek(0).obj);}
break;
case 75:
//#line 337 "syntax.y"
{yyval.obj = new NodeCond(TypeCond.LE, (Node) val_peek(2).obj, (Node) val_peek(0).obj);}
break;
case 76:
//#line 338 "syntax.y"
{yyval.obj = new NodeCond(TypeCond.GE, (Node) val_peek(2).obj, (Node) val_peek(0).obj);}
break;
case 77:
//#line 339 "syntax.y"
{yyval.obj = val_peek(1).obj;}
break;
case 78:
//#line 343 "syntax.y"
{yyval.obj = val_peek(0).obj;}
break;
case 79:
//#line 344 "syntax.y"
{yyval.obj = val_peek(0).obj;}
break;
case 80:
//#line 345 "syntax.y"
{yyval.obj = new Node(TypeNode.ROWID, "rowid");}
break;
case 81:
//#line 346 "syntax.y"
{yyval.obj = new Node(TypeNode.ROWNUM, "rownum");}
break;
case 82:
//#line 347 "syntax.y"
{yyval.obj = new NodeMath(TypeMath.PLUS, (Node) val_peek(2).obj, (Node) val_peek(0).obj);}
break;
case 83:
//#line 348 "syntax.y"
{yyval.obj = new NodeMath(TypeMath.MINUS,(Node) val_peek(2).obj, (Node) val_peek(0).obj);}
break;
case 84:
//#line 349 "syntax.y"
{yyval.obj = new NodeMath(TypeMath.MULTIPLE, (Node) val_peek(2).obj, (Node) val_peek(0).obj);}
break;
case 85:
//#line 350 "syntax.y"
{yyval.obj = new NodeMath(TypeMath.DIVISE, (Node) val_peek(2).obj, (Node) val_peek(0).obj);}
break;
case 86:
//#line 351 "syntax.y"
{yyval.obj = new NodeMath(TypeMath.EXPONENT, (Node) val_peek(2).obj, (Node) val_peek(0).obj);}
break;
case 87:
//#line 352 "syntax.y"
{yyval.obj = new NodeMath(TypeMath.MODULO, (Node) val_peek(2).obj, (Node) val_peek(0).obj);}
break;
case 88:
//#line 353 "syntax.y"
{yyval.obj = new NodeMath(TypeMath.MUNAIRE, (Node)val_peek(0).obj);}
break;
case 89:
//#line 354 "syntax.y"
{yyval.obj = val_peek(1).obj;}
break;
case 90:
//#line 358 "syntax.y"
{yyval.obj = val_peek(0).obj;}
break;
case 91:
//#line 359 "syntax.y"
{yyval.obj = val_peek(0).obj;}
break;
case 92:
//#line 360 "syntax.y"
{yyval.obj = val_peek(0).obj;}
break;
case 93:
//#line 361 "syntax.y"
{yyval.obj = val_peek(0).obj;}
break;
case 94:
//#line 362 "syntax.y"
{yyval.obj = val_peek(0).obj;}
break;
case 95:
//#line 363 "syntax.y"
{yyval.obj = val_peek(0).obj;}
break;
case 96:
//#line 364 "syntax.y"
{yyval.obj = val_peek(0).obj;}
break;
case 97:
//#line 365 "syntax.y"
{yyval.obj = val_peek(0).obj;}
break;
case 98:
//#line 369 "syntax.y"
{yyval.obj = new Node(TypeNode.IDENT,val_peek(0).sval);}
break;
case 99:
//#line 373 "syntax.y"
{yyval.obj = new NodeValue(TypeValue.INTEGER,val_peek(0).sval); }
break;
case 100:
//#line 374 "syntax.y"
{yyval.obj = new NodeValue(TypeValue.LONG,val_peek(0).sval); }
break;
case 101:
//#line 375 "syntax.y"
{yyval.obj = new NodeValue(TypeValue.FLOAT,val_peek(0).sval); }
break;
case 102:
//#line 376 "syntax.y"
{yyval.obj = new NodeValue(TypeValue.DOUBLE,val_peek(0).sval); }
break;
case 103:
//#line 380 "syntax.y"
{yyval.obj = new NodeValue(TypeValue.STRING,val_peek(0).sval); }
break;
case 104:
//#line 384 "syntax.y"
{yyval.obj = new NodeValue(TypeValue.DATE,val_peek(0).sval); }
break;
case 105:
//#line 385 "syntax.y"
{yyval.obj = new NodeValue(TypeValue.DATE,val_peek(0).sval); }
break;
case 106:
//#line 389 "syntax.y"
{yyval.obj = new NodeValue(TypeValue.HEXA,val_peek(0).sval); }
break;
case 107:
//#line 393 "syntax.y"
{yyval.obj = new NodeValue(TypeValue.BOOL,"true"); }
break;
case 108:
//#line 394 "syntax.y"
{yyval.obj = new NodeValue(TypeValue.BOOL,"false"); }
break;
case 109:
//#line 398 "syntax.y"
{yyval.obj = new NodeValue(TypeValue.ON_OFF,"on"); }
break;
case 110:
//#line 399 "syntax.y"
{yyval.obj = new NodeValue(TypeValue.ON_OFF,"off"); }
break;
case 111:
//#line 403 "syntax.y"
{yyval.obj = new NodeValue(TypeValue.ERROR,val_peek(0).sval); }
break;
case 112:
//#line 407 "syntax.y"
{yyval.obj = INode.NODE_NULL;}
break;
//#line 1308 "Parser.java"
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
