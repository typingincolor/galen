package info.losd.galen.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import info.losd.galen.api.dto.HealthcheckApiDTO;
import info.losd.galen.client.ApiClient;
import info.losd.galen.repository.HealthcheckRepo;
import org.junit.Before;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
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
abstract public class GalenApiControllerTest {
    MockMvc mockMvc;

    @Mock
    ApiClient client;

    @Mock
    HealthcheckRepo repo;

    @InjectMocks
    GalenApiController apiController;

    Gson gson = new GsonBuilder().serializeNulls().create();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("blah");
        mockMvc = MockMvcBuilders.standaloneSetup(apiController).setViewResolvers(viewResolver).build();
    }

    void checkHealthcheck(HealthcheckApiDTO healthcheck, String text) {
        assertThat(healthcheck.getLink("self").toString(), is(equalTo("<http://localhost/healthchecks/" + text + ">;rel=\"self\"")));
        assertThat(healthcheck.getLink("statistics").toString(), is(equalTo("<http://localhost/healthchecks/" + text + "/statistics?period=2m>;rel=\"statistics\"")));
        assertThat(healthcheck.getLink("mean").toString(), is(equalTo("<http://localhost/healthchecks/" + text + "/statistics/mean?period=2m>;rel=\"mean\"")));
    }
}
