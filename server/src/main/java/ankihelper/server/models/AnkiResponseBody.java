package ankihelper.server.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import models.AnkiRequestBody;

public class AnkiResponseBody {
    @JsonProperty("result")
    private Object result;
    @JsonProperty("error")
    private String error;

    public AnkiResponseBody() {}

    public AnkiResponseBody(String result, String error) {
        this.result = result;
        this.error = error;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
