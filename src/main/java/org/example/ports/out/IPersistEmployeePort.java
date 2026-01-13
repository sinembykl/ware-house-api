package org.example.ports.out;

import org.example.core.domain.Employee;
import org.example.core.results.NoContentResult;

public interface IPersistEmployeePort {

    public NoContentResult persistEmployee(Employee employee);
    Employee readEmployee(Long employeeId); // Added for database lookup
}
