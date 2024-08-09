package com.kob.backend.controller.user;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    UserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/user/all/")
    public List<User> getAll() {
        return userMapper.selectList(null);
    }

    @GetMapping("/user/{id}")
    public User getById(@PathVariable int id) {
//        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("id", id);
//        return userMapper.selectOne(queryWrapper);
        return userMapper.selectById(id);
    }

    @GetMapping("/user/add/{id}/{username}/{password}")
    public String add(@PathVariable int id, @PathVariable String username, @PathVariable String password) {
        if (password.length() < 6) {
            return "密码太短";
        }
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        User user = new User(id, username, passwordEncoder.encode(password));
        userMapper.insert(user);

        return "add success";
    }

    @GetMapping("/user/delete/{id}")
    public String delete(@PathVariable int id) {
        userMapper.deleteById(id);
        return "delete success";
    }

}
