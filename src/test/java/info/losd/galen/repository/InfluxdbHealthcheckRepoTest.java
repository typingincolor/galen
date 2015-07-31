package info.losd.galen.repository;

import info.losd.galen.configuration.InfluxDbName;
import org.influxdb.InfluxDB;
import org.influxdb.dto.QueryResult;
import org.junit.Before;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

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
abstract public class InfluxdbHealthcheckRepoTest {
    @Mock
    InfluxDB db;

    @Mock
    InfluxDbName dbname;

    @InjectMocks
    InfluxdbHealthcheckRepo repo;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(dbname.getName()).thenReturn("galen-web");
    }

    @SafeVarargs
    final QueryResult buildQueryResult(List<Object>... values) {
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
