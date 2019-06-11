package com.example.hun73.seac_apply_ver2;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Semaphore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;


public class ActivityOpenCvCamera extends AppCompatActivity
        implements CameraBridgeViewBase.CvCameraViewListener2
{
    private static final String TAG = "opencv";
    //    private CameraBridgeViewBase mOpenCvCameraView;
    private CameraBridgeViewBase mOpenCvCameraView;
    private Mat matInput;
    private Mat matResult;
    private Mat mGray;

    //    public native void ConvertRGBtoGray(long matAddrInput, long matAddrResult);
    public native long loadCascade(String cascadeFileName);

    public native int detect_On(long cascadeClassifier_face,
                                long cascadeClassifier_eye, long matAddrInput, long matAddrResult);

//    public native int detect_Mask_On(long cascadeClassifier_face,
//                                long cascadeClassifier_eye, long matAddrInput, long matAddrResult);

    public native int detect_Off(long cascadeClassifier_face,
                                 long cascadeClassifier_eye, long matAddrInput, long matAddrResult);

//    public native int putMask(Mat src, Point center, Size face_size);

    // 화면 방향 리스너 (카메라 방향을 세로로 돌리기 위한)
    private OrientationEventListener mOrientEventListener;

    public long cascadeClassifier_face = 0;
    public long cascadeClassifier_face_mask = 0;
    public long cascadeClassifier_eye = 0;
    public long cascadeClassifier_logo = 0;

    private final Semaphore writeLock = new Semaphore(1);

    public void getWriteLock() throws InterruptedException
    {
        writeLock.acquire();
    }

    public void releaseWriteLock()
    {
        writeLock.release();
    }

    static
    {
        System.loadLibrary("opencv_java4");
        System.loadLibrary("native-lib");
    }

    private void copyFile(String filename)
    {
        String baseDir = Environment.getExternalStorageDirectory().getPath();
        String pathDir = baseDir + File.separator + filename;

        AssetManager assetManager = this.getAssets();

        InputStream inputStream = null;
        OutputStream outputStream = null;

        try
        {
            Log.d(TAG, "copyFile :: 다음 경로로 파일복사 " + pathDir);
            inputStream = assetManager.open(filename);
            outputStream = new FileOutputStream(pathDir);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1)
            {
                outputStream.write(buffer, 0, read);
            }
            inputStream.close();
            inputStream = null;
            outputStream.flush();
            outputStream.close();
            outputStream = null;
        } catch (Exception e)
        {
            Log.d(TAG, "copyFile :: 파일 복사 중 예외 발생 " + e.toString());
        }
    }

    private void read_cascade_file()
    {
        copyFile("haarcascade_frontalface_alt2.xml");
//        copyFile("haarcascade_eye_tree_eyeglasses.xml");
//        copyFile("opencv_mask_5.jpg");

        cascadeClassifier_face = loadCascade("haarcascade_frontalface_alt2.xml");

        Log.d(TAG, "read_cascade_file:");

//        cascadeClassifier_eye = loadCascade("haarcascade_eye_tree_eyeglasses.xml");
//
//        Log.d(TAG, "read_cascade_file:");

//        cascadeClassifier_face_mask = loadCascade("opencv_mask_5.jpg");
//        cascadeClassifier_logo = loadCascade("drawable/logo_3_op_60.png");
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this)
    {
        @Override
        public void onManagerConnected(int status)
        {
            switch (status)
            {
                case LoaderCallbackInterface.SUCCESS:
                {
                    mOpenCvCameraView.enableView();
                }
                break;
                default:
                {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_opencv_camera);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            //퍼미션 상태 확인
            if (!hasPermissions(PERMISSIONS))
            {
                //퍼미션 허가 안되어있다면 사용자에게 요청
                requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            } else
            {
                read_cascade_file(); //추가
            }
        } else
        {
            read_cascade_file(); //추가
        }

//        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.activity_surface_view);
        mOpenCvCameraView = findViewById(R.id.activity_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);

        // 카메라 위치
        mOpenCvCameraView.setCameraIndex(1); // front-camera(1) 기기 화면 방향,  back-camera(0) 기기 후면 방향(노트북 웹캠 방향)

        mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);


//        imageView = (ImageView)this.findViewById(R.id.image_view);

        // 캡쳐버튼
        Button button = (Button) findViewById(R.id.button);
        button.setVisibility(View.GONE);
        button.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                try
                {
                    getWriteLock();

                    File path = new File(Environment.getExternalStorageDirectory() + "/Images/");
                    path.mkdirs();
                    File file = new File(path, getTime() + ".png");

                    String filename = file.toString();

                    Imgproc.cvtColor(matResult, matResult, Imgproc.COLOR_BGR2RGB, 4);
                    boolean ret  = Imgcodecs.imwrite( filename, matResult);
                    if ( ret ) Log.d(TAG, "SUCESS");
                    else Log.d(TAG, "FAIL");

                    Intent mediaScanIntent = new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    mediaScanIntent.setData(Uri.fromFile(file));
                    sendBroadcast(mediaScanIntent);

                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }

                releaseWriteLock();
            }
        });
    }
    // todo: 스크린샷 시작

    // 시간계산
    long mNow;
    Date mDate;

    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    // 현재시간 구하기
    private String getTime()
    {
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        return mFormat.format(mDate);
    }

    // todo: 스크린샷 끝
    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (!OpenCVLoader.initDebug())
        {
            Log.d(TAG, "onResume :: Internal OpenCV library not found.");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this, mLoaderCallback);
        } else
        {
            Log.d(TAG, "onResum :: OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy()
    {
        super.onDestroy();

        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onCameraViewStarted(int width, int height)
    {

    }

    @Override
    public void onCameraViewStopped()
    {

    }

    // 안면인식 활성, 비활성 전환하기 위한 변수
    int i = 0;

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame)
    {
        try
        {
            getWriteLock();

            matInput = inputFrame.rgba();
            mGray = inputFrame.gray();

            //if ( matResult != null ) matResult.release(); fix 2018. 8. 18

            if (matResult == null)

//             matResult = new Mat(rows(가로), cols(세로), matInput.type());
                matResult = new Mat(matInput.rows(), matInput.cols(), matInput.type());

            // 카메라 세로회전
            Mat rotImage = Imgproc.getRotationMatrix2D(new Point(matInput.cols() / 2, matInput.rows() / 2), 90, 1.0);
            Imgproc.warpAffine(matInput, matInput, rotImage, matInput.size());
            Imgproc.warpAffine(mGray, mGray, rotImage, matInput.size());

            // Core.flip(원본 이미지, 실행된 flip이 저장될 이미지, 각종 flip);
            Core.flip(matInput, matInput, 1);

            // 카메라 세로회전
            // mRgba = mRgba.t();
            // mGray = mGray.t();//(288,352)
            // Core.flip(mRgba, mRgba, 1);
            // Core.flip(mGray, mGray, 1);//(288,352)

            // 안면인식 버튼
            Button button = findViewById(R.id.button_detect);
            button.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (i == 0)
                    {
                        i = 1;

                        return;
                    }

                    if (i == 1)
                    {
                        i = 0;

                        return;
                    }
                }
            });

            if (i == 0)
            {
                // 안면인식 비활성
                int ret = detect_Off(cascadeClassifier_face, cascadeClassifier_eye, matInput.getNativeObjAddr(), matResult.getNativeObjAddr());
//                int ret2 = detect_Mask_On(cascadeClassifier_face_mask, cascadeClassifier_eye, matInput.getNativeObjAddr(), matResult.getNativeObjAddr());

                if (ret != 0)
                    Log.d(TAG, "face " + ret + " found");
//                    Log.d(TAG, "face " + ret2 + " found");

            } else if (i == 1)
            {
                // 안면인식 활성
                int ret = detect_On(cascadeClassifier_face, cascadeClassifier_eye, matInput.getNativeObjAddr(), matResult.getNativeObjAddr());

                if (ret != 0)
                    Log.d(TAG, "face " + ret + " found");
            }

        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        releaseWriteLock();
        return matResult;
    }

    @Override
    public void onBackPressed()
    {
        finish();
        super.onBackPressed();
    }

    //여기서부턴 퍼미션 관련 메소드
    static final int PERMISSIONS_REQUEST_CODE = 1000;
    //    String[] PERMISSIONS = {"android.permission.CAMERA"};
    String[] PERMISSIONS = {"android.permission.CAMERA",
            "android.permission.WRITE_EXTERNAL_STORAGE"};

    private boolean hasPermissions(String[] permissions)
    {
        int result;

        //스트링 배열에 있는 퍼미션들의 허가 상태 여부 확인
        for (String perms : permissions)
        {

            result = ContextCompat.checkSelfPermission(this, perms);

            if (result == PackageManager.PERMISSION_DENIED)
            {
                //허가 안된 퍼미션 발견
                return false;
            }
        }

        //모든 퍼미션이 허가되었음
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode)
        {

            case PERMISSIONS_REQUEST_CODE:
                if (grantResults.length > 0)
                {
                    boolean cameraPermissionAccepted = grantResults[0]
                            == PackageManager.PERMISSION_GRANTED;

//                    if (!cameraPermissionAccepted)
//                        showDialogForPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.");

                    boolean writePermissionAccepted = grantResults[1]
                            == PackageManager.PERMISSION_GRANTED;

                    if (!cameraPermissionAccepted || !writePermissionAccepted)
                    {
                        showDialogForPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.");
                        return;
                    } else
                    {
                        read_cascade_file();
                    }
                }
                break;
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void showDialogForPermission(String msg)
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityOpenCvCamera.this);
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface arg0, int arg1)
            {
                finish();
            }
        });
        builder.create().show();
    }
}
