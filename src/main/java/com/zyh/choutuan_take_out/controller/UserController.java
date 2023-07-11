package com.zyh.choutuan_take_out.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mysql.cj.util.StringUtils;
import com.zyh.choutuan_take_out.common.BaseContext;
import com.zyh.choutuan_take_out.common.R;
import com.zyh.choutuan_take_out.entity.AddressBook;
import com.zyh.choutuan_take_out.entity.User;
import com.zyh.choutuan_take_out.service.UserService;
import com.zyh.choutuan_take_out.service.impl.MailService;
import com.zyh.choutuan_take_out.utils.SMSUtils;
import com.zyh.choutuan_take_out.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Resource
    private RedisTemplate redisTemplate;

    @Autowired
    private MailService mailService;

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
            return R.error("手机号或邮箱为空");
        }
        String rulesPhone = "^1([38][0-9]|4[5-9]|5[0-3,5-9]|66|7[0-8]|9[89])[0-9]{8}$";
        String rulesEmail = "^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*\\.[a-zA-Z0-9]{2,6}$";
        String code = ValidateCodeUtils.generateValidateCode(4).toString();
        if (phone.matches(rulesPhone)) {
            SMSUtils.sendMessage( "张忆恒的博客","SMS_461860811",phone, code);
            return R.error("发短信花钱,请使用邮箱");
        } else if (phone.matches(rulesEmail)) {
            mailService.sendMimeMail(phone, code);
        }



        log.info("{}:{}",phone,code);
//        session.setAttribute(phone, code);
        redisTemplate.opsForValue().set(phone, code, 5, TimeUnit.MINUTES);
        return R.success("验证码发送成功");
    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map<String, String> map, HttpSession session){
        String phone = map.get("phone");
        String code = map.get("code");
        log.info("{}", map);

//        Object codeInSession = session.getAttribute(phone);
        Object codeInRedis = redisTemplate.opsForValue().get(phone);
        if(codeInRedis == null || !codeInRedis.equals(code)){
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
        redisTemplate.delete(phone);
        return R.success(user);
    }

    @PostMapping("/loginout")
    public R<String> logout(HttpSession session){
        session.removeAttribute("userId");
        return R.success("退出成功");
    }
}
