package org.stonex.auth.handler;

import io.jsonwebtoken.CompressionCodecs;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * token 工具类
 */
@Component
public class TokenManager {

    //token有效时长
    private long tokenExpiration = 24*60*60*1000;
    //编码密钥(生产中 随机生成)
    private String tokenSignKey = "123456";

    /**
     * 功能描述: <br>
     * 〈使用jwt 生成 token〉
     * @Param: [username]
     * @Return: java.lang.String
     * @Author: zheng_fx
     * @Date: 2021/3/3 22:23
     */
    public  String createToken(String username) {
        String token = Jwts.builder().setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis()+tokenExpiration))//设置过期时间
                .signWith(SignatureAlgorithm.HS512,tokenSignKey).compressWith(CompressionCodecs.GZIP).compact();//固定写法
        return token;
    }

    /**
     * 功能描述: <br>
     * 〈根据 token 获取用户信息〉
     * @Param: [token]
     * @Return: java.lang.String
     * @Author: zheng_fx
     * @Date: 2021/3/3 22:17
     */
    public String getUserFromToken(String token) {
        String userInfo = Jwts.parser().setSigningKey(tokenSignKey).parseClaimsJws(token).getBody().getSubject();
        return userInfo;
    }

    /**
     * 功能描述: <br>
     * 〈 jwttoken 无需删除，客户端扔掉即可。〉
     * @Param: [token]
     * @Return: void
     * @Author: zheng_fx
     * @Date: 2021/3/3 22:26
     */
    public void removeToken(String token) {

    }


    public static void main(String[] args) {
        TokenManager tokenManager = new TokenManager();
        String token = tokenManager.createToken("zhangsan");
        System.out.println(token);
        System.out.println(tokenManager.getUserFromToken(token));
    }
}
