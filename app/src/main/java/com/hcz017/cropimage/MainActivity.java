package com.hcz017.cropimage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private CropImageView mCropImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCropImageView = (CropImageView) findViewById(R.id.cropimage);
        Button mBtnImg1 = (Button) findViewById(R.id.btn_img1);
        Button mBtnImg2 = (Button) findViewById(R.id.btn_img2);
        Button mBtnCut = (Button) findViewById(R.id.btn_cut);
        mBtnImg1.setOnClickListener(this);
        mBtnImg2.setOnClickListener(this);
        mBtnCut.setOnClickListener(this);

        // 调用该方法得到剪裁好的图片，保存这个bitmap就可以保存裁剪后的图片
        // Bitmap mBitmap= mCropImageView.getCropImage();
    }

    @Override
    public void onClick(View v) {
        Bitmap bitmap;
        // 这里设置visibility为GONE 是为了触发CropImageView里面的onMeasure来重新设置控件大小
        mCropImageView.setVisibility(View.GONE);
        switch (v.getId()){
            case R.id.btn_img1:
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.test2);
                mCropImageView.setDrawable(bitmap, 200, 300);
                mCropImageView.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_img2:
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.husun);
                mCropImageView.setDrawable(bitmap, 200, 300);
                mCropImageView.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_cut:
                mCropImageView.setDrawable(mCropImageView.getCropImage(), 200, 300);
                mCropImageView.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }
}
