package com.ruijing.registry.admin.data.mapper;

import com.ruijing.registry.admin.data.model.TokenDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * token mapper
 *
 * @author mwup
 * @version 1.0
 * @created 2018/9/4 17:03
 **/
@Mapper
public interface TokenMapper {

    int insertSelective(TokenDO tokenDO);

    TokenDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TokenDO tokenDO);

    List<TokenDO> listBizToken();

    List<TokenDO> get(String appkey);
}