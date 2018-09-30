package com.smartcitypune.smartpune;

/**
 * Created by Kapil on 29-09-2018.
 */

public class NotificationCase {
    String body;
    long timestamp;
    String title;

    public NotificationCase() {
    }

    public NotificationCase(String body, long timestamp, String title) {
        this.body = body;
        this.timestamp = timestamp;
        this.title = title;
    }

    @Override
    public String toString() {
        return "NotificationCase{" +
                "body='" + body + '\'' +
                ", timestamp=" + timestamp +
                ", title='" + title + '\'' +
                '}';
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
