package org.example;

class Structure {
    private String label;
    private Node dest;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Node getDest() {
        return dest;
    }

    public Structure(String label, Node dest) {
        this.label = label;
        this.dest = dest;
    }

}
