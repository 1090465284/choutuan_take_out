package com.zyh.choutuan_take_out;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.zyh.choutuan_take_out.entity.AddressBook;
import com.zyh.choutuan_take_out.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest
class ChoutuanTakeOutApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    public void test1(){
        String fileName = "21.8039.42819.jpg";
        String[] split = fileName.split("\\.");
        String name = UUID.randomUUID().toString();
        name += split[split.length - 1];
        System.out.println(name);
    }

    @Test
    public void test2(){
        String fileName = "21.8039.42819.jpg";
        fileName.substring(fileName.lastIndexOf("."));
        String name = UUID.randomUUID().toString();
        name += fileName;
        System.out.println(name);
    }


}
