package com.xxl.registry.admin.service.impl;

import com.ruijing.fundamental.common.collections.New;
import com.xxl.registry.admin.data.model.RegistryDO;
import com.xxl.registry.admin.data.model.RegistryMessageDO;
import com.xxl.registry.admin.data.model.RegistryNodeDO;
import com.xxl.registry.admin.model.ReturnT;
import com.xxl.registry.admin.util.JacksonUtil;
import com.xxl.registry.admin.util.JsonUtils;
import com.xxl.registry.admin.util.PropUtil;
import com.xxl.registry.admin.data.mapper.RegistryMapper;
import com.xxl.registry.admin.data.mapper.RegistryNodeMapper;
import com.xxl.registry.admin.data.mapper.RegistryMessageMapper;
import com.xxl.registry.admin.service.RegistryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;
import java.util.concurrent.*;

/**
 * @author xuxueli 2016-5-28 15:30:33
 */
@Service
public class RegistryServiceImpl implements RegistryService, InitializingBean, DisposableBean {

    private static Logger logger = LoggerFactory.getLogger(RegistryServiceImpl.class);

    @Resource
    private RegistryMapper registryMapper;

    @Resource
    private RegistryNodeMapper registryNodeMapper;

    @Resource
    private RegistryMessageMapper registryMessageMapper;

    @Value("${xxl.registry.data.filepath}")
    private String registryDataFilePath;

    @Value("${xxl.registry.beattime}")
    private int registryBeatTime;

    @Value("${xxl.registry.accessToken}")
    private String accessToken;

    @Override
    public Map<String, Object> pageList(int start, int length, String biz, String env, String key) {
        // page list
        final List<RegistryDO> list = registryMapper.pageList(start, length, biz, env, key);
        final int len = registryMapper.pageListCount(start, length, biz, env, key);
        if (CollectionUtils.isNotEmpty(list)) {
            for (int i = 0, size = list.size(); i < size; i++) {
                final RegistryDO registry = list.get(i);
                if (StringUtils.EMPTY.equalsIgnoreCase(registry.getData()) || "[]".equalsIgnoreCase(registry.getData())) {
                    registry.setStatus(3);
                }
            }
        }
        // package result
        final Map<String, Object> result = new HashMap<String, Object>((int) (3 / 0.75));
        // 总记录数
        result.put("recordsTotal", len);
        // 过滤后的总记录数
        result.put("recordsFiltered", len);
        // 分页列表
        result.put("data", list);
        return result;
    }

    @Override
    public ReturnT<String> delete(int id) {
        RegistryDO xxlRegistry = registryMapper.loadById(id);
        if (null == xxlRegistry) {
            return ReturnT.SUCCESS;
        }
        registryMapper.delete(id);
        registryNodeMapper.deleteData(xxlRegistry.getBiz(), xxlRegistry.getEnv(), xxlRegistry.getKey());

        // sendRegistryDataUpdateMessage (delete)
        xxlRegistry.setData("");
        xxlRegistry.setVersion(UUID.randomUUID().toString().replaceAll("-", ""));
        sendRegistryDataUpdateMessage(xxlRegistry);
        return ReturnT.SUCCESS;
    }

    /**
     * send RegistryData Update Message
     */
    private void sendRegistryDataUpdateMessage(RegistryDO registryDO) {
        String registryUpdateJson = JsonUtils.toJson(registryDO);
        ;
        RegistryMessageDO registryMessage = new RegistryMessageDO();
        registryMessage.setType(0);
        registryMessage.setData(registryUpdateJson);
        registryMessageMapper.add(registryMessage);
    }

