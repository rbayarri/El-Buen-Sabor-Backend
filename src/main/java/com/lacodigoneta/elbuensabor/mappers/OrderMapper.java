package com.lacodigoneta.elbuensabor.mappers;

import com.lacodigoneta.elbuensabor.dto.order.ClientOrderDto;
import com.lacodigoneta.elbuensabor.dto.order.OrderDto;
import com.lacodigoneta.elbuensabor.dto.orderdetails.ClientOrderDetailDto;
import com.lacodigoneta.elbuensabor.dto.orderdetails.OrderDetailDto;
import com.lacodigoneta.elbuensabor.entities.Order;
import com.lacodigoneta.elbuensabor.entities.OrderDetail;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class OrderMapper {

    private final ModelMapper mapper;

    private final OrderDetailMapper orderDetailMapper;

    private final UserMapper userMapper;

    private AddressMapper addressMapper;

    private PhoneNumberMapper phoneNumberMapper;

    private ProductMapper productMapper;

    public Order toEntity(ClientOrderDto clientOrderDto) {
        Order order = mapper.map(clientOrderDto, Order.class);
        List<OrderDetail> orderDetails = clientOrderDto.getOrderDetails().stream().map(cod -> orderDetailMapper.toOrderDetail(cod, order)).toList();
        order.setOrderDetails(orderDetails);
        if (Objects.isNull(clientOrderDto.getDateTime()))
            order.setDateTime(LocalDateTime.now());
        if (Objects.nonNull(clientOrderDto.getAddress())) {
            order.setAddress(addressMapper.toEntity(clientOrderDto.getAddress()));
        }
        if (Objects.nonNull(clientOrderDto.getPhoneNumber())) {
            order.setPhoneNumber(phoneNumberMapper.toEntity(clientOrderDto.getPhoneNumber()));
        }
        return order;
    }

    public ClientOrderDto toClientOrderDto(Order order) {
        ClientOrderDto clientOrderDto = mapper.map(order, ClientOrderDto.class);
        List<ClientOrderDetailDto> clientOrderDetails = order.getOrderDetails().stream().map(this::toClientOrderDetailDto).toList();
        clientOrderDto.setOrderDetails(clientOrderDetails);
        if (Objects.nonNull(order.getAddress())) {
            clientOrderDto.setAddress(addressMapper.toSimpleAddressDto(order.getAddress()));
        }
        if (Objects.nonNull(order.getPhoneNumber())) {
            clientOrderDto.setPhoneNumber(phoneNumberMapper.toSimplePhoneNumberDto(order.getPhoneNumber()));
        }
        return clientOrderDto;
    }

    private ClientOrderDetailDto toClientOrderDetailDto(OrderDetail orderDetail) {
        ClientOrderDetailDto clientOrderDetailDto = mapper.map(orderDetail, ClientOrderDetailDto.class);
        clientOrderDetailDto.setProduct(productMapper.toSimpleProductDto(orderDetail.getProduct()));
        return clientOrderDetailDto;
    }

    public OrderDto toOrderDto(Order order) {
        OrderDto orderDto = mapper.map(order, OrderDto.class);
        orderDto.setUser(userMapper.toUserDto(order.getUser()));
        List<OrderDetailDto> orderDetailsDto = order.getOrderDetails().stream().map(orderDetailMapper::toOrderDetailDto).toList();
        orderDto.setOrderDetails(orderDetailsDto);
        if (Objects.nonNull(order.getAddress())) {
            orderDto.setAddress(addressMapper.toAddressDto(order.getAddress()));
        }
        if (Objects.nonNull(order.getPhoneNumber())) {
            orderDto.setPhoneNumber(phoneNumberMapper.toPhoneNumberDto(order.getPhoneNumber()));
        }
        return orderDto;
    }
}
