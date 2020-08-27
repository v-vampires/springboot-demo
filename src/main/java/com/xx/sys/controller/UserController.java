package com.xx.sys.controller;


import com.xx.sys.entity.User;
import com.xx.sys.mapper.UserMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author yifanl
 * @since 2020-05-27
 */
@RestController
@RequestMapping("/sys/user")
public class UserController {
    @Resource
    private UserMapper userMapper;
    @GetMapping("/list")
    public List<User> list(){
        return userMapper.selectList(null);
    }

}
