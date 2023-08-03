package com.xxl.job.core.util;

import com.xxl.job.core.biz.model.ReturnT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class XxlJobRemotingUtil {
    private static Logger logger = LoggerFactory.getLogger(XxlJobRemotingUtil.class);

    public static final String XXL_JOB_ACCESS_TOKEN = "XXL-JOB-ACCESS-TOKEN";

    private static void trustAllHosts(HttpsURLConnection connection) {
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
            SSLSocketFactory newFactory = sc.getSocketFactory();
            connection.setSSLSocketFactory(newFactory);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        connection.setHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
    }

    private static final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }};

    public static ReturnT postBody(String url, String accessToken, int timeout, Object requestObj, Class returnTagClassOfT) {
        HttpURLConnection connection = null;
        BufferedReader bufferedReader = null;
        URL readUrl = null;
        try {
            readUrl = new URL(url);
            connection = (HttpURLConnection) readUrl.openConnection();
            //判断是不是https开头的
            boolean useHttps = url.startsWith("https");
            if (useHttps) {
                HttpsURLConnection https = (HttpsURLConnection) connection;
                trustAllHosts(https);
            }
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setReadTimeout(timeout * 1000);
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            connection.setRequestProperty("Accept-Charset", "application/json;charset=UTF-8");
            //判断令牌是否为空
            if (accessToken != null && accessToken.trim().length() > 0) {
                connection.setRequestProperty(XXL_JOB_ACCESS_TOKEN, accessToken);
            }
            //进行链接
            connection.connect();
            if (requestObj != null) {
                //序列化请求体，也就是要发送的触发器参数
                String requestBody = GsonTool.toJson(requestObj);
                //下面就开始正式发送消息了
                DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
                dataOutputStream.write(requestBody.getBytes("UTF-8"));
                //刷新缓冲区
                dataOutputStream.flush();
                //释放资源
                dataOutputStream.close();
            }
            //获取响应码
            int statusCode = connection.getResponseCode();
            if (statusCode != 200) {
                //设置失败结果
                return new ReturnT<String>(ReturnT.FAIL_CODE, "xxl-job remoting fail, StatusCode(" + statusCode + ")");

            }
            bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }
            String resultJson = result.toString();
            try {
                ReturnT returnT = GsonTool.fromJson(resultJson, ReturnT.class, returnTagClassOfT);
                return returnT;
            } catch (Exception e) {
                logger.error("xxl-job remoting (url=" + url + ") response content invalid(" + resultJson + ").", e);
                return new ReturnT<String>(ReturnT.FAIL_CODE, "xxl-job remoting (url=" + url + ") response content invalid(" + resultJson + ").");
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new ReturnT<String>(ReturnT.FAIL_CODE, "xxl-job remoting error(" + e.getMessage() + "), for url : " + url);
        }finally {
            try{
                if(bufferedReader != null){
                    bufferedReader.close();
                }
                if(connection != null){
                    connection.disconnect();
                }
            } catch (IOException e2) {
                logger.error(e2.getMessage(), e2);
            }
        }
    }
}