package com.zyh.choutuan_take_out.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zyh.choutuan_take_out.common.R;
import com.zyh.choutuan_take_out.dto.OrdersDto;
import com.zyh.choutuan_take_out.entity.Orders;
import com.zyh.choutuan_take_out.service.OrderDetailService;
import com.zyh.choutuan_take_out.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrdersService ordersService;

    @Autowired
    private OrderDetailService orderDetailService;

    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders, HttpSession session){
        Long userId = (Long)session.getAttribute("userId");
        ordersService.submit(orders, userId);

        return R.success("下单成功");
    }

    @GetMapping("/userPage")
    public R<Page<OrdersDto>> getPage(int page, int pageSize, HttpSession session){
        Object userId = session.getAttribute("userId");
        Page<OrdersDto> ordersDtoPage = ordersService.getOrdersDtoPage(page, pageSize, (Long) userId);
        return R.success(ordersDtoPage);
    }
    @GetMapping("/page")
    public R<Page<OrdersDto>> getPage(int page, int pageSize){
        Page<OrdersDto> ordersDtoPage = ordersService.getOrdersDtoPage(page, pageSize);
        return R.success(ordersDtoPage);
    }

    @PutMapping
    public R<String> changeStatus(@RequestBody Orders orders){
        ordersService.updateById(orders);
        return R.success("修改成功");
    }
}
