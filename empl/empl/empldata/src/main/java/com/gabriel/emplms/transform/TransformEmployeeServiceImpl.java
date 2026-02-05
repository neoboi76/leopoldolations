package com.gabriel.emplms.transform;
import com.gabriel.emplms.entity.EmployeeData;
import com.gabriel.emplms.model.Employee;
import org.springframework.stereotype.Service;
@Service
public class TransformEmployeeServiceImpl implements TransformEmployeeService {
	@Override
	public EmployeeData transform(Employee employee){
		EmployeeData employeeData = new EmployeeData();
		employeeData.setId(employee.getId());
		employeeData.setFirstName(employee.getFirstName());
        employeeData.setLastName(employee.getLastName());
        employeeData.setEmail(employee.getEmail());
        employeeData.setDepartment(employee.getDepartment());
		return employeeData;
	}
	@Override

	public Employee transform(EmployeeData employeeData){
		Employee employee = new Employee();
		employee.setId(employeeData.getId());
        employee.setFirstName(employeeData.getFirstName());
        employee.setLastName(employeeData.getLastName());
        employee.setEmail(employeeData.getEmail());
        employee.setDepartment(employeeData.getDepartment());
		return employee;
	}
}
