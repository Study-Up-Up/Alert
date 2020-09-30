package cn.hzby.whc.mapperService;

import cn.hzby.whc.entity.AlertRule;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface AlertRuleMapper {
    /**
     * insert record to table
     *
     * @param record the record
     * @return insert count
     */
    int insert(AlertRule record);

    /**
     * select by primary key
     *
     * @param id primary key
     * @return object by primary key
     */
    AlertRule selectByPrimaryKey(Long id);

    /**
     * 查询全部的组合规则
     */
    List<AlertRule> selectRuleCombination();
}