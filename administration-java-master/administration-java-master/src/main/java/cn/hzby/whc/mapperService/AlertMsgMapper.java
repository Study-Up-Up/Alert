package cn.hzby.whc.mapperService;

import cn.hzby.whc.entity.AlertMsg;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface AlertMsgMapper {
    int deleteByPrimaryKey(Long msgId);

    int insert(AlertMsg record);

    AlertMsg selectByPrimaryKey(Long msgId);

    int updateByPrimaryKey(AlertMsg record);

    List<AlertMsg> selectAllMsg();
}