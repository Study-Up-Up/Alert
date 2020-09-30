package cn.hzby.whc.mapperService;

import cn.hzby.whc.entity.AlertUser;
import org.apache.ibatis.annotations.Mapper;import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface AlertUserMapper {

    int insert(AlertUser record);

    AlertUser selectByPrimaryKey(Long alertId);

    //查询账号和密码是否一致
    AlertUser getUser(@Param("username") String username, @Param("password") String password);
}