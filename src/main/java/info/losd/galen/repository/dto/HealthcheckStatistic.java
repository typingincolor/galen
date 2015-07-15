package info.losd.galen.repository.dto;

import java.time.Instant;

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
public class HealthcheckStatistic implements Comparable {
    private Instant timestamp;
    private int responseTime;
    private int statusCode;

    public HealthcheckStatistic(String time, int responseTime, int statusCode) {
        this.timestamp = Instant.parse(time);
        this.responseTime = responseTime;
        this.statusCode = statusCode;
    }

    @Override
    public int compareTo(Object o) {
        HealthcheckStatistic that = (HealthcheckStatistic) o;

        if (this.timestamp.equals(that.timestamp) && this.responseTime == that.responseTime && this.statusCode == that.statusCode) {
            return 0;
        } else if (this.timestamp.isBefore(that.timestamp)) {
            return -1;
        }

        return 1;
    }
}
