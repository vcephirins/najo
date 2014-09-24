#include "dex.h"
#include "y.tab.h"

/*====================================
* Declaration des variables globales
* ===================================*/

/* Correspond a l'enumeration t_genre_esa */
char *gpcLabelGenre[MAX_GENRE_ASA] = {
	   "alias", "and",
      "bool", "break on",
      "colonne", "constante",
      "date_iso_a", "date_iso_b",
      "define", "different", "divise", "double",
      "egale",
	   "alias fichier", "fichier", "type fichier",
      "flag", "fmt_ascii", "fmt_bin", "fonction", "for", "format", "from",
      "group_by",
      "hexa",
      "ident", "inferieur", "inferieur_egale", "integer", 
      "intervalle",
      "keyword",
      "liste_alias", "liste_arg", "liste_fichiers",
      "liste_fmt_ascii", "liste_fmt_ascii_sep", "liste_fmt_bin", "liste_ident",
      "liste_colonnes", "liste_intervalles", "liste_ref",
      "liste1d", "liste2d", "liste3d",
      "liste_liste1d", "liste_liste2d", "liste_liste3d",
      "liste_surfaces", "liste_volumes",
      "modulo", "moins", "moins_unaire", "multiple",
      "not", 
      "or", 
      "plus", "plus_unaire", "puissance",
      "ref", "requete", "rowid", "rownum",
      "select", "sel_for", "sel_with", "sel_define", "sel_colonnes", 
      "sel_from", "sel_where", "sel_group_by", "sel_break_on", "sel_format",
      "string", "superieur", "superieur_egale", "surface",
      "type",
      "volume",
      "where", "with"};

   /* Declaration des noeuds principaux */
   t_asa *gpNodeSelect = NULL;
   t_asa *gpNodeFor = NULL;
   t_asa *gpNodeWith = NULL;
   t_asa *gpNodeDefine = NULL;
   t_asa *gpNodeColonnes = NULL;
   t_asa *gpNodeFrom = NULL;
   t_asa *gpNodeWhere = NULL;
   t_asa *gpNodeGroupBy = NULL;
   t_asa *gpNodeBreakOn = NULL;
   t_asa *gpNodeFormat = NULL;
   
   t_asa *gpNodeRowid = NULL;
   t_asa *gpNodeRownum = NULL;
   
   UINT  gNbNodesAlloc = 0;                   /* Nb de nodes allouees */
   UINT  gNbNodesFree = 0;                    /* Nb de nodes liberees */

   UINT giIdNode = 0;                        /* Identifiant du node en cours */
   
   /* Struture de gestion des evenements */
   t_event gEvent = {EVT_INIT};
   
   /*====================================
   * fonctions de gestion des evenements
   * ===================================*/
   
   /* Mise à jour du dernier evenement */
   /*----------------------------------*/
   void dexEventSet(T_GENRE_ASA node, T_TYPE_EVENT event) {
      
      switch(node) {
      case a_requete :
         switch(event) {
         case EVT_INIT :
            {
               int iRang;
               for(iRang = 0; iRang < MAX_GENRE_ASA; iRang++)
                  gEvent.nodeEvent[iRang] = EVT_INIT;
            }
            gEvent.eventList = EVT_INIT;
            break;
         case EVT_START : 
            event = EVT_GROUP_BY; 
         case EVT_NORMAL : 
            dexEventSet(a_select, EVT_INIT); 
            giRowid = 0; giRowidSub = 0;
            giRownum = 1; giRownumAll = 1;
            break;
         default : break;
         }
         break;
         case a_group_by :
            switch(event) {
            case EVT_IN_PROGRESS : 
               {
                  UINT iFn = 0;
                  t_val *pVal = NULL;
                  
                  switch(gEvent.eventList) {
                  case EVT_INIT : 
                  case EVT_SEARCH : 
                  case EVT_END : 
                     gEvent.eventList = EVT_START; break;
                  case EVT_START : 
                     gEvent.eventList = EVT_IN_PROGRESS; break;
                  default : break;
                  }
                  
                  /* Calcul des fonctions groupes */
                  iFn = 0;
                  while(indexNodesFnGroup && iFn < indexNodesFnGroup->nbr) {
                     dexNodeValUnset(indexNodesFnGroup->tab[iFn]);
                     pVal = EXPR((t_asa *)indexNodesFnGroup->tab[iFn], 0);
                     iFn++;
                  }
               }
               break;
            case EVT_FALSE : 
               {
                  UINT iFn = 0;
                  char acNom[kiNOM_PILE + 1];
                  t_val *pVal = NULL;
                  
                  gEvent.eventList = EVT_END;
                  
                  /* Calcul de fin des fonctions groupes */
                  iFn = 0;
                  while(indexNodesFnGroup && iFn < indexNodesFnGroup->nbr) {
                     dexNodeValUnset(indexNodesFnGroup->tab[iFn]);
                     pVal = EXPR((t_asa *)indexNodesFnGroup->tab[iFn], 0);
                     snprintf(acNom, kiNOM_PILE, "GROUP_%d", 
                        ((t_asa *)indexNodesFnGroup->tab[iFn])->id);
                     dexPilePush(acNom, pVal);
                     iFn++;
                  }
                  event = EVT_SEARCH;
                  gEvent.eventList = EVT_INIT;
               }
               break;
            case EVT_END :
               dexEventSet(a_requete, EVT_NORMAL);
               break;
            default : break;
            }
            break;
            case a_select :
               switch(event) {
               case EVT_START : 
                  {
                     UINT iFn = 0;
                     t_val *pVal = NULL;
                     char acNom[kiNOM_PILE + 1];
                     
                     gEvent.eventList = EVT_START;
                     
                     /* Remise à zero des fonctions gap et delta */
                     iFn = 0;
                     while(indexGapDelta && iFn < indexGapDelta->nbr) {
                        dexNodeValUnset(indexGapDelta->tab[iFn]);
                        pVal = EXPR((t_asa *)indexGapDelta->tab[iFn], 0);
                        iFn++;
                     }
                     
                     /* recupere les resultats des fonctions groupes */
                     iFn = 0;
                     while(indexNodesFnGroup && iFn < indexNodesFnGroup->nbr) {
                        snprintf(acNom, kiNOM_PILE, "GROUP_%d", 
                           ((t_asa *)indexNodesFnGroup->tab[iFn])->id);
                        pVal = dexPilePop(acNom, PILE_FIRST);
                        dexNodeValSet(indexNodesFnGroup->tab[iFn], 
                                 pVal->type, pVal->value, pVal->length);
                        pVal = dexValDel(pVal);
                        iFn++;
                     }
                     
                     event = EVT_IN_PROGRESS;
                     gEvent.eventList = EVT_IN_PROGRESS;
                  }
                  break;
               case EVT_IN_PROGRESS : 
                  gEvent.eventList = EVT_IN_PROGRESS;
                  break;
               case EVT_END :
                  gEvent.eventList = EVT_END;
                  event = EVT_INIT;
                  break;
               default : break;
               }
               break;
               default : break;
   }
   
   gEvent.nodeEvent[node] = event;
}

