package domain;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Student {
	public static final int FAIL_COURSE_GRADE = 10;
	private String id;
	private String name;

	static class CourseSection {
        CourseSection(Course course, int section) {
            this.course = course;
            this.section = section;
        }
        Course course;
	    int section;
    }
	private Map<Term, Map<Course, Double>> transcript;
	private List<CourseSection> currentTerm;

	public Student(String id, String name) {
		this.id = id;
		this.name = name;
		this.transcript = new HashMap<>();
		this.currentTerm = new ArrayList<>();
	}
	
	public void takeCourse(Course _course, int section) {
		currentTerm.add(new CourseSection(_course, section));
	}

	public Map<Term, Map<Course, Double>> getTranscript() {
		return transcript;
	}

	public void addTranscriptRecord(Course course, Term term, double grade) {
	    if (!transcript.containsKey(term))
	        transcript.put(term, new HashMap<>());
	    transcript.get(term).put(course, grade);
    }

	public double calculateGPA() {
		double points = 0;
		int totalUnits = 0;
		for (Map.Entry<Term, Map<Course, Double>> term : transcript.entrySet()) {
			for (Map.Entry<Course, Double> course : term.getValue().entrySet()) {
				points += course.getValue() * course.getKey().getUnits();
				totalUnits += course.getKey().getUnits();
			}
		}
		return points / totalUnits;
	}

	public boolean hasPassed(Course course) {
		for (Map.Entry<Term, Map<Course, Double>> term : getTranscript().entrySet()) {
			for (Map.Entry<Course, Double> termCourse : term.getValue().entrySet()) {
				if (termCourse.getKey().equals(course) && termCourse.getValue() >= FAIL_COURSE_GRADE)
					return true;
			}
		}
		return false;
	}


    public List<CourseSection> getCurrentTerm() {
        return currentTerm;
    }

    public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
	public String toString() {
		return name;
	}
}