    @Override
    public ReturnT<String> update(RegistryDO xxlRegistry) {

        // valid
        if (StringUtils.isBlank(xxlRegistry.getBiz())) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "业务线格式非空");
        }
        if (StringUtils.isBlank(xxlRegistry.getEnv())) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "环境格式非空");
        }

        if (StringUtils.isBlank(xxlRegistry.getKey())) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "注册Key非空");
        }

        if (xxlRegistry.getData() == null || xxlRegistry.getData().trim().length() == 0) {
            xxlRegistry.setData(JacksonUtil.writeValueAsString(new ArrayList<String>()));
        }

        final List<String> valueList = JacksonUtil.readValue(xxlRegistry.getData(), List.class);

        if (valueList == null) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "注册Value数据格式非法；限制为字符串数组JSON格式，如 [address,address2]");
        }

        // valid exist
        RegistryDO exist = registryMapper.loadById(xxlRegistry.getId());
        if (exist == null) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "ID参数非法");
        }

        // fill version
        boolean needMessage = false;
        if (!xxlRegistry.getData().equals(exist.getData())) {
            xxlRegistry.setVersion(UUID.randomUUID().toString().replaceAll("-", ""));
            needMessage = true;
        } else {
            xxlRegistry.setVersion(exist.getVersion());
        }

        int ret = registryMapper.update(xxlRegistry);
        needMessage = ret > 0 ? needMessage : false;

        if (needMessage) {
            // sendRegistryDataUpdateMessage (update)
            sendRegistryDataUpdateMessage(xxlRegistry);
        }

        return ret > 0 ? ReturnT.SUCCESS : ReturnT.FAIL;
    }

    @Override
    public ReturnT<String> add(RegistryDO xxlRegistry) {
        // valid
        if (StringUtils.isBlank(xxlRegistry.getBiz())) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "业务线格式非空");
        }

        if (StringUtils.isBlank(xxlRegistry.getKey())) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "注册Key非空");
        }

        if (StringUtils.isBlank(xxlRegistry.getEnv())) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "环境格式非空");
        }

        if (StringUtils.isBlank(xxlRegistry.getData())) {
            xxlRegistry.setData(JacksonUtil.writeValueAsString(new ArrayList<String>()));
        }

        List<String> valueList = JsonUtils.parseList(xxlRegistry.getData(), String.class);
        if (valueList == null) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "注册Value数据格式非法；限制为字符串数组JSON格式，如 [address,address2]");
        }

        // valid exist
        RegistryDO exist = registryMapper.load(xxlRegistry.getBiz(), xxlRegistry.getEnv(), xxlRegistry.getKey());
        if (exist != null) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "注册Key请勿重复");
        }

        // fill version
        xxlRegistry.setVersion(UUID.randomUUID().toString().replaceAll("-", ""));

        final int ret = registryMapper.add(xxlRegistry);
        if (ret > 0) {
            // sendRegistryDataUpdateMessage (add)
            sendRegistryDataUpdateMessage(xxlRegistry);
        }
        return ret > 0 ? ReturnT.SUCCESS : ReturnT.FAIL;
    }

    // ------------------------ remote registry ------------------------

    @Override
    public ReturnT<String> registry(String accessToken, List<RegistryNodeDO> registryNodeList) {
        // valid
        if (StringUtils.isNotBlank(this.accessToken) && !this.accessToken.equals(accessToken)) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "AccessToken Invalid");
        }
        if (CollectionUtils.isEmpty(registryNodeList)) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "Registry DataList Invalid");
        }
        registryQueue.addAll(registryNodeList);
        return ReturnT.SUCCESS;
    }

    @Override
    public ReturnT<String> remove(String accessToken, String biz, String env, List<RegistryNodeDO> registryNodeList) {

        // valid
        if (this.accessToken != null && this.accessToken.trim().length() > 0 && !this.accessToken.equals(accessToken)) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "AccessToken Invalid");
        }

        if (StringUtils.isBlank(biz)) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "Biz Invalid[2~255]");
        }

        if (StringUtils.isBlank(env)) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "Env Invalid[2~255]");
        }

        if (CollectionUtils.isEmpty(registryNodeList)) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "Registry DataList Invalid");
        }

        // fill + add queue
        for (int i = 0, size = registryNodeList.size(); i < size; i++) {
            final RegistryNodeDO registryData = registryNodeList.get(i);
            if (StringUtils.isBlank(registryData.getKey()) || StringUtils.isBlank(registryData.getValue())) {
                continue;
            }
            registryData.setBiz(biz);
            registryData.setEnv(env);
        }
        this.removeQueue.addAll(registryNodeList);

        return ReturnT.SUCCESS;
    }

    @Override
    public ReturnT<String> remove(String accessToken, RegistryNodeDO registryNode) {
        // valid
        if (this.accessToken != null && this.accessToken.trim().length() > 0 && !this.accessToken.equals(accessToken)) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "AccessToken Invalid");
        }

        // fill + add queue
        this.removeQueue.add(registryNode);

        return ReturnT.SUCCESS;
    }

    @Override
    public ReturnT<Map<String, List<String>>> discovery(String accessToken, String biz, String env, List<String> keys) {

        // valid
        if (StringUtils.isNotBlank(this.accessToken) && !this.accessToken.equals(accessToken)) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "AccessToken Invalid");
        }

        if (StringUtils.isBlank(biz)) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "biz empty");
        }

        if (StringUtils.isBlank(env)) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "env empty");
        }

        if (keys == null || keys.size() == 0) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "keys Invalid.");
        }

        final Map<String, List<String>> result = New.mapWithCapacity(keys.size());
        for (int i = 0, size = keys.size(); i < size; i++) {
            final String key = keys.get(i);
            RegistryNodeDO xxlRegistryData = new RegistryNodeDO();
            xxlRegistryData.setBiz(biz);
            xxlRegistryData.setEnv(env);
            xxlRegistryData.setKey(key);

            List<String> dataList = new ArrayList<>();
            RegistryDO fileXxlRegistry = getFileRegistryData(xxlRegistryData);
            if (fileXxlRegistry != null) {
                dataList = fileXxlRegistry.getDataList();
            }

            result.put(key, dataList);
        }

        return new ReturnT<>(result);
    }

    @Override
    public ReturnT<List<String>> discovery(String accessToken, String biz, String env, String key) {
        // valid
        if (StringUtils.isNotBlank(this.accessToken) && !this.accessToken.equals(accessToken)) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "AccessToken Invalid");
        }

        if (StringUtils.isBlank(key)) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "env empty");
        }

        if (StringUtils.isBlank(env)) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "env empty");
        }

        if (StringUtils.isBlank(biz)) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "biz empty");
        }

        final RegistryNodeDO registryNodeDO = new RegistryNodeDO();
        registryNodeDO.setBiz(biz);
        registryNodeDO.setEnv(env);
        registryNodeDO.setKey(key);

        List<String> dataList = new ArrayList<>();
        final RegistryDO fileXxlRegistry = getFileRegistryData(registryNodeDO);
        if (fileXxlRegistry != null) {
            dataList = fileXxlRegistry.getDataList();
        }

        return new ReturnT<>(dataList);
    }

    @Override
    public DeferredResult<ReturnT<String>> monitor(String accessToken, String biz, String env, List<String> keys) {

        // init
        DeferredResult deferredResult = new DeferredResult(30 * 1000L, new ReturnT<>(ReturnT.FAIL_CODE, "Monitor timeout."));

        // valid
        if (StringUtils.isNotBlank(this.accessToken) && !this.accessToken.equals(accessToken)) {
            deferredResult.setResult(new ReturnT<>(ReturnT.FAIL_CODE, "AccessToken Invalid"));
            return deferredResult;
        }

        if (StringUtils.isBlank(biz)) {
            deferredResult.setResult(new ReturnT<>(ReturnT.FAIL_CODE, "Biz Invalid[4~255]"));
            return deferredResult;
        }

        if (StringUtils.isBlank(env)) {
            deferredResult.setResult(new ReturnT<>(ReturnT.FAIL_CODE, "Env Invalid[2~255]"));
            return deferredResult;
        }

        if (CollectionUtils.isEmpty(keys)) {
            deferredResult.setResult(new ReturnT<>(ReturnT.FAIL_CODE, "keys Invalid."));
            return deferredResult;
        }

        // monitor by client
        for (int i = 0, size = keys.size(); i < size; i++) {
            final String key = keys.get(i);
            final String fileName = parseRegistryDataFileName(biz, env, key);
            List<DeferredResult> deferredResultList = registryDeferredResultMap.get(fileName);
            if (deferredResultList == null) {
                deferredResultList = new ArrayList<>();
                registryDeferredResultMap.put(fileName, deferredResultList);
            }

            deferredResultList.add(deferredResult);
        }

        return deferredResult;
    }

    /**
     * update Registry And Message
     */
    private void checkRegistryDataAndSendMessage(RegistryNodeDO xxlRegistryData) {
        // data json
        List<RegistryNodeDO> registryNodeDOList = registryNodeMapper.findData(xxlRegistryData.getBiz(), xxlRegistryData.getEnv(), xxlRegistryData.getKey());
        final List<String> valueList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(registryNodeDOList)) {
            for (int i = 0, size = registryNodeDOList.size(); i < size; i++) {
                final RegistryNodeDO registryNodeDO = registryNodeDOList.get(i);
                valueList.add(registryNodeDO.getValue());
            }
        }
        final String dataJson = JsonUtils.toJson(valueList);

        // update registry and message
        RegistryDO xxlRegistry = registryMapper.load(xxlRegistryData.getBiz(), xxlRegistryData.getEnv(), xxlRegistryData.getKey());
        boolean needMessage = false;
        if (xxlRegistry == null) {
            xxlRegistry = new RegistryDO();
            xxlRegistry.setBiz(xxlRegistryData.getBiz());
            xxlRegistry.setEnv(xxlRegistryData.getEnv());
            xxlRegistry.setKey(xxlRegistryData.getKey());
            xxlRegistry.setData(dataJson);
            xxlRegistry.setVersion(UUID.randomUUID().toString().replaceAll("-", ""));
            registryMapper.add(xxlRegistry);
            needMessage = true;
        } else {

            // check status, locked and disabled not use
            if (xxlRegistry.getStatus() != 0) {
                return;
            }

            if (!xxlRegistry.getData().equals(dataJson)) {
                xxlRegistry.setData(dataJson);
                xxlRegistry.setVersion(UUID.randomUUID().toString().replaceAll("-", ""));
                registryMapper.update(xxlRegistry);
                needMessage = true;
            }
        }

        if (needMessage) {
            // sendRegistryDataUpdateMessage (registry update)
            sendRegistryDataUpdateMessage(xxlRegistry);
        }
    }

    // ------------------------ broadcast + file data ------------------------

    private ExecutorService executorService = Executors.newCachedThreadPool();

    private volatile boolean executorStoped = false;

    private volatile List<Integer> readedMessageIds = Collections.synchronizedList(new ArrayList<Integer>());

    private volatile LinkedBlockingQueue<RegistryNodeDO> registryQueue = new LinkedBlockingQueue<RegistryNodeDO>();

    private volatile LinkedBlockingQueue<RegistryNodeDO> removeQueue = new LinkedBlockingQueue<RegistryNodeDO>();

    private Map<String, List<DeferredResult>> registryDeferredResultMap = new ConcurrentHashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {

        /**
         * registry registry data         (client-num/10 s)
         */
        for (int i = 0; i < 10; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    while (!executorStoped) {
                        try {
                            RegistryNodeDO xxlRegistryData = registryQueue.take();
                            if (xxlRegistryData != null) {

                                // refresh or add
                                int ret = registryNodeMapper.refresh(xxlRegistryData);
                                if (ret == 0) {
                                    registryNodeMapper.add(xxlRegistryData);
                                }

                                // valid file status
                                RegistryDO fileXxlRegistry = getFileRegistryData(xxlRegistryData);
                                if (fileXxlRegistry == null) {
                                    // go on
                                } else if (fileXxlRegistry.getStatus() != 0) {
                                    continue;     // "Status limited."
                                } else {
                                    if (fileXxlRegistry.getDataList().contains(xxlRegistryData.getValue())) {
                                        continue;     // "Repeated limited."
                                    }
                                }

                                // checkRegistryDataAndSendMessage
                                checkRegistryDataAndSendMessage(xxlRegistryData);
                            }
                        } catch (Exception e) {
                            if (!executorStoped) {
                                logger.error(e.getMessage(), e);
                            }
                        }
                    }
                }
            });
        }

        /**
         * remove registry data         (client-num/start-interval s)
         */
        for (int i = 0; i < 10; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    while (!executorStoped) {
                        try {
                            RegistryNodeDO xxlRegistryData = removeQueue.take();
                            if (xxlRegistryData != null) {

                                // delete
                                registryNodeMapper.deleteDataValue(xxlRegistryData.getBiz(), xxlRegistryData.getEnv(), xxlRegistryData.getKey(), xxlRegistryData.getValue());

                                // valid file status
                                RegistryDO fileXxlRegistry = getFileRegistryData(xxlRegistryData);
                                if (fileXxlRegistry == null) {
                                    // go on
                                } else if (fileXxlRegistry.getStatus() != 0) {
                                    continue;   // "Status limited."
                                } else {
                                    if (!fileXxlRegistry.getDataList().contains(xxlRegistryData.getValue())) {
                                        continue;   // "Repeated limited."
                                    }
                                }

                                // checkRegistryDataAndSendMessage
                                checkRegistryDataAndSendMessage(xxlRegistryData);
                            }
                        } catch (Exception e) {
                            if (!executorStoped) {
                                logger.error(e.getMessage(), e);
                            }
                        }
                    }
                }
            });
        }

        /**
         * broadcast new one registry-data-file     (1/1s)
         *
         * clean old message   (1/10s)
         */
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                while (!executorStoped) {
                    try {
                        // new message, filter readed
                        List<RegistryMessageDO> messageList = registryMessageMapper.findMessage(readedMessageIds);
                        if (messageList != null && messageList.size() > 0) {
                            for (RegistryMessageDO message : messageList) {
                                readedMessageIds.add(message.getId());

                                // from registry、add、update、deelete，ne need sync from db, only write
                                if (message.getType() == 0) {

                                    RegistryDO registryDO = JacksonUtil.readValue(message.getData(), RegistryDO.class);

                                    // process data by status
                                    if (registryDO.getStatus() == 1) {
                                        // locked, not updated
                                    } else if (registryDO.getStatus() == 2) {
                                        // disabled, write empty
                                        registryDO.setData(JacksonUtil.writeValueAsString(new ArrayList<String>()));
                                    } else {
                                        // default, sync from db （aready sync before message, only write）
                                    }

                                    // sync file
                                    setFileRegistryData(registryDO);
                                }
                            }
                        }

                        // clean old message;
                        if ((System.currentTimeMillis() / 1000) % registryBeatTime == 0) {
                            registryMessageMapper.cleanMessage(registryBeatTime);
                            readedMessageIds.clear();
                        }
                    } catch (Exception e) {
                        if (!executorStoped) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (Exception e) {
                        if (!executorStoped) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                }
            }
        });

        /**
         *  clean old registry-data     (1/10s)
         *
         *  sync total registry-data db + file      (1+N/10s)
         *
         *  clean old registry-data file
         */
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                while (!executorStoped) {
                    try {
                        // clean old registry-data in db
                        registryNodeMapper.cleanData(registryBeatTime * 3);

                        // sync registry-data, db + file
                        int offset = 0;
                        int pagesize = 1000;
                        List<String> registryDataFileList = new ArrayList<>();

                        List<RegistryDO> registryList = registryMapper.pageList(offset, pagesize, null, null, null);
                        while (registryList != null && registryList.size() > 0) {

                            for (RegistryDO registryItem : registryList) {

                                // process data by status
                                if (registryItem.getStatus() == 1) {
                                    // locked, not updated
                                } else if (registryItem.getStatus() == 2) {
                                    // disabled, write empty
                                    String dataJson = JacksonUtil.writeValueAsString(new ArrayList<String>());
                                    registryItem.setData(dataJson);
                                } else {
                                    // default, sync from db
                                    List<RegistryNodeDO> xxlRegistryDataList = registryNodeMapper.findData(registryItem.getBiz(), registryItem.getEnv(), registryItem.getKey());
                                    List<String> valueList = new ArrayList<String>();
                                    if (xxlRegistryDataList != null && xxlRegistryDataList.size() > 0) {
                                        for (RegistryNodeDO dataItem : xxlRegistryDataList) {
                                            valueList.add(dataItem.getValue());
                                        }
                                    }
                                    String dataJson = JacksonUtil.writeValueAsString(valueList);

                                    // check update, sync db
                                    if (!registryItem.getData().equals(dataJson)) {
                                        registryItem.setData(dataJson);
                                        registryItem.setVersion(UUID.randomUUID().toString().replaceAll("-", ""));
                                        registryMapper.update(registryItem);
                                    }
                                }

                                // sync file
                                String registryDataFile = setFileRegistryData(registryItem);

                                // collect registryDataFile
                                registryDataFileList.add(registryDataFile);
                            }


                            offset += 1000;
                            registryList = registryMapper.pageList(offset, pagesize, null, null, null);
                        }

                        // clean old registry-data file
                        cleanFileRegistryData(registryDataFileList);

                    } catch (Exception e) {
                        if (!executorStoped) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                    try {
                        TimeUnit.SECONDS.sleep(registryBeatTime);
                    } catch (Exception e) {
                        if (!executorStoped) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void destroy() throws Exception {
        executorStoped = true;
        executorService.shutdownNow();
    }


    // ------------------------ file opt ------------------------
    // get
    public RegistryDO getFileRegistryData(RegistryNodeDO xxlRegistryData) {

        // fileName
        String fileName = parseRegistryDataFileName(xxlRegistryData.getBiz(), xxlRegistryData.getEnv(), xxlRegistryData.getKey());

        // read
        Properties prop = PropUtil.loadProp(fileName);
        if (prop != null) {
            RegistryDO fileXxlRegistry = new RegistryDO();
            fileXxlRegistry.setData(prop.getProperty("data"));
            fileXxlRegistry.setStatus(Integer.valueOf(prop.getProperty("status")));
            fileXxlRegistry.setDataList(JacksonUtil.readValue(fileXxlRegistry.getData(), List.class));
            return fileXxlRegistry;
        }
        return null;
    }

    private String parseRegistryDataFileName(String biz, String env, String key) {
        // fileName
        String fileName = registryDataFilePath
                .concat(File.separator).concat(biz)
                .concat(File.separator).concat(env)
                .concat(File.separator).concat(key)
                .concat(".properties");
        return fileName;
    }

    // set
    public String setFileRegistryData(RegistryDO xxlRegistry) {

        // fileName
        String fileName = parseRegistryDataFileName(xxlRegistry.getBiz(), xxlRegistry.getEnv(), xxlRegistry.getKey());

        // valid repeat update
        Properties existProp = PropUtil.loadProp(fileName);
        if (existProp != null && existProp.getProperty("data").equals(xxlRegistry.getData()) && existProp.getProperty("status").equals(String.valueOf(xxlRegistry.getStatus()))) {
            return new File(fileName).getPath();
        }

        // write
        Properties prop = new Properties();
        prop.setProperty("data", xxlRegistry.getData());
        prop.setProperty("status", String.valueOf(xxlRegistry.getStatus()));

        PropUtil.writeProp(prop, fileName);

        logger.info(">>>>>>>>>>> xxl-registry, setFileRegistryData: biz={}, env={}, key={}, data={}", xxlRegistry.getBiz(), xxlRegistry.getEnv(), xxlRegistry.getKey(), xxlRegistry.getData());


        // brocast monitor client
        List<DeferredResult> deferredResultList = registryDeferredResultMap.get(fileName);
        if (deferredResultList != null) {
            registryDeferredResultMap.remove(fileName);
            for (DeferredResult deferredResult : deferredResultList) {
                deferredResult.setResult(new ReturnT<>(ReturnT.FAIL_CODE, "Monitor key update."));
            }
        }

        return new File(fileName).getPath();
    }

    // clean
    public void cleanFileRegistryData(List<String> registryDataFileList) {
        filterChildPath(new File(registryDataFilePath), registryDataFileList);
    }

    public void filterChildPath(File parentPath, final List<String> registryDataFileList) {
        if (!parentPath.exists() || parentPath.list() == null || parentPath.list().length == 0) {
            return;
        }
        File[] childFileList = parentPath.listFiles();
        for (File childFile : childFileList) {
            if (childFile.isFile() && !registryDataFileList.contains(childFile.getPath())) {
                childFile.delete();

                logger.info(">>>>>>>>>>> xxl-registry, cleanFileRegistryData, RegistryData Path={}", childFile.getPath());
            }
            if (childFile.isDirectory()) {
                if (parentPath.listFiles() != null && parentPath.listFiles().length > 0) {
                    filterChildPath(childFile, registryDataFileList);
                } else {
                    childFile.delete();
                }
            }
        }
    }
}
