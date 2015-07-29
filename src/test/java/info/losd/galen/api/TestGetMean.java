package info.losd.galen.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import info.losd.galen.api.dto.HealthcheckApiMean;
import info.losd.galen.repository.Period;
import info.losd.galen.repository.dto.HealthcheckMean;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

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
public class TestGetMean extends GalenApiControllerTest {
    @Test
    public void test_it_gets_the_mean() throws Exception {
        HealthcheckMean mean = new HealthcheckMean("2015-07-13T07:51:25.165Z", 200);

        when(repo.getMeanForPeriod(eq("healthcheck1"), eq(Period.TWO_MINUTES))).thenReturn(mean);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/healthchecks/healthcheck1/statistics/mean?period=2m"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ObjectMapper mapper = new ObjectMapper();
        String contentAsString = mvcResult.getResponse().getContentAsString();

        HealthcheckApiMean result = mapper.readValue(contentAsString, HealthcheckApiMean.class);

        assertThat(result.getTimestamp(), is(equalTo(mean.getTimestamp())));
        assertThat(result.getMean(), is(equalTo(mean.getMean())));
    }
}
