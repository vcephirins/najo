%{
#include "dex.h"

/* Syntax request */
typedef enum {
   _not_defined,
	_commande, 
	_select, _generateMetadata, 
   _for, _with, 
   _define, _colonnes, _metadata,
   _from, 
   _where, _group_by, 
   _break_on, _format,
   _alias, _ref,
	_ffortran,
   MAX_SYNTAX_ID
} T_SYNTAX_ID;
   
char *pcSyntaxeDex[MAX_SYNTAX_ID] = {
   "", 
	"SET <ident> = <valeur>\nFOR ... SELECT ... FROM ...\nFOR ... GENERATE METADATA ... FROM ...",
   "FOR ... [WITH ...] [DEFINE ...] SELECT ... [FROM ...] [WHERE ...] [GROUP BY ...] [BREAK ON ...] [FORMAT ...]",
   "FOR ... [WITH ...] [DEFINE ...] GENERATE METADATA ... [FROM ...] [WHERE ...] [GROUP BY ...] [BREAK ON ...] [FORMAT ...]",
   "FOR [<alias>:]<ident> AS <alias> [TYPE <type> [flag]] [, ...]",
   "WITH <expr> AS <alias> [, ...]",
   "DEFINE <ident> AS <alias> [TYPE <type> [flag]] [, ...]",
   "SELECT <expr> [AS <alias>] [, ... ]",
   "GENERATE METADATA <expr> [, ... ]",
   "FROM <string> [AS <alias>] [WITH <ident> <string>] [, ...]",
   "WHERE <condition> [AND | OR <condition>] [, ...]",
   "GROUP BY <condition> [AND | OR <condition>] [, ...] HAVING <condition> [AND | OR <condition>] [, ...]",
   "BREAK ON <condition> [AND | OR <condition>] [, ...]",
   "FORMAT BINARY [AS <ident> [, ...]]\n=> FORMAT ASCII [AS <ident> [, ...]] [SEPARATED_BY <string>]",
   "[<alias>:]<ident> AS <alias> [TYPE <type> [flag]]",
   "<expr> AS <alias>",
	"A20, I4, F12.8, E6.3, ..."
   };

void yyerror(const char *msg) {
   yyerrorDex(121, "");
}

void yyerrorSyntaxe(int code) {
   dexLogTrace(1, "=> %s\n", pcSyntaxeDex[(code >= MAX_SYNTAX_ID)? _not_defined : code]);
}

%}

%union	{
	double dVal;
	int iVal;
    char *pcval;
	char acLigne[kiLIGNE + 1];
	char acIdent[kiIDENT + 1];
	char acString[kiSTRING + 1];
	char acNombre[kiNOMBRE + 1];
	char acFmt[kiFMT + 1];
	t_asa *asaPtr;
	}	

%token   <acNombre> INTEGER DOUBLE HEXA
%token   <acIdent> IDENT 
%token   <acString> STRING DATE_ISO_A DATE_ISO_B
%token   <acFmt> FMTASCII
%token   <iVal> BOOL_TRUE BOOL_FALSE

%token  FOR AS CLASS_DEX WITH DEFINE LISTE_1D LISTE_2D LISTE_3D SELECT WHERE
%token  FROM GROUP_BY HAVING ON BREAK ROWID ROWNUM
%token  FORMAT BIN ASCII_DEX SEPARATED_BY
%token  INFEQUAL SUPEQUAL NOTEQUAL EQUAL AND OR NOT
%token  ILLEGAL 

%token  GENERATE METADATA
%token  HELP SET SHOW TYPES_ALL TYPE_DEX LIBRARIES LIBRARIE FUNCTIONS FUNCTION

%token   '<' '>' ',' '+' '-' '*' '/' '^' '(' ')' ';' '%'

%left OR
%left AND
%left NOT
%left   '+' '-'
%left   '*' '/' '%'
%right  '^'
%right  MUNAIRE PUNAIRE
%nonassoc '<' '>' EQUAL INFEQUAL SUPEQUAL NOTEQUAL

%start   liste_commandes

