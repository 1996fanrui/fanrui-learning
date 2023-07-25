package com.dream.flink.kafka.demo;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonProperty;

public class SamplePojo {

    public SamplePojo() {
    }

    private int app;

    @JsonProperty("city_id")
    private int cityId;

    @JsonProperty("user_id")
    private String userId;

    private String ts;

    public int getApp() {
        return app;
    }

    public void setApp(int app) {
        this.app = app;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

    @Override
    public String toString() {
        return "SamplePojo{" +
                "ts=" + ts +
                ", app=" + app +
                ", cityId=" + cityId +
                ", userId='" + userId + '\'' +
                '}';
    }
}
