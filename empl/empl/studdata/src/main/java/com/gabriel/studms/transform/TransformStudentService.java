package com.gabriel.studms.transform;
import com.gabriel.studms.entity.StudentData;
import com.gabriel.studms.model.Student;
public interface TransformStudentService {
	StudentData transform(Student student);
	Student transform(StudentData studentData);
}
