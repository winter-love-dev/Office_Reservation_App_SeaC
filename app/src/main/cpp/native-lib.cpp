//#include <jni.h>
//#include <opencv2/opencv.hpp>
#include <android/log.h>

//using namespace cv;
//using namespace std;

//extern "C"
//JNIEXPORT void JNICALL
//Java_com_example_open_1cv_1test_ActivityOpenCvCamera_ConvertRGBtoGray(JNIEnv *env, jobject instance,
//                                                                      jlong matAddrInput,
//                                                                      jlong matAddrResult) {
//    // 입력 RGBA 이미지를 GRAY 이미지로 변환
//    Mat &matInput = *(Mat *) matAddrInput;
//    Mat &matResult = *(Mat *) matAddrResult;
//    cvtColor(matInput, matResult, COLOR_RGBA2GRAY);
//}

#include <jni.h>
#include <opencv2/opencv.hpp>
#include <android/log.h>


using namespace cv;
using namespace std;

//extern "C"
//JNIEXPORT void JNICALL
//Java_com_tistory_webnautes_useopencvwithcmake_MainActivity_ConvertRGBtoGray(JNIEnv *env,
//                                                                            jobject instance,
//                                                                            jlong matAddrInput,
//                                                                            jlong matAddrResult) {
//
//    Mat &matInput = *(Mat *)matAddrInput;
//    Mat &matResult = *(Mat *)matAddrResult;
//
//    cvtColor(matInput, matResult, CV_RGBA2GRAY);
//
//}

float resize(Mat img_src, Mat &img_resize, int resize_width) {

    float scale = resize_width / (float) img_src.cols;
    if (img_src.cols > resize_width) {
        int new_height = cvRound(img_src.rows * scale);
        resize(img_src, img_resize, Size(resize_width, new_height));
    } else {
        img_resize = img_src;
    }
    return scale;
}

extern "C"
JNIEXPORT jlong JNICALL
Java_com_example_hun73_seac_1apply_1ver2_opencv_ActivityOpenCvCamera_loadCascade(JNIEnv *env,
                                                                          jobject instance,
                                                                          jstring cascadeFileName_) {
    const char *nativeFileNameString = env->GetStringUTFChars(cascadeFileName_, 0);

    string baseDir("/storage/emulated/0/");
    baseDir.append(nativeFileNameString);
    const char *pathDir = baseDir.c_str();

    jlong ret = 0;
    ret = (jlong) new CascadeClassifier(pathDir);
    if (((CascadeClassifier *) ret)->empty()) {
        __android_log_print(ANDROID_LOG_DEBUG, "native-lib :: ",
                            "CascadeClassifier로 로딩 실패  %s", nativeFileNameString);
    }
    else
        __android_log_print(ANDROID_LOG_DEBUG, "native-lib :: ",
                            "CascadeClassifier로 로딩 성공 %s", nativeFileNameString);

    env->ReleaseStringUTFChars(cascadeFileName_, nativeFileNameString);

    return ret;
//    env->ReleaseStringUTFChars(cascadeFileName_, cascadeFileName);
}

