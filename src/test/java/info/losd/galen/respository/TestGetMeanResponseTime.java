package info.losd.galen.respository;

import info.losd.galen.repository.Period;
import info.losd.galen.repository.dto.HealthcheckMean;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
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
public class TestGetMeanResponseTime extends InfluxdbHealthcheckRepoTest {
    @Test
    public void test_it_can_get_the_mean_response_time_for_a_period() throws Exception {
        List<Object> mean = new LinkedList<>(Arrays.asList("2015-07-12T00:00:00.05138052Z", 227.22135161606295));

        QueryResult queryResult = buildQueryResult(mean);

        ArgumentCaptor<Query> query = ArgumentCaptor.forClass(Query.class);
        when(db.query(query.capture())).thenReturn(queryResult);

        HealthcheckMean result = repo.getMeanForPeriod("healthcheck1", Period.TWO_MINUTES);

        assertThat(query.getValue().getCommand(), is(equalTo("SELECT mean(response_time) FROM statistic WHERE time > now() - 2m AND healthcheck = 'healthcheck1'")));
        assertThat(query.getValue().getDatabase(), is(equalTo("galen-web")));

        assertThat(result.getTimestamp(), is(equalTo(Instant.parse("2015-07-12T00:00:00.05138052Z"))));
        assertThat(result.getMean(), is(equalTo(227.22135161606295)));
    }
}
