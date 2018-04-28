package com.nightchat.utils.http;

public class XRHttpResponse {
    private int status;
    private String reasonPhrase;
    private long contentLength;
    private String content;

    public XRHttpResponse() {}

    public XRHttpResponse(int status, String reasonPhrase, long contentLength, String content) {
        this.status = status;
        this.reasonPhrase = reasonPhrase;
        this.contentLength = contentLength;
        this.content = content;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }

    public void setReasonPhrase(String reasonPhrase) {
        this.reasonPhrase = reasonPhrase;
    }

    public long getContentLength() {
        return contentLength;
    }

    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
