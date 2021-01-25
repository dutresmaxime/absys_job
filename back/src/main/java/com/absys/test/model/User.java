package com.absys.test.model;

import java.util.Date;

public class User {
    private String id;
    private String firstname;
    private String lastname;
    private Date birthday;
    private String earthCountry;
    private String earthJob;
    private UserState state = UserState.DONE;

    public User(String id, String firstname, String lastname, Date birthday, String earthCountry, String earthJob) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.birthday = birthday;
        this.earthCountry = earthCountry;
        this.state = UserState.DONE;
        this.earthJob = earthJob;
    }

    public User() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public UserState getState() {
        return state;
    }

    public void setState(UserState state) {
        this.state = state;
    }

    public String getEarthCountry() {
        return earthCountry;
    }

    public void setEarthCountry(String earthCountry) {
        this.earthCountry = earthCountry;
    }

    public String getEarthJob() {
        return earthJob;
    }

    public void setEarthJob(String earthJob) {
        this.earthJob = earthJob;
    }
}
