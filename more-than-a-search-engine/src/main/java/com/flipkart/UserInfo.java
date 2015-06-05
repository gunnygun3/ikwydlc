package com.flipkart;

import com.google.common.base.Objects;

/**
 * Created by gopi.vishwakarma on 05/06/15.
 */
public class UserInfo {
    private String name;
    private String designation;
    private String team;


    public UserInfo() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("name", name)
                .add("designation", designation)
                .add("team", team)
                .toString();
    }
}
