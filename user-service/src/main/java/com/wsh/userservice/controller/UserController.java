package com.wsh.userservice.controller;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wsh.feignapi.clients.CarClient;
import com.wsh.feignapi.common.Result;
import com.wsh.feignapi.entity.Car;
import com.wsh.feignapi.entity.User;
import com.wsh.feignapi.utils.UserHolder;
import com.wsh.userservice.ai.Answer;
import com.wsh.userservice.ai.Choices;
import com.wsh.userservice.entity.enEntity;
import com.wsh.userservice.entity.pyqEntity;
import com.wsh.userservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.net.ssl.SSLContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
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

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @GetMapping("/news")
    public Result news() {
        log.error(JSON.toJSONString(stringRedisTemplate.opsForHash().entries("newsByDay")));
        return Result.success(JSON.toJSONString(stringRedisTemplate.opsForHash().entries("newsByDay")));
    }

    @GetMapping("/chat")
    public Result chat() throws IOException {
//        try (CloseableHttpClient httpClient = HttpClientBuilder.create()
//                .setProxy(new HttpHost("localhost", 7890))
//                .setSSLSocketFactory(getSslConnectionSocketFactory())
//                .build()) {
//            return Result.success(submit(httpClient, getHttpPost(), req));
//        } catch (Exception e) {
//            return Result.error("请求失败");
//        }
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            CloseableHttpResponse response = null;
            Random random = new Random();
            int target = random.nextInt(2);
            if (target == 0) {
                HttpGet request = new HttpGet("http://apis.juhe.cn/fapigx/everyday/query?key=9c5a4167c018a215c581acf61d2a4d3b");
                request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
                request.setHeader(HttpHeaders.ACCEPT, "application/json");
                response = httpClient.execute(request);
            } else {
                HttpGet request = new HttpGet("http://apis.juhe.cn/fapigx/pyqwenan/query?key=3be6f9655c933e1204e44fef3b8ce3a7");
                request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
                request.setHeader(HttpHeaders.ACCEPT, "application/json");
                response = httpClient.execute(request);
            }
            if (response.getStatusLine().getStatusCode() == 0) {
                return Result.error("今日语句已展示完毕");
            } else {
                String s = EntityUtils.toString(response.getEntity());
                if (target == 0) {
                    enEntity enEntity = JSON.parseObject(s, enEntity.class);
                    return Result.success(enEntity.result.content);
                } else {
                    pyqEntity pyqEntity = JSON.parseObject(s, pyqEntity.class);
                    return Result.success(pyqEntity.result.content);
                }
            }
        } catch (Exception ex) {
            return Result.error("出错了");
        }


    }

    /**
     * 用户登录
     *
     * @param user
     * @return
     */
    @PostMapping("/login")
    public Result login(@RequestBody User user) {
        return userService.login(user);
    }

    /**
     * 发送验证码
     *
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
     *
     * @param map
     * @return
     */
    @PostMapping("/register")
    public Result register(@RequestBody Map map) {
        return userService.register(map);
    }

    /**
     * 列车信息导出
     *
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
     *
     * @return
     */
    @GetMapping("/information")
    public Result show() {
        User user = UserHolder.getUser();
        return Result.success(user);
    }

    /**
     * 退出系统
     *
     * @param token
     * @return
     */
    @GetMapping("/out")
    public Result out(@RequestHeader(value = "authorization") String token) {
        return userService.out(token);
    }

//    private static String submit(CloseableHttpClient httpClient, HttpPost post, String req) throws IOException {
//        StringEntity stringEntity = new StringEntity(getRequestJson(req), getContentType());
//        post.setEntity(stringEntity);
//        CloseableHttpResponse response;
//        response = httpClient.execute(post);
//        return printAnswer(response);
//    }
//
//    private static String printAnswer(CloseableHttpResponse response) throws IOException {
//        if (response.getStatusLine().getStatusCode() == org.apache.http.HttpStatus.SC_OK) {
//            String responseJson = EntityUtils.toString(response.getEntity());
//            Answer answer = JSON.parseObject(responseJson, Answer.class);
//            StringBuilder answers = new StringBuilder();
//            List<Choices> choices = answer.getChoices();
//            for (Choices choice : choices) {
//                answers.append(choice.getText());
//            }
//            log.info(answers.toString());
//            return answers.substring(2, answers.length());
//        } else {
//            return String.valueOf(response.getStatusLine().getStatusCode());
//        }
//    }
//
//    private static ContentType getContentType() {
//        return ContentType.create("text/json", "UTF-8");
//    }
//
//    private static String getRequestJson(String question) {
//        return "{\"model\": \"text-davinci-003\", \"prompt\": \"" + question + "\", \"temperature\": 0, \"max_tokens\": 1024}";
//    }
//
//    private static HttpPost getHttpPost() throws IOException {
//        String openAiKey = "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";
//        String connectTimeout = "60000";
//        String connectionRequestTimeout = "60000";
//        String socketTimeout = "60000";
//        HttpPost post = new HttpPost("https://api.openai.com/v1/completions");
//        post.addHeader("Content-Type", "application/json");
//        post.addHeader("Authorization", "Bearer " + openAiKey);
//        RequestConfig requestConfig = RequestConfig.custom()
//                .setConnectTimeout(Integer.parseInt(connectTimeout)).setConnectionRequestTimeout(Integer.parseInt(connectionRequestTimeout))
//                .setSocketTimeout(Integer.parseInt(socketTimeout)).build();
//        post.setConfig(requestConfig);
//        return post;
//    }
//
//    private static SSLConnectionSocketFactory getSslConnectionSocketFactory() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
//        TrustStrategy acceptingTrustStrategy = (x509Certificates, s) -> true;
//        SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
//        return new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());
//    }

}
