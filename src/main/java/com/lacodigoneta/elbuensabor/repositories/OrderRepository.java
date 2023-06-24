package com.lacodigoneta.elbuensabor.repositories;

import com.lacodigoneta.elbuensabor.entities.Order;
import com.lacodigoneta.elbuensabor.entities.User;
import com.lacodigoneta.elbuensabor.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    List<Order> findAllByStatusOrderByDateTimeAsc(Status status);

    Page<Order> findAllByStatusOrderByDateTimeAsc(Status status, Pageable pageable);

    List<Order> findAllByUserOrderByDateTimeAsc(User user);

    Page<Order> findAllByUserOrderByDateTimeAsc(User user, Pageable pageable);

    List<Order> findAllByDateTimeBetweenOrderByDateTimeDesc(LocalDateTime from, LocalDateTime to);

    Page<Order> findAllByDateTimeBetweenOrderByDateTimeDesc(LocalDateTime from, LocalDateTime to, Pageable pageable);

    Order findByPreferenceId(String preferenceId);

    Optional<Order> findFirstByStatusAndDateTimeBeforeOrderByDateTimeDesc(Status status, LocalDateTime to);
}