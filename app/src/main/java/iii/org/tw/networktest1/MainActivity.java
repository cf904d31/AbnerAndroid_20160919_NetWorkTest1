package iii.org.tw.networktest1;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.Enumeration;

public class MainActivity extends AppCompatActivity {
    private ConnectivityManager mgr;
    private String data;
    private TextView mesg;
    private StringBuffer sb1;
    private UIHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mesg = (TextView) findViewById(R.id.mesg);
        handler = new UIHandler();


        mgr = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = mgr.getActiveNetworkInfo();
        if (info != null && info.isConnected()){
            try {
                Enumeration<NetworkInterface> ifs = NetworkInterface.getNetworkInterfaces();
                while (ifs.hasMoreElements()){
                    NetworkInterface ip = ifs.nextElement();
                    Enumeration<InetAddress> ips = ip.getInetAddresses();
                    while (ips.hasMoreElements()){
                        InetAddress ia = ips.nextElement();
                        Log.d("Abner", ia.getHostAddress());
                    }
                }


            } catch (SocketException e) {
                e.printStackTrace();
            }
        }else{
            Log.d("Abner", "NOT Connect");
        }
    }


    public void test1(View v) {
        mesg.setText("");
        new Thread() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://data.coa.gov.tw/Service/OpenData/EzgoTravelFoodStay.aspx");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.connect();
//                    InputStream in = conn.getInputStream();
//                    int c; StringBuffer sb = new StringBuffer();
//                    while ( (c = in.read()) != -1) {
//                        sb.append((char)c);
//                    }
//                    in.close();
//                    Log.d("Abner",sb.toString());
                    BufferedReader buf =
                            new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    data = buf.readLine();
                    buf.close();
                    //Log.d("Abner",data);
                    parseJSON();

                } catch (Exception e) {
                    //Log.d("Abner",e.toString());
                }
            }
        }.start();

    }

    private void parseJSON () {
        sb1 = new StringBuffer();
        try {
            JSONArray jsonArray = new JSONArray(data);
            //Log.d("Abner",""+jsonArray.length());
            for ( int i=0 ; i<jsonArray.length() ; i++) {
                JSONObject row = jsonArray.getJSONObject(i);
                String name = row.getString("Name");
                String addr = row.getString("Address");
                Log.d("Abner", name + " -> " + addr);
                sb1.append(name + " -> " + addr + "\n");
            }
            handler.sendEmptyMessage(0);
        } catch (Exception e) {
            Log.d("Abner",e.toString());
        }
    }

    private class UIHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mesg.setText(sb1);
        }
    }


}
