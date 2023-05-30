package com.lacodigoneta.elbuensabor.mappers;

import com.lacodigoneta.elbuensabor.dto.orderdetails.ClientOrderDetailDto;
import com.lacodigoneta.elbuensabor.dto.orderdetails.OrderDetailDto;
import com.lacodigoneta.elbuensabor.entities.Order;
import com.lacodigoneta.elbuensabor.entities.OrderDetail;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class OrderDetailMapper {

    private final ModelMapper mapper;

    private final ProductMapper productMapper;

    public OrderDetailDto toOrderDetailDto(OrderDetail orderDetail) {
        OrderDetailDto orderDetailDto = mapper.map(orderDetail, OrderDetailDto.class);
        orderDetailDto.setProduct(productMapper.toProductForOrderDetailDto(orderDetail.getProduct()));
        return orderDetailDto;
    }

    public OrderDetail toOrderDetail(ClientOrderDetailDto clientOrderDetailDto, Order order) {
        OrderDetail orderDetail = mapper.map(clientOrderDetailDto, OrderDetail.class);
        orderDetail.setProduct(productMapper.toEntity(clientOrderDetailDto.getProduct()));
        orderDetail.setOrder(order);
        return orderDetail;
    }
}
