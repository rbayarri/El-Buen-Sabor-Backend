package com.lacodigoneta.elbuensabor.services;

import com.lacodigoneta.elbuensabor.entities.OrderDetail;
import com.lacodigoneta.elbuensabor.repositories.OrderDetailRepository;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailService extends BaseServiceImpl<OrderDetail, OrderDetailRepository> {

    public OrderDetailService(OrderDetailRepository repository) {
        super(repository);
    }

    @Override
    public OrderDetail changeStates(OrderDetail source, OrderDetail destination) {
        return null;
    }

    @Override
    public void beforeSaveValidations(OrderDetail entity) {
    }

}
