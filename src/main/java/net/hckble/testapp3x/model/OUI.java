package net.hckble.testapp3x.model;

import com.google.gson.annotations.SerializedName;

public class OUI {

    @SerializedName("OUI")
    private String oui;
    @SerializedName("shortname")
    private String shortname;
    @SerializedName("longname")
    private String longname;

    public OUI(String oui, String shortname, String longname) {
        this.oui = oui;
        this.shortname = shortname;
        this.longname = longname;
    }

    public String getOui() {
        return oui;
    }

    public void setOui(String oui) {
        this.oui = oui;
    }

    public String getShortname() {
        return shortname;
    }

    public void setShortname(String shortname) {
        this.shortname = shortname;
    }

    public String getLongname() {
        return longname;
    }

    public void setLongname(String longname) {
        this.longname = longname;
    }

}
