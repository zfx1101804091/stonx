package org.stonex.common.auth.handler;

import io.jsonwebtoken.CompressionCodecs;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class TokenManager {

    //token有效时长
    private long tokenExpiration = 24*60*60*1000;
    //编码密钥(生产中 随机生成)
    private String tokenSignKey = "123456";

    //jwt根据用户名 生成token
    public  String createToken(String username) {
        String token = Jwts.builder().setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis()+tokenExpiration))//设置过期时间
                .signWith(SignatureAlgorithm.HS512,tokenSignKey).compressWith(CompressionCodecs.GZIP).compact();//固定写法
        return token;
    }

    public String getUserFromToken(String token) {
        String user = Jwts.parser().setSigningKey(tokenSignKey).parseClaimsJws(token).getBody().getSubject();
        return user;
    }
    public void removeToken(String token) {
        //jwttoken 无需删除，客户端扔掉即可。
    }


    public static void main(String[] args) {
        TokenManager tokenManager = new TokenManager();
        String token = tokenManager.createToken("zhangsan");
        System.out.println(token);
        System.out.println(tokenManager.getUserFromToken(token));
    }
}
