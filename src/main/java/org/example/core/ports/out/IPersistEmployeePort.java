package org.example.core.ports.out;

import org.example.core.domain.Employee;
import org.example.core.results.NoContentResult;

public interface IPersistEmployeePort {

    public NoContentResult persistEmployee(Employee employee);
}
