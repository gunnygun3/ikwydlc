package com.flipkart;

import com.google.common.base.Objects;

/**
 * Created by gopi.vishwakarma on 05/06/15.
 */
public class Content {
    private String content;

    public Content() {
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("content", content)
                .toString();
    }
}
