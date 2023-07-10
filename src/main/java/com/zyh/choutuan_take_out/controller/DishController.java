package com.zyh.choutuan_take_out.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zyh.choutuan_take_out.common.R;
import com.zyh.choutuan_take_out.dto.DishDto;
import com.zyh.choutuan_take_out.entity.Category;
import com.zyh.choutuan_take_out.entity.Dish;
import com.zyh.choutuan_take_out.service.CategoryService;
import com.zyh.choutuan_take_out.service.DishFlavorService;
import com.zyh.choutuan_take_out.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @Resource
    private RedisTemplate redisTemplate;

    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        dishService.saveWithFlavor(dishDto);
        String key = "dish_" + dishDto.getCategoryId();
        redisTemplate.delete(key);
        return R.success("新增菜品成功");
    }

    @GetMapping("/page")
    public R<Page> getPage(int page, int pageSize, String name){
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> pageRes = new Page<>();
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(name != null){
            lambdaQueryWrapper.like(Dish::getName, name);
        }
        lambdaQueryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        dishService.page(pageInfo, lambdaQueryWrapper);
        BeanUtils.copyProperties(pageInfo, pageRes, "records");
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> collect = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Category category = categoryService.getById(item.getCategoryId());
            dishDto.setCategoryName(category.getName());
            return dishDto;
        }).collect(Collectors.toList());
        pageRes.setRecords(collect);
        return R.success(pageRes);
    }

    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        dishService.updateWithFlavor(dishDto);
        String key = "dish_" + dishDto.getCategoryId();
        redisTemplate.delete(key);
        return R.success("修改成功");
    }

//    @GetMapping("/list")
//    public R<List<Dish>> getDishList(Long categoryId){
//        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//        lambdaQueryWrapper.eq(Dish::getCategoryId, categoryId);
//        //起售状态
//        lambdaQueryWrapper.eq(Dish::getStatus, 1);
//        lambdaQueryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//        List<Dish> list = dishService.list(lambdaQueryWrapper);
//        return R.success(list);
//    }

    @GetMapping("/list")
    public R<List<DishDto>> getDishList(Long categoryId){
        /**
         * 从redis中获取缓存数据
         * 存在则直接返回
         * 不存在再查询数据库
         */
        String redisKey = "dish_" + categoryId.toString();
        List<DishDto> redisDish = (List<DishDto>)redisTemplate.opsForValue().get(redisKey);
        if(redisDish != null){
            return R.success(redisDish);
        }


        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Dish::getCategoryId, categoryId);
        //起售状态
        lambdaQueryWrapper.eq(Dish::getStatus, 1);
        lambdaQueryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(lambdaQueryWrapper);
        List<DishDto> collect = list.stream().map((item) ->
            dishService.getByIdWithFlavor(item.getId())
        ).collect(Collectors.toList());
        redisTemplate.opsForValue().set(redisKey, collect, 60, TimeUnit.MINUTES);
        return R.success(collect);
    }

    @DeleteMapping
    public R<String> delete(Long... ids){
        dishService.removeWithFlavor(ids);
        return R.success("删除成功");
    }

    @PostMapping("/status/{status}")
    public R<String> changeStatus(@PathVariable int status, Long... ids){
        List<Dish> collect = Arrays.stream(ids).map((id) -> {
            Dish dish = new Dish();
            dish.setId(id);
            dish.setStatus(status);
            return dish;
        }).collect(Collectors.toList());
        dishService.updateBatchById(collect);
        return R.success("修改成功");
    }
}
