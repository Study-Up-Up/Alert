package cn.hzby.whc.tokenauthentication.TokenController;

import cn.hzby.whc.entity.AlertUser;
import cn.hzby.whc.mapperService.AlertUserMapper;
import cn.hzby.whc.tokenauthentication.TokenUtils.ConstantKit;
import cn.hzby.whc.tokenauthentication.TokenUtils.Md5TokenGenerator;
import cn.hzby.whc.tokenauthentication.model.ResponseTemplate;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;


//所有的登录都是经过这个TokenController,所以这个服务既是服务消费者也是服务提供者.
@RestController
public class LoginToken {

    //控制台输出
     private Logger logger = LoggerFactory.getLogger(LoginToken.class);

    //MD5算法生成Token码
    @Autowired
    Md5TokenGenerator tokenGenerator;

    //查询账号和密码的Mapper
    @Resource(name = "alertUserMapper")
    AlertUserMapper alertUserMapper;

    //登录时候的Token(Token存在的时间看情况而定)
    //所有的登录请求都经这个Controller,这个是服务提供者.
    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/loginToken", method = RequestMethod.POST)
    public ResponseTemplate loginToken (@RequestParam("username") String username, @RequestParam("password") String password)throws Exception {

        AlertUser user = alertUserMapper.getUser(username,password);

        JSONObject result = new JSONObject();
        if (user != null) {

            Long id=user.getAlertId();

            //打开Redis
            Jedis jedis = new Jedis("localhost", 6379);
            jedis.auth("");

            //删除之前存在的Token
            String tokenOld=jedis.get(id.toString());
            //控制台检测一下是否存在
            //logger.info(tokenOld);
            if (tokenOld!=null){
                jedis.del(tokenOld);
                jedis.del(id.toString());
            }

            //Token的生成
            String token = tokenGenerator.generate(id.toString(), password);

            //设置key生存时间，当key过期时，它会被自动删除，时间是秒
            jedis.set(id.toString(), token);
            jedis.expire(id.toString(), ConstantKit.TOKEN_EXPIRE_TIME);
            jedis.set(token, id.toString());
            jedis.expire(token, ConstantKit.TOKEN_EXPIRE_TIME);

            //用完关闭
            jedis.close();

            result.put("token", token);


        } else {
            result.put("status", 401);
            result.put("message","账号密码不对");
            return new ResponseTemplate(401,"登录失败",result);
        }
        return new ResponseTemplate(200,"登录成功",result);
    }

}
