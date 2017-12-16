package h.group.sem.bumpingbike.Database.Tables;

import android.provider.BaseColumns;

/**
 * Created by ditlev on 12/15/17.
 */

public final class CreateTables {
    private CreateTables() {}

    public static class PosData implements BaseColumns {

        // Table name
        public static final String TABLE_NAME = "pos";

        /**
         * Column names
         */
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_LAT = "lat";
        public static final String COLUMN_LONG = "long";

        /**
         * Create table User
         */
        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + PosData.TABLE_NAME + " (" +
                        PosData._ID + " INTEGER PRIMARY KEY," +
                        PosData.COLUMN_ID + " TEXT," +
                        PosData.COLUMN_LAT + " REAL," +
                        PosData.COLUMN_LONG + " REAL);";
    }
}
