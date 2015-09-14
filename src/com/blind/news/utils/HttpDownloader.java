package com.blind.news.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.BufferUnderflowException;
import java.util.zip.GZIPInputStream;

/**
 * Created by Sophia.
 */
public class HttpDownloader {
    private URL url = null;
    /**
     *  根据URL下载文件，前提是这个文件当中的内容是文本，函数的返回值就是文件当中的内容
     * 1.创建一个URL对象
     * 2.通过URL对象，创建一个HttpURLConnection对象
     * 3.得到InputStram
     * 4.从InputStream当中读取数据
     * @param urlStr isGzip是否压缩格式
     * */
    public String download(String urlStr, String decoder, boolean isGzip){
        StringBuffer sBuffer = new StringBuffer();
        String line = null;
        BufferedReader buffer = null;
        try {
            url = new URL(urlStr);
            /*Create a network connection*/
            HttpURLConnection urlConn = (HttpURLConnection)url.openConnection();
            if (urlConn.getResponseCode() !=200) throw new RuntimeException("请求url失败");
            /*Read Data by IO Stream*/
            if (isGzip){
                buffer = new BufferedReader(new InputStreamReader(new GZIPInputStream(
                        urlConn.getInputStream()),decoder));
            }else {
                buffer = new BufferedReader(new InputStreamReader(urlConn.getInputStream(),decoder));
            }
            while ((line = buffer.readLine())!=null){
                sBuffer.append(line);
                System.out.println(line);
            }
        }catch (Exception e ){
            e.printStackTrace();
        }finally {
            try {
                buffer.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return sBuffer.toString();
    }

 }
