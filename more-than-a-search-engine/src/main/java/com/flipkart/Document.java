package com.flipkart;

import com.google.common.base.Objects;

import java.util.Date;
import java.util.List;

/**
 * Created by gopi.vishwakarma on 05/06/15.
 */
public class Document {
    private String userId;
    private String title;
    private String source;
    private Date timestamp;
    private String contents;
    private UserInfo organiser;
    private List<UserInfo> participants;
    private boolean attended;

    public Document() {
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
        if (o == null || getClass() != o.getClass()) return false;

        Document document = (Document) o;

        if (attended != document.attended) return false;
        if (userId != null ? !userId.equals(document.userId) : document.userId != null) return false;
        if (title != null ? !title.equals(document.title) : document.title != null) return false;
        if (source != null ? !source.equals(document.source) : document.source != null) return false;
        if (timestamp != null ? !timestamp.equals(document.timestamp) : document.timestamp != null) return false;
        if (contents != null ? !contents.equals(document.contents) : document.contents != null) return false;
        if (organiser != null ? !organiser.equals(document.organiser) : document.organiser != null) return false;
        return !(participants != null ? !participants.equals(document.participants) : document.participants != null);

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
