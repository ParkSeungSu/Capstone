package halla.icsw.smartpill;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import halla.icsw.smartpill.Fragment.DashboardFragment;
import halla.icsw.smartpill.Fragment.HomeFragment;
import halla.icsw.smartpill.Fragment.NotificationFragment;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private BottomNavigationView bottomNavigationView;
    private GoogleApiClient mGoogleApiClient;
    private String mUsername;
    private BluetoothSPP bt;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDataRef;
    private ArrayList<UserAlarm> arrayList;
    AlarmManager alarm_manager;
    DashboardFragment dashboardFragment;
    HomeFragment homeFragment;
    NotificationFragment notificationFragment;

    final String TAG = MainActivity.class.getName();


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menuitem_bottombar_home:
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, homeFragment).commit();
                    return true;
                case R.id.menuitem_bottombar_dashboard:
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, dashboardFragment).commit();
                    return true;
                case R.id.menuitem_bottombar_notification:
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, notificationFragment).commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        arrayList = new ArrayList<>();

        bottomNavigationView = findViewById(R.id.bottomNavigationView_main_menu);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mDatabase = FirebaseDatabase.getInstance();
        mDataRef = mDatabase.getReference("userData");
//        bt = new BluetoothSPP(this);
//        if (!bt.isBluetoothAvailable()) {
//            Toast.makeText(this, "Bluetooth is not Available", Toast.LENGTH_SHORT).show();
//            finish();
//        }
//
//        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {//데이터 수신
//            @Override
//            public void onDataReceived(byte[] data, String message) {
//                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
//            }
//        });
//        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
//            @Override
//            public void onDeviceConnected(String name, String address) {//연결됬을때
//                Toast.makeText(MainActivity.this, "Connected to" + name + "\n" + address, Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onDeviceDisconnected() {//연결해제
//                Toast.makeText(MainActivity.this, "Connection lost", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onDeviceConnectionFailed() {//연결실패
//                Toast.makeText(MainActivity.this, "Unable to Connect", Toast.LENGTH_SHORT).show();
//            }
//        });


        homeFragment = new HomeFragment();
        dashboardFragment = new DashboardFragment();
        notificationFragment = new NotificationFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, homeFragment).commit();

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            Toast.makeText(this, "로그인이 필요합니다", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
            finish();
        } else {
            mUsername = mFirebaseUser.getDisplayName();
            Bundle bundle = new Bundle();
            bundle.putString("userName", mUsername);
            homeFragment.setArguments(bundle);
            Toast.makeText(this, mUsername + "님 환영합니다.", Toast.LENGTH_SHORT).show();
            loadUserAlarm();

        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();
        // 알람매니저 설정


    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    public void signOut() {
        mFirebaseAuth.signOut();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
        LoginManager.getInstance().logOut();

        Intent intent = new Intent(MainActivity.this, SignInActivity.class);
        startActivity(intent);
        finish();
    }

    //
//    public void connectPill() {//연결시도
//        if (bt.getServiceState() == BluetoothState.STATE_CONNECTED) {
//            bt.disconnect();
//        } else {
//            Intent intent = new Intent(getApplicationContext(), DeviceList.class);
//            startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
//        }
//
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        if (!bt.isBluetoothEnabled()) { //
//            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
//        } else {
//            if (!bt.isServiceAvailable()) {
//                bt.setupService();
//                bt.startService(BluetoothState.DEVICE_OTHER); //DEVICE_ANDROID는 안드로이드 기기 끼리
//                setup();
//            }
//        }
//
//    }
//
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        bt.stopService();
//    }
//
//    private void setup() {
//        bt.send("Text", true);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
//            if (resultCode == Activity.RESULT_OK)
//                bt.connect(data);
//        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
//            if (resultCode == Activity.RESULT_OK) {
//                bt.setupService();
//                bt.startService(BluetoothState.DEVICE_OTHER);
//                setup();
//            } else {
//                Toast.makeText(getApplicationContext()
//                        , "Bluetooth was not enabled."
//                        , Toast.LENGTH_SHORT).show();
//                finish();
//            }
//        }
//
//    }
    public void loadUserAlarm() {
        Log.d("load","유저 데이터");
        mDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //파이어베이스 데이터 베이스에 데이터를 받아오는 곳
                arrayList.clear();//기존 배열 초기화
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {//반복문으로 데이터 리스트를 추출
                    UserAlarm userAlarm = snapshot.getValue(UserAlarm.class);
                    if (userAlarm.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        arrayList.add(userAlarm);//데이터를 넣고 리사이클러 뷰로 보낼 준비

                    }
                }
                for (int i=0;i<arrayList.size();i++) {
                    Log.d("포문 진입","포문 진입");
                    UserAlarm user=arrayList.get(i);
                    String madicine = user.getMadicine();
                    String[] hourMin = user.getClock().split(":");

                    alarmSetting(user.getId()+i,madicine, hourMin[0], hourMin[1]);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //디비 오류
                Log.d("DashboardFragment", String.valueOf(databaseError.toException()));
            }
        });

    }

    public void alarmSetting(String id,String madicine, String hour, String min) {
        Log.d("알람 셋팅","알람 셋팅");

        // 앞서 설정한 값으로 보여주기
        // 없으면 디폴트 값은 현재시간
        SharedPreferences sharedPreferences = getSharedPreferences(id, MODE_PRIVATE);
        long millis = sharedPreferences.getLong(id+"nextNotifyTime", Calendar.getInstance().getTimeInMillis());

        Calendar nextNotifyTime = new GregorianCalendar();
        nextNotifyTime.setTimeInMillis(millis);

        Date nextDate = nextNotifyTime.getTime();
        String date_text = new SimpleDateFormat("yyyy년 MM월 dd일 EE요일 a hh시 mm분 ", Locale.getDefault()).format(nextDate);
        Toast.makeText(getApplicationContext(), "[처음 실행시] 다음 알람은 " + date_text + "으로 알람이 설정되었습니다!", Toast.LENGTH_SHORT).show();


        // 이전 설정값으로 TimePicker 초기화


        int intHour, hour_24, minute;
        String am_pm;
        hour_24=Integer.parseInt(hour);

        if (hour_24 > 12) {
            am_pm = "PM";
            intHour = hour_24 - 12;
        } else {
            intHour = hour_24;
            am_pm = "AM";
        }
        minute=Integer.parseInt(min);

        // 현재 지정된 시간으로 알람 시간 설정
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour_24);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        // 이미 지난 시간을 지정했다면 다음날 같은 시간으로 설정
        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1);
        }

        Date currentDateTime = calendar.getTime();
        date_text = new SimpleDateFormat("yyyy년 MM월 dd일 EE요일 a hh시 mm분 ", Locale.getDefault()).format(currentDateTime);
        Toast.makeText(getApplicationContext(), date_text + "으로 알람이 설정되었습니다!", Toast.LENGTH_SHORT).show();

        //  Preference에 설정한 값 저장
        SharedPreferences.Editor editor = getSharedPreferences(id, MODE_PRIVATE).edit();
        editor.putLong(id+"nextNotifyTime", (long) calendar.getTimeInMillis());
        editor.apply();


        diaryNotification(calendar,madicine);

    }


    void diaryNotification(Calendar calendar,String madicine) {
        Log.d("알람 기록","알람 기록!!!");
//        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
//        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
//        Boolean dailyNotify = sharedPref.getBoolean(SettingsActivity.KEY_PREF_DAILY_NOTIFICATION, true);
        Boolean dailyNotify = true; // 무조건 알람을 사용

        PackageManager pm = this.getPackageManager();
        ComponentName receiver = new ComponentName(this, DeviceBootReceiver.class);
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        alarmIntent.putExtra("madicine",madicine);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);


        // 사용자가 매일 알람을 허용했다면
        if (dailyNotify) {


            if (alarmManager != null) {

                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY, pendingIntent);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                }
            }

            // 부팅 후 실행되는 리시버 사용가능하게 설정
            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);

        }
//        else { //Disable Daily Notifications
//            if (PendingIntent.getBroadcast(this, 0, alarmIntent, 0) != null && alarmManager != null) {
//                alarmManager.cancel(pendingIntent);
//                //Toast.makeText(this,"Notifications were disabled",Toast.LENGTH_SHORT).show();
//            }
//            pm.setComponentEnabledSetting(receiver,
//                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
//                    PackageManager.DONT_KILL_APP);
//        }
    }


}