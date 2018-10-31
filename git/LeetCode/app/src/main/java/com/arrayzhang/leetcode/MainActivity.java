package com.arrayzhang.leetcode;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.arrayzhang.leetcode.simple.Test1;
import com.arrayzhang.leetcode.simple.Test2;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "leetcode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void start(View view){
        Test2 test1 = new Test2();
        Log.e("zr_log","leetCode:"+test1.reverseString("ds2wdae2sd2qew"));
    }
}
