/**
 * 
 */
package org.najo.Nodes;

import org.najo.exceptions.NajoException;
import org.najo.values.Value;
import org.najo.values.ValueFmtAscii;
import org.najo.values.ValueString;

import enums.TypeNode;

/**
 * Node.java.
 * <p>
 * 
 * @author Vincent Cephirins
 * @version 1.0, 15 nov. 2010 <li>Creation</>
 */

public class Node implements INode, Comparable<Node> {
    static long seqId = 1; // S�quence pour la classe
    long id; // identifiant interne du node
    TypeNode type; // type du node
    Value value = Value.VALUE_NULL;; // la valeur du node par defaut
    Node child = null;
    ListNodes children = null; // noeuds children
    Node parent = null; // pointeur sur le noeud parent
    boolean calculated = false; // Indique que la valeur est calculee
    String name = ""; // name du noeud

    /**
     * Constructeur pour un node terminal (Value).
     * <p>
     * 
     * @param type
     * @param val
     * @throws NajoException
     */
    public Node(TypeNode type, String val) {
        id = seqId++;

        this.type = type;
        this.name = (val == null)? "" : val;
        this.calculated = false;

        try {
            switch (type) {
            case LIST_FMT_ASCII_SEP:
            case FMT_BIN:
                this.value = new ValueString(val);
                calculated = true;
                break;
            case FMT_ASCII:
                this.value = new ValueFmtAscii(val);
                calculated = true;
                break;
            default:
                calculated = false;
                // throw new NajoException("exception.nodeType.notFound", type);
            }
        }
        catch (NajoException ne) {
            ne.printMessages();
        }
    }

    /**
     * Constructeur pour un node simple.
     * <p>
     * 
     * @param type
     * @param val
     * @throws NajoException
     */
    public Node(TypeNode type, String val, Node child) {
        this(type, val);
        this.child = ((Node) child);
        this.child.setParent(this);
    }

    /**
     * Constructeur pour un node liste (ex: function)
     * <p>
     * 
     * @param type
     * @param val
     * @throws NajoException
     */
    public Node(TypeNode type, ListNodes children) {
        id = seqId++;

        this.type = type;
        this.name = type.name();
        this.calculated = false;

        this.children = children;
        for (Node node : this.children) {
            node.setParent(this);
        }
    }

    /**
     * Constructeur.
     * <p>
     * 
     * @param type
     * @param name
     * @throws NajoException
     */
    public Node(TypeNode type, String name, final Node child1, final Node child2) {
        this(type, new ListNodes(name, 2) {
            private static final long serialVersionUID = 1L;
            {
                add(child1);
                add(child2);
            }
        });
    }

    /*
     * (non-Javadoc)
     * @see org.najo.INode#setParent(org.najo.INode)
     */
    @Override
    public void setParent(Node parent) {
        this.parent = parent;
    }

    /*
     * (non-Javadoc)
     * @see org.najo.INode#resetCalculated()
     */
    @Override
    public void resetCalculated() {
        // Force le calcul des valeurs dépendantes (récursif)
        if (calculated) {
            calculated = false;
            if (parent != null) parent.resetCalculated();
        }
    }

    /*
     * (non-Javadoc)
     * @see org.najo.INode#setValue(java.lang.String)
     */
    @Override
    public void setValue(String val) throws NajoException {
        value.setValue(val);
    }

    /*
     * (non-Javadoc)
     * @see org.najo.INode#setValue(org.najo.values.IValue)
     */
    @Override
    public void setValue(Value val) throws NajoException {
        value.setValue(val);
    }

    /*
     * (non-Javadoc)
     * @see org.najo.INode#getValue()
     */
    @Override
    public Value getValue() throws NajoException {
        if (calculated) return value;

        value = expr();
        calculated = true;
        return value;
    }

    /*
     * (non-Javadoc)
     * @see org.najo.INode#getId()
     */
    @Override
    public long getId() {
        return id;
    }

    /*
     * (non-Javadoc)
     * @see org.najo.INode#getType()
     */
    @Override
    public TypeNode getType() {
        return type;
    }

    /*
     * (non-Javadoc)
     * @see org.najo.INode#getName()
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * @param name The name to set.
     */
    protected void setName(String name) {
        this.name = name;
    }

    /*
     * (non-Javadoc)
     * @see org.najo.INode#getChildren()
     */
    @Override
    public ListNodes getChildren() {
        return children;
    }

    /*
     * (non-Javadoc)
     * @see org.najo.INode#getChildren(int)
     */
    @Override
    public Node getChildren(int pos) {
        return children.get(pos);
    }

    /*
     * (non-Javadoc)
     * @see org.najo.INode#getChild()
     */
    @Override
    public Node getChild() {
        return child;
    }

    /**
     * Créer le lien entre un alias et sa référence.<p>
     * @param ref l'expression référencée par l'alias
     */
    public void setRef(Node ref) {
        if (type == TypeNode.IDENT) child = ref;
    }
    
    /**
     * Affiche l'arbre sur la sortie standard.
     * <p>
     * 
     * @param level niveau d'indentation à l'affichage
     */
    /*
     * (non-Javadoc)
     * @see org.najo.INode#display(int)
     */
    @Override
    public void display(int level) {
        System.out.println(String.format("%" + level * 3 + "s%s", "", this.toString()));
        if (child != null) child.display(level + 1);
        if (children != null) {
            for (Node child : children) {
                child.display(level + 1);
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see org.najo.INode#printValue()
     */
    @Override
    public String printValue() {
        try {
            value = getValue();
            return value.toString();
        }
        catch (NajoException e) {
            return e.getMessage();
        }
    }

    /*
     * (non-Javadoc)
     * @see org.najo.INode#printValue(String)
     */
    @Override
    public String printValue(String format) {
        try {
            value = getValue();
            return value.toString(format);
        }
        catch (NajoException e) {
            return e.getMessage();
        }
    }

    /*
     * (non-Javadoc)
     * @see org.najo.INode#expr()
     */
    @Override
    public Value expr() throws NajoException {
        switch (type) {
        case QUIT:
            value = Value.VALUE_NULL;
            break;
        default:
            if (child != null) value = child.getValue();
        }

        return value;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(Node other) {
        return name.compareTo(other.getName());
    }

    
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        return name.equals(((Node) obj).getName());
    }

    /**
     * Retourne les informations du node.
     */
    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        if (type == TypeNode.NULL) return String.format("[%s]", type.toString());
        else return String.format("[%s] = %s", type.toString(), name);
    }

}
