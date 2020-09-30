package cn.hzby;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@MapperScan("cn.hzby.whc.mapperService")
@SpringBootApplication
public class Administration
{
    public static void main( String[] args )
    {
        SpringApplication.run(Administration.class);
    }
}
