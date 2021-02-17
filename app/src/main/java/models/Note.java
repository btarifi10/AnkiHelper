package models;

public class Note {
    private String deckName;
    private String modelName;
    private String fields;

    public Note(String deckName, String modelName, String fields) {
        this.deckName = deckName;
        this.modelName = modelName;
        this.fields = fields;
    }

    public String getDeckName() {
        return deckName;
    }

    public void setDeckName(String deckName) {
        this.deckName = deckName;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getFields() {
        return fields;
    }

    public void setFields(String fields) {
        this.fields = fields;
    }

    public String toJSON() {
        return "{" +
                "\"deckName\":\"" + deckName + "\"" +
                ",\"modelName\":\"" + modelName + "\"" +
                ",\"fields\":" + fields +
                '}';
    }
}