T_TYPE_EVENT dexEventGet(T_GENRE_ASA node) {
   return gEvent.nodeEvent[node];
}

/*====================================
* fonctions diverses
* ===================================*/

/* Remise a 0 de la branche ascendante */
/*-------------------------------------*/
void dexNodeValUnset(t_asa *a) {
/* Si le noeud est 'calcule' alors 
   * il faut reinitialise la branche ascendante */
   if(a && a->calcule) {
      if(a->genre == a_alias || 
         a->genre == a_ref) {
         UINT iRang;
         
         /* unset toutes les references a cet alias ou reference */
         for(iRang = 0;a->pIndex && iRang < a->pIndex->nbr; iRang++)
            dexNodeValUnset(a->pIndex->tab[iRang]);
      }
      dexNodeValUnset(a->parent);
      a->calcule = false;
   }
}

/* stocke la valeur calcule sur le node */
/*--------------------------------------*/
t_val *dexNodeValSet(t_asa *a, int type, void *value, ...) {
   static int bErrTraite = false;  /* Evite d'afficher l'ascendance d'une erreur */
   int length = 0;
   int nbElements = 0;

   va_list args;
   
   /* length et nbElements sont obligatoires dans le cas de tableaux */
   va_start(args, value); 
   length = va_arg(args, int);
   nbElements = va_arg(args, int);
   va_end(args);
   
   if(a == NULL) dexMessageDisplay(209, "dexNodeValSet");
   
   /* Reinitialise la branche ascendante */
   /* Optim a faire : Si seulement la valeur du noeud deja calcule */
   /* est differente alors il faut reinitilalise la branche ascendante */
   
   dexNodeValUnset(a);
   
   a->pVal = dexValSet(a->pVal, type, value, length, nbElements);
   a->calcule = true;
   
   /* Memorise les erreurs en cours */
   if(IS_TYPE(type, TYPE_VAL) && IS_TYPE(((t_val *)value)->type, TYPE_ERR)) {
      /* On affiche que les erreurs non traitees */
      if(!bErrTraite) {
         indexErr = dexIndexAdd(indexErr, (void *)a, NULL);
         bErrTraite = true;
      }
   }
   else bErrTraite = false;
   
   /* Si nouvelle colonne alors on reinitialise le traitement d'erreur */
   if(a->genre == a_col) bErrTraite = false;
   
   return a->pVal;
}

/* fonction de comparaison pour les nodes alias */
/*----------------------------------------------*/
int comparNodeAlias(const void *a, const void *b) {
   t_asa *aa;
   t_asa *bb;
   
   aa = *((t_asa **) a);
   bb = *((t_asa **) b);
   /* Insensitive case */
   return (strcasecmp(aa->nom, bb->nom));
}


/*====================================
* Gestion d'une branche
* ===================================*/

