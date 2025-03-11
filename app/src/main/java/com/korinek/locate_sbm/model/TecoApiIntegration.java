package com.korinek.locate_sbm.model;

public class TecoApiIntegration {

    private String tecoApiUrl;
    private String tecoApiBuildingName;
    private boolean useAuthorization;
    private String username;
    private String password;

    public TecoApiIntegration() {
        this.tecoApiUrl = "";
        this.tecoApiBuildingName = "";
        this.useAuthorization = false;
        this.username = "";
        this.password = "";
    }

    public String getTecoApiUrl() {
        return tecoApiUrl;
    }

    public void setTecoApiUrl(String tecoApiUrl) {
        this.tecoApiUrl = tecoApiUrl;
    }

    public String getTecoApiBuildingName() {
        return tecoApiBuildingName;
    }

    public void setTecoApiBuildingName(String tecoApiBuildingName) {
        this.tecoApiBuildingName = tecoApiBuildingName;
    }

    public boolean isUseAuthorization() {
        return useAuthorization;
    }

    public void setUseAuthorization(boolean useAuthorization) {
        this.useAuthorization = useAuthorization;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
