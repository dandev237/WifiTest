package dandev237.wifitest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Actividad principal de la aplicación
 *
 * Autor: Daniel Castro García
 * Email: dandev237@gmail.com
 * Fecha: 14/05/2016
 */

public class MainActivity extends AppCompatActivity {

    WifiManager wifiManager;
    WifiScanReceiver wifiReceiver;

    GeolocationGPS geoGPS;
    //GeolocationHTTP geoHTTP;

    String wifiList[];
    List<WifiData> wifiDataList;

    ListView wifiListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wifiListView = (ListView) findViewById(R.id.wifiListView);

        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiReceiver = new WifiScanReceiver();

        geoGPS = new GeolocationGPS(getApplicationContext(), this);
        //geoHTTP = new GeolocationHTTP(getApplicationContext());

        //El escaneo comienza al abrir la aplicación
        wifiManager.startScan();
    }

    @Override
    protected void onStart(){
        geoGPS.connect();
        super.onStart();
    }

    @Override
    protected void onResume(){
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        super.onResume();
    }

    @Override
    protected void onPause(){
        unregisterReceiver(wifiReceiver);
        super.onPause();
    }

    @Override
    protected void onStop(){
        geoGPS.disconnect();
        super.onStop();
    }

    /**
     * BroadcastReceiver personalizado para recibir las redes WiFi detectadas y poder
     * trabajar con sus datos.
     */
    private class WifiScanReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            List<ScanResult> scanResultList = wifiManager.getScanResults();
            wifiList = new String[scanResultList.size()];
            wifiDataList = new ArrayList<>();

            for (ScanResult s : scanResultList) {
                WifiData w = new WifiData(s);
                w.setLatitude(geoGPS.getLatitude());
                w.setLongitude(geoGPS.getLongitude());
                wifiDataList.add(w);
            }

            //Determinar localización mediante HTTP con lista de datos ya completa
            /*try {
                geoHTTP.postRequest(getApplicationContext(), wifiDataList);
                for(WifiData w: wifiDataList){
                    w.setLatitude(GeolocationHTTP.getLatitude());
                    w.setLongitude(GeolocationHTTP.getLongitude());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }*/

            for (int i = 0; i < scanResultList.size(); i++) {
                //wifiList[i] = (scanResultList.get(i)).toString();
                wifiList[i] = wifiDataList.get(i).toString();
            }

            ArrayAdapter<String> wifiAdapter = new ArrayAdapter<>(getApplicationContext(),
                    android.R.layout.simple_list_item_1, wifiList);
            wifiListView.setAdapter(wifiAdapter);
        }
    }

}
