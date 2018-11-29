package com.xxl.registry.admin.test.dao;

import com.xxl.registry.admin.dao.IXxlRegistryDao;
import com.xxl.registry.admin.dao.IXxlRegistryDataDao;
import com.xxl.registry.admin.dao.IXxlRegistryMessageDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DaoTest {

    @Resource
    private IXxlRegistryDao xxlRegistryDao;
    @Resource
    private IXxlRegistryDataDao xxlRegistryDataDao;
    @Resource
    private IXxlRegistryMessageDao xxlRegistryMessageDao;

    @Test
    public void test(){
        xxlRegistryDao.pageList(0, 100, null, null, null);
    }


}
