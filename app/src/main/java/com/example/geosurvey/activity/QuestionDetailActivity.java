package com.example.geosurvey.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.geosurvey.R;
import com.example.geosurvey.model.Question;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class QuestionDetailActivity extends AppCompatActivity {
    private Question question;
    private TableLayout detailAnswers;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_detail);
        Intent intent = getIntent();
        question = intent.getParcelableExtra("QUESTION");
        TextView detailTitle = findViewById(R.id.detail_title);
        TextView detailContent = findViewById(R.id.detail_content);
        TextView detailCreatedAt = findViewById(R.id.detail_created_at);
        detailAnswers = findViewById(R.id.detail_table_answers);
        detailTitle.setText(question.getTitle());
        detailContent.setText(question.getContent());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getString(R.string.date_format), Locale.ENGLISH);
        detailCreatedAt.setText(simpleDateFormat.format(question.getCreatedAt()));

        question.getAnswers().forEach(answer -> {
            TableRow tr = new TableRow(getApplicationContext());
            tr.setPadding(0, 0, 0, 5);
            TextView text = new TextView(getApplicationContext());
            text.setPadding(5, 5, 5, 5);
            TextView count = new TextView(getApplicationContext());
            count.setPadding(5, 5, 5, 5);
            text.setText(answer.getText());
            count.setText(String.format(Locale.ENGLISH, "%d", answer.getCount()));
            tr.addView(text);
            tr.addView(count);
            detailAnswers.addView(tr);
        });

        setUpChart();
    }

    private void setUpChart() {
        List<PieEntry> pieEntries = new ArrayList<>();
        question.getAnswers().forEach(answer -> pieEntries.add(new PieEntry(answer.getCount(), answer.getText())));
        PieDataSet pieDataSet = new PieDataSet(pieEntries, getString(R.string.votes));
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        PieData pieData = new PieData(pieDataSet);
        pieData.setValueTextColor(Color.WHITE);
        pieDataSet.setSliceSpace(3f);
        pieDataSet.setLabel("");
        PieChart pieChart = findViewById(R.id.pie_chart);
        pieData.setValueFormatter(new PercentFormatter());
        pieData.setValueTextSize(15f);
        pieChart.setData(pieData);
        pieChart.setUsePercentValues(true);
        pieChart.setDrawEntryLabels(false);
        pieChart.setCenterTextSize(20);
        pieChart.getDescription().setEnabled(false);
        pieChart.animateY(1000, Easing.EaseInOutCubic);
        pieChart.getLegend().setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        pieChart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        pieChart.getLegend().setOrientation(Legend.LegendOrientation.VERTICAL);
        pieChart.invalidate();
    }
}
