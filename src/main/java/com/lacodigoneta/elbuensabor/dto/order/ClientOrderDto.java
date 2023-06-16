package com.lacodigoneta.elbuensabor.dto.order;

import com.lacodigoneta.elbuensabor.dto.address.SimpleAddressDto;
import com.lacodigoneta.elbuensabor.dto.orderdetails.ClientOrderDetailDto;
import com.lacodigoneta.elbuensabor.dto.phonenumber.SimplePhoneNumberDto;
import com.lacodigoneta.elbuensabor.enums.DeliveryMethod;
import com.lacodigoneta.elbuensabor.enums.PaymentMethod;
import com.lacodigoneta.elbuensabor.enums.Status;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.lacodigoneta.elbuensabor.config.AppConstants.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClientOrderDto {

    @Null(message = NULL_VALIDATION_MESSAGE)
    private UUID id;

    @Null(message = NULL_VALIDATION_MESSAGE)
    private LocalDateTime dateTime;

    @NotNull(message = NOT_NULL_VALIDATION_MESSAGE)
    private DeliveryMethod deliveryMethod;

    private SimpleAddressDto address;

    private SimplePhoneNumberDto phoneNumber;

    @NotNull(message = NOT_NULL_VALIDATION_MESSAGE)
    private PaymentMethod paymentMethod;

    @Null(message = NULL_VALIDATION_MESSAGE)
    private Boolean paid;

    @Null(message = NULL_VALIDATION_MESSAGE)
    private Status status;

    @Null(message = NULL_VALIDATION_MESSAGE)
    @PositiveOrZero(message = POSITIVE_OR_ZERO_VALIDATION_MESSAGE)
    private BigDecimal discount;

    @NotNull(message = NOT_NULL_VALIDATION_MESSAGE)
    private List<ClientOrderDetailDto> orderDetails;

    @Null(message = NULL_VALIDATION_MESSAGE)
    private Integer totalTime;
}
