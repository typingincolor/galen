package info.losd.galen.respository;

import info.losd.galen.repository.Period;
import info.losd.galen.repository.dto.HealthcheckStatistic;
import org.hamcrest.collection.IsCollectionWithSize;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
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
public class TestGetStatisticsForPeriod extends InfluxdbHealthcheckRepoTest {
    @Test
    public void test_it_can_get_statistics_for_a_period() throws Exception {
        List<Object> statistic1 = new LinkedList<>(Arrays.asList("2015-07-13T07:51:25.165Z", 937.0, 200.0));
        List<Object> statistic2 = new LinkedList<>(Arrays.asList("2015-07-13T07:51:32.358Z", 192.0, 500.0));
        List<Object> statistic3 = new LinkedList<>(Arrays.asList("2015-07-13T07:51:33.426Z", 185.0, 200.0));

        QueryResult queryResult = buildQueryResult(statistic1, statistic2, statistic3);

        ArgumentCaptor<Query> query = ArgumentCaptor.forClass(Query.class);
        when(db.query(query.capture())).thenReturn(queryResult);

        List<HealthcheckStatistic> result = repo.getStatisticsForPeriod("healthcheck1", Period.TWO_MINUTES);

        assertThat(query.getValue().getCommand(), is(equalTo("SELECT response_time, status_code FROM statistic WHERE time > now() - 2m AND healthcheck = 'healthcheck1'")));
        assertThat(query.getValue().getDatabase(), is(equalTo("galen-web")));
        assertThat(result, IsCollectionWithSize.hasSize(3));

        assertThat(result.get(0), is(equalTo(new HealthcheckStatistic("2015-07-13T07:51:25.165Z", 937, 200))));
        assertThat(result.get(1), is(equalTo(new HealthcheckStatistic("2015-07-13T07:51:32.358Z", 192, 500))));
        assertThat(result.get(2), is(equalTo(new HealthcheckStatistic("2015-07-13T07:51:33.426Z", 185, 200))));
    }
}
