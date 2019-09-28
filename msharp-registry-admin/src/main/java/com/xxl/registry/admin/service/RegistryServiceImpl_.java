package com.xxl.registry.admin.service;

import com.ruijing.fundamental.cat.Cat;
import com.ruijing.fundamental.common.collections.New;
import com.xxl.registry.admin.data.mapper.RegistryMapper;
import com.xxl.registry.admin.data.mapper.RegistryMessageMapper;
import com.xxl.registry.admin.data.mapper.RegistryNodeMapper;
import com.xxl.registry.admin.data.model.RegistryDO;
import com.xxl.registry.admin.data.model.RegistryMessageDO;
import com.xxl.registry.admin.data.model.RegistryNodeDO;
import com.xxl.registry.admin.model.ReturnT;
import com.xxl.registry.admin.util.JacksonUtil;
import com.xxl.registry.admin.util.JsonUtils;
import com.xxl.registry.admin.util.PropUtil;
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
public class RegistryServiceImpl_ implements InitializingBean, DisposableBean {

    private static Logger logger = LoggerFactory.getLogger(RegistryServiceImpl_.class);

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

    /**
     * send RegistryData Update Message
     */
    private void sendRegistryDataUpdateMessage(RegistryDO registryDO) {
        String registryUpdateJson = JsonUtils.toJson(registryDO);
        RegistryMessageDO registryMessage = new RegistryMessageDO();
        registryMessage.setType(0);
        registryMessage.setData(registryUpdateJson);
        registryMessageMapper.add(registryMessage);
    }


    /**
     * update Registry And Message
     */
    private void checkRegistryDataAndSendMessage(RegistryNodeDO registryNode) {
        // data json
        List<RegistryNodeDO> registryNodeDOList = registryNodeMapper.findData(registryNode.getBiz(), registryNode.getEnv(), registryNode.getKey());
        final List<String> valueList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(registryNodeDOList)) {
            for (int i = 0, size = registryNodeDOList.size(); i < size; i++) {
                final RegistryNodeDO registryNodeDO = registryNodeDOList.get(i);
                valueList.add(registryNodeDO.getValue());
            }
        }
        final String dataJson = JsonUtils.toJson(valueList);

        // update registry and message
        RegistryDO registryDO = registryMapper.load(registryNode.getBiz(), registryNode.getEnv(), registryNode.getKey());
        boolean needMessage = false;
        if (registryDO == null) {
            registryDO = new RegistryDO();
            registryDO.setBiz(registryNode.getBiz());
            registryDO.setEnv(registryNode.getEnv());
            registryDO.setKey(registryNode.getKey());
            registryDO.setData(dataJson);
            registryDO.setVersion(UUID.randomUUID().toString().replaceAll("-", ""));
            registryMapper.add(registryDO);
            needMessage = true;
        } else {

            // check status, locked and disabled not use
            if (registryDO.getStatus() != 0) {
                return;
            }

            if (!registryDO.getData().equals(dataJson)) {
                registryDO.setData(dataJson);
                registryDO.setVersion(UUID.randomUUID().toString().replaceAll("-", ""));
                registryMapper.update(registryDO);
                needMessage = true;
            }
        }

        if (needMessage) {
            // sendRegistryDataUpdateMessage (registry update)
            sendRegistryDataUpdateMessage(registryDO);
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

    }

    public void afterPropertiesSet0() throws Exception {
        /**
         * registry registry data         (client-num/10 s)
         */
        for (int i = 0; i < 5; i++) {

            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    while (!executorStoped) {
                        try {
                            RegistryNodeDO registryNode = registryQueue.take();
                            if (registryNode != null) {
                                // refresh or add
                                int ret = registryNodeMapper.refresh(registryNode);
                                if (ret == 0) {
                                    registryNodeMapper.add(registryNode);
                                }

                                // valid file status
                                RegistryDO fileXxlRegistry = getFileRegistryData(registryNode);
                                if (fileXxlRegistry == null) {
                                    // go on
                                } else if (fileXxlRegistry.getStatus() != 0) {
                                    continue;     // "Status limited."
                                } else {
                                    if (fileXxlRegistry.getDataList().contains(registryNode.getValue())) {
                                        continue;     // "Repeated limited."
                                    }
                                }

                                // checkRegistryDataAndSendMessage
                                checkRegistryDataAndSendMessage(registryNode);
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
            this.executorService.execute(new Runnable() {
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
