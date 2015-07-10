package info.losd.galen.client;

import java.util.HashMap;
import java.util.Map;

/**
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2015 Andrew Braithwaite
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
public class ApiRequest {
    private String url;
    private ApiMethod method;
    private Map<String, String> headers;

    private ApiRequest(ApiMethod method, String url, Map<String, String> headers) {
        this.method = method;
        this.url = url;
        this.headers = headers;
    }

    public static Method url(String url) {
        return new ApiRequest.Builder(url);
    }

    public String getUrl() {
        return url;
    }

    public ApiMethod getMethod() {
        return method;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public static class Builder implements Method, Header, Build {
        private String url;
        private ApiMethod method;
        private Map<String, String> headers = new HashMap<>();

        private Builder(String url) {
            this.url = url;
        }

        public ApiRequest build() {
            return new ApiRequest(method, url, headers);
        }

        public Header method(ApiMethod method) {
            this.method = method;
            return this;
        }

        public Header header(String header, String value) {
            headers.put(header, value);
            return this;
        }
    }

    public interface Method {
        Header method(ApiMethod method);
    }

    public interface Header {
        Header header(String header, String value);
        ApiRequest build();
    }

    public interface Build {
        ApiRequest build();
    }
}
