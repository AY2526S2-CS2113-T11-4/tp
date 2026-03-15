package dextro.model;

public class Module {

    private String code;
    private Grade grade;

    public Module(String code, Grade grade) {
        this.code = code;
        this.grade = grade;
    }

    public String getCode() {
        return code;
    }

    public Grade getGrade() {
        return grade;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setGrade(Grade grade) {
        this.grade = grade;
    }

    @Override
    public String toString() {
        return code + "/" + grade;
    }
}