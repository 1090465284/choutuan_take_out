package com.zyh.choutuan_take_out.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zyh.choutuan_take_out.dto.DishDto;
import com.zyh.choutuan_take_out.entity.Dish;
import org.springframework.stereotype.Service;


public interface DishService extends IService<Dish> {
    void saveWithFlavor(DishDto dishDto);

    DishDto getByIdWithFlavor(Long id);

    void updateWithFlavor(DishDto dishDto);

    void removeWithFlavor(Long[] ids);

}
