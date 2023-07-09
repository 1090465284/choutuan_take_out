package com.zyh.choutuan_take_out.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zyh.choutuan_take_out.common.CustomException;
import com.zyh.choutuan_take_out.entity.Category;
import com.zyh.choutuan_take_out.entity.Dish;
import com.zyh.choutuan_take_out.entity.Setmeal;
import com.zyh.choutuan_take_out.mapper.CategoryMapper;
import com.zyh.choutuan_take_out.service.CategoryService;
import com.zyh.choutuan_take_out.service.DishService;
import com.zyh.choutuan_take_out.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    @Override
    public boolean removeCategory(Long id) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);
        int dishCount = dishService.count(dishLambdaQueryWrapper);
        if(dishCount > 0){
            throw new CustomException("当前分类下关联了菜品,不能删除");
        }

        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, id);
        int setmealCount = setmealService.count(setmealLambdaQueryWrapper);
        if(setmealCount > 0){
            throw new CustomException("当前分类下关联了套餐,不能删除");
        }
        return super.removeById(id);
    }
}
