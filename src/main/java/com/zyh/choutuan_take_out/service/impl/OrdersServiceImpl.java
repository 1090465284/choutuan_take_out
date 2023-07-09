package com.zyh.choutuan_take_out.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zyh.choutuan_take_out.common.CustomException;
import com.zyh.choutuan_take_out.dto.OrdersDto;
import com.zyh.choutuan_take_out.entity.*;
import com.zyh.choutuan_take_out.mapper.OrdersMapper;
import com.zyh.choutuan_take_out.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Override
    public void submit(Orders orders, Long userId) {
        /**
         * 查询购物车数据
         * 向订单表插入一条数据
         * 向订单明细表插入多条数据
         * 清空购物车数据
         */
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId, userId);
        List<ShoppingCart> cartList = shoppingCartService.list(shoppingCartLambdaQueryWrapper);
        if(cartList == null || cartList.size() == 0){
            throw new CustomException("购物车为空");
        }

        User user = userService.getById(userId);

        AddressBook addressBook = addressBookService.getById(orders.getAddressBookId());
        if(addressBook == null){
            throw new CustomException("地址为空");
        }

        long orderId = IdWorker.getId();

        AtomicInteger amount = new AtomicInteger(0);

        List<OrderDetail> orderDetails = cartList.stream().map((item) -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());


        orders.setNumber(String.valueOf(orderId));
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get())); //总金额
        orders.setUserId(userId);
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress(addressBook.getDetail());

        this.save(orders);
        orderDetailService.saveBatch(orderDetails);
        shoppingCartService.remove(shoppingCartLambdaQueryWrapper);
    }

    @Override
    public Page<OrdersDto> getOrdersDtoPage(int page, int pageSize, Long userId) {
        Page<Orders> pageInfo = new Page<>(page, pageSize);
        Page<OrdersDto> pageRes = new Page<>();
        LambdaQueryWrapper<Orders> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(userId != null, Orders::getUserId, userId);
        lambdaQueryWrapper.orderByDesc(Orders::getOrderTime);
        this.page(pageInfo, lambdaQueryWrapper);
        BeanUtils.copyProperties(pageInfo, pageRes, "records");
        List<Orders> orders = pageInfo.getRecords();
        List<OrdersDto> collect = orders.stream().map((item) -> {
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(item, ordersDto);
            LambdaQueryWrapper<OrderDetail> orderDetailLambdaQueryWrapper = new LambdaQueryWrapper<>();
            orderDetailLambdaQueryWrapper.eq(OrderDetail::getOrderId, item.getNumber());
            List<OrderDetail> list = orderDetailService.list(orderDetailLambdaQueryWrapper);
            ordersDto.setOrderDetails(list);
            return ordersDto;
        }).collect(Collectors.toList());
        pageRes.setRecords(collect);
        return pageRes;
    }

    @Override
    public Page<OrdersDto> getOrdersDtoPage(int page, int pageSize) {
        return getOrdersDtoPage(page, pageSize, null);
    }
}
