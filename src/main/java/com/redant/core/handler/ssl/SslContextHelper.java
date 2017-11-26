package com.redant.core.handler.ssl;

import com.xiaoleilu.hutool.crypto.SecureUtil;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author gris.wang
 * @since 2017/11/19
 **/
public class SslContextHelper {

    private static final String KEY_STORE_JKS = "JKS";

    private static final String ALGORITHM = "SunX509";

    private static Map<String,SslContext> contents = new ConcurrentHashMap<String,SslContext>();

    private static String getKey(String keyPath,String keyPassword){
        if(keyPath==null || keyPath.trim().length()==0 || keyPassword==null || keyPassword.trim().length()==0){
            return null;
        }
        String keyStr = keyPath+"&"+keyPassword;
        return SecureUtil.md5(keyStr);
    }

    /**
     * 获取SslContext
     * @param keyPath
     * @param keyPassword
     * @return
     */
    public static SslContext getSslContext(String keyPath,String keyPassword){
        if(keyPath==null || keyPath.trim().length()==0 || keyPassword==null || keyPassword.trim().length()==0){
            return null;
        }
        SslContext sslContext = null;
        InputStream is = null;
        try {
            String key = getKey(keyPath,keyPassword);
            sslContext = contents.get(key);
            if(sslContext!=null){
                return sslContext;
            }

            KeyStore keyStore = KeyStore.getInstance(KEY_STORE_JKS);
            is = new FileInputStream(keyPath);
            keyStore.load(is, keyPassword.toCharArray());

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(ALGORITHM);
            keyManagerFactory.init(keyStore,keyPassword.toCharArray());

            sslContext = SslContextBuilder.forServer(keyManagerFactory).build();
            if(sslContext!=null){
                contents.put(key,sslContext);
            }
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (SSLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(is!=null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sslContext;
    }

}
