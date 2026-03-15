package dextro.model;

public enum Grade {
    A_PLUS("A+", 5.0),
    A("A", 5.0),
    A_MINUS("A-", 4.5),
    B_PLUS("B+", 4.0),
    B("B", 3.5),
    B_MINUS("B-", 3.0),
    C("C", 2.5),
    F("F", 0.0);

    private final String label;
    private final double cap;

    Grade(String label, double cap) {
        this.label = label;
        this.cap = cap;
    }

    public double getCap() {
        return cap;
    }

    public String getLabel() {
        return label;
    }

    public static Grade fromString(String input) {
        for (Grade g : Grade.values()) {
            if (g.label.equals(input)) {
                return g;
            }
        }
        throw new IllegalArgumentException("Invalid grade: " + input);
    }

    @Override
    public String toString() {
        return label;
    }
}
