package org.example.ports.out;

import org.example.core.results.NoContentResult;
import org.example.persistence.OrderEntity;

public interface IAssignOrderOutPort {
    public NoContentResult updateOrder(Long id, Long EmployeeId);
}
