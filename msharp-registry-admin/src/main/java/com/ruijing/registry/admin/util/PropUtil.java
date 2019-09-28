package com.ruijing.registry.admin.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.util.Properties;

/**
 * RegistryServiceImpl
 *
 * @author mwup
 * @version 1.0
 * @created 2019/07/23 17:03
 **/
public class PropUtil {

    private static Logger logger = LoggerFactory.getLogger(PropUtil.class);

    /**
     * load prop
     *
     * @param propertyFileName disk path when start with "file:", other classpath
     * @return
     */
    public static Properties loadProp(String propertyFileName) {
        InputStream in = null;
        try {
            // load file location, disk
            File file = new File(propertyFileName);
            if (!file.exists()) {
                return null;
            }
            URL url = new File(propertyFileName).toURI().toURL();
            in = new FileInputStream(url.getPath());
            if (in == null) {
                return null;
            }

            Properties prop = new Properties();
            prop.load(new InputStreamReader(in, "utf-8"));

            return prop;
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return null;
    }

    /**
     * write prop to disk
     *
     * @param properties
     * @param filePathName
     * @return
     */
    public static boolean writeProp(Properties properties, String filePathName){
        FileOutputStream fileOutputStream = null;
        try {

            // mk file
            File file = new File(filePathName);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
            }

            // write data
            fileOutputStream = new FileOutputStream(file, false);
            properties.store(new OutputStreamWriter(fileOutputStream, "utf-8"), null);
            return true;
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return false;
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

}
