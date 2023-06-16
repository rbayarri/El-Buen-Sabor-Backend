package com.lacodigoneta.elbuensabor.services;

import com.lacodigoneta.elbuensabor.dto.order.ProfitReport;
import com.lacodigoneta.elbuensabor.dto.order.RankingClients;
import com.lacodigoneta.elbuensabor.dto.order.RankingProduct;
import com.lacodigoneta.elbuensabor.entities.Order;
import com.lacodigoneta.elbuensabor.entities.OrderDetail;
import com.lacodigoneta.elbuensabor.enums.Status;
import com.lacodigoneta.elbuensabor.repositories.OrderRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
public class ReportsService {

    private final UserService userService;

    private final ProductService productService;

    private final OrderRepository orderRepository;

    public ReportsService(UserService userService, ProductService productService, OrderRepository orderRepository) {
        this.userService = userService;
        this.productService = productService;
        this.orderRepository = orderRepository;
    }

    public List<RankingProduct> getRankingProducts(LocalDate from, LocalDate to, int quantityRegisters) {

        if (from.isAfter(to)) {
            throw new RuntimeException("La fecha desde debe ser posterior a la fecha hasta");
        }

        return productService.findAll().stream().map(p -> RankingProduct.builder()
                        .productName(p.getName())
                        .cookingTime(p.getCookingTime())
                        .quantity(p.getOrderDetails().stream()
                                .filter(o -> !o.getOrder().getStatus().equals(Status.CANCELLED))
                                .filter(o -> o.getOrder().getDateTime().toLocalDate().isBefore(to))
                                .filter(o -> o.getOrder().getDateTime().toLocalDate().isAfter(from))
                                .mapToInt(OrderDetail::getQuantity)
                                .sum())
                        .build())
                .filter(r -> r.getQuantity() > 0)
                .sorted(Comparator.comparingInt(RankingProduct::getQuantity).reversed())
                .limit(quantityRegisters)
                .toList();

    }

    public List<RankingClients> getRankingClients(LocalDate from, LocalDate to, int quantityRegisters, boolean byQuantity) {

        if (from.isAfter(to)) {
            throw new RuntimeException("La fecha desde debe ser posterior a la fecha hasta");
        }

        return userService.findAll().stream().map(u -> RankingClients.builder()
                        .id(u.getId())
                        .name(u.getName())
                        .lastName(u.getLastName())
                        .quantity(u.getOrders().stream()
                                .filter(o -> !o.getStatus().equals(Status.CANCELLED))
                                .filter(o -> o.getDateTime().toLocalDate().isBefore(to.plusDays(1)))
                                .filter(o -> o.getDateTime().toLocalDate().isAfter(from.minusDays(1)))
                                .flatMap(o -> o.getOrderDetails().stream())
                                .mapToInt(OrderDetail::getQuantity)
                                .sum())
                        .total(u.getOrders().stream()
                                .filter(o -> !o.getStatus().equals(Status.CANCELLED))
                                .filter(o -> o.getDateTime().toLocalDate().isBefore(to.plusDays(1)))
                                .filter(o -> o.getDateTime().toLocalDate().isAfter(from.minusDays(1)))
                                .flatMap(o -> o.getOrderDetails().stream())
                                .map(od -> od.getUnitPrice().multiply(BigDecimal.valueOf(od.getQuantity())))
                                .reduce(BigDecimal.ZERO, BigDecimal::add))
                        .build())
                .filter(r -> r.getQuantity() > 0)
                .sorted(byQuantity ? Comparator.comparingInt(RankingClients::getQuantity).reversed() :
                        Comparator.comparing(RankingClients::getTotal).reversed())
                .limit(quantityRegisters)
                .toList();
    }

    public ProfitReport getProfit(LocalDate from, LocalDate to) {

        if (from.isAfter(to)) {
            throw new RuntimeException("La fecha desde debe ser posterior a la fecha hasta");
        }

        List<Order> orders = orderRepository.findAllByDateTimeBetweenOrderByDateTimeDesc(
                from.atStartOfDay(), to.atTime(23, 59, 59, 999_999_999));

        BigDecimal cost = BigDecimal.ZERO;
        BigDecimal profit = BigDecimal.ZERO;

        for (Order order : orders) {
            if (!order.getStatus().equals(Status.CANCELLED)) {
                for (OrderDetail orderDetail : order.getOrderDetails()) {
                    cost = cost.add(orderDetail.getUnitCost().multiply(BigDecimal.valueOf(orderDetail.getQuantity())));
                    profit = profit.add(orderDetail.getUnitPrice().subtract(orderDetail.getUnitCost()).multiply(BigDecimal.valueOf(orderDetail.getQuantity())));
                }
            }
        }
        return ProfitReport.builder()
                .profits(profit)
                .costs(cost)
                .build();
    }
}
