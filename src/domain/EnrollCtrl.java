package domain;

import java.util.List;

import domain.exceptions.EnrollmentRulesViolationException;

public class EnrollCtrl {

    public static final int FAILED_TERM_GPA = 12;
    public static final int FAILED_TERM_LIMIT_UNITS = 14;
    public static final int UNCONDITIONAL_MAX_UNITS_LIMIT = 20;
    public static final int GOOD_STUDENT_GPA = 16;
    public static final int NOT_GOOD_STUDENTS_LIMIT_UNITS = 16;

    public void enroll(Student student, List<CSE> courses) throws EnrollmentRulesViolationException {
        checkForTakingPassedCourses(courses, student);
        checkForPassingAllPrerequisites(courses, student);
        checkForNotTakingSameExamTime(courses);
        checkForNotTakingTheSameCourseTwice(courses);
        checkForTakingUnitsRequestLimit(courses, student);
        for (CSE course : courses)
            student.takeCourse(course.getCourse(), course.getSection());
    }

    private void checkForTakingUnitsRequestLimit(List<CSE> courses, Student student) throws EnrollmentRulesViolationException {
        int unitsRequested = courses.stream().mapToInt(c -> c.getCourse().getUnits()).sum();
        double gpa = student.calculateGPA();
        if (validatedUnitsRequest(unitsRequested, gpa))
            throw new EnrollmentRulesViolationException(String.format("Number of units (%d) requested does not match GPA of %f", unitsRequested, gpa));
    }

    private boolean validatedUnitsRequest(int requested, double gpa) {
        return (gpa < FAILED_TERM_GPA && requested > FAILED_TERM_LIMIT_UNITS) ||
                (gpa < GOOD_STUDENT_GPA && requested > NOT_GOOD_STUDENTS_LIMIT_UNITS) ||
                (requested > UNCONDITIONAL_MAX_UNITS_LIMIT);
    }

    private void checkForNotTakingTheSameCourseTwice(List<CSE> courses) throws EnrollmentRulesViolationException {
        for (CSE course1 : courses) {
            for (CSE course2 : courses) {
                if (course1 != course2 && course1.getCourse().equals(course2.getCourse()))
                    throw new EnrollmentRulesViolationException(String.format("%s is requested to be taken twice", course1.getCourse().getName()));
            }
        }
    }

    private void checkForNotTakingSameExamTime(List<CSE> courses) throws EnrollmentRulesViolationException {
        for (CSE course1 : courses) {
            for (CSE course2 : courses) {
                if (course1 != course2 && course1.getExamTime().equals(course2.getExamTime()))
                    throw new EnrollmentRulesViolationException(String.format("Two offerings %s and %s have the same exam time", course1, course2));
            }
        }
    }

    private void checkForPassingAllPrerequisites(List<CSE> courses, Student student) throws EnrollmentRulesViolationException {
        for (CSE course : courses) {
            List<Course> prerequisites = course.getCourse().getPrerequisites();
            for (Course prerequisite : prerequisites) {
                if (!student.hasPassed(prerequisite))
                    throw new EnrollmentRulesViolationException(String.format("The student has not passed %s as a prerequisite of %s", prerequisite.getName(), course.getCourse().getName()));
            }
        }
    }

    private void checkForTakingPassedCourses(List<CSE> courses, Student student) throws EnrollmentRulesViolationException {
        for (CSE course : courses) {
            if (student.hasPassed(course.getCourse()))
                throw new EnrollmentRulesViolationException(String.format("The student has already passed %s", course.getCourse().getName()));
        }
    }
}