/* Allocation d'un node */
/*======================*/
t_asa *dexNodeNew(T_GENRE_ASA genre, char *nom, ...)
{
   UINT iRang;
   int iNbrBranches;
   va_list args;
   
   static UINT id = 0;      /* Identifiant unique du node */
   static int bForOk = false;
   
   t_asa *desc =NULL;
   t_asa *a = NULL;
   
   if(!nom) nom ="";
   a = (t_asa *) dexObjNew(a, sizeof(t_asa) + strlen(nom), "dexNodeNew");
   if(!a) dexMessageDisplay(310, "dexNodeNew", nom);
   gNbNodesAlloc++;

   a->id = ++id;
   a->genre = genre;
   strcpy(a->nom, nom);

   /* Initialisation des branches passees en parametres */
   va_start(args, nom); 
   
   iRang = 0;
   iNbrBranches = 0;
   while((desc = va_arg(args, t_asa *)) != NULL && iRang < kiMAX_BRANCHES) {
      /* Le type ASA_NULL correspond a un parametre NULL */
      if(desc != ASA_NULL) {
         a->branches[iRang] = desc;
         
         /* Reconnais son fils */
         a->branches[iRang]->parent = a;
         iNbrBranches++;
      }
      
      iRang++;
   }
   va_end(args);
   
   
   /* Memorise les principales branches pour simplification des appels */
   if(genre == a_select) gpNodeSelect = a;
   if(genre == a_for) {gpNodeFor = a; bForOk = true; }
   if(genre == a_with) gpNodeWith = a;
   if(genre == a_liste_colonnes) gpNodeColonnes = a;
   if(genre == a_from) gpNodeFrom = a;
   if(genre == a_where) gpNodeWhere = a;
   if(genre == a_group_by) gpNodeGroupBy = a;
   if(genre == a_break_on) gpNodeBreakOn = a;
   if(genre == a_format) gpNodeFormat = a;
   
   /* Cas particulier de ROWID et ROWNUM qui sont des pseudo Variables */
   if(genre == a_rowid) {
      if(!gpNodeRowid ) {
         /* Creation du node principal ROWID comme une reference */
         gpNodeRowid = a;
         a->genre = genre = a_ref;
      }	
      else {
         /* Indexe la reference */
         gpNodeRowid->pIndex = dexIndexAdd(gpNodeRowid->pIndex, (void *)a, NULL);
      }	     
   }
   
   if(genre == a_rownum) {
      if(!gpNodeRownum ) {
         /* Creation du node principal ROWNUM comme une reference */
         gpNodeRownum = a;
         a->genre = genre = a_ref;
      }	
      else {
         /* Indexe la reference */
         gpNodeRownum->pIndex = dexIndexAdd(gpNodeRownum->pIndex, (void *)a, NULL);
      }	     
   }
   
   /* Constitution d'un index pour les mots clefs */
   if(genre == a_keyword)  {
      /* Control de l'unicite du mot clef */
      if(dexIndexSearch(indexKeyword, (void *)a, comparNodeAlias)) {
         yynerrs++;
         yyerrorDex(158, NOM(a));	  
      }
      else {
         indexKeyword = dexIndexAdd(indexKeyword, (void *)a, comparNodeAlias);
      }
   }
   
   /* Constitution d'un index pour les constantes */
   if(genre == a_const)  {
      t_asa *search = NULL;

      /* Control que la constante n'est pas un mot clef */
      if(dexIndexSearch(indexKeyword, (void *)a, comparNodeAlias)) {
         yynerrs++;
         yyerrorDex(159, NOM(a));	  
      }
      else{
         
         /* Control de l'unicite de la constante */
         search = dexIndexSearch(indexConst, (void *)a, comparNodeAlias);
         if(search) {
            /* Supprime la valeur precedente */
            dexIndexDelKey(indexConst, (void *)a, comparNodeAlias, dexObjDel);
            dexNodeDel(search);
         }
         indexConst = dexIndexAdd(indexConst, (void *)a, comparNodeAlias);
      }
   }
   
   /* Constitution des indexes pour les alias (FOR ou WITH) */
   if(genre == a_alias || genre == a_ref)  {
      /* Control que l'alias n'est pas un mot clef */
      if(dexIndexSearch(indexKeyword, (void *)a, comparNodeAlias)) {
         yynerrs++;
         yyerrorDex(159, NOM(a));	  
      }
      else{
         /* Control de l'unicite de l'alias */
         if(dexIndexSearch(indexAlias, (void *)a, comparNodeAlias)) {
            yynerrs++;
            yyerrorDex(158, NOM(a));	  
         }
         else {
            indexAlias = dexIndexAdd(indexAlias, (void *)a, comparNodeAlias);
         }
         /* Creation d'un index des nodes references par l'alias */
         a->pIndex = dexIndexNew(a->pIndex, "ref Alias", 10, 5);
      }
   }
   
   if(genre == a_alias)  {
      /* Constitution d'un indexe non trie pour les colonnes */
      indexColumn = dexIndexAdd(indexColumn, (void *)a, NULL);

      /* Creation et collecte des informations colonne */
      a->pColumn = dexColumnNew(a);
   }
   
   if(genre == a_fic_alias)  {
      //dexNodeDisplay(a, 0, 1);
   }

   /* Si '*' alors duplique les alias d'entree en colonnes de sortie */
   if(genre == a_col && *nom == '*' && indexColumn && indexColumn->nbr) {
      t_asa *fils = NULL;
      t_asa *pere = NULL;
      /* Creer le premier noeud */
      iRang = 0;
      
      /* Creer une branche pour le select */
      while(iRang < (indexColumn->nbr - 1)) {
         pere = dexNodeNew(a_col, "", dexNodeNew(a_ident, COLUMN(iRang)->pcName, NULL), NULL); 
         if(fils) fils->parent = pere;
         pere->branches[1] = fils;
         fils = pere;
         iRang++;
      }
      /* Rattache le dernier noeud à l'arbre */
      a->branches[0] = dexNodeNew(a_ident, COLUMN(iRang)->pcName, NULL); 
      a->branches[0]->parent = a;
      a->branches[1] = fils;
   }
   
   /* Constitution d'un indexe non trie pour les colonnes en sortie */
   if(genre == a_col) indexSelect = dexIndexAdd(indexSelect, (void *)a, NULL);
   
   /* Constitution d'un indexe non trie pour les formats des colonnes */
   if(genre == a_fmt_bin || genre == a_fmt_ascii) 
      indexFmt = dexIndexAdd(indexFmt, (void *)a, NULL);
   
   /* Constitution d'un index pour les fonctions groupes */
   if(genre == a_fonction)  {
      /* Search function */
      DECL_FN *search = NULL;
      DECL_FN value;
      value.name = a->nom;
      search = (DECL_FN *)dexIndexSearch(indexStdFn, (void *)&value, dexFnCmp);
      if(search) {
         /* Si fonction groupe, l'ajouter dans l'index */
         if(search->groupFn == GROUP_FN) 
            indexNodesFnGroup = dexIndexAdd(indexNodesFnGroup, (void *)a, NULL);
         
         /* Si fonction gap ou delta, l'ajouter dans l'index */
         if(!strcmp(a->nom, "gap") || !strcmp(a->nom, "delta")) 
            indexGapDelta = dexIndexAdd(indexGapDelta, (void *)a, NULL);
      }
   }
   
   /* Controle les Alias et les constantes sauf pour la clause FOR */
   /* + Constitution d'un index de reference pour chaque alias */
   if(bForOk && genre == a_ident) {
      t_asa *search = NULL;
      search = (t_asa *)dexIndexSearch(indexAlias, (void *)a, comparNodeAlias);
      
      if(search) {
         /* Indexe la reference */
         search->pIndex = dexIndexAdd(search->pIndex, (void *)a, NULL);
      }
      else  {
         /* Si non trouve, recherche dans les mots clefs */
         search = (t_asa *)dexIndexSearch(indexKeyword, (void *)a, comparNodeAlias);

         if(!search) {
            /* Si non trouve, recherche dans les constantes */
            search = (t_asa *)dexIndexSearch(indexConst, (void *)a, comparNodeAlias);
            
            /* Si non trouve, alors erreur */
            if(!search) {
               yynerrs++;
               yyerrorDex(161, NOM(a));
            }	
         }
      }
   }
   
   /* Constitution d'un index pour les fichiers */
   if(genre == a_fichier)  {
      static t_file dexFileCurrent = {0};

      /* Collecte des informations */
      dexFileCurrent.pcName = NOM(a);
      dexFileCurrent.pcNameExt = NOM(FILS(a, 1));
      dexFileCurrent.pcNameDesc = NOM(FILS(FILS(a , 2), 1));
      dexFileCurrent.type = dexFileTypeGet(NOM(FILS(a, 2)));

      /* Control de l'unicite de l'alias */
      if(dexIndexSearch(indexFrom, (void *)&dexFileCurrent, dexFileCmp)) {
         yynerrs++;
         yyerrorDex(158, dexFileCurrent.pcName);	  
      }
      else {
         UINT iRang = 0;
         t_file *pFileDex = NULL;
         t_column *pColumn = NULL;

         /* Renseigne la structure d'information sur la table */
         pFileDex = dexFileNew(dexFileCurrent.type , dexFileCurrent.pcName, 
                               dexFileCurrent.pcNameExt, dexFileCurrent.pcNameDesc);
         indexFrom = dexIndexAdd(indexFrom, (void *)pFileDex, dexFileCmp);

         /* Ouverture de la table */
         dexFileOpen(pFileDex);

         /* Associe les colonnes a un fichier */
         for(iRang = 0;iRang < indexColumn->nbr; iRang++) {
            pColumn = COLUMN(iRang);
            if(pColumn->pFile) continue;   /* Fichier deja assigne */

            if(strcasecmp(NOM(PARENT((t_asa *)pColumn->pNode)), pFileDex->pcName) == 0 ||
               strcasecmp(NOM(PARENT((t_asa *)pColumn->pNode)), kFICDEX_DEFAULT) == 0 ) pColumn->pFile = pFileDex;

         }

      }
   }
   
   return a;
}

