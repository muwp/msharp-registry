package com.xxl.registry.admin.service.impl;

import com.xxl.registry.admin.core.model.XxlRegistry;
import com.xxl.registry.admin.core.model.XxlRegistryData;
import com.xxl.registry.admin.core.model.XxlRegistryMessage;
import com.xxl.registry.admin.core.result.ReturnT;
import com.xxl.registry.admin.core.util.JacksonUtil;
import com.xxl.registry.admin.core.util.PropUtil;
import com.xxl.registry.admin.dao.IXxlRegistryDao;
import com.xxl.registry.admin.dao.IXxlRegistryDataDao;
import com.xxl.registry.admin.dao.IXxlRegistryMessageDao;
import com.xxl.registry.admin.service.IXxlRegistryService;
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
public class XxlRegistryServiceImpl implements IXxlRegistryService, InitializingBean, DisposableBean {
    private static Logger logger = LoggerFactory.getLogger(XxlRegistryServiceImpl.class);


    @Resource
    private IXxlRegistryDao xxlRegistryDao;
    @Resource
    private IXxlRegistryDataDao xxlRegistryDataDao;
    @Resource
    private IXxlRegistryMessageDao xxlRegistryMessageDao;

    @Value("${xxl.registry.data.filepath}")
    private String registryDataFilePath;
    @Value("${xxl.registry.beattime}")
    private int registryBeatTime;


    @Override
    public Map<String, Object> pageList(int start, int length, String biz, String env, String key) {

        // page list
        List<XxlRegistry> list = xxlRegistryDao.pageList(start, length, biz, env, key);
        int list_count = xxlRegistryDao.pageListCount(start, length, biz, env, key);

        // package result
        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("recordsTotal", list_count);		// 总记录数
        maps.put("recordsFiltered", list_count);	// 过滤后的总记录数
        maps.put("data", list);  					// 分页列表
        return maps;
    }

    @Override
    public ReturnT<String> delete(int id) {
        XxlRegistry xxlRegistry = xxlRegistryDao.loadById(id);
        if (xxlRegistry != null) {
            xxlRegistryDao.delete(id);
            xxlRegistryDataDao.deleteData(xxlRegistry.getBiz(), xxlRegistry.getEnv(), xxlRegistry.getKey());

            // sendRegistryDataUpdateMessage (delete)
            xxlRegistry.setData("");
            xxlRegistry.setVersion(UUID.randomUUID().toString().replaceAll("-", ""));
            sendRegistryDataUpdateMessage(xxlRegistry);
        }

        return ReturnT.SUCCESS;
    }

    /**
     * send RegistryData Update Message
     */
    private void sendRegistryDataUpdateMessage(XxlRegistry xxlRegistry){
        String registryUpdateJson = JacksonUtil.writeValueAsString(xxlRegistry);

        XxlRegistryMessage registryMessage = new XxlRegistryMessage();
        registryMessage.setType(0);
        registryMessage.setData(registryUpdateJson);
        xxlRegistryMessageDao.add(registryMessage);
    }

