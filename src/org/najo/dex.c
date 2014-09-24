#include "dex.h"               /* Definitions generales */

/* Globals variables */
/*********************/

char *gpcDebug = NULL;
char *gpcTrace = NULL;
char gacBuffer[kiBUFFER + 1] = "";

/* Erreur globale DEX */
t_err_dex errDex = {0};

int tokenpos = 0;
int numligne = 1;

/* Declaration des compteurs pour les perfs */
t_counter gtCounters[kiMAX_COUNTERS] = {0};

/* Variables de parametrages par defaut */
char gacLang[kiLANG + 1] = "en";            /* Englais */
char gacSep[kiSEP + 1] = " ";               /* Separateur de champs */
char gacNullString[kiNULL_STRING + 1] = ""; /* NULL non affiche */
double gdRefDate = kiTAI;                   /* 01/01/1958 */
int gbFmt = kiBINARY;                       /* Affichage binaire */
int gbCheck = 0;                            /* flag de test de la requete */
int gbGenerateMetadata = 0;                 /* flag de generation des METADATA */
char gacArrayBegin[kiSEP + 1] = "";         /* marqueur de début de tableau */
char gacArrayEnd[kiSEP + 1] = "";           /* marqueur de fin de tableau */
char gacArraySep[kiSEP + 1] = "";           /* Separateur d'éléments de tableau */

/* On conserve la convention de la machine par defaut */
#ifdef _BIG_ENDIAN
int gbEndian = kiBIG_ENDIAN;                 /* convention MSB (SUN) */
#else
int gbEndian = kiLITTLE_ENDIAN;              /* convention MSB (PC) */
#endif

/* Variables de stats des traitements */
UINT giRowid = 0;
UINT giRowidSub = 0;
UINT giRownum = 1;
UINT giRownumAll = 1;

/* Racine de l'arbre */
t_asa *gpAsaRacine = {NULL};

/* Declaration des valeurs par defaut pour chaque type null */
t_column t_column_null = {0};
t_file t_file_null = {0};
t_asa t_asa_null = {0};
t_index t_index_null = {0};
t_val t_val_null = {0};
t_mem t_mem_null = {0};
t_event t_event_null = {0};
t_message t_message_null = {0};
t_objs t_objs_null = {0};
t_obj_ext t_obj_ext_null = {0};

/* indexes */
t_index *indexSelect = NULL;
t_index *indexFmt = NULL;
t_index *indexColumn = NULL;
t_index *indexRef = NULL;
t_index *indexAlias = NULL;
t_index *indexConst = NULL;
t_index *indexKeyword = NULL;
t_index *indexNodesFnGroup = NULL;
t_index *indexGapDelta = NULL;
t_index *indexStdFn = NULL;
t_index *indexErr = NULL;
t_index *indexMessage = NULL;
t_index *indexMem = NULL;
t_index *indexPushPop = NULL;
t_index *indexFrom = NULL;

/* Variables  globales pour les fichiers */
FILE *gpfResult = NULL;          /* global aux traitements */
char *gpcResult = NULL;
   

static FILE *gpfRequest = NULL;
static char *gpcRequest = NULL;

static char *gpcDexConf = "dex.ini";           /* Fichier de conf. */
static FILE *gpfDexConf = NULL;
static char *gpcDexPath = NULL;                /* Chemin du fichier de conf. */
static char *gpcDexHelp = NULL;
static FILE *gpfDexHelp = NULL;

#ifdef _MSC_VER
int yywrap()
{
   return 1;
}
#endif

/****************************************************************/
/*                                                              */
/* Function : dexProgExit                                         */
/*                                                              */
/* Description    : Free memory and exit program                */
/*                                                              */
/****************************************************************/

