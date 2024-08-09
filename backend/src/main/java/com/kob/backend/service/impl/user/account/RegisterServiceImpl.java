package com.kob.backend.service.impl.user.account;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.User;
import com.kob.backend.service.user.account.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RegisterServiceImpl implements RegisterService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Map<String, String> register(String username, String password, String confirmPassword) {
        Map<String, String> map = new HashMap<>();
        if (username == null || password == null || confirmPassword == null) {
            map.put("error_message", "用户名或密码不能为空");
            return map;
        }

        username = username.trim();
        password = password.trim();
        confirmPassword = confirmPassword.trim();
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            map.put("error_message","用户名和密码不能为空");
            return map;
        }

        if (username.length() > 100 || password.length() > 100 || confirmPassword.length() > 100) {
            map.put("error_message", "用户名或密码过长");
            return map;
        }

        if (!password.equals(confirmPassword)) {
            map.put("error_message", "两次密码不一致");
            return map;
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        List<User> users = userMapper.selectList(queryWrapper);
        if (!users.isEmpty()) {
            map.put("error_message", "用户名已存在");
            return map;
        }

        String encodedPassword = passwordEncoder.encode(password);
        String photo = "https://cdn.acwing.com/media/user/profile/photo/441054_lg_c43cdd9202.jpeg";
        User user = new User(null, username, encodedPassword, photo);
        userMapper.insert(user);

        map.put("error_message", "成功注册");

        return map;
    }
}
