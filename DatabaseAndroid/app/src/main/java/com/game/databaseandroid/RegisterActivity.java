package com.game.databaseandroid;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class RegisterActivity extends AppCompatActivity {

    private TableLayout _table = null;
    private Button _btnAdd = null;
    private EditText _regNumber = null;
    private EditText _passNumber = null;
    private EditText _dateIssue = null;
    private EditText _dateReturn = null;
    private Button _btnRefactor = null;
    private Button _btnDelete = null;
    private static ManagerDatabase dbManager;

    public static final int STD_SIZE_PASS = 10;

    private int _changeRowIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);

        _table = (TableLayout) findViewById(R.id.tableReader);
        _regNumber = (EditText) findViewById(R.id.dataNumber);
        _passNumber = (EditText) findViewById(R.id.dataPassword);
        _dateIssue = (EditText) findViewById(R.id.dataDateIssue);
        _dateReturn = (EditText) findViewById(R.id.dataDateReturn);
        _btnAdd = (Button) findViewById(R.id.btnAdd);
        _btnRefactor = (Button) findViewById(R.id.btnRefactor);
        _btnDelete = (Button) findViewById(R.id.btnDelete);

        _btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    toBaseAllCells(_table, true);
                    addRow(_table, lastIndex(_table), _regNumber.getText().toString().trim(), _passNumber.getText().toString().trim(),
                            _dateIssue.getText().toString().trim(), _dateReturn.getText().toString().trim(), true);
                }catch (Exception e){
                    Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        _btnRefactor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    refactorRow(_table, _dateIssue.getText().toString().trim(),
                            _dateReturn.getText().toString().trim());
                    toBaseAllCells(_table, true);
                }catch (Exception e){
                    Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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
                    Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        dbManager = new ManagerDatabase(RegisterActivity.this);
        try {
            ReadAllData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int lastIndex(TableLayout table){
        if((table == null) || (table.getChildCount() <= 1))
            return 1;
        int max = 1;

        for(int i = 1; i < table.getChildCount(); i++){
            int value = Integer.valueOf(((TextView)table.getChildAt(i).findViewById(R.id.registerCol1)).getText().toString());
            if(max < value)
                max = value;
        }

        return (max + 1);
    }

    private void ReadAllData() throws Exception {
        SQLiteDatabase database = dbManager.getWritableDatabase();
        Cursor cursor = database.query(ManagerDatabase.TABLE_REGISTER, null, null,
                null, null, null, null);
        while(cursor.moveToNext()){
            int index = cursor.getColumnIndex(ManagerDatabase.ID);
            int numIndex= cursor.getColumnIndex(ManagerDatabase.REGISTER_REGNUM),
                    passIndex = cursor.getColumnIndex(ManagerDatabase.REGISTER_PASSWORD),
                    issueIndex = cursor.getColumnIndex(ManagerDatabase.DATA_ISSUE),
                    returnIndex = cursor.getColumnIndex(ManagerDatabase.DATA_RETURN);
            addRow(_table, cursor.getInt(index),
                    cursor.getString(numIndex).trim(),
                    cursor.getString(passIndex).trim(),
                    cursor.getString(issueIndex).trim(),
                    cursor.getString(returnIndex).trim(), false);
        }
        cursor.close();
        database.close();
    }

    public void toBaseAllCells(TableLayout table, boolean flag){
        if((table == null) || (_changeRowIndex <= 0) || (_changeRowIndex >= table.getChildCount())){
            _changeRowIndex = 0;
            return;
        }

        ((TextView)table.getChildAt(_changeRowIndex).findViewById(R.id.registerCol1)).setBackgroundColor(Color.WHITE);
        ((TextView)table.getChildAt(_changeRowIndex).findViewById(R.id.registerCol2)).setBackgroundColor(Color.WHITE);
        ((TextView)table.getChildAt(_changeRowIndex).findViewById(R.id.registerCol3)).setBackgroundColor(Color.WHITE);
        ((TextView)table.getChildAt(_changeRowIndex).findViewById(R.id.registerCol4)).setBackgroundColor(Color.WHITE);
        ((TextView)table.getChildAt(_changeRowIndex).findViewById(R.id.registerCol5)).setBackgroundColor(Color.WHITE);

        if(flag)
            _changeRowIndex = 0;
    }

    public static boolean isDateValidate(String date){
        if((date == null) || (date.length() == 0))
            return false;
        int count = 0;
        for(int i = 0; i < date.length(); i++){
            if(date.charAt(i) == '.'){
                count++;
            }else if(!Character.isDigit(date.charAt(i))){
                return false;
            }
        }
        if(count != 2)
            return false;

        String[] dateSplit = date.split("\\.");
        if((dateSplit.length != 3) || (dateSplit[2].length() != 4)
                || (dateSplit[0].length() != 2) || (dateSplit[1].length() != 2))
            return false;

        int value = Integer.valueOf(dateSplit[0]);
        if((value <= 0) || (value > 31))
            return false;
        value = Integer.valueOf(dateSplit[1]);
        if((value <= 0) || (value > 12))
            return false;

        return true;
    }

    public void addRow(TableLayout table, int id,  String numData, String passData, String issueData, String returnData, boolean flag) throws Exception{
        if((table == null)
        || (numData == null) || (passData == null) || (issueData == null) || (returnData == null)
        || (numData.length() == 0) || (passData.length() == 0) || (issueData.length() != 10) || (returnData.length() != 10)
        || (!ReaderActivity.passwordValidate(passData)) || (!BookActivity.numberValidate(numData))
        || (!isDateValidate(issueData)) || (!isDateValidate(returnData)))
            throw new Exception("Ошибка: не корректные входные данные!");

        DateFormat format = new SimpleDateFormat("dd.mm.yyyy");
        if(format.parse(issueData).after(
                format.parse(returnData)
        )){
            throw new Exception("Ошибка: дата выдачи не может быть позже даты возврата!");
        }

        LayoutInflater inflater = LayoutInflater.from(RegisterActivity.this);
        TableRow row = (TableRow) inflater.inflate(R.layout.register_template, null);
        ((TextView) row.findViewById(R.id.registerCol1)).setText(String.valueOf(id));
        ((TextView) row.findViewById(R.id.registerCol2)).setText(numData);
        ((TextView) row.findViewById(R.id.registerCol3)).setText(passData);
        ((TextView) row.findViewById(R.id.registerCol4)).setText(issueData);
        ((TextView) row.findViewById(R.id.registerCol5)).setText(returnData);

        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toBaseAllCells(table, false);
                for(int i = 1; i < table.getChildCount(); i++){
                    if(
                            (table.getChildAt(i) != null) && (table.getChildAt(i).findViewById(R.id.registerCol1) != null) &&
                            ((TextView)table.getChildAt(i).findViewById(R.id.registerCol1)).getText().toString()
                            .equals(((TextView) row.findViewById(R.id.registerCol1)).getText().toString())){
                        if(_changeRowIndex == i){
                            _changeRowIndex = 0;
                            return;
                        }
                        _changeRowIndex = i;
                        break;
                    }
                }

                ((TextView)table.getChildAt(_changeRowIndex).findViewById(R.id.registerCol1)).setBackgroundColor(Color.YELLOW);
                ((TextView)table.getChildAt(_changeRowIndex).findViewById(R.id.registerCol2)).setBackgroundColor(Color.YELLOW);
                ((TextView)table.getChildAt(_changeRowIndex).findViewById(R.id.registerCol3)).setBackgroundColor(Color.YELLOW);
                ((TextView)table.getChildAt(_changeRowIndex).findViewById(R.id.registerCol4)).setBackgroundColor(Color.YELLOW);
                ((TextView)table.getChildAt(_changeRowIndex).findViewById(R.id.registerCol5)).setBackgroundColor(Color.YELLOW);

                _regNumber.setText(((TextView)table.getChildAt(_changeRowIndex).findViewById(R.id.registerCol2)).getText().toString());
                _passNumber.setText(((TextView)table.getChildAt(_changeRowIndex).findViewById(R.id.registerCol3)).getText().toString());
                _dateIssue.setText(((TextView)table.getChildAt(_changeRowIndex).findViewById(R.id.registerCol4)).getText().toString());
                _dateReturn.setText(((TextView)table.getChildAt(_changeRowIndex).findViewById(R.id.registerCol5)).getText().toString());
            }
        });

        if(flag){
            SQLiteDatabase database = null;
            try{
                {
                    SQLiteDatabase dbs = dbManager.getWritableDatabase();
                    Cursor cursor = dbs.query(ManagerDatabase.TABLE_READER, null, null,
                            null, null, null, null);
                    boolean exist = false;
                    if(cursor.moveToFirst()) {
                        while (!cursor.isAfterLast()) {
                            String data = cursor.getString(cursor.getColumnIndex(ManagerDatabase.PASSWORD));
                            if(data.equals(passData)){
                                exist = true;
                                break;
                            }
                            cursor.moveToNext();
                        }
                    }
                    cursor.close();
                    dbs.close();
                    if(!exist){
                        throw new Exception();
                    }
                }

                {
                    SQLiteDatabase dbs = dbManager.getWritableDatabase();
                    Cursor cursor = dbs.query(ManagerDatabase.TABLE_BOOK, null, null,
                            null, null, null, null);
                    boolean exist = false;
                    if(cursor.moveToFirst()) {
                        while (!cursor.isAfterLast()) {
                            String data = cursor.getString(cursor.getColumnIndex(ManagerDatabase.REGISTER));
                            if(data.equals(numData)){
                                exist = true;
                                break;
                            }
                            cursor.moveToNext();
                        }
                    }
                    cursor.close();
                    dbs.close();
                    if(!exist){
                        throw new Exception();
                    }
                }

                {
                    SQLiteDatabase dbs = dbManager.getWritableDatabase();
                    Cursor cursor = dbs.query(ManagerDatabase.TABLE_BOOK, null, null,
                            null, null, null, null);
                    boolean exist = false;
                    if(cursor.moveToFirst()) {
                        while (!cursor.isAfterLast()) {
                            String data = cursor.getString(cursor.getColumnIndex(ManagerDatabase.REGISTER));
                            if(data.equals(numData)){
                                int yearBook = cursor.getInt(cursor.getColumnIndex(ManagerDatabase.YEAR));
                                if(yearBook > Integer.valueOf(issueData.split("\\.")[2])){
                                    exist = true;
                                    break;
                                }
                            }
                            cursor.moveToNext();
                        }
                    }
                    cursor.close();
                    dbs.close();
                    if(exist){
                        throw new Exception();
                    }
                }

                database = dbManager.getWritableDatabase();
                database.setForeignKeyConstraintsEnabled(true);
                ContentValues contentValues = new ContentValues();

                contentValues.put(ManagerDatabase.ID, id);
                contentValues.put(ManagerDatabase.REGISTER_REGNUM, numData);
                contentValues.put(ManagerDatabase.REGISTER_PASSWORD, passData);
                contentValues.put(ManagerDatabase.DATA_ISSUE, issueData);
                contentValues.put(ManagerDatabase.DATA_RETURN, returnData);

                database.insert(ManagerDatabase.TABLE_REGISTER, null, contentValues);
                database.close();
            } catch (Exception e){
                Toast.makeText(RegisterActivity.this, "Ошибка: невозможно добавить запись в базу данных! В данной записи содержиться не существующие паспортные" +
                        " данные, регистрационный номер или данные содержат некорректную дату!", Toast.LENGTH_LONG).show();
                toBaseAllCells(table, true);
                if(database != null)
                    database.close();
                return;
            }
        }

        table.addView(row);
    }

    public void refactorRow(TableLayout table, String issueData, String returnData) throws Exception{
        if((table == null)
                || (issueData == null) || (returnData == null)
                || (issueData.length() == 0) || (returnData.length() == 0)
                || (!isDateValidate(issueData)) || (!isDateValidate(returnData)))
            throw new Exception("Ошибка: не корректные входные данные!");

        DateFormat format = new SimpleDateFormat("dd.mm.yyyy");
        if(format.parse(issueData).after(
                format.parse(returnData)
        )){
            throw new Exception("Ошибка: дата выдачи не может быть позже даты возврата!");
        }

        if((_changeRowIndex <= 0) || (_changeRowIndex >= _table.getChildCount()))
            throw new Exception("Ошибка: не выделена строка для изменения!");

        SQLiteDatabase database = null;
        try{
            {
                SQLiteDatabase dbs = dbManager.getWritableDatabase();
                Cursor cursor = dbs.query(ManagerDatabase.TABLE_BOOK, null, null,
                        null, null, null, null);
                boolean exist = false;
                if(cursor.moveToFirst()) {
                    while (!cursor.isAfterLast()) {
                        String data = cursor.getString(cursor.getColumnIndex(ManagerDatabase.REGISTER));
                        if(data.equals(((TextView) _table.getChildAt(_changeRowIndex).findViewById(R.id.registerCol2)).getText().toString())){
                            int yearBook = cursor.getInt(cursor.getColumnIndex(ManagerDatabase.YEAR));
                            if(yearBook > Integer.valueOf(issueData.split("\\.")[2])){
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
            cv.put(ManagerDatabase.DATA_ISSUE, issueData);
            cv.put(ManagerDatabase.DATA_RETURN, returnData);
            database.update(ManagerDatabase.TABLE_REGISTER, cv, "id = ?",
                    new String[]{
                            ((TextView) _table.getChildAt(_changeRowIndex).findViewById(R.id.registerCol1)).getText().toString()
                    });
            database.close();
        }catch(Exception e){
            Toast.makeText(RegisterActivity.this, "Ошибка: невозможно изменить существующую запись", Toast.LENGTH_LONG).show();
            toBaseAllCells(table, true);
            if(database != null)
                database.close();
            return;
        }

        ((TextView) _table.getChildAt(_changeRowIndex).findViewById(R.id.registerCol4)).setText(issueData);
        ((TextView) _table.getChildAt(_changeRowIndex).findViewById(R.id.registerCol5)).setText(returnData);
    }

    public void deleteChangeRow() throws Exception{
        if((_changeRowIndex <= 0) || (_changeRowIndex >= _table.getChildCount()))
            throw new Exception("Ошибка: не выделена строка для удаления!");

        SQLiteDatabase database = dbManager.getWritableDatabase();
        try{
            database.delete(ManagerDatabase.TABLE_REGISTER, "id = ?",
                    new String[]{
                            ((TextView) _table.getChildAt(_changeRowIndex).findViewById(R.id.registerCol1)).getText().toString()
                    });
            database.close();
        }catch (Exception e){
            Toast.makeText(RegisterActivity.this, "Ошибка: невозможно удалить зарегистрированную запись!", Toast.LENGTH_LONG).show();
            toBaseAllCells(_table, true);
            if(database != null)
                database.close();
            return;
        }

        _table.removeViewAt(_changeRowIndex);
    }
}