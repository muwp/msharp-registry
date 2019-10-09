package com.ruijing.registry.admin.test.dao;

import com.ruijing.registry.admin.data.mapper.RegistryMapper;
import com.ruijing.registry.admin.data.mapper.RegistryNodeMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DaoTest {

    @Resource
    private RegistryMapper registryDao;
    @Resource
    private RegistryNodeMapper registryDataDao;

    @Test
    public void test(){
        registryDao.pageList(0, 100, null, null, null);
    }


}
