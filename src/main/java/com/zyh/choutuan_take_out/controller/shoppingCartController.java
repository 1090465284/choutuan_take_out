package com.zyh.choutuan_take_out.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zyh.choutuan_take_out.common.R;
import com.zyh.choutuan_take_out.entity.ShoppingCart;
import com.zyh.choutuan_take_out.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
public class shoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @GetMapping("/list")
    public R<List<ShoppingCart>> getList(HttpSession session){
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId, session.getAttribute("userId"));
        lambdaQueryWrapper.orderByDesc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(lambdaQueryWrapper);
        return R.success(list);
    }

    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart, HttpSession session){
        shoppingCart.setUserId((Long)session.getAttribute("userId"));
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId, shoppingCart.getUserId());
        lambdaQueryWrapper.eq(shoppingCart.getDishId() != null, ShoppingCart::getDishId, shoppingCart.getDishId());
//        lambdaQueryWrapper.eq(shoppingCart.getDishFlavor() != null, ShoppingCart::getDishFlavor, shoppingCart.getDishFlavor());
        lambdaQueryWrapper.eq(shoppingCart.getSetmealId() != null, ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        ShoppingCart one = shoppingCartService.getOne(lambdaQueryWrapper);
        if(one != null){
            one.setNumber(one.getNumber() + 1);
            shoppingCartService.updateById(one);
        }else{
            shoppingCart.setNumber(1);
            shoppingCartService.save(shoppingCart);
            one = shoppingCart;
        }
        return R.success(one);
    }
    @PostMapping("/sub")
    public R<String> sub(@RequestBody ShoppingCart shoppingCart, HttpSession session){
        Long dishId = shoppingCart.getDishId();
        Long setmealId = shoppingCart.getSetmealId();
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(dishId != null, ShoppingCart::getDishId, dishId);
        lambdaQueryWrapper.eq(setmealId != null, ShoppingCart::getSetmealId, setmealId);
        lambdaQueryWrapper.eq(ShoppingCart::getUserId, session.getAttribute("userId"));
        ShoppingCart one = shoppingCartService.getOne(lambdaQueryWrapper);
        if(one == null){
            return R.error("菜品不存在");
        }
        if (one.getNumber() > 1) {
            one.setNumber(one.getNumber() - 1);
            shoppingCartService.updateById(one);
        }else {
            shoppingCartService.removeById(one);
        }
        return R.success("成功");
    }

    @DeleteMapping("/clean")
    public R<String> clean(HttpSession session){
        Object userId = session.getAttribute("userId");
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId, userId);
        shoppingCartService.remove(lambdaQueryWrapper);
        return R.error("删除成功");
    }
}
