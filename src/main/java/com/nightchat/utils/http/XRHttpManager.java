package com.nightchat.utils.http;

import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

public interface XRHttpManager {
    XRHttpResponse httpGet(String url) throws IOException;

    XRHttpResponse httpGet(String url, Map<String, String> Header) throws IOException;

    XRHttpResponse httpPost(String url, Map<String, String> params) throws IOException;

    XRHttpResponse httpPost(String url, List<BasicNameValuePair> params) throws IOException;

    XRHttpResponse httpJsonPost(String url, String json) throws IOException;

    XRHttpResponse httpJsonPost(String url, String json, Map<String, String> Header) throws IOException;

    XRHttpResponse httpMultiPost(String url, Map<String, Object> params) throws IOException;

    XRHttpResponse httpPost(String url, byte[] params) throws IOException;

    XRHttpResponse httpMultiPost(String url, Map<String, Object> params, Charset charset) throws IOException;

    XRHttpResponse httpPost(String url, List<BasicNameValuePair> params, Map<String, String> Header) throws IOException;
}