package com.zyh.choutuan_take_out.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zyh.choutuan_take_out.dto.OrdersDto;
import com.zyh.choutuan_take_out.entity.Orders;

public interface OrdersService extends IService<Orders> {
    void submit(Orders orders, Long userId);

    Page<OrdersDto> getOrdersDtoPage(int page, int pageSize, Long userId);

    Page<OrdersDto> getOrdersDtoPage(int page, int pageSize);
}
