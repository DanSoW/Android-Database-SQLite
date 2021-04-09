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

import java.util.ArrayList;

public class SolutionActivity extends AppCompatActivity {

    private TableLayout _table = null;
    private TableLayout _table2 = null;
    private EditText _valueMonth = null;
    private EditText _passData = null;
    private Button _btnReaderInfo = null;
    private Button _btnReadersInfo = null;
    private static ManagerDatabase dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.solution_layout);

        _table = (TableLayout) findViewById(R.id.tableSolution2);
        _table2 = (TableLayout) findViewById(R.id.tableSolution1);
        _valueMonth = (EditText) findViewById(R.id.monthValue);
        _passData = (EditText) findViewById(R.id.passDataD);
        _btnReaderInfo = (Button) findViewById(R.id.btnReaderInfo);
        _btnReadersInfo = (Button) findViewById(R.id.btnReadersInfo);
        dbManager = new ManagerDatabase(SolutionActivity.this);

        _btnReaderInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((_passData.getText() == null) || (_passData.getText().toString().length() == 0))
                    return;

                if(_table.getChildCount() != 1){
                    while(_table.getChildCount() != 1)
                        _table.removeViewAt(1);
                }

                LayoutInflater inflater = LayoutInflater.from(SolutionActivity.this);

                String pwd = _passData.getText().toString().trim();
                String fullName = "";

                {
                    SQLiteDatabase dbs = dbManager.getWritableDatabase();
                    Cursor cursor = dbs.query(ManagerDatabase.TABLE_READER, null, null,
                            null, null, null, null);

                    if(cursor.moveToFirst()) {
                        while (!cursor.isAfterLast()) {
                            String data = cursor.getString(cursor.getColumnIndex(ManagerDatabase.PASSWORD));
                            if(data.equals(pwd)){
                                fullName = cursor.getString(cursor.getColumnIndex(ManagerDatabase.FULLNAME));
                            }
                            cursor.moveToNext();
                        }
                    }
                    cursor.close();
                    dbs.close();
                }

                ArrayList<String> numbersBook = new ArrayList<>();
                ArrayList<String> dataIssue = new ArrayList<>();
                ArrayList<String> namesBook = new ArrayList<>();
                ArrayList<Integer> years = new ArrayList<>();
                ArrayList<Integer> pages = new ArrayList<>();

                {
                    SQLiteDatabase dbs = dbManager.getWritableDatabase();
                    Cursor cursor = dbs.query(ManagerDatabase.TABLE_REGISTER, null, null,
                            null, null, null, null);

                    if(cursor.moveToFirst()) {
                        while (!cursor.isAfterLast()) {
                            String data = cursor.getString(cursor.getColumnIndex(ManagerDatabase.REGISTER_PASSWORD));
                            if(data.equals(pwd)){
                                numbersBook.add(cursor.getString(cursor.getColumnIndex(ManagerDatabase.REGISTER_REGNUM)));
                                dataIssue.add(cursor.getString(cursor.getColumnIndex(ManagerDatabase.DATA_ISSUE)));
                            }
                            cursor.moveToNext();
                        }
                    }

                    cursor.close();
                    dbs.close();
                }

                for(int i = 0; i < numbersBook.size(); i++){
                    SQLiteDatabase dbs = dbManager.getWritableDatabase();
                    Cursor cursor = dbs.query(ManagerDatabase.TABLE_BOOK, null, null,
                            null, null, null, null);

                    if(cursor.moveToFirst()) {
                        while (!cursor.isAfterLast()) {
                            String data = cursor.getString(cursor.getColumnIndex(ManagerDatabase.REGISTER));
                            if(data.equals(numbersBook.get(i))){
                                namesBook.add(cursor.getString(cursor.getColumnIndex(ManagerDatabase.NAME)));
                                years.add(cursor.getInt(cursor.getColumnIndex(ManagerDatabase.YEAR)));
                                pages.add(cursor.getInt(cursor.getColumnIndex(ManagerDatabase.PAGES)));
                                break;
                            }
                            cursor.moveToNext();
                        }
                    }

                    cursor.close();
                    dbs.close();
                }

                for(int i = 0; i < numbersBook.size(); i++){
                    TableRow row = (TableRow) inflater.inflate(R.layout.solution2_template, null);
                    ((TextView) row.findViewById(R.id.solution2Col1)).setText(fullName);
                    ((TextView) row.findViewById(R.id.solution2Col2)).setText(namesBook.get(i));
                    ((TextView) row.findViewById(R.id.solution2Col3)).setText(dataIssue.get(i));
                    ((TextView) row.findViewById(R.id.solution2Col4)).setText(pages.get(i).toString());
                    ((TextView) row.findViewById(R.id.solution2Col5)).setText(years.get(i).toString());

                    _table.addView(row);
                }
            }
        });

        _btnReadersInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((_valueMonth.getText() == null) || (_valueMonth.getText().toString().length() == 0))
                    return;

                if(_table2.getChildCount() != 1){
                    while(_table2.getChildCount() != 1)
                        _table2.removeViewAt(1);
                }

                LayoutInflater inflater = LayoutInflater.from(SolutionActivity.this);

                int month = Integer.valueOf(_valueMonth.getText().toString().trim());

                ArrayList<String> fullnames = new ArrayList<>();
                ArrayList<String> pwds = new ArrayList<>();
                ArrayList<String> numbersBook = new ArrayList<>();
                ArrayList<String> datasIssue = new ArrayList<>();
                ArrayList<String> datasReturn = new ArrayList<>();

                {
                    SQLiteDatabase dbs = dbManager.getWritableDatabase();
                    Cursor cursor = dbs.query(ManagerDatabase.TABLE_REGISTER, null, null,
                            null, null, null, null);

                    if(cursor.moveToFirst()) {
                        while (!cursor.isAfterLast()) {
                            String issue = cursor.getString(cursor.getColumnIndex(ManagerDatabase.DATA_ISSUE)),
                                    ret = cursor.getString(cursor.getColumnIndex(ManagerDatabase.DATA_RETURN));
                            int yearI = Integer.valueOf(issue.split("\\.")[2]),
                                    yearR = Integer.valueOf(ret.split("\\.")[2]),
                                    monthI = Integer.valueOf(issue.split("\\.")[1]),
                                    monthR = Integer.valueOf(ret.split("\\.")[1]);

                            if(((monthR - monthI) + 12 * (yearR - yearI)) > month){
                                datasIssue.add(cursor.getString(cursor.getColumnIndex(ManagerDatabase.DATA_ISSUE)));
                                datasReturn.add(cursor.getString(cursor.getColumnIndex(ManagerDatabase.DATA_RETURN)));
                                numbersBook.add(cursor.getString(cursor.getColumnIndex(ManagerDatabase.REGISTER_REGNUM)));
                                pwds.add(cursor.getString(cursor.getColumnIndex(ManagerDatabase.REGISTER_PASSWORD)));
                            }
                            cursor.moveToNext();
                        }
                    }
                    cursor.close();
                    dbs.close();
                }

                for(int i = 0; i < pwds.size(); i++){
                    SQLiteDatabase dbs = dbManager.getWritableDatabase();
                    Cursor cursor = dbs.query(ManagerDatabase.TABLE_READER, null, null,
                            null, null, null, null);

                    if(cursor.moveToFirst()) {
                        while (!cursor.isAfterLast()) {
                            String data = cursor.getString(cursor.getColumnIndex(ManagerDatabase.PASSWORD));
                            if(data.equals(pwds.get(i))){
                                fullnames.add(cursor.getString(cursor.getColumnIndex(ManagerDatabase.FULLNAME)));
                            }
                            cursor.moveToNext();
                        }
                    }
                    cursor.close();
                    dbs.close();
                }

                for(int i = 0; i < pwds.size(); i++){
                    TableRow row = (TableRow) inflater.inflate(R.layout.solution1_template, null);
                    ((TextView) row.findViewById(R.id.solution1Col1)).setText(fullnames.get(i));
                    ((TextView) row.findViewById(R.id.solution1Col2)).setText(pwds.get(i));
                    ((TextView) row.findViewById(R.id.solution1Col3)).setText(numbersBook.get(i));
                    ((TextView) row.findViewById(R.id.solution1Col4)).setText(datasIssue.get(i));
                    ((TextView) row.findViewById(R.id.solution1Col5)).setText(datasReturn.get(i));
                    try {
                        ((TextView) row.findViewById(R.id.solution1Col6)).setText(String.valueOf(CountBooksByPwd(pwds.get(i))));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    _table2.addView(row);
                }
            }
        });
    }

    private int CountBooksByPwd(String pass) throws Exception {
        if((pass == null) || (pass.length() != ReaderActivity.STD_SIZE_PASS))
            throw new Exception("Ошибка: введены некорректные данные!");

        SQLiteDatabase dbs = dbManager.getWritableDatabase();
        Cursor cursor = dbs.query(ManagerDatabase.TABLE_REGISTER, null, null,
                null, null, null, null);

        int count = 0;
        if(cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String data = cursor.getString(cursor.getColumnIndex(ManagerDatabase.REGISTER_PASSWORD));
                if(data.equals(pass)){
                    count++;
                }
                cursor.moveToNext();
            }
        }
        cursor.close();
        dbs.close();

        return count;
    }
}
