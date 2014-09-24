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



package grammaire;



//#line 26 "najo.y"
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
  
//#line 35 "Parser.java"




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
    0,    0,    2,    2,    2,    2,    2,    2,    2,    2,
    2,    3,    3,    3,    4,    4,    4,    7,    7,    7,
    5,    5,    6,    8,   18,   18,   19,   19,    9,   20,
   20,   21,   21,   22,   22,   23,   23,   23,   24,   24,
   25,   25,   25,   25,   32,   32,   31,   37,   37,   37,
   37,   37,   37,   35,   35,   33,   33,   33,   36,   36,
   34,   34,   38,   28,   28,   28,   28,   28,   28,   26,
   26,   27,   27,   30,   30,   30,   30,   30,   30,   30,
   30,   30,   30,   29,   29,   29,   29,   29,   29,   29,
   29,   29,   29,   29,   29,   39,   39,   39,   39,   39,
   39,   39,   39,   10,   11,   11,   11,   11,   12,   13,
   13,   14,   15,   15,   17,   17,    1,   16,
};
final static short yylen[] = {                            2,
    1,    2,    1,    2,    1,    2,    1,    1,    1,    1,
    2,    3,    3,    3,    2,    2,    2,    1,    1,    2,
    4,    2,    4,    5,    0,    2,    0,    2,    7,    0,
    2,    0,    4,    0,    3,    0,    2,    2,    1,    3,
    1,    3,    4,    6,    1,    3,    5,    1,    3,    3,
    5,    1,    3,    1,    3,    0,    1,    1,    1,    3,
    0,    1,    4,    1,    1,    1,    3,    3,    3,    1,
    3,    3,    3,    2,    3,    3,    3,    3,    3,    3,
    3,    3,    3,    1,    1,    1,    1,    3,    3,    3,
    3,    3,    3,    2,    3,    1,    1,    1,    1,    1,
    1,    1,    1,    1,    1,    1,    1,    1,    1,    1,
    1,    1,    1,    1,    1,    1,    1,    1,
};
final static short yydefred[] = {                         0,
    0,    0,    0,    0,    0,   18,    0,    0,    1,    0,
    0,    7,    8,    9,    0,   11,   20,    0,    0,    0,
   22,  105,  106,  108,  107,  112,    0,  109,  110,  111,
  113,  114,  118,  117,   86,   87,    0,    0,    0,  103,
   98,   97,   99,  100,  102,  101,   96,   66,    0,    0,
    0,   85,   84,    0,   70,    0,    0,   17,   15,   16,
    2,    4,    6,    0,    0,  115,  116,   14,   12,   13,
    0,    0,   74,    0,   94,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,   95,   83,   38,    0,    0,   37,   69,
    0,    0,   21,   72,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,   73,   76,    0,    0,
   71,    0,    0,    0,   23,   63,    0,    0,    0,    0,
   45,    0,   24,    0,    0,   39,   62,   59,    0,   58,
   57,   54,    0,   49,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,   46,    0,    0,    0,   40,
    0,    0,   60,   51,   55,    0,    0,   43,    0,    0,
   29,   47,    0,    0,    0,   44,    0,    0,
};
final static short yydgoto[] = {                          8,
   40,    9,   10,   11,   12,   13,   14,   15,   65,   41,
   42,   43,   44,   45,   46,   47,   68,   99,  133,  161,
  172,  181,   80,  145,  146,   54,   48,   49,   72,   51,
  141,  142,  152,  148,  153,  149,  109,   52,   53,
};
final static short yysindex[] = {                       -68,
  -54,  -38, -203,  186,  202,    0, -186,  -68,    0,  -27,
   -8,    0,    0,    0, -227,    0,    0, -184, -184, -184,
    0,    0,    0,    0,    0,    0,   41,    0,    0,    0,
    0,    0,    0,    0,    0,    0,  202,  554,  202,    0,
    0,    0,    0,    0,    0,    0,    0,    0,  -44,  -25,
 -251,    0,    0,  -41,    0,  -25, -251,    0,    0,    0,
    0,    0,    0,  202, -206,    0,    0,    0,    0,    0,
  202,  450,    0,  554,    0,   -3,  -10, -223,  202,   33,
 -159,  554,  554,  554,  554,  554,  554,  554,  554,  554,
  554,  554,  554, -157,  202,  202,  202,  202, -171,  -42,
   53,  -15,   19,    0,    0,    0, -164, -229,    0,    0,
  -25, -251,    0,    0,  180,  180,  180,  180,  180,  180,
   -1,   -1,   23,   23,   23,   23,    0,    0, -173,   91,
    0,   46,   85, -120,    0,    0, -118, -182, -103,  554,
    0,  103,    0, -189,  -30,    0,    0,    0,  119,    0,
    0,    0,  -31,    0,  122,   46,  -96,  -94,  202, -120,
 -113, -118,  -87, -182,  554,    0,  -99, -120, -188,    0,
  202, -105,    0,    0,    0,  540,  -73,    0, -214,  -78,
    0,    0, -120,  202,  202,    0, -188, -188,
};
final static short yyrindex[] = {                         0,
    1,    4,    0,    0,    0,    0,    0,    0,    0,    7,
   10,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,   35,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,  135,  -16,
   14,    0,    0,  -48,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,  135,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,  136,    0,
    0,    0,    0,    0,    0,    0,  137,  138,    0,    0,
   79,  133,    0,    0,  257,  281,  359,  465,  489,  513,
  320,  439,   59,   89,  113,  142,    0,    0,  -35,  -40,
    0,    0,    0,    0,    0,    0,  -29,  -36,    0,    0,
    0,  139,    0,  231,   16,    0,    0,    0,  140,    0,
    0,    0,  141,    0,    0,    0,    0,    0,    0,    0,
   49,  -29,    0,  -36,    0,    0,  292,    0,   63,    0,
    0,  -43,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,   80,    6,
};
final static short yygindex[] = {                         0,
    0,  195,    0,    0,    0,    0,    0,    0,    0,    0,
    0,   -7,    0,    0,    0,    0,  121,    0,    0,    0,
    0,    0,  145,    0,   51,  108,   45,  -19,  765,  534,
   56,    0,   50,   57,    0,    0,    0,    0,    0,
};
final static int YYTABLESIZE=930;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         79,
   10,   79,   98,   19,   16,   75,    3,   56,   75,    5,
   25,   93,  164,  160,   61,   34,   90,   88,   26,   89,
   17,   91,   56,   75,   64,  136,   94,   64,   79,   61,
  105,   62,  106,   93,   86,   93,   87,  104,   90,   88,
   90,   89,   64,   91,  100,   91,   95,   96,  138,   55,
   63,  102,   18,   64,   65,   93,   86,   65,   87,  104,
   90,   88,  139,   89,   35,   91,  107,  108,   92,   58,
  184,  104,   65,  150,   30,  104,  104,  104,  104,  104,
   71,  104,   78,   95,   96,  151,   66,   67,  157,  158,
   92,  113,   92,  104,  104,   90,  104,   19,   20,   90,
   90,   90,   90,   90,  114,   90,  127,   32,  132,   95,
   96,  135,   92,  137,   59,   60,   92,   90,   90,   67,
   90,   31,   67,  110,   95,   91,  144,  104,  104,   91,
   91,   91,   91,   91,   98,   91,  140,   67,   33,   69,
   70,   55,  131,  143,   28,  147,  156,   91,   91,   92,
   91,   90,  144,   92,   92,   92,   92,   92,   93,   92,
  178,  154,  162,   90,   88,  165,   89,  167,   91,  168,
  171,   92,   92,   68,   92,  186,   68,  174,   93,  177,
  180,   91,   93,   93,   93,   93,   93,    1,   93,    2,
  183,   68,  185,   36,   27,   52,   48,   28,   53,   50,
   93,   93,   61,   93,  130,   92,    3,    4,    5,  101,
  170,  166,    0,  175,    0,   92,   93,    0,  173,    0,
    0,   90,   88,    0,   89,   39,   91,    0,    0,    0,
   38,   25,    0,    0,   93,    0,    6,   97,    7,   26,
  134,   39,   75,    0,   78,   34,   38,   75,   75,   75,
   75,  159,   81,   75,    0,   56,   10,    0,   10,   19,
  163,   19,    3,   75,    3,    5,   64,    5,   82,   83,
   84,   85,   64,   92,   41,   10,   10,   10,   19,   19,
   19,    3,    3,    3,    5,    5,    5,   95,   96,   41,
   82,   83,   84,   85,   35,    0,   65,   81,    0,   30,
   81,   30,   65,    0,   30,   10,    0,   10,   19,    0,
   19,    3,  104,    3,    5,   81,    5,  104,  104,  104,
  104,   82,    0,  104,   82,    0,    0,    0,  104,  104,
  104,  104,  104,  104,   32,   42,   90,   32,    0,   82,
    0,   90,   90,   90,   90,    0,   31,   90,   31,    0,
   42,   31,   90,   90,   90,   90,   90,   90,    0,    0,
   88,   67,   88,   88,   88,   33,   91,   67,   33,    0,
    0,   91,   91,   91,   91,    0,    0,   91,   88,   88,
    0,   88,   91,   91,   91,   91,   91,   91,    0,    0,
   92,    0,    0,    0,    0,   92,   92,   92,   92,   79,
    0,   92,   79,    0,    0,    0,   92,   92,   92,   92,
   92,   92,   88,    0,    0,   68,    0,   79,    0,   93,
    0,   68,    0,    0,   93,   93,   93,   93,    0,    0,
   93,    0,    0,    0,    0,   93,   93,   93,   93,   93,
   93,   21,    0,    0,   22,   23,   24,   25,   26,   27,
   28,   29,   30,    0,   31,   32,    0,    0,   33,   34,
   22,   23,   24,   25,   26,   27,   28,   29,   30,    0,
   31,   32,   35,   36,   33,   34,    0,    0,    0,   89,
    0,   89,   89,   89,    0,   37,   93,    0,   35,   36,
    0,   90,   88,    0,   89,    0,   91,   89,   89,    0,
   89,   37,    0,    0,    0,   80,    0,    0,   80,   86,
    0,   87,   41,    0,   41,    0,   41,    0,    0,   41,
    0,    0,    0,   80,    0,    0,    0,    0,    0,   77,
    0,   89,   77,    0,   81,    0,    0,    0,   57,   81,
   81,   81,   81,   92,    0,   81,    0,   77,    0,    0,
    0,    0,    0,   78,   81,   81,   78,    0,   82,    0,
    0,    0,    0,   82,   82,   82,   82,    0,    0,   82,
   73,   78,   77,   42,    0,   42,   93,   42,   82,   82,
   42,   90,   88,    0,   89,    0,   91,    0,    0,    0,
    0,    0,    0,   74,    0,    0,    0,   88,   38,    0,
    0,    0,   88,   88,   88,   88,    0,    0,   88,    0,
    0,    0,  112,   88,   88,   88,   88,   88,   88,    0,
    0,    0,    0,    0,    0,    0,    0,    0,  128,  129,
   57,   57,  182,   92,    0,    0,   79,    0,    0,    0,
    0,   79,   79,   79,   79,    0,    0,   79,    0,    0,
    0,    0,    0,    0,    0,    0,   79,   79,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,  169,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,  179,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,   89,  187,  188,    0,
    0,   89,   89,   89,   89,    0,    0,   89,    0,    0,
    0,    0,   89,   89,   89,   89,   89,   89,    0,    0,
    0,    0,   80,   82,   83,   84,   85,   80,   80,   80,
   80,    0,    0,   80,    0,    0,    0,    0,    0,    0,
    0,    0,   80,   80,    0,    0,   77,    0,   50,   56,
    0,   77,   77,   77,   77,    0,    0,   77,    0,    0,
    0,    0,    0,    0,    0,    0,   77,   77,    0,    0,
   78,    0,    0,    0,    0,   78,   78,   78,   78,    0,
    0,   78,   75,   76,    0,    0,    0,    0,    0,    0,
   78,   78,   22,   23,   24,   25,   26,   27,   28,   29,
   30,    0,   31,   32,    0,    0,   33,   34,   50,    0,
    0,    0,    0,    0,    0,   50,    0,    0,  103,    0,
   35,   36,    0,  111,    0,    0,  115,  116,  117,  118,
  119,  120,  121,  122,  123,  124,  125,  126,    0,    0,
    0,   56,   56,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,  155,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,  176,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         44,
    0,   44,   44,    0,   59,   41,    0,   44,   44,    0,
   59,   37,   44,   44,   44,   59,   42,   43,   59,   45,
   59,   47,   59,   59,   41,   41,  278,   44,   44,   59,
   41,   59,  256,   37,   60,   37,   62,   41,   42,   43,
   42,   45,   59,   47,   64,   47,  298,  299,  278,    5,
   59,   71,  256,  281,   41,   37,   60,   44,   62,   41,
   42,   43,  292,   45,   59,   47,  290,  291,   94,  256,
  285,   37,   59,  256,   59,   41,   42,   43,   44,   45,
   40,   47,  289,  298,  299,  268,  271,  272,  278,  279,
   94,   59,   94,   59,   60,   37,   62,  301,  302,   41,
   42,   43,   44,   45,  264,   47,  264,   59,  280,  298,
  299,   59,   94,  278,  301,  302,   94,   59,   60,   41,
   62,   59,   44,   79,  298,   37,  134,   93,   94,   41,
   42,   43,   44,   45,   44,   47,   91,   59,   59,   19,
   20,   97,   98,   59,  265,  264,   44,   59,   60,   37,
   62,   93,  160,   41,   42,   43,   44,   45,   37,   47,
  168,  265,   44,   42,   43,   44,   45,  264,   47,  264,
  284,   59,   60,   41,   62,  183,   44,  265,   37,  279,
  286,   93,   41,   42,   43,   44,   45,  256,   47,  258,
  264,   59,  271,   59,   59,   59,   59,   59,   59,   59,
   59,   60,    8,   62,   97,   93,  275,  276,  277,   65,
  160,  156,   -1,  164,   -1,   94,   37,   -1,  162,   -1,
   -1,   42,   43,   -1,   45,   40,   47,   -1,   -1,   -1,
   45,  280,   -1,   -1,   93,   -1,  305,  279,  307,  280,
  283,   40,  278,   -1,  289,  289,   45,  283,  284,  285,
  286,  282,  278,  289,   -1,  292,  256,   -1,  258,  256,
  292,  258,  256,  299,  258,  256,  283,  258,  294,  295,
  296,  297,  289,   94,   44,  275,  276,  277,  275,  276,
  277,  275,  276,  277,  275,  276,  277,  298,  299,   59,
  294,  295,  296,  297,  289,   -1,  283,   41,   -1,  284,
   44,  286,  289,   -1,  289,  305,   -1,  307,  305,   -1,
  307,  305,  278,  307,  305,   59,  307,  283,  284,  285,
  286,   41,   -1,  289,   44,   -1,   -1,   -1,  294,  295,
  296,  297,  298,  299,  286,   44,  278,  289,   -1,   59,
   -1,  283,  284,  285,  286,   -1,  284,  289,  286,   -1,
   59,  289,  294,  295,  296,  297,  298,  299,   -1,   -1,
   41,  283,   43,   44,   45,  286,  278,  289,  289,   -1,
   -1,  283,  284,  285,  286,   -1,   -1,  289,   59,   60,
   -1,   62,  294,  295,  296,  297,  298,  299,   -1,   -1,
  278,   -1,   -1,   -1,   -1,  283,  284,  285,  286,   41,
   -1,  289,   44,   -1,   -1,   -1,  294,  295,  296,  297,
  298,  299,   93,   -1,   -1,  283,   -1,   59,   -1,  278,
   -1,  289,   -1,   -1,  283,  284,  285,  286,   -1,   -1,
  289,   -1,   -1,   -1,   -1,  294,  295,  296,  297,  298,
  299,  256,   -1,   -1,  259,  260,  261,  262,  263,  264,
  265,  266,  267,   -1,  269,  270,   -1,   -1,  273,  274,
  259,  260,  261,  262,  263,  264,  265,  266,  267,   -1,
  269,  270,  287,  288,  273,  274,   -1,   -1,   -1,   41,
   -1,   43,   44,   45,   -1,  300,   37,   -1,  287,  288,
   -1,   42,   43,   -1,   45,   -1,   47,   59,   60,   -1,
   62,  300,   -1,   -1,   -1,   41,   -1,   -1,   44,   60,
   -1,   62,  282,   -1,  284,   -1,  286,   -1,   -1,  289,
   -1,   -1,   -1,   59,   -1,   -1,   -1,   -1,   -1,   41,
   -1,   93,   44,   -1,  278,   -1,   -1,   -1,    5,  283,
  284,  285,  286,   94,   -1,  289,   -1,   59,   -1,   -1,
   -1,   -1,   -1,   41,  298,  299,   44,   -1,  278,   -1,
   -1,   -1,   -1,  283,  284,  285,  286,   -1,   -1,  289,
   37,   59,   39,  282,   -1,  284,   37,  286,  298,  299,
  289,   42,   43,   -1,   45,   -1,   47,   -1,   -1,   -1,
   -1,   -1,   -1,   40,   -1,   -1,   -1,  278,   45,   -1,
   -1,   -1,  283,  284,  285,  286,   -1,   -1,  289,   -1,
   -1,   -1,   79,  294,  295,  296,  297,  298,  299,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   95,   96,
   97,   98,   93,   94,   -1,   -1,  278,   -1,   -1,   -1,
   -1,  283,  284,  285,  286,   -1,   -1,  289,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,  298,  299,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,  159,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,  171,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,  278,  184,  185,   -1,
   -1,  283,  284,  285,  286,   -1,   -1,  289,   -1,   -1,
   -1,   -1,  294,  295,  296,  297,  298,  299,   -1,   -1,
   -1,   -1,  278,  294,  295,  296,  297,  283,  284,  285,
  286,   -1,   -1,  289,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,  298,  299,   -1,   -1,  278,   -1,    4,    5,
   -1,  283,  284,  285,  286,   -1,   -1,  289,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,  298,  299,   -1,   -1,
  278,   -1,   -1,   -1,   -1,  283,  284,  285,  286,   -1,
   -1,  289,   38,   39,   -1,   -1,   -1,   -1,   -1,   -1,
  298,  299,  259,  260,  261,  262,  263,  264,  265,  266,
  267,   -1,  269,  270,   -1,   -1,  273,  274,   64,   -1,
   -1,   -1,   -1,   -1,   -1,   71,   -1,   -1,   74,   -1,
  287,  288,   -1,   79,   -1,   -1,   82,   83,   84,   85,
   86,   87,   88,   89,   90,   91,   92,   93,   -1,   -1,
   -1,   97,   98,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,  140,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  165,
};
}
final static short YYFINAL=8;
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
"$accept : list_commande",
"list_commande : commande",
"list_commande : list_commande commande",
"commande : set",
"commande : set ';'",
"commande : show",
"commande : show ';'",
"commande : print",
"commande : request",
"commande : quit",
"commande : error",
"commande : error ';'",
"set : SET TRACE on_off",
"set : SET DEBUG on_off",
"set : SET error on_off",
"show : SHOW TRACE",
"show : SHOW DEBUG",
"show : SHOW error",
"quit : EOF",
"quit : QUIT",
"quit : QUIT ';'",
"print : PRINT list_expr opt_format ';'",
"print : PRINT error",
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
"opt_format : FORMAT error",
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
"fmt_ascii : error",
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

