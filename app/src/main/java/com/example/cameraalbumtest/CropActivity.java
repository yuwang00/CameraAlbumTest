package com.example.cameraalbumtest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import static com.example.cameraalbumtest.MainActivity.HEADPORTRAITNAME;


/**
 * Created by mathewchen on 2017/8/8.
 */

public class CropActivity extends AppCompatActivity {
    public static final float STROKE_WIDTH = 5.0f;
    public static final float MARGIN_WIDTH = 20.0f;
    private ZoomImageView zoomImageView;
    private MaskView maskView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.activity_crop);

        zoomImageView = (ZoomImageView) findViewById(R.id.zoomImageView);
        zoomImageView.setMarginWidth(MARGIN_WIDTH);
        maskView = (MaskView) findViewById(R.id.maskView);
        maskView.setMargin(MARGIN_WIDTH);
        maskView.setStrokeWidth(STROKE_WIDTH);



        if (intent!=null){

            Bitmap bitmap = null;

                Uri imageUri = Uri.parse(intent.getStringExtra(MediaStore.EXTRA_OUTPUT));
                try {
                    bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            zoomImageView.setImageBitmap(bitmap);
        }else{
            zoomImageView.setImageResource(R.mipmap.ic_launcher);
        }

        Button backbutton = (Button) findViewById(R.id.button_goback);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button selectbutton = (Button) findViewById(R.id.button_select);
        selectbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = getCircleBitmap(zoomImageView.getCropedImage());
                writeFileData(HEADPORTRAITNAME,bitmap);
                Intent intent = new Intent();
                setResult(RESULT_OK,intent);

                finish();
            }
        });
    }
    //向指定的文件中写入指定的数据
    public void writeFileData(String filename, Bitmap message){
        try {

            FileOutputStream fout = openFileOutput(filename, MODE_PRIVATE);//获得FileOutputStream
            //将要写入的字符串转换为byte数组

            message.compress(Bitmap.CompressFormat.PNG, 100, fout);

            fout.close();//关闭文件输出流
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Bitmap getCircleBitmap(Bitmap bitmap) {//把图片裁剪成圆形
        if (bitmap == null) {
            return null;
        }
        try {
            Bitmap circleBitmap = Bitmap.createBitmap(bitmap.getWidth(),
                    bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(circleBitmap);
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(),
                    bitmap.getHeight());
            final RectF rectF = new RectF(new Rect(0, 0, bitmap.getWidth(),
                    bitmap.getHeight()));
            float roundPx = 0.0f;
            roundPx = bitmap.getWidth();
            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(Color.WHITE);
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            final Rect src = new Rect(0, 0, bitmap.getWidth(),
                    bitmap.getHeight());
            canvas.drawBitmap(bitmap, src, rect, paint);
            return circleBitmap;
        } catch (Exception e) {
            return bitmap;
        }
    }
}
