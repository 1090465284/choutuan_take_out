package com.zyh.choutuan_take_out.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.zyh.choutuan_take_out.common.BaseContext;
import com.zyh.choutuan_take_out.common.R;
import com.zyh.choutuan_take_out.entity.AddressBook;
import com.zyh.choutuan_take_out.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/addressBook")
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;

    @PostMapping
    public R<String> save(@RequestBody AddressBook addressBook, HttpSession session){
        addressBook.setUserId((Long) session.getAttribute("userId"));
        addressBookService.save(addressBook);
        return R.success("保存成功");
    }

    @GetMapping("/list")
    public R<List<AddressBook>> getList(HttpSession session){
        LambdaQueryWrapper<AddressBook> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(AddressBook::getUserId, (Long)session.getAttribute("userId"));
        lambdaQueryWrapper.orderByDesc(AddressBook::getUpdateTime);
        List<AddressBook> list = addressBookService.list(lambdaQueryWrapper);
        return R.success(list);
    }

    @PutMapping("/default")
    public R<String> setDefault(@RequestBody AddressBook addressBook, HttpSession session){
        LambdaUpdateWrapper<AddressBook> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(AddressBook::getUserId, (Long)session.getAttribute("userId"));
        lambdaUpdateWrapper.set(AddressBook::getIsDefault, 0);
        addressBookService.update(lambdaUpdateWrapper);

        addressBook.setIsDefault(1);
        addressBookService.updateById(addressBook);
        return R.success("设置成功");
    }

    @GetMapping("/default")
    public R<AddressBook> getDefault(HttpSession session){
        Object userId = session.getAttribute("userId");
        LambdaQueryWrapper<AddressBook> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(AddressBook::getUserId, userId);
        lambdaQueryWrapper.eq(AddressBook::getIsDefault, 1);
        AddressBook one = addressBookService.getOne(lambdaQueryWrapper);
        return R.success(one);
    }

    @GetMapping("/{id}")
    public R<AddressBook> getById(@PathVariable Long id){
        AddressBook addressBook = addressBookService.getById(id);
        if(addressBook == null){
            return R.error("没有该地址");
        }
        return R.success(addressBook);
    }

    @PutMapping
    public R<String> update(@RequestBody AddressBook addressBook){
        addressBookService.updateById(addressBook);
        return R.success("修改成功");
    }

    @DeleteMapping
    public R<String> delete(Long ids){
        addressBookService.removeById(ids);
        return R.success("删除成功");
    }
}
