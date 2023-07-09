package com.zyh.choutuan_take_out.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zyh.choutuan_take_out.common.CustomException;
import com.zyh.choutuan_take_out.dto.DishDto;
import com.zyh.choutuan_take_out.entity.Dish;
import com.zyh.choutuan_take_out.entity.DishFlavor;
import com.zyh.choutuan_take_out.entity.Setmeal;
import com.zyh.choutuan_take_out.entity.SetmealDish;
import com.zyh.choutuan_take_out.mapper.DishMapper;
import com.zyh.choutuan_take_out.service.DishFlavorService;
import com.zyh.choutuan_take_out.service.DishService;
import com.zyh.choutuan_take_out.service.SetmealDishService;
import com.zyh.choutuan_take_out.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Override

    public void saveWithFlavor(DishDto dishDto) {
        this.save(dishDto);
        Long id = dishDto.getId();
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.forEach((item)->item.setDishId(id));
//        System.out.println(flavors);
        dishFlavorService.saveBatch(flavors);
    }

    @Override
    public DishDto getByIdWithFlavor(Long id) {
        Dish dish = this.getById(id);
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DishFlavor::getDishId, id);
        List<DishFlavor> flavors = dishFlavorService.list(lambdaQueryWrapper);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);
        dishDto.setFlavors(flavors);
        return dishDto;
    }

    @Override
    public void updateWithFlavor(DishDto dishDto) {

        this.updateById(dishDto);

        //口味先清理再添加
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(lambdaQueryWrapper);

        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.forEach((item)->item.setDishId(dishDto.getId()));
        dishFlavorService.saveBatch(flavors);
    }

    @Override
    public void removeWithFlavor(Long[] ids) {
        LambdaQueryWrapper<Dish> statusQueryWrapper = new LambdaQueryWrapper<>();
        statusQueryWrapper.in(Dish::getId, ids);
        statusQueryWrapper.eq(Dish::getStatus, 1);
        int count = this.count(statusQueryWrapper);
        if(count > 0){
            throw new CustomException("菜品正在售卖中,不能删除");
        }
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.in(SetmealDish::getDishId, ids);
        int count1 = setmealDishService.count(setmealDishLambdaQueryWrapper);
        if(count1 > 0){
            throw new CustomException("菜品在套餐中,请先删除套餐再删除菜品");
        }
        LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorLambdaQueryWrapper.in(DishFlavor::getDishId, ids);
        dishFlavorService.remove(dishFlavorLambdaQueryWrapper);
        this.removeByIds(Arrays.asList(ids));
    }

}
