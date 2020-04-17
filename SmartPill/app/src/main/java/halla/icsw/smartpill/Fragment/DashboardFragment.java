package halla.icsw.smartpill.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import halla.icsw.smartpill.CustomAdapter;
import halla.icsw.smartpill.MainActivity;
import halla.icsw.smartpill.R;
import halla.icsw.smartpill.UserAlarm;


public class DashboardFragment extends Fragment {
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private ViewGroup view;
    private RecyclerView recyclerView;
    public CustomAdapter adapter;
    private MainActivity mainActivity;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<UserAlarm> arrayList;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = (ViewGroup) inflater.inflate(R.layout.fragment_dashboard, container, false);
        recyclerView=(RecyclerView) view.findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);

        database= FirebaseDatabase.getInstance();//firebase database 연동
        arrayList = new ArrayList<>();
        mainActivity=(MainActivity) getActivity();
        layoutManager=new LinearLayoutManager(mainActivity);
        recyclerView.setLayoutManager(layoutManager);
        adapter=new CustomAdapter(arrayList,mainActivity);
        recyclerView.setAdapter(adapter);//리사이클러뷰에 어댑터 연결
        databaseReference=database.getReference("userData");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //파이어베이스 데이터 베이스에 데이터를 받아오는 곳
               arrayList.clear();//기존 배열 초기화
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){//반복문으로 데이터 리스트를 추출
                    UserAlarm userAlarm=snapshot.getValue(UserAlarm.class);
                    if(userAlarm.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        arrayList.add(userAlarm);//데이터를 넣고 리사이클러 뷰로 보낼 준비
                        adapter.notifyDataSetChanged();//리스트 저장및 새로 고침
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //디비 오류
                Log.d("DashboardFragment", String.valueOf(databaseError.toException()));
            }
        });
        // Inflate the layout for this fragment



        return view;
    }

}
