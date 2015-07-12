package info.losd.galen.client;

import info.losd.galen.repository.Statistic;
import info.losd.galen.repository.StatisticsRepo;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;

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
@Service
public class ApiClient {
    Logger logger = LoggerFactory.getLogger(ApiClient.class);

    @Autowired
    StatisticsRepo statisticsRepo;

    public ApiResponse execute(ApiRequest req) {
        try {
            Request request = request(req);

            req.getHeaders().forEach((header, value) -> request.addHeader(header, value));

            long start = System.nanoTime();
            HttpResponse response = request.execute().returnResponse();
            long end = System.nanoTime();

            Statistic stat = Statistic.tag(req.getTag())
                    .duration((end - start) / 1000000)
                    .statusCode(response.getStatusLine().getStatusCode())
                    .build();
            statisticsRepo.save(stat);

            return ApiResponse
                    .statusCode(response.getStatusLine().getStatusCode())
                    .body(getResponseBody(response)).build();
        }
        catch (IOException e) {
            logger.error("IO Exception", e);
            throw new RuntimeException(e);
        }
    }

    private String getResponseBody(HttpResponse response) throws
            IOException
    {
        StringWriter writer = new StringWriter();
        IOUtils.copy(response.getEntity().getContent(), writer);
        return writer.toString();
    }

    private Request request(ApiRequest req) {
        switch (req.getMethod()) {
            case GET:
                return Request.Get(req.getUrl());
            case POST:
                return Request.Post(req.getUrl());
            default:
                throw new RuntimeException("Unimplemented ApiRequest type");
        }
    }
}
