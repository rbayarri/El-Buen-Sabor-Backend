package com.lacodigoneta.elbuensabor.entities;

import com.lacodigoneta.elbuensabor.enums.DeliveryMethod;
import com.lacodigoneta.elbuensabor.enums.PaymentMethod;
import com.lacodigoneta.elbuensabor.enums.Status;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "orders")
public class Order extends BaseEntity {

    private LocalDateTime dateTime;

    @ManyToOne(optional = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private DeliveryMethod deliveryMethod;

    @ManyToOne
    private Address address;

    @ManyToOne
    private PhoneNumber phoneNumber;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    private boolean paid;

    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToOne(mappedBy = "order")
    private Invoice invoice;

    @Transient
    private BigDecimal discount;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderDetail> orderDetails;

    @Transient
    private int cookingTime;

    private int delayedMinutes;

    @Transient
    private int deliveryTime;

    @Transient
    private int totalTime;

    @Column(unique = true)
    private String preferenceId;

    @Column(unique = true)
    private String paymentId;
}