//#line 417 "najo.y"

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

//#line 653 "Parser.java"
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
//#line 90 "najo.y"
{najo.showError(); najo.newRequest();}
break;
case 2:
//#line 91 "najo.y"
{najo.showError(); najo.newRequest();}
break;
case 3:
//#line 95 "najo.y"
{if (najo.isTrace()) ((Node)val_peek(0).obj).display(1);}
break;
case 4:
//#line 96 "najo.y"
{if (najo.isTrace()) ((Node)val_peek(1).obj).display(1);}
break;
case 5:
//#line 97 "najo.y"
{if (najo.isTrace()) ((Node)val_peek(0).obj).display(1);}
break;
case 6:
//#line 98 "najo.y"
{if (najo.isTrace()) ((Node)val_peek(1).obj).display(1);}
break;
case 7:
//#line 99 "najo.y"
{
 	Node print = new Node(TypeNode.PRINT, (ListNodes)val_peek(0).obj);
 	if (najo.isTrace()) print.display(1); 
 	najo.stackRequest(print);
 	najo.showError();
 	najo.execute();
 	}
break;
case 8:
//#line 106 "najo.y"
{
 	Node request = new Node(TypeNode.REQUEST, (ListNodes)val_peek(0).obj);
 	if (najo.isTrace()) request.display(1); 
 	najo.stackRequest(request);
 	najo.showError();
 	najo.execute();
 	}
