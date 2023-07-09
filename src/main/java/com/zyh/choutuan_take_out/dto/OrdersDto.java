package com.zyh.choutuan_take_out.dto;


import com.zyh.choutuan_take_out.entity.OrderDetail;
import com.zyh.choutuan_take_out.entity.Orders;
import lombok.Data;
import java.util.List;

@Data
public class OrdersDto extends Orders {

    private String userName;

    private String phone;

    private String address;

    private String consignee;

    private List<OrderDetail> orderDetails;
	
}