/* Liberation de toute la descendance */
/*====================================*/
t_asa *dexNodeDel(t_asa *a)
{
   int iRang;
   
   if(!a) return a;

   if(a->genre == 19)
	   a->genre = 19;

   /* Balaye tous les fils */
   for(iRang = 0; iRang < kiMAX_BRANCHES; iRang++) {
      a->branches[iRang] = dexNodeDel(a->branches[iRang]);
   }
   
   /* Supprime l'index si existe (pour les alias) */
   a->pIndex = dexIndexDel(a->pIndex, NULL);
   
   /* Supprime la valeur */
   a->pVal = dexValDel(a->pVal);
   
   /* Supprime les infos colonnes */
   a->pColumn = dexColumnDel(a->pColumn);

   /* Liberation du node */
   a = dexObjDel(a);
   gNbNodesFree++;

   return a;
}


/* Affichage d'une branche */
/*=========================*/
void dexNodeDisplay(t_asa *a, int iNiveau, short bRecurse)
{
   short iRang;
   
   errDex.err = 0;
   
   if (!a) return;
   
   dexLogTrace(1, "%*s|--(%d) %s = '%s' ",iNiveau*3, "", 
      iNiveau, gpcLabelGenre[a->genre], NOM(a));
   
   if(a->calcule) {
      int signe = 0;
      int type = 0;
      char *retpc = NULL;
      
      signe = IS_CLASS(TYPE(a), CLASS_BIN) && IS_FLAG(TYPE(a), FLAG_SIGNED);
      type = TYPE(a) & TYPE_MASK;
      retpc = dexToChar(a->pVal, NULL);
      if(signe)
         dexLogTrace(1, "=> (SIGNED %s) '%s'\n", gpcTypeVal[type], (retpc)? retpc: "");
      else
         dexLogTrace(1, "=> (%s) '%s'\n", gpcTypeVal[type], (retpc)? retpc: "");
   }
   else dexLogTrace(1, "\n");
   
   /* Si bRecurse == false alors on s'arrete */
   if(!bRecurse) return;
   
   /* Balaye tous les fils */
   iNiveau++;
   for(iRang = 0; iRang < kiMAX_BRANCHES; iRang++) {
      dexNodeDisplay(a->branches[iRang], iNiveau, bRecurse);
   }
}