break;
case 9:
//#line 113 "najo.y"
{najo.bye(0);}
break;
case 10:
//#line 114 "najo.y"
{ najo.setError(""); }
break;
case 11:
//#line 115 "najo.y"
{}
break;
case 12:
//#line 118 "najo.y"
{ yyval.obj = val_peek(0).obj; najo.setTrace((Boolean) najo.getValue((Node) val_peek(0).obj));}
break;
case 13:
//#line 119 "najo.y"
{ yydebug = true; yyval.obj = val_peek(0).obj;}
break;
case 14:
//#line 120 "najo.y"
{ najo.setError(Syntax.SET_ON.toString()); }
break;
case 15:
//#line 124 "najo.y"
{ yyval.obj = 0;}
break;
case 16:
//#line 125 "najo.y"
{ yyval.obj = 0;}
break;
case 17:
//#line 126 "najo.y"
{ najo.setError(Syntax.SHOW.toString()); }
break;
case 18:
//#line 130 "najo.y"
{}
break;
case 19:
//#line 131 "najo.y"
{}
break;
case 20:
//#line 132 "najo.y"
{}
break;
case 21:
//#line 139 "najo.y"
{ListNodes list = new ListNodes("print", 2);
       list.add(new Node(TypeNode.LIST_COLUMN, (ListNodes)val_peek(2).obj));
       list.add((Node)val_peek(1).obj);
       yyval.obj = list;}
