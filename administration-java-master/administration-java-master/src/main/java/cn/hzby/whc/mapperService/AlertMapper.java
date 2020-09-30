package cn.hzby.whc.mapperService;

import cn.hzby.whc.entity.Alert;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface AlertMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Alert record);

    Alert selectByPrimaryKey(Long id);

    int updateByPrimaryKey(Alert record);

    List<Alert> selectAllRules();
}