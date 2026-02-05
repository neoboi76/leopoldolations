package com.gabriel.emplms.serviceimpl;
import com.gabriel.emplms.entity.EmployeeData;
import com.gabriel.emplms.model.Employee;
import com.gabriel.emplms.repository.EmployeeDataRepository;
import com.gabriel.emplms.service.EmployeeService;
import com.gabriel.emplms.transform.TransformEmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
@Service
public class EmployeeServiceImpl implements EmployeeService {
	Logger logger = LoggerFactory.getLogger(EmployeeServiceImpl.class);
	@Autowired
	EmployeeDataRepository employeeDataRepository;
	@Autowired
	TransformEmployeeService transformerEmployeeService;
	@Override
	public Employee[] getAll() {
		List<EmployeeData> employeesData = new ArrayList<>();
		List<Employee> employees = new ArrayList<>();
		employeeDataRepository.findAll().forEach(employeesData::add);
		Iterator<EmployeeData> it = employeesData.iterator();
		while(it.hasNext()) {
			EmployeeData employeeData = it.next();
			Employee employee = transformerEmployeeService.transform(employeeData);
			employees.add(employee);
		}
		Employee[] array = new Employee[employees.size()];
		for  (int i=0; i<employees.size(); i++){
			array[i] = employees.get(i);
		}
		return array;
	}
	@Override
	public Employee create(Employee employee) {
		logger.info(" add:Input " + employee.toString());
		EmployeeData employeeData = transformerEmployeeService.transform(employee);
		employeeData = employeeDataRepository.save(employeeData);
		logger.info(" add:Input " + employeeData.toString());
		Employee newEmployee = transformerEmployeeService.transform(employeeData);
		return newEmployee;
	}
	@Override
	public Employee update(Employee employee) {
		EmployeeData employeeData = transformerEmployeeService.transform(employee);
		employeeData = employeeDataRepository.save(employeeData);
		Employee newEmployee = transformerEmployeeService.transform(employeeData);
		return newEmployee;
	}
	@Override
	public Employee get(Integer id) {
		logger.info(" Input id >> "+  Integer.toString(id) );
		Optional<EmployeeData> optional = employeeDataRepository.findById(id);
		if(optional.isPresent()) {
			logger.info(" Is present >> ");
			EmployeeData employeeDatum = optional.get();
			Employee employee = transformerEmployeeService.transform(employeeDatum);
			return employee;
		}
		logger.info(" Failed >> unable to locate id: " +  Integer.toString(id)  );
		return null;
	}
	@Override
	public void delete(Integer id) {
		logger.info(" Input >> " +  Integer.toString(id));
		Optional<EmployeeData> optional = employeeDataRepository.findById(id);
		if( optional.isPresent()) {
			EmployeeData employeeDatum = optional.get();
			employeeDataRepository.delete(employeeDatum);
			logger.info(" Success >> " + employeeDatum.toString());
		}
		else {
			logger.info(" Failed >> unable to locateemployee id:" +  Integer.toString(id));
		}
	}
}
