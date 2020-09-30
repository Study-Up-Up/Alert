package cn.hzby.whc.mapperService;

import cn.hzby.whc.entity.AlertRule;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface AlertRuleMapper {
    int deleteByPrimaryKey(Long id);

    int insert(AlertRule record);

    AlertRule selectByPrimaryKey(Long id);

    int updateByPrimaryKey(AlertRule record);

    /**
     * 查询全部的组合规则
     */
    List<AlertRule> selectRuleCombination();
}