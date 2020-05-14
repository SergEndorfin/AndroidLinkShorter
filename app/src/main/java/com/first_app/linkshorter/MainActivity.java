package com.first_app.linkshorter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
//import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    DataBase dbHelper;

    private EditText longLink;
    private EditText shortLink;
    private Button button;
    private ListView linksList;
    private ArrayAdapter<String> shortLinkRowAdapter;
//    private SharedPreferences sPref;
//    public static int currentDbVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        currentDbVersion = loadDbVersion();
        dbHelper = new DataBase(this);
        longLink = findViewById(R.id.long_link_field);
        shortLink = findViewById(R.id.short_link_field);
        linksList = findViewById(R.id.links_list_view);
        button = findViewById(R.id.button);

        loadAllLinks();
    }

//    @Override
//    protected void onDestroy() {
//        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
////        saveDbVersion();
//        dbHelper.deleteDataBase();
//        super.onDestroy();
//    }

    private void loadAllLinks() {
        ArrayList<String> linksListFromDB = dbHelper.getAllShortLinksFromDB();
        if (shortLinkRowAdapter == null) {
            shortLinkRowAdapter = new ArrayAdapter<String>(this, R.layout.short_link_row, R.id.short_link, linksListFromDB);
            linksList.setAdapter(shortLinkRowAdapter);
        }
        else {
            shortLinkRowAdapter.clear();
            shortLinkRowAdapter.addAll(linksListFromDB);
            shortLinkRowAdapter.notifyDataSetChanged();
        }
    }

    public void addSortLinkOnBtnClick(View view) {
        String long_Link = longLink.getText().toString();
        String short_link = shortLink.getText().toString();
        if (long_Link.matches("")) {
            this.button.setText("Неверная ссылка");
        }
        else if (short_link.matches("")) {
            button.setText("Сокращенная ссылка неверная");
        }
        else if (dbHelper.getDbShortLink(short_link)) {
            button.setText("Укажите другое сокращение");
        }
        else {
            dbHelper.insertData(long_Link, short_link);
            button.setText("Сократить");
            loadAllLinks();
            Toast toast = Toast.makeText(MainActivity.this, "Готово", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    public void onClickLayout(View view) {
        View parent = (View) view.getParent();
        TextView txtShortLink = parent.findViewById(R.id.short_link);
        String shortLink = String.valueOf(txtShortLink.getText());
        System.out.println(shortLink);
        String longUrl = dbHelper.getLongLinkByShort(shortLink);

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + longUrl));
        startActivity(intent);
        System.out.println(longUrl);
    }

//    private void saveDbVersion() {
//        sPref = getPreferences(MODE_PRIVATE);
//        SharedPreferences.Editor ed = sPref.edit();
//        ed.putInt("db_version", ++currentDbVersion);
//        System.out.println(" ================= saveDbVersion == " + currentDbVersion);
//
//        ed.commit();
//    }
//
//    private int loadDbVersion() {
//        sPref = getPreferences(MODE_PRIVATE);
//        int lastDbVer = sPref.getInt("db_version", 0);
//        System.out.println(" ############### loadDbVersion == " + lastDbVer);
//        return lastDbVer;
//    }


}