break;
case 22:
//#line 143 "najo.y"
{ najo.setError(Syntax.PRINT.definition()); yyval.obj = new ListNodes("print", 0); }
break;
case 23:
//#line 151 "najo.y"
{ListNodes list = new ListNodes("request", 3);
       list.add(new Node(TypeNode.FOR, (ListNodes)val_peek(3).obj));
       list.add(new Node(TypeNode.SELECT, (ListNodes)val_peek(2).obj));
       list.add((Node)val_peek(1).obj);
       yyval.obj = list;}
break;
case 24:
//#line 162 "najo.y"
{ListNodes list = new ListNodes("for", 3);
       list.add(new Node(TypeNode.LIST_ALIAS, (ListNodes)val_peek(3).obj));
       list.add((Node)val_peek(2).obj);
       list.add((Node)val_peek(1).obj);
       yyval.obj = list;}
break;
case 25:
//#line 171 "najo.y"
{yyval.obj = INode.NODE_NULL;}
break;
case 26:
//#line 173 "najo.y"
{yyval.obj = new Node(TypeNode.WITH, (ListNodes)val_peek(0).obj);}
break;
case 27:
//#line 178 "najo.y"
{yyval.obj = INode.NODE_NULL;}
break;
case 28:
//#line 180 "najo.y"
{yyval.obj = new Node(TypeNode.DEFINE, (ListNodes)val_peek(0).obj);}
break;
case 29:
//#line 190 "najo.y"
{ListNodes list = new ListNodes("for", 3);
       list.add(new Node(TypeNode.LIST_COLUMN, (ListNodes)val_peek(5).obj));
       list.add(new Node(TypeNode.FROM, (ListNodes)val_peek(3).obj));
       list.add((Node)val_peek(2).obj);
       list.add((Node)val_peek(1).obj);
       list.add((Node)val_peek(0).obj);
       yyval.obj = list;}
