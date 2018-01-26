package android.b.m.photoapp4;

import java.util.Date;
import java.util.UUID;

public class Photo {
    private String mtitle;
    private UUID mId;
    private Date mDate;
    private boolean mIsChecked;

    public Photo() {
        this(UUID.randomUUID());
    }
    public Photo(UUID id) {
        mId = id;
        mDate = new Date();
        mIsChecked = true;
    }

    public String getTitle() {
        return mtitle;
    }

    public void setTitle(String title) {
        mtitle = title;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public UUID getId() {
        return mId;
    }

    public String getPhotoFileName() {
        return "IMG" + getId().toString() + ".jpg";
    }

    public boolean getChecked() {
        return mIsChecked;
    }

    public void setChecked(boolean isChecked) {
        mIsChecked = isChecked;
    }
}
