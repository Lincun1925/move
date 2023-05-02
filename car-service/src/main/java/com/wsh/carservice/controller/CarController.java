package com.wsh.carservice.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wsh.feignapi.common.Result;
import com.wsh.feignapi.entity.Car;
import com.wsh.carservice.service.CarService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/car")
public class CarController {

    @Resource
    private CarService carService;

    /**
     * 新增车次
     * @param car
     * @return
     */
    @PostMapping
    public Result save(@RequestBody Car car) {
        carService.save(car);
        return Result.success(car);
    }

    /**
     * 修改车次
     *
     * @return
     */
    @PutMapping
    public Result update(@RequestBody Car car) {
        carService.updateById(car);
        return Result.success(null);
    }

    /**
     * 删除车次
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        carService.removeById(id);
        return Result.success(null);
    }

    /**
     * 列车信息分页查询
     *
     * @param pageNum
     * @param pageSize
     * @param search
     * @return
     */
    @GetMapping("/list1")
    public Result findPage1(@RequestParam(defaultValue = "1") Integer pageNum,
                            @RequestParam(defaultValue = "10") Integer pageSize,
                            @RequestParam(defaultValue = "") String search) {
//        分页构造器
        Page page = new Page<>(pageNum, pageSize);
//        条件构造器
        LambdaQueryWrapper<Car> queryWrapper = new LambdaQueryWrapper<>();
//        if (StrUtil.isNotBlank(search)) {
//            queryWrapper.like(Car::getEnd, search);
//        }
//        排序
//        queryWrapper.orderByAsc(Car::getStartTime);
//        使索引有效
        queryWrapper.apply("end like {0}", search + "%");
//        分页查询
        carService.page(page, queryWrapper);
        return Result.success(page);
    }

    /**
     * 用户页面下的分页查询
     *
     * @param pageNum
     * @param pageSize
     * @param search1
     * @param search2
     * @param search3
     * @return
     */
    @GetMapping("/list2")
    public Result findPage2(@RequestParam(defaultValue = "1") Integer pageNum,
                            @RequestParam(defaultValue = "10") Integer pageSize,
                            @RequestParam(defaultValue = "") String search1,
                            @RequestParam(defaultValue = "") String search2,
                            @RequestParam(defaultValue = "") String search3) {
//        分页构造器
        Page page = new Page<>(pageNum, pageSize);
//        条件构造器
        LambdaQueryWrapper<Car> queryWrapper = new LambdaQueryWrapper<>();
        //search1 2 3分别是目的地、起点、班次
//        if (StrUtil.isNotBlank(search1)) {
//            queryWrapper.like(Car::getEnd, search1);
//        }
//        if (StrUtil.isNotBlank(search2)) {
//            queryWrapper.like(Car::getStart, search2);
//        }
//        if (StrUtil.isNotBlank(search3)) {
//            queryWrapper.like(Car::getNum, search3);
//        }
//        默认发车时间升序排序
//        queryWrapper.orderByAsc(Car::getStartTime);
//        联合索引
        queryWrapper.apply("end like {0} and start like {1} and middle like {2}"
                , search1 + "%", search2 + "%", search3 + "%");
//        分页查询
        carService.page(page, queryWrapper);
        return Result.success(page);
    }

    /**
     * feign远程调用
     *
     * @return
     */
    @GetMapping("/feign")
    public List<Car> list() {
        return carService.list();
    }

    @PostMapping("/feign")
    public List<Car> listByIds(@RequestBody List<Long> list) {
        return carService.listByIds(list);
    }

    @DeleteMapping("/feign/del/{id}")
    public void del(@PathVariable Long id) {
        carService.update().setSql("stock=stock-1")
                .eq("id", id)
                .gt("stock", 0)
                .update();
    }

    @GetMapping("/feign/add/{id}")
    public void add(@PathVariable Long id) {
        carService.update().setSql("stock=stock+1").eq("id", id).update();
    }

    @GetMapping("/feign/{id}")
    public Car getById(@PathVariable Long id) {
        QueryWrapper<Car> queryWrapper = new QueryWrapper<>();
        return carService.getOne(queryWrapper.eq("id", id));
    }
}
