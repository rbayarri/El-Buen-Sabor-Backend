package com.lacodigoneta.elbuensabor.controllers;

import com.lacodigoneta.elbuensabor.dto.order.ClientOrderDto;
import com.lacodigoneta.elbuensabor.dto.order.OrderDto;
import com.lacodigoneta.elbuensabor.entities.Order;
import com.lacodigoneta.elbuensabor.enums.Status;
import com.lacodigoneta.elbuensabor.mappers.OrderMapper;
import com.lacodigoneta.elbuensabor.services.OrderService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
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

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<OrderDto>> getAll() {
        List<Order> all = service.findAll();
        return ResponseEntity.ok(all.stream().map(mapper::toOrderDto).toList());
    }

    @GetMapping("/admin/user/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<OrderDto>> getAllByUserId(@PathVariable UUID id) {
        List<Order> allByUserId = service.findAllByUserId(id);
        return ResponseEntity.ok(allByUserId.stream().map(mapper::toOrderDto).toList());
    }

    @GetMapping("")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<List<ClientOrderDto>> getMyOrders() {
        List<Order> myOrders = service.findMyOrders();
        return ResponseEntity.ok(myOrders.stream().map(mapper::toClientOrderDto).toList());
    }

    @GetMapping("/{id}/users")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ClientOrderDto> getOrderByIdForUser(@PathVariable UUID id) {
        return ResponseEntity.ok(mapper.toClientOrderDto(service.findOrderById(id)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_CASHIER','ROLE_CHEF','ROLE_DELIVERY')")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable UUID id) {
        return ResponseEntity.ok(mapper.toOrderDto(service.findOrderById(id)));
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

    @GetMapping("/cooking")
    @PreAuthorize("hasRole('ROLE_CHEF')")
    public ResponseEntity<List<OrderDto>> getCookingOrders() {
        List<Order> cookingOrders = service.findAllByStatus(Status.COOKING);
        return ResponseEntity.ok(cookingOrders.stream().map(mapper::toOrderDto).toList());
    }

    @GetMapping("/delivery")
    @PreAuthorize("hasRole('ROLE_DELIVERY')")
    public ResponseEntity<List<OrderDto>> getOrdersToDeliver() {
        List<Order> ordersToDeliver = service.findAllByStatus(Status.ON_THE_WAY);
        return ResponseEntity.ok(ordersToDeliver.stream().map(mapper::toOrderDto).toList());
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

    @PatchMapping("/registerPayment/{id}")
    @PreAuthorize("hasRole('ROLE_CASHIER')")
    public ResponseEntity<OrderDto> markAsPaid(@PathVariable UUID id) {
        Order order = service.pay(id);
        service.generatePdfAndSendMail(order, true);
        OrderDto orderDto = mapper.toOrderDto(order);
        return ResponseEntity.ok(orderDto);
    }

    @PostMapping("/cancel/{id}")
    @PreAuthorize("hasAnyRole('ROLE_CASHIER', 'ROLE_USER')")
    public ResponseEntity<OrderDto> cancel(@PathVariable UUID id) {
        Order order = service.cancel(id);
        if (order.isPaid()) {
            service.generatePdfAndSendMail(order, false);
        }
        OrderDto orderDto = mapper.toOrderDto(order);
        return ResponseEntity.ok(orderDto);
    }

    @PatchMapping("/addMinutes/{id}")
    @PreAuthorize("hasRole('ROLE_CHEF')")
    public ResponseEntity<OrderDto> addMinutes(@PathVariable UUID id) {
        Order order = service.add10Minutes(id);
        return ResponseEntity.ok(mapper.toOrderDto(order));
    }

    @GetMapping(value = "/viewInvoice/{id}", produces = {MediaType.APPLICATION_PDF_VALUE})
    public void getPdf(@PathVariable UUID id, HttpServletResponse response) {
        service.getInvoicePdf(id, response);
    }

    @GetMapping(value = "/viewCreditNote/{id}", produces = {MediaType.APPLICATION_PDF_VALUE})
    public void getPdfCreditNote(@PathVariable UUID id, HttpServletResponse response) {
        service.getCreditNotePdf(id, response);
    }
}