%type <iVal>   liste_commandes commande requete 
%type <asaPtr> for select identificateur nombre string date_iso_a date_iso_b hexa bool
%type <asaPtr> colonnes opt_type opt_with opt_define from opt_where opt_group_by opt_break_on opt_format
%type <asaPtr> liste_fichiers fichier
%type <asaPtr> liste_alias alias fic_alias liste_ref ref liste_expressions
%type <asaPtr> liste_ident liste_liste liste_arg 
%type <asaPtr> liste1d liste2d liste3d
%type <asaPtr> intervalle liste_intervalles surface liste_surfaces volume liste_volumes
%type <asaPtr> fmt_ascii fmt_bin liste_fmt_ascii liste_fmt_bin liste_fmt
%type <asaPtr> expression expression_cond fonction paramVal 

%%

liste_commandes : 
   commande
      {$$ = $1;} 
 | liste_commandes commande
      {$$ = $2;} 
;

commande : 
 SET IDENT '=' paramVal ';'
      {$$ = dexParamSet($2,$4); }
 | SHOW IDENT ';'
      {$$ = 0; }
 | requete ';'
      {$$ = $1; }
;

requete : 
   /* empty */
      {$$ = 0;}
 | select 
      {gpAsaRacine = dexNodeNew (a_requete, "requete", $1,NULL); $$ = 0;}
;

select	: 
   /* Syntaxe obligatoire : FOR ... SELECT ... WHERE */
   for  
   opt_with                     /* optionnel */
   opt_define                   /* optionnel */
   colonnes
   from
   opt_where                    /* optionnel */
   opt_group_by                 /* optionnel */ 
   opt_break_on                 /* optionnel */
   opt_format                   /* optionnel */
      {
       $$ = dexNodeNew(a_select,"", 
             dexNodeNew(a_sel_for, "", $1, 
              dexNodeNew(a_sel_with, "", $2,
               dexNodeNew(a_sel_define, "", $3,
                dexNodeNew(a_sel_colonnes, "", $4,
                 dexNodeNew(a_sel_from, "", $5,
                  dexNodeNew(a_sel_where, "", $6,
                   dexNodeNew(a_sel_group_by, "", $7,
                    dexNodeNew(a_sel_break_on, "", $8,
                     dexNodeNew(a_sel_format, "", $9, NULL), 
                    NULL), 
                   NULL), 
                  NULL), 
                 NULL), 
                NULL),
               NULL),
              NULL),
             NULL),
            NULL);
      }
 | for  
   opt_with                     /* optionnel */
   opt_define                   /* optionnel */
   colonnes
   error
      {yyerrorSyntaxe(_from);$$ = ASA_NULL; yyerrok; }
 ;

for : 
   FOR liste_alias
      {$$ = dexNodeNew(a_for,"",$2,NULL);}
 | FOR error 
      {yyerrorSyntaxe(_for);$$ = ASA_NULL; yyerrok; }
;

opt_with :
   /* empty */
      {$$ = ASA_NULL;}
 | WITH liste_ref
      {$$ = dexNodeNew(a_with,"",$2,NULL);}
 | WITH error 
      {yyerrorSyntaxe(_with);$$ = ASA_NULL; yyerrok; }
;

opt_define :
   /* empty */
      {$$ = ASA_NULL;}
 | DEFINE liste_liste
      {$$ = dexNodeNew(a_define,"",$2,NULL);}
 | DEFINE error 
      {yyerrorSyntaxe(_define);$$ = ASA_NULL; yyerrok; }
;
	   
colonnes :
   SELECT liste_expressions
      {$$ = dexNodeNew(a_liste_colonnes,"",$2,NULL);}
 | GENERATE METADATA liste_expressions
      {gbGenerateMetadata = 1;$$ = dexNodeNew(a_liste_colonnes,"",$3,NULL);}
 | SELECT error 
      {yyerrorSyntaxe(_colonnes);$$ = ASA_NULL; yyerrok; }
 | GENERATE METADATA error 
      {yyerrorSyntaxe(_generateMetadata);$$ = ASA_NULL; yyerrok; }
