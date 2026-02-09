package com.gabriel.emplms.transform;
import com.gabriel.emplms.entity.StudentData;
import com.gabriel.emplms.model.Student;
import org.springframework.stereotype.Service;
@Service
public class TransformStudentServiceImpl implements TransformStudentService {
@Override
	public StudentData transform(Student student){
		StudentData studentData = new StudentData();
		// Don't set ID for new students - let database auto-generate it
		// Only set ID if it's greater than 0 (indicating an existing student)
		if (student.getId() > 0) {
			studentData.setId(student.getId());
		}
		studentData.setFirstName(student.getFirstName());
        studentData.setLastName(student.getLastName());
        studentData.setStudentNumber(student.getStudentNumber());
        studentData.setEmail(student.getEmail());
        studentData.setDepartment(student.getDepartment());
        // Note: created and lastUpdated are managed by database annotations
		return studentData;
	}
@Override

	public Student transform(StudentData studentData){
		Student student = new Student();
		student.setId(studentData.getId());
        student.setFirstName(studentData.getFirstName());
        student.setLastName(studentData.getLastName());
        student.setStudentNumber(studentData.getStudentNumber());
        student.setEmail(studentData.getEmail());
        student.setDepartment(studentData.getDepartment());
        student.setCreated(studentData.getCreated());
        student.setLastUpdated(studentData.getLastUpdated());
		return student;
	}
}
