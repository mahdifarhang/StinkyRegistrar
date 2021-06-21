package domain;

import java.util.List;
import java.util.Map;

import domain.exceptions.EnrollmentRulesViolationException;

public class EnrollCtrl {
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
        return (gpa < 12 && requested > 14) ||
                (gpa < 16 && requested > 16) ||
                (requested > 20);
    }

    private void checkForNotTakingTheSameCourseTwice(List<CSE> courses) throws EnrollmentRulesViolationException {
        for (CSE course1 : courses) {
            for (CSE course2 : courses) {
                if (course1 == course2)
                    continue;
                if (course1.getCourse().equals(course2.getCourse()))
                    throw new EnrollmentRulesViolationException(String.format("%s is requested to be taken twice", course1.getCourse().getName()));
            }
        }
    }

    private void checkForNotTakingSameExamTime(List<CSE> courses) throws EnrollmentRulesViolationException {
        for (CSE course1 : courses) {
            for (CSE course2 : courses) {
                if (course1 == course2)
                    continue;
                if (course1.getExamTime().equals(course2.getExamTime()))
                    throw new EnrollmentRulesViolationException(String.format("Two offerings %s and %s have the same exam time", course1, course2));
            }
        }
    }

    private void checkForPassingAllPrerequisites(List<CSE> courses, Student student) throws EnrollmentRulesViolationException {
        Map<Term, Map<Course, Double>> transcript = student.getTranscript();
        for (CSE course : courses) {
            List<Course> prerequisites = course.getCourse().getPrerequisites();
            nextPre:
            for (Course prerequisite : prerequisites) {
                for (Map.Entry<Term, Map<Course, Double>> term : transcript.entrySet()) {
                    for (Map.Entry<Course, Double> termCourse : term.getValue().entrySet()) {
                        if (termCourse.getKey().equals(prerequisite) && termCourse.getValue() >= 10)
                            continue nextPre;
                    }
                }
                throw new EnrollmentRulesViolationException(String.format("The student has not passed %s as a prerequisite of %s", prerequisite.getName(), course.getCourse().getName()));
            }
        }
    }

    private void checkForTakingPassedCourses(List<CSE> courses, Student student) throws EnrollmentRulesViolationException {
        Map<Term, Map<Course, Double>> transcript = student.getTranscript();
        for (CSE course : courses) {
            for (Map.Entry<Term, Map<Course, Double>> term : transcript.entrySet()) {
                for (Map.Entry<Course, Double> termCourse : term.getValue().entrySet()) {
                    if (termCourse.getKey().equals(course.getCourse()) && termCourse.getValue() >= 10)
                        throw new EnrollmentRulesViolationException(String.format("The student has already passed %s", course.getCourse().getName()));
                }
            }
        }
    }
}
