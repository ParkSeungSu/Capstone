package halla.icsw.smartpill.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.dinuscxj.progressbar.CircleProgressBar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import halla.icsw.smartpill.BarChartActivity;
import halla.icsw.smartpill.DatePill;
import halla.icsw.smartpill.MainActivity;
import halla.icsw.smartpill.R;
import halla.icsw.smartpill.UserAlarm;

public class HomeFragment extends Fragment {
    private MainActivity mainActivity;
    private View view;
    private Button logout;
    private TextView name;
    private Button commitButton;
    private Button chartViewButton;
    private CircleProgressBar progressBar;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDataRef;
    private Map<String,Object> userPillDate;
    private HashMap<String, Object> childUpdates;
    private Button resetButton;
    private DatePill datePill;
    private boolean commitSign=false;
    private  String todayMan;
    private String korDayOfWeek="";
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainActivity=(MainActivity) getActivity();
        view= inflater.inflate(R.layout.fragment_home,container,false);
        logout=view.findViewById(R.id.logout_button);
        name=view.findViewById(R.id.textview_main_message);
        resetButton=view.findViewById(R.id.reset);
        progressBar=view.findViewById(R.id.progressBar);
        progressBar.setProgress(0);
        progressBar.setMax(100);
        mDatabase=FirebaseDatabase.getInstance();
        mDataRef=mDatabase.getReference();
        childUpdates=new HashMap<>();
        Calendar cal=Calendar.getInstance();
        int year=cal.get(Calendar.YEAR);
        final int month=cal.get(Calendar.MONTH)+1;
        final int date=cal.get(Calendar.DATE);
        int dayOfWeek=cal.get(Calendar.DAY_OF_WEEK);
        switch (dayOfWeek){
            case 1:
                korDayOfWeek="일";
                break;
            case 2:
                korDayOfWeek="월";
                break;
            case 3:
                korDayOfWeek="화";
                break;
            case 4:
                korDayOfWeek="수";
                break;
            case 5:
                korDayOfWeek="목";
                break;
            case 6:
                korDayOfWeek="금";
                break;
            case 7:
                korDayOfWeek="토";
                break;
        }
        todayMan=year+"-"+month+"-"+date;
        mDataRef=mDatabase.getReference(FirebaseAuth.getInstance().getCurrentUser().getUid());
        mDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){//반복문으로 데이터 리스트를 추출
                    DatePill dateP=snapshot.getValue(DatePill.class);
                    if(dateP.getDate().equals(todayMan)) {
                        datePill=dateP;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        chartViewButton=view.findViewById(R.id.chartViewer);
        commitButton=view.findViewById(R.id.commit);
        Bundle extra=this.getArguments();
        name.setText(extra.getString("userName"));
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getActivity())
                        .setMessage("Signout ?")
                        .setPositiveButton("signout", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mainActivity.signOut();
                            }
                        }).show();
            }
        });
        commitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.setup();
                if (commitSign) {
                    if ((progressBar.getProgress() + 30) < 90) {
                        progressBar.setProgress(progressBar.getProgress() + 30);
                        if(datePill!=null){
                            datePill.setNum(datePill.getNum()+1);
                        }else{
                            datePill=new DatePill(1,todayMan,korDayOfWeek);
                        }
                        userPillDate=datePill.toMap();
                        childUpdates.put(todayMan,userPillDate);
                        mDataRef.updateChildren(childUpdates);
                    } else {
                        progressBar.setProgress(100);
                    }
                    commitSign=false;
                }
            }
        });
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.resetWeight();
                Toast.makeText(mainActivity, "무게값이 초기화 되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });
        chartViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               mainActivity.getNumsOfUser();
            }
        });
        return view;
    }
    public void setBoolean(){
        commitSign=true;
    }


}