void dexProgExit(int iErr) {
   static int bExit = 0;
   
   time_t sDate = {0};
   UINT iRang = 0;
   UINT iRangBis = 0;
   
   t_asa *a = NULL;
   
   /* Evite la reentrance */
   if(bExit) return;
   bExit = 1;
   
   /* Display the tree and indexes */
   /*------------------------------*/
   if(gpcTrace) {
      dexLogTrace(1, "\n=>Arbre syntaxique : \n");
      dexNodeDisplay(gpAsaRacine, 0, 1); 
      
      dexLogTrace(1, "\n=>index des mots clef : \n");
      for(iRang = 0;indexKeyword && iRang < indexKeyword->nbr; iRang++)
         dexNodeDisplay(indexKeyword->tab[iRang], 0, 1); 
      
      dexLogTrace(1, "\n=>index des constantes : \n");
      for(iRang = 0;indexConst && iRang < indexConst->nbr; iRang++)
         dexNodeDisplay(indexConst->tab[iRang], 0, 1); 
      
      dexLogTrace(1, "\n=>index des alias (clause FOR & WITH) : \n");
      for(iRang = 0;indexAlias && iRang < indexAlias->nbr; iRang++) {
         a = indexAlias->tab[iRang];
         dexNodeDisplay(a, 0, 1); 
         for(iRangBis = 0;a->pIndex && iRangBis < a->pIndex->nbr; iRangBis++)
            dexNodeDisplay(a->pIndex->tab[iRangBis], 1, 0);
      } 
      
      dexLogTrace(1, "\n=>index des alias East (clause FOR) : \n");
      for(iRang = 0;indexColumn && iRang < indexColumn->nbr; iRang++)
         dexNodeDisplay(indexColumn->tab[iRang], 0, 1); 
      
      dexLogTrace(1, "\n=>index des colonnes (clause SELECT) : \n");
      for(iRang = 0;indexSelect && iRang < indexSelect->nbr; iRang++) {
         dexNodeDisplay(FILS((t_asa *)(indexSelect->tab[iRang]), 1), 0, 1); 
         if(iRang <= indexFmt->nbr) dexNodeDisplay(indexFmt->tab[iRang], 0, 1); 
      }
      
      dexLogTrace(1, "\n=>index des fichiers (clause FROM) : \n");
      for(iRang = 0;indexFrom && iRang < indexFrom->nbr; iRang++)
         dexFileDisplay(FIC(iRang)); 
      
      dexLogTrace(1, "\n=>index des fonctions : \n");
      for(iRang = 0;indexStdFn && iRang < indexStdFn->nbr; iRang++)
         dexFnDisplay(indexStdFn->tab[iRang]); 
      
      dexLogTrace(1, "\n=>index des nodes des fonctions groupes : \n");
      for(iRang = 0;indexNodesFnGroup && iRang < indexNodesFnGroup->nbr; iRang++)
         dexNodeDisplay(indexNodesFnGroup->tab[iRang], 0, 1); 
      
      dexLogTrace(1, "\n=>index des nodes des fonctions gap et delta : \n");
      for(iRang = 0;indexGapDelta && iRang < indexGapDelta->nbr; iRang++)
         dexNodeDisplay(indexGapDelta->tab[iRang], 0, 1); 
      
   }
   
   if(gpcDebug) {
      /* Affichage des stats du traitement */
      dexMessageDisplay(10, giRownumAll - 1, giRowid);
      
      time(&sDate);
      dexMessageDisplay(3, ctime(&sDate));
   }
   
   if(gpcTrace) {
      /* if(gpcDebug) { */
      /* Affiche les stats de perf */
      dexLogTrace(1, "\n=> Statistiques de perf. : \n");
      dexCounterDisplays();
   }
   
   /* Free memory */
   /*-------------*/
   /* Free indexes */
   dexIndexDel(indexFrom, (void * (*) (void *))dexFileDel);   /* Arret des serveurs */
   dexIndexDel(indexErr, NULL);
   dexIndexDel(indexStdFn, NULL);
   dexIndexDel(indexNodesFnGroup, NULL);
   dexIndexDel(indexGapDelta, NULL);
   dexIndexDel(indexRef, NULL);
   dexIndexDel(indexConst, (void * (*) (void *))dexNodeDel);
   dexIndexDel(indexKeyword, (void * (*) (void *))dexNodeDel);
   dexIndexDel(indexColumn, NULL);
   dexIndexDel(indexAlias, NULL);
   dexIndexDel(indexFmt, NULL);
   dexIndexDel(indexSelect, NULL);
   dexIndexDel(indexMessage, (void * (*) (void *))dexMessageDel);
   dexIndexDel(indexMem, (void * (*) (void *))dexMemDel);
   dexIndexDel(indexPushPop, (void * (*) (void *))dexPileDel);

   /* Libere les pseudo colonnes si non referencees dans l'arbre */
   if(gpNodeRowid && !gpNodeRowid->parent) gpNodeRowid = dexNodeDel(gpNodeRowid);
   if(gpNodeRownum && !gpNodeRownum->parent) gpNodeRownum = dexNodeDel(gpNodeRownum);

   /* libere l'arbre tree */
   gpAsaRacine = dexNodeDel(gpAsaRacine);
   
   if(gpcTrace) {
      /* Affiche le nombre des valeurs allouees */
      dexLogTrace(1, "\n=> Nombre de nodes liberes/alloues           : %d/%d", gNbNodesFree, gNbNodesAlloc);
      dexLogTrace(1, "\n=> Nombre de valeurs liberees/allouees       : %d/%d", gNbValsFree, gNbValsAlloc);
      dexLogTrace(1, "\n=> Nombre de sauvegardes liberees/allouees   : %d/%d", gNbMemsFree, gNbMemsAlloc);
      dexLogTrace(1, "\n=> Nombre de piles liberees/allouees         : %d/%d", gNbPilesFree, gNbPilesAlloc);
      dexLogTrace(1, "\n=> Nombre d'indexes liberees/allouees        : %d/%d", gNbIndexesFree, gNbIndexesAlloc);
      dexLogTrace(1, "\n=> Nombre de messages liberees/allouees      : %d/%d", gNbMessagesFree, gNbMessagesAlloc);
      dexLogTrace(1, "\n=> Nombre d'objets liberes/alloues/Realloues : %d/%d/%d\n", gNbObjsFree, gNbObjsAlloc, gNbObjsRealloc);

   }

   while(iRang < gNbObjsAlloc) {
      if(gtObjs[iRang].address != NULL) {
         if(gpcTrace) {
            dexLogTrace(1, "\n=> Objet id = %d, adresse = %08x, taille = %7d, nom = '%s'", 
                        iRang, gtObjs[iRang].address, gtObjs[iRang].size, gtObjs[iRang].name);
            if(!strcmp(gtObjs[iRang].name, "dexNodeNew"))
               dexLogTrace(1, " (%s)", ((t_asa *)gtObjs[iRang].address)->nom);
         }
         else {
            dexObjDel(gtObjs[iRang].address);  
         }
      }
      iRang++;
   }

   /* Close Files */
   if(gpfResult != NULL) fclose(gpfResult);
   if(gpfRequest != NULL) fclose(gpfRequest);
   
   fflush(stdout);
   fflush(stderr);

   exit(iErr);
}