/* Calcul de l'expression à partir du noeud donne */
/* retourne la valeur calculee                    */
/*================================================*/
t_val *dexExprTraiter(t_asa *a)
{
   int res = 0;
   t_val *ret=NULL;
   t_val *retFmt=NULL;
   
   t_val *pRowidStart = NULL;
   t_val *pRowidStop = NULL;
   static UINT rowidStart = 0;
   static UINT rowidStop = 0;
   
   UINT idNodePrec = 0;
   
   static int iRangVar = 0;
   
   errno = 0;
   errDex.err = 0;
   
   if (VIDE(a)) return NULL;
   
   /* Pour optimisation */
   /* Un index est cree dans le noeud alias contenant les noeuds 
   * referant cet alias */
   /* Si valeur deja calculee alors retour avec la valeur du noeud */
   /* sauf pour a_requete qui force le recalcul de certains noeuds */
   /* dexCounterStart(12); */
   if(a->calcule && a->genre != a_requete) return a->pVal;
   /* dexCounterStart(13); */
   
   /* Recupere l'identifiant du node en cours de traitement */
   idNodePrec = giIdNode;
   giIdNode = a->id;
   
   switch (a->genre) {
      
      /* Traitement d'une requete */
   case  a_requete: 
      {
         UINT iRang;
         
         /* Nombre de lignes traitees */
         giRowid++;
         
         /* Force le retraitement de toute la branche d'affichage (colonnes) */
         /* nb : La derniere colonne est la premiere valeur du tableau */
         dexNodeValUnset(indexSelect->tab[0]);

         /* Force le retraitement du select dans le cas ou il n'y a pas eu d'affichage */
         dexNodeValUnset(gpNodeSelect);
         
         /* Force aussi le recalcul des pseudo colonnes ROWID et ROWNUM */
         if(gpNodeRowid) dexNodeValSet(gpNodeRowid, TYPE_INT32, &giRowid);
         if(gpNodeRownum) dexNodeValSet(gpNodeRownum, TYPE_INT32, &giRownum);
         
         /* Recalcul des fonctions gap et delta */
         iRang = 0;
         while(indexGapDelta && iRang < indexGapDelta->nbr) {
            ret = EXPR((t_asa *)indexGapDelta->tab[iRang], 0);
            iRang++;
         }
         
         /* Evalue la requete */
         ret = EXPR(a, 1);
      }
      break;
      
      /* Traitement d'une requete select */
   case  a_select: 
      iRangVar = 0;
      
      /* Evaluation de la Clause BREAK ON */
      /* Si branche Break ON vrai alors arret du traitement */
      ret = EXPR(gpNodeBreakOn, 0);
      if(dexEventGet(a_break_on) == EVT_TRUE) break;
      
      /* Evaluation de la clause GROUP BY si non traitee */
      if(dexEventGet(a_requete) == EVT_GROUP_BY) {
         ret = EXPR(gpNodeGroupBy, 0);
         
         /* Si pas de group by, on passe en mode normal */
         if(dexEventGet(a_group_by) == EVT_INIT) {
            dexEventSet(a_requete, EVT_NORMAL);
            giRowid++;
            if(gpcTrace) dexLogTrace(1, "=>PASS : NORMAL\n");
         }
         else break;
      }
      
      /* Evalue la clause SELECT pour le mode normal */
      
      /* Traitements des sous-ensembles */
      if(dexEventGet(a_select) == EVT_INIT)  {
         giRowidSub = 0;
         
         /* Selection du sous-ensemble */
         dexEventSet(a_select, EVT_SEARCH);
         pRowidStart = dexPilePop("GROUP_BY_ROWID", PILE_FIRST);
         pRowidStop = dexPilePop("HAVING_ROWID", PILE_FIRST);
         
         if(gpNodeGroupBy && !pRowidStart) {
            /* Aucun sous-ensemble trouve */
            dexEventSet(a_break_on, EVT_TRUE);
            break;
         }
         
         rowidStart = ((pRowidStart)? dexValGeti32(pRowidStart, 0) : 1);
         pRowidStart = dexValDel(pRowidStart);
         rowidStop = ((pRowidStop)? dexValGeti32(pRowidStop, 0) : -1);
         pRowidStop = dexValDel(pRowidStop);

         /* Reinitialise ROWNUM */
         if(gpNodeRownum) {
            giRownum = 1;
            dexNodeValSet(gpNodeRownum, TYPE_INT32, &giRownum);
         }
      }
      
      /* recherche du debut du sous-ensembles */
      if(dexEventGet(a_select) == EVT_SEARCH)  {
         if(rowidStart <= giRowid) dexEventSet(a_select, EVT_START);
      }
      
      /* Traitement du sous-ensemble */
      if(dexEventGet(a_select) == EVT_IN_PROGRESS) {
         giRowidSub++;
         
         /* Evalue la clause where */
         ret = EXPR(gpNodeWhere, 0);
         
         /* Si pas de WHERE ou OK, Affichage des colonnes (SELECT) */
         if(dexEventGet(a_where) == EVT_INIT ||
            dexEventGet(a_where) == EVT_TRUE)  {
            /* Evalue les formats de sortie */
            ret = EXPR(gpNodeFormat, 0);
            
            /* Evalue les colonnes */
            ret = EXPR(gpNodeColonnes, 0);   
            
            /* Nouvelle ligne */
            if(gbFmt == kiASCII) {
               int nb = 0;
               nb = fprintf(gpfResult, "\r\n");
               if(nb < 0 && ferror(gpfResult)) {
                  dexMessageDisplay(302, gpcResult);
                  clearerr(gpfResult);
               }
            }
            giRownum++;
            giRownumAll++;
         }
         
         /* Controle de la fin du sous-ensemble */
         /* On repasse en mode SEARCH */
         if(rowidStop <= giRowid) dexEventSet(a_select, EVT_END);
      }
      
      res = true;
      ret = dexNodeValSet(a, TYPE_BOOL, (void *)&res); 
      break;
		    
      /* Traitement de la clause from */
   case  a_from: 
      break;
      
      /* Traitement de la clause where ou break on */
   case  a_where: 
   case  a_break_on: 
      ret = EXPR(a, 1); 
      dexEventSet(a->genre, (dexValGetb(ret, 0)? EVT_TRUE : EVT_FALSE));
      break;
      
      /* Traitement des branches sans sauvegardes du resultat */
   case  a_liste_colonnes: 
      ret = EXPR(a, 1); 
      ret = NULL;
      break;

      /* Traitement des branches */
   case  a_for: 
   case  a_with: 
   case  a_define: 
   case  a_format: 
   case  a_sel_for: 
   case  a_sel_with: 
   case  a_sel_define: 
   case  a_sel_colonnes: 
   case  a_sel_from: 
   case  a_sel_where: 
   case  a_sel_group_by: 
   case  a_sel_break_on: 
   case  a_sel_format: 
      ret = EXPR(a, 1); 
      break;
      
      /* Traitement de la branche GROUP BY */
      /* Condition de demarrage/arret du sous-ensemble */
   case  a_group_by: 
      {
         int bDemarre = false;
         if(dexEventGet(a_group_by) != EVT_IN_PROGRESS) {
            /* Condition de demarrage : group by */
            ret = EXPR(a,1);
            if(!ret || dexValGetb(ret, 0)) {
               dexPilePush("GROUP_BY_ROWID", gpNodeRowid->pVal);
               dexEventSet(a_group_by, EVT_IN_PROGRESS);
               bDemarre = true;
            }
            else dexEventSet(a_group_by, EVT_SEARCH);
         }
         
         if(dexEventGet(a_group_by) == EVT_IN_PROGRESS) {
            /* Condition d'arret : having */
            ret = EXPR(a, 2);
            if(ret && !dexValGetb(ret, 0)) {
               t_val *pVal;
               int iVal;
               
               iVal = dexValGeti32(gpNodeRowid->pVal, 0) - ((bDemarre)? 0 : 1);
               pVal = dexNodeValSet(a,TYPE_INT32, (void *)&iVal);
               dexPilePush("HAVING_ROWID", pVal);
               dexEventSet(a_group_by, EVT_FALSE);
               
               /* Condition de reprise sur cette meme valeur ? */
               if(!bDemarre) {
                  ret = EXPR(a,1);
                  if(!ret || dexValGetb(ret, 0)) {
                     dexPilePush("GROUP_BY_ROWID", gpNodeRowid->pVal);
                     dexEventSet(a_group_by, EVT_IN_PROGRESS);
                  }
                  else dexEventSet(a_group_by, EVT_SEARCH);
               }
               else dexEventSet(a_group_by, EVT_SEARCH);
            }
            else if(!bDemarre) dexEventSet(a_group_by, EVT_IN_PROGRESS);
         }
      }
      break;
      
      /* parcoure les listes */
   case  a_liste_liste1d: 
   case  a_liste_liste2d: 
   case  a_liste_liste3d: 
   case  a_liste_intervalles: 
   case  a_liste_surfaces: 
   case  a_liste_volumes: 
      ret = EXPR(a, 1);    /* traite la liste */
      ret = EXPR(a, 2);    /* Parcoure le reste de la liste */
      break;
      
      /* Evaluation des listes */
   case  a_liste1d: 
   case  a_liste2d: 
   case  a_liste3d: 
      ret = EXPR(a, 1); 
      break;
      
      /* intervalle */
   case  a_intervalle: 
      ret = EXPR(a, 1);    /* traite le 1er ident */
      ret = EXPR(a, 2);    /* traite le 2eme ident */
      break;
      
      /* surface */
   case  a_surface: 
      ret = EXPR(a, 1);    /* traite le 1er intervalle */
      ret = EXPR(a, 2);    /* traite le 2eme intervalle */
      break;
      
      /* volume */
   case  a_volume: 
      ret = EXPR(a, 1);    /* traite le 1er intervalle */
      ret = EXPR(a, 2);    /* traite le 2eme intervalle */
      ret = EXPR(a, 3);    /* traite le 3eme intervalle */
      break;
      
      /* operations conditionnelles */
   case a_not: 
   case a_or : 
   case a_and: 
   case a_inferieur: 
   case a_superieur: 
   case a_egale: 
   case a_different: 
   case a_inferieur_egale: 
   case a_superieur_egale: 
      ret = dexExprCond(a->genre, EXPR(a,1), EXPR(a,2));
      break;
      
      /* operations arithmetiques */
   case  a_plus_unaire: 
   case  a_moins_unaire: 
   case  a_plus: 
   case  a_moins: 
   case  a_multiple: 
   case  a_divise: 
   case  a_puissance: 
   case  a_modulo: 
      ret = dexExprMath(a->genre, EXPR(a,1), EXPR(a,2));
      break;
      
      /* appel fonction */
   case  a_fonction: 
      {
         /* Search function */
         DECL_FN *search = NULL;
         DECL_FN value;
         value.name = a->nom;
         search = (DECL_FN *)dexIndexSearch(indexStdFn, (void *)&value, dexFnCmp);
         if(search == NULL) {
            ret = dexNodeValSet(a, TYPE_ERR, dexMessageDisplay(1022, a->nom));
         }
         else {
            /* Evalue les arguments */
            ret = EXPR(a,1);   
            /* Appel de la fonction */
            ret = dexFnCall(search,FILS(a,1)); 
         }
      }
      break;
      
      /* Liste d'argument d'une fonction */
   case  a_liste_arg: 
      ret = (EXPR(a,2));    /* parcoure le reste de la liste */
      ret = (EXPR(a,1));    /* traite l'argument */
      break;
      
      /* Liste d'alias */
   case  a_liste_alias: 
      ret = (EXPR(a,2));    /* parcoure le reste de la liste */
      ret = (EXPR(a,1));    /* traite l'alias */
      ret = NULL;
      break;
      
      /* Recherche de l'alias (alias east ou expression) */
   case  a_ident: 
      {
         t_asa *search = NULL;
         search = (t_asa *)dexIndexSearch(indexAlias, (void *)a, comparNodeAlias);
         
         /* Si non trouve, recherche dans les mots clefs */
         if(search == NULL) 
            search = (t_asa *)dexIndexSearch(indexKeyword, (void *)a, comparNodeAlias);
         
         /* Si non trouve, recherche dans les constantes */
         if(search == NULL) 
            search = (t_asa *)dexIndexSearch(indexConst, (void *)a, comparNodeAlias);
         
         /* Si non trouve, alors erreur */
         if(search == NULL) 
            ret = dexNodeValSet(a, TYPE_ERR, dexMessageDisplay(1023, NOM(a)));
         else ret = EXPR(search, 0);   /* Evalue l'expression trouvee */
      }
      break;
      
      /* alias east, recuperation de la valeur */
   case  a_alias: 
      ret = a->pVal;
      break;
      
   case  a_fic_alias: 
      break;
      
      /* class et type */
   case  a_flag: 
   case  a_type: 
      break;
      
      /* Liste de ref */
   case  a_liste_ref: 
      ret = (EXPR(a,2));    /* parcoure le reste de la liste */
      ret = (EXPR(a,1));    /* traite la reference */
      ret = NULL;
      break;
      
      /* evaluation d'une reference ou d'une constante */
   case  a_keyword: 
   case  a_ref: 
   case  a_const: 
      ret = EXPR(a, 1); 
      break;
      
      /* liste_fmt_ascii */
   case  a_liste_fmt_ascii: 
      gbFmt = kiASCII;
      ret = EXPR(a, 2);    /* Parcoure le reste de la liste */
      ret = EXPR(a, 1);    /* traite fmt ascii */
      break;
      
      /* liste_fmt_ascii avec separateur */
   case  a_liste_fmt_ascii_sep: 
      gbFmt = kiASCII;
      strncpy(gacSep, NOM(a), kiSEP); /* separateur */
      ret = EXPR(a, 2);    /* Parcoure le reste de la liste */
      ret = EXPR(a, 1);    /* traite fmt ascii */
      break;
      
      /* liste_fmt_bin */
   case  a_liste_fmt_bin: 
      gbFmt = kiBINARY;
      ret = EXPR(a, 2);    /* Parcoure le reste de la liste */
      ret = EXPR(a, 1);    /* traite fmt bin */
      break;
      
      /* fmt_ascii, recuperation du format */
   case  a_fmt_ascii: 
      {
         char *pcFmtC = NULL;

         gbFmt = kiASCII;
         pcFmtC = dexFmtFortranConv(NOM(a));
         ret = dexNodeValSet(a, TYPE_ASCII, pcFmtC); 
      }
      break;
      
      /* fmt_bin, recuperation du format */
   case  a_fmt_bin: 
      {
         t_asa *search = NULL;
         
         gbFmt = kiBINARY;
         if(*(NOM(a)) != '\0') {
            search = (t_asa *)dexIndexSearch(indexKeyword, (void *)a, comparNodeAlias);
            
            /* Si non trouve, alors erreur */
            if(search == NULL) 
               ret = dexNodeValSet(a, TYPE_ERR, dexMessageDisplay(1005, NOM(a)));
            else ret = EXPR(search, 0);   /* Evalue l'expression trouvee */
         }
      }
      break;
      
      /* affichage des colonnes */
   case  a_col: 
      ret = (EXPR(a,2));    /* parcoure le reste de la liste */
      
      if(gbFmt == kiASCII) {
         /* sortie ASCII */
         char *retpc = NULL;
         
         /* Affichage du separateur */
         if(ret) {
            int nb = 0;
            nb = fprintf(gpfResult, "%s", gacSep);
            if(nb < 0 && ferror(gpfResult)) {
               dexMessageDisplay(302, gpcResult);
               clearerr(gpfResult);
            }
         }

         /* Evalue la colonne */
         ret = EXPR(a, 1);

         /* Recherche du format de sortie */
         retFmt = EXPR((t_asa *)indexFmt->tab[iRangVar], 0);

         /* Recupere le resultat formatte */
         if(retFmt && IS_TYPE(retFmt->type, TYPE_NULL)) retFmt = NULL;
         retpc = dexToChar(ret, (char *)((retFmt)? dexValGet(retFmt, 0) : NULL)); //dexToChar(retFmt, NULL) : NULL));

         dexCounterStart(9);

         if(errDex.err) {
            int nb = 0;
            nb = fprintf(gpfResult, "(ERR: %s)", (errDex.msg)? errDex.msg : "");
            if(nb < 0 && ferror(gpfResult)) {
               dexMessageDisplay(302, gpcResult);
               clearerr(gpfResult);
            }
         }
         else if(retpc) {
            int nb = 0;
            nb = fprintf(gpfResult, "%s", retpc);
            if(nb < 0 && ferror(gpfResult)) {
               dexMessageDisplay(302, gpcResult);
               clearerr(gpfResult);
            }
         }
      }
      else {
#ifdef _MSC_VER
         /* Sous windows la redirection d'un flux binaire vers un fichier (>) */
         /* provoque la substitution de la séquence 0A par 0D0A */
         /* L'option -o devient alors obligatoire pour eviter ce probleme */
         if(gbFmt == kiBINARY && !gpcResult) dexMessageDisplay(241);
#endif
         
         /* sortie Binaire */

         /* Evalue la colonne */
         ret = EXPR(a, 1);

         /* Recherche du format de sortie */
         retFmt = EXPR((t_asa *)indexFmt->tab[iRangVar], 0);

         /* Cas particulier des formats de sortie si non precises */
         if(retFmt == NULL) {
            /* Format date SDF (Simplified Data Format) par defaut */
            if(IS_TYPE(ret->type, TYPE_DATE_DEX)) {
               ret = dexToBin(ret, TYPE_DATE);
            }
         }

         /* Format precise */
         else ret = dexToBin(ret, dexValGeti32(retFmt, 0));

#ifdef _BIG_ENDIAN
         if(gbEndian == kiLITTLE_ENDIAN) {
            /* Ordonne les octets selon LSB : Less Significant Byte first (PC) */
            dexValSwap(ret);
         }
#else
         if(gbEndian == kiBIG_ENDIAN) {
            /* Ordonne les octets selon MSB : Most Significant Byte first (SUN) */
            dexValSwap(ret);
         }

#endif
         dexCounterStart(9);

         if(!errDex.err) {
            int nb = 0;
            nb = fwrite(ret->value, ret->length, 1, gpfResult);
            if(!nb && ferror(gpfResult)) {
               dexMessageDisplay(302, gpcResult);
               clearerr(gpfResult);
            }
         }
      }

      dexCounterStop(9);

      iRangVar++;
      break;
      
      /* retourne la valeur entiere du nombre ascii */
   case  a_integer: 
      {
         int res = 0;
         if((res = atoi(NOM(a))) == 0 && errno) 
            dexMessageDisplay(311, "dexExprTraiter", "atoi");
         ret = dexNodeValSet(a, TYPE_INT32, &res);
      }
      break;
      
      /* retourne la valeur double du nombre ascii */
   case  a_double: 
      {
         double res = 0.0;
         if((res = atof(NOM(a))) == 0 && errno) 
            dexMessageDisplay(311, "dexExprTraiter", "atof");
         ret = dexNodeValSet(a, TYPE_REAL64, &res);
      }
      break;
      
      /* retourne la valeur binaire du nombre hexa */
   case  a_hexa: 
      ret = dexNodeValSet(a, TYPE_HEXA, NOM(a));
      break;
      
      /* retourne la valeur booleenne */
   case  a_bool: 
      {
         int res = 0;
         res = (!strcasecmp(NOM(a), "true"))? true : false;
         ret = dexNodeValSet(a, TYPE_BOOL, &res);
      }
      break;
      
      /* Retourne une chaine */      
   case  a_string: 
      ret = dexNodeValSet(a, TYPE_ASCII, NOM(a));
      break;
      
      /* Retourne une date */      
   case  a_date_iso_a: 
      ret = dexNodeValSet(a, TYPE_DATE_DEX, dexDateConv((void *)NOM(a), TYPE_DATE_ISO_A));
      break;
      
   case  a_date_iso_b: 
      ret = dexNodeValSet(a, TYPE_DATE_DEX, dexDateConv((void *)NOM(a), TYPE_DATE_ISO_B));
      break;

      /* retourne le numero de ligne traitee */
   case  a_rowid: 
      ret = gpNodeRowid->pVal;
      break;
      
      /* retourne le numero de ligne du resultat */
   case  a_rownum: 
      ret = gpNodeRownum->pVal;
      break;
      
   default:
      dexMessageDisplay(202, "dexExprTraiter", a->genre);
      break;
   }
   
   /* sauvegarde de l'expression evaluee */
   if (ret) ret = dexNodeValSet(a, TYPE_VAL, ret, ret->length);
   
   if (errno) dexMessageDisplay(313, "dexExprTraiter", gpcLabelGenre[a->genre]);
   
   /* restitue l'identifiant du node parent */
   giIdNode = idNodePrec;
   
   return ret; 
}

