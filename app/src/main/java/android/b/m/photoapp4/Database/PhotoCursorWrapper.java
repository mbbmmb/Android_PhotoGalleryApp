package android.b.m.photoapp4.Database;

import android.b.m.photoapp4.Database.PhotoDbSchema.PhotoTable;
import android.b.m.photoapp4.Photo;
import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.Date;
import java.util.UUID;

public class PhotoCursorWrapper extends CursorWrapper {

    public PhotoCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Photo getPhoto() {
        String uuidString = getString(getColumnIndex(PhotoTable.Cols.UUID));
        String title = getString(getColumnIndex(PhotoTable.Cols.TITLE));
        long date = getLong(getColumnIndex(PhotoTable.Cols.DATE));
        int checked = getInt(getColumnIndex(PhotoTable.Cols.CHECKED));

        Photo photo = new Photo(UUID.fromString(uuidString));
        photo.setTitle(title);
        photo.setDate(new Date(date));
        photo.setChecked(checked != 0);

        return photo;
    }
}
