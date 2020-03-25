package halla.icsw.smartpill;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.facebook.FacebookSdk;

public class MyApplication extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
    }
}
