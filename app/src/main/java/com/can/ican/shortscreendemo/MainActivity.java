package com.can.ican.shortscreendemo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_MEDIA_PROJECTION = 0x2893;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private void requestScreenShot() {
        //请求权限
        startActivityForResult(
                ((MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE)).createScreenCaptureIntent(),
                REQUEST_MEDIA_PROJECTION);
    }

    /**
     * 请求权限回调
     * @param requestCode
     * @param resultCode
     * @param data
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_MEDIA_PROJECTION: {
                if (resultCode == -1 && data != null) {
                    Shotter shotter = new Shotter(MainActivity.this, data);
                    shotter.startScreenShot(new Shotter.OnShotListener() {
                        @Override
                        public void onFinish(Bitmap bitmap) {
                            Log.i(TAG, "截屏图片大小=" + bitmap.getByteCount());
                            Intent intent = new Intent(MainActivity.this, SecendActivity.class);
                            Bitmap bitmap1 = doBlur(bitmap, 10, 0.1f);
                            Log.i(TAG, "模糊后图片大小=" + bitmap1.getByteCount());
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap1.compress(Bitmap.CompressFormat.PNG, 100, baos);
                            byte[] bitmapByte = baos.toByteArray();
                            intent.putExtra("bitmap", bitmapByte);
                            startActivity(intent);
                        }
                    });
                }
            }
        }
    }

    /**
     * 高斯模糊处理
     *
     * @param fromBitmap 原始图片
     * @param radius     0 < radius <= 25
     * @param scale 缩放大小
     */
    private Bitmap doBlur(Bitmap fromBitmap, int radius, float scale) {
        int width = fromBitmap.getWidth();
        int height = fromBitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        Bitmap newbm = Bitmap.createBitmap(fromBitmap, 0, 0, width, height, matrix, true);
        RenderScript rs = RenderScript.create(this);
        Allocation input = Allocation.createFromBitmap(rs, newbm, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
        Allocation output = Allocation.createTyped(rs, input.getType());
        ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        script.setRadius(radius);
        script.setInput(input);
        script.forEach(output);
        output.copyTo(newbm);
        return newbm;
    }

    public void startShot(View view) {
        requestScreenShot();
    }
}