/****************************************************************/
/*                                                              */
/* Function : dexLogTrace                                         */
/*                                                              */
/* Description    :                                             */
/*                                                              */
/****************************************************************/

void dexLogTrace(int iCase, char *pcErr, ...)
{
   va_list args;
   
   /* Error case */
   char *pcTextErr = NULL;
   
   va_start(args, pcErr); 
   
   switch (iCase) {
   case 0 :                             /* Sortie standard message */
      vfprintf(stdout, pcErr, args);
      break;
   case 1 :                              /* Sortie standard erreur */
      vfprintf(stderr, pcErr, args);
      break;
   case 2 :                              /* Sortie des erreurs applicatives */
      vfprintf(stderr, pcErr, args);
      fputs("\n", stderr);
      dexProgExit(iCase);
      break;
   default :                             /* Sortie des erreurs system */
      pcTextErr = strerror(errno);
      if(pcTextErr){
         vfprintf(stderr, pcErr, args);
         fprintf(stderr, ":%s", pcTextErr);
         fputs("\n", stderr);
      }
      dexProgExit(iCase);
      break;
   }
   
   va_end(args);
}

void yyerrorDex(int err, ...) {
   char *msg = NULL;
   static int numLignePrec = 0;
   
   va_list args;
   
   va_start(args, err); 
   msg = va_arg(args, char *); 
   va_end(args);
   
   /* On affiche qu'une erreur par ligne */
   if(numligne == numLignePrec) return;

   dexLogTrace(1, "\n%*s", 1 + tokenpos - yyleng, "^");
   dexMessageDisplay(err, gpcRequest, numligne, msg);
   numLignePrec = numligne;
}