;

from :
   FROM liste_fichiers
      {$$ = dexNodeNew(a_from,"",$2,NULL);}
 | FROM error 
      {yyerrorSyntaxe(_from);$$ = ASA_NULL; yyerrok; }
;

opt_where :
   /* empty */
      {$$ = ASA_NULL;}
 | WHERE expression_cond
      {$$ = dexNodeNew(a_where,"",$2,NULL);}
 | WHERE error 
      {yyerrorSyntaxe(_where);$$ = ASA_NULL; yyerrok; }
;
	   
opt_group_by :
   /* empty */
      {$$ = ASA_NULL;}
 | GROUP_BY expression_cond HAVING expression_cond
      {$$ = dexNodeNew(a_group_by,"",$2,$4,NULL); }
 | GROUP_BY error HAVING expression_cond
      {yyerrorSyntaxe(_group_by);$$ = ASA_NULL; yyerrok; }
 | GROUP_BY expression_cond HAVING error
      {yyerrorSyntaxe(_group_by);$$ = ASA_NULL; yyerrok; }
;
	   
opt_break_on :
   /* empty */
      {$$ = ASA_NULL;}
 | BREAK ON expression_cond
      {$$ = dexNodeNew(a_break_on,"",$3,NULL);}
 | BREAK ON error 
      {yyerrorSyntaxe(_break_on);$$ = ASA_NULL; yyerrok; }
;
	   
opt_format :
   /* empty */
      {$$ = ASA_NULL;}
 | FORMAT liste_fmt
      {$$ = dexNodeNew(a_format,"",$2,NULL);}
 | FORMAT error 
      {yyerrorSyntaxe(_format);$$ = ASA_NULL; yyerrok; }
;

liste_fichiers :
   fichier
      {$$ = $1;} 
 | liste_fichiers ',' fichier
      {$$ = dexNodeNew(a_liste_fichiers,"",$3,$1, NULL); }
 | liste_fichiers error fichier
      {yyerrorSyntaxe(_from);$$ = ASA_NULL; yyerrok; }
 | error ',' fichier 
      {yyerrorSyntaxe(_from);$$ = ASA_NULL; yyerrok; }
;

fichier :
   string
      {$$ = dexNodeNew(a_fichier,kFICDEX_DEFAULT,$1,NULL);}
 | string AS IDENT
      {$$ = dexNodeNew(a_fichier,$3,$1,NULL);}
 | string WITH IDENT string
      {$$ = dexNodeNew(a_fichier,kFICDEX_DEFAULT,$1,dexNodeNew(a_fichier_type,$3,$4,NULL),NULL);}
 | string AS IDENT WITH IDENT string
      {$$ = dexNodeNew(a_fichier,$3,$1,dexNodeNew(a_fichier_type, $5,$6,NULL),NULL);}
;
	   
liste_liste : 
   LISTE_1D IDENT AS liste1d
      {$$ = dexNodeNew(a_liste1d,$2,$4, NULL);} 
 | LISTE_2D IDENT AS liste2d
      {$$ = dexNodeNew(a_liste2d,$2,$4, NULL);} 
 | LISTE_3D IDENT AS liste3d
      {$$ = dexNodeNew(a_liste3d,$2,$4, NULL);} 
 | liste_liste ',' LISTE_1D IDENT AS liste1d 
      {$$ = dexNodeNew(a_liste_liste1d,$4,$6,$1, NULL);} 
 | liste_liste ',' LISTE_2D IDENT AS liste2d 
      {$$ = dexNodeNew(a_liste_liste2d,$4,$6,$1, NULL);} 
 | liste_liste ',' LISTE_3D IDENT AS liste3d 
      {$$ = dexNodeNew(a_liste_liste3d,$4,$6,$1, NULL);} 
;

liste1d : 
   '{' liste_ident '}'
      {$$ = $2;} 
 | '{' liste_intervalles '}'
      {$$ = $2;} 
 | '{' error '}'
      {$$ = ASA_NULL;yyerrok; }
