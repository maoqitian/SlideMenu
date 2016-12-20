package com.mao.slidemenu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.mao.slidemenu.view.MySilemenu;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private MySilemenu silemenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        silemenu = (MySilemenu) findViewById(R.id.silemenu);
        findViewById(R.id.ib_main_back).setOnClickListener(this);
        findViewById(R.id.tv_news).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        //打开或关闭侧滑面板
        silemenu.switchState();
        switch (v.getId()){
            case R.id.tv_news:
                Toast.makeText(getApplicationContext(),"点击了新闻",Toast.LENGTH_SHORT).show();
                break;
        }

    }


}