/****************************************************************/
/*                                                              */
/* Function : Functions for statistics                          */
/*                                                              */
/* Description    : Start, stop and displaying stats            */
/*                                                              */
/****************************************************************/

/* Demarre un compteur */
void dexCounterStart(int num) {
   t_counter *pCounter;
   
   if(num < 0 || num >= kiMAX_COUNTERS)
      dexLogTrace(2, "=>Numero de compteur < 0 ou > %d\n", kiMAX_COUNTERS);
   
   pCounter = &gtCounters[num];
   pCounter->iter++;
   /* Initialisation du temps de reference si premier appel */
   if(pCounter->t_ref == 0) pCounter->t_ref = clock();
}

/* Arrete et cumul un compteur */
void dexCounterStop(int num) {
   t_counter *pCounter;
   clock_t tCurrent = 0;
   
   tCurrent = clock();
   
   if(num < 0 || num >= kiMAX_COUNTERS)
      dexLogTrace(2, "=>Numero de compteur < 0 ou > %d\n", kiMAX_COUNTERS);
   
   pCounter = &gtCounters[num];
   if(pCounter->t_ref != 0) {
      /* Cumul du temps en millisecondes */
      pCounter->t_cumul += (tCurrent - pCounter->t_ref);
      pCounter->t_ref = 0;
   }
}	

/* Affiche les stats d'un compteur */
void dexCounterDisplay(int num) {
   t_counter *pCounter;
   
   if(num < 0) {
      /* affiche l'entete */
      dexLogTrace(1, "compteur iterations temps total (ms) temps moyen (ms)\n");
      dexLogTrace(1, "-----------------------------------------------------\n");
      return;
   }
   
   /* Arrete et cumul le compteur si toujour en cours */
   dexCounterStop(num);
   
   pCounter = &gtCounters[num];
   if(pCounter->iter != 0) {
      dexLogTrace(1, "%8d %10d %16.2f %16.2f\n", num, pCounter->iter, 
         ((double )pCounter->t_cumul * 1000.0 / CLOCKS_PER_SEC),
         ((double )pCounter->t_cumul * 1000.0 / CLOCKS_PER_SEC) / pCounter->iter);
      
   }
}	

void dexCounterDisplays() {
   int iRang;
   dexCounterDisplay(-1);  /* Affiche l'entete */
   for(iRang = 0; iRang < kiMAX_COUNTERS; iRang++){
      dexCounterDisplay(iRang);
   }
}

/****************************************************************/
/*                                                              */
/* Function :  dexWordGet                                          */
/*                                                              */
/* Description    : Read a word from a buffer                   */
/*                                                              */
/* Return         : 0 -> Ok                                     */
/*                  1 -> Empty                                  */
/*                  2 -> Size Error                             */
/****************************************************************/

