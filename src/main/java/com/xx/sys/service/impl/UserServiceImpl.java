package com.xx.sys.service.impl;

import com.xx.sys.entity.User;
import com.xx.sys.mapper.UserMapper;
import com.xx.sys.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author yifanl
 * @since 2020-05-27
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}
