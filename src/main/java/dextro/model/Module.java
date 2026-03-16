package dextro.model;

public class Module {

    private String code;
    private Grade grade;

    public Module(String code, Grade grade) {
        this.code = code;
        this.grade = grade;
    }

    @Override
    public String toString() {
        return code + "/" + grade;
    }
}
