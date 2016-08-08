package com.kaushal.scale;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.widget.TextView;
import android.app.AlertDialog;

public class ScaleActivity extends Activity {

    float mmHeight;
    int height;
    private TextView txtValue;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scale_main);
        final ScaleView rulerViewMm = (ScaleView) findViewById(R.id.scale);
        txtValue = (TextView) findViewById(R.id.length_text);
        getDimensions();
        rulerViewMm.setStartingPoint(0, height, mmHeight);
        rulerViewMm.setUpdateListener(new onViewUpdateListener() {
            @Override
            public void onViewUpdate(float result) {
                float value = (float) Math.round(result * 10f) / 10f;
                txtValue.setText(value + " cm");
            }
        });
    }

    private void getDimensions() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        height = metrics.heightPixels;
        mmHeight = height / metrics.xdpi * 25.4f;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitByBackKey();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void exitByBackKey() {
        AlertDialog alertbox = new AlertDialog.Builder(this)
            .setMessage("Do you want to exit scale application?")
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                }
            })
            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                }
            }).show();

    }
}
