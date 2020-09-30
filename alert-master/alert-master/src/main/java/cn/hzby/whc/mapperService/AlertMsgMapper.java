package cn.hzby.whc.mapperService;

import cn.hzby.whc.entity.AlertMsg;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface AlertMsgMapper {
    /**
     * insert record to table
     *
     * @param record the record
     * @return insert count
     */
    int insert(AlertMsg record);

    /**
     * select by primary key
     *
     * @param msgId primary key
     * @return object by primary key
     */
    AlertMsg selectByPrimaryKey(Long msgId);
}