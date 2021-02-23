package models;

public class Note {
    private String deckName;
    private String modelName;
    private String front;
    private String back;

    public Note(String deckName, String modelName, String front, String back) {
        this.deckName = deckName;
        this.modelName = modelName;
        this.front = front;
        this.back = back;
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

    public String getFront() {
        return front;
    }

    public void setFront(String front) {
        this.front = front;
    }

    public String getBack() {
        return back;
    }

    public void setBack(String back) {
        this.back = back;
    }

    public String toJSON() {
        String fields = "";

        if (modelName.equals("Cloze")) {
            fields = "{"+
                    "\"Text\":\"" + front + "\"," +
                    "\"Back Extra\":\"" + back +
                    "\"}";
        } else {
            fields = "{"+
                    "\"Front\":\"" + front + "\"," +
                    "\"Back\":\"" + back +
                    "\"}";
        }

        return "{" +
                "\"deckName\":\"" + deckName + "\"" +
                ",\"modelName\":\"" + modelName + "\"" +
                ",\"fields\":" + fields +
                '}';
    }
}
