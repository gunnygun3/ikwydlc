package com.flipkart;

import com.google.common.base.Objects;

import java.util.Date;
import java.util.List;

/**
 * Created by gopi.vishwakarma on 05/06/15.
 */
public class ESDocument {
    private String userId;
    private String title;
    private String source;
    private Date timestamp;
    private String contents;
    private UserInfo organiser;
    private List<UserInfo> participants;
    private boolean attended;

    public ESDocument() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public UserInfo getOrganiser() {
        return organiser;
    }

    public void setOrganiser(UserInfo organiser) {
        this.organiser = organiser;
    }

    public List<UserInfo> getParticipants() {
        return participants;
    }

    public void setParticipants(List<UserInfo> participants) {
        this.participants = participants;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isAttended() {
        return attended;
    }

    public void setAttended(boolean attended) {
        this.attended = attended;
    }

    @Override
    public String toString() {
        return "ESDocument{" +
                "userId='" + userId + '\'' +
                ", title='" + title + '\'' +
                ", source='" + source + '\'' +
                ", timestamp=" + timestamp +
                ", contents='" + contents + '\'' +
                ", organiser=" + organiser +
                ", participants=" + participants +
                ", attended=" + attended +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ESDocument)) return false;

        ESDocument that = (ESDocument) o;

        if (attended != that.attended) return false;
        if (contents != null ? !contents.equals(that.contents) : that.contents != null) return false;
        if (organiser != null ? !organiser.equals(that.organiser) : that.organiser != null) return false;
        if (participants != null ? !participants.equals(that.participants) : that.participants != null) return false;
        if (source != null ? !source.equals(that.source) : that.source != null) return false;
        if (timestamp != null ? !timestamp.equals(that.timestamp) : that.timestamp != null) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = userId != null ? userId.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (source != null ? source.hashCode() : 0);
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        result = 31 * result + (contents != null ? contents.hashCode() : 0);
        result = 31 * result + (organiser != null ? organiser.hashCode() : 0);
        result = 31 * result + (participants != null ? participants.hashCode() : 0);
        result = 31 * result + (attended ? 1 : 0);
        return result;
    }
}