break;
case 30:
//#line 201 "najo.y"
{yyval.obj = INode.NODE_NULL;}
break;
case 31:
//#line 203 "najo.y"
{yyval.obj = new Node(TypeNode.WHERE,"where",(Node)val_peek(0).obj);}
break;
case 32:
//#line 208 "najo.y"
{yyval.obj = INode.NODE_NULL;}
break;
case 33:
//#line 210 "najo.y"
{yyval.obj = new Node(TypeNode.GROUP_BY,"group_by",(Node)val_peek(2).obj,(Node)val_peek(0).obj); }
break;
case 34:
//#line 215 "najo.y"
{yyval.obj = INode.NODE_NULL;}
break;
case 35:
//#line 217 "najo.y"
{yyval.obj = new Node(TypeNode.BREAK_ON,"break_on",(Node)val_peek(0).obj);}
break;
case 36:
//#line 222 "najo.y"
{yyval.obj = INode.NODE_NULL;}
break;
case 37:
//#line 224 "najo.y"
{yyval.obj = new Node(TypeNode.FORMAT, "format", (Node)val_peek(0).obj);}
break;
case 38:
//#line 225 "najo.y"
{ najo.setError(Syntax.FORMAT.toString()); yyval.obj = INode.NODE_NULL; }
break;
case 39:
//#line 230 "najo.y"
{ListNodes list = new ListNodes("list", 20);
       list.add((Node)val_peek(0).obj);
       yyval.obj = list;}
