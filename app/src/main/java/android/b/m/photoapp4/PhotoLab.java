package android.b.m.photoapp4;

import android.b.m.photoapp4.Database.PhotoDbSchema.PhotoTable;
import android.b.m.photoapp4.Database.PhotoBaseHelper;
import android.b.m.photoapp4.Database.PhotoCursorWrapper;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PhotoLab {
    private Context mContext;
    private SQLiteDatabase mDatabase;
    private static PhotoLab sPhotoLab;

    public static PhotoLab get(Context context) {
        if(sPhotoLab == null) {
            sPhotoLab = new PhotoLab(context);
        }
        return sPhotoLab;
    }

    private PhotoLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new PhotoBaseHelper(mContext).getWritableDatabase();
    }

    public void addPhoto(Photo p) {
        ContentValues values = getContentValues(p);
        mDatabase.insert(PhotoTable.NAME, null, values);
    }

    public void updatePhoto(Photo p) {
        String uuidString = p.getId().toString();
        ContentValues values = getContentValues(p);
        mDatabase.update(PhotoTable.NAME, values, PhotoTable.Cols.UUID + " = ?", new String[] { uuidString });
    }

    public void deletePhoto(Photo p) {
        String uuidString = p.getId().toString();
        mDatabase.delete(PhotoTable.NAME, PhotoTable.Cols.UUID + " = ?", new String[] { uuidString});
    }

    public List<Photo> getPhotos() {
        List<Photo> mPhotos = new ArrayList<>();
        PhotoCursorWrapper cursor = queryPhotos(null, null);
        try{
            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                mPhotos.add(cursor.getPhoto());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return mPhotos;
    }

    public Photo getPhoto(UUID id) {
        PhotoCursorWrapper cursor = queryPhotos(PhotoTable.Cols.UUID + " = ?", new String[] { id.toString()});
        try{
            if(cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getPhoto();
        } finally {
            cursor.close();
        }
    }

    public File getPhotoFile(Photo photo) {
        File filesDir = mContext.getFilesDir();
        return new File(filesDir, photo.getPhotoFileName());
    }

    public ContentValues getContentValues(Photo photo) {
        ContentValues values = new ContentValues();
        values.put(PhotoTable.Cols.UUID, photo.getId().toString());
        values.put(PhotoTable.Cols.TITLE, photo.getTitle());
        values.put(PhotoTable.Cols.DATE, photo.getDate().getTime());
        values.put(PhotoTable.Cols.CHECKED, photo.getChecked() ? 1 : 0);
        return values;
    }

    public PhotoCursorWrapper queryPhotos(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                PhotoTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new PhotoCursorWrapper(cursor);
    }
}
