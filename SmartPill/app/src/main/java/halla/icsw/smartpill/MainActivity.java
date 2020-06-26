package halla.icsw.smartpill;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
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

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;
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
    private ArrayList<String> datePillDate;
    private ArrayList<Integer> datePillNum;
    static private String SHARE_NAME="SHARE_PREF";
    static SharedPreferences sharedPref = null;
    static SharedPreferences.Editor editor=null;

    String dateFormat="dd/MM/yyyy hh:mm:ss.SSS";
    SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
    Calendar alarmCalendar;
    private float savedGram;
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
        //SharedPrefrence 값 불러오기
        sharedPref=getSharedPreferences(SHARE_NAME,MODE_PRIVATE);
        editor=sharedPref.edit();
        savedGram=sharedPref.getFloat("gram",-1);
        datePillDate=new ArrayList<>();
        datePillNum=new ArrayList<>();
        arrayList = new ArrayList<>();
        homeFragment = new HomeFragment();
        dashboardFragment = new DashboardFragment();
        notificationFragment = new NotificationFragment();
        bottomNavigationView = findViewById(R.id.bottomNavigationView_main_menu);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mDatabase = FirebaseDatabase.getInstance();
        mDataRef = mDatabase.getReference("userData");
        bt = new BluetoothSPP(this);
        if (!bt.isBluetoothAvailable()) {
            Toast.makeText(this, "Bluetooth is not Available", Toast.LENGTH_SHORT).show();
            finish();
        }

        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {//데이터 수신
            @Override
            public void onDataReceived(byte[] data, String message) {
                Log.d("브루투스","부루투스");
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                float gram=Float.valueOf(message);
                if(savedGram<=-1){
                    savedGram=gram;
                    Toast.makeText(MainActivity.this, "무게 설정후 복용확인!", Toast.LENGTH_SHORT).show();
                }
                else if(gram<savedGram){
                  homeFragment.setBoolean();
                  savedGram=gram;
                  editor.putFloat("gram",savedGram);
                  editor.commit();
                    Toast.makeText(MainActivity.this, "복용 확인!", Toast.LENGTH_SHORT).show();
                }

            }
        });
        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
            @Override
            public void onDeviceConnected(String name, String address) {//연결됬을때
                Toast.makeText(MainActivity.this, "Connected to" + name + "\n" + address, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeviceDisconnected() {//연결해제
                Toast.makeText(MainActivity.this, "Connection lost", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeviceConnectionFailed() {//연결실패
                Toast.makeText(MainActivity.this, "Unable to Connect", Toast.LENGTH_SHORT).show();
            }
        });

        connectPill();
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


    public void connectPill() {//연결시도
        if (bt.getServiceState() == BluetoothState.STATE_CONNECTED) {
            bt.disconnect();
        } else {
            Intent intent = new Intent(getApplicationContext(), DeviceList.class);
            startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!bt.isBluetoothEnabled()) { //
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
        } else {
            if (!bt.isServiceAvailable()) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER); //DEVICE_ANDROID는 안드로이드 기기 끼리
                setup();
            }
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        bt.stopService();
    }

    public void setup() {
        bt.send("t", true);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK)
                bt.connect(data);
        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
                setup();
            } else {
                Toast.makeText(getApplicationContext()
                        , "Bluetooth was not enabled."
                        , Toast.LENGTH_SHORT).show();
                finish();
            }
        }

    }
    public void loadUserAlarm() {
        Log.d("load","유저 데이터");
        mDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
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
                    long times=user.getLongTime();
                    Calendar calendar= Calendar.getInstance();
                    calendar.setTimeInMillis(times);
                    setAlarm(calendar,i);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //디비 오류
                Log.d("DashboardFragment", String.valueOf(databaseError.toException()));
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    void setAlarm(Calendar calendar,int i) {
        Date date=calendar.getTime();
        alarmCalendar = Calendar.getInstance();
        alarmCalendar.setTimeInMillis(System.currentTimeMillis());
        alarmCalendar.set(Calendar.HOUR_OF_DAY, date.getHours());
        Log.d("알람", String.valueOf(date.getHours()));
        alarmCalendar.set(Calendar.MINUTE, date.getMinutes());
        Log.d("알람", String.valueOf(date.getMinutes()));
        alarmCalendar.set(Calendar.SECOND, 0);
        // TimePickerDialog 에서 설정한 시간을 알람 시간으로 설정

        if (alarmCalendar.before(Calendar.getInstance())) alarmCalendar.add(Calendar.DATE, 1);
        // 알람 시간이 현재시간보다 빠를 때 하루 뒤로 맞춤
        Intent alarmIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmIntent.setAction(AlarmReceiver.ACTION_RESTART_SERVICE);
        PendingIntent alarmCallPendingIntent
                = PendingIntent.getBroadcast
                (MainActivity.this, i, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            alarmManager.setExactAndAllowWhileIdle
                    (AlarmManager.RTC_WAKEUP, alarmCalendar.getTimeInMillis(), alarmCallPendingIntent);
        else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            alarmManager.setExact
                    (AlarmManager.RTC_WAKEUP, alarmCalendar.getTimeInMillis(), alarmCallPendingIntent);
    } // 알람 설정
    public void getNumsOfUser(){
        mDataRef = mDatabase.getReference(FirebaseAuth.getInstance().getCurrentUser().getUid());
        mDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                datePillDate.clear();//기존 배열 초기화
                datePillNum.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    DatePill datePill=snapshot.getValue(DatePill.class);
                    datePillDate.add(datePill.getDate());
                    datePillNum.add(datePill.getNum());
                    Log.d("main pill",datePill.getDate());

                }
                Intent intent=new Intent(MainActivity.this,BarChartActivity.class);
                intent.putStringArrayListExtra("Date",datePillDate);
                intent.putIntegerArrayListExtra("Num",datePillNum);

                startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    public void resetWeight(){
        savedGram=-1;
        editor.putFloat("gram",savedGram);
        editor.commit();
    }

}