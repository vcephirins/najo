/*===========================*
* Fichier des declarations  *
*===========================*/

#ifndef __DEX_H
#define __DEX_H

#include <stdio.h>
#include <ctype.h>
#include <math.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include <stdarg.h>
#include <errno.h>
#include <limits.h>

/* EAST APIs */
#include "libdex.h"
#include "interpreter_mt.h"

/* SDF APIs */
#include "SDFWrapper.h"
#include "SDFMetaDataManager.h"
#include "SDFMetaData.h"
#include "SDFDataSet.h"
#include "SDFRecord.h"
#include "SDFParam.h"
#include "SDFStandardDate.h"

#ifdef __cplusplus
extern "C" {
#endif
   
/*=========================================*/
/* Constantes                              */
/*=========================================*/
#define DEX_VERSION   "V0.9 Beta"                     /* version of program */
   
#define YYDEBUG 0

#ifndef _INT64
#define _INT64
#ifdef _MSC_VER
   typedef __int64             INT64;           /* 8 bytes (Visual C) */
#else
   typedef long long           INT64;           /* 8 bytes */
#endif
#endif   

#ifndef _UCHAR
#define _UCHAR
   typedef unsigned char       UCHAR;           /* 1 byte */
#endif
#ifndef _USHORT
#define _USHORT
   typedef unsigned short      USHORT;          /* 2 bytes */
#endif
#ifndef _UINT
#define _UINT
   typedef unsigned int        UINT;            /* 4 bytes */
#endif
#ifndef _ULINT
#define _ULINT
   typedef unsigned long int  ULINT;            /* 4 bytes */
#endif
#ifndef _ULONG
#define _ULONG
   typedef unsigned long       ULONG;           /* 4/8 bytes (according to the processor) */
#endif
   
#ifndef _ULLONG
#define _ULLONG
#ifdef _MSC_VER
   typedef unsigned __int64    ULLONG;          /* 8 bytes (Visual C) */
#else
   typedef unsigned long long  ULLONG;          /* 8 bytes */
#endif
#endif
   
#define false 0
#define true  1

#define kiMAX_BRANCHES    3  /* nombre max de branches d'un noeud */
#define kiMAX_MEM_VALS    3  /* nombre max de valeurs par sauvegarde dispo */
#define kiMAX_COUNTERS   20  /* nombre max de compteurs */
#define kiMAX_OBJECTS 10000  /* nombre max d'objets pour debuggage */ 

#define kiBUFFER       1024  /* taille max pour les messages standards */
#define kiLIGNE        2000  /* taille max d'une ligne */
#define kiNOMBRE        100  /* taille max du resultat de l'expression */
#define kiNOM_INDEX      30  /* taille max du nom de l'index */
#define kiIDENT         300  /* taille max du nom d'un identifiant  */
#define kiSTRING       2000  /* taille max d'une chaine */
#define kiFMT            50  /* taille max d'une chaine format */
#define kiDATE_ASCII     50  /* taille max d'une date en ASCII */
#define kiNOM_COMPTEUR   20  /* taille max du nom d'un compteur */
#define kiNOM_PILE       20  /* taille max du nom d'une pile */
#define kiFORMAT       1000  /* taille max d'une chaine de format */
#define kiNOM_OBJECT     30  /* taille max du nom d'un objet */
   
#define kiLANG            2  /* taille max du code de la langue */
#define kiSEP            10  /* taille max du separateur des colonnes */
#define kiNULL_STRING     8  /* taille max de la chaine representant le NULL */
#define kiASCII           0  /* code du format ascii */
#define kiBINARY          1  /* code du format binaire */
#define kiBIG_ENDIAN      0  /* convention du format de sortie binaire BIG_ENDIAN (SUN) */
#define kiLITTLE_ENDIAN   1  /* convention du format de sortie binaire LITTLE_ENDIAN (PC) */
   
#define kiPREC_MILLI      3
#define kiPREC_MICRO      6
#define kiPREC_NANO       9
#define kiPREC_PICO      12
   
/* POSITION DANS UNE PILE */
#define PILE_LAST           0
#define PILE_FIRST          1
   
   /* Definition des types EAST pour DEX */
   typedef enum {
      TYPE_EAST_LOGICAL = LOGICAL,
         TYPE_EAST_INTEGER_8 = INTEGER_8, 
         TYPE_EAST_INTEGER_16 = INTEGER_16, 
         TYPE_EAST_INTEGER_32 = INTEGER_32, 
         TYPE_EAST_REAL_32 = REAL_32, 
         TYPE_EAST_REAL_64 = REAL_64,
         TYPE_EAST_ASCII = ASCII, 
         MAX_TYPE_EAST = 7
   } T_TYPE_EAST;

   /* Definition des types SDF pour DEX */
   typedef enum {
      TYPE_SDF_ERROR = -1,
      TYPE_SDF_INTEGER_8 = ByteValue,
      TYPE_SDF_INTEGER_16 = ShortValue,
      TYPE_SDF_INTEGER_32 = IntValue,
      TYPE_SDF_INTEGER_64 = LongValue,
      TYPE_SDF_REAL_32 = FloatValue,
      TYPE_SDF_REAL_64 = DblValue,
      TYPE_SDF_ASCII = StrValue,
      TYPE_SDF_DATE = DateValue,
      TYPE_SDF_DATE_MICRO,
      TYPE_SDF_DATE_NANO,
      TYPE_SDF_DATE_PICO,
      MAX_TYPE_SDF = 12
   } T_TYPE_SDF;
   
   /*=========================================*
   * Structures                              *
   *=========================================*/
   
   /* types de noeuds */
   /*=================*/
   typedef enum {
	     a_alias, a_and,
           a_bool, a_break_on,
           a_col, a_const,
           a_date_iso_a, a_date_iso_b,
           a_define, a_different, a_divise, a_double,
           a_egale,
           a_fic_alias, a_fichier, a_fichier_type,
           a_flag, a_fmt_ascii, a_fmt_bin, a_fonction, a_for, a_format, a_from,
           a_group_by,
           a_hexa,
           a_ident, a_inferieur, a_inferieur_egale, a_integer, 
           a_intervalle,
           a_keyword,
           a_liste_alias, a_liste_arg, a_liste_fichiers,
           a_liste_fmt_ascii, a_liste_fmt_ascii_sep, a_liste_fmt_bin, a_liste_ident,
           a_liste_colonnes, a_liste_intervalles, a_liste_ref,
           a_liste1d, a_liste2d, a_liste3d,
           a_liste_liste1d, a_liste_liste2d, a_liste_liste3d,
           a_liste_surfaces, a_liste_volumes,
           a_modulo, a_moins, a_moins_unaire, a_multiple,
           a_not, 
           a_or, 
           a_plus, a_plus_unaire, a_puissance,
           a_ref, a_requete, a_rowid, a_rownum,
           a_select, a_sel_for, a_sel_with, a_sel_define, a_sel_colonnes, 
           a_sel_from, a_sel_where, a_sel_group_by, a_sel_break_on, a_sel_format,
           a_string, a_superieur, a_superieur_egale, a_surface,
           a_type,
           a_volume,
           a_where, a_with,
           MAX_GENRE_ASA
   } T_GENRE_ASA;
   
   /* Gestion des fichiers des données et descrition */
   /*================================================*/
   
#define kFICDEX_DEFAULT    "FICDEX_DEFAULT"
   
   /* Structure des informations fichier */
   /*------------------------------------*/
   
   typedef enum {
      TYPE_FILE_NULL,
         TYPE_FILE_REQUEST,
         TYPE_FILE_FULL, 
         TYPE_FILE_EAST, 
         TYPE_FILE_SDF,      /* TYPE_CFA ou TYPE_CFB */
         TYPE_FILE_CFA,      
         TYPE_FILE_CFB,      
         MAX_TYPE_FILE
   } T_TYPE_FILE;
   
   typedef struct {
      int idFile;                      /* Handle table */
      char *pcName;                    /* Nom interne du fichier (Alias) */
      char *pcNameExt;                 /* Chemin d'acces au fichier des donnees */
      char *pcNameDesc;                /* Chemin d'acces au fichier de description */
      int type;                        /* Type du fichier (DEFAULT, EAST, SDF, CFA, CFB, ...) */
      T_PIPE_INTERFACE  *pPipeEast;    /* interface East du fichier de donnees EAST (TYPE_EAST)*/
      /* Instance pour SDF */
      SDFMetaDataManagerInstance lSDFMetaDataManager;
      SDFDataSetInstance lSDFDataSet;
      SDFRecordInstance lSDFRecord;
      FILE *pFile;                     /* Handle du fichier des donnees standard (TYPE_FULL) */
      UINT rowid;                      /* Memorise la position de la ligne courante */
   } t_file;
   
/* Declaration du type NULL */
extern t_file t_file_null;
#define FILE_NULL     (&t_file_null)
   
   /* Structure des informations colonne */
   /*------------------------------------*/
   
   typedef struct {
      char *pcName;                    /* Alias */
      char *pcNameExt;                 /* Nom complet */
      long lIndex;                     /* Position dans le record */
      T_TYPE_DEX typeDex;              /* type interne */
      /* Instance pour SDF */
      ValueType typeSDF;
      PrecisionType precisionDate;     /* Precision si le type est une date */
      char *pcFlag;                    /* flag : ARRAY, ... */
      t_file *pFile;                   /* table source */
      int length;                      /* longeur de la valeur */
      void *pBuffer;                   /* valeur */
      void *pNode;                     /* node de l'arbre ou se trouve la colonne */
   } t_column;
   
/* Declaration du type NULL */
extern t_column t_column_null;
#define COLUMN_NULL     (&t_column_null)
   
   /* Structures d'une date   */
   /*=========================*/
#define kiMJD    2400000.5        /* jour julien modifie : 17/11/1858 minuit */
#define kiCJD    2433282.5        /* jour julien CNES    : 01/01/1950 minuit */
#define kiTAI    2436204.5        /* jour TAI            : 01/01/1958 minuit */
#define kiF70JD  2440587.5        /* jour julien from 70 : 01/01/1970 minuit */
#define kiSDF    2433282.5        /* jour julien SDF     : 01/01/1950 minuit */
   
   typedef struct {
      double jours;                  /* date in CNES Julian Date (CJD) */
      UINT sec;                      /* seconds of day */
      double msec;                   /* picosecondes (12 digits) */
   } t_date;
   
   /* Date From 70 */
   typedef struct {
      UINT sec;
      UINT msec;
   } t_date_F70;
   
   /* Date CDS */
   /*----------*/
   /* Date CDS en char pour manipulation d'adresse */
   typedef struct {
      char p_field;
      char days[3];
      char milli[4];
      char subMilli[4];
   } t_date_CDS_bytes;
   
   /* DATE CALENDAIRE A */
   typedef struct {
      UINT year;
      UINT month;
      UINT day;
      UINT hour;
      UINT min;
      UINT sec;
   } t_date_cal_a;
   
   /* DATE CALENDAIRE B (quantieme) */
   typedef struct {
      UINT year;
      UINT day;
      UINT hour;
      UINT min;
      UINT sec;
   } t_date_cal_b;
   
   /* DATE format simplifie (CDF) */
   typedef struct {
      INT64 date;             /* secondes et millisecondes */
      UINT msec;              /* microsecondes à picosecondes */
   } t_date_SDF;
   
#define kiT_DATE_SDF (sizeof(INT64 )+sizeof(UINT ))
 
   /* Structure d'une valeur   */
   /*==========================*/
   typedef struct {
      int type;                       /* type du resultat memorise */
      UINT length;                    /* longueur de la valeur en octets */
      UINT nbElements;                /* nombre d'elements dans le cas d'un tableau */
      void *value;                    /* Pointe sur la valeur active */
      char int8Val;                   /* int8 (8 bits) */
      short int int16Val;             /* int16 (16 bits) */
      int int32Val;                   /* int32 (32 bits) */
      INT64  int64Val;                /* int64 (64 bits) */
      float fVal;                     /* simple precision (32 bits) */
      double dVal;                    /* double precision (64 bits) */
      t_date sDate;                   /* Date DEX */
      t_date_SDF sDateSDF;          /* Date SDF */
      void *pObj;                     /* tous les autres types */
   } t_val;
   
/* Declaration du type NULL */
extern t_val t_val_null;
#define VAL_NULL     (&t_val_null)
   
   /* Structure de sauvegarde de valeurs pour un node   */
   /*========================================i==========*/
   typedef struct {
      UINT id;                        /* Identifiant unique */
      t_val *pVal[kiMAX_MEM_VALS];    /* Valeur sauvegardee */
   } t_mem;
   
/* Declaration du type FILE_NULL */
extern t_mem t_mem_null;
#define MEM_NULL     (&t_mem_null)
   
   /* Structure d'un index */
   /*======================*/
   typedef struct {
      char nom[kiNOM_INDEX + 1];
      UINT nbr;
      UINT max;
      UINT extend;
      void *tab[1];
   } t_index;
   
/* Declaration du type INDEX_NULL */
extern t_index t_index_null;
#define INDEX_NULL     (&t_index_null)

   /* Structure d'un noeud */
   /*======================*/
   typedef struct T_ASA {
      UINT id;                         /* identifiant interne du noeud */
      T_GENRE_ASA genre;               /* genre : voir t_asa_genre */
      struct T_ASA *parent;            /* pointeur sur le noeud parent */
      short calcule;                   /* Indique que la valeur est calculee */
      t_val *pVal;                     /* Pointeur sur un objet valeur */
      DECL_FN *fn;                     /* pointeur sur une structure fonction */
      t_index *pIndex;                 /* pointeur sur un index */
      t_column *pColumn;               /* Pointeur sur les infos colonne */
      struct T_ASA *branches[kiMAX_BRANCHES];  /* noeuds fils */
      char nom[1];                     /* label du noeud */
   } t_asa;
   
/* Declaration du type ASA NULL */
extern t_asa t_asa_null;
#define ASA_NULL          (&t_asa_null)

   /* Structure de gestion des etats et evenements */
   /*==============================================*/
   typedef enum {
      EVT_INIT, EVT_START, EVT_IN_PROGRESS, EVT_END,
         EVT_NORMAL, EVT_GROUP_BY,
         EVT_SEARCH,
         EVT_TRUE, EVT_FALSE,
         MAX_TYPE_EVENT
   } T_TYPE_EVENT;
      
      typedef struct {
         T_TYPE_EVENT event;
         T_TYPE_EVENT eventList;
         T_TYPE_EVENT nodeEvent[MAX_GENRE_ASA];
      } t_event;

/* Declaration du type NULL */
extern t_event t_event_null;
#define EVENT_NULL     (&t_event_null)
      
      /* Structure d'un message */
      /*========================*/
      typedef struct {
         int code;
         char level;
         char text[1];
      } t_message;
      
/* Declaration du type NULL */
extern t_message t_message_null;
#define MESSAGE_NULL     (&t_message_null)
   
      /* Structure de gestion d'erreur interne */
      /*=======================================*/
      typedef struct {
         int err;                       /* error code */
         char msg[SIZE_MSG + 1];        /* error message */
      } t_err_dex;
      
      extern t_err_dex errDex;
      
      /* Structure de gestion des compteurs */
      /*====================================*/
      typedef struct {
         UINT iter;                     /* nombre d'iteration */
         clock_t t_ref;                 /* Temps de reference */
         clock_t t_cumul;               /* Temps cumule */
      } t_counter;
      
      extern t_counter gtCounters[];
      
      /* Structure de gestion de la memoire */
      /*====================================*/
      typedef struct {
         UINT size;                     /* Taille du buffer */
         void *address;                 /* Pointeur sur l'objet */
         char name[kiNOM_OBJECT + 1];      /* nom de l'object */
      } t_objs;
      
/* Declaration du type NULL */
extern t_objs t_objs_null;
#define OBJS_NULL     (&t_objs_null)
   
      extern t_objs gtObjs[kiMAX_OBJECTS];     /* tableau des adresses des objets alloues */
      

      /*=====================*
      * Variables communes  *
      *=====================*/
      /*
      ** global variables declared in dex.c
      */
      extern char *gpcDebug;
      extern char *gpcTrace;
      extern char gacBuffer[kiBUFFER + 1]; /* Buffer standard */
      
      extern char gacLang[kiLANG + 1];
      extern char gacSep[kiSEP + 1];
      extern char gacNullString[kiNULL_STRING + 1];
      extern double gdRefDate;
      extern int gbFmt;
      extern int gbEndian;
      extern int gbCheck;
      extern int gbGenerateMetadata;
      extern char gacArrayBegin[kiSEP + 1];
      extern char gacArrayEnd[kiSEP + 1];
      extern char gacArraySep[kiSEP + 1];
      
      extern UINT giRowid;
      extern UINT giRowidSub;
      extern UINT giRownum;
      extern UINT giRownumAll;
      
      extern FILE *gpfResult;              /* fichier de sortie */
      extern char *gpcResult;              /* fichier de sortie */
      
      extern int tokenpos;
      extern t_asa *gpAsaRacine;
      extern char *gpcLabelGenre[];
      extern char *gpcLabelTypeFile[];
      
      /* Tableau des types DEX des valeurs passees aux fonctions */
      extern int gArgsType[MAX_ARGS];
      
      /* Tableau de correspondance EAST <-> DEX */
      extern T_TYPE_DEX gtEastToDex[MAX_TYPE_EAST];
      extern T_TYPE_EAST gtDexToEast[MAX_TYPE_DEX];

      /* Tableau de correspondance SDF <-> DEX */
      extern T_TYPE_DEX gtSdfToDex[MAX_TYPE_SDF];
      extern T_TYPE_SDF gtDexToSdf[MAX_TYPE_DEX];
      
      /* Gestion des valeurs */
      extern char *gpcClassVal[];        /* libelles des flags et Class */
      extern char *gpcTypeVal[];         /* libelles des types */
      extern UINT gNbValsAlloc;           /* Nombre des valeurs allouees */
      extern UINT gNbValsFree;            /* Nombre des valeurs liberees */
      extern UINT gNbMemsAlloc;           /* Nombre des sauvegardes allouees */
      extern UINT gNbMemsFree;            /* Nombre des sauvegardes liberees */
      extern UINT gNbPilesAlloc;          /* Nombre des piles allouees */
      extern UINT gNbPilesFree;           /* Nombre des piles liberees */
      extern UINT gNbObjsAlloc;           /* Nombre d'objets alloues */
      extern UINT gNbObjsRealloc;         /* Nombre d'objets Realloues */
      extern UINT gNbObjsFree;            /* Nombre d'objets liberees */
      extern UINT gNbNodesAlloc;          /* Nombre de nodes allouees */
      extern UINT gNbNodesFree;           /* Nombre de nodes liberees */
      extern UINT gNbIndexesAlloc;        /* Nombre d'indexes allouees */
      extern UINT gNbIndexesFree;         /* Nombre d'indexes liberees */
      extern UINT gNbMessagesAlloc;       /* Nombre des messages allouees */
      extern UINT gNbMessagesFree;        /* Nombre des messagesliberees */
      
      /* Pointeurs sur les principales branches */
      extern t_asa *gpNodeSelect;
      extern t_asa *gpNodeFor;
      extern t_asa *gpNodeWith;
      extern t_asa *gpNodeDefine;
      extern t_asa *gpNodeColonnes;
      extern t_asa *gpNodeFrom;
      extern t_asa *gpNodeWhere;
      extern t_asa *gpNodeGroupBy;
      extern t_asa *gpNodeBreakOn;
      extern t_asa *gpNodeFormat;
      
      /* Pseudo colonnes */
      extern t_asa *gpNodeRowid;
      extern t_asa *gpNodeRownum;
      
      /* Identifiant unique du noeud en cours de calcul */
      extern UINT giIdNode;
      
      /* Structure de gestion des evenements */
      extern t_event gEvent;
      
      /* indexes sur les nodes */
      extern t_index *indexSelect;
      extern t_index *indexFmt;
      extern t_index *indexAlias;
      extern t_index *indexColumn;
      extern t_index *indexRef;
      extern t_index *indexConst;
      extern t_index *indexKeyword;
      extern t_index *indexNodesFnGroup;
      extern t_index *indexGapDelta;
      extern t_index *indexErr;
      extern t_index *indexFrom;
      
      extern t_index *indexStdFn;    /* index sur les fonctions standards */
      extern t_index *indexMessage;  /* index sur les messages */
      extern t_index *indexMem;      /* index pour les valeurs sauvergardees */
      extern t_index *indexPushPop;  /* index pour les fonctions push et pop */
      
      /* global variables used by the parser (generated by yacc or lex)  */
      extern int yydebug;
      extern FILE *yyin;
      extern int *yyps;			/* top of state stack */
      
      extern int yystate;			/* current state */
      extern int yytmp;			/* extra var (lasts between blocks) */
      
      extern int numligne;			/* line error */
      extern int yynerrs;			/* number of errors */
      extern int yyerrflag;			/* error recovery flag */
      extern int yychar;			/* current input token number */
      
      extern int yyleng;
      
      extern char *pcSyntaxeDex[];
      extern int syntaxeId;
      
      /*=====================*
      * MACROS              *
      *=====================*/
      
#define FILS(a, pos) (((a) && pos >= 1 && pos <= kiMAX_BRANCHES)? (a)->branches[pos-1] : NULL)
#define PARENT(a) ((a)? (a)->parent : ASA_NULL)
#define NOM(a) ((a)? a->nom : "")
#define VIDE(a) ((a) == NULL || (a) == ASA_NULL)
#define TYPE(a) (((a) && (a)->pVal)? (a)->pVal->type : TYPE_NULL)
#define VAL(a) (((a) && (a)->pVal)? (a)->pVal->value : NULL)
#define EXPR(a, pos)  (dexExprTraiter((pos <= 0)? a : FILS((a),pos)))
#define OPER(a, pos)  ((a)? (a)->branches[pos-1]->nom : "")
      
#define FIC(pos) ((indexFrom && pos < indexFrom->nbr)? ((t_file *)indexFrom->tab[pos]) : FILE_NULL)
#define COLUMN(pos) ((indexColumn && pos < indexColumn->nbr)? (((t_asa *)indexColumn->tab[pos])->pColumn) : COLUMN_NULL)
      
#define LTRIM(chaine, car) \
      { while(*chaine == car) chaine++;}
      
      /* Macros de définition d'une valeur */
#define IS_TYPE(value, mask) (((value & TYPE_MASK) == (mask & TYPE_MASK))? 1 : 0) 
#define IS_CLASS(value, mask) (((value & CLASS_MASK) == (mask & CLASS_MASK))? 1 : 0) 
#define IS_FLAG(value, mask) (((value & FLAG_MASK) == (mask & FLAG_MASK))? 1 : 0) 
      
      /* Macro de declaration des fonctions d'une librairie */
#define ADD_LIB(nomLib) {\
   ret = header_##nomLib();\
   if(ret && atof(ret->name) == 0.0) {\
   errDex.err = 1;\
   dexMessageDisplay(110, "##nomLib");\
   }\
   \
   ret++;\
   while(ret && ret->name) {\
   if(dexFnAdd(ret) == NULL) {\
   errDex.err = 1;\
   if(!ret) dexMessageDisplay(206, "##nomLib");\
   else \
   dexMessageDisplay(102, (ret - 1)->name);\
   }\
   ret++;\
   }\
      }
      
      /*=====================*
      * Prototypes          *
      *=====================*/
      
      int yyparse(void);
      void yyerror(const char *);
      void yyerrorDex(int, ...);
      
      void dexProgExit(int );
      void dexLogTrace(int , char *, ...);
      
      int dexEastStatusCheck(int );
      int dexWordGet(char *, int, char *, char **);
      int dexTypesInit();
      
      extern int dexIntCmp(const void *, const void *);
      extern int dexUIntCmp(const void *, const void *);

      t_index *dexIndexNew(t_index *, char *, int , int );
      t_index *dexIndexDel(t_index *, void * (*delete) (void *));
      extern int dexIndexCmp(const void *, const void *);
      t_index *dexIndexAdd(t_index *, void *, int (*compar)(const void *, const void *));
      void *dexIndexSearch(t_index *, void *, int (*compar)(const void *, const void *));
      int dexIndexDelRang(t_index *, UINT, void * (*delete) (void *));
      int dexIndexDelKey(t_index *, void *, int (*compar) (const void *, const void *), void * (*delete) (void *));
      
      void dexEventSet(T_GENRE_ASA, T_TYPE_EVENT);
      T_TYPE_EVENT dexEventGet(T_GENRE_ASA );
      
      extern int dexParamSet(char *, t_asa *);

      extern t_message *dexMessageNew(int , char, char *);
      extern void *dexMessageDel(void *);
      extern int dexMessagesLoad(char *);
      extern char *dexMessageDisplay(int , ...);
      
      extern t_file *dexFileNew(int , char *, char *, char *);
      extern t_file *dexFileDel(t_file *);
      extern int dexFileCmp(const void *, const void *);
      extern t_file *dexFileGet(char *);
      extern t_file *dexFileOpen(t_file *);
      extern t_file *dexFileClose(t_file *);
      extern int dexFileRestart(t_file *);
      extern int dexFileRecordNext(t_file *);
      extern int dexFileColumnGet(t_column *);
      extern int dexFileTypeGet(char *);
      extern void dexFileDisplay(t_file *);

      extern t_column *dexColumnNew(t_asa *);
      extern t_column *dexColumnDel(t_column *);
      extern int dexColumnCmp(const void *, const void *);
      extern void dexColumnDisplay(t_column *);

      extern void dexCounterStart(int );
      extern void dexCounterStop(int );
      extern void dexCounterDisplay(int );
      extern void dexCounterDisplays();
      
      extern void *dexObjNew(void *, UINT, char *);
      extern void *dexObjDel(void *);

      extern t_asa *dexNodeNew(T_GENRE_ASA, char *, ...);
      extern t_asa *dexNodeDel(t_asa *);
      extern void dexNodeDisplay(t_asa *, int, short);
      extern t_val *dexNodeValSet(t_asa *, int, void *, ...);
      extern void dexNodeValUnset(t_asa *);
      
      extern t_mem *dexMemSet(UINT ,UINT , t_val *);
      extern t_mem *dexMemGet(UINT );
      extern t_mem *dexMemDel(void * );
      extern int dexMemCmp(const void *, const void *);

      extern t_index *dexPilePush(char *, t_val *);
      extern t_val *dexPilePop(char *, UINT );
      extern t_val *dexPileRead(char *, UINT );
      extern t_index *dexPileDel(void *);
      
      extern void dexRequeteExecuter(t_asa *);
      extern void dexXmlGen(void );
      extern t_val *dexExprTraiter(t_asa *);
      
      extern int dexFnCmp(const void *, const void *);
      extern void dexLibAdd();
      extern t_val *dexFnCall(DECL_FN *, t_asa *);
      extern DECL_FN *dexFnAdd(DECL_FN *);
      extern void dexFnDisplay(DECL_FN *);
      
      extern t_val *dexValSet(t_val *, int , void *, ...);
      extern void *dexValGet(t_val *, UINT);
      extern t_val *dexValDel(t_val *);
      extern int dexValCmp(t_val *, t_val *);
      extern int dexValPropGet(t_val *);
      extern int dexValClassGet(t_val *);
      extern int dexValTypeGet(t_val *);
      extern int dexValLengthGet(t_val *);
      extern int dexValFlagGet(t_val *);
      extern int dexValGetb(t_val *, UINT);
      extern char dexValGeti8(t_val *, UINT);
      extern short int dexValGeti16(t_val *, UINT);
      extern int dexValGeti32(t_val *, UINT);
      extern INT64  dexValGeti64(t_val *, UINT);
      extern float dexValGetf(t_val *, UINT);
      extern double dexValGetd(t_val *, UINT);
      extern char *dexValGetpc(t_val *, char *, UINT);
      extern t_date *dexValGetdate(t_val *);
      extern t_date_SDF *dexValGetdateSDF(t_val *, int );
      extern void *dexValToBin(t_val *, int, UINT);
      extern t_val *dexExprCond(int , t_val *, t_val *);
      extern t_val *dexExprMath(int , t_val *, t_val *);
      extern void *dexDateConv(void *, int);
      extern double MJDto(double, int *, unsigned int *, unsigned int *);
      extern double toMJD(int , unsigned int , unsigned int );
      extern double CJDto(double, int *, unsigned int *, unsigned int *);
      extern double toCJD(int , unsigned int , unsigned int );
      extern double ReftoMJD(double, double);
      extern double MJDtoRef(double, double);
      
      extern void *dexMinMax(void *, int);
      extern void *dexSwap(void *, int);
      extern t_val *dexValSwap(t_val *);
      extern void *dexValueSwap(void *, int , UINT, UINT );
      
      double dexDigitsDecode(void *, UINT );
      extern char *dexFmtFortranConv(char *);
      
      /* Pour compatibilite avec win32 */
#ifdef _MSC_VER
#define snprintf _snprintf
#define vsnprintf _vsnprintf
#define isnan _isnan
      
      extern int strcasecmp(const char *, const char *);
      extern int strncasecmp(const char *, const char *, size_t );
      extern int snprintf(char *, size_t, const char *, ...);
      extern int vsnprintf(char *, size_t, const char *, va_list);
      extern int isnan(double);
#endif
      
#ifdef __cplusplus
}
#endif

#endif

