package com.kob.backend.service.impl.user.bot;

import com.kob.backend.mapper.BotMapper;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.User;
import com.kob.backend.service.impl.utils.UserDetailsImpl;
import com.kob.backend.service.user.bot.UpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class UpdateServiceImpl implements UpdateService {
    @Autowired
    private BotMapper botMapper;

    @Override
    public Map<String, String> update(Map<String, String> data) {
        Map<String, String> result = new HashMap<>();

        int bot_id = Integer.parseInt(data.get("bot_id"));
        String content = data.get("content");
        String title = data.get("title");
        String description = data.get("description");
        System.out.println(content);

        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        Bot bot = botMapper.selectById(bot_id);
        if (bot == null) {
            result.put("error_message", "Bot not found");
            return result;
        }

        if (!Objects.equals(bot.getUserId(), user.getId())) {
            result.put("error_message", "Bot id mismatch");
            return result;
        }

        if (title.length() > 100) {
            result.put("error_message", "Title too long");
            return result;
        }

        if (description.length() > 300) {
            result.put("error_message", "Description too long");
            return result;
        }

        if (content.length() > 10000) {
            result.put("error_message", "Content too long");
            return result;
        }

        bot.setContent(content);
        bot.setTitle(title);
        bot.setDescription(description);
        botMapper.updateById(bot); 
        result.put("error_message", "Bot content update");

        return result;
    }
}
