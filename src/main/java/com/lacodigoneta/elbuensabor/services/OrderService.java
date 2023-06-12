package com.lacodigoneta.elbuensabor.services;

import com.lacodigoneta.elbuensabor.entities.*;
import com.lacodigoneta.elbuensabor.enums.DeliveryMethod;
import com.lacodigoneta.elbuensabor.enums.PaymentMethod;
import com.lacodigoneta.elbuensabor.enums.Role;
import com.lacodigoneta.elbuensabor.enums.Status;
import com.lacodigoneta.elbuensabor.exceptions.AddressException;
import com.lacodigoneta.elbuensabor.exceptions.NotAllowedOperationException;
import com.lacodigoneta.elbuensabor.exceptions.PermissionsException;
import com.lacodigoneta.elbuensabor.exceptions.PhoneNumberException;
import com.lacodigoneta.elbuensabor.repositories.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static com.lacodigoneta.elbuensabor.config.AppConstants.*;

@Service
public class OrderService extends BaseServiceImpl<Order, OrderRepository> {

    private final UserService userService;

    private final ProductService productService;

    private final AddressService addressService;

    private final PhoneNumberService phoneNumberService;

    private final InvoiceService invoiceService;

    public OrderService(OrderRepository repository, UserService userService, ProductService productService, AddressService addressService, PhoneNumberService phoneNumberService, InvoiceService invoiceService) {
        super(repository);
        this.userService = userService;
        this.productService = productService;
        this.addressService = addressService;
        this.phoneNumberService = phoneNumberService;
        this.invoiceService = invoiceService;
    }

    public Page<Order> findAllByStatus(Status status, Pageable pageable) {
        return repository.findAllByStatusOrderByDateTimeAsc(status, pageable);
    }

    public List<Order> findAllByStatus(Status status) {
        return repository.findAllByStatusOrderByDateTimeAsc(status);
    }

    public Page<Order> findAllByUserId(UUID id, Pageable pageable) {
        User byId = userService.findById(id);
        return repository.findAllByUserOrderByDateTimeAsc(byId, pageable);
    }

    public List<Order> findAllByUserId(UUID id) {
        User byId = userService.findById(id);
        return repository.findAllByUserOrderByDateTimeAsc(byId);
    }

    public Page<Order> findMyOrders(Pageable pageable) {
        User user = userService.getLoggedUser();
        return repository.findAllByUserOrderByDateTimeAsc(user, pageable);
    }

    public List<Order> findMyOrders() {
        User user = userService.getLoggedUser();
        return repository.findAllByUserOrderByDateTimeAsc(user);
    }

    public Page<Order> findAllForCashier(Pageable pageable) {
        return repository.findAllByDateTimeBetweenOrderByDateTimeDesc(LocalDateTime.now().minusHours(6), LocalDateTime.now(), pageable);
    }

    public List<Order> findAllForCashier() {
        return repository.findAllByDateTimeBetweenOrderByDateTimeDesc(LocalDateTime.now().minusHours(6), LocalDateTime.now());
    }

    public Order findByPreferenceId(String preferenceId) {
        return repository.findByPreferenceId(preferenceId);
    }

    @Override
    public Order changeStates(Order source, Order destination) {
        return null;
    }

    @Override
    public void beforeSaveValidations(Order entity) {

        User user = userService.getLoggedUser();

        if (!user.isEmailConfirmed()) {
            throw new NotAllowedOperationException(UNCONFIRMED_EMAIL);
        }

        user.getOrders().add(entity);
        entity.setUser(user);

        if (Objects.nonNull(entity.getAddress())) {
            Address addressById = addressService.findById(entity.getAddress().getId());
            if (!addressById.getUser().equals(user)) {
                throw new PermissionsException();
            }
            if (!addressById.isActive()) {
                throw new AddressException();
            }
        }

        if (Objects.nonNull(entity.getPhoneNumber())) {
            PhoneNumber phoneNumberById = phoneNumberService.findById(entity.getPhoneNumber().getId());
            if (!phoneNumberById.getUser().equals(user)) {
                throw new PermissionsException();
            }
            if (!phoneNumberById.isActive()) {
                throw new PhoneNumberException();
            }
        }

        entity.setStatus(Status.PENDING);
        entity.setPaid(false);

        if (entity.getDeliveryMethod().equals(DeliveryMethod.HOME_DELIVERY) && entity.getPaymentMethod().equals(PaymentMethod.CASH)) {
            throw new NotAllowedOperationException(FORBIDDEN_OPERATION);
        }

        Map<Ingredient, BigDecimal> necessaryStock = new HashMap<>();

        for (OrderDetail orderDetail : entity.getOrderDetails()) {
            orderDetail.setProduct(productService.findById(orderDetail.getProduct().getId()));
        }

        entity.getOrderDetails().forEach(od -> {
            Integer quantity = od.getQuantity();
            od.getProduct().getProductDetails().forEach(pd -> {
                if (necessaryStock.containsKey(pd.getIngredient())) {
                    necessaryStock.replace(pd.getIngredient(), necessaryStock.get(pd.getIngredient()), necessaryStock.get(pd.getIngredient()).add(pd.getQuantity().multiply(BigDecimal.valueOf(quantity))));
                } else {
                    necessaryStock.put(pd.getIngredient(), pd.getQuantity().multiply(BigDecimal.valueOf(quantity)));
                }
            });
        });

        necessaryStock.forEach((ing, value) -> {
            if (ing.getCurrentStock().compareTo(value) < 0) {
                throw new NotAllowedOperationException(INSUFFICIENT_STOCK_FOR_ORDER);
            }
        });

        for (OrderDetail orderDetail : entity.getOrderDetails()) {
            orderDetail.setDiscount(BigDecimal.ZERO);
            orderDetail.setUnitPrice(orderDetail.getProduct().getPrice());
        }
    }

