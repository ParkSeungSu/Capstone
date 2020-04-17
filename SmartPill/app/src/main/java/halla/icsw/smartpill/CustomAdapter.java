package halla.icsw.smartpill;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {
    private ArrayList<UserAlarm> arrayList;
    private Context context;

    public CustomAdapter(ArrayList<UserAlarm> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.alarm_list,parent,false);
        CustomViewHolder holer=new CustomViewHolder(view);

        return holer;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        Log.d("asdfasdf","asdfasdfasdfasdf");
        holder.pillName.setText(arrayList.get(position).getMadicine());
        holder.pillTime.setText(arrayList.get(position).getClock());
    }

    @Override
    public int getItemCount() {
        //3항 연산자
        return (arrayList != null?arrayList.size():0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView pillName;
        TextView pillTime;
        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.pillName=itemView.findViewById(R.id.textView_pillname);
            this.pillTime=itemView.findViewById(R.id.textView_pillTime);

        }
    }
}
