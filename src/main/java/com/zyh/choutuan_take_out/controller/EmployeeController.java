package com.zyh.choutuan_take_out.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zyh.choutuan_take_out.common.R;
import com.zyh.choutuan_take_out.entity.Employee;
import com.zyh.choutuan_take_out.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@RestController
@Slf4j
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        /**1.根据username查询数据库
         * 2.将password进行MD5加密
         * 3.密码对比
         * 4.查看是否被禁用
         * 5.登录成功将id存入session
         */

        LambdaQueryWrapper<Employee> usernameQueryWrapper = new LambdaQueryWrapper<>();
        usernameQueryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee one = employeeService.getOne(usernameQueryWrapper);
        if(one == null){
            return R.error("用户名或密码错误");
        }

        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        if(!password.equals(one.getPassword())){
            return R.error("用户名或密码错误");
        }

        if(one.getStatus() != 1){
            return R.error("账户已禁用");
        }

        request.getSession().setAttribute("employeeId", one.getId());
        return R.success(one);
    }

    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("employeeId");
        return R.success("退出成功");
    }

    @PostMapping
    public R<String> addEmployee(HttpServletRequest request, @RequestBody Employee employee){
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//        Long id = (Long) request.getSession().getAttribute("employeeId");
//        employee.setCreateUser(id);
//        employee.setUpdateUser(id);
        boolean save = employeeService.save(employee);
        return R.success("新增员工成功");
    }

    @GetMapping("/page")
    public R<Page> getPage(int page, int pageSize, String name){
        Page<Employee> pageInfo = new Page(page, pageSize);
        LambdaQueryWrapper<Employee> lambdaQueryWrapper = new LambdaQueryWrapper();
        if(name != null)
        lambdaQueryWrapper.like(Employee::getName, name);
        lambdaQueryWrapper.orderByDesc(Employee::getUpdateTime);
        employeeService.page(pageInfo, lambdaQueryWrapper);
        return R.success(pageInfo);
    }

    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser((Long) request.getSession().getAttribute("employeeId"));
        employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }

    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        Employee employee = employeeService.getById(id);
        if(employee != null){
            return R.success(employee);
        }
        return R.error("没有查询到员工信息");
    }
    @GetMapping("/judge")
    public R<String> judge(){
        return R.success("成功");
    }
}
