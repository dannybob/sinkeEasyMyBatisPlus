package com.sinke.easymybatisplus;


import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties("easy-mybatis-plus")
public class EProperties {

    private String scanEntityLocation="";

    public String getScanEntityLocation() {
        return scanEntityLocation;
    }

    public void setScanEntityLocation(String scanEntityLocation) {
        this.scanEntityLocation = scanEntityLocation;
    }

    public List<String> getDbUsers() {
        return dbUsers;
    }

    public void setDbUsers(List<String> dbUsers) {
        this.dbUsers = dbUsers;
    }

    private List<String > dbUsers=new ArrayList<>();




}
