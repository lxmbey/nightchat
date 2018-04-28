package com.nightchat.utils.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import com.nightchat.utils.StringUtils;

public class HttpClientManager implements XRHttpManager {

    private HttpClient httpClient;

    public HttpClientManager(int maxPreRoute, int maxTotal)
            throws IOException, KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException, CertificateException, KeyManagementException {
        this(maxPreRoute, maxTotal, null, null, null, null);
    }

    public HttpClientManager(int maxPreRoute, int maxTotal, String keyStoreFile, String keyStorePass)
            throws IOException, KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException, CertificateException, KeyManagementException {
        this(maxPreRoute, maxTotal, keyStoreFile, keyStorePass, null, null);
    }

    public HttpClientManager(int maxPreRoute, int maxTotal, String keyStoreFile, String keyStorePass, String trustStoreFile, String trustStorePass)
            throws IOException, KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException, CertificateException, KeyManagementException {
        SSLContextBuilder builder = SSLContexts.custom();
        if (!StringUtils.isEmpty(keyStoreFile)) {
            URL fileUrl = HttpClientManager.class.getResource(keyStoreFile);
            String path = fileUrl.getPath();
            InputStream ksis = new FileInputStream(new File(path));// 私钥证书
            KeyStore ks = KeyStore.getInstance("PKCS12");
            ks.load(ksis, keyStorePass.toCharArray());
            builder.loadKeyMaterial(ks, keyStorePass.toCharArray());
            ksis.close();
        }
        if (!StringUtils.isEmpty(trustStoreFile)) {
            InputStream tsis = new FileInputStream(new File(trustStoreFile));// 信任证书库
            KeyStore ts = KeyStore.getInstance("JKS");
            ts.load(tsis, trustStorePass.toCharArray());
            builder.loadTrustMaterial(ts, new TrustSelfSignedStrategy());
            tsis.close();
        } else {
            // 如果没有服务器证书，可以采用自定义 信任机制
            // 信任所有
            builder.loadTrustMaterial(null, (arg0, arg1) -> true);
        }

        SSLContext sslContext = builder.build();
        SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext, new String[] {"TLSv1"}, null,
                SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

        RegistryBuilder registryBuilder = RegistryBuilder.create();
        registryBuilder.register("http", PlainConnectionSocketFactory.INSTANCE);
        registryBuilder.register("https", sslConnectionSocketFactory);

        Registry registry = registryBuilder.build();

        PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager(registry);
        poolingHttpClientConnectionManager.setDefaultMaxPerRoute(maxPreRoute);
        poolingHttpClientConnectionManager.setMaxTotal(maxTotal);

        httpClient = HttpClients.custom().setConnectionManager(poolingHttpClientConnectionManager).build();
    }

    @Override
	public XRHttpResponse httpGet(String url) throws IOException {
        HttpResponse httpResponse = null;
        try {
            HttpGet httpGet = new HttpGet(url);
            RequestConfig config = RequestConfig.custom().setConnectTimeout(5000).setSocketTimeout(50000).build();
            httpGet.setConfig(config);
            
            httpResponse = httpClient.execute(httpGet);

            return xrHttpResp(httpResponse);
        } finally {
            closeHttpResponse(httpResponse);
        }
    }

    //带hearder参数设置的请求
    @Override
	public XRHttpResponse httpGet(String url, Map<String, String> Header) throws IOException {
        HttpResponse httpResponse = null;
        try {
            HttpGet httpGet = new HttpGet(url);
            RequestConfig config = RequestConfig.custom().setConnectTimeout(5000).setSocketTimeout(50000).build();
            httpGet.setConfig(config);
            for (Map.Entry<String, String> entry : Header.entrySet()) {
                httpGet.setHeader(entry.getKey(), entry.getValue());
            }
            httpResponse = httpClient.execute(httpGet);
            return xrHttpResp(httpResponse);
        } finally {
            closeHttpResponse(httpResponse);
        }
    }

    private void closeHttpResponse(HttpResponse httpResponse) throws IOException {
        if (httpResponse != null && httpResponse instanceof CloseableHttpResponse) {
            CloseableHttpResponse closeableHttpResponse = (CloseableHttpResponse) httpResponse;
            closeableHttpResponse.close();
        }
    }

	private XRHttpResponse xrHttpResp(HttpResponse httpResponse) throws IOException {
        int status = -1;
        String reasonPhrase = null;
        long contentLength = 0;
        String content = null;

        if (httpResponse != null) {
            StatusLine statusLine = httpResponse.getStatusLine();

            status = statusLine.getStatusCode();
            reasonPhrase = statusLine.getReasonPhrase();
            contentLength = 0;
            content = null;

            HttpEntity httpEntity = httpResponse.getEntity();
            if (httpEntity != null) {
                contentLength = httpEntity.getContentLength();
                content = EntityUtils.toString(httpEntity);
            }
        }

        return new XRHttpResponse(status, reasonPhrase, contentLength, content);
    }

