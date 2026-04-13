package dextro.model;

import java.util.ArrayList;
import java.util.List;

public class Student {

    private final String name;
    private final String phone;
    private final String email;
    private final String address;
    private final String course;

    private final List<Module> modules;

    private Student(Builder builder) {
        this.name = builder.name;
        this.phone = builder.phone == null ? "" : builder.phone;
        this.email = builder.email == null ? "" : builder.email;
        this.address = builder.address == null ? "" : builder.address;
        this.course = builder.course == null ? "" : builder.course;
        this.modules = new ArrayList<>();
    }

    // Getters return "N.A." if empty
    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone.isEmpty() ? "N.A." : phone;
    }

    public String getEmail() {
        return email.isEmpty() ? "N.A." : email;
    }

    public String getAddress() {
        return address.isEmpty() ? "N.A." : address;
    }

    public String getCourse() {
        return course.isEmpty() ? "N.A." : course;
    }

    @Override
    public String toString() {
        return name + "/" +
                getPhone() + "/" +
                getEmail() + "/" +
                getAddress() + "/" +
                getCourse();
    }

    public void addModule(Module module) {
        for (Module m : modules) {
            if (m.getCode().equals(module.getCode())) {
                throw new IllegalArgumentException(
                        "Module " + module.getCode() + " already exists for this student."
                );
            }
        }
        modules.add(module);
    }

    public boolean removeModule(String moduleCode) {
        return modules.removeIf(m -> m.getCode().equalsIgnoreCase(moduleCode));
    }

    public List<Module> getModules() {
        return modules;
    }

    public double calculateCap() {
        assert modules != null : "Modules list should not be null";

        double totalPoints = 0.0;
        int totalCredits = 0;

        for (Module module : modules) {
            assert module != null : "Module should not be null";
            Grade grade = module.getGrade();
            assert grade != null : "Grade should not be null";

            if (grade.getCountsToGpa()) {
                int credits = module.getCredits();
                assert credits >= 0 : "Credits should not be negative";
                totalPoints += grade.getCap() * credits;
                totalCredits += credits;
            }
        }

        if (totalCredits == 0) {
            return 0.0;
        }

        double cap = totalPoints / totalCredits;
        assert cap >= 0.0 && cap <= 5.0 : "Calculated CAP should be between 0.0 and 5.0";
        return cap;
    }

    public int getTotalMCs() {
        assert modules != null : "Modules list should not be null";

        int total = 0;

        for (Module module : modules) {
            assert module != null : "Module should not be null";
            assert module.getGrade() != null : "Module grade should not be null";

            if (module.getGrade().getCountsToCompletion()) {
                int credits = module.getCredits();
                assert credits >= 0 : "Credits should not be negative";
                total += credits;
            }
        }

        assert total >= 0 : "Total MCs should not be negative";
        return total;
    }

    public String getProgressStatus() {
        int totalMCs = getTotalMCs();
        assert totalMCs >= 0 : "Total MCs should not be negative";

        String status;
        if (totalMCs >= 160) {
            status = "Completed";
        } else if (totalMCs >= 120) {
            status = "Good Progress";
        } else if (totalMCs >= 80) {
            status = "Satisfactory";
        } else if (totalMCs >= 40) {
            status = "On Track";
        } else {
            status = "Just Started";
        }

        assert status != null && !status.isEmpty() : "Status should not be null or empty";
        return status;
    }

    // Builder class
    public static class Builder {
        private String name; // compulsory
        private String phone;
        private String email;
        private String address;
        private String course;

        public Builder(String name) {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("Name is compulsory");
            }
            this.name = name;
        }

        public Builder(Student existing) {
            this.name = existing.name;
            this.phone = existing.phone;
            this.email = existing.email;
            this.address = existing.address;
            this.course = existing.course;
        }

        public Builder name(String name) {
            if (name != null && !name.isBlank()) {
                this.name = name;
            }
            return this;
        }

        public Builder phone(String phone) {
            if (phone != null) {
                this.phone = phone;
            }
            return this;
        }

        public Builder email(String email) {
            if (email != null) {
                this.email = email;
            }
            return this;
        }

        public Builder address(String address) {
            if (address != null) {
                this.address = address;
            }
            return this;
        }

        public Builder course(String course) {
            if (course != null) {
                this.course = course;
            }
            return this;
        }

        public Student build() {
            return new Student(this);
        }
    }

}
