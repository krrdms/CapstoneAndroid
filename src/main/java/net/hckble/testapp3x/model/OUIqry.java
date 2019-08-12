package net.hckble.testapp3x.model;

import com.google.gson.annotations.SerializedName;

public class OUIqry {

    @SerializedName("oui")
    private String oui;

    public OUIqry(String oui) {
        this.oui = oui;
    }

    public String getOui() {
        return oui;
    }

    public void setOui(String oui) {
        this.oui = oui;
    }

}
