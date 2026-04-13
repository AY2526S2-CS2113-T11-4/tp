package dextro.model.record;

import dextro.model.Student;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StudentDatabase {
    private final List<Student> studentList;

    public StudentDatabase() {
        studentList = new ArrayList<>();
    }

    public StudentDatabase(List<Student> studentList) {
        this.studentList = studentList;
    }

    public void addStudent(Student student) {
        studentList.add(student);
    }

    public void insertStudent(int index, Student student) {
        studentList.add(index, student);
    }

    public Student getStudent(int index) {
        return studentList.get(index);
    }

    public Student removeStudent(int index) {
        return studentList.remove(index);
    }

    public int getStudentCount() {
        return studentList.size();
    }

    public List<Student> getAllStudents() {
        return studentList;
    }

    public void updateStudent(int index, Student updatedStudent) {
        studentList.set(index, updatedStudent);
    }

    public Map<Student, List<String>> findDuplicateFields(String name, String phone,
                                                          String email, String address) {
        Map<Student, List<String>> conflicts = new LinkedHashMap<>();
        for (Student s : studentList) {
            List<String> matched = new ArrayList<>();
            if (s.getName().equalsIgnoreCase(name)) {
                matched.add("name");
            }
            if (phone != null && !phone.isBlank() && !phone.equals("N.A.")
                    && s.getPhone().equalsIgnoreCase(phone)) {
                matched.add("phone");
            }
            if (email != null && !email.isBlank() && !email.equals("N.A.")
                    && s.getEmail().equalsIgnoreCase(email)) {
                matched.add("email");
            }
            if (address != null && !address.isBlank() && !address.equals("N.A.")
                    && s.getAddress().equalsIgnoreCase(address)) {
                matched.add("address");
            }
            if (!matched.isEmpty()) {
                conflicts.put(s, matched);
            }
        }
        return conflicts;
    }
}
