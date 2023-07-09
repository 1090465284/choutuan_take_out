package com.zyh.choutuan_take_out.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mysql.cj.util.StringUtils;
import com.zyh.choutuan_take_out.common.BaseContext;
import com.zyh.choutuan_take_out.common.R;
import com.zyh.choutuan_take_out.entity.AddressBook;
import com.zyh.choutuan_take_out.entity.User;
import com.zyh.choutuan_take_out.service.UserService;
import com.zyh.choutuan_take_out.utils.SMSUtils;
import com.zyh.choutuan_take_out.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        /**
         * 获取手机号
         * 生成验证码
         * 调用阿里云服务
         * 存储
         */
        String phone = user.getPhone();
        log.info("{}", phone);
        if(StringUtils.isNullOrEmpty(phone)){
            return R.error("手机号为空");
        }
        String code = ValidateCodeUtils.generateValidateCode(4).toString();
//        SMSUtils.sendMessage( "张忆恒的博客","SMS_461860811",phone, code);
        log.info("{}",code);
        session.setAttribute(phone, code);
        return R.success("手机验证码发送成功");
    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map<String, String> map, HttpSession session){
        String phone = map.get("phone");
        String code = map.get("code");
        log.info("{}", map);

        Object codeInSession = session.getAttribute(phone);
        if(codeInSession == null || !codeInSession.equals(code)){
            return R.error("登录失败");
        }

        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getPhone, phone);

        User user = userService.getOne(lambdaQueryWrapper);
        if(user == null){
            user = new User();
            user.setPhone(phone);
            user.setStatus(1);
            userService.save(user);
        }
        session.setAttribute("userId", user.getId());
        return R.success(user);
    }

    @PostMapping("/loginout")
    public R<String> logout(HttpSession session){
        session.removeAttribute("userId");
        return R.success("退出成功");
    }
}
