package org.example.ports.in;

import org.example.core.results.NoContentResult;

public interface IAssignOrder {
    public NoContentResult assignOrder(Long id, Long EmployeeId);

}