int dexEastStatusCheck(int status)
{
   if (status != 1) {
      /* -12 : Access data error */
      if(status == -12) return status;
      dexMessageDisplay(240, status);
   }
   return 0;
}

void dexRequeteExecuter(t_asa *a)
{
   UINT iRang;
   int resultat = 0;
   int nbElements = 0;
   
   t_file *pFileDex = NULL;
   t_column *pColumn = NULL;

   errDex.err = 0;
   
   if (a == NULL) {
      dexLogTrace(1, "*NULL* \n");
      return;
   }

   /* Initialise le gestionnaire d'evenement */
   dexEventSet(a_requete, EVT_INIT);
   dexEventSet(a_requete, EVT_START);
   
   /* Initialise les pseudo-variables si necessaire */
   if(!gpNodeRowid) dexNodeNew(a_rowid, "ROWID", NULL);
   if(!gpNodeRownum) dexNodeNew(a_rownum, "ROWNUM", NULL);
   
   /* Travaille sur 1 fichier pour l'instant */
   pFileDex = dexFileGet((COLUMN(0))->pFile->pcName);

   while(dexEventGet(a_requete) != EVT_END){
      /* Boucle de lecture sur le fichier de donnees */
      if(gpcDebug) {
         dexLogTrace(1, "=>PASS : %s\n",
            (dexEventGet(a_requete) == EVT_GROUP_BY)? "GROUP BY" : "NORMAL");
      }

      /* Lecture du premier record */
      resultat = dexFileRestart(pFileDex);
      
      while(resultat == 1) {  
         /* Initialisation de toutes les colonnes */
         for (iRang=0; iRang < indexColumn->nbr; iRang++) {

            pColumn = COLUMN(iRang); /* Recupere les proprietes de la colonne */

            /* Optim : Ne traite que les colonnes qui sont utilisees */
            if(((t_asa *)pColumn->pNode)->pIndex->nbr == 0) continue;
            
            /* recupere le flag si defini */
            if(!strcasecmp(pColumn->pcFlag, "ARRAY") && 
               (pColumn->typeDex != TYPE_OBJECT && pColumn->typeDex != TYPE_ASCII)) {
               pColumn->typeDex |= FLAG_ARRAY;
            }

            /* Lecture de la valeur */
            nbElements = 1;
            resultat = dexFileColumnGet(pColumn);

            if(resultat == 1) {
               
      /* Calcul du nombre d'elements pour les tableaux */
      switch(pColumn->typeDex & TYPE_MASK) {
         case TYPE_INT8 :
            if(pColumn->length != 1) {
               nbElements = pColumn->length;pColumn->typeDex |= FLAG_ARRAY;
	    }
            break;
         case TYPE_INT16 :
            if(pColumn->length != 2) {
               nbElements = pColumn->length / 2;pColumn->typeDex |= FLAG_ARRAY;
	    }
            break;
         case TYPE_INT32 :
            if(pColumn->length != 4) {
               nbElements = pColumn->length / 4;pColumn->typeDex |= FLAG_ARRAY;
	    }
            break;
         case TYPE_INT64 :
            if(pColumn->length != 8) {
               nbElements = pColumn->length / 8;pColumn->typeDex |= FLAG_ARRAY;
	    }
            break;
         case TYPE_REAL32 :
            if(pColumn->length != 4) {
               nbElements = pColumn->length / 4;pColumn->typeDex |= FLAG_ARRAY;
	    }
            break;
         case TYPE_REAL64 :
            if(pColumn->length != 8) {
               nbElements = pColumn->length / 8;pColumn->typeDex |= FLAG_ARRAY;
	    }
            break;
         case TYPE_DATE :
            if(pColumn->length != sizeof(INT64 )) {
               nbElements = pColumn->length / sizeof(INT64 );pColumn->typeDex |= FLAG_ARRAY;
	    }
            break;
         case TYPE_DATE_MICRO :
         case TYPE_DATE_NANO :
         case TYPE_DATE_PICO :
            if(pColumn->length != kiT_DATE_SDF) {
               nbElements = pColumn->length / kiT_DATE_SDF;pColumn->typeDex |= FLAG_ARRAY;
	    }
            break;
         default : break;
         }
      
               /* Ecrit la valeur dans le noeud east (alias) correspondant */
               dexCounterStart(5);
               dexNodeValSet(pColumn->pNode, pColumn->typeDex, pColumn->pBuffer, pColumn->length, nbElements);
               dexCounterStop(5);

	       /* On libere le buffer si necessaire */
               if(pColumn->pFile->type == TYPE_FILE_EAST) {
                  free(pColumn->pBuffer);
                  pColumn->pBuffer = NULL;
	       }
            }
            
            if(resultat == -12){
               /* Erreur d'acces aux donnees */
               /* Ecrit la valeur NULL dans le noeud east correspondant */
               dexCounterStart(5);
               dexNodeValSet(pColumn->pNode, TYPE_NULL, NULL);
               dexCounterStop(5);
               resultat = 0;
            }
         }
         
         /* Evaluation de la requete */
         dexCounterStart(10);
         EXPR(a, 0);
         dexCounterStop(10);
         
         /* Affichage des erreurs eventuelles */
         if(indexErr->nbr) {
            if(gpcDebug) {
               dexMessageDisplay(120);
               for(iRang = 0;indexErr && iRang < indexErr->nbr; iRang++) {
                  dexLogTrace(1, "\n");
                  dexNodeDisplay(indexErr->tab[iRang], 0, 1); 
               }
            }
            /* Sortie du programme en erreur */
            dexProgExit(1);
         }
         /* Si clause BREAK ON vrai, alors arret du traitement */
         if(dexEventGet(a_break_on) == EVT_TRUE) resultat = 0;
         else resultat = dexFileRecordNext(pFileDex);
      }
      
      /* Traitement termine ? */
      if(dexEventGet(a_requete) == EVT_NORMAL) dexEventSet(a_requete, EVT_END);
      
      /* push de la derniere valeur rowid */
      if(dexEventGet(a_requete) == EVT_GROUP_BY) {
         if(dexEventGet(a_group_by) == EVT_IN_PROGRESS) {
            dexPilePush("HAVING_ROWID", gpNodeRowid->pVal);
            dexEventSet(a_group_by, EVT_FALSE);
         }
         dexEventSet(a_group_by, EVT_END);
      }
      
      /* Affichage des intervals GROUP BY */
      if(gpcTrace)  {
         char acNom[kiNOM_PILE + 1];
         t_val *pRowidStart = NULL;
         t_val *pRowidStop = NULL;
         t_val *pVal = NULL;
         int iRang = PILE_FIRST;
         UINT iFn = 0;
         
         pRowidStart = dexPileRead("GROUP_BY_ROWID", iRang);
         pRowidStop = dexPileRead("HAVING_ROWID", iRang);
         while(pRowidStart && pRowidStop) {
            dexLogTrace(1, "=>rowid start : %d, rowid stop : %d ",
               dexValGeti32(pRowidStart, 0), dexValGeti32(pRowidStop, 0));
            
            /* Affichage des resultats des fonctions groupes */
            iFn = 0;
            while(indexNodesFnGroup && iFn < indexNodesFnGroup->nbr) {
               
               snprintf(acNom, kiNOM_PILE, "GROUP_%d", 
                  ((t_asa *)indexNodesFnGroup->tab[iFn])->id);
               pVal = dexPileRead(acNom, iRang);
               dexLogTrace(1, ", %s", dexToChar(pVal, NULL));
               iFn++;
            }
            
            dexLogTrace(1, "\n");
            iRang++;
            pRowidStart = dexPileRead("GROUP_BY_ROWID", iRang);
            pRowidStop = dexPileRead("HAVING_ROWID", iRang);
         }
      }
  } 
  
}

