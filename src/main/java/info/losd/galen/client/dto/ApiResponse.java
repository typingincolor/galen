package info.losd.galen.client.dto;

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
public class ApiResponse {
    private int statusCode;
    private String body;

    public String getBody() {
        return body;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public static Body statusCode(int statusCode) {
        return new ApiResponse.Builder(statusCode);
    }

    private ApiResponse(int statusCode, String body) {
        this.statusCode = statusCode;
        this.body = body;
    }

    public static class Builder implements Body, Build {
        int statusCode;
        String body;

        private Builder(int statusCode) {
            this.statusCode = statusCode;
        }

        public Build body(String body) {
            this.body = body;
            return this;
        }

        public ApiResponse build() {
            return new ApiResponse(statusCode, body);
        }
    }

    public interface Body {
        Build body(String body);
    }

    public interface Build {
        ApiResponse build();
    }
}
