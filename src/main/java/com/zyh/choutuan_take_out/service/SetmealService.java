package com.zyh.choutuan_take_out.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zyh.choutuan_take_out.dto.DishDto;
import com.zyh.choutuan_take_out.dto.SetmealDto;
import com.zyh.choutuan_take_out.entity.Setmeal;
import org.springframework.stereotype.Service;


public interface SetmealService extends IService<Setmeal> {
    void saveWithDish(SetmealDto setmealDto);

    void removeWithDish(Long[] ids);

    SetmealDto getWithDish(Long id);

    void updateWithDish(SetmealDto setmealDto);
}
