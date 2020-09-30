package cn.hzby.whc.mapperService;

import cn.hzby.whc.entity.Alert;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface AlertMapper {
    /**
     * insert record to table
     *
     * @param record the record
     * @return insert count
     */
    int insert(Alert record);

    /**
     * select by primary key
     *
     * @param id primary key
     * @return object by primary key
     */
    Alert selectByPrimaryKey(Long id);

    /**
     * select rules
     */
    List<Alert> selectAllRules();
}