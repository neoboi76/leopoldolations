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
		employeeData.setDescription(employee.getDescription());
		employeeData.setName(employee.getName());
		return employeeData;
	}
	@Override

	public Employee transform(EmployeeData employeeData){;
		Employee employee = new Employee();
		employee.setId(employeeData.getId());
		employee.setDescription(employeeData.getDescription());
		employee.setName(employeeData.getName());
		return employee;
	}
}
