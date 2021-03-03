package org.stonex.auth.handler;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.stonex.common.utils.R;
import org.stonex.common.utils.ResponseUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @program: stonx
 * @description: 退出处理器
 * @author: zheng_fx
 * @create: 2021-03-03 22:30
 */
public class TokenLogoutHandler implements LogoutHandler {

    private TokenManager tokenManager;
    private RedisTemplate redisTemplate;

    public TokenLogoutHandler(TokenManager tokenManager, RedisTemplate redisTemplate) {
        this.tokenManager = tokenManager;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        //从 header 里面获取token
        String token = request.getHeader("token");

        //token不为空，移除token 并从redis中删除
        if(token!=null){
            //移除token
            tokenManager.removeToken(token);
            //从redis中删除
            String username = tokenManager.getUserFromToken(token);
            redisTemplate.delete(username);
        }
        ResponseUtil.out(response, R.ok());
    }
}
