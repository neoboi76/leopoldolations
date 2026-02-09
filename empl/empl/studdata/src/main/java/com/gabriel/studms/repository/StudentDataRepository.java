package com.gabriel.studms.repository;
import com.gabriel.studms.entity.StudentData;
import org.springframework.data.repository.CrudRepository;
public interface StudentDataRepository extends CrudRepository<StudentData,Integer> {}