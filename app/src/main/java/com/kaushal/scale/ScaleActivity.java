package com.kaushal.scale;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class ScaleActivity extends Activity {

    private TextView txtValue;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scale_main);
        final ScaleView rulerViewMm = (ScaleView) findViewById(R.id.scale);
        txtValue = (TextView) findViewById(R.id.length_text);
        rulerViewMm.setStartingPoint(0);
        rulerViewMm.setUpdateListener(new onViewUpdateListener() {
            @Override
            public void onViewUpdate(float result) {
                float value = (float) Math.round(result * 10f) / 10f;
                txtValue.setText(value + " cm");
            }
        });
    }
}
