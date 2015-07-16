package info.losd.galen.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import info.losd.galen.repository.Period;
import info.losd.galen.repository.dto.HealthcheckStatistic;
import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsCollectionWithSize;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.Instant;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

public class TestGetStatisticsForHealthcheck extends GalenApiControllerTest{
    @Test
    public void it_gets_a_list_of_statistics() throws Exception {
        List<HealthcheckStatistic> list = new LinkedList<HealthcheckStatistic>(Arrays.asList(
                new HealthcheckStatistic("2015-07-13T07:51:25.165Z", 101L, 200L),
                new HealthcheckStatistic("2015-07-13T07:51:25.166Z", 102L, 200L),
                new HealthcheckStatistic("2015-07-13T07:51:25.167Z", 103L, 200L)
        ));
        Mockito.when(repo.getStatisticsForPeriod(Matchers.anyString(), Matchers.any(Period.class))).thenReturn(list);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/healthchecks/healthcheck1/statistics?period=2m"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ObjectMapper mapper = new ObjectMapper();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        List<info.losd.galen.api.dto.HealthcheckStatistic> result =
                mapper.readValue(contentAsString, new TypeReference<List<info.losd.galen.api.dto.HealthcheckStatistic>>() {
                });

        MatcherAssert.assertThat(result, IsCollectionWithSize.hasSize(3));

        checkHealthCheckStatistic(result.get(0), "2015-07-13T07:51:25.165Z", 101L, 200L);
        checkHealthCheckStatistic(result.get(1), "2015-07-13T07:51:25.166Z", 102L, 200L);
        checkHealthCheckStatistic(result.get(2), "2015-07-13T07:51:25.167Z", 103L, 200L);
    }

    private void checkHealthCheckStatistic(info.losd.galen.api.dto.HealthcheckStatistic healthcheckStatistic, String timestamp, long responseTime, long statusCode) {
        assertThat(healthcheckStatistic.getTimestamp(), is(equalTo(Instant.parse(timestamp))));
        assertThat(healthcheckStatistic.getResponseTime(), is(equalTo(responseTime)));
        assertThat(healthcheckStatistic.getStatusCode(), is(equalTo(statusCode)));
    }
}