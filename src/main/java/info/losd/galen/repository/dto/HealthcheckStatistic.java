package info.losd.galen.repository.dto;

import java.time.Instant;
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
public class HealthcheckStatistic {
    private Instant timestamp;
    private long responseTime;
    private long statusCode;

    public HealthcheckStatistic(List<Object> values) {
        this.timestamp = Instant.parse((String) values.get(0));
        this.responseTime = Math.round((double) values.get(1));
        this.statusCode = Math.round((double) values.get(2));
    }

    public HealthcheckStatistic(String timestamp, long responseTime, long statusCode) {
        this.timestamp = Instant.parse(timestamp);
        this.responseTime = responseTime;
        this.statusCode = statusCode;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public long getResponseTime() {
        return responseTime;
    }

    public long getStatusCode() {
        return statusCode;
    }

    @Override
    public int hashCode() {
        int result = timestamp != null ? timestamp.hashCode() : 0;
        result = 31 * result + (int) (responseTime ^ (responseTime >>> 32));
        result = 31 * result + (int) (statusCode ^ (statusCode >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        HealthcheckStatistic that = (HealthcheckStatistic) obj;
        return this.timestamp.equals(that.timestamp) && this.responseTime == that.responseTime && this.statusCode == that.statusCode;
    }
}
