package h.group.sem.bumpingbike.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import h.group.sem.bumpingbike.Database.Tables.CreateTables;
import h.group.sem.bumpingbike.Models.Position;

/**
 * Created by ditlev on 12/15/17.
 */

public class DbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "pos";
    private Context context;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CreateTables.PosData.SQL_CREATE_ENTRIES);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public ArrayList<Position> readDb() {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery("SELECT * FROM " + CreateTables.PosData.TABLE_NAME, null);

        ArrayList<Position> SQLitePosList = new ArrayList<>();

        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    String id = c.getString(c.getColumnIndex(CreateTables.PosData.COLUMN_ID));
                    double lat = c.getDouble(c.getColumnIndex(CreateTables.PosData.COLUMN_LAT));
                    double mLong = c.getDouble(c.getColumnIndex(CreateTables.PosData.COLUMN_LONG));

                    SQLitePosList.add(new Position(id, lat, mLong));
                } while (c.moveToNext());
            }
        }
        c.close();
        db.close();
        return SQLitePosList;
    }
}
