package com.example.valentinmakhlay.bloknot;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.support.annotation.StringDef;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.os.EnvironmentCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public EditText editText;
    public String filename = null;
    private String path = Environment.getExternalStorageDirectory().toString()+"/files/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        editText = (EditText) findViewById(R.id.editText);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        float fSize = Float.parseFloat(sharedPreferences.getString("Размер", "20"));
        editText.setTextSize(fSize);

        String regular = sharedPreferences.getString("Стиль", "");
        int typeface = Typeface.NORMAL;

        if(regular.contains("Полужирный"));
        typeface += Typeface.BOLD;

        if(regular.contains("Курсив"));
        typeface += Typeface.ITALIC;

        editText.setTypeface(null, typeface);

        int color = Color.BLACK;
        if(sharedPreferences.getBoolean(getString(R.string.pref_color_red), false))
            color += Color.RED;

        if(sharedPreferences.getBoolean(getString(R.string.pref_color_green), false))
            color += Color.GREEN;

        if(sharedPreferences.getBoolean(getString(R.string.pref_color_blue), false))
            color += Color.BLUE;

        editText.setTextColor(color);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.action_clear:
                editText.setText("");
                Toast.makeText(getApplicationContext(), "Очищено", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.open:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Имя файла");
                builder.setMessage("Введите имя файла для открытия");
                final  EditText input = new EditText(this);
                builder.setView(input);
                builder.setPositiveButton("Открыть", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        editText.setText("");
                        String value = input.getText().toString();

                        filename = value;
                        File file = new File(path + filename);
                        if (file.exists() && file.isFile()) {
                            editText.setText(openFile(filename));
                        } else {
                            Toast.makeText(MainActivity.this, "Файла не существует", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.show();
                return true;

            case R.id.save:
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Имя файла");
                alert.setMessage("Введите имя файла для сохранения");
                final EditText input2 = new EditText(this);
                alert.setView(input2);
                alert.setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String value = input2.getText().toString();
                        filename = value;
                        saveFile(filename, editText.getText().toString());
                    }
                });
                alert.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(MainActivity.this, "Вы нажали отмена!", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.show();
                return true;
            case R.id.settings:
                Intent i = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(i);

        }
        return super.onOptionsItemSelected(item);
    }

    private void saveFile(String filename, String body) {
        try {
            File root = new File(this.path);
            if(!root.exists()){
                root.mkdirs();
        }
        File file = new File(root, filename);
        FileWriter writer = new FileWriter(file);
            writer.append(body);
            writer.flush();
            writer.close();
            Toast.makeText(MainActivity.this, "Сохранено", Toast.LENGTH_SHORT).show();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }


private String openFile(String filename) {
    StringBuilder text = new StringBuilder();
    try {
        File file = new File(this.path, filename);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        while ((line = br.readLine())!=null){
            text.append(line + "\n");
        }
    } catch  (Exception e) {
        e.printStackTrace();
    }
    return  text.toString();
}

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
