package info.losd.galen.api;

import info.losd.galen.api.dto.*;
import info.losd.galen.client.ApiClient;
import info.losd.galen.client.ApiMethod;
import info.losd.galen.client.dto.ApiRequest;
import info.losd.galen.client.dto.ApiResponse;
import info.losd.galen.repository.HealthcheckRepo;
import info.losd.galen.repository.Period;
import info.losd.galen.repository.dto.HealthcheckMean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

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
@RestController
public class GalenApiController {
    @Autowired
    ApiClient client;

    @Autowired
    HealthcheckRepo repo;

    @RequestMapping(value = "/healthchecks", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public HttpEntity<HealthcheckApiResult> run(@RequestBody HealthcheckApiRequest galen) {
        ApiRequest.Header apiRequestBuilder = ApiRequest.tag(galen.getTag().getName()).url(galen.getUrl()).method(ApiMethod.valueOf(galen.getMethod()));
        galen.getHeaders().forEach((header, value) -> apiRequestBuilder.header(header, value));
        ApiResponse response = client.execute(apiRequestBuilder.build());

        return new ResponseEntity<>(HealthcheckApiResult.statusCode(response.getStatusCode()).build(), HttpStatus.OK);
    }

    @RequestMapping(value = "/healthchecks", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public HttpEntity<List<HealthcheckApiDTO>> getHealthcheckList() {
        List<HealthcheckApiDTO> result = new LinkedList<>();

        repo.getHealthchecks().forEach(healthcheck -> {
            result.add(new HealthcheckApiDTO(healthcheck.getName()));
        });

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/healthchecks/{name}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public HttpEntity<HealthcheckApiDTO> getHealtcheck(@PathVariable String name) {
        return new ResponseEntity<>(new HealthcheckApiDTO(name), HttpStatus.OK);
    }

    @RequestMapping(value = "/healthchecks/{name}/statistics", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public HttpEntity<List<HealthcheckApiStatistic>> getStatistics(@PathVariable String name,
                                                                   @RequestParam(value = "period",
                                                                           required = false,
                                                                           defaultValue = "2m") String period) {
        List<HealthcheckApiStatistic> result = new LinkedList<>();
        Period p = Period.getPeriod(period);
        List<info.losd.galen.repository.dto.HealthcheckStatistic> stats =
                repo.getStatisticsForPeriod(name, p);

        stats.forEach(stat -> {
            result.add(new HealthcheckApiStatistic(stat.getTimestamp(), stat.getResponseTime(), stat.getStatusCode()));
        });

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/healthchecks/{name}/statistics/mean", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public HttpEntity<HealthcheckApiMean> getMean(@PathVariable String name,
                                                  @RequestParam(value = "period",
                                                          required = false,
                                                          defaultValue = "2m") String period) {
        Period p = Period.getPeriod(period);

        HealthcheckMean result = repo.getMeanForPeriod(name, p);
        return new ResponseEntity<>(new HealthcheckApiMean(result.getTimestamp(), result.getMean()), HttpStatus.OK);
    }


    @ExceptionHandler(IllegalArgumentException.class)
    void illegalArgumentException(HttpServletResponse response) throws
            IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.sendError(HttpStatus.BAD_REQUEST.value(), "The method specified is invalid");
    }
}
