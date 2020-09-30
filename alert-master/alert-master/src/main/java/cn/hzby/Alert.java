package cn.hzby;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;



/**
 * 钉钉报警机器人
 */
@MapperScan("cn.hzby.whc.mapperService")
@SpringBootApplication
public class Alert
{
    public static void main( String[] args ) {
        System.out.println("钉钉报警机器人已启动");
        SpringApplication.run(Alert.class, args);
    }
}
