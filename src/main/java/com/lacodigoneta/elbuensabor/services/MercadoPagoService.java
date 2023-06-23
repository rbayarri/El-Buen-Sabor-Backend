package com.lacodigoneta.elbuensabor.services;

import com.lacodigoneta.elbuensabor.entities.Order;
import com.lacodigoneta.elbuensabor.entities.OrderDetail;
import com.lacodigoneta.elbuensabor.entities.User;
import com.lacodigoneta.elbuensabor.enums.DeliveryMethod;
import com.lacodigoneta.elbuensabor.enums.PaymentMethod;
import com.lacodigoneta.elbuensabor.exceptions.NotAllowedOperationException;
import com.lacodigoneta.elbuensabor.exceptions.PermissionsException;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.*;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.servlet.view.RedirectView;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.lacodigoneta.elbuensabor.config.AppConstants.ORIGIN_APP;

@Service
@AllArgsConstructor
public class MercadoPagoService {

    private OrderService orderService;

    private UserService userService;

    @Transactional(rollbackOn = Exception.class)
    public Preference createPreference(UUID orderId) throws MPException, MPApiException {
        User loggedUser = userService.getLoggedUser();
        Order order = orderService.findById(orderId);

        if (Objects.isNull(order)) {
            throw new RuntimeException("Orden no encontrada");
        }
        if (!order.getUser().equals(loggedUser)) {
            throw new PermissionsException();
        }
        if (!order.getPaymentMethod().equals(PaymentMethod.MERCADO_PAGO)) {
            throw new NotAllowedOperationException("La orden debe ser pagada en efectivo");
        }
        if (order.isPaid()) {
            throw new NotAllowedOperationException("La orden ya se encuentra pagada");
        }

        List<PreferenceItemRequest> items = new ArrayList<>();
        for (OrderDetail orderDetail : order.getOrderDetails()) {
            items.add(PreferenceItemRequest.builder()
                    .title(orderDetail.getProduct().getName())
                    .quantity(orderDetail.getQuantity())
                    .unitPrice(orderDetail.getUnitPrice().subtract(orderDetail.getDiscount()))
                    .pictureUrl(orderDetail.getProduct().getImage().getLocation())
                    .build());
        }
        if (order.getDeliveryMethod().equals(DeliveryMethod.LOCAL_PICKUP)) {
            items.add(PreferenceItemRequest.builder()
                    .title("Descuento por retiro en local")
                    .quantity(1)
                    .unitPrice(order.getDiscount().negate())
                    .build());
        }

        PreferenceBackUrlsRequest preferenceBackUrlsRequest = PreferenceBackUrlsRequest.builder()
                .success(ServletUriComponentsBuilder
                        .fromCurrentContextPath()
                        .path("/api/v1/mercadoPago/success")
                        .build()
                        .toUri().toString())
                .failure(ORIGIN_APP + "/error/Ocurrio un error")
                .build();

        PreferenceClient preferencieClient = new PreferenceClient();
        Preference preference = preferencieClient.create(PreferenceRequest.builder()
                .items(items)
                .backUrls(preferenceBackUrlsRequest)
                .autoReturn("approved")
                .expirationDateTo(OffsetDateTime.now().plusMinutes(5))
                .paymentMethods(PreferencePaymentMethodsRequest.builder()
                        .excludedPaymentTypes(List.of(PreferencePaymentTypeRequest.builder()
                                .id("ticket")
                                .build()))
                        .build())
                .build());

        order.setPreferenceId(preference.getId());
        return preference;
    }

    public RedirectView processPayment(Long paymentId, String preferenceId) {
        try {
            PaymentClient client = new PaymentClient();
            Payment payment = client.get(paymentId);

            if (Objects.isNull(payment)) {
                throw new RuntimeException("Pago no encontrado");
            }

            if (!payment.getStatus().equals("approved")) {
                throw new RuntimeException("Pago no aprobado");
            }
            Order order = orderService.registerPayment(paymentId, preferenceId);
            orderService.generatePdfAndSendMail(order, true);
            return redirect(order.getId());
        } catch (Exception e) {
            return new RedirectView(ORIGIN_APP + "/error/" + e.getMessage());
        }
    }

    private RedirectView redirect(UUID id) {
        String redirectUrl = ORIGIN_APP + "/pedidos/" + id;
        return new RedirectView(redirectUrl);
    }
}
