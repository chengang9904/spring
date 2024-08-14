package com.kob.backend.service.impl.user.bot;

import com.kob.backend.mapper.BotMapper;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.User;
import com.kob.backend.service.impl.utils.UserDetailsImpl;
import com.kob.backend.service.user.bot.RemoveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Service
public class RemoveServiceImpl implements RemoveService {

    @Autowired
    private BotMapper botMapper;

    @Override
    public Map<String, String> remove(Map<String, String> data) {
        Map<String, String> result = new HashMap<>();

        int bot_id = Integer.parseInt(data.get("bot_id"));

        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        Bot bot = botMapper.selectById(bot_id);
        if (bot == null) {
            result.put("error_message", "bot not found");
            return result;
        }

        if (!bot.getUserId().equals(user.getId())) {
            result.put("error_message", "bot id mismatch");
            return result;
        }

        botMapper.deleteById(bot_id);
        result.put("error_message", "success_remove");

        return result;
    }
}
