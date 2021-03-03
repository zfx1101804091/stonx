package org.stonex.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.security.servlet.AntPathRequestMatcherProvider;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.stonex.auth.entity.SecurityUser;
import org.stonex.auth.entity.User;
import org.stonex.auth.handler.TokenManager;
import org.stonex.common.utils.R;
import org.stonex.common.utils.ResponseUtil;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @program: stonx
 * @description: 认证过滤器
 * @author: zheng_fx
 * @create: 2021-03-03 23:16
 */
public class TokenLoginFilter extends UsernamePasswordAuthenticationFilter {

    private TokenManager tokenManager;
    private RedisTemplate redisTemplate;
    private AuthenticationManager authenticationManager;

    public TokenLoginFilter(TokenManager tokenManager, RedisTemplate redisTemplate, AuthenticationManager authenticationManager) {
        this.tokenManager = tokenManager;
        this.redisTemplate = redisTemplate;
        this.authenticationManager = authenticationManager;
        //取消仅仅POST提交
        this.setPostOnly(false);
        //设置登录路径和匹配方式
        this.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/admin/acl/login", "POST"));
    }

    /**
     * 功能描述: <br>
     * 〈获取表单提交的用户名和密码〉
     * @Param: [request, response]
     * @Return: org.springframework.security.core.Authentication
     * @Author: zheng_fx
     * @Date: 2021/3/3 23:21
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        //1.获取表单提交数据
        try {
            User user = new ObjectMapper().readValue(request.getInputStream(), User.class);
//           //UsernamePasswordAuthenticationToken 第三个参数是权限集合
            Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(), new ArrayList<>()));
            return authenticate;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    /**
     * 功能描述: <br>
     * 〈认证成功调用的方法〉
     * @Param: [request, response, chain, authResult]
     * @Return: void
     * @Author: zheng_fx
     * @Date: 2021/3/3 23:20
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult)
            throws IOException, ServletException {

        //1.认证成功 得到认证成功后的用户信息(因为SecurityUser实现了UserDetails接口 所以这里可以强转)
        SecurityUser user = (SecurityUser)authResult.getPrincipal();
        //2.根据用户名生成token
        String token = tokenManager.createToken(user.getCurrentUserInfo().getUsername());
        //3.把用户名和用户权限列表放到redis(k:username v:权限列表)
        redisTemplate.opsForValue().set(user.getCurrentUserInfo().getUsername(),user.getPermissionValueList());
        //返回token
        ResponseUtil.out(response, R.ok().data("token",token));

    }

    /**
     * 功能描述: <br>
     * 〈认证失败调用的方法〉
     * @Param: [request, response, failed]
     * @Return: void
     * @Author: zheng_fx
     * @Date: 2021/3/3 23:21
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        ResponseUtil.out(response, R.error());
    }
}