/// 안면인식 활성화
extern "C"
JNIEXPORT jint JNICALL
Java_com_example_hun73_seac_1apply_1ver2_opencv_ActivityOpenCvCamera_detect(JNIEnv *env, jobject instance,
                                                                     jlong cascadeClassifier_face,
                                                                     jlong cascadeClassifier_eye,
                                                                     jlong matAddrInput,
                                                                     jlong matAddrResult) {
    // 이미지를 입력받음
    Mat &img_input = *(Mat *) matAddrInput;

    // 이미지를 출력함
    Mat &img_result = *(Mat *) matAddrResult;

    // 얼굴개수 반환받기
    int ret = 0;

    // 입력받은 이미지를 출력 변수에 담는다.
    img_result = img_input.clone();

    /// veector(백터): 동적 배열 구조를 c++로 구현한 것이다... ( https://ko.wikipedia.org/wiki/%EB%B2%A1%ED%84%B0_(STL) )
    /// 벡터는 C++ 표준 템플릿 라이브러리 중의 하나인 템플릿 클래스이다.
    /// 어떤 타입이라도 저장할 수 있지만, 한 번에 한 타입만 저장이 가능하다.
    /// 요소에 접근하거나, 앞 또는 뒤에 요소를 추가하거나 삭제할 수 있고 크기를 알 수 있는 멤버 함수를 제공하고 있다.
    // 얼굴을 표시할 백터 영역을 동그라미로 (rectangle)
    std::vector<Rect> faces;

    Mat img_gray;

    cvtColor(img_input, img_gray, COLOR_BGR2GRAY);
    equalizeHist(img_gray, img_gray);

    Mat img_resize;
    float resizeRatio = resize(img_gray, img_resize, 640);

    //-- Detect faces
    ((CascadeClassifier *) cascadeClassifier_face)->detectMultiScale( img_resize, faces, 1.1, 2, 0|CASCADE_SCALE_IMAGE, Size(30, 30) );

    // 로그값 혼동을 방지하기 위해 주석처리
    //__android_log_print(ANDROID_LOG_DEBUG, (char *) "native-lib :: ",
    //                   (char *) "face %d found ", faces.size());
    ret = faces.size();

    __android_log_print(ANDROID_LOG_DEBUG, (char *) "native-lib :: ",
                        (char *) "face %d found ", faces.size());

    for (int i = 0; i < faces.size(); i++)
    {
        double real_facesize_x = faces[i].x / resizeRatio;
        double real_facesize_y = faces[i].y / resizeRatio;
        double real_facesize_width = faces[i].width / resizeRatio;
        double real_facesize_height = faces[i].height / resizeRatio;


        // 원 중앙 비우기
//        ellipse(img_result, center, Size( real_facesize_width / 2, real_facesize_height / 2), 0, 0, 360,
//                Scalar(255, 0, 255), 30, 8, 0); // Scalar:

        /// todo: 카카오 캐릭터 어피치 마스크 그리기
        /// 얼굴 중앙에 배치하기
        Point center( real_facesize_x + real_facesize_width / 2, real_facesize_y + real_facesize_height/2);

        /// 얼굴 그리기
        // 원 중앙 채우기
        ellipse(img_result, center, Size( real_facesize_width / 2, real_facesize_height / 2), 0, 0, 360,
                Scalar(245, 196, 202/*컬러*/), -1/*채우기??*/); // Scalar:

        ////////////////////////////////////////////////
        /// 왼 쪽 눈
        ////////////////////////////////////////////////

        /// 왼쪽 눈 배치
        // 왼 쪽 위로 위치 지정하기
        Point top_left( real_facesize_x + real_facesize_width / 3.5, real_facesize_y + real_facesize_height/3.5);

        // 왼 쪽 눈 그리기 (검은자)
        ellipse(img_result, top_left, Size( real_facesize_width / 7/*숫자가 클 수록 원이 작아짐*/,
                                            real_facesize_height / 7), 10, 0, 360, Scalar(41, 53, 66), -1);

        /// 왼쪽 눈동자 1 배치
        // 왼 쪽 위로 위치 지정하기
        Point top_left1( real_facesize_x + real_facesize_width / 4.4/*숫자가 클 수록 왼쪽으로*/,
                         real_facesize_y + real_facesize_height/4.5/*숫자가 클 수록 위로*/);

        // 왼 쪽 눈동자 1 그리기 (흰 자)
        ellipse(img_result, top_left1, Size( real_facesize_width / 20/*숫자가 클 수록 원이 작아짐*/,
                                            real_facesize_height / 20), 10, 0, 360, Scalar(255, 255, 255), -1);

       /// 왼쪽 눈동자 2 배치
        // 왼 쪽 위로 위치 지정하기
        Point top_left2( real_facesize_x + real_facesize_width / 2.9,/*숫자가 클 수록 왼쪽으로*/
                        real_facesize_y + real_facesize_height/4.4/*숫자가 클 수록 위로*/);

        // 왼 쪽 눈동자 2 그리기 (흰 자)
        ellipse(img_result, top_left2, Size( real_facesize_width / 26/*숫자가 클 수록 원이 작아짐*/,
                                             real_facesize_height / 25), 10, 0, 360, Scalar(255, 255, 255), -1);

        /// 왼쪽 눈동자 3 배치
        // 왼 쪽 위로 위치 지정하기
        Point top_left3( real_facesize_x + real_facesize_width / 3.5,/*숫자가 클 수록 왼쪽으로*/
                         real_facesize_y + real_facesize_height/3/*숫자가 클 수록 위로*/);

        // 왼 쪽 눈동자 2 그리기 (흰 자)
        ellipse(img_result, top_left3, Size( real_facesize_width / 24/*숫자가 클 수록 원이 작아짐*/,
                                             real_facesize_height / 22), 10, 0, 360, Scalar(255, 255, 255), -1);

        ////////////////////////////////////////////////
        /// 오른 쪽 눈
        ////////////////////////////////////////////////


        /// 오른 쪽 눈 배치
        // 오른 쪽 위로 위치 지정하기
        Point top_right( real_facesize_width + real_facesize_x / 3.5 , real_facesize_y + real_facesize_height / 3.5);

        // 오른 쪽 눈 그리기 (검은자)
        ellipse(img_result, top_right, Size( real_facesize_width / 7/*숫자가 클 수록 원이 작아짐*/,
                                            real_facesize_height / 7), 10, 0, 360, Scalar(41, 53, 66), -1);
        /// 오른 쪽 눈동자 1 배치
        // 오른 쪽 위로 위치 지정하기
        Point top_right1( real_facesize_width + real_facesize_x / 5.5/*숫자가 클 수록 왼쪽으로*/,
                         real_facesize_y + real_facesize_height/4.5/*숫자가 클 수록 위로*/);

        // 왼 쪽 눈동자 1 그리기 (흰 자)
        ellipse(img_result, top_right1, Size( real_facesize_width / 20/*숫자가 클 수록 원이 작아짐*/,
                                             real_facesize_height / 20), 10, 0, 360, Scalar(255, 255, 255), -1);

        /// 오른 쪽 눈동자 2 배치
        // 오른 쪽 위로 위치 지정하기
        Point top_right2( real_facesize_width + real_facesize_x / 2.3,/*숫자가 클 수록 왼쪽으로*/
                         real_facesize_y + real_facesize_height/4.4/*숫자가 클 수록 위로*/);

        // 오른 쪽 눈동자 2 그리기 (흰 자)
        ellipse(img_result, top_right2, Size( real_facesize_width / 26/*숫자가 클 수록 원이 작아짐*/,
                                             real_facesize_height / 25), 10, 0, 360, Scalar(255, 255, 255), -1);

        /// 오른 쪽 눈동자 3 배치
        // 오른 쪽 위로 위치 지정하기
        Point top_right3( real_facesize_width + real_facesize_x / 3.5,/*숫자가 클 수록 왼쪽으로*/
                         real_facesize_y + real_facesize_height/3/*숫자가 클 수록 위로*/);

        // 오른 쪽 눈동자 2 그리기 (흰 자)
        ellipse(img_result, top_right3, Size( real_facesize_width / 24/*숫자가 클 수록 원이 작아짐*/,
                                             real_facesize_height / 22), 10, 0, 360, Scalar(255, 255, 255), -1);

        ////////////////////////////////////////////////
        /// 입
        ////////////////////////////////////////////////

        /// 가운데 아래로 입 그리기 (입 내부)
        Point center_bottom2( real_facesize_x + real_facesize_width / 2,
                              real_facesize_height + real_facesize_y / 1.7/*숫자가 작을수록 입이 내려감*/);

        // 입 내부 그리기
        ellipse(img_result, center_bottom2, Size( real_facesize_width / 16/*숫자가 클 수록 원이 작아짐*/,
                                                  real_facesize_height / 13), 0, 0, 360, Scalar(255, 255, 255)/*흰색*/, -1);

        /// 가운데 아래로 입 그리기 (입 테두리)
        Point center_bottom( real_facesize_x + real_facesize_width / 2,
                             real_facesize_height + real_facesize_y / 1.7/*숫자가 작을수록 입이 내려감*/);

        // 입 테두리 그리기
        ellipse(img_result, center_bottom, Size( real_facesize_width / 14/*숫자가 클 수록 원이 작아짐*/,
                                                 real_facesize_height / 12), 0, 0, 360, Scalar(0, 0, 0)/*검정*/, 10, 8, 0);

        ////////////////////////////////////////////////
        /// 얼굴 볼살
        ////////////////////////////////////////////////


        /// 왼 쪽 볼살 배치
        // 왼 쪽 볼살 위치 지정하기
        Point center_left( real_facesize_x + real_facesize_width / 6/*숫자가 클 수록 왼쪽으로*/,
                           real_facesize_y + real_facesize_height / 1.7);

        // 왼 쪽 볼살 그리기
        ellipse(img_result, center_left, Size( real_facesize_width / 10/*숫자가 클 수록 원이 작아짐*/,
                                            real_facesize_height / 16), 0, 0, 360, Scalar(255, 153, 153), -1);

        /// 오른 쪽 볼살 배치
        // 왼 쪽 볼살 위치 지정하기
        Point center_right( real_facesize_width + real_facesize_x / 2,
                           real_facesize_y + real_facesize_height / 1.7);

        // 왼 쪽 볼살 그리기
        ellipse(img_result, center_right, Size( real_facesize_width / 10/*숫자가 클 수록 원이 작아짐*/,
                                                real_facesize_height / 16), 0, 0, 360, Scalar(255, 153, 153), -1);

        Rect face_area(real_facesize_x, real_facesize_y, real_facesize_width,real_facesize_height);

        Mat faceROI = img_gray( face_area );
        std::vector<Rect> eyes;

        //-- In each face, detect eyes
//        ((CascadeClassifier *) cascadeClassifier_eye)->detectMultiScale( faceROI, eyes, 1.1, 2, 0 |CASCADE_SCALE_IMAGE, Size(30, 30) );

        for ( size_t j = 0; j < eyes.size(); j++ )
        {
//            Point eye_center( real_facesize_x + eyes[j].x + eyes[j].width/2, real_facesize_y + eyes[j].y + eyes[j].height/2 );
//            Point eye_center( real_facesize_x + eyes[j].x + eyes[j].width/4, real_facesize_y + eyes[j].y + eyes[j].height/4 );
//            int radius = cvRound( (eyes[j].width + eyes[j].height)*0.25 );

//            circle( img_result, eye_center, radius, Scalar( 255, 0, 0 ), 30, 8, 0 );
//            circle( img_result, eye_center, radius, Scalar( 41, 53, 66 ), -1 );
//            rectangle( img_result, Point(400, 100), eye_center, Scalar( 41, 53, 66 ));
        }
    }
    return ret;
}


