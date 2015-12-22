/**
 * 
 */
package org.najo;

import org.najo.Nodes.INode;
import org.najo.Nodes.IndexNode;
import org.najo.Nodes.ListNodes;
import org.najo.Nodes.Node;
import org.najo.Nodes.NodeAlias;
import org.najo.values.Value;
import org.najo.values.ValueFmtAscii;
import org.najo.values.ValueNull;

import enums.TypeNode;

/**
 * Request.java.
 * <p>
 * 
 * @author Vincent Cephirins
 * @version 1.0, 1 nov. 2012 <li>Creation</>
 */

public class Request {
    private String literal = "";
    private Node racine = INode.NODE_NULL;

    // Raccourcis
    private ListNodes listOutput = null; // Liste des columns
    private ListNodes listFormat = null; // Liste des formats pour les columns
    private IndexNode indAlias = null;   // index des alias pour les références
    private boolean binFmt = false;      // Type du format binaire ou ascii (defaut)
    private String separatedBy = ",";    // Separateur par défaut pour le format ascii
    
    private boolean compiled = false;

    /**
     * Constructeur.
     * <p>
     * 
     * @param literal text of request.
     * @param racine first nde of request.
     * @throws NajoException 
     */
    public Request(String literal, Node racine) throws NajoException {
        if (literal != null) this.literal = literal;
        this.racine = racine;
        
        compiled = compile();
    }

    private boolean compile() throws NajoException {
        Node find;

        switch (racine.getType()) {
        case PRINT:
        case REQUEST:
            // Memorise le premier node des colonnes
            find = findNode(TypeNode.LIST_COLUMN);
            if (find != null) listOutput = find.getChildren();

            // Memorise le premier node du format
            find = findNode(TypeNode.FORMAT);
            if (find != null) {
                // Il existe un format explicite
                find = find.getChild();
                if (find.getType() == TypeNode.LIST_FMT_BIN) {
                    binFmt = true;
                    listFormat = find.getChildren();
                }
                else {
                    binFmt = false;
                    if (find.getType() == TypeNode.LIST_FMT_ASCII_SEP) {
                        // Memorise le format separateur
                        separatedBy = find.getValue().toString();
                        find = find.getChild();
                    }
                    listFormat = find.getChildren();
                }
            }
            break;

        default:
            break;
        }
        
        // Création d'un index sur les Alias
        IndexNode indAlias = new IndexNode("Alias", findAllNodes(TypeNode.ALIAS));
        
        // Création d'une liste des références
        ListNodes listIdent = findAllNodes(TypeNode.IDENT);
        
        // Optimisation des références internes à la requête
        for (Node ident : listIdent) {
            // Recherche de la référence
            Node search = indAlias.search(ident.getName());
            if (search != null) {
                // Pointe le node de référence
                ident.setRef(search.getChild());
                
                // Ajoute l'ident dans la liste des référents
                ((NodeAlias) search).addIdent(ident);
            }
        }
    
        return true;
    }
    
    /**
     * Affichage formattée des résultats de la requete.<p>
     * @throws NajoException 
     */
    public void output() throws NajoException {
        if (compiled == false) compile();
        if (listOutput != null) {
            StringBuffer buffer = new StringBuffer(listOutput.size() * 20);
            int indNode = 0;
            ValueNull valueNull = new ValueNull();
            for (Node node : listOutput) {
                if (indNode > 0) buffer.append(separatedBy);
                Value fmt = valueNull;
                if (indNode < listFormat.size()) fmt = listFormat.get(indNode).getValue();
                if (fmt instanceof ValueNull) buffer.append(node.printValue());
                else buffer.append(node.printValue(((ValueFmtAscii) fmt).getValConv()));
                indNode++;
            }
            System.out.println(buffer.toString());
        }
    }
     
    /**
     * Returns first child for the specified type.
     * @param pos type of Node
     * @return Returns child or null.
     */
    public Node findNode(TypeNode type) {
        return findNode(racine, type);
    }

    /**
     * Returns first child or himself for the specified type from node.
     * @param type type of Node
     * @return Returns child or null.
     */
    public Node findNode(Node node, TypeNode type) {
        Node find = null;
        
        // Trouvé,  sortie
        if (node.getType().equals(type)) return node;
        
        // Regarde en premier le fils direct
        if (node.getChild() != null) {
            find = findNode(node.getChild(), type);
            if (find != null) return find;
        }
        
        // Regarde ensuite les fils
        if (node.getChildren() != null) {
            for (Node child : node.getChildren()) {
                find = findNode(child, type);
                
                // Si trouvé, sortie de la boucle de recherche
                if (find != null) return find;
            }
        }
        return null;
    }

    /**
     * Returns all childs for the specified type.
     * @return Returns list of child or null.
     */
    public ListNodes findAllNodes(TypeNode type) {
        ListNodes listNodes = new ListNodes(type.name());
        return findAllNodes(listNodes, racine, type);
    }
    
    /**
     * Returns all childs for the specified type from node.
     * @return Returns list of child or null.
     */
    public ListNodes findAllNodes(Node node, TypeNode type) {
        ListNodes listNodes = new ListNodes(type.name());
        return findAllNodes(listNodes, node, type);
    }
    
    private ListNodes findAllNodes(ListNodes listNodes, Node node, TypeNode type) {
        // Trouvé,  Mémorise le node
        if (node.getType().equals(type)) listNodes.add(node);
        
        // Parcours le reste de l'arbre
        // Regarde en premier le fils direct
        if (node.getChild() != null) findAllNodes(listNodes, node.getChild(), type);
        
        // Regarde ensuite les freres
        if (node.getChildren() != null) {
            for (Node child : node.getChildren()) {
                findAllNodes(listNodes, child, type);                
            }
        }
        
        return listNodes;
    }

    /**
     * @return Returns the literal.
     */
    public String getLiteral() {
        return literal;
    }

    /**
     * @return Returns the racine.
     */
    public Node getRacine() {
        return racine;
    }

    /**
     * @return Returns the listOutput.
     */
    public ListNodes getListOutput() {
        return listOutput;
    }

    /**
     * @return Returns the listOutput.
     */
    public ListNodes getListFormat() {
        return listFormat;
    }

    /**
     * @return Returns the index to Alias.
     */
    public IndexNode getIndAlias() {
        return indAlias;
    }

    /**
     * @return Returns the binFmt.
     */
    public boolean isBinFmt() {
        return binFmt;
    }

    /**
     * @return Returns the separatedBy.
     */
    public String getSeparatedBy() {
        return separatedBy;
    }
}
