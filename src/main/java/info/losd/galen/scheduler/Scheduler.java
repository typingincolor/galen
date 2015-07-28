package info.losd.galen.scheduler;

import com.google.gson.Gson;
import info.losd.galen.api.dto.HealthcheckRequest;
import info.losd.galen.repository.dto.Healthcheck;
import info.losd.galen.scheduler.entity.Task;
import info.losd.galen.scheduler.repository.TaskRepo;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
@Component
public class Scheduler {
    @Autowired
    TaskRepo repo;

    Logger LOG = LoggerFactory.getLogger(Scheduler.class);

    @Scheduled(fixedRate = 5000)
    public void processTasks() {
        List<Task> tasks = repo.findTasksToBeRun();
        LOG.info("There are {} tasks waiting", tasks.size());

        tasks.forEach(task -> {
            LOG.info("processing: {}", task.toString());

            Map<String, String> headers = new HashMap<>();
            task.getHeaders().forEach(header -> {
                headers.put(header.getHeader(), header.getValue());
            });

            HealthcheckRequest healthcheckRequest = new HealthcheckRequest();
            healthcheckRequest.setHeaders(headers);
            healthcheckRequest.setMethod(task.getMethod());
            healthcheckRequest.setTag(new Healthcheck(task.getName()));
            healthcheckRequest.setUrl(task.getUrl());

            String body = new Gson().toJson(healthcheckRequest, HealthcheckRequest.class);

            try {
                HttpResponse response = Request.Post("http://127.0.0.1:8080/healthchecks")
                        .bodyString(body, ContentType.APPLICATION_JSON)
                        .execute()
                        .returnResponse();

                StatusLine status = response.getStatusLine();
                if (status.getStatusCode() == 200) {
                    task.setLastUpdated(Instant.now());
                    repo.save(task);
                    LOG.info("processed:  {}", task.toString());
                }
                else {
                    LOG.error("status code: {}, reason: {}", status.getStatusCode(), status.getReasonPhrase());
                }
            } catch (Exception e) {
                LOG.error("Problem processing task {}", task.toString(), e);
            }
        });
    }
}