    @Override
    public ReturnT<String> update(XxlRegistry xxlRegistry) {

        // valid
        if (xxlRegistry.getBiz()==null || xxlRegistry.getBiz().trim().length()<4 ||xxlRegistry.getBiz().trim().length()>255) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "业务线格式非法[4~255]");
        }
        if (xxlRegistry.getEnv()==null || xxlRegistry.getEnv().trim().length()<4 || xxlRegistry.getEnv().trim().length()>255 ) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "环境格式非法[4~255]");
        }
        if (xxlRegistry.getKey()==null || xxlRegistry.getKey().trim().length()<4 || xxlRegistry.getKey().trim().length()>255) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "注册Key格式非法[4~255]");
        }
        if (xxlRegistry.getData()==null || xxlRegistry.getData().trim().length()==0) {
            xxlRegistry.setData(JacksonUtil.writeValueAsString(new ArrayList<String>()));
        }
        List<String> valueList = JacksonUtil.readValue(xxlRegistry.getData(), List.class);
        if (valueList == null) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "注册Value数据格式非法；限制为字符串数组JSON格式，如 [address,address2]");
        }

        // valid exist
        XxlRegistry exist = xxlRegistryDao.loadById(xxlRegistry.getId());
        if (exist == null) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "ID参数非法");
        }

        // fill version
        boolean needMessage = false;
        if (!xxlRegistry.getData().equals(exist.getData())) {
            xxlRegistry.setVersion(UUID.randomUUID().toString().replaceAll("-", ""));
            needMessage = true;
        } else {
            xxlRegistry.setVersion(exist.getVersion());
        }

        int ret = xxlRegistryDao.update(xxlRegistry);
        needMessage = ret>0?needMessage:false;

        if (needMessage) {
            // sendRegistryDataUpdateMessage (update)
            sendRegistryDataUpdateMessage(xxlRegistry);
        }

        return ret>0?ReturnT.SUCCESS:ReturnT.FAIL;
    }

    @Override
    public ReturnT<String> add(XxlRegistry xxlRegistry) {

        // valid
        if (xxlRegistry.getBiz()==null || xxlRegistry.getBiz().trim().length()<4 ||xxlRegistry.getBiz().trim().length()>255) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "业务线格式非法[4~255]");
        }
        if (xxlRegistry.getEnv()==null || xxlRegistry.getEnv().trim().length()<4 || xxlRegistry.getEnv().trim().length()>255 ) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "环境格式非法[4~255]");
        }
        if (xxlRegistry.getKey()==null || xxlRegistry.getKey().trim().length()<4 || xxlRegistry.getKey().trim().length()>255) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "注册Key格式非法[4~255]");
        }
        if (xxlRegistry.getData()==null || xxlRegistry.getData().trim().length()==0) {
            xxlRegistry.setData(JacksonUtil.writeValueAsString(new ArrayList<String>()));
        }
        List<String> valueList = JacksonUtil.readValue(xxlRegistry.getData(), List.class);
        if (valueList == null) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "注册Value数据格式非法；限制为字符串数组JSON格式，如 [address,address2]");
        }

        // valid exist
        XxlRegistry exist = xxlRegistryDao.load(xxlRegistry.getBiz(), xxlRegistry.getEnv(), xxlRegistry.getKey());
        if (exist != null) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "注册Key请勿重复");
        }

        // fill version
        xxlRegistry.setVersion(UUID.randomUUID().toString().replaceAll("-", ""));

        int ret = xxlRegistryDao.add(xxlRegistry);
        boolean needMessage = ret>0?true:false;

        if (needMessage) {
            // sendRegistryDataUpdateMessage (add)
            sendRegistryDataUpdateMessage(xxlRegistry);
        }

        return ret>0?ReturnT.SUCCESS:ReturnT.FAIL;
    }


    // ------------------------ remote registry ------------------------

    @Override
    public ReturnT<String> registry(String biz, String env, List<XxlRegistryData> registryDataList) {

        // valid
        if (biz==null || biz.trim().length()<4 || biz.trim().length()>255) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "Biz Invalid[4~255]");
        }
        if (env==null || env.trim().length()<4 || env.trim().length()>255) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "Env Invalid[4~255]");
        }
        if (registryDataList==null || registryDataList.size()==0) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "Registry DataList Invalid");
        }
        for (XxlRegistryData registryData: registryDataList) {
            if (registryData.getKey()==null || registryData.getKey().trim().length()<4 || registryData.getKey().trim().length()>255) {
                return new ReturnT<String>(ReturnT.FAIL_CODE, "Registry Key Invalid[4~255]");
            }
            if (registryData.getValue()==null || registryData.getValue().trim().length()<4 || registryData.getValue().trim().length()>255) {
                return new ReturnT<String>(ReturnT.FAIL_CODE, "Registry Value Invalid[4~255]");
            }
        }

        // fill + add queue
        for (XxlRegistryData registryData: registryDataList) {
            registryData.setBiz(biz);
            registryData.setEnv(env);
        }
        registryQueue.addAll(registryDataList);

        return ReturnT.SUCCESS;
    }

    @Override
    public ReturnT<String> remove(String biz, String env, List<XxlRegistryData> registryDataList) {

        // valid
        if (biz==null || biz.trim().length()<4 || biz.trim().length()>255) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "Biz Invalid[4~255]");
        }
        if (env==null || env.trim().length()<4 || env.trim().length()>255) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "Env Invalid[4~255]");
        }
        if (registryDataList==null || registryDataList.size()==0) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "Registry DataList Invalid");
        }
        for (XxlRegistryData registryData: registryDataList) {
            if (registryData.getKey()==null || registryData.getKey().trim().length()<4 || registryData.getKey().trim().length()>255) {
                return new ReturnT<String>(ReturnT.FAIL_CODE, "Registry Key Invalid[4~255]");
            }
            if (registryData.getValue()==null || registryData.getValue().trim().length()<4 || registryData.getValue().trim().length()>255) {
                return new ReturnT<String>(ReturnT.FAIL_CODE, "Registry Value Invalid[4~255]");
            }
        }

        // fill + add queue
        for (XxlRegistryData registryData: registryDataList) {
            registryData.setBiz(biz);
            registryData.setEnv(env);
        }
        removeQueue.addAll(registryDataList);

        return ReturnT.SUCCESS;
    }

    @Override
    public ReturnT<Map<String, List<String>>> discovery(String biz, String env, List<String> keys) {

        // valid
        if (biz==null || biz.trim().length()<4 || biz.trim().length()>255) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "Biz Invalid[4~255]");
        }
        if (env==null || env.trim().length()<4 || env.trim().length()>255) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "Env Invalid[4~255]");
        }
        if (keys==null || keys.size()==0) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "keys Invalid.");
        }
        for (String key: keys) {
            if (key==null || key.trim().length()<4 || key.trim().length()>255) {
                return new ReturnT<>(ReturnT.FAIL_CODE, "Key Invalid[4~255]");
            }
        }

        Map<String, List<String>> result = new HashMap<String, List<String>>();
        for (String key: keys) {
            XxlRegistryData xxlRegistryData = new XxlRegistryData();
            xxlRegistryData.setBiz(biz);
            xxlRegistryData.setEnv(env);
            xxlRegistryData.setKey(key);

            List<String> dataList = new ArrayList<String>();
            XxlRegistry fileXxlRegistry = getFileRegistryData(xxlRegistryData);
            if (fileXxlRegistry!=null) {
                dataList = fileXxlRegistry.getDataList();
            }

            result.put(key, dataList);
        }

        return new ReturnT<Map<String, List<String>>>(result);
    }

    @Override
    public DeferredResult<ReturnT<String>> monitor(String biz, String env, List<String> keys) {

        // init
        DeferredResult deferredResult = new DeferredResult(registryBeatTime * 3 * 1000L, new ReturnT<>(ReturnT.FAIL_CODE, "Monitor timeout."));

        // valid
        if (biz==null || biz.trim().length()<4 || biz.trim().length()>255) {
            deferredResult.setResult(new ReturnT<>(ReturnT.FAIL_CODE, "Biz Invalid[4~255]"));
            return deferredResult;
        }
        if (env==null || env.trim().length()<4 || env.trim().length()>255) {
            deferredResult.setResult(new ReturnT<>(ReturnT.FAIL_CODE, "Env Invalid[4~255]"));
            return deferredResult;
        }
        if (keys==null || keys.size()==0) {
            deferredResult.setResult(new ReturnT<>(ReturnT.FAIL_CODE, "keys Invalid."));
            return deferredResult;
        }
        for (String key: keys) {
            if (key==null || key.trim().length()<4 || key.trim().length()>255) {
                deferredResult.setResult(new ReturnT<>(ReturnT.FAIL_CODE, "Key Invalid[4~255]"));
                return deferredResult;
            }
        }

        // monitor by client
        for (String key: keys) {
            String fileName = parseRegistryDataFileName(biz, env, key);

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
    private void checkRegistryDataAndSendMessage(XxlRegistryData xxlRegistryData){
        // data json
        List<XxlRegistryData> xxlRegistryDataList = xxlRegistryDataDao.findData(xxlRegistryData.getBiz(), xxlRegistryData.getEnv(), xxlRegistryData.getKey());
        List<String> valueList = new ArrayList<>();
        if (xxlRegistryDataList!=null && xxlRegistryDataList.size()>0) {
            for (XxlRegistryData dataItem: xxlRegistryDataList) {
                valueList.add(dataItem.getValue());
            }
        }
        String dataJson = JacksonUtil.writeValueAsString(valueList);

        // update registry and message
        XxlRegistry xxlRegistry = xxlRegistryDao.load(xxlRegistryData.getBiz(), xxlRegistryData.getEnv(), xxlRegistryData.getKey());
        boolean needMessage = false;
        if (xxlRegistry == null) {
            xxlRegistry = new XxlRegistry();
            xxlRegistry.setBiz(xxlRegistryData.getBiz());
            xxlRegistry.setEnv(xxlRegistryData.getEnv());
            xxlRegistry.setKey(xxlRegistryData.getKey());
            xxlRegistry.setData(dataJson);
            xxlRegistry.setVersion(UUID.randomUUID().toString().replaceAll("-", ""));
            xxlRegistryDao.add(xxlRegistry);
            needMessage = true;
        } else {

            // check status, locked and disabled not use
            if (xxlRegistry.getStatus() != 0) {
                return;
            }

            if (!xxlRegistry.getData().equals(dataJson)) {
                xxlRegistry.setData(dataJson);
                xxlRegistry.setVersion(UUID.randomUUID().toString().replaceAll("-", ""));
                xxlRegistryDao.update(xxlRegistry);
                needMessage = true;
            }
        }

        if (needMessage) {
            // sendRegistryDataUpdateMessage (registry update)
            sendRegistryDataUpdateMessage(xxlRegistry);
        }

    }

    // ------------------------ broadcase + file data ------------------------

    private ExecutorService executorService = Executors.newCachedThreadPool();
    private volatile boolean executorStoped = false;
    private volatile List<Integer> readedMessageIds = Collections.synchronizedList(new ArrayList<Integer>());

    private volatile LinkedBlockingQueue<XxlRegistryData> registryQueue = new LinkedBlockingQueue<XxlRegistryData>();
    private volatile LinkedBlockingQueue<XxlRegistryData> removeQueue = new LinkedBlockingQueue<XxlRegistryData>();
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
                            XxlRegistryData xxlRegistryData = registryQueue.take();
                            if (xxlRegistryData !=null) {

                                // refresh or add
                                int ret = xxlRegistryDataDao.refresh(xxlRegistryData);
                                if (ret == 0) {
                                    xxlRegistryDataDao.add(xxlRegistryData);
                                }

                                // valid file status
                                XxlRegistry fileXxlRegistry = getFileRegistryData(xxlRegistryData);
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
                            XxlRegistryData xxlRegistryData = removeQueue.take();
                            if (xxlRegistryData != null) {

                                // delete
                                xxlRegistryDataDao.deleteDataValue(xxlRegistryData.getBiz(), xxlRegistryData.getEnv(), xxlRegistryData.getKey(), xxlRegistryData.getValue());

                                // valid file status
                                XxlRegistry fileXxlRegistry = getFileRegistryData(xxlRegistryData);
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
         * broadcase new one registry-data-file     (1/1s)
         *
         * clean old message   (1/10s)
         */
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                while (!executorStoped) {
                    try {
                        // new message, filter readed
                        List<XxlRegistryMessage> messageList = xxlRegistryMessageDao.findMessage(readedMessageIds);
                        if (messageList!=null && messageList.size()>0) {
                            for (XxlRegistryMessage message: messageList) {
                                readedMessageIds.add(message.getId());

                                if (message.getType() == 0) {   // from registry、add、update、deelete，ne need sync from db, only write

                                    XxlRegistry xxlRegistry = JacksonUtil.readValue(message.getData(), XxlRegistry.class);

                                    // process data by status
                                    if (xxlRegistry.getStatus() == 1) {
                                        // locked, not updated
                                    } else if (xxlRegistry.getStatus() == 2) {
                                        // disabled, write empty
                                        xxlRegistry.setData(JacksonUtil.writeValueAsString(new ArrayList<String>()));
                                    } else {
                                        // default, sync from db （aready sync before message, only write）
                                    }

                                    // sync file
                                    setFileRegistryData(xxlRegistry);
                                }
                            }
                        }

                        // clean old message;
                        if (System.currentTimeMillis() % registryBeatTime ==0) {
                            xxlRegistryMessageDao.cleanMessage(10);
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
                        xxlRegistryDataDao.cleanData(registryBeatTime * 3);

                        // sync registry-data, db + file
                        int offset = 0;
                        int pagesize = 1000;
                        List<String> registryDataFileList = new ArrayList<>();

                        List<XxlRegistry> registryList = xxlRegistryDao.pageList(offset, pagesize, null, null, null);
                        while (registryList!=null && registryList.size()>0) {

                            for (XxlRegistry registryItem: registryList) {

                                // process data by status
                                if (registryItem.getStatus() == 1) {
                                    // locked, not updated
                                } else if (registryItem.getStatus() == 2) {
                                    // disabled, write empty
                                    String dataJson = JacksonUtil.writeValueAsString(new ArrayList<String>());
                                    registryItem.setData(dataJson);
                                } else {
                                    // default, sync from db
                                    List<XxlRegistryData> xxlRegistryDataList = xxlRegistryDataDao.findData(registryItem.getBiz(), registryItem.getEnv(), registryItem.getKey());
                                    List<String> valueList = new ArrayList<String>();
                                    if (xxlRegistryDataList!=null && xxlRegistryDataList.size()>0) {
                                        for (XxlRegistryData dataItem: xxlRegistryDataList) {
                                            valueList.add(dataItem.getValue());
                                        }
                                    }
                                    String dataJson = JacksonUtil.writeValueAsString(valueList);

                                    // check update, sync db
                                    if (!registryItem.getData().equals(dataJson)) {
                                        registryItem.setData(dataJson);
                                        registryItem.setVersion(UUID.randomUUID().toString().replaceAll("-", ""));
                                        xxlRegistryDao.update(registryItem);
                                    }
                                }

                                // sync file
                                String registryDataFile = setFileRegistryData(registryItem);

                                // collect registryDataFile
                                registryDataFileList.add(registryDataFile);
                            }


                            offset += 1000;
                            registryList = xxlRegistryDao.pageList(offset, pagesize, null, null, null);
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
    public XxlRegistry getFileRegistryData(XxlRegistryData xxlRegistryData){

        // fileName
        String fileName = parseRegistryDataFileName(xxlRegistryData.getBiz(), xxlRegistryData.getEnv(), xxlRegistryData.getKey());

        // read
        Properties prop = PropUtil.loadProp(fileName);
        if (prop!=null) {
            XxlRegistry fileXxlRegistry = new XxlRegistry();
            fileXxlRegistry.setData(prop.getProperty("data"));
            fileXxlRegistry.setStatus(Integer.valueOf(prop.getProperty("status")));
            fileXxlRegistry.setDataList(JacksonUtil.readValue(fileXxlRegistry.getData(), List.class));
            return fileXxlRegistry;
        }
        return null;
    }
    private String parseRegistryDataFileName(String biz, String env, String key){
        // fileName
        String fileName = registryDataFilePath
                .concat(File.separator).concat(biz)
                .concat(File.separator).concat(env)
                .concat(File.separator).concat(key)
                .concat(".properties");
        return fileName;
    }

    // set
    public String setFileRegistryData(XxlRegistry xxlRegistry){

        // fileName
        String fileName = parseRegistryDataFileName(xxlRegistry.getBiz(), xxlRegistry.getEnv(), xxlRegistry.getKey());

        // valid repeat update
        Properties existProp = PropUtil.loadProp(fileName);
        if (existProp != null
                && existProp.getProperty("data").equals(xxlRegistry.getData())
                && existProp.getProperty("status").equals(String.valueOf(xxlRegistry.getStatus()))
                ) {
            return new File(fileName).getPath();
        }

        // write
        Properties prop = new Properties();
        prop.setProperty("data", xxlRegistry.getData());
        prop.setProperty("status", String.valueOf(xxlRegistry.getStatus()));

        PropUtil.writeProp(prop, fileName);

        logger.info(">>>>>>>>>>> xxl-registry, setFileRegistryData: biz={}, env={}, key={}, data={}"
                , xxlRegistry.getBiz(), xxlRegistry.getEnv(), xxlRegistry.getKey(), xxlRegistry.getData());


        // brocast monitor client
        List<DeferredResult> deferredResultList = registryDeferredResultMap.get(fileName);
        if (deferredResultList != null) {
            registryDeferredResultMap.remove(fileName);
            for (DeferredResult deferredResult: deferredResultList) {
                deferredResult.setResult(new ReturnT<>(ReturnT.FAIL_CODE, "Monitor key update."));
            }
        }

        return new File(fileName).getPath();
    }
    // clean
    public void cleanFileRegistryData(List<String> registryDataFileList){
        filterChildPath(new File(registryDataFilePath), registryDataFileList);
    }

    public void filterChildPath(File parentPath, final List<String> registryDataFileList){
        if (!parentPath.exists() || parentPath.list()==null || parentPath.list().length==0) {
            return;
        }
        File[] childFileList = parentPath.listFiles();
        for (File childFile: childFileList) {
            if (childFile.isFile() && !registryDataFileList.contains(childFile.getPath())) {
                childFile.delete();

                logger.info(">>>>>>>>>>> xxl-registry, cleanFileRegistryData, RegistryData Path={}", childFile.getPath());
            }
            if (childFile.isDirectory()) {
                if (parentPath.listFiles()!=null && parentPath.listFiles().length>0) {
                    filterChildPath(childFile, registryDataFileList);
                } else {
                    childFile.delete();
                }

            }
        }

    }

}
