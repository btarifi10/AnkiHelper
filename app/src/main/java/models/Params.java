package models;

public class Params {
    private Note note;

    public Params(Note note) {
        this.note = note;
    }

    public Note getNote() {
        return note;
    }

    public void setNote(Note note) {
        this.note = note;
    }

    public String toJSON() {
        if (this.note == null) {
            return "{}";
        }

        return "{" +
                "\"note\":" + note.toJSON() +
                "}";
    }
}
