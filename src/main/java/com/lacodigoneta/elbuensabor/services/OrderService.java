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
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

    private final JavaMailService mailService;

    public OrderService(OrderRepository repository, UserService userService, ProductService productService, AddressService addressService, PhoneNumberService phoneNumberService, InvoiceService invoiceService, JavaMailService mailService) {
        super(repository);
        this.userService = userService;
        this.productService = productService;
        this.addressService = addressService;
        this.phoneNumberService = phoneNumberService;
        this.invoiceService = invoiceService;
        this.mailService = mailService;
    }

    public Page<Order> findAllByStatus(Status status, Pageable pageable) {
        return repository.findAllByStatusOrderByDateTimeAsc(status, pageable).map(this::completeEntity);
    }

    public List<Order> findAllByStatus(Status status) {
        return repository.findAllByStatusOrderByDateTimeAsc(status).stream().map(this::completeEntity).toList();
    }

    public List<Order> findAllByStatusExceptOrderById(Status status, UUID idOrderExcluded) {
        return repository.findAllByStatusOrderByDateTimeAsc(status).stream()
                .filter(o -> !o.getId().equals(idOrderExcluded))
                .map(this::completeEntity).toList();
    }

    public Order findFirstByStatusAndDateTimeBefore(Status status, LocalDateTime to) {
        Order firstOrder = repository.findFirstByStatusAndDateTimeBeforeOrderByDateTimeDesc(status, to).orElse(null);
        if (Objects.nonNull(firstOrder)) {
            return completeEntity(firstOrder);
        }
        return null;
    }

    public Page<Order> findAllByUserId(UUID id, Pageable pageable) {
        User byId = userService.findById(id);
        return repository.findAllByUserOrderByDateTimeAsc(byId, pageable).map(this::completeEntity);
    }

    public List<Order> findAllByUserId(UUID id) {
        User byId = userService.findById(id);
        return repository.findAllByUserOrderByDateTimeAsc(byId).stream().map(this::completeEntity).toList();
    }

    public Page<Order> findMyOrders(Pageable pageable) {
        User user = userService.getLoggedUser();
        return repository.findAllByUserOrderByDateTimeAsc(user, pageable).map(this::completeEntity);
    }

    public List<Order> findMyOrders() {
        User user = userService.getLoggedUser();
        return repository.findAllByUserOrderByDateTimeAsc(user).stream().map(this::completeEntity).toList();
    }

    public Order findOrderById(UUID id) {
        User user = userService.getLoggedUser();
        Order order = findById(id);
        if (!order.getUser().equals(user) && user.getRole().equals(Role.USER)) {
            throw new PermissionsException();
        }
        return order;
    }

    public Page<Order> findAllForCashier(Pageable pageable) {
        return repository.findAllByDateTimeBetweenOrderByDateTimeDesc(LocalDateTime.now().minusHours(6), LocalDateTime.now(), pageable).map(this::completeEntity);
    }

    public List<Order> findAllForCashier() {
        List<Order> orders = repository.findAllByDateTimeBetweenOrderByDateTimeDesc(LocalDateTime.now().minusHours(6), LocalDateTime.now());
        return orders.stream().filter(o -> !(o.getPaymentMethod().equals(PaymentMethod.MERCADO_PAGO) && !o.isPaid())).map(this::completeEntity).toList();
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

        if (entity.getDeliveryMethod().equals(DeliveryMethod.HOME_DELIVERY)) {
            if (Objects.isNull(entity.getAddress()) || Objects.isNull(entity.getPhoneNumber())) {
                throw new NotAllowedOperationException("Para envíos a domicilio debe indicar información de contacto");
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
            orderDetail.setUnitCost(orderDetail.getProduct().getPrice().divide(orderDetail.getProduct().getProfitMargin().add(BigDecimal.ONE), 10, RoundingMode.UP));
        }
    }

    @Override
    public Order completeEntity(Order order) {
        if (!order.getStatus().equals(Status.CANCELLED) && !order.getStatus().equals(Status.DELIVERED)) {
            addCookingTime(order);
        }
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

        int chefs = userService.countUsersByRoleAndActiveTrue(Role.CHEF);
        order.setCookingTime(orderCookingTime / chefs + order.getDelayedMinutes());

        if (order.getDeliveryMethod().equals(DeliveryMethod.HOME_DELIVERY)) {
            order.setDeliveryTime(10);
        }

        if (orderCookingTime != 0) {

            int previousOrderCookingTime = 0;
            Order previousCookingOrder = findFirstByStatusAndDateTimeBefore(Status.COOKING, order.getDateTime());
            if (Objects.nonNull(previousCookingOrder)) {
                previousOrderCookingTime = previousCookingOrder.getCookingTime();
            }

            order.setCookingTime(order.getCookingTime() + previousOrderCookingTime);
            order.setTotalTime(order.getCookingTime() + order.getDeliveryTime());
        } else {
            order.setTotalTime(order.getDeliveryTime());
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
        User user = userService.getLoggedUser();

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
                if (!(order.getStatus().equals(Status.READY) && user.getRole().equals(Role.CASHIER))) {
                    throw new NotAllowedOperationException(FORBIDDEN_OPERATION);
                }
            }
        }

        order.setStatus(status);
        return order;
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
        return order;
    }

    @Transactional(rollbackOn = Exception.class)
    public Order registerPayment(Long paymentId, String preferenceId) {

        Order order = findByPreferenceId(preferenceId);
        if (Objects.isNull(order)) {
            throw new RuntimeException("No existe orden");
        }
        if (order.isPaid() || Objects.nonNull(order.getPaymentId())) {
            throw new RuntimeException("Orden ya pagada");
        }
        order.setPaymentId(String.valueOf(paymentId));
        order.setPaid(true);
        Invoice invoice = invoiceService.createInvoice(order);
        order.setInvoice(invoice);
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
        }
        order.setStatus(Status.CANCELLED);
        return order;
    }

    public void getInvoicePdf(UUID id, HttpServletResponse response) {
        Order order = findById(id);
        String headerkey = "Content-Disposition";
        String headervalue = "attachment; filename=Factura" + ".pdf";
        response.setHeader(headerkey, headervalue);
        PdfGenerator.generateResponse(response, order, true);
    }

    public void getCreditNotePdf(UUID id, HttpServletResponse response) {
        Order order = findById(id);
        String headerkey = "Content-Disposition";
        String headervalue = "attachment; filename=Nota_de_Credito" + ".pdf";
        response.setHeader(headerkey, headervalue);
        PdfGenerator.generateResponse(response, order, false);
    }

    public void generatePdfAndSendMail(Order order, boolean invoice) {
        ByteArrayResource pdf = PdfGenerator.generate(order, invoice);
        String target = invoice ? "Factura" : "Nota de crédito";
        mailService.sendWithAttach("lacodigoneta@gmail.com",
                order.getUser().getUsername(),
                target,
                "Adjuntamos tu " + target.toLowerCase() + " como archivo adjunto",
                target.replace(" ", "_").replace("é", "e") + ".pdf",
                pdf);
    }

    @Transactional(rollbackOn = Exception.class)
    public Order add10Minutes(UUID id) {
        Order order = findById(id);
        order.setDelayedMinutes(order.getDelayedMinutes() + 10);
        return order;
    }
}
