package com.ruijing.registry.common.util;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * AES加密解密工具类
 *
 * @author zhengzhongyin
 * @date 2018-10-24
 * @version  1.0
 */
public class AESUtils {

    private static final Logger logger  = LoggerFactory.getLogger(AESUtils.class);
    /**
     * 密钥算法
     */
    private static final String KEY_ALGORITHM = "AES";
    /**
     * 加解密算法/工作模式/填充方式
     */
    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";
    /**
     * 用于生成密钥
     */
    private static final String KEY = "123";
    /**
     * 编码格式
     */
    private static final String ENCODE = "UTF-8";

    private static Cipher cipher;

    static {
        try {
            cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            logger.error("NoSuchAlgorithmException:Type{}",DEFAULT_CIPHER_ALGORITHM,e);
        } catch (NoSuchPaddingException e) {
            logger.error("NoSuchPaddingException:Type{}",DEFAULT_CIPHER_ALGORITHM,e);
        }
    }

    /**
     * AES 加密操作
     *
     * @param content 待加密内容
     * @return 返回Base64转码后的加密数据
     */
    public static String encrypt(String content) {
        if (null == content){
            return null;
        }
        try {
            byte[] byteContent = content.getBytes(ENCODE);
            // 初始化为加密模式的密码器
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(KEY));
            // 加密
            byte[] result = cipher.doFinal(byteContent);
            //通过Base64转码返回
            return Base64.encodeBase64String(result);
        } catch (Exception ex) {
            logger.error("encrypt error:",ex);
        }
        return null;
    }

    /**
     * AES 解密操作
     * @param content 解密字符串
     * @return
     */
    public static String decrypt(String content) {
        if (null == content){
            return null;
        }
        try {
            //使用密钥初始化，设置为解密模式
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(KEY));
            //执行操作
            byte[] result = cipher.doFinal(Base64.decodeBase64(content));
            return new String(result, ENCODE);
        } catch (Exception ex) {
            logger.error("decrypt error:",ex);
        }
        return null;
    }

    /**
     * 生成加密秘钥
     * @param password password
     * @return
     */
    private static SecretKeySpec getSecretKey(final String password) {
        //返回生成指定算法密钥生成器的 KeyGenerator 对象
        KeyGenerator kg = null;
        try {
            kg = KeyGenerator.getInstance(KEY_ALGORITHM);
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(password.getBytes(ENCODE));
            //AES 要求密钥长度为 128
            kg.init(128, random);
            //生成一个密钥
            SecretKey secretKey = kg.generateKey();
            // 转换为AES专用密钥
            return new SecretKeySpec(secretKey.getEncoded(), KEY_ALGORITHM);
        } catch (NoSuchAlgorithmException ex) {
            logger.error("NoSuchAlgorithmException:Type{}",KEY_ALGORITHM,ex);
        } catch (UnsupportedEncodingException e) {
            logger.error("UnsupportedEncodingException:code-type{}",ENCODE,e);
        }
        return null;
    }
}
