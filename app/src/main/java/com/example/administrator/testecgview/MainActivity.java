package com.example.administrator.testecgview;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private EcgBgView ecgBgView;
    private long time;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.arg1 == 0x11){
                int index = (int)msg.obj;
                EcgDataBean dataBean = new EcgDataBean();
                if(index % 20 == 0){
                    time = System.currentTimeMillis();
                }
                dataBean.setData(Integer.valueOf(new Random().nextInt(200)));
                dataBean.setTime(0);
                ecgBgView.addData(dataBean);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ecgBgView = findViewById(R.id.ecgBgView);
        //测试需要，实际可根据数据情况
        TimeUtils.startTime(-1, 0, 100, 1, new TimeUtils.TimeListener() {
            @Override
            public void doAction(int index) {
                if(index % 5 == 0){
                   Message message = Message.obtain();
                   message.obj = index;
                   message.arg1 = 0x11;
                   handler.sendMessage(message);
                }
            }
        });
    }
}
