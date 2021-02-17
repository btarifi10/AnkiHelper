package models;

public class AnkiRequestBody {
    private String action;
    private String version;
    private Params params;

    public AnkiRequestBody(String action, String version, Params params) {
        this.action = action;
        this.version = version;
        this.params = params;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Params getParams() {
        return params;
    }

    public void setParams(Params params) {
        this.params = params;
    }

    public String toJSON() {
        return "{" +
                "\"action\":\"" + action + "\"," +
                "\"version\":\"" + version + "\"," +
                "\"params\":" + params.toJSON() +
                "}";
    }
}
