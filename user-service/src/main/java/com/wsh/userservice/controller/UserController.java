package com.wsh.userservice.controller;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wsh.feignapi.clients.CarClient;
import com.wsh.feignapi.common.Result;
import com.wsh.feignapi.entity.Car;
import com.wsh.feignapi.entity.User;
import com.wsh.feignapi.utils.UserHolder;
import com.wsh.userservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;
    //远程调用
    @Resource
    private CarClient carClient;


    /**
     * 用户登录
     * @param user
     * @return
     */
    @PostMapping("/login")
    public Result login(@RequestBody User user) {
        return userService.login(user);
    }

    /**
     * 发送验证码
     * @param email
     * @return
     */
    @GetMapping("/sendMsg/{email}")
    public Result sendMsg(@PathVariable String email) {
        if (email.equals("undefined")) {
            return Result.error("请输入邮箱！");
        }
        return userService.sendCode(email);
    }

    /**
     * 用户注册
     * @param map
     * @return
     */
    @PostMapping("/register")
    public Result register(@RequestBody Map map) {
        return userService.register(map);
    }

    /**
     * 列车信息导出
     * @param response
     * @throws IOException
     */
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        //feign远程调用
        List<Car> cars = carClient.getList();
        for (Car car : cars) {
            Map<String, Object> map = new HashMap<>();
            map.put("始发站", car.getStart());
            map.put("经停站", car.getMiddle());
            map.put("到达站", car.getEnd());
            map.put("剩余票数", car.getStock());
            map.put("发车时间", car.getStartTime());
            map.put("班车频次", car.getNum());
            map.put("载客容量", car.getCapacity());
            list.add(map);
        }

        ExcelWriter writer = ExcelUtil.getWriter(true);
        writer.write(list, true);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        String fileName = URLEncoder.encode("列车信息", "UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + "(" + LocalDateTime.now() + ")" + ".xlsx");

        ServletOutputStream out = response.getOutputStream();
        writer.flush(out, true);
        writer.close();
        IoUtil.close(System.out);
    }


    /**
     * 前端信息获取
     * @return
     */
    @GetMapping("/information")
    public Result show() {
        User user = UserHolder.getUser();
        return Result.success(user);
    }

    /**
     * 退出系统
     * @param token
     * @return
     */
    @GetMapping("/out")
    public Result out(@RequestHeader(value = "authorization") String token){
        return userService.out(token);
    }
}
