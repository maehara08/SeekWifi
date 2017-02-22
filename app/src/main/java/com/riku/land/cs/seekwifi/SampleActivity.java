package com.riku.land.cs.seekwifi;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class SampleActivity extends AppCompatActivity {

    private WifiRadarView radarView;
    private WifiManager manager;
    private Timer timer = new Timer();

    private static final int REQUEST_PERMISSION = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
        radarView = (WifiRadarView) findViewById(R.id.radarview);

        manager = (WifiManager) getSystemService(WIFI_SERVICE);
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,}, REQUEST_PERMISSION);
                return;
            }
        }
    }

    // 結果の受け取り
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION) {
            // 使用が許可された
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                scanWifiState();
            } else {
                // それでも拒否された時の対応
                Toast toast = Toast.makeText(this, "これ以上なにもできません", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startScan();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopScan();
    }

    private void startScan() {
        final Handler handler = new Handler();
        if (timer != null) {
            stopScan();
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        scanWifiState();
                    }
                });
            }
        }, 0, 1000);
    }

    private void stopScan() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void scanWifiState() {
        if (manager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
            // APをスキャン
            manager.startScan();
            // スキャン結果を取得
            radarView.updateScanResult(manager.getScanResults());
        }
    }
}
