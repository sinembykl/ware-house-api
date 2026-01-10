package org.example.core.ports.in;

import org.example.core.domain.Employee;
import org.example.core.results.NoContentResult;

public interface ICreateEmployee {

    public NoContentResult createEmployee(Employee employee);
}
