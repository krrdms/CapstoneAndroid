package net.hckble.testapp3x.model;

import com.google.gson.annotations.SerializedName;

public class AccessPoint {

    @SerializedName("bssid")
    private String bssid;
    @SerializedName("ssid")
    private String ssid;
    @SerializedName("capabilities")
    private String capabilities;
    private String capability[];

    public AccessPoint(String bssid, String ssid, String capabilities) {
        this.bssid = bssid;
        this.ssid = ssid;
        if ((capabilities.startsWith("[")) && (capabilities.endsWith("]"))) {
            capabilities = capabilities.substring(1, capabilities.length() - 2);
            capabilities = capabilities.replace("][", "/");
            capability = capabilities.split("//");
        } else capabilities = "null";
        this.capabilities = capabilities;
    }

    public String getBSSID() {
        return bssid;
    }

    public void setBSSID(String bssid) {
        this.bssid = bssid;
    }

    public String getSSID() {
        return ssid;
    }

    public void setSSID(String ssid) {
        this.ssid = ssid;
    }

    public String getCapabilities() { return capabilities; }

    public void setCapabilities (String capabilities) {
        if ((capabilities.startsWith("[")) && (capabilities.endsWith("]"))) {
            capabilities = capabilities.substring(1, capabilities.length() - 2);
            capabilities = capabilities.replace("][", "/");
            this.capability = capabilities.split("//");
        } else capabilities = "null";
        this.capabilities = capabilities;
    }

    public boolean isEncrypted () {
        return (this.capabilities.contains("WPA2")) ||
                (this.capabilities.contains("WPA")) ||
                (this.capabilities.contains("WEP"));
    }

    public boolean isHidden () {
        return this.ssid.length() == 0;
    }

}