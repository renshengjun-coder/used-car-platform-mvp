package com.usedcar.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private Jwt jwt = new Jwt();
    private Cors cors = new Cors();
    private Recommendation recommendation = new Recommendation();
    private Upload upload = new Upload();

    public Jwt getJwt() { return jwt; }
    public void setJwt(Jwt jwt) { this.jwt = jwt; }
    public Cors getCors() { return cors; }
    public void setCors(Cors cors) { this.cors = cors; }
    public Recommendation getRecommendation() { return recommendation; }
    public void setRecommendation(Recommendation recommendation) { this.recommendation = recommendation; }
    public Upload getUpload() { return upload; }
    public void setUpload(Upload upload) { this.upload = upload; }

    public static class Jwt {
        private String secret;
        private long expirationMs = 86400000;
        public String getSecret() { return secret; }
        public void setSecret(String secret) { this.secret = secret; }
        public long getExpirationMs() { return expirationMs; }
        public void setExpirationMs(long expirationMs) { this.expirationMs = expirationMs; }
    }

    public static class Cors {
        private String allowedOrigins = "http://localhost:3000";
        public String getAllowedOrigins() { return allowedOrigins; }
        public void setAllowedOrigins(String allowedOrigins) { this.allowedOrigins = allowedOrigins; }
    }

    public static class Recommendation {
        private double weightAttrMatch = 0.5;
        private double weightPopularity = 0.3;
        private double weightRecency = 0.2;
        private int popularWindowDays = 7;
        private int maxResults = 10;
        public double getWeightAttrMatch() { return weightAttrMatch; }
        public void setWeightAttrMatch(double v) { this.weightAttrMatch = v; }
        public double getWeightPopularity() { return weightPopularity; }
        public void setWeightPopularity(double v) { this.weightPopularity = v; }
        public double getWeightRecency() { return weightRecency; }
        public void setWeightRecency(double v) { this.weightRecency = v; }
        public int getPopularWindowDays() { return popularWindowDays; }
        public void setPopularWindowDays(int v) { this.popularWindowDays = v; }
        public int getMaxResults() { return maxResults; }
        public void setMaxResults(int v) { this.maxResults = v; }
    }

    public static class Upload {
        private int maxPhotos = 9;
        public int getMaxPhotos() { return maxPhotos; }
        public void setMaxPhotos(int maxPhotos) { this.maxPhotos = maxPhotos; }
    }
}
