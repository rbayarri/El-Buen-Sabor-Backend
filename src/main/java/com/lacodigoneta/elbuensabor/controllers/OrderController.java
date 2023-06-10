package com.lacodigoneta.elbuensabor.controllers;

import com.lacodigoneta.elbuensabor.dto.order.ClientOrderDto;
import com.lacodigoneta.elbuensabor.dto.order.OrderDto;
import com.lacodigoneta.elbuensabor.entities.Order;
import com.lacodigoneta.elbuensabor.enums.Status;
import com.lacodigoneta.elbuensabor.mappers.OrderMapper;
import com.lacodigoneta.elbuensabor.services.OrderService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@AllArgsConstructor
public class OrderController {

    private final OrderService service;

    private final OrderMapper mapper;

    //TODO: Pago

    @GetMapping(value = "/admin", params = "pageable")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Page<OrderDto>> getAll(Pageable pageable) {
        Page<Order> all = service.findAllPaged(pageable);
        return ResponseEntity.ok(all.map(mapper::toOrderDto));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<OrderDto>> getAll() {
        List<Order> all = service.findAll();
        return ResponseEntity.ok(all.stream().map(mapper::toOrderDto).toList());
    }

    @GetMapping(value = "/admin/user/{id}", params = "pageable")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Page<OrderDto>> getAllByUserId(@PathVariable UUID id, Pageable pageable) {
        Page<Order> allByUserId = service.findAllByUserId(id, pageable);
        return ResponseEntity.ok(allByUserId.map(mapper::toOrderDto));
    }

    @GetMapping("/admin/user/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<OrderDto>> getAllByUserId(@PathVariable UUID id) {
        List<Order> allByUserId = service.findAllByUserId(id);
        return ResponseEntity.ok(allByUserId.stream().map(mapper::toOrderDto).toList());
    }

    @GetMapping(value = "", params = "pageable")
    public ResponseEntity<Page<ClientOrderDto>> getMyOrders(Pageable pageable) {
        Page<Order> myOrders = service.findMyOrders(pageable);
        return ResponseEntity.ok(myOrders.map(mapper::toClientOrderDto));
    }

    @GetMapping("")
    public ResponseEntity<List<ClientOrderDto>> getMyOrders() {
        List<Order> myOrders = service.findMyOrders();
        return ResponseEntity.ok(myOrders.stream().map(mapper::toClientOrderDto).toList());
    }

    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ClientOrderDto> save(@RequestBody @Valid ClientOrderDto clientOrderDto) {

        Order saved = service.save(mapper.toEntity(clientOrderDto));
        ClientOrderDto savedOrderDto = mapper.toClientOrderDto(saved);

        return ResponseEntity.created(
                        ServletUriComponentsBuilder
                                .fromCurrentRequestUri()
                                .path("/{id}")
                                .buildAndExpand(savedOrderDto.getId())
                                .toUri())
                .body(savedOrderDto);
    }

    @GetMapping(value = "/cooking", params = "pageable")
    @PreAuthorize("hasRole('ROLE_CHEF')")
    public ResponseEntity<Page<OrderDto>> getCookingOrders(Pageable pageable) {
        Page<Order> cookingOrders = service.findAllByStatus(Status.COOKING, pageable);
        return ResponseEntity.ok(cookingOrders.map(mapper::toOrderDto));
    }

    @GetMapping("/cooking")
    @PreAuthorize("hasRole('ROLE_CHEF')")
    public ResponseEntity<List<OrderDto>> getCookingOrders() {
        List<Order> cookingOrders = service.findAllByStatus(Status.COOKING);
        return ResponseEntity.ok(cookingOrders.stream().map(mapper::toOrderDto).toList());
    }

    @GetMapping(value = "/delivery", params = "pageable")
    @PreAuthorize("hasRole('ROLE_DELIVERY')")
    public ResponseEntity<Page<OrderDto>> getOrdersToDeliver(Pageable pageable) {
        Page<Order> ordersToDeliver = service.findAllByStatus(Status.ON_THE_WAY, pageable);
        return ResponseEntity.ok(ordersToDeliver.map(mapper::toOrderDto));
    }

    @GetMapping("/delivery")
    @PreAuthorize("hasRole('ROLE_DELIVERY')")
    public ResponseEntity<List<OrderDto>> getOrdersToDeliver() {
        List<Order> ordersToDeliver = service.findAllByStatus(Status.ON_THE_WAY);
        return ResponseEntity.ok(ordersToDeliver.stream().map(mapper::toOrderDto).toList());
    }

    @GetMapping(value = "/cashier", params = "pageable")
    @PreAuthorize("hasRole('ROLE_CASHIER')")
    public ResponseEntity<Page<OrderDto>> getOrdersForCashier(Pageable pageable) {
        Page<Order> allForCashier = service.findAllForCashier(pageable);
        return ResponseEntity.ok(allForCashier.map(mapper::toOrderDto));
    }

    @GetMapping("/cashier")
    @PreAuthorize("hasRole('ROLE_CASHIER')")
    public ResponseEntity<List<OrderDto>> getOrdersForCashier() {
        List<Order> allForCashier = service.findAllForCashier();
        return ResponseEntity.ok(allForCashier.stream().map(mapper::toOrderDto).toList());
    }

    @PatchMapping("/newState/cooking/{id}")
    @PreAuthorize("hasRole('ROLE_CASHIER')")
    public ResponseEntity<OrderDto> markAsCooking(@PathVariable UUID id) {
        Order order = service.changeState(id, Status.COOKING);
        OrderDto orderDto = mapper.toOrderDto(order);
        return ResponseEntity.ok(orderDto);
    }

    @PatchMapping("/newState/ready/{id}")
    @PreAuthorize("hasAnyRole('ROLE_CASHIER', 'ROLE_CHEF')")
    public ResponseEntity<OrderDto> markAsReady(@PathVariable UUID id) {
        Order order = service.changeState(id, Status.READY);
        OrderDto orderDto = mapper.toOrderDto(order);
        return ResponseEntity.ok(orderDto);
    }

    @PatchMapping("/newState/delivery/{id}")
    @PreAuthorize("hasRole('ROLE_CASHIER')")
    public ResponseEntity<OrderDto> markAsOnTheWay(@PathVariable UUID id) {
        Order order = service.changeState(id, Status.ON_THE_WAY);
        OrderDto orderDto = mapper.toOrderDto(order);
        return ResponseEntity.ok(orderDto);
    }

    @PatchMapping("/newState/delivered/{id}")
    @PreAuthorize("hasAnyRole('ROLE_CASHIER', 'ROLE_DELIVERY')")
    public ResponseEntity<OrderDto> markAsDelivered(@PathVariable UUID id) {
        Order order = service.changeState(id, Status.DELIVERED);
        OrderDto orderDto = mapper.toOrderDto(order);
        return ResponseEntity.ok(orderDto);
    }

}
