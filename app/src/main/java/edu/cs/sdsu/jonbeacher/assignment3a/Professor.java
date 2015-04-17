package edu.cs.sdsu.jonbeacher.assignment3a;

public class Professor {
    private String mFirstName;
    private String mLastName;
    private String mId;

    public String getFirstName() {
        return mFirstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public String getFullName() {
        return mFirstName + mLastName;
    }

    public String getId() {
        return mId;
    }

    public void setFirstName(String firstName) {
        mFirstName = firstName;
    }

    public void setLastName(String lastName) {
        mLastName = lastName;
    }

    public void setId(String id) {
        mId = id;
    }

    public String toString() {
        return mFirstName + " " + mLastName ;
    }

}

