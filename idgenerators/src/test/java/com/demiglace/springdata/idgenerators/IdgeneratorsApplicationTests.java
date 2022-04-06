package com.demiglace.springdata.idgenerators;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.demiglace.springdata.idgenerators.entities.Employee;
import com.demiglace.springdata.idgenerators.repos.EmployeeRepository;

@SpringBootTest
class IdgeneratorsApplicationTests {

	@Autowired
	EmployeeRepository er;
	
	@Test
	void testCreateEmployee() {
		Employee employee = new Employee();
		employee.setName("Doge");
		er.save(employee);
	}

}