    @Override
	public XRHttpResponse httpPost(String url, Map<String, String> params) throws IOException {
        HttpResponse httpResponse = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            RequestConfig config = RequestConfig.custom().setConnectTimeout(5000).setSocketTimeout(50000).build();
            httpPost.setConfig(config);
            if (params != null && params.size() > 0) {
                List<BasicNameValuePair> list = new ArrayList<>(params.size());
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    list.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
                httpPost.setEntity(new UrlEncodedFormEntity(list));
            }

            httpResponse = httpClient.execute(httpPost);

            return xrHttpResp(httpResponse);
        } finally {
            closeHttpResponse(httpResponse);
        }
    }
    //有序请求
    @Override
	public XRHttpResponse httpPost(String url, List<BasicNameValuePair> params) throws IOException {
        HttpResponse httpResponse = null;
        try{
            HttpPost httpPost = new HttpPost(url);
            RequestConfig config = RequestConfig.custom().setConnectTimeout(5000).setSocketTimeout(50000).build();
            httpPost.setConfig(config);
            httpPost.setEntity(new UrlEncodedFormEntity(params,"UTF-8"));
            httpResponse = httpClient.execute(httpPost);
            return xrHttpResp(httpResponse);
        }finally {
            closeHttpResponse(httpResponse);
        }
    }

    @Override
	public XRHttpResponse httpPost(String url, List<BasicNameValuePair> params, Map<String, String> Header) throws IOException {
        HttpResponse httpResponse = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            RequestConfig config = RequestConfig.custom().setConnectTimeout(5000).setSocketTimeout(50000).build();
            httpPost.setConfig(config);
            
            for (Map.Entry<String, String> entry : Header.entrySet()) {
                httpPost.setHeader(entry.getKey(), entry.getValue());
            }
            httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            httpResponse = httpClient.execute(httpPost);
            return xrHttpResp(httpResponse);
        } finally {
            closeHttpResponse(httpResponse);
        }
    }

    @Override
	public XRHttpResponse httpPost(String url, byte[] params) throws IOException {
        HttpResponse httpResponse = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            RequestConfig config = RequestConfig.custom().setConnectTimeout(5000).setSocketTimeout(50000).build();
            httpPost.setConfig(config);
            
            ByteArrayEntity reqEntity = new ByteArrayEntity(params);
            httpPost.setEntity(reqEntity);
            httpResponse = httpClient.execute(httpPost);
            return xrHttpResp(httpResponse);
        } finally {
            closeHttpResponse(httpResponse);
        }
    }


    @Override
	public XRHttpResponse httpJsonPost(String url, String json) throws IOException {
        HttpResponse httpResponse = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            RequestConfig config = RequestConfig.custom().setConnectTimeout(5000).setSocketTimeout(50000).build();
            httpPost.setConfig(config);
            httpPost.setEntity(new StringEntity(json,ContentType.APPLICATION_JSON));

            httpResponse = httpClient.execute(httpPost);

            return xrHttpResp(httpResponse);
        } finally {
            closeHttpResponse(httpResponse);
        }
    }

    //带hearder参数设置的请求
    @Override
	public XRHttpResponse httpJsonPost(String url, String json, Map<String, String> Header) throws IOException {
        HttpResponse httpResponse = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            RequestConfig config = RequestConfig.custom().setConnectTimeout(5000).setSocketTimeout(50000).build();
            httpPost.setConfig(config);
            for (Map.Entry<String, String> entry : Header.entrySet()) {
                httpPost.setHeader(entry.getKey(), entry.getValue());
            }
            httpPost.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));

            httpResponse = httpClient.execute(httpPost);

            return xrHttpResp(httpResponse);
        } finally {
            closeHttpResponse(httpResponse);
        }
    }

    @Override
	public XRHttpResponse httpMultiPost(String url, Map<String, Object> params) throws IOException {
        HttpResponse httpResponse = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            RequestConfig config = RequestConfig.custom().setConnectTimeout(5000).setSocketTimeout(50000).build();
            httpPost.setConfig(config);
            if (params != null && params.size() > 0) {
                MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
                for (Map.Entry<String, Object> entry : params.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    if (value instanceof byte[]) {
                        multipartEntityBuilder.addPart(key, new ByteArrayBody((byte[]) value, ContentType.DEFAULT_BINARY, key));
                    } else if (value instanceof File) {
                        multipartEntityBuilder.addPart(key, new FileBody((File) value));
                    } else {
                        multipartEntityBuilder.addPart(key, new StringBody((String) value, ContentType.MULTIPART_FORM_DATA));
                    }
                }
                httpPost.setEntity(multipartEntityBuilder.build());
            }

            httpResponse = httpClient.execute(httpPost);

            return xrHttpResp(httpResponse);
        } finally {
            closeHttpResponse(httpResponse);
        }
    }

    @Override
	public XRHttpResponse httpMultiPost(String url, Map<String, Object> params, Charset charset) throws IOException {
        HttpResponse httpResponse = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            RequestConfig config = RequestConfig.custom().setConnectTimeout(5000).setSocketTimeout(50000).build();
            httpPost.setConfig(config);
            
            if (params != null && params.size() > 0) {
                MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
                multipartEntityBuilder.setCharset(charset);
                for (Map.Entry<String, Object> entry : params.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    if (value instanceof byte[]) {
                        multipartEntityBuilder.addPart(key, new ByteArrayBody((byte[]) value, ContentType.DEFAULT_BINARY, key));
                    } else if (value instanceof File) {
                        multipartEntityBuilder.addPart(key, new FileBody((File) value));
                    } else {
                        multipartEntityBuilder.addPart(key, new StringBody((String) value, ContentType.create("multipart/form-data", Consts.UTF_8)));
                    }
                }
                httpPost.setEntity(multipartEntityBuilder.build());
            }

            httpResponse = httpClient.execute(httpPost);

            return xrHttpResp(httpResponse);
        } finally {
            closeHttpResponse(httpResponse);
        }
    }

}
