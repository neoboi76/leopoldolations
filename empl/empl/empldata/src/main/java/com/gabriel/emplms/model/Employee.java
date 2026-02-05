package com.gabriel.emplms.model;
import lombok.Data;
import java.util.Date;

@Data
public class Employee{
	int id;
	String name;
    private String firstName;
    private String lastName;
    private String email;
    private String department;
}
