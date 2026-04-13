package dextro.parser;

public class Normalizer {
    public static String normalizeName(String name) {
        return name == null ? null : name.strip().replaceAll("\\s+", " ");
    }

    public static String normalizePhone(String phone) {
        return phone == null ? null : phone.strip().replaceAll("\\s+", "");
    }

    public static String normalizeEmail(String email) {
        return email == null ? null : email.strip().toLowerCase();
    }

    public static String normalizeAddress(String address) {
        return address == null ? null : address.strip().replaceAll("\\s+", " ");
    }

    public static String normalizeCourse(String course) {
        return course == null ? null : course.strip().replaceAll("\\s+", " ");
    }

    public static String normalizeModuleCode(String code) {
        return code == null ? null : code.strip().toUpperCase();
    }

    public static String normalizeGrade(String grade) {
        return grade == null ? null : grade.strip().toUpperCase();
    }
}
