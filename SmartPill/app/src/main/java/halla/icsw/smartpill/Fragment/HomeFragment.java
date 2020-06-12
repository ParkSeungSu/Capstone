package halla.icsw.smartpill.Fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.dinuscxj.progressbar.CircleProgressBar;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.firebase.auth.FirebaseAuth;

import halla.icsw.smartpill.MainActivity;
import halla.icsw.smartpill.R;
import halla.icsw.smartpill.SignInActivity;


public class HomeFragment extends Fragment {
    private MainActivity mainActivity;
    private View view;
    private Button logout;
    private TextView name;
    private Button commitButton;
    private CircleProgressBar progressBar;
    private static final String DEFAULT_PATTERN = "%d%%";
    private boolean commitSign=false;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainActivity=(MainActivity) getActivity();
        view= inflater.inflate(R.layout.fragment_home,container,false);
        logout=view.findViewById(R.id.logout_button);
        name=view.findViewById(R.id.textview_main_message);
        progressBar=view.findViewById(R.id.progressBar);
        progressBar.setProgress(0);
        progressBar.setMax(100);
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
                    } else {
                        progressBar.setProgress(100);
                    }
                    commitSign=false;
                }
            }
        });
        return view;
    }
    public void setBoolean(){
        commitSign=true;
    }


}
