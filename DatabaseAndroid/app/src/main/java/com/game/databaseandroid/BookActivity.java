package com.game.databaseandroid;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class BookActivity extends AppCompatActivity {
    private TableLayout _table = null;
    private EditText _regNum = null;
    private EditText _nameBook = null;
    private EditText _year = null;
    private EditText _countPages = null;
    private EditText _section = null;

    private Button _btnAdd = null;
    private Button _btnRefactor = null;
    private Button _btnDelete = null;

    private static ManagerDatabase dbManager;

    public static final int STD_SIZE_REGNUM = 10;

    private int _changeRowIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_layout);

        _table = (TableLayout) findViewById(R.id.tableReader);
        _regNum = (EditText) findViewById(R.id.reg_data);
        _nameBook = (EditText) findViewById(R.id.name_book);
        _year = (EditText) findViewById(R.id.year);
        _countPages = (EditText) findViewById(R.id.count_pages);
        _section = (EditText) findViewById(R.id.section);

        _btnAdd = (Button) findViewById(R.id.btnAdd);
        _btnRefactor = (Button) findViewById(R.id.btnRefactor);
        _btnDelete = (Button) findViewById(R.id.btnDelete);

        _btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    toBaseAllCells(_table, true);
                    addRow(_table, _regNum.getText().toString().trim(), _nameBook.getText().toString().trim(),
                            Integer.valueOf(_year.getText().toString().trim()),
                            Integer.valueOf(_countPages.getText().toString().trim()),
                            _section.getText().toString().trim(), true);
                }catch (Exception e){
                    Toast.makeText(BookActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        _btnRefactor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    refactorRow(_table,
                            _nameBook.getText().toString().trim(),
                            Integer.valueOf(_year.getText().toString().trim()),
                            Integer.valueOf(_countPages.getText().toString().trim()),
                            _section.getText().toString().trim());
                    toBaseAllCells(_table, true);
                }catch (Exception e){
                    Toast.makeText(BookActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        _btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    deleteChangeRow();
                    toBaseAllCells(_table, true);
                }catch (Exception e){
                    Toast.makeText(BookActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        dbManager = new ManagerDatabase(BookActivity.this);
        try {
            ReadAllData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ReadAllData() throws Exception {
        SQLiteDatabase database = dbManager.getWritableDatabase();
        Cursor cursor = database.query(ManagerDatabase.TABLE_BOOK, null, null,
                null, null, null, null);
        while(cursor.moveToNext()){
            int regIndex= cursor.getColumnIndex(ManagerDatabase.REGISTER),
                    nameIndex = cursor.getColumnIndex(ManagerDatabase.NAME),
                    yearIndex = cursor.getColumnIndex(ManagerDatabase.YEAR),
                    pagesIndex = cursor.getColumnIndex(ManagerDatabase.PAGES),
                    sectionIndex = cursor.getColumnIndex(ManagerDatabase.SECTION);
            addRow(_table, cursor.getString(regIndex).trim(),
                    cursor.getString(nameIndex).trim(),
                    cursor.getInt(yearIndex),
                    cursor.getInt(pagesIndex),
                    cursor.getString(sectionIndex).trim(), false);
        }
        cursor.close();
        database.close();
    }

    public void toBaseAllCells(TableLayout table, boolean flag){
        if((table == null) || (_changeRowIndex <= 0) || (_changeRowIndex >= table.getChildCount())){
            _changeRowIndex = 0;
            return;
        }

        ((TextView)table.getChildAt(_changeRowIndex).findViewById(R.id.bookCol1)).setBackgroundColor(Color.WHITE);
        ((TextView)table.getChildAt(_changeRowIndex).findViewById(R.id.bookCol2)).setBackgroundColor(Color.WHITE);
        ((TextView)table.getChildAt(_changeRowIndex).findViewById(R.id.bookCol3)).setBackgroundColor(Color.WHITE);
        ((TextView)table.getChildAt(_changeRowIndex).findViewById(R.id.bookCol4)).setBackgroundColor(Color.WHITE);
        ((TextView)table.getChildAt(_changeRowIndex).findViewById(R.id.bookCol5)).setBackgroundColor(Color.WHITE);

        if(flag)
            _changeRowIndex = 0;
    }

    public static boolean numberValidate(String regNum){
        if((regNum == null) || (regNum.length() != STD_SIZE_REGNUM))
            return false;
        for(int i = 0; i < regNum.length(); i++)
            if(!Character.isDigit(regNum.charAt(i)))
                return false;
        return true;
    }

    public boolean numberExists(TableLayout table, String regNum){
        if((!numberValidate(regNum)) || (table == null))
            return false;
        for(int i = 1; i < table.getChildCount(); i++){
            if(((TextView) (table.getChildAt(i)).findViewById(R.id.bookCol1)).getText().toString().equals(regNum))
                return true;
        }

        return false;
    }

    public void addRow(TableLayout table, String regNum, String nameBook, int year, int countPages, String section, boolean flag) throws Exception{
        if((table == null) || (regNum == null) || (section == null)
                || (year < 0) || (countPages <= 0)
                || (nameBook == null) || (nameBook.length() == 0)
                || (section.length() == 0)
                || (section.length() == 0)
                || (!numberValidate(regNum)))
            throw new Exception("Ошибка: не корректные входные данные!");
        if(numberExists(table, regNum))
            throw new Exception("Ошибка: данный регистрационный номер уже есть в базе данных!");

        LayoutInflater inflater = LayoutInflater.from(BookActivity.this);
        TableRow row = (TableRow) inflater.inflate(R.layout.book_template, null);
        ((TextView) row.findViewById(R.id.bookCol1)).setText(regNum);
        ((TextView) row.findViewById(R.id.bookCol2)).setText(nameBook);
        ((TextView) row.findViewById(R.id.bookCol3)).setText(String.valueOf(year));
        ((TextView) row.findViewById(R.id.bookCol4)).setText(String.valueOf(countPages));
        ((TextView) row.findViewById(R.id.bookCol5)).setText(section);

        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toBaseAllCells(table, false);
                for(int i = 1; i < table.getChildCount(); i++){
                    if(
                            (table.getChildAt(i) != null) && (table.getChildAt(i).findViewById(R.id.bookCol1) != null) &&
                            ((TextView)table.getChildAt(i).findViewById(R.id.bookCol1)).getText().toString()
                            .equals(((TextView) row.findViewById(R.id.bookCol1)).getText().toString())){
                        if(_changeRowIndex == i){
                            _changeRowIndex = 0;
                            return;
                        }
                        _changeRowIndex = i;
                        break;
                    }
                }

                if((_changeRowIndex <= 0) || (_changeRowIndex >= table.getChildCount())){
                    toBaseAllCells(table, true);
                    return;
                }

                ((TextView)table.getChildAt(_changeRowIndex).findViewById(R.id.bookCol1)).setBackgroundColor(Color.YELLOW);
                ((TextView)table.getChildAt(_changeRowIndex).findViewById(R.id.bookCol2)).setBackgroundColor(Color.YELLOW);
                ((TextView)table.getChildAt(_changeRowIndex).findViewById(R.id.bookCol3)).setBackgroundColor(Color.YELLOW);
                ((TextView)table.getChildAt(_changeRowIndex).findViewById(R.id.bookCol4)).setBackgroundColor(Color.YELLOW);
                ((TextView)table.getChildAt(_changeRowIndex).findViewById(R.id.bookCol5)).setBackgroundColor(Color.YELLOW);

                _regNum.setText(((TextView)table.getChildAt(_changeRowIndex).findViewById(R.id.bookCol1)).getText().toString());
                _nameBook.setText(((TextView)table.getChildAt(_changeRowIndex).findViewById(R.id.bookCol2)).getText().toString());
                _year.setText(((TextView)table.getChildAt(_changeRowIndex).findViewById(R.id.bookCol3)).getText().toString());
                _countPages.setText(((TextView)table.getChildAt(_changeRowIndex).findViewById(R.id.bookCol4)).getText().toString());
                _section.setText(((TextView)table.getChildAt(_changeRowIndex).findViewById(R.id.bookCol5)).getText().toString());
            }
        });

        table.addView(row);

        if(flag){
            SQLiteDatabase database = dbManager.getWritableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.put(ManagerDatabase.REGISTER, regNum);
            contentValues.put(ManagerDatabase.NAME, nameBook);
            contentValues.put(ManagerDatabase.YEAR, year);
            contentValues.put(ManagerDatabase.PAGES, countPages);
            contentValues.put(ManagerDatabase.SECTION, section);

            database.insert(ManagerDatabase.TABLE_BOOK, null, contentValues);
            database.close();
        }
    }

    public void refactorRow(TableLayout table, String nameBook, int year, int countPages, String section) throws Exception{
        if((table == null) || (section == null)
                || (year < 0) || (countPages <= 0)
                || (nameBook == null) || (nameBook.length() == 0)
                || (section.length() == 0)
                || (section.length() == 0))
            throw new Exception("Ошибка: не корректные входные данные!");
        if((_changeRowIndex <= 0) || (_changeRowIndex >= _table.getChildCount()))
            throw new Exception("Ошибка: не выделена строка для изменения!");

        SQLiteDatabase database = null;
        try{
            {
                SQLiteDatabase dbs = dbManager.getWritableDatabase();
                Cursor cursor = dbs.query(ManagerDatabase.TABLE_REGISTER, null, null,
                        null, null, null, null);
                boolean exist = false;
                if(cursor.moveToFirst()) {
                    while (!cursor.isAfterLast()) {
                        String data = cursor.getString(cursor.getColumnIndex(ManagerDatabase.REGISTER_REGNUM));
                        if(data.equals(((TextView) _table.getChildAt(_changeRowIndex).findViewById(R.id.bookCol1)).getText().toString())){
                            int yearReg = Integer.valueOf(cursor.getString(cursor.getColumnIndex(ManagerDatabase.DATA_ISSUE)).split("\\.")[2]);
                            if(yearReg < Integer.valueOf(year)){
                                exist = true;
                                break;
                            }
                        }
                        cursor.moveToNext();
                    }
                }
                cursor.close();
                dbs.close();

                if(exist)
                    throw new Exception();
            }

            database = dbManager.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(ManagerDatabase.NAME, nameBook);
            cv.put(ManagerDatabase.YEAR, year);
            cv.put(ManagerDatabase.PAGES, countPages);
            cv.put(ManagerDatabase.SECTION, section);
            database.update(ManagerDatabase.TABLE_BOOK, cv, "Number_Register = ?",
                    new String[]{
                            ((TextView) _table.getChildAt(_changeRowIndex).findViewById(R.id.bookCol1)).getText().toString()
                    });
            database.close();
        }catch(Exception e){
            Toast.makeText(BookActivity.this, "Ошибка: невозможно изменить существующую запись", Toast.LENGTH_LONG).show();
            toBaseAllCells(table, true);
            if(database != null)
                database.close();
            return;
        }

        ((TextView) _table.getChildAt(_changeRowIndex).findViewById(R.id.bookCol2)).setText(nameBook);
        ((TextView) _table.getChildAt(_changeRowIndex).findViewById(R.id.bookCol3)).setText(String.valueOf(year));
        ((TextView) _table.getChildAt(_changeRowIndex).findViewById(R.id.bookCol4)).setText(String.valueOf(countPages));
        ((TextView) _table.getChildAt(_changeRowIndex).findViewById(R.id.bookCol5)).setText(section);
    }

    public void deleteChangeRow() throws Exception{
        if((_changeRowIndex <= 0) || (_changeRowIndex >= _table.getChildCount()))
            throw new Exception("Ошибка: не выделена строка для удаления!");

        SQLiteDatabase database = null;
        try{
            {
                SQLiteDatabase dbs = dbManager.getWritableDatabase();
                Cursor cursor = dbs.query(ManagerDatabase.TABLE_REGISTER, null, null,
                        null, null, null, null);
                boolean exist = false;
                if(cursor.moveToFirst()) {
                    while (!cursor.isAfterLast()) {
                        String data = cursor.getString(cursor.getColumnIndex(ManagerDatabase.REGISTER_REGNUM));
                        if(data.equals(((TextView) _table.getChildAt(_changeRowIndex).findViewById(R.id.bookCol1)).getText().toString())){
                            exist = true;
                            break;
                        }
                        cursor.moveToNext();
                    }
                }
                cursor.close();
                dbs.close();

                if(exist)
                    throw new Exception();
            }

            database = dbManager.getWritableDatabase();
            database.delete(ManagerDatabase.TABLE_BOOK, "Number_Register = ?",
                    new String[]{
                            ((TextView) _table.getChildAt(_changeRowIndex).findViewById(R.id.bookCol1)).getText().toString()
                    });
            database.close();
        }catch (Exception e){
            Toast.makeText(BookActivity.this, "Ошибка: невозможно удалить зарегистрированную запись!", Toast.LENGTH_LONG).show();
            toBaseAllCells(_table, true);
            if(database != null)
                database.close();
            return;
        }

        _table.removeViewAt(_changeRowIndex);
    }
}
