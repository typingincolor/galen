package info.losd.galen.api;

import com.google.gson.reflect.TypeToken;
import info.losd.galen.api.dto.HealthcheckApiDTO;
import info.losd.galen.repository.dto.Healthcheck;
import org.hamcrest.collection.IsCollectionWithSize;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
public class TestGetHealthcheckList extends GalenApiControllerTest {
    @Test
    public void it_gets_a_list_of_healthchecks() throws Exception {
        List<Healthcheck> healthcheckList = new LinkedList<>();
        healthcheckList.add(new info.losd.galen.repository.dto.Healthcheck("healthcheck1"));
        healthcheckList.add(new info.losd.galen.repository.dto.Healthcheck("healthcheck2"));
        healthcheckList.add(new info.losd.galen.repository.dto.Healthcheck("healthcheck3"));

        when(repo.getHealthchecks()).thenReturn(healthcheckList);

        MvcResult mvcResult = mockMvc.perform(get("/healthchecks"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        Type type = new TypeToken<List<HealthcheckApiDTO>>() {
        }.getType();

        List<HealthcheckApiDTO> result = gson.fromJson(mvcResult.getResponse().getContentAsString(), type);

        assertThat(result, is(IsCollectionWithSize.hasSize(3)));
        assertThat(result.get(0).getName(), is(equalTo("healthcheck1")));

        checkHealthcheck(result.get(0), "healthcheck1");
        checkHealthcheck(result.get(1), "healthcheck2");
        checkHealthcheck(result.get(2), "healthcheck3");
    }
}
