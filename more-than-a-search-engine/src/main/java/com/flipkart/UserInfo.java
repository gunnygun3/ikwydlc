package com.flipkart;

import com.google.common.base.Objects;

/**
 * Created by gopi.vishwakarma on 05/06/15.
 */
public class UserInfo {
    private String name;
    private String designation;
    private String team;
    private String email;


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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("name", name)
                .add("designation", designation)
                .add("team", team)
                .add("email", email)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserInfo userInfo = (UserInfo) o;

        if (name != null ? !name.equals(userInfo.name) : userInfo.name != null) return false;
        if (designation != null ? !designation.equals(userInfo.designation) : userInfo.designation != null)
            return false;
        if (team != null ? !team.equals(userInfo.team) : userInfo.team != null) return false;
        return !(email != null ? !email.equals(userInfo.email) : userInfo.email != null);

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (designation != null ? designation.hashCode() : 0);
        result = 31 * result + (team != null ? team.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        return result;
    }
}