;

liste2d : 
   '{' liste_surfaces '}'
      {$$ = $2;} 
;

liste3d : 
   '{' liste_volumes '}'
      {$$ = $2;} 
;

liste_volumes : 
   volume
      {$$ = $1;} 
 | liste_volumes ',' volume
      {$$ = dexNodeNew(a_liste_volumes,"",$3,$1, NULL);} 
 | error ',' volume {$$ = ASA_NULL; yyerrok; /* Resynchronise */}
;

/*
volume : 
   '(' intervalle ',' intervalle  ',' intervalle ')'		
      {$$ = dexNodeNew(a_volume,"",$2,$4,$6, NULL);} 
;
*/

liste_surfaces : 
   surface
      {$$ = $1;} 
 | liste_surfaces ',' surface
      {$$ = dexNodeNew(a_liste_surfaces,"",$3,$1, NULL);} 
 | error ',' surface {$$ = ASA_NULL;yyerrok; /* Resynchronise */}
;

/*
surface : 
   '(' intervalle ',' intervalle ')'		
      {$$ = dexNodeNew(a_surface,"",$2,$4, NULL);} 
;
*/
			 
volume : 
   '(' liste_intervalles ')'		
      {$$ = dexNodeNew(a_volume,"",$2, NULL);} 
;

surface : 
   '(' liste_intervalles ')'		
      {$$ = dexNodeNew(a_surface,"",$2, NULL);} 
;

liste_intervalles : 
   intervalle
      {$$ = $1;} 
 | liste_intervalles',' intervalle 
      {$$ = dexNodeNew(a_liste_intervalles,"",$3,$1, NULL);} 
 | error ',' intervalle {$$ = ASA_NULL; yyerrok; /* Resynchronise */}
;

intervalle : 
   '[' expression ',' expression ']'
      {$$ = dexNodeNew(a_intervalle,"",$2,$4, NULL);} 
;

liste_fmt: 
   ASCII_DEX AS liste_fmt_ascii
       {$$ = $3;}
 | ASCII_DEX SEPARATED_BY STRING 
      {$$ = dexNodeNew (a_liste_fmt_ascii_sep,$3,
                     dexNodeNew(a_fmt_ascii, NULL, NULL),NULL);}
 | ASCII_DEX
      {$$ = dexNodeNew (a_liste_fmt_ascii,"",
                     dexNodeNew(a_fmt_ascii, NULL, NULL),NULL);}
 | ASCII_DEX error
      {yyerrorSyntaxe(_format);$$ = ASA_NULL; yyerrok; }
 | BIN AS liste_fmt_bin
       {$$ = $3;}
 | BIN
      {$$ = dexNodeNew (a_liste_fmt_bin,"",
                     dexNodeNew(a_fmt_bin, NULL, NULL),NULL);}
 | BIN error
     {yyerrorSyntaxe(_format);$$ = ASA_NULL; yyerrok; }
;

liste_fmt_ascii: 
   fmt_ascii
      {$$ = $1;}
 | fmt_ascii SEPARATED_BY STRING 
      {$$ = dexNodeNew (a_liste_fmt_ascii_sep,$3,$1,NULL);}
 | liste_fmt_ascii ',' fmt_ascii
      {$$ = dexNodeNew (a_liste_fmt_ascii,"",$3,$1,NULL);}
 | liste_fmt_ascii ',' fmt_ascii SEPARATED_BY STRING 
      {$$ = dexNodeNew (a_liste_fmt_ascii_sep,$5,$3,$1,NULL);}
 | error
      {yyerrorSyntaxe(_ffortran);$$ = ASA_NULL; yyerrok; }
 | liste_fmt_ascii ',' error
      {yyerrorSyntaxe(_ffortran);$$ = ASA_NULL; yyerrok; }
;

liste_fmt_bin: 
   fmt_bin
      {$$ = $1;}
 | liste_fmt_bin ',' fmt_bin
      {$$ = dexNodeNew (a_liste_fmt_bin,"",$3,$1,NULL);}
 | error ',' fmt_bin 
      {yyerrorSyntaxe(_format);$$ = ASA_NULL; yyerrok; }