break;
case 40:
//#line 234 "najo.y"
{((ListNodes)val_peek(2).obj).add((Node)val_peek(0).obj); yyval.obj = val_peek(2).obj;}
break;
case 41:
//#line 238 "najo.y"
{yyval.obj = new Node(TypeNode.FILE,"file",(Node)val_peek(0).obj);}
break;
case 42:
//#line 239 "najo.y"
{yyval.obj = new Node(TypeNode.ALIAS_FILE,val_peek(0).sval,(Node)val_peek(2).obj);}
break;
case 43:
//#line 241 "najo.y"
{yyval.obj = new Node(TypeNode.FILE,"file",(Node)val_peek(3).obj,
             new Node(TypeNode.TYPE_FILE,val_peek(1).sval,(Node)val_peek(0).obj));}
break;
case 44:
//#line 244 "najo.y"
{yyval.obj = new Node(TypeNode.ALIAS_FILE,val_peek(3).sval,(Node)val_peek(5).obj,
             new Node(TypeNode.TYPE_FILE, val_peek(1).sval,(Node)val_peek(0).obj));}
break;
case 45:
//#line 250 "najo.y"
{ListNodes list = new ListNodes("list", 20);
       list.add((Node)val_peek(0).obj);
        yyval.obj = list;}
break;
case 46:
//#line 254 "najo.y"
{((ListNodes)val_peek(2).obj).add((Node)val_peek(0).obj);
       yyval.obj = val_peek(2).obj;}
break;
case 47:
//#line 260 "najo.y"
{yyval.obj = new Node(TypeNode.INTERVAL,"interval",(Node)val_peek(3).obj,(Node)val_peek(1).obj);}
break;
case 48:
//#line 264 "najo.y"
{yyval.obj = new Node(TypeNode.LIST_FMT_ASCII,"default");}
break;
case 49:
//#line 265 "najo.y"
{yyval.obj = new Node(TypeNode.LIST_FMT_ASCII_SEP,val_peek(0).sval);}
break;
case 50:
//#line 266 "najo.y"
{yyval.obj = new Node(TypeNode.LIST_FMT_ASCII, (ListNodes)val_peek(0).obj);}
break;
case 51:
//#line 268 "najo.y"
{yyval.obj = new Node(TypeNode.LIST_FMT_ASCII_SEP, val_peek(0).sval, 
               new Node(TypeNode.LIST_FMT_ASCII, (ListNodes)val_peek(2).obj));}
break;
case 52:
//#line 270 "najo.y"
{yyval.obj = new Node(TypeNode.LIST_FMT_BIN,"default");}
break;
case 53:
//#line 271 "najo.y"
{yyval.obj = new Node(TypeNode.LIST_FMT_BIN, (ListNodes)val_peek(0).obj);}
break;
case 54:
//#line 276 "najo.y"
{ListNodes list = new ListNodes("list", 20);
       list.add((Node)val_peek(0).obj);
       yyval.obj = list;}
break;
case 55:
//#line 280 "najo.y"
{((ListNodes)val_peek(2).obj).add((Node)val_peek(0).obj);
       yyval.obj = val_peek(2).obj;}
