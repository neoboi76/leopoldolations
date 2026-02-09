package com.gabriel.studms.transform;
import com.gabriel.studms.entity.StudentData;
import com.gabriel.studms.model.Student;
import org.springframework.stereotype.Service;
@Service
public class TransformStudentServiceImpl implements TransformStudentService {
@Override
	public StudentData transform(Student student){
		StudentData studentData = new StudentData();
		
		if (student.getId() > 0) {
			studentData.setId(student.getId());
		}
		studentData.setFirstName(student.getFirstName());
        studentData.setLastName(student.getLastName());
        studentData.setStudentNumber(student.getStudentNumber());
        studentData.setEmail(student.getEmail());
        studentData.setDepartment(student.getDepartment());
        
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
