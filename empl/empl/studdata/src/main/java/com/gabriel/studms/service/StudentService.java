package com.gabriel.studms.service;
import com.gabriel.studms.model.Student;
public interface StudentService {
	Student[] getAll() throws Exception;
	Student get(Integer id) throws Exception;
	Student create(Student student) throws Exception;
	Student update(Student student) throws Exception;
	void delete(Integer id) throws Exception;
}