break;
case 56:
//#line 286 "najo.y"
{yyval.obj = INode.NODE_NULL;}
break;
case 57:
//#line 287 "najo.y"
{yyval.obj = new Node(TypeNode.FMT_ASCII,val_peek(0).sval);}
break;
case 58:
//#line 288 "najo.y"
{ najo.setError(Syntax.FFORTRAN.toString()); yyval.obj = INode.NODE_NULL; }
break;
case 59:
//#line 293 "najo.y"
{ListNodes list = new ListNodes("list", 20); 
       list.add((Node)val_peek(0).obj); 
       yyval.obj = list;}
break;
case 60:
//#line 297 "najo.y"
{((ListNodes)val_peek(2).obj).add((Node)val_peek(0).obj);
       yyval.obj = val_peek(2).obj;}
break;
case 61:
//#line 303 "najo.y"
{yyval.obj = INode.NODE_NULL;}
break;
case 62:
//#line 304 "najo.y"
{yyval.obj = new Node(TypeNode.FMT_BIN,val_peek(0).sval);}
break;
case 63:
//#line 309 "najo.y"
{yyval.obj = new Node(TypeNode.FUNCTION, val_peek(3).sval, 
          new Node(TypeNode.LIST_EXPR, (ListNodes)val_peek(1).obj));}
break;
case 64:
//#line 314 "najo.y"
{ListNodes list = new ListNodes("list", 20); list.add((Node)val_peek(0).obj); yyval.obj = list;}
break;
case 65:
//#line 315 "najo.y"
{ListNodes list = new ListNodes("list", 20); list.add((Node)val_peek(0).obj); yyval.obj = list;}
break;
case 66:
//#line 316 "najo.y"
{ListNodes list = new ListNodes("list", 20); list.add((Node)val_peek(0).obj); yyval.obj = list;}
break;
case 67:
//#line 317 "najo.y"
{((ListNodes)val_peek(2).obj).add((Node)val_peek(0).obj); yyval.obj = val_peek(2).obj;}
break;
case 68:
//#line 318 "najo.y"
{((ListNodes)val_peek(2).obj).add((Node)val_peek(0).obj); yyval.obj = val_peek(2).obj;}
break;
case 69:
//#line 319 "najo.y"
{((ListNodes)val_peek(2).obj).add((Node)val_peek(0).obj); yyval.obj = val_peek(2).obj;}
break;
case 70:
//#line 324 "najo.y"
{ListNodes list = new ListNodes("list", 20);
       list.add((Node)val_peek(0).obj);
       yyval.obj = list;}
