package kutschi.de.httpplugin.model;

import java.io.Serializable;

/**
 * Created by seb on 09.03.17.
 */

public class Profile implements Serializable {

    private String description;
    private String url;
    private int method;
    private String[] zones;
    private String username;
    private String password;
    private String contentType;
    private String enteringContent;
    private String leavingContent;

    public Profile(String description, String url, int method, String[] zones) {
        this.description = description;
        this.url = url;
        this.method = method;
        this.zones = zones;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getMethod() {
        return method;
    }

    public void setMethod(int method) {
        this.method = method;
    }

    public String[] getZones() {
        return zones;
    }

    public void setZones(String[] zones) {
        this.zones = zones;
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

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getEnteringContent() {
        return enteringContent;
    }

    public void setEnteringContent(String enteringContent) {
        this.enteringContent = enteringContent;
    }

    public String getLeavingContent() {
        return leavingContent;
    }

    public void setLeavingContent(String leavingContent) {
        this.leavingContent = leavingContent;
    }
}
