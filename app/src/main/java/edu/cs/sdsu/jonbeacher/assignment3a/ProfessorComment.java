package edu.cs.sdsu.jonbeacher.assignment3a;

public class ProfessorComment {
    private String mText;
    private String mDate;

    public String getText() {
        return mText;
    }

    public String getDate() {
        return mDate;
    }

    public void setText(String text) {
        mText = text;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public String toString() {
        return mDate + ": " + mText;
    }

}
