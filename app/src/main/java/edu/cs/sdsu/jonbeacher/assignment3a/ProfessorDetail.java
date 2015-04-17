package edu.cs.sdsu.jonbeacher.assignment3a;

public class ProfessorDetail {
    private String mOffice;
    private String mPhone;
    private String mEmail;
    private String mAverageRating;
    private String mTotalRatings;

    private String mFirstName;
    private String mLastName;
    private String mId;

    public String getId() {
        return mId;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public String getFullName() {
        return mFirstName + mLastName;
    }

    public String getOffice() {
        return mOffice;
    }

    public String getPhone() {
        return mPhone;
    }

    public String getEmail() {
        return mEmail;
    }

    public String getAverageRating() {
        return mAverageRating;
    }

    public String getTotalRatings() {
        return mTotalRatings;
    }

    public void setFirstName(String firstName) {
        mFirstName = firstName;
    }

    public void setLastName(String lastName) {
        mLastName = lastName;
    }

    public void setID(String id) {
        mId = id;
    }

    public void setOffice(String office) {
        mOffice = office;
    }

    public void setPhone(String phone) {
        mPhone = phone;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public void setAverageRating(String averageRating) {
        mAverageRating = averageRating;
    }

    public void setTotalRatings(String totalRatings) {
        mTotalRatings = totalRatings;
    }

    public String toString() {
        return "\n" + mFirstName + " " + mLastName + "\n\nOffice: " + mOffice + " \n\nPhone: " + mPhone + " \n\nEmail: " + mEmail + " \n\nAverage Rating: " + mAverageRating + "/5" + " \n\nTotal Ratings: " + mTotalRatings + "\n";
    }
}