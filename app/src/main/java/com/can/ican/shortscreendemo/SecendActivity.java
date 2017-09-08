package com.can.ican.shortscreendemo;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by chenzaidong on 2017/9/8.
 */

public class SecendActivity extends AppCompatActivity {
    private static final String TAG = "SecendActivity";
    private ImageView imageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secend);
        byte [] bitmapByte = getIntent().getByteArrayExtra("bitmap");
        Log.i(TAG, "onCreate: bitmap="+bitmapByte.length);
        imageView = (ImageView) findViewById(R.id.iv);
        imageView.setImageBitmap(BitmapFactory.decodeByteArray(bitmapByte,0,bitmapByte.length));
    }
}
