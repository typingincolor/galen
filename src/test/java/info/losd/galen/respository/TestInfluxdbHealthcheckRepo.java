package info.losd.galen.respository;

import info.losd.galen.repository.InfluxdbHealthcheckRepo;
import info.losd.galen.repository.dto.Healthcheck;
import info.losd.galen.repository.dto.HealthcheckDetails;
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
        HealthcheckDetails stat = HealthcheckDetails.tag("api_name").duration(100L).statusCode(200).build();

        repo.save(stat);

        ArgumentCaptor<Point> point = ArgumentCaptor.forClass(Point.class);
        ArgumentCaptor<String> dbname = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> retentionPolicy = ArgumentCaptor.forClass(String.class);

        verify(db, times(1)).write(dbname.capture(), retentionPolicy.capture(), point.capture());
        assertThat("point", point.getValue().toString(),
                allOf(containsString("fields={response_time=100, status_code=200}"),
                        containsString("name=statistic"),
                        containsString("precision=MILLISECONDS"),
                        containsString("tags={api=api_name}")
                ));

        assertThat("dbname", dbname.getValue(), is(equalTo("galen")));
        assertThat("retentionPolicy", retentionPolicy.getValue(), is(equalTo("default")));
    }

    @Test
    public void test_it_can_get_a_list_of_apis() {
        QueryResult queryResult = new QueryResult();
        QueryResult.Result res = new QueryResult.Result();
        LinkedList<QueryResult.Result> resultList = new LinkedList<>();
        QueryResult.Series series = new QueryResult.Series();
        List<QueryResult.Series> seriesList = new LinkedList<>();
        List<Object> values1 = new LinkedList<>();
        List<Object> values2 = new LinkedList<>();
        List<Object> values3 = new LinkedList<>();
        values1.add("api1");
        values2.add("api2");
        values3.add("api3");

        List<List<Object>> valueList = new LinkedList<>();
        valueList.add(values1);
        valueList.add(values2);
        valueList.add(values3);
        series.setValues(valueList);
        seriesList.add(series);
        res.setSeries(seriesList);
        resultList.add(res);

        queryResult.setResults(resultList);

        ArgumentCaptor<Query> query = ArgumentCaptor.forClass(Query.class);
        when(db.query(query.capture())).thenReturn(queryResult);

        List<Healthcheck> result = repo.getApis();

        assertThat(query.getValue().getCommand(), is(equalTo("SHOW TAG VALUES FROM statistic WITH KEY = api")));
        assertThat(query.getValue().getDatabase(), is(equalTo("galen")));
        assertThat(result.size(), is(3));
        assertThat(result.get(0).getName(), is(equalTo("api1")));
        assertThat(result.get(1).getName(), is(equalTo("api2")));
        assertThat(result.get(2).getName(), is(equalTo("api3")));
    }
}
