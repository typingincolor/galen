package info.losd.galen.respository;

import info.losd.galen.repository.dto.Healthcheck;
import org.hamcrest.collection.IsCollectionWithSize;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
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
public class TestHealthcheckList extends InfluxdbHealthcheckRepoTest {
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
        assertThat(query.getValue().getDatabase(), is(equalTo("galen-web")));
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
}
