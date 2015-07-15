package info.losd.galen.respository;

import info.losd.galen.repository.InfluxdbHealthcheckRepo;
import info.losd.galen.repository.Period;
import info.losd.galen.repository.dto.Healthcheck;
import info.losd.galen.repository.dto.HealthcheckDetails;
import info.losd.galen.repository.dto.HealthcheckMean;
import info.losd.galen.repository.dto.HealthcheckStatistic;
import org.hamcrest.collection.IsCollectionWithSize;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.Mockito.*;

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
public class TestInfluxdbHealthcheckRepo {
    @Mock
    private InfluxDB db;

    @InjectMocks
    InfluxdbHealthcheckRepo repo;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void test_it_writes_to_influxdb() {
        HealthcheckDetails stat = HealthcheckDetails.tag("healthcheck1").duration(100L).statusCode(200).build();

        repo.save(stat);

        ArgumentCaptor<Point> point = ArgumentCaptor.forClass(Point.class);
        ArgumentCaptor<String> dbname = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> retentionPolicy = ArgumentCaptor.forClass(String.class);

        verify(db, times(1)).write(dbname.capture(), retentionPolicy.capture(), point.capture());
        assertThat("point", point.getValue().toString(),
                allOf(containsString("fields={response_time=100, status_code=200}"),
                        containsString("name=statistic"),
                        containsString("precision=MILLISECONDS"),
                        containsString("tags={healthcheck=healthcheck1}")
                ));

        assertThat("dbname", dbname.getValue(), is(equalTo("galen")));
        assertThat("retentionPolicy", retentionPolicy.getValue(), is(equalTo("default")));
    }

    @Test
    public void test_it_can_get_a_list_of_healthchecks() {
        List<Object> api1 = new LinkedList<>(Collections.singletonList("api1"));
        List<Object> api2 = new LinkedList<>(Collections.singletonList("api2"));
        List<Object> api3 = new LinkedList<>(Collections.singletonList("api3"));

        QueryResult queryResult = buildQueryResult(api1, api2, api3);

        ArgumentCaptor<Query> query = ArgumentCaptor.forClass(Query.class);
        when(db.query(query.capture())).thenReturn(queryResult);

        List<Healthcheck> result = repo.getHealthchecks();

        assertThat(query.getValue().getCommand(), is(equalTo("SHOW TAG VALUES FROM statistic WITH KEY = healthcheck")));
        assertThat(query.getValue().getDatabase(), is(equalTo("galen")));
        assertThat(result, IsCollectionWithSize.hasSize(3));
        assertThat(result.get(0).getName(), is(equalTo("api1")));
        assertThat(result.get(1).getName(), is(equalTo("api2")));
        assertThat(result.get(2).getName(), is(equalTo("api3")));
    }

    @Test
    public void test_it_handles_an_empty_list_of_healthchecks() {
        QueryResult queryResult = buildQueryResult(Collections.emptyList());

        ArgumentCaptor<Query> query = ArgumentCaptor.forClass(Query.class);
        when(db.query(query.capture())).thenReturn(queryResult);

        List<Healthcheck> result = repo.getHealthchecks(); buildQueryResult(Collections.emptyList());
        assertThat(result, IsCollectionWithSize.hasSize(0));
    }

    @Test
    public void test_it_can_get_statistics_for_a_period() throws Exception {
        List<Object> statistic1 = new LinkedList<>(Arrays.asList("2015-07-13T07:51:25.165Z", 937, 200));
        List<Object> statistic2 = new LinkedList<>(Arrays.asList("2015-07-13T07:51:32.358Z", 192, 500));
        List<Object> statistic3 = new LinkedList<>(Arrays.asList("2015-07-13T07:51:33.426Z", 185, 200));

        QueryResult queryResult = buildQueryResult(statistic1, statistic2, statistic3);

        ArgumentCaptor<Query> query = ArgumentCaptor.forClass(Query.class);
        when(db.query(query.capture())).thenReturn(queryResult);

        List<HealthcheckStatistic> result = repo.getStatisticsForPeriod("healthcheck1", Period.TWO_MINUTES);

        assertThat(query.getValue().getCommand(), is(equalTo("SELECT time, response_time, status_code FROM statistic WHERE time > now() - 2m AND healthcheck = 'healthcheck1'")));
        assertThat(query.getValue().getDatabase(), is(equalTo("galen")));
        assertThat(result, IsCollectionWithSize.hasSize(3));

        assertThat(result.get(0), is(equalTo(new HealthcheckStatistic("2015-07-13T07:51:25.165Z", 937, 200))));
        assertThat(result.get(1), is(equalTo(new HealthcheckStatistic("2015-07-13T07:51:32.358Z", 192, 500))));
        assertThat(result.get(2), is(equalTo(new HealthcheckStatistic("2015-07-13T07:51:33.426Z", 185, 200))));
    }

    @Test
    public void test_it_can_get_the_mean_response_time_for_a_period() throws Exception {
        List<Object> mean = new LinkedList<>(Arrays.asList("2015-07-12T00:00:00.05138052Z", 227.22135161606295));

        QueryResult queryResult = buildQueryResult(mean);

        ArgumentCaptor<Query> query = ArgumentCaptor.forClass(Query.class);
        when(db.query(query.capture())).thenReturn(queryResult);

        HealthcheckMean result = repo.getMeanForPeriod("healthcheck1", Period.TWO_MINUTES);

        assertThat(query.getValue().getCommand(), is(equalTo("SELECT mean(response_time) FROM statistic WHERE time > now() - 2m AND healthcheck = 'healthcheck1'")));
        assertThat(query.getValue().getDatabase(), is(equalTo("galen")));

        assertThat(result.getTimestamp(), is(equalTo(Instant.parse("2015-07-12T00:00:00.05138052Z"))));
        assertThat(result.getMean(), is(equalTo(227.22135161606295)));
    }

    @SafeVarargs
    private final QueryResult buildQueryResult(List<Object>... values) {
        QueryResult queryResult = new QueryResult();
        QueryResult.Result res = new QueryResult.Result();
        LinkedList<QueryResult.Result> resultList = new LinkedList<>();
        QueryResult.Series series = new QueryResult.Series();
        List<QueryResult.Series> seriesList = new LinkedList<>();

        List<List<Object>> valueList = new LinkedList<>();

        Collections.addAll(valueList, values);

        series.setValues(valueList);
        seriesList.add(series);
        res.setSeries(seriesList);
        resultList.add(res);
        queryResult.setResults(resultList);

        return queryResult;
    }
}
