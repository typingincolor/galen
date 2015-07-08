package info.losd.galen.client;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
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
    Logger logger = LoggerFactory.getLogger(ApiRequest.class);

    private String url;
    private ApiMethod method;
    private Map<String, String> headers;

    private ApiRequest(ApiMethod method, String url, Map<String, String> headers) {
        this.method = method;
        this.url = url;
        this.headers = headers;
    }

    public ApiResponse execute() {
        try {

            Request request = request();

            headers.forEach((header, value) -> request.addHeader(header, value));

            HttpResponse response = request.execute().returnResponse();

            return new ApiResponse.Builder()
                    .statusCode(response.getStatusLine().getStatusCode())
                    .body(getResponseBody(response)).build();
        } catch (IOException e) {
            logger.error("IO Exception", e);
            throw new RuntimeException(e);
        }
    }

    private String getResponseBody(HttpResponse response) throws IOException {
        StringWriter writer = new StringWriter();
        IOUtils.copy(response.getEntity().getContent(), writer);
        return writer.toString();
    }

    private Request request() {
        switch (method) {
            case GET:
                return Request.Get(url);
            case POST:
                return Request.Post(url);
            default:
                throw new RuntimeException("Unimplemented ApiRequest type");
        }
    }

    public static class Builder {
        private String url;
        private ApiMethod method;
        private Map<String, String> headers = new HashMap<>();

        Builder url(String url) {
            this.url = url;
            return this;
        }

        ApiRequest build() {
            return new ApiRequest(method, url, headers);
        }

        public Builder method(ApiMethod method) {
            this.method = method;
            return this;
        }

        public Builder header(String header, String value) {
            headers.put(header, value);
            return this;
        }
    }
}
