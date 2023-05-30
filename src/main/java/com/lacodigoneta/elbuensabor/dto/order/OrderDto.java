package com.lacodigoneta.elbuensabor.dto.order;

import com.lacodigoneta.elbuensabor.dto.address.AddressDto;
import com.lacodigoneta.elbuensabor.dto.orderdetails.OrderDetailDto;
import com.lacodigoneta.elbuensabor.dto.phonenumber.PhoneNumberDto;
import com.lacodigoneta.elbuensabor.dto.user.UserDto;
import com.lacodigoneta.elbuensabor.enums.DeliveryMethod;
import com.lacodigoneta.elbuensabor.enums.PaymentMethod;
import com.lacodigoneta.elbuensabor.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDto {

    private UUID id;

    private LocalDateTime dateTime;

    private UserDto user;

    private DeliveryMethod deliveryMethod;

    private AddressDto address;

    private PhoneNumberDto phoneNumber;

    private PaymentMethod paymentMethod;

    private boolean paid;

    private Status status;

    private BigDecimal discount;

    private List<OrderDetailDto> orderDetails;

    private int cookingTime;

    private int delayedMinutes;

    private int deliveryTime;

    private int totalTime;
}
