package info.losd.galen.repository;

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
public class HealthcheckDetails {
    private long duration;
    private int statusCode;
    private Healthcheck healthcheck;

    private HealthcheckDetails(Healthcheck healthcheck, long duration, int statusCode) {
        this.healthcheck = healthcheck;
        this.duration = duration;
        this.statusCode = statusCode;
    }

    public long getDuration() {
        return duration;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public Healthcheck getHealthcheck() {
        return healthcheck;
    }

    public static HealthcheckDetails.Duration tag(String name) {
        return new HealthcheckDetails.Builder(name);
    }

    public static class Builder implements Duration, StatusCode, Build {
        private long duration;
        private int statusCode;
        private Healthcheck healthcheck;

        private Builder(String tag) {
            this.healthcheck = new Healthcheck(tag);
        }

        public StatusCode duration(long duration) {
            this.duration = duration;
            return this;
        }

        public Build statusCode(int statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public HealthcheckDetails build() {
            return new HealthcheckDetails(healthcheck, duration, statusCode);
        }
    }

    public interface Duration {
        StatusCode duration(long duration);
    }

    public interface StatusCode {
        Build statusCode(int statusCode);
    }

    public interface Build {
        HealthcheckDetails build();
    }
}
