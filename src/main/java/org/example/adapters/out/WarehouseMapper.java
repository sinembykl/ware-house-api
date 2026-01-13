package org.example.adapters.out;

import org.example.core.domain.Employee;
import org.example.core.domain.Item;
import org.example.core.domain.Order;
import org.example.core.domain.OrderItem;
import org.example.persistence.EmployeeEntity;
import org.example.persistence.ItemEntity;
import org.example.persistence.OrderEntity;
import org.example.persistence.OrderItemEntity;

import java.util.List;
import java.util.stream.Collectors;

public class WarehouseMapper {

    // --- ITEM MAPPING ---
    public static Item toDomain(ItemEntity entity) {
        if (entity == null) return null;
        Item item = new Item(entity.getSku(), entity.getItem_name(), entity.getLocation());
        item.setItem_id(entity.getItem_id());
        return item;
    }

    public static ItemEntity toEntity(Item domain) {
        if (domain == null) return null;
        return new ItemEntity(domain);
    }

    public static Order toDomain(OrderEntity entity) {
        if (entity == null) return null;
        Order order = new Order(entity.getStore(), entity.getUnit());
        order.setOrder_id(entity.getOrder_id());
        order.setStatus(entity.getStatus());

        // Check if the entity has items and map them
        if (entity.getOrderItemEntities() != null) {
            List<OrderItem> domainItems = entity.getOrderItemEntities().stream()
                    .map(WarehouseMapper::toDomain) // Map each Entity to Domain
                    .collect(Collectors.toList());

            // This now works because Order.setOrderItems accepts a List
            order.setOrderItems(domainItems);
        }

        return order;
    }
    public static OrderItem toDomain(OrderItemEntity entity) {
        if (entity == null) return null;
        OrderItem item = new OrderItem();
        item.setId(entity.getId());
        item.setQtyRequired(entity.getQtyRequired());
        item.setQtyPicked(entity.getQtyPicked());
        item.setLocation(entity.getLocation());

        // Fix: Pull the SKU from the linked ItemEntity
        if (entity.getItem() != null) {
            item.setItem_sku(entity.getItem().getSku()); // This fills item_sku
            item.setItem(toDomain(entity.getItem()));   // This fills the nested item object
        }

        return item;
    }
    public static Employee toDomain(EmployeeEntity entity) {
        if (entity == null) {
            return null;
        }

        // Use the domain constructor: Employee(String name, boolean active, int shift)
        Employee employee = new Employee(
                entity.getName(),
                entity.isActive(),
                entity.getShift()
        );

        // Don't forget to set the ID so the Domain knows which record it represents
        employee.setId(entity.getId());

        return employee;
    }



}