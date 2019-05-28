package com.example.hun73.seac_apply_ver2.chat;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.example.hun73.seac_apply_ver2.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

import static java.lang.StrictMath.abs;



public class Activity_ImageView extends AppCompatActivity
{

    private double touch_interval_X = 0; // X 터치 간격
    private double touch_interval_Y = 0; // Y 터치 간격
    private int zoom_in_count = 0; // 줌 인 카운트
    private int zoom_out_count = 0; // 줌 아웃 카운트
    private int touch_zoom = 0; // 줌 크기

    private PhotoViewAttacher mAttacher;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__image_view);

//        ImageView chat_detail_image_view;

        PhotoView chat_detail_image_view;;
        chat_detail_image_view = findViewById(R.id.chat_detail_image_view);

        String ImagePath;
        Intent intent = getIntent();
        ImagePath = intent.getExtras().getString("image"); // 사무실 이름

        // 서버 URL로 불러온 이미지를 세팅한다.
        Picasso.get().load(ImagePath).
                memoryPolicy(MemoryPolicy.NO_CACHE).
                placeholder(R.drawable.logo_2).
                networkPolicy(NetworkPolicy.NO_CACHE).
                into(chat_detail_image_view);

        // 이미지 줌
//        mAttacher = new PhotoViewAttacher(chat_detail_image_view);
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event)
//    {
//
//        switch (event.getAction() & MotionEvent.ACTION_MASK)
//        {
//
//            case MotionEvent.ACTION_DOWN: // 싱글 터치
//                CameraPreview.mCamera.autoFocus(new Camera.AutoFocusCallback()
//                { // 오토 포커스 설정
//                    @Override
//                    public void onAutoFocus(boolean success, Camera camera)
//                    {
//                        return;
//                    }
//                });
//                break;
//
//            case MotionEvent.ACTION_MOVE: // 터치 후 이동 시
//                if (event.getPointerCount() == 2)
//                { // 터치 손가락 2개일 때
//                    double now_interval_X = (double) abs(event.getX(0) - event.getX(1)); // 두 손가락 X좌표 차이 절대값
//                    double now_interval_Y = (double) abs(event.getY(0) - event.getY(1)); // 두 손가락 Y좌표 차이 절대값
//
//                    if (touch_interval_X < now_interval_X && touch_interval_Y < now_interval_Y)
//                    { // 이전 값과 비교
//
//                    // 여기에 확대기능에 대한 코드를 정의 하면됩니다. (두 손가락을 벌렸을 때 분기점입니다.)
//                        zoom_in_count++;
//                        if (zoom_in_count > 5)
//                        { // 카운트를 세는 이유 : 너무 많은 호출을 줄이기 위해
//                            zoom_in_count = 0;
//                            touch_zoom += 5;
//                            ZoomseekBar.setProgress(touch_zoom / 6);
//                            if (CameraPreview.params.getMaxZoom() < touch_zoom)
//                                touch_zoom = CameraPreview.params.getMaxZoom();
//                            CameraPreview.params.setZoom(touch_zoom);
//                            CameraPreview.mCamera.setParameters(CameraPreview.params);
//                        }
//                    }
//                    if (touch_interval_X > now_interval_X && touch_interval_Y > now_interval_Y)
//                    {
//                    // 여기에 축소기능에 대한 코드를 정의 하면됩니다. (두 손가락 사이를 좁혔을 때 분기점입니다.)
//                        zoom_out_count++;
//
//                        if (zoom_out_count > 5)
//                        {
//                            zoom_out_count = 0;
//                            touch_zoom -= 10;
//                            ZoomseekBar.setProgress(touch_zoom / 6);
//
//                            if (0 > touch_zoom)
//                                touch_zoom = 0;
//                            CameraPreview.params.setZoom(touch_zoom);
//                            CameraPreview.mCamera.setParameters(CameraPreview.params);
//                        }
//                    }
//                    touch_interval_X = (double) abs(event.getX(0) - event.getX(1));
//                    touch_interval_Y = (double) abs(event.getY(0) - event.getY(1));
//                }
//                break;
////            case MotionEvent.ACTION_POINTER_DOWN : // 여러개 터치했을 때
////                Log.d(TAG, "멀티터치 : " + event.getX() + " , " + event.getY());
////                break;
////            case MotionEvent.ACTION_UP: // 터치 뗐을 때
////                break;
//        }
//
//        return super.onTouchEvent(event);
//    }
}
