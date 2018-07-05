package com.example.szx.butterknife_like;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.szx.inject.ViewInject;
import com.example.szx.viewinject_annotation.BindView;
import com.example.szx.viewinject_annotation.OnClick;

public class MainActivity extends Activity {

    @BindView(R.id.id_text1)
    TextView text1;
    @BindView(R.id.id_text2)
    TextView text2;

    @OnClick(R.id.id_text1)
    public void handleClick() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewInject.inject(this);

        text1.setText("hello");
        text2.setText("world");

    }
}
