package com.gabriel.emplms.transform;
import com.gabriel.emplms.entity.StudentData;
import com.gabriel.emplms.model.Student;
public interface TransformStudentService {
	StudentData transform(Student student);
	Student transform(StudentData studentData);
}
