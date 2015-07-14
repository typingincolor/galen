package info.losd.galen.api;

import info.losd.galen.client.ApiClient;
import info.losd.galen.client.ApiMethod;
import info.losd.galen.client.ApiRequest;
import info.losd.galen.client.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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

    @RequestMapping(value = "/healthcheck", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    HealthcheckResult run(@RequestBody HealthcheckRequest galen) {
        ApiRequest.Header apiRequestBuilder = ApiRequest.tag(galen.getTag().getName()).url(galen.getUrl()).method(ApiMethod.valueOf(galen.getMethod()));
        galen.getHeaders().forEach((header, value) -> apiRequestBuilder.header(header, value));
        ApiResponse response = client.execute(apiRequestBuilder.build());

        return HealthcheckResult.statusCode(response.getStatusCode()).build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    void illegalArgumentException(HttpServletResponse response) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.sendError(HttpStatus.BAD_REQUEST.value(), "The method specified is invalid");
    }
}
