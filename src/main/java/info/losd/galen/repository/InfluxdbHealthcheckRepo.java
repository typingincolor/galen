package info.losd.galen.repository;

import info.losd.galen.repository.dto.Healthcheck;
import info.losd.galen.repository.dto.HealthcheckDetails;
import info.losd.galen.repository.dto.HealthcheckMean;
import info.losd.galen.repository.dto.HealthcheckStatistic;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.Collections;
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
public class InfluxdbHealthcheckRepo implements HealthcheckRepo {
    @Autowired
    InfluxDB influxDB;

    @Value("${galen.influxdb.dbname}")
    private String dbname = "galen";

    private Logger logger = LoggerFactory.getLogger(InfluxdbHealthcheckRepo.class);

    @Override
    public void save(HealthcheckDetails s) {
        Point point = Point.measurement("statistic")
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .tag("healthcheck", s.getHealthcheck().getName())
                .field("response_time", s.getDuration())
                .field("status_code", s.getStatusCode())
                .build();

        influxDB.write(dbname, "default", point);
    }

    @Override
    public List<Healthcheck> getHealthchecks() {
        Query query = new Query("SHOW TAG VALUES FROM statistic WITH KEY = healthcheck", dbname);
        QueryResult apiList = influxDB.query(query);

        List<Healthcheck> healthchecks = new LinkedList<>();

        try {
            apiList.getResults().get(0).getSeries().get(0).getValues().forEach(value -> healthchecks.add(new Healthcheck((String) value.get(0))));
        } catch (Exception e) {
            logger.info("Returning empty list of health checks: {}", e.getMessage());
            return Collections.<Healthcheck>emptyList();
        }

        return healthchecks;
    }

    @Override
    public List<HealthcheckStatistic> getStatisticsForPeriod(String healthcheck, Period period) {
        String queryString = String.format(
                "SELECT time, response_time, status_code FROM statistic WHERE time > now() - %s AND healthcheck = '%s'"
                , period.toString()
                , healthcheck);

        Query query = new Query(queryString, dbname);
        QueryResult healthcheckLost = influxDB.query(query);

        List<HealthcheckStatistic> statistics = new LinkedList<>();

        try {
            healthcheckLost.getResults().get(0).getSeries().get(0).getValues().forEach(value ->
                statistics.add(new HealthcheckStatistic((String) value.get(0), (int) value.get(1), (int) value.get(2)))
            );
        } catch (Exception e) {
            logger.info("Returning empty list of statistics: {}", e.getMessage());
            return Collections.<HealthcheckStatistic>emptyList();
        }
        return statistics;
    }

    @Override
    public HealthcheckMean getMeanForPeriod(String healthcheck, Period period) throws MeanNotCalculatedException {
        String queryString = String.format(
                "SELECT mean(response_time) FROM statistic WHERE time > now() - %s AND healthcheck = '%s'"
                , period.toString()
                , healthcheck);

        Query query = new Query(queryString, dbname);
        QueryResult healthcheckLost = influxDB.query(query);

        try {
            List<Object> value = healthcheckLost.getResults().get(0).getSeries().get(0).getValues().get(0);
            return new HealthcheckMean((String) value.get(0), (double) value.get(1));
        }
        catch (Exception e) {
            logger.info("Unable to calculate mean: {}", e.getMessage());
            throw new MeanNotCalculatedException(e);
        }
    }
}
