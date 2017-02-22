package com.riku.land.cs.seekwifi;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private WifiManager manager;

    private static final int REQUEST_PERMISSION = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.list_view);
        manager = (WifiManager) getSystemService(WIFI_SERVICE);
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,}, REQUEST_PERMISSION);
                return;
            }
        }
        scanWifiState();
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

    private void scanWifiState() {
        if (manager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
            // APをスキャン
            manager.startScan();
            // スキャン結果を取得
            List<ScanResult> apList = manager.getScanResults();
            String[] aps = new String[apList.size()];
            for (int i = 0; i < apList.size(); i++) {
                aps[i] = "SSID:" + apList.get(i).SSID + "\n"
                        + apList.get(i).frequency + "MHz " + apList.get(i).level + "dBm";
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, aps);
//            setListAdapter(adapter);
            listView.setAdapter(adapter);
        }
    }

}
