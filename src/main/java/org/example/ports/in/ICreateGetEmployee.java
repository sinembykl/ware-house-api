package org.example.ports.in;

import org.example.core.domain.Employee;
import org.example.core.results.NoContentResult;

public interface ICreateGetEmployee {

    public NoContentResult createEmployee(Employee employee);

    Employee getEmployee(Long employeeId);
}
