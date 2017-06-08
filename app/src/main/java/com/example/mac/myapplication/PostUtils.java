package com.example.mac.myapplication;


import android.util.Log;

import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

import static android.content.ContentValues.TAG;

/**
 * Created by mac on 17/4/20.
 */

public class PostUtils {
    /**
     *  以get方式向服务端发送请求，并将服务端的响应结果以字符串方式返回。如果没有响应内容则返回空字符串
     * @param url 请求的url地址
     * @param params 请求参数
     * @param charset url编码采用的码表
     * @return
     */
    public static String localhost="192.168.1.106";
    public static String getDataByGet(String url,Map<String,String>params,String charset) {
        if (url == null) {
            return null;
        }
        url = url.trim();
        URL targetUrl = null;
        try {
            if (params == null) {
                targetUrl = new URL(url);
            } else {
                StringBuilder sb = new StringBuilder(url + "?");
                for (Map.Entry<String, String> me : params.entrySet()) {
                    //解决请求参数中含有中文导致乱码问题
                    sb.append(me.getKey()).append("=").append(URLEncoder.encode(
                            me.getValue(), charset)).append("&");
                }
                sb.deleteCharAt(sb.length() - 1);
                targetUrl = new URL(sb.toString());
            }
            Log.i(TAG, "get:url----->" + targetUrl.toString());//打印log
            HttpURLConnection conn = (HttpURLConnection) targetUrl.openConnection();
            conn.setConnectTimeout(3000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                String ss=stream2String(conn.getInputStream(), charset);
               // Log.d("response.get",ss);
                return ss;
            }
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
        return null;
    }
/**
 *  以post方式向服务端发送请求，并将服务端的响应结果以字符串方式返回。如果没有响应内容则返回空字符串
 * @param url 请求的url地址
 * @param params 请求参数
 * @param charset url编码采用的码表
 * @return
 */
    public static String getDataByPost(String url,Map<String,String>params,String charset) {
        if(url == null)
        {return null;}
        url = url.trim();
        URL targetUrl = null;
        OutputStream out = null;
        try{
            targetUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) targetUrl.openConnection();
            conn.setConnectTimeout(3000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            StringBuilder sb = new StringBuilder();
            if(params!=null && !params.isEmpty()) {
                for(Map.Entry<String,String> me : params.entrySet()) {
                    // 对请求数据中的中文进行编码
                    sb.append(me.getKey()).append("=").append(URLEncoder.encode(
                            me.getValue(),charset)).append("&");
                }
                sb.deleteCharAt(sb.length()-1);
            }
            byte[] data = sb.toString().getBytes();
            conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length",String.valueOf(data.length));
            out = conn.getOutputStream();
            out.write(data);
            Log.i(TAG,"post:url----->"+targetUrl.toString());//打印log

            int responseCode = conn.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK)
            {
                String ss=stream2String(conn.getInputStream(),charset);
                return ss;
            }
        } catch (Exception e) {
            Log.i(TAG,e.getMessage());
        }
        return null;
    }
    /**
     * 将输入流对象中的数据输出到字符串中返回
     * @param in
     * @return
     * @throws
     */
    private static String stream2String(InputStream in,String charset) throws IOException
    {
        if(in == null)
            return null;
        byte[] buffer = new byte[1024];
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        int len = 0;
        while((len = in.read(buffer)) !=-1)
        {
            bout.write(buffer, 0, len);
        }
        String result = new String(bout.toByteArray(),charset);
        in.close();
        return result;
    }
}

