package cn.cnu.contactbook.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;

/**
 * Created by dell on 2016/10/10.
 */
public class DBAdapter {

    private static final String DB_NAME = "contactBook.db";
    private static final String DB_TABLE = "contact";
    private static final int DB_version = 1;

    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_PHONE = "phone";

    private SQLiteDatabase db;
    private final Context context;

    private static class DBOpenHelper extends SQLiteOpenHelper {
        DBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        private static final String DB_CREATE = "create table " +
                DB_TABLE + "(" + KEY_ID + " integer primary key autoincrement," +
                KEY_NAME + " varchar(20)," + KEY_PHONE + " varchar(20))";

        @Override
        public void onCreate(SQLiteDatabase _db) {
            _db.execSQL(DB_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase _db, int _oldVersion, int _newVersion) {
            _db.execSQL("DROP TABLE IF EXISTS" + DB_TABLE);
            onCreate(_db);
        }

    }

    public DBAdapter(Context _context) {
        context = _context;
    }

    public void open() throws SQLiteException {
        DBOpenHelper dbOpenHelper = new DBOpenHelper(context, DB_NAME, null, DB_version);
        try {
            db = dbOpenHelper.getWritableDatabase();
        } catch (SQLiteException ex) {
            db = dbOpenHelper.getReadableDatabase();
        }
    }

    public void close() {
        if (db != null) {
            db.close();
            db = null;
        }
    }

    public long insert(Contact contact) {
        ContentValues newValues = new ContentValues();
        newValues.put(KEY_NAME, contact.getName());
        newValues.put(KEY_PHONE, contact.getPhone());
        return db.insert(DB_TABLE, null, newValues);
    }

    public long delete(int id) {
        return db.delete(DB_TABLE, KEY_ID + " like ? ", new String[]{id+""});
    }

    public long update(int id, Contact contact) {
        ContentValues updateValues = new ContentValues();
        updateValues.put(KEY_NAME, contact.getName());
        updateValues.put(KEY_PHONE, contact.getPhone());
        return db.update(DB_TABLE, updateValues, KEY_ID + " like ? ", new String[]{id + ""});
    }

    public Contact[] getContact(int id) {
        Cursor cursor = db.query(DB_TABLE, new String[]{KEY_ID, KEY_NAME, KEY_PHONE},
                KEY_ID + " like ? ", new String[]{id + ""}, null, null, null, null);
        return ConvertToContact(cursor);
    }

    public Contact[] getAll() {
        Cursor cursor = db.query(DB_TABLE, new String[]{KEY_ID, KEY_NAME, KEY_PHONE},
                null, null, null, null, KEY_NAME + " asc");
        return ConvertToContact(cursor);
    }

    private Contact[] ConvertToContact(Cursor cursor) {
        int resultCounts = cursor.getCount();
        if (resultCounts == 0 || !cursor.moveToFirst()) return null;
        Contact[] peoples = new Contact[resultCounts];
        for (int i = 0; i < resultCounts; i++) {
            peoples[i] = new Contact();
            peoples[i].setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
            peoples[i].setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
            peoples[i].setPhone(cursor.getString(cursor.getColumnIndex(KEY_PHONE)));
            cursor.moveToNext();
        }
        return peoples;
    }

    public Contact[] readContacts() {
        Cursor cursor = null;
        try {
            // 查询联系人数据
            cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            int resultCounts = cursor.getCount();
            Contact[] peoples = new Contact[resultCounts];
            int i = 0;
            while (cursor.moveToNext()) {
                peoples[i]=new Contact();
                // 获取联系人姓名
                String displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                // 获取联系人手机号
                String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));


                peoples[i].setName(displayName);
                peoples[i].setPhone(number);
                i++;
            }
            return peoples;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return new Contact[0];
    }
}
