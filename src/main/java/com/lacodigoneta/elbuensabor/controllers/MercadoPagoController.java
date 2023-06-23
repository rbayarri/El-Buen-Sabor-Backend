package com.lacodigoneta.elbuensabor.controllers;

import com.lacodigoneta.elbuensabor.services.MercadoPagoService;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.UUID;

import static com.lacodigoneta.elbuensabor.config.AppConstants.ORIGIN_APP;

@RestController
@RequestMapping("/api/v1/mercadoPago")
@AllArgsConstructor
public class MercadoPagoController {

    private MercadoPagoService service;

    @PostMapping("/createPreference/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public Preference createPreference(@PathVariable UUID id) throws MPException, MPApiException {
        return service.createPreference(id);
    }

    @GetMapping("/success")
    public RedirectView success(HttpServletRequest request,
                                @RequestParam("collection_id") String collectionId,
                                @RequestParam("collection_status") String collectionStatus,
                                @RequestParam("payment_id") Long paymentId,
                                @RequestParam("status") String status,
                                @RequestParam("external_reference") String externalReference,
                                @RequestParam("payment_type") String paymentType,
                                @RequestParam("merchant_order_id") String merchantOrderId,
                                @RequestParam("preference_id") String preferenceId,
                                @RequestParam("site_id") String siteId,
                                @RequestParam("processing_mode") String processingMode,
                                @RequestParam("merchant_account_id") String merchantAccountId) {

        return service.processPayment(paymentId, preferenceId);
    }

    @GetMapping("/failure")
    public RedirectView failure(HttpServletRequest request,
                                @RequestParam("collection_id") String collectionId,
                                @RequestParam("collection_status") String collectionStatus,
                                @RequestParam("external_reference") String externalReference,
                                @RequestParam("payment_type") String paymentType,
                                @RequestParam("merchant_order_id") String merchantOrderId,
                                @RequestParam("preference_id") String preferenceId,
                                @RequestParam("site_id") String siteId,
                                @RequestParam("processing_mode") String processingMode,
                                @RequestParam("merchant_account_id") String merchantAccountId
    ) {

        return new RedirectView(ORIGIN_APP + "/error");
    }
}
