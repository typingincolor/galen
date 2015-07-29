package info.losd.galen.scheduler;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import info.losd.galen.scheduler.entity.Header;
import info.losd.galen.scheduler.entity.Task;
import info.losd.galen.scheduler.repository.TaskRepo;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;
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
public class TestScheduler {
    @ClassRule
    @SuppressFBWarnings(value = {"URF_UNREAD_FIELD", "URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"}, justification = "Wiremock uses it")
    public static WireMockRule wireMockRule = new WireMockRule();

    @Mock
    TaskRepo repo;

    @InjectMocks
    Scheduler scheduler;

    Random random = new Random();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void test_it_processes_outstanding_tasks() {
        stubFor(post(urlEqualTo("/healthchecks"))
                .withRequestBody(matchingJsonPath("$.tag"))
                .withRequestBody(matchingJsonPath("$.url"))
                .withRequestBody(matchingJsonPath("$.method"))
                .withRequestBody(matchingJsonPath("$.headers"))
                .willReturn(aResponse().withBody("OK")));

        List<Task> tasks = Arrays.asList(createTask(), createTask(), createTask());

        when(repo.findTasksToBeRun()).thenReturn(tasks);

        scheduler.processTasks();
        verify(repo, times(1)).findTasksToBeRun();
        verify(repo, times(3)).save(any(Task.class));
    }

    private Task createTask() {
        List<Header> headers = Arrays.asList(new Header(r(10), r(10)), new Header(r(10), r(10)));
        return new Task(r(10), 10, Instant.now(), "http://" + r(10) + ".com", "GET", headers);
    }

    private String r(int length){
        return random.ints(48,122)
                .filter(i-> (i<57 || i>65) && (i <90 || i>97))
                .mapToObj(i -> (char) i)
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }
}
