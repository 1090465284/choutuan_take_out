package com.zyh.choutuan_take_out.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zyh.choutuan_take_out.common.R;
import com.zyh.choutuan_take_out.dto.SetmealDto;
import com.zyh.choutuan_take_out.entity.Category;
import com.zyh.choutuan_take_out.entity.Dish;
import com.zyh.choutuan_take_out.entity.Setmeal;
import com.zyh.choutuan_take_out.entity.SetmealDish;
import com.zyh.choutuan_take_out.service.CategoryService;
import com.zyh.choutuan_take_out.service.SetmealDishService;
import com.zyh.choutuan_take_out.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        setmealService.saveWithDish(setmealDto);
        return R.success("保存成功");
    }

    @GetMapping("page")
    public R<Page> getPage(int page, int pageSize, String name){
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        Page<SetmealDto> pageRes = new Page<>();

        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(name != null, Setmeal::getName, name);
        lambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(pageInfo, lambdaQueryWrapper);

        BeanUtils.copyProperties(pageInfo, pageRes, "records");
        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> collect = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            Category category = categoryService.getById(item.getCategoryId());
            BeanUtils.copyProperties(item, setmealDto);
            setmealDto.setCategoryName(category.getName());
            return setmealDto;
        }).collect(Collectors.toList());
        pageRes.setRecords(collect);
        return R.success(pageRes);

    }

    @DeleteMapping
    public R<String> delete(Long... ids){
        setmealService.removeWithDish(ids);
        return R.success("套餐删除成功");
    }

    @GetMapping("/{id}")
    public R<SetmealDto> getOne(@PathVariable Long id){
        SetmealDto setmealDto = setmealService.getWithDish(id);
        return R.success(setmealDto);

    }

    @PostMapping("/status/{status}")
    public R<String> changeStatus(@PathVariable int status, Long... ids){
        List<Setmeal> collect = Arrays.stream(ids).map((id) -> {
            Setmeal setmeal = new Setmeal();
            setmeal.setId(id);
            setmeal.setStatus(status);
            return setmeal;
        }).collect(Collectors.toList());
        setmealService.updateBatchById(collect);
        return R.success("修改成功");
    }

    @PutMapping
    public R<String> updateWithDish(@RequestBody SetmealDto setmealDto){
        setmealService.updateWithDish(setmealDto);
        return R.success("修改成功");
    }
    @GetMapping("/list")
    public R<List<Setmeal>> getList(@RequestParam("categoryId") Long id){
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(id != null, Setmeal::getCategoryId, id);
        lambdaQueryWrapper.eq(Setmeal::getStatus, 1);
        lambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> list = setmealService.list(lambdaQueryWrapper);
        return R.success(list);
    }
}
