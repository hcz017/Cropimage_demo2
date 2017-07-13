package com.hcz017.cropimage;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private CropImageView mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mView = (CropImageView) findViewById(R.id.cropimage);
        // 设置资源和默认长宽
        mView.setDrawable(getResources().getDrawable(R.drawable.test2), 300, 300);
        // 调用该方法得到剪裁好的图片
        // Bitmap mBitmap= mView.getCropImage();
    }
}
