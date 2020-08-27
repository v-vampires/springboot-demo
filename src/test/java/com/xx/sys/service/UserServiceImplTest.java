package com.xx.sys.service;

import com.xx.sys.entity.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author yifanl
 * @Date 2020/7/21 9:52
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceImplTest {
    @Resource
    private IUserService userService;
    @Test
    public void testSave(){
        User user = new User();
        user.setName("hello");
        user.setAge(18);
        user.setEmail("test@test.com");
        boolean save = userService.save(user);
        Assert.assertTrue(save);
        List<User> list = userService.list();
        System.out.println(list.size());
        Assert.assertEquals(1, list.size());
    }
}
