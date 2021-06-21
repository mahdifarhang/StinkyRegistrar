package domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import domain.exceptions.EnrollmentRulesViolationException;

public class EnrollCtrl {

    public static final int FAILED_TERM_GPA = 12;
    public static final int FAILED_TERM_LIMIT_UNITS = 14;
    public static final int UNCONDITIONAL_MAX_UNITS_LIMIT = 20;
    public static final int GOOD_STUDENT_GPA = 16;
    public static final int NOT_GOOD_STUDENTS_LIMIT_UNITS = 16;

    public List<EnrollmentRulesViolationException> enroll(Student student, List<CSE> courses) {
        List<EnrollmentRulesViolationException> violations = new ArrayList<>();
        violations.add(checkForTakingPassedCourses(courses, student));
        violations.add(checkForPassingAllPrerequisites(courses, student));
        violations.add(checkForNotTakingSameExamTime(courses));
        violations.add(checkForNotTakingTheSameCourseTwice(courses));
        violations.add(checkForTakingUnitsRequestLimit(courses, student));
        violations.removeAll(Collections.singleton(null));
        if (!violations.isEmpty())
            return violations;
        for (CSE course : courses)
            student.takeCourse(course.getCourse(), course.getSection());
        return null;
    }

    private EnrollmentRulesViolationException checkForTakingUnitsRequestLimit(List<CSE> courses, Student student) {
        int unitsRequested = courses.stream().mapToInt(c -> c.getCourse().getUnits()).sum();
        double gpa = student.calculateGPA();
        if (validatedUnitsRequest(unitsRequested, gpa))
            return new EnrollmentRulesViolationException(String.format("Number of units (%d) requested does not match GPA of %f", unitsRequested, gpa));
        return null;
    }

    private boolean validatedUnitsRequest(int requested, double gpa) {
        return (gpa < FAILED_TERM_GPA && requested > FAILED_TERM_LIMIT_UNITS) ||
                (gpa < GOOD_STUDENT_GPA && requested > NOT_GOOD_STUDENTS_LIMIT_UNITS) ||
                (requested > UNCONDITIONAL_MAX_UNITS_LIMIT);
    }

    private EnrollmentRulesViolationException checkForNotTakingTheSameCourseTwice(List<CSE> courses) {
        for (CSE course1 : courses) {
            for (CSE course2 : courses) {
                if (course1 != course2 && course1.getCourse().equals(course2.getCourse()))
                    return new EnrollmentRulesViolationException(String.format("%s is requested to be taken twice", course1.getCourse().getName()));
            }
        }
        return null;
    }

    private EnrollmentRulesViolationException checkForNotTakingSameExamTime(List<CSE> courses) {
        for (CSE course1 : courses) {
            for (CSE course2 : courses) {
                if (course1 != course2 && course1.getExamTime().equals(course2.getExamTime()))
                    return new EnrollmentRulesViolationException(String.format("Two offerings %s and %s have the same exam time", course1, course2));
            }
        }
        return null;
    }

    private EnrollmentRulesViolationException checkForPassingAllPrerequisites(List<CSE> courses, Student student) {
        for (CSE course : courses) {
            List<Course> prerequisites = course.getCourse().getPrerequisites();
            for (Course prerequisite : prerequisites) {
                if (!student.hasPassed(prerequisite))
                    return new EnrollmentRulesViolationException(String.format("The student has not passed %s as a prerequisite of %s", prerequisite.getName(), course.getCourse().getName()));
            }
        }
        return null;
    }

    private EnrollmentRulesViolationException checkForTakingPassedCourses(List<CSE> courses, Student student) {
        for (CSE course : courses) {
            if (student.hasPassed(course.getCourse()))
                return new EnrollmentRulesViolationException(String.format("The student has already passed %s", course.getCourse().getName()));
        }
        return null;
    }
}
