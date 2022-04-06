package com.demiglace.springdata.idgenerators.repos;

import org.springframework.data.repository.CrudRepository;

import com.demiglace.springdata.idgenerators.entities.Employee;

public interface EmployeeRepository extends CrudRepository<Employee, Long> {

}