/// 안면인식 종료
extern "C"
JNIEXPORT jint JNICALL
Java_com_example_hun73_seac_1apply_1ver2_opencv_ActivityOpenCvCamera_detect2(JNIEnv *env, jobject instance,
                                                                      jlong cascadeClassifier_face,
                                                                      jlong cascadeClassifier_eye,
                                                                      jlong matAddrInput,
                                                                      jlong matAddrResult) {

    Mat &img_input = *(Mat *) matAddrInput;
    Mat &img_result = *(Mat *) matAddrResult;

    int ret = 0;

    img_result = img_input.clone();
    std::vector<Rect> faces;
    Mat img_gray;

    cvtColor(img_input, img_gray, COLOR_BGR2GRAY);
    equalizeHist(img_gray, img_gray);

    Mat img_resize;
    float resizeRatio = resize(img_gray, img_resize, 640);

    //-- Detect faces
//    ((CascadeClassifier *) cascadeClassifier_face)->detectMultiScale( img_resize, faces, 1.1, 2, 0|CASCADE_SCALE_IMAGE, Size(30, 30) );

    // 로그값 혼동을 방지하기 위해 주석처리
    //__android_log_print(ANDROID_LOG_DEBUG, (char *) "native-lib :: ",
    //                   (char *) "face %d found ", faces.size());
    ret = faces.size();


    __android_log_print(ANDROID_LOG_DEBUG, (char *) "native-lib :: ",
                        (char *) "face %d found ", faces.size());

    for (int i = 0; i < faces.size(); i++)
    {
        double real_facesize_x = faces[i].x / resizeRatio;
        double real_facesize_y = faces[i].y / resizeRatio;
        double real_facesize_width = faces[i].width / resizeRatio;
        double real_facesize_height = faces[i].height / resizeRatio;

        Point center( real_facesize_x + real_facesize_width / 2, real_facesize_y + real_facesize_height/2);

        ellipse(img_result, center, Size( real_facesize_width / 2, real_facesize_height / 2), 0, 0, 360,
                Scalar(255, 0, 255), 30, 8, 0);

        Rect face_area(real_facesize_x, real_facesize_y, real_facesize_width,real_facesize_height);
        Mat faceROI = img_gray( face_area );
        std::vector<Rect> eyes;

        //-- In each face, detect eyes
//        ((CascadeClassifier *) cascadeClassifier_eye)->detectMultiScale( faceROI, eyes, 1.1, 2, 0 |CASCADE_SCALE_IMAGE, Size(30, 30) );

//        for ( size_t j = 0; j < eyes.size(); j++ )
//        {
//            Point eye_center( real_facesize_x + eyes[j].x + eyes[j].width/2, real_facesize_y + eyes[j].y + eyes[j].height/2 );
//            int radius = cvRound( (eyes[j].width + eyes[j].height)*0.25 );
//            circle( img_result, eye_center, radius, Scalar( 255, 0, 0 ), 30, 8, 0 );
//        }
    }
    return ret;

}