int dexWordGet(char *pcBuffer, int iLong, char *pcSource, char **ppcReturn)
{
   int iErr = 0;
   int iSize = 0;
   
   char *pcSearch;
   char cEnd = ' ';              /* Se termine par ' ' par defaut */
   
   memset(pcBuffer, '\0', iLong); 
   
   
   /* Suppress blanks */
   LTRIM(pcSource, ' ');
   
   if(*pcSource == '#')
   {
      /* Ignore rest of line */
      return 1;
   }
   
   pcSearch = pcSource;
   
   /* Ignore cotation marks */
   if(*pcSearch == '"')
   {
      pcSearch++;
      cEnd = '"';
   }
   
   while(*pcSearch == '\\' ||
      (*pcSearch != '\0' &&
      *pcSearch != '\n' &&
      *pcSearch != '=' &&
      *pcSearch != cEnd))
   {
      if(++iSize > iLong) break;
      if(*pcSearch == '\\') pcSearch++;
      *pcBuffer = *pcSearch;
      pcBuffer++;
      pcSearch++;
   }
   
   if(iSize == 0) iErr = 1;
   
   if(iSize > iLong) iErr = 2;
   else if(*pcSearch == '"') pcSearch++;
   
   *ppcReturn = pcSearch;
   return iErr;
}

/****************************************************************/
/*                                                              */
/* Function :  dexParamSet                                      */
/*                                                              */
/* Description    : dexSet parameter                            */
/*                                                              */
/* Return         : 0 -> Ok                                     */
/*                  1 -> bad parameter                          */
/*                  2 -> bad value                              */
/****************************************************************/

int dexParamSet(char *name, t_asa *val)
{
	int iErr = 0;
	
	if(name == NULL) return 1;
	if(val == NULL) return 2;
	
	if(!strcasecmp(name, "LANG")) {
		if(val->genre == a_string || val->genre == a_ident) {
			
			/* chargement des messages suivant la langue choisie */
			if(strcmp(dexLower(NOM(val)), gacLang) != 0) {
				strncpy(gacLang, dexLower(NOM(val)), kiLANG);
				snprintf(gacBuffer, kiBUFFER, "%s/%s.%s", gpcDexPath, "dex_messages", gacLang);
				dexMessagesLoad(gacBuffer);
			}
		}
		else iErr = 2;
	}
	else if(!strcasecmp(name, "COLSEP")) {
		if(val->genre == a_string || val->genre == a_ident) 
			strncpy(gacSep, NOM(val), kiSEP);
		else iErr = 2;
	}
	else if(!strcasecmp(name, "FMT_DEFAULT")) {
		if(val->genre == a_string || val->genre == a_ident) {
			if(!strcmp("ASCII", NOM(val))) gbFmt = kiASCII;
			else gbFmt = kiBINARY;
		}
		else iErr = 2;
	}
	else if(!strcasecmp(name, "NULL_STRING")) {
		if(val->genre == a_string || val->genre == a_ident) 
			strncpy(gacNullString, NOM(val), kiNULL_STRING);
		else iErr = 2;
	}
	else if(!strcasecmp(name, "REF_DATE")) {
		if(val->genre == a_double || val->genre == a_integer) 
			gdRefDate = atof(VAL(val));
		else iErr = 2;
	}
	else if(!strcasecmp(name, "VERBOSE")) {
		if(val->genre == a_string || val->genre == a_ident) {
			if(strcasecmp(NOM(val), "YES") == 0) gpcDebug = "TRUE";
			else gpcDebug = "FALSE";
		}
		else iErr = 2;
	}
	/* Separateurs tableaux */
	else if(!strcasecmp(name, "ARRAY_BEGIN")) {
		if(val->genre == a_string || val->genre == a_ident) 
			strncpy(gacArrayBegin, NOM(val), kiSEP);
		else iErr = 2;
	}
	else if(!strcasecmp(name, "ARRAY_END")) {
		if(val->genre == a_string || val->genre == a_ident) 
			strncpy(gacArrayEnd, NOM(val), kiSEP);
		else iErr = 2;
	}
	else if(!strcasecmp(name, "ARRAY_SEP")) {
		if(val->genre == a_string || val->genre == a_ident) 
			strncpy(gacArraySep, NOM(val), kiSEP);
		else iErr = 2;
	}
	

return iErr;
}

/*==============================
* Fonction principale 
* =============================*/