;

fmt_bin: 
   /* empty : le node doit etre creer pour eviter une fin de liste */
      {$$ = dexNodeNew(a_fmt_bin, NULL, NULL);} 
 | IDENT
      {$$ = dexNodeNew(a_fmt_bin,$1, NULL);} 
;

fmt_ascii: 
   /* empty : le node doit etre creer pour eviter une fin de liste */
      {$$ = dexNodeNew(a_fmt_ascii, NULL, NULL);} 
 | FMTASCII
      {$$ = dexNodeNew(a_fmt_ascii,$1, NULL);} 
;

expression : 
   expression '+' expression
      {$$ = dexNodeNew(a_plus,"+",$1,$3, NULL); }
 | expression '-' expression
      {$$ = dexNodeNew(a_moins,"-",$1,$3, NULL); }
 | expression '*' expression
      {$$ = dexNodeNew(a_multiple,"*",$1,$3, NULL); }
 | expression '/' expression
      {$$ = dexNodeNew(a_divise,"/",$1,$3, NULL); }
 | expression '^' expression
      {$$ = dexNodeNew(a_puissance,"^",$1,$3, NULL); }
 | expression '%' expression
      {$$ = dexNodeNew(a_modulo,"%",$1,$3, NULL); }
 | '-' expression %prec MUNAIRE
      {$$ = dexNodeNew(a_moins_unaire,"-",$2, NULL); }
 | '+' expression %prec PUNAIRE  
      {$$ = dexNodeNew(a_plus_unaire,"+",$2, NULL); }
 | '(' expression ')' 
      {$$ = $2;  }
 | fonction
      {$$ = $1; }
 | identificateur
      {$$ = $1; }
 | nombre
      {$$ = $1; }
 | string
      {$$ = $1; }
 | date_iso_a 
      {$$ = $1; }
 | date_iso_b
      {$$ = $1; }
 | bool
      {$$ = $1; }
 | hexa
      {$$ = $1; }
 | ROWID
      {$$ = dexNodeNew(a_rowid, "ROWID", NULL); }
 | ROWNUM
      {$$ = dexNodeNew(a_rownum, "ROWNUM", NULL); }
;

fonction :  
   IDENT '(' liste_arg ')'
      {$$ = dexNodeNew(a_fonction,$1,$3, NULL); }
;
		
liste_arg : 
   /* empty */
   {$$ = ASA_NULL; }
 | expression
      {$$ = $1;}
 | liste_arg ',' expression
      {$$ = dexNodeNew(a_liste_arg,"",$3,$1, NULL); }
 | error ',' expression {$$ = ASA_NULL; yyerrok; /* Resynchronise */}
;

expression_cond : 
   NOT expression_cond
      {$$ = dexNodeNew(a_not,"not",$2, NULL);}
 | expression_cond OR expression_cond
      {$$ = dexNodeNew(a_or,"or",$1,$3, NULL); }
 | expression_cond AND expression_cond
      {$$ = dexNodeNew(a_and,"and",$1,$3, NULL); }
 | expression '<' expression
      {$$ = dexNodeNew(a_inferieur,"<",$1,$3, NULL); }
 | expression '>' expression
      {$$ = dexNodeNew(a_superieur,">",$1,$3, NULL); }
 | expression NOTEQUAL expression
      {$$ = dexNodeNew(a_different,"!=",$1,$3, NULL); }
 | expression EQUAL expression
      {$$ = dexNodeNew(a_egale,"==",$1,$3, NULL); }
 | expression INFEQUAL expression
      {$$ = dexNodeNew(a_inferieur_egale,"<=",$1,$3, NULL); }
 | expression SUPEQUAL expression
      {$$ = dexNodeNew(a_superieur_egale,">=",$1,$3, NULL); }
 | '(' expression_cond ')' 
      {$$ = $2;  }
;