    @Override
    public Order completeEntity(Order order) {
        addCookingTime(order);
        addDiscount(order);
        return order;
    }

    private BigDecimal getOrderPrice(Order order) {
        return order.getOrderDetails().stream()
                .map(od -> od.getUnitPrice().multiply(BigDecimal.valueOf(od.getQuantity())).subtract(od.getDiscount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void addDiscount(Order order) {
        if (order.getDeliveryMethod().equals(DeliveryMethod.LOCAL_PICKUP)) {
            order.setDiscount(getOrderPrice(order).multiply(BigDecimal.valueOf(0.1)));
        }
    }

    private void addCookingTime(Order order) {

        int orderCookingTime = order.getOrderDetails().stream()
                .mapToInt(this::calculateCookingTimePerOrderDetail).sum();
        orderCookingTime += order.getDelayedMinutes();

        order.setCookingTime(orderCookingTime);

        if (order.getDeliveryMethod().equals(DeliveryMethod.HOME_DELIVERY)) {
            order.setDeliveryTime(10);
        }

        if (orderCookingTime != 0) {
            List<Order> cookingOrders = findAllByStatus(Status.COOKING);
            cookingOrders.forEach(this::completeEntity);
            int previousOrdersCookingTime = cookingOrders.stream()
                    .flatMap(o -> o.getOrderDetails().stream())
                    .mapToInt(this::calculateCookingTimePerOrderDetail)
                    .sum() + cookingOrders.stream()
                    .mapToInt(Order::getDelayedMinutes)
                    .sum();

            int chefs = userService.countUsersByRoleAndActiveTrue(Role.CHEF);
            order.setTotalTime((orderCookingTime + previousOrdersCookingTime) / chefs + order.getDeliveryTime());
        }
    }

    private int calculateCookingTimePerOrderDetail(OrderDetail orderDetail) {
        Integer cookingTime = orderDetail.getProduct().getCookingTime();
        if (cookingTime != 0) {
            cookingTime += 5 * (orderDetail.getQuantity() - 1);
        }
        return cookingTime;
    }

    @Transactional(rollbackOn = Exception.class)
    public Order changeState(UUID id, Status status) {

        Order order = findById(id);
        if (order.getStatus().ordinal() >= status.ordinal()) {
            throw new NotAllowedOperationException(FORBIDDEN_OPERATION);
        }

        if (status.equals(Status.COOKING)) {
            if (order.getCookingTime() == 0) {
                throw new NotAllowedOperationException(NO_PRODUCTS_TO_COOK);
            }
        }

        if (status.equals(Status.READY)) {
            if (order.getStatus().equals(Status.PENDING)) {
                if (order.getCookingTime() != 0) {
                    throw new NotAllowedOperationException(PRODUCTS_TO_COOK);
                }
            }
        }

        if (status.equals(Status.ON_THE_WAY)) {
            if (!order.getStatus().equals(Status.READY)) {
                throw new NotAllowedOperationException(FORBIDDEN_OPERATION);
            }
            if (!order.getDeliveryMethod().equals(DeliveryMethod.HOME_DELIVERY)) {
                throw new NotAllowedOperationException(LOCAL_PICK_UP);
            }
        }

        if (status.equals(Status.DELIVERED)) {
            if (!order.getStatus().equals(Status.ON_THE_WAY)) {
                throw new NotAllowedOperationException(FORBIDDEN_OPERATION);
            }
        }

        order.setStatus(status);
        return completeEntity(order);
    }

    @Transactional(rollbackOn = Exception.class)
    public Order pay(UUID id) {
        Order order = findById(id);
        if (order.isPaid()) {
            throw new RuntimeException("Orden ya pagada");
        }
        if (!order.getDeliveryMethod().equals(DeliveryMethod.LOCAL_PICKUP)) {
            throw new NotAllowedOperationException("El pago debe realizarse a través de Mercado Pago");
        }
        order.setPaid(true);
        Invoice invoice = invoiceService.createInvoice(order);
        order.setInvoice(invoice);
        //TODO: Generar pdf y mandar por mail
        return order;
    }

    @Transactional(rollbackOn = Exception.class)
    public Order cancel(UUID id) {
        Order order = findById(id);

        if (order.getStatus().equals(Status.CANCELLED)) {
            throw new NotAllowedOperationException("Orden ya cancelada");
        }

        if (order.isPaid()) {
            invoiceService.cancel(order);
            //generarPdfYMail(order.getInvoice().getCreditNote())
            // TODO: Hacer nota de crédito y mandar por mail
        }

        order.setStatus(Status.CANCELLED);
        return order;
    }
}
