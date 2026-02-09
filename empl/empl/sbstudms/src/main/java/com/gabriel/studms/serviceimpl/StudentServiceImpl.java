package com.gabriel.studms.serviceimpl;
import com.gabriel.studms.entity.StudentData;
import com.gabriel.studms.model.Student;
import com.gabriel.studms.repository.StudentDataRepository;
import com.gabriel.studms.service.StudentService;
import com.gabriel.studms.transform.TransformStudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class StudentServiceImpl implements StudentService {
	Logger logger = LoggerFactory.getLogger(StudentServiceImpl.class);
	@Autowired
	StudentDataRepository studentDataRepository;
	@Autowired
	TransformStudentService transformerStudentService;
	@Override

public Student[] getAll() {
		List<StudentData> studentsData = new ArrayList<>();
		List<Student> students = new ArrayList<>();
		studentDataRepository.findAll().forEach(studentsData::add);
		Iterator<StudentData> it = studentsData.iterator();
		while(it.hasNext()) {
			StudentData studentData = it.next();
			Student student = transformerStudentService.transform(studentData);
			students.add(student);
		}
		Student[] array = new Student[students.size()];
		for  (int i = 0; i < students.size(); i++){
			array[i] = students.get(i);
		}
		return array;
	}
	@Override
public Student create(Student student) {
		logger.info(" add:Input " + student.toString());
		StudentData studentData = transformerStudentService.transform(student);
        System.out.println(studentData);
		studentData = studentDataRepository.save(studentData);
		logger.info(" add:Input " + studentData.toString());
		Student newStudent = transformerStudentService.transform(studentData);
        System.out.println(newStudent);
		return newStudent;
	}
@Override
public Student update(Student student) {
		logger.info(" update:Input " + student.toString());
		
		// Load existing student from database to preserve created timestamp
		Optional<StudentData> existingStudentOpt = studentDataRepository.findById(student.getId());
		if (!existingStudentOpt.isPresent()) {
			logger.error(" Failed >> unable to locate student id: " + student.getId());
			return null;
		}
		
		// Update only the fields that can change, preserving created timestamp
		StudentData studentData = existingStudentOpt.get();
		studentData.setFirstName(student.getFirstName());
		studentData.setLastName(student.getLastName());
		studentData.setStudentNumber(student.getStudentNumber());
		studentData.setEmail(student.getEmail());
		studentData.setDepartment(student.getDepartment());
		
		// Save - this will preserve created timestamp and update lastUpdated timestamp
		studentData = studentDataRepository.save(studentData);
		logger.info(" update:Result " + studentData.toString());
		
		Student newStudent = transformerStudentService.transform(studentData);
		return newStudent;
	}
	@Override
public Student get(Integer id) {
		logger.info(" Input id >> "+  Integer.toString(id) );
		Optional<StudentData> optional = studentDataRepository.findById(id);
		if(optional.isPresent()) {
			logger.info(" Is present >> ");
			StudentData studentDatum = optional.get();
			Student student = transformerStudentService.transform(studentDatum);
			return student;
		}
		logger.info(" Failed >> unable to locate id: " +  Integer.toString(id)  );
		return null;
	}
	@Override
public void delete(Integer id) {
		logger.info(" Input >> " +  Integer.toString(id));
		Optional<StudentData> optional = studentDataRepository.findById(id);
		if( optional.isPresent()) {
			StudentData studentDatum = optional.get();
			studentDataRepository.delete(studentDatum);
			logger.info(" Success >> " + studentDatum.toString());
		}
		else {
			logger.info(" Failed >> unable to locate student id:" +  Integer.toString(id));
		}
	}
}
