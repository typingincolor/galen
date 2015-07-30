package info.losd.galen.scheduler.repository;

import info.losd.galen.Galen;
import info.losd.galen.scheduler.entity.Header;
import info.losd.galen.scheduler.entity.Task;
import org.hamcrest.collection.IsCollectionWithSize;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpMethod;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;

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
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
                         TransactionalTestExecutionListener.class})
@SpringApplicationConfiguration(classes = Galen.class)
@TestPropertySource("/test.properties")
@DirtiesContext
@Category(IntegrationTest.class)
public class TestTaskRepo {
    @Autowired
    TaskRepo repo;

    Logger LOG = LoggerFactory.getLogger(TestTaskRepo.class);

    @Before
    public void setup() {
        repo.deleteAll();
    }

    @Test
    public void test_it_can_get_a_list_of_tasks_to_be_run() {
        List<Header> headers = Arrays.asList(new Header("h1", "v1"), new Header("h2", "v2"));
        String url = "http://localhost:8080/test";
        String method = HttpMethod.GET.toString();

        repo.save(new Task("test_task", 10, Instant.now().minusSeconds(10), url, method, Collections.emptyList()));
        repo.save(new Task("test_task", 10, Instant.now().minusSeconds(10), url, method, Collections.emptyList()));
        repo.save(new Task("test_task", 10, Instant.now().minusSeconds(10), url, method, headers));
        repo.save(new Task("test_task", 10, Instant.now().plusSeconds(11), url, method, Collections.emptyList()));
        repo.save(new Task("test_task", 10, Instant.now().plusSeconds(12), url, method, Collections.emptyList()));

        List<Task> result = repo.findTasksToBeRun();

        assertThat(result, IsCollectionWithSize.hasSize(3));

        result.forEach(res -> {
            LOG.info("{} has {} headers", res.toString(), res.getHeaders().size());
        });
    }
}