liste_alias : 
   fic_alias
      {$$ = $1;} 
 | liste_alias ',' fic_alias  
      {$$ = dexNodeNew ( a_liste_alias,"",$3,$1, NULL); }
 | liste_alias error fic_alias
      {yyerrorSyntaxe(_for);$$ = ASA_NULL; yyerrok; }
 | error ',' fic_alias
      {yyerrorSyntaxe(_for);$$ = ASA_NULL; yyerrok; }
;

fic_alias : 
   alias
      {$$ = dexNodeNew(a_fic_alias, kFICDEX_DEFAULT,$1,NULL);}
 | IDENT ':' alias
      {$$ = dexNodeNew(a_fic_alias,$1,$3,NULL);}
;

alias : 
   identificateur AS IDENT opt_type
      {
      dexUpper($1->nom);  /* Exigence DEX_SL_LANGAGE_050 */
      $$ = dexNodeNew(a_alias,$3,$1,$4,NULL);}
 | identificateur error IDENT opt_type
      {yyerrorSyntaxe(_alias);$$ = ASA_NULL; yyerrok; }
;

opt_type :
   /* empty */
      {$$ = ASA_NULL; }
 | TYPE_DEX IDENT
      {$$ = dexNodeNew (a_type,$2,NULL);}
 | TYPE_DEX IDENT IDENT
      {$$ = dexNodeNew (a_type,$2,dexNodeNew(a_flag, $3, NULL), NULL);}
;

liste_ref : 
   ref
      {$$ = $1;} 
 | liste_ref ',' ref  
      {$$ = dexNodeNew(a_liste_ref,"",$3,$1, NULL); }
 | liste_ref error ref
      {yyerrorSyntaxe(_with);$$ = ASA_NULL; yyerrok; }
 | error ',' ref 
      {yyerrorSyntaxe(_with);$$ = ASA_NULL; yyerrok; }
;

ref : 
   expression AS IDENT  
      {$$ = dexNodeNew(a_ref,$3,$1, NULL);} 
;

paramVal : 
 nombre
      {$$ = $1; }
 | identificateur
      {$$ = $1; }
 | string
      {$$ = $1; }
 | date_iso_a 
      {$$ = $1; }
 | date_iso_b
      {$$ = $1; }
 | bool
      {$$ = $1; }
 | hexa
      {$$ = $1; }
;

liste_expressions : 
   expression
      {$$ = dexNodeNew(a_col,"",$1, NULL);} 
 |  ref
      {$$ = dexNodeNew(a_col,"",$1, NULL);} 
 | liste_expressions ',' expression 
      {$$ = dexNodeNew(a_col,"",$3,$1, NULL);}
 | liste_expressions ',' ref 
      {$$ = dexNodeNew(a_col,"",$3,$1, NULL);}
 | '*'
      {$$ = dexNodeNew (a_col,"*", NULL);}
;

liste_ident :
   identificateur 
      {$$ = $1;} 
 | liste_ident ',' identificateur 
      {$$ = dexNodeNew(a_liste_ident,"",$3,$1, NULL);} 
 | error ',' identificateur {$$ = ASA_NULL;yyerrok; /* Resynchronise */}
;

identificateur :
   IDENT
      {$$ = dexNodeNew (a_ident,$1, NULL);}
;

nombre :
   INTEGER
      {$$ = dexNodeNew(a_integer,$1,NULL); }
 | DOUBLE
      {$$ = dexNodeNew(a_double,$1,NULL); }
;

string :
   STRING
      {$$ = dexNodeNew(a_string,$1,NULL); }
;

date_iso_a :
   DATE_ISO_A
      {$$ = dexNodeNew(a_date_iso_a,$1,NULL); }
;

date_iso_b :
   DATE_ISO_B
      {$$ = dexNodeNew(a_date_iso_b,$1,NULL); }
;

hexa :
   HEXA
      {$$ = dexNodeNew(a_hexa,$1,NULL); }
;

bool :
   BOOL_TRUE
      {$$ = dexNodeNew(a_bool,"true",NULL); }
 | BOOL_FALSE
      {$$ = dexNodeNew(a_bool,"false",NULL); }
;

%%


