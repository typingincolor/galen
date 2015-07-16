package info.losd.galen.api;

import info.losd.galen.api.dto.HealthcheckResult;
import info.losd.galen.client.ApiMethod;
import info.losd.galen.client.dto.ApiRequest;
import info.losd.galen.client.dto.ApiResponse;
import org.hamcrest.collection.IsMapContaining;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
public class TestPostHealthCheckRequest extends GalenApiControllerTest {
    @Test
    public void it_runs_the_posted_api_request() throws Exception {
        String json = "{\"tag\":\"test_api\",\"url\":\"http://localhost:9090/test\", \"method\": \"GET\",\"headers\": {\"header1\": \"value1\"}}";

        ArgumentCaptor<ApiRequest> argumentCaptor = ArgumentCaptor.forClass(ApiRequest.class);
        ApiResponse response = ApiResponse.statusCode(200).body(null).build();

        when(client.execute(argumentCaptor.capture())).thenReturn(response);

        MvcResult mvcResult = mockMvc.perform(post("/healthchecks").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        HealthcheckResult result = gson.fromJson(mvcResult.getResponse().getContentAsString(), HealthcheckResult.class);
        assertThat(result.getStatusCode(), is(200));

        ApiRequest captured = argumentCaptor.getValue();
        assertThat(captured.getTag(), is(equalTo("test_api")));
        assertThat(captured.getMethod(), is(equalTo(ApiMethod.GET)));
        assertThat(captured.getUrl(), is(equalTo("http://localhost:9090/test")));
        assertThat(captured.getHeaders(), IsMapContaining.hasEntry("header1", "value1"));
    }

    @Test
    public void it_handles_an_unknown_method() throws Exception {
        String json = "{\"tag\":\"test_api\",\"url\":\"http://localhost:9090/test\", \"method\": \"JUNK\",\"headers\": {\"header1\": \"value1\"}}";

        MvcResult mvcResult = mockMvc.perform(post("/healthchecks").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        assertThat(mvcResult.getResponse().getErrorMessage(),
                is(equalTo("The method specified is invalid")));
    }
}
