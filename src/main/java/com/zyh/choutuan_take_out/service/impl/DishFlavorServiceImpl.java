package com.zyh.choutuan_take_out.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zyh.choutuan_take_out.entity.DishFlavor;
import com.zyh.choutuan_take_out.mapper.DishFlavorMapper;
import com.zyh.choutuan_take_out.service.DishFlavorService;
import com.zyh.choutuan_take_out.service.DishService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
