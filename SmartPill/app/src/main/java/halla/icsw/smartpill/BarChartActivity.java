package halla.icsw.smartpill;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class BarChartActivity extends AppCompatActivity {
    ArrayList NoOfEmp;
    ArrayList year;
    ArrayList<Integer> NumArray;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_chart);
        Intent intent=getIntent();
        BarChart chart = findViewById(R.id.barchart);
        NoOfEmp = new ArrayList();
        NumArray = new ArrayList();
        NumArray=intent.getIntegerArrayListExtra("Num");
        year = new ArrayList();
        year=intent.getStringArrayListExtra("Date");
        for(int i=0;i<NumArray.size();i++) {

            NoOfEmp.add(new BarEntry(NumArray.get(i), i));
        }
        BarDataSet bardataset = new BarDataSet(NoOfEmp, "최근 7일간의 복용");
        chart.animateY(2000);
        BarData data = new BarData(year, bardataset); // MPAndroidChart v3.X 오류 발생
        bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
        chart.setData(data);

    }
}