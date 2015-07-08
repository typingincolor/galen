package info.losd.galen.api;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
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
public class TestApi {
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().port(9090));

    private MockMvc mockMvc;

    @InjectMocks
    private GalenApiController apiController;

    private Gson gson = new GsonBuilder().serializeNulls().create();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("blah");
        mockMvc = MockMvcBuilders.standaloneSetup(apiController).setViewResolvers(viewResolver).build();
    }

    @Test
    public void it_runs_the_posted_api_request() throws Exception {
        String json = "{\"url\":\"http://localhost:9090/test\", \"method\": \"GET\",\"headers\": {\"header1\": \"value1\"}}";

        stubFor(WireMock.get(urlPathEqualTo("/test"))
                .withHeader("header1", WireMock.equalTo("value1"))
                .willReturn(aResponse().withBody("Test")));

        MvcResult mvcResult = mockMvc.perform(post("/run").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        GalenResult result = gson.fromJson(mvcResult.getResponse().getContentAsString(), GalenResult.class);
        assertThat(result.getStatusCode(), is(200));
    }

    @Test
    public void it_handles_an_unknown_method() throws Exception {
        String json = "{\"url\":\"http://localhost:9090/test\", \"method\": \"JUNK\",\"headers\": {\"header1\": \"value1\"}}";

        MvcResult mvcResult = mockMvc.perform(post("/run").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

       assertThat(mvcResult.getResponse().getContentAsString(), is(Matchers.equalTo("The method specified is invalid")));
    }
}
