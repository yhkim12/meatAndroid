package mobile.meatproject.com.meatproject;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private TextView phon_num;
    private TextView point;
    private EditText ipNum;
    private Button resetBtn;
    private TelephonyManager telephony = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        ipNum = (EditText) findViewById(R.id.ipNum);
        point = (TextView) findViewById(R.id.point);
        phon_num = (TextView) findViewById(R.id.main1);
        resetBtn = (Button) findViewById(R.id.resetBtn);

        telephony = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        phon_num.setText(PhoneNumberUtils.formatNumber(telephony.getLine1Number()));

        //초기 포인트 조회 테스트 주석처리
        //memberPoint();

        //새로고침 이벤트
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Thread로 웹서버에 접속
                new Thread() {
                    public void run() {
                        memberPoint();
                    }
                }.start();
            }
        });

    }

    private void memberPoint() {
        String result = null;
        URL url =null;
        HttpURLConnection http = null;
        InputStreamReader isr = null;
        BufferedReader br = null;

        try{
            url = new URL("http://"+ipNum.getText().toString()+"/member/memberPoint?pNum="+PhoneNumberUtils.formatNumber(telephony.getLine1Number()));
//            url = new URL("http://192.168.0.208:8080/member/memberPoint?pNum="+PhoneNumberUtils.formatNumber(telephony.getLine1Number()));
            http = (HttpURLConnection) url.openConnection();
            http.setConnectTimeout(3*1000);
            http.setReadTimeout(3*1000);

            isr = new InputStreamReader(http.getInputStream());
            br = new BufferedReader(isr);

            String str = null;
            while ((str = br.readLine()) != null) {
                JSONObject tem = new JSONObject(str);
                result = (String)tem.get("result");
                point.setText(result);
            }

        }catch(Exception e){
            Log.e("Exception", e.toString());
        }finally{
            if(http != null){
                try{http.disconnect();}catch(Exception e){}
            }

            if(isr != null){
                try{isr.close();}catch(Exception e){}
            }

            if(br != null){
                try{br.close();}catch(Exception e){}
            }
        }
    }
}
