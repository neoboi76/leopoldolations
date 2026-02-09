package com.gabriel.emplms.controller;
import com.gabriel.emplms.model.Student;
import com.gabriel.emplms.service.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class StudentController {
	Logger logger = LoggerFactory.getLogger( StudentController.class);
	@Autowired
	private StudentService studentService;
@GetMapping("/api/student")
	public ResponseEntity<?> listStudent()
{
		HttpHeaders headers = new HttpHeaders();
		ResponseEntity<?> response;
		try {
			Student[] students = studentService.getAll();
			response =  ResponseEntity.ok().headers(headers).body(students);
		}
		catch( Exception ex)
		{
			response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
		}
		return response;
	}
@PostMapping("/api/student")
	public ResponseEntity<?> add(@RequestBody Student student){
		logger.info("Input >> " + student.toString() );
		HttpHeaders headers = new HttpHeaders();
		ResponseEntity<?> response;
		try {
			Student newStudent = studentService.create(student);
			logger.info("created student >> " + newStudent.toString() );
			response = ResponseEntity.ok(newStudent);
		}
		catch( Exception ex)
		{
			logger.error("Failed to retrieve student with id : {}", ex.getMessage(), ex);
			response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
		}
		return response;
	}
@PutMapping("/api/student/{id}")
	public ResponseEntity<?> update(@PathVariable final Integer id, @RequestBody Student student){
        logger.info("Input >> " + student.toString() );
        HttpHeaders headers = new HttpHeaders();
        ResponseEntity<?> response;
        try {
            student.setId(id);
            Student newStudent = studentService.update(student);
            response = ResponseEntity.ok(newStudent);
        }
        catch( Exception ex)
        {
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
        return response;
	}

@GetMapping("/api/student/{id}")
	public ResponseEntity<?> get(@PathVariable final Integer id){
		logger.info("Input student id >> " + Integer.toString(id));
		HttpHeaders headers = new HttpHeaders();
		ResponseEntity<?> response;
		try {
			Student student = studentService.get(id);
			response = ResponseEntity.ok(student);
		}
		catch( Exception ex)
		{
			response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
		}
		return response;
	}
@DeleteMapping("/api/student/{id}")
	public ResponseEntity<?> delete(@PathVariable final Integer id){
		logger.info("Input >> " + Integer.toString(id));
		HttpHeaders headers = new HttpHeaders();
		ResponseEntity<?> response;
		try {
			studentService.delete(id);
			response = ResponseEntity.ok(null);
		}
		catch( Exception ex)
		{
			response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
		}
		return response;
	}
}
