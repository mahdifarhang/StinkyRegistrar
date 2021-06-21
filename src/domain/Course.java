package domain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Course {
	private String id;
	private String name;
	private int units;
	
	List<Course> prerequisites;

	public Course(String id, String name, int units) {
		this.id = id;
		this.name = name;
		this.units = units;
		prerequisites = new ArrayList<Course>();
	}
	
	public void addPre(Course course) {
		getPrerequisites().add(course);
	}

	public Course withPre(Course... courses) {
		prerequisites.addAll(Arrays.asList(courses));
		return this;
	}

	public List<Course> getPrerequisites() {
		return prerequisites;
	}

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(name);
		stringBuilder.append(" {");
		for (Course pre : getPrerequisites()) {
			stringBuilder.append(pre.getName());
			stringBuilder.append(", ");
		}
		stringBuilder.append("}");
		return stringBuilder.toString();
	}

	public String getName() {
		return name;
	}

	public int getUnits() {
		return units;
	}

	public String getId() {
		return id;
	}

	public boolean equals(Object obj) {
		Course other = (Course)obj;
		return id.equals(other.id);
	}
}
