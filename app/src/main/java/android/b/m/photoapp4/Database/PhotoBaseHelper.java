package android.b.m.photoapp4.Database;

import android.b.m.photoapp4.Database.PhotoDbSchema.PhotoTable;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PhotoBaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "photoBase.db";

    public PhotoBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + PhotoTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                PhotoTable.Cols.UUID + ", " +
                PhotoTable.Cols.TITLE + ", " +
                PhotoTable.Cols.DATE + ", " +
                PhotoTable.Cols.CHECKED +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        return;
    }
}
