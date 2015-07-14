package info.losd.galen.repository;

import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
@Repository
public class InfluxdbStatisticsRepo implements StatisticsRepo {
    @Autowired
    InfluxDB influxDB;

    @Override
    public void save(Statistic s) {
        Point point = Point.measurement("statistic")
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .tag("api", s.getTag())
                .field("response_time", s.getDuration())
                .field("status_code", s.getStatusCode())
                .build();

        influxDB.write("galen", "default", point);
    }

    @Override
    public List<Api> getApis() {
        Query query = new Query("SHOW TAG VALUES FROM statistic WITH KEY = api", "galen");
        QueryResult apiList = influxDB.query(query);

        List<Api> apis = new LinkedList<>();

        apiList.getResults().get(0).getSeries().get(0).getValues().forEach(value -> apis.add(new Api((String) value.get(0))));

        return apis;
    }
}