break;
case 71:
//#line 327 "najo.y"
{((ListNodes)val_peek(2).obj).add((Node)val_peek(0).obj); yyval.obj = val_peek(2).obj;}
break;
case 72:
//#line 331 "najo.y"
{yyval.obj = new NodeAlias(TypeNode.ALIAS, val_peek(0).sval, (Node)val_peek(2).obj);}
break;
case 73:
//#line 332 "najo.y"
{yyval.obj = new NodeAlias(TypeNode.ALIAS, val_peek(0).sval, (Node)val_peek(2).obj);}
break;
case 74:
//#line 336 "najo.y"
{yyval.obj = new NodeCond(TypeCond.NOT,(Node)val_peek(0).obj);}
break;
case 75:
//#line 337 "najo.y"
{yyval.obj = new NodeCond(TypeCond.OR,(Node) val_peek(2).obj, (Node) val_peek(0).obj);}
break;
case 76:
//#line 338 "najo.y"
{yyval.obj = new NodeCond(TypeCond.AND,(Node) val_peek(2).obj, (Node) val_peek(0).obj);}
break;
case 77:
//#line 339 "najo.y"
{yyval.obj = new NodeCond(TypeCond.LESS,(Node) val_peek(2).obj, (Node) val_peek(0).obj);}
break;
case 78:
//#line 340 "najo.y"
{yyval.obj = new NodeCond(TypeCond.SUP,(Node) val_peek(2).obj, (Node) val_peek(0).obj);}
break;
case 79:
//#line 341 "najo.y"
{yyval.obj = new NodeCond(TypeCond.NE, (Node) val_peek(2).obj, (Node) val_peek(0).obj);}
break;
case 80:
//#line 342 "najo.y"
{yyval.obj = new NodeCond(TypeCond.EQEQ, (Node) val_peek(2).obj, (Node) val_peek(0).obj);}
break;
case 81:
//#line 343 "najo.y"
{yyval.obj = new NodeCond(TypeCond.LE, (Node) val_peek(2).obj, (Node) val_peek(0).obj);}
break;
case 82:
//#line 344 "najo.y"
{yyval.obj = new NodeCond(TypeCond.GE, (Node) val_peek(2).obj, (Node) val_peek(0).obj);}
break;
case 83:
//#line 345 "najo.y"
{yyval.obj = val_peek(1).obj;}
break;
case 84:
//#line 349 "najo.y"
{yyval.obj = val_peek(0).obj;}
break;
case 85:
//#line 350 "najo.y"
{yyval.obj = val_peek(0).obj;}
break;
case 86:
//#line 351 "najo.y"
{yyval.obj = new Node(TypeNode.ROWID, "rowid");}
break;
case 87:
//#line 352 "najo.y"
{yyval.obj = new Node(TypeNode.ROWNUM, "rownum");}
break;
case 88:
//#line 353 "najo.y"
{yyval.obj = new NodeMath(TypeMath.PLUS, (Node) val_peek(2).obj, (Node) val_peek(0).obj);}
break;
case 89:
//#line 354 "najo.y"
{yyval.obj = new NodeMath(TypeMath.MINUS,(Node) val_peek(2).obj, (Node) val_peek(0).obj);}
break;
case 90:
//#line 355 "najo.y"
{yyval.obj = new NodeMath(TypeMath.MULTIPLE, (Node) val_peek(2).obj, (Node) val_peek(0).obj);}
break;
case 91:
//#line 356 "najo.y"
{yyval.obj = new NodeMath(TypeMath.DIVISE, (Node) val_peek(2).obj, (Node) val_peek(0).obj);}
break;
case 92:
//#line 357 "najo.y"
{yyval.obj = new NodeMath(TypeMath.EXPONENT, (Node) val_peek(2).obj, (Node) val_peek(0).obj);}
break;
case 93:
//#line 358 "najo.y"
{yyval.obj = new NodeMath(TypeMath.MODULO, (Node) val_peek(2).obj, (Node) val_peek(0).obj);}
break;
case 94:
//#line 359 "najo.y"
{yyval.obj = new NodeMath(TypeMath.MUNAIRE, (Node)val_peek(0).obj);}
break;
case 95:
//#line 360 "najo.y"
{yyval.obj = val_peek(1).obj;}
break;
case 96:
//#line 364 "najo.y"
{yyval.obj = val_peek(0).obj;}
break;
case 97:
//#line 365 "najo.y"
{yyval.obj = val_peek(0).obj;}
break;
case 98:
//#line 366 "najo.y"
{yyval.obj = val_peek(0).obj;}
break;
case 99:
//#line 367 "najo.y"
{yyval.obj = val_peek(0).obj;}
break;
case 100:
//#line 368 "najo.y"
{yyval.obj = val_peek(0).obj;}
break;
case 101:
//#line 369 "najo.y"
{yyval.obj = val_peek(0).obj;}
break;
case 102:
//#line 370 "najo.y"
{yyval.obj = val_peek(0).obj;}
break;
case 103:
//#line 371 "najo.y"
{yyval.obj = val_peek(0).obj;}
break;
case 104:
//#line 375 "najo.y"
{yyval.obj = new Node(TypeNode.IDENT,val_peek(0).sval);}
break;
case 105:
//#line 379 "najo.y"
{yyval.obj = new NodeValue(TypeValue.INTEGER,val_peek(0).sval); }
break;
case 106:
//#line 380 "najo.y"
{yyval.obj = new NodeValue(TypeValue.LONG,val_peek(0).sval); }
break;
case 107:
//#line 381 "najo.y"
{yyval.obj = new NodeValue(TypeValue.FLOAT,val_peek(0).sval); }
break;
case 108:
//#line 382 "najo.y"
{yyval.obj = new NodeValue(TypeValue.DOUBLE,val_peek(0).sval); }
break;
case 109:
//#line 386 "najo.y"
{yyval.obj = new NodeValue(TypeValue.STRING,val_peek(0).sval); }
break;
case 110:
//#line 390 "najo.y"
{yyval.obj = new NodeValue(TypeValue.DATE,val_peek(0).sval); }
break;
case 111:
//#line 391 "najo.y"
{yyval.obj = new NodeValue(TypeValue.DATE,val_peek(0).sval); }
break;
case 112:
//#line 395 "najo.y"
{yyval.obj = new NodeValue(TypeValue.HEXA,val_peek(0).sval); }
break;
case 113:
//#line 399 "najo.y"
{yyval.obj = new NodeValue(TypeValue.BOOL,"true"); }
break;
case 114:
//#line 400 "najo.y"
{yyval.obj = new NodeValue(TypeValue.BOOL,"false"); }
break;
case 115:
//#line 404 "najo.y"
{yyval.obj = new NodeValue(TypeValue.ON_OFF,"on"); }
break;
case 116:
//#line 405 "najo.y"
{yyval.obj = new NodeValue(TypeValue.ON_OFF,"off"); }
break;
case 117:
//#line 409 "najo.y"
{yyval.obj = new NodeValue(TypeValue.ERROR,val_peek(0).sval); }
break;
case 118:
//#line 413 "najo.y"
{yyval.obj = INode.NODE_NULL;}
break;
//#line 1320 "Parser.java"
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
