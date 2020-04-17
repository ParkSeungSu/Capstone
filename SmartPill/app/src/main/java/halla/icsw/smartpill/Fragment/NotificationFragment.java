package halla.icsw.smartpill.Fragment;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import halla.icsw.smartpill.R;
import halla.icsw.smartpill.UserAlarm;

public class NotificationFragment extends Fragment {
    private View view;
    private Button addPillButton;
    private EditText pillName;
    private TimePicker pillTime;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDataRef;
    private Map<String,Object> userData;
    private HashMap<String, Object> childUpdates;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_notification, container, false);
        addPillButton=view.findViewById(R.id.addPillButton);
        pillName=view.findViewById(R.id.pillName);
        pillTime=view.findViewById(R.id.pillTime);
        mDatabase=FirebaseDatabase.getInstance();
        mDataRef=mDatabase.getReference();
        childUpdates=new HashMap<>();

        addPillButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                String pName=pillName.getText().toString();
                if(pName.length()>0){
                    int hour= pillTime.getHour();
                    int min=pillTime.getMinute();
                    String user= FirebaseAuth.getInstance().getCurrentUser().getUid();
                    UserAlarm userAlarm=new UserAlarm(user,pName,hour+":"+min);
                    userData=userAlarm.toMap();
                    childUpdates.put("/userData/"+user+pName,userData);
                    mDataRef.updateChildren(childUpdates);
                    Toast.makeText(getActivity(), hour+":"+min+"  "+user, Toast.LENGTH_SHORT).show();
                    pillName.setText("");

                }else{
                    Toast.makeText(getActivity(), "복용 약의 이름과 시간을 입력해주세요!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
}
