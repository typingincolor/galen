package info.losd.galen.respository;

import info.losd.galen.repository.InfluxdbStatisticsRepo;
import info.losd.galen.repository.Statistic;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
public class TestInfluxdbStatisticsRepo {
    @Mock
    private InfluxDB db;

    @InjectMocks
    InfluxdbStatisticsRepo repo;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void test_it_writes_to_influxdb() {
        Statistic stat = Statistic.tag("api_name").duration(100L).statusCode(200).build();

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
}
