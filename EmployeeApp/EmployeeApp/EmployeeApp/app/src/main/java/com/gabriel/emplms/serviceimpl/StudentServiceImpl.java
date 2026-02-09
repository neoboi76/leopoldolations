package com.gabriel.emplms.serviceimpl;

import com.gabriel.emplms.entity.StudentData;
import com.gabriel.emplms.model.Student;
import com.gabriel.emplms.repository.StudentRepository;
import com.gabriel.emplms.transform.TransformStudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentServiceImpl implements StudentService {
    Logger logger = LoggerFactory.getLogger(StudentServiceImpl.class);
    @Autowired
    StudentRepository studentRepository;
    @Autowired
    TransformStudentService transformerStudentService;

    @Override
    public Student[] getAll() {
        java.util.List<StudentData> studentsData = new java.util.ArrayList<>();
        studentRepository.findAll().forEach(studentsData::add);
        java.util.List<Student> students = new java.util.ArrayList<>();
        java.util.Iterator<StudentData> it = studentsData.iterator();
        while(it.hasNext()) {
            StudentData studentDatum = it.next();
            Student student = transformerStudentService.transform(studentDatum);
            students.add(student);
        }
        Student[] array = new Student[students.size()];
        for (int i = 0; i < students.size(); i++){
            array[i] = students.get(i);
        }
        return array;
    }

    @Override
    public Student create(Student student) {
        logger.info(" add:Input " + student.toString());
        StudentData studentData = transformerStudentService.transform(student);
        System.out.println(studentData);
        studentData = studentRepository.save(studentData);
        logger.info(" add:Input " + studentData.toString());
        Student newStudent = transformerStudentService.transform(studentData);
        System.out.println(newStudent);
        return newStudent;
    }

    @Override
    public Student update(Student student) {
        logger.info(" update:Input " + student.toString());
        // Load existing student from database to preserve created timestamp
        java.util.Optional<StudentData> existingStudentOpt = studentRepository.findById(student.getId());
        if (!existingStudentOpt.isPresent()) {
            logger.error(" Failed >> unable to locate student id: " + Integer.toString(student.getId()));
            return null;
        }
        
        // Update only the fields that can change, preserving the created timestamp
        StudentData studentData = existingStudentOpt.get();
        studentData.setFirstName(student.getFirstName());
        studentData.setLastName(student.getLastName());
        studentData.setEmail(student.getEmail());
        studentData.setDepartment(student.getDepartment());
        studentData.setStudentNumber(student.getStudentNumber());
        
        logger.info(" update:Result " + studentData.toString());
        studentData = studentRepository.save(studentData);
        Student newStudent = transformerStudentService.transform(studentData);
        logger.info(" update:Result " + newStudent.toString());
        return newStudent;
    }

    @Override
    public Student get(Integer id) {
        logger.info(" Input id >> "+ Integer.toString(id) );
        java.util.Optional<StudentData> optional = studentRepository.findById(id);
        if(optional.isPresent()) {
            logger.info(" Is present >> ");
            StudentData studentDatum = optional.get();
            Student student = transformerStudentService.transform(studentDatum);
            return student;
        }
        logger.error(" Failed >> unable to locatestudent id:" + Integer.toString(id));
        return null;
    }

    @Override
    public void delete(Integer id) {
        logger.info(" Input >> " + Integer.toString(id));
        java.util.Optional<StudentData> optional = studentRepository.findById(id);
        if( optional.isPresent()) {
            StudentData studentDatum = optional.get();
            studentRepository.delete(studentDatum);
            logger.info(" Success >> " + studentDatum.toString());
        }
        else {
            logger.error(" Failed >> unable to locatestudent id:" + Integer.toString(id));
        }
    }
    }
}