int main(int argc, char *argv[])
{
   int yyRet = 0;
   int iErr = 0;
   int iRang = 0;
   
   /* Variables du fichier de conf. */
   char acWord[kiBUFFER + 1];
   char acValue[kiBUFFER + 1];
   char acLine[kiBUFFER + 1];
   char *pcLine = NULL;
   int iLine = 0;
   int bConst = 0;
   
   gpfResult = stdout;     /* Sortie standard par defaut */
   
   /* Demarre le compteur referentiel */
   clock();
   
   /* Chargement des ressources */
   /*****************************/
   gpcTrace = getenv("DEX_DEBUG");
   
   gpcDexPath = getenv("DEX_PATH");
   if(!gpcDexPath || *gpcDexPath == '\0') gpcDexPath = ".";
   
   /* chargement des messages en Anglais par defaut */
   snprintf(gacBuffer, kiBUFFER, "%s/%s.%s", gpcDexPath, "dex_messages", "en");
   dexMessagesLoad(gacBuffer);
   
   /* Creation des indexes necessaires */
   indexSelect = dexIndexNew(indexSelect, "Colonnes", 50, 20);
   indexFmt = dexIndexNew(indexFmt, "Formats", 50, 20);
   indexColumn = dexIndexNew(indexColumn, "Alias des colonnes", 100, 50);
   indexRef = dexIndexNew(indexRef, "Alias expressions", 100, 50);
   indexAlias = dexIndexNew(indexAlias, "Alias east + expressions", 200, 50);
   indexConst = dexIndexNew(indexConst, "Constantes", 30, 10);
   indexKeyword = dexIndexNew(indexKeyword, "Mots clef", 50, 10);
   indexNodesFnGroup = dexIndexNew(indexNodesFnGroup, "Nodes des fonctions groupes", 10, 5);
   indexGapDelta = dexIndexNew(indexGapDelta, "Nodes des fonctions gap et delta", 10, 5);
   indexErr = dexIndexNew(indexErr, "Errors", 50, 10);
   indexMem = dexIndexNew(indexMem, "Mem", 50, 10);
   indexPushPop = dexIndexNew(indexPushPop, "Push - Pop", 10, 10);
   indexFrom = dexIndexNew(indexFrom, "From clause", 20, 10);
   
   /* Chargement du fichier du configuration */
   /******************************************/
   
   snprintf(gacBuffer, kiBUFFER, "%s/%s", gpcDexPath, gpcDexConf);
   
   /* Control the configuration file for reading */
   if ((gpfDexConf = fopen(gacBuffer, "r")) == NULL)
   {
      dexMessageDisplay(301, gacBuffer);
   }
   
   while(fgets(acLine, kiBUFFER, gpfDexConf)) {
      bConst = 0;
      iLine++;
      pcLine = acLine;
      
      /* Suppress blanks */
      LTRIM(pcLine, ' ');
      
      /* Ignored lines */
      if(*pcLine == '\n' ||        /* New line */
         *pcLine == '#' ||         /* Comment */
         *pcLine == '\0')          /* empty */
         continue;
      
      /* Key name */
      if(*pcLine == '=') dexMessageDisplay(221, gpcDexConf, 
         (pcLine)? pcLine : "NULL", iLine);    
      
      iErr = dexWordGet(acWord, kiBUFFER, pcLine, &pcLine);
      if(iErr) dexMessageDisplay(220, gpcDexConf, 
         (pcLine)? pcLine : "NULL", iLine);    
      
      /* Suppress blanks */
      LTRIM(pcLine, ' ');
      
      if(!strcasecmp(acWord, "CONST")) {
         iErr = dexWordGet(acWord, kiBUFFER, pcLine, &pcLine);
         if(iErr) dexMessageDisplay(220, gpcDexConf,
            (pcLine)? pcLine : "NULL", iLine);    
         
         /* Suppress blanks */
         LTRIM(pcLine, ' ');
         bConst = 1;
      }
      
      /* = */
      if(*pcLine != '=') dexMessageDisplay(223, gpcDexConf, iLine);    
      pcLine++;
      
      /* Suppress blanks */
      LTRIM(pcLine, ' ');
      
      /* Key value */
      iErr = dexWordGet(acValue, kiBUFFER, pcLine, &pcLine);
      // if(iErr) dexMessageDisplay(222, gpcDexConf, (pcLine)? pcLine : "NULL", iLine);
      if(iErr) pcLine = "";
      
      /* Prise en compte des constantes dans DEX */
      /*=========================================*/
      if(bConst) {
         /* Creation d'une constante */
         dexNodeNew(a_const,acWord,dexNodeNew(a_double,acValue, NULL),NULL);
      }

      /* Prise en compte des parametres dans DEX */
      /*=========================================*/
      else if(!strcasecmp(acWord, "LANG"))
         strncpy(gacLang, dexLower(acValue), kiLANG);
      else if(!strcasecmp(acWord, "COLSEP"))
         strncpy(gacSep, acValue, kiSEP);
      else if(!strcasecmp(acWord, "FMT_DEFAULT")) {
         if(!strcmp("ASCII", acValue)) gbFmt = kiASCII;
      }
      else if(!strcasecmp(acWord, "NULL_STRING"))
         strncpy(gacNullString, acValue, kiNULL_STRING);
      else if(!strcasecmp(acWord, "REF_DATE"))
         gdRefDate = atof(acValue);
      else if(!strcasecmp(acWord, "VERBOSE") &&
         strcasecmp(acValue, "YES") == 0) {
         gpcDebug = "TRUE";
#if YYDEBUG
         yydebug = 1;
#endif
      }
      /* Separateurs tableaux */
      else if(!strcasecmp(acWord, "ARRAY_BEGIN"))
         strncpy(gacArrayBegin, acValue, kiSEP);
      else if(!strcasecmp(acWord, "ARRAY_END"))
         strncpy(gacArrayEnd, acValue, kiSEP);
      else if(!strcasecmp(acWord, "ARRAY_SEP"))
         strncpy(gacArraySep, acValue, kiSEP);
   }
   
   fclose(gpfDexConf);
   
   /* chargement des messages suivant la langue choisie */
   if(strcmp(dexLower(gacLang), "en") != 0) {
      snprintf(gacBuffer, kiBUFFER, "%s/%s.%s", 
         gpcDexPath, "dex_messages", gacLang);
      dexMessagesLoad(gacBuffer);
   }
   
   /* Control des arguments */
   /*************************/
   
   iErr = 1;         /* Nbr de parametres obligatoires */
   for(iRang = 1; iRang < argc; iRang++)
   {
      /* Acquisition des parametres */
      if(argv[iRang][0] == '-')
      {
         /* Parametres optionnels */
         switch(argv[iRang][1])
         {
         case 'c' :
         case 'C' : 
            gbCheck = 1;
            break;
         case 'h' :
         case 'H' : 
         case '?' : 
            /* Affichage de l'aide */
            iErr = 99;    
            gpcDexHelp = "dex_help";
            break;
         case 'o' :
         case 'O' : 
            if((iRang + 1) >= argc) {iErr += 99; break;}
            gpcResult = argv[iRang + 1];
            if(!strcmp(gpcResult, "-")) {iErr += 99; break;}
            iRang++;
            break;
         case 's' :
         case 'S' : 
            /* Convention de la sortie binaire (BIG ou LITTLE ENDIAN) */
            if((iRang + 1) >= argc) {iErr += 99; break;}
            if(!strcasecmp(argv[iRang + 1], "BIG")) gbEndian = kiBIG_ENDIAN;
            else if(!strcasecmp(argv[iRang + 1], "LITTLE")) gbEndian = kiLITTLE_ENDIAN;
            else {iErr += 99; break;}
            iRang++;
            break;
         case 'v' :
         case 'V' : 
            {
               time_t sDate;
               gpcDebug = "TRUE";
#if YYDEBUG
               yydebug = 1;
#endif
               time(&sDate);
               dexMessageDisplay(2, ctime(&sDate));
               dexLogTrace(1, "=>MODE VERBOSE\n");
            }
            break;
         default : iErr++; break;
         }
         continue;
      }
      
      /* Parametres obligatoires */
      if(!gpcRequest) {
         gpcRequest = argv[iRang];
         iErr--;
         continue;
      }
      
      /* If No found => error */
      iErr += 99;
   }
   
   /* Affichage de l'aide */
   /***********************/
   
   if(gpcDexHelp) {
      snprintf(gacBuffer, kiBUFFER, "%s/%s.%s", 
         gpcDexPath, "dex_help", gacLang);
      
      /* Control the configuration file for reading */
      if ((gpfDexHelp = fopen(gacBuffer, "r")) == NULL) {
         dexMessageDisplay(301, gacBuffer);
      }
      
      while(fgets(acLine, kiBUFFER, gpfDexHelp)) {
         dexLogTrace(0, acLine);
      }
      fclose(gpfDexHelp);
   }
   
   /* Control de la syntaxe */
   /*************************/
   if (iErr)
   {
      dexMessageDisplay(1);
      dexLogTrace(0, "dex %s - Copyright 2006 CNES / GFI Informatique\n", DEX_VERSION);   
      dexProgExit(1);
   }
   
   /* Initialisation des types */
   /****************************/
   
   dexTypesInit();
   
   /* Ouverture des fichiers */
   /**************************/
   if(gpcRequest) {
      /* Control the request file for reading */
      if ((gpfRequest = fopen(gpcRequest, "r")) == NULL) {
         dexMessageDisplay(301, gpcRequest);
      }
      if(gpcDebug) dexMessageDisplay(4, gpcRequest);
      yyin = gpfRequest;
   }

   if(gpcResult) {
      /* Control the result file for writting */
      if ((gpfResult = fopen(gpcResult, "wb")) == NULL)
      {
         dexMessageDisplay(301, gpcResult);
      }
      if(gpcDebug) dexMessageDisplay(4, gpcResult);
   }
   
   /* Chargement des fonctions */
   /****************************/
   dexLibAdd();
   
   /* Analyse syntaxique de la requete */
   /************************************/
   if ((yyRet = yyparse()) == 0) {
      if (yynerrs != 0) {
         dexMessageDisplay(245, yynerrs);
      }
   }
   else dexMessageDisplay(250);
   
   /* Analyse intraseque de la requete */
   /***********************************/

   /* controle de la clause GROUP BY pour les fonctions groupes */
   if(indexNodesFnGroup->nbr != 0 &&
      gpNodeGroupBy == NULL) {
      UINT iFn;
      /* la clause GROUP BY est obligatoire pour les fonctions groupes */
      dexMessageDisplay(162);
      for(iFn = 0;indexNodesFnGroup && iFn < indexNodesFnGroup->nbr; iFn++)
         dexNodeDisplay(indexNodesFnGroup->tab[iFn], 0, 0); 
      dexMessageDisplay(250);
   }
   
   /* Controle des alias fic sur les colonnes */
   {
      UINT iRang = 0;
      t_column *pColumn = NULL;

      for(iRang = 0; iRang < indexColumn->nbr; iRang++) {
         pColumn = COLUMN(iRang);
         if(pColumn->pFile == NULL) {
            dexMessageDisplay(161, gpcRequest, 0, NOM(PARENT((t_asa *)pColumn->pNode)));
            dexMessageDisplay(250);
         }
      }
   }

   /* Traitement de la requete */
   /****************************/
   dexCounterStart(0);
   
   if(!gbCheck) {
      if(gpcDebug) dexMessageDisplay(9);
      if(gbGenerateMetadata) {
         /* Generation des METADATA */
         dexXmlGen();
      }
      else {
         /* Traitement des données */
         dexRequeteExecuter(gpAsaRacine);  
      }
   }
   
   dexCounterStop(0);
   
   dexProgExit(0);
}
