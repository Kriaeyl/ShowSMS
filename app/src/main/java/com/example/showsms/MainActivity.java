package com.example.showsms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    TextView tv1;
    Button b1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv1 = findViewById(R.id.textView);
        b1 = findViewById(R.id.button);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int permission = PermissionChecker.checkSelfPermission(MainActivity.this, Manifest.permission.READ_SMS);
                if (permission != PermissionChecker.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_SMS}, 0);
                    return;
                }
                Uri uri = Uri.parse("content://sms");
                String[] reqCols = new String[]{"date", "address", "body", "type"};
                ContentResolver cr = getContentResolver();
                String filter = "body LIKE ? AND body LIKE ?";
                String[] fArgs = {"%late%", "%min%"};
                Cursor cursor = cr.query(uri, reqCols, filter, fArgs, null);
                String smsBody = "";
                if (cursor.moveToFirst()) {
                    do {
                        long dateinMillis = cursor.getLong(0);
                        String date = (String) DateFormat.format("dd MM yyyy h:mm:ss aa", dateinMillis);
                        String address = cursor.getString(1);
                        String body = cursor.getString(2);
                        String type = cursor.getString(3);
                        if (type.equalsIgnoreCase("1")) {
                            type = "Inbox: ";
                        }
                        else {
                            type = "Sent: ";
                        }
                        smsBody += type + " " + address + "\nat " + date + "\n\"" + body + "\"\n\n";
                    } while (cursor.moveToNext());
                }
                tv1.setText(smsBody);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 0: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    b1.performClick();
                } else {
                    Toast.makeText(MainActivity.this, "Permission Not Granted", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}