package com.game.databaseandroid;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class ManagerDatabase extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "DataBaseSQL";

    //Таблица читателей
    public static final String TABLE_READER = "Reader";
    public static final String PASSWORD = "Number_Password";
    public static final String HOME = "Home_Address";
    public static final String FULLNAME = "Full_Name";

    //Таблица книг
    public static final String TABLE_BOOK = "Book";
    public static final String REGISTER = "Number_Register";
    public static final String NAME = "Name_Book";
    public static final String YEAR = "Year_Publishing";
    public static final String PAGES = "Count_Pages";
    public static final String SECTION = "Section";

    //Таблица регистрации
    public static final String TABLE_REGISTER = "RecordRegisterT";
    public static final String ID = "id";
    public static final String REGISTER_REGNUM = "Register";
    public static final String REGISTER_PASSWORD = "Password";
    public static final String DATA_ISSUE = "Data_Issue";
    public static final String DATA_RETURN = "Data_Return";

    private static final int DATABASE_VERSION = 9;

    public ManagerDatabase(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private void updateDatabase(SQLiteDatabase db, int oldVersion, int newVersion) {
        /*if(newVersion == 1){
            try{
                db.execSQL("CREATE TABLE " + TABLE_REGISTER + " ("
                        + ID + " INTEGER PRIMARY KEY, " +
                        REGISTER_REGNUM + " TEXT, " +
                        REGISTER_PASSWORD + " TEXT, " +
                        DATA_ISSUE + " TEXT, " +
                        DATA_RETURN + " TEXT, " +
                        "FOREIGN KEY (" + REGISTER_PASSWORD + ") REFERENCES " + TABLE_READER + "(" + PASSWORD + "), " +
                        "FOREIGN KEY (" + REGISTER_REGNUM + ") REFERENCES " + TABLE_BOOK + "(" + REGISTER + "));"
                );
            }catch(Exception e){}
        }else if(newVersion == 2){
            try{
                db.execSQL("CREATE TABLE " + TABLE_READER +
                        " (" + PASSWORD + " TEXT PRIMARY KEY, "
                        + HOME + " TEXT, " + FULLNAME + " TEXT);"
                );
            }catch(Exception e){}
        }else if(newVersion == 3){
            try{
                db.execSQL("CREATE TABLE " + TABLE_BOOK + " (" +
                        REGISTER + " TEXT PRIMARY KEY, " +
                        NAME + " TEXT, " +
                        YEAR + " INTEGER, " +
                        PAGES+  " INTEGER, " +
                        SECTION + " TEXT);");
            }catch (Exception e){}
        }*/
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        updateDatabase(db, oldVersion, newVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        updateDatabase(db, 0, DATABASE_VERSION);
    }
}
