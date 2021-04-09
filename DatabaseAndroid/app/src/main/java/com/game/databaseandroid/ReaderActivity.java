package com.game.databaseandroid;

import androidx.appcompat.app.AppCompatActivity;

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

public class ReaderActivity extends AppCompatActivity {

    private TableLayout _table = null;
    private Button _btnAdd = null;
    private EditText _passData = null;
    private EditText _homeAddress = null;
    private EditText _fullName = null;
    private Button _btnRefactor = null;
    private Button _btnDelete = null;
    private static ManagerDatabase dbManager;

    public static final int STD_SIZE_PASS = 10;

    private int _changeRowIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reader_layout);

        _table = (TableLayout) findViewById(R.id.tableReader);
        _btnAdd = (Button) findViewById(R.id.btnAdd);
        _passData = (EditText) findViewById(R.id.pass_data);
        _homeAddress = (EditText) findViewById(R.id.home_address);
        _fullName = (EditText) findViewById(R.id.full_name);
        _btnRefactor = (Button) findViewById(R.id.btnRefactor);
        _btnDelete = (Button) findViewById(R.id.btnDelete);

        _btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    toBaseAllCells(_table, true);
                    addRow(_table, _passData.getText().toString().trim(), _homeAddress.getText().toString().trim(),
                            _fullName.getText().toString().trim(), true);
                }catch (Exception e){
                    Toast.makeText(ReaderActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        _btnRefactor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    refactorRow(_table, _homeAddress.getText().toString().trim(),
                            _fullName.getText().toString().trim());
                    toBaseAllCells(_table, true);
                }catch (Exception e){
                    Toast.makeText(ReaderActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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
                    Toast.makeText(ReaderActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        dbManager = new ManagerDatabase(ReaderActivity.this);
        try {
            ReadAllData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_reader, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.goToBook :
                Intent intent1 = new Intent(ReaderActivity.this, BookActivity.class);
                startActivity(intent1);
                return true;
            case R.id.goToReader:
                Intent intent2 = new Intent(ReaderActivity.this, RegisterActivity.class);
                startActivity(intent2);
                return true;
            case R.id.goToSolution:
                Intent intent3 = new Intent(ReaderActivity.this, SolutionActivity.class);
                startActivity(intent3);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void ReadAllData() throws Exception {
        SQLiteDatabase database = dbManager.getWritableDatabase();
        Cursor cursor = database.query(ManagerDatabase.TABLE_READER, null, null,
                null, null, null, null);
        while(cursor.moveToNext()){
            int passIndex= cursor.getColumnIndex(ManagerDatabase.PASSWORD),
                    homeIndex = cursor.getColumnIndex(ManagerDatabase.HOME),
                    fullIndex = cursor.getColumnIndex(ManagerDatabase.FULLNAME);
            addRow(_table, cursor.getString(passIndex).trim(),
                    cursor.getString(homeIndex).trim(),
                    cursor.getString(fullIndex).trim(), false);
        }
        cursor.close();
        database.close();
    }

    public void toBaseAllCells(TableLayout table, boolean flag){
        if((table == null) || (_changeRowIndex <= 0) || (_changeRowIndex >= table.getChildCount())){
            _changeRowIndex = 0;
            return;
        }

        ((TextView)table.getChildAt(_changeRowIndex).findViewById(R.id.readerCol1)).setBackgroundColor(Color.WHITE);
        ((TextView)table.getChildAt(_changeRowIndex).findViewById(R.id.readerCol2)).setBackgroundColor(Color.WHITE);
        ((TextView)table.getChildAt(_changeRowIndex).findViewById(R.id.readerCol3)).setBackgroundColor(Color.WHITE);

        if(flag)
            _changeRowIndex = 0;
    }

    public static boolean passwordValidate(String passData){
        if((passData == null) || (passData.length() != STD_SIZE_PASS))
            return false;
        for(int i = 0; i < passData.length(); i++)
            if(!Character.isDigit(passData.charAt(i)))
                return false;
        return true;
    }

    public boolean passwordExists(TableLayout table, String passData){
        if((!passwordValidate(passData)) || (table == null))
            return false;
        for(int i = 1; i < table.getChildCount(); i++){
            if(((TextView) (table.getChildAt(i)).findViewById(R.id.readerCol1)).getText().toString().equals(passData))
                return true;
        }

        return false;
    }

    public void addRow(TableLayout table, String passData, String homeAddress, String fullName, boolean flag) throws Exception{
        if((table == null) || (homeAddress == null)
        || (fullName == null) || (homeAddress.length() == 0)
        || (fullName.length() == 0)
        || (!passwordValidate(passData)))
            throw new Exception("Ошибка: не корректные входные данные!");
        if(passwordExists(table, passData))
            throw new Exception("Ошибка: данные паспортные данные уже есть в базе данных!");

        LayoutInflater inflater = LayoutInflater.from(ReaderActivity.this);
        TableRow row = (TableRow) inflater.inflate(R.layout.reader_template, null);
        ((TextView) row.findViewById(R.id.readerCol1)).setText(passData);
        ((TextView) row.findViewById(R.id.readerCol2)).setText(homeAddress);
        ((TextView) row.findViewById(R.id.readerCol3)).setText(fullName);
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toBaseAllCells(table, false);
                for(int i = 1; i < table.getChildCount(); i++){
                    if(
                            (table.getChildAt(i) != null) &&
                            (table.getChildAt(i).findViewById(R.id.readerCol1) != null) &&
                            ((TextView)table.getChildAt(i).findViewById(R.id.readerCol1)).getText().toString()
                            .equals(((TextView) row.findViewById(R.id.readerCol1)).getText().toString())){
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

                ((TextView)table.getChildAt(_changeRowIndex).findViewById(R.id.readerCol1)).setBackgroundColor(Color.YELLOW);
                ((TextView)table.getChildAt(_changeRowIndex).findViewById(R.id.readerCol2)).setBackgroundColor(Color.YELLOW);
                ((TextView)table.getChildAt(_changeRowIndex).findViewById(R.id.readerCol3)).setBackgroundColor(Color.YELLOW);

                _passData.setText(((TextView)table.getChildAt(_changeRowIndex).findViewById(R.id.readerCol1)).getText().toString());
                _homeAddress.setText(((TextView)table.getChildAt(_changeRowIndex).findViewById(R.id.readerCol2)).getText().toString());
                _fullName.setText(((TextView)table.getChildAt(_changeRowIndex).findViewById(R.id.readerCol3)).getText().toString());
            }
        });

        table.addView(row);

        if(flag){
            SQLiteDatabase database = dbManager.getWritableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.put(ManagerDatabase.PASSWORD, passData);
            contentValues.put(ManagerDatabase.HOME, homeAddress);
            contentValues.put(ManagerDatabase.FULLNAME, fullName);
            database.insert(ManagerDatabase.TABLE_READER, null, contentValues);
            database.close();
        }
    }

    public void refactorRow(TableLayout table, String homeAddress, String fullName) throws Exception{
        if((table == null) || (homeAddress == null)
                || (fullName == null) || (homeAddress.length() == 0)
                || (fullName.length() == 0))
            throw new Exception("Ошибка: не корректные входные данные!");
        if((_changeRowIndex <= 0) || (_changeRowIndex >= _table.getChildCount()))
            throw new Exception("Ошибка: не выделена строка для изменения!");

        SQLiteDatabase database = dbManager.getWritableDatabase();
        try{
            ContentValues cv = new ContentValues();
            cv.put(ManagerDatabase.HOME, homeAddress);
            cv.put(ManagerDatabase.FULLNAME, fullName);
            database.update(ManagerDatabase.TABLE_READER, cv, "Number_Password = ?",
                    new String[]{
                            ((TextView) _table.getChildAt(_changeRowIndex).findViewById(R.id.readerCol1)).getText().toString()
                    });
            database.close();
        }catch(Exception e){
            Toast.makeText(ReaderActivity.this, "Ошибка: невозможно изменить существующую запись", Toast.LENGTH_LONG).show();
            toBaseAllCells(table, true);
            if(database != null)
                database.close();
            return;
        }

        ((TextView) _table.getChildAt(_changeRowIndex).findViewById(R.id.readerCol2)).setText(homeAddress);
        ((TextView) _table.getChildAt(_changeRowIndex).findViewById(R.id.readerCol3)).setText(fullName);
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
                        String data = cursor.getString(cursor.getColumnIndex(ManagerDatabase.REGISTER_PASSWORD));
                        if(data.equals(((TextView) _table.getChildAt(_changeRowIndex).findViewById(R.id.readerCol1)).getText().toString())){
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
            database.delete(ManagerDatabase.TABLE_READER, "Number_Password = ?",
                    new String[]{
                            ((TextView) _table.getChildAt(_changeRowIndex).findViewById(R.id.readerCol1)).getText().toString()
                    });
            database.close();
        }catch (Exception e){
            Toast.makeText(ReaderActivity.this, "Ошибка: невозможно удалить зарегистрированную запись!", Toast.LENGTH_LONG).show();
            toBaseAllCells(_table, true);
            if(database != null)
                database.close();
            return;
        }

        _table.removeViewAt(_changeRowIndex);
    }
}