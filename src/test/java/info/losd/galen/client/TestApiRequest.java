package info.losd.galen.client;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Rule;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsEqual.equalTo;

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
public class TestApiRequest {
    @Rule
    public WireMockRule wireMockRule = new WireMockRule();

    @Test
    public void test_it_can_make_a_get_request() {
        stubFor(get(urlEqualTo("/test"))
                .willReturn(aResponse().withBody("Test")));

        ApiRequest request = new ApiRequest.Builder().method(ApiMethod.GET).url("http://localhost:8080/test").build();

        ApiResponse result = request.execute();

        assertThat("status code", result.getStatusCode(), is(200));
        assertThat("body", result.getBody(), is(equalTo("Test")));
    }

    @Test
    public void test_it_can_make_a_post_request() {
        stubFor(post(urlEqualTo("/test"))
                .willReturn(aResponse().withBody("Test")));

        ApiRequest request = new ApiRequest.Builder().method(ApiMethod.POST).url("http://localhost:8080/test").build();

        ApiResponse result = request.execute();

        assertThat("status code", result.getStatusCode(), is(200));
        assertThat("body", result.getBody(), is(equalTo("Test")));
    }

    @Test
    public void test_it_works_with_a_header() {
        stubFor(get(urlEqualTo("/test"))
                .withHeader("X_TEST.header", WireMock.equalTo("test-value"))
                .willReturn(aResponse().withBody("Test")));

        ApiRequest request = new ApiRequest.Builder().method(ApiMethod.GET)
                .url("http://localhost:8080/test")
                .header("X_TEST.header", "test-value")
                .build();

        ApiResponse result = request.execute();

        assertThat("status code", result.getStatusCode(), is(200));
        assertThat("body", result.getBody(), is(equalTo("Test")));
    }

    @Test
    public void test_it_works_with_query_parameters() {
        stubFor(get(urlPathEqualTo("/test"))
                .withQueryParam("param1", WireMock.equalTo("test"))
                .willReturn(aResponse().withBody("Test")));

        ApiRequest request = new ApiRequest.Builder().method(ApiMethod.GET).url("http://localhost:8080/test?param1=test").build();

        ApiResponse result = request.execute();

        assertThat("status code", result.getStatusCode(), is(200));
        assertThat("body", result.getBody(), is(equalTo("Test")));
    }
}
