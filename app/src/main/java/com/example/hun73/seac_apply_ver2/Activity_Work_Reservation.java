package com.example.hun73.seac_apply_ver2;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Activity_Work_Reservation extends AppCompatActivity
{
    private String TAG = "Activity_Work_Reservation";

    private RadioGroup radioGroup1, radioGroup2;
    private RadioButton
            reser_reg_now_radio, // 즉시 사용
            reser_reg_reservation, //예약
            work_reser_use_day_radio, // 이용일수: 직접입력 선택
            work_reser_use_week_radio, // 이용일수: 일 주일 선택
            work_reser_use_month_radio; // 이용일수: 28일 선택

    private TextView
            work_reser_name, // 사무실 이름
            work_reser_price_d, // 가격
            work_reser_price_w, // 가격
            work_reser_price_m, // 가격
    //            work_reser_use_day_input, // 이용 일 수: 직접입력
    work_reser_use_day_input_result, // 요금 입력 결과
            work_reger_use_week_price_result,  // 요금 선택 7일
            work_reger_use_month_price_result, // 요금 선택 28일
            work_reser_use_start_day, // 이용 시작일
            work_reser_use_end_day, // 이용 종료일
            work_reser_total_price, // 총 가격
            work_reser_address1, // 주소
            work_reser_address2, // 주소
            work_reser_host_name,
            reser_date_pick, // 데이트 피커
            reser_time_pick, // 타임피 피커
            work_reser_host_contact, // 호스트 연락처
            work_reser_confirm; // 결제버튼

    private ImageView reser_work_image; // 사무실 이미지

    EditText work_reser_use_day_input; // 이용 일 수: 직접입력

//    private DatePicker ; // 달력

//    private TimePicker reser_time_pick; // 시간

    private String
            Work_reser_name, // 사무실 이름
            Reser_work_image,
            Work_reser_price_d, // 가격
            Work_reser_price_w, // 가격
            Work_reser_price_m, // 가격
            CWorkPriceDD, // 하루 가격
            getInputUseDay, // 입력한 사용날짜 불러오기
            Work_reser_use_day_input_result, // 요금 입력 결과
            Work_reger_use_week_price_result,  // 요금 선택 7일
            Work_reger_use_month_price_result, // 요금 선택 28일
            Work_reser_use_start_day, // 이용 시작일
            Work_reser_use_end_day, // 이용 종료일
            Work_reser_total_price, // 총 가격
            Work_reser_address1, // 주소
            Work_reser_address2, // 주소
            Work_reser_host_host_name, // 호스트 이름
            Work_reser_host_contact, // 호스트 연락처
            Work_reser_confirm, // 결제버튼
            Work_reser_use_day_input, // 이용일수: 직접입력
            Reser_date_pick, // 달력
            Reser_time_pick, // 시간
            WorkDBIndex,
            Aviliability;

    // 세션 선언.
    // 회원정보 변경 또는 로그아웃
    SessionManager sessionManager;

    // 세션으로 불러온 유저의 '유저 번호' 불러오기
    String getId;

    // 예약 완료 후 해당 액티비티를 종료하기 위해 이 액티비티를 static으로 선언한다.
    public static Activity reservationActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__work__reservation);

        // 예약 완료 후 해당 액티비티를 종료하기 위해 이 액티비티를 static으로 선언한다.
        reservationActivity = Activity_Work_Reservation.this;

        // 툴바 생성
        Toolbar toolbar = (Toolbar) findViewById(R.id.Reservation_Space_toolbar); // 툴바 연결하기, 메뉴 서랍!!
        setSupportActionBar(toolbar); // 툴바 띄우기

        //actionBar 객체를 가져올 수 있다.
        ActionBar actionBar = getSupportActionBar();

        // 메뉴바에 '<-' 버튼이 생긴다.(두개는 항상 같이다닌다)
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        toolbar.setTitleTextColor(Color.WHITE); // 툴바 타이틀 색상 흰색으로 지정하기
        setSupportActionBar(toolbar);

        // 뷰 연결하기
        work_reser_name = findViewById(R.id.work_reser_name); // 사무실 이름
        work_reser_price_d = findViewById(R.id.work_reser_price_d); // 가격
        work_reser_price_w = findViewById(R.id.work_reser_price_w); // 가격
        work_reser_price_m = findViewById(R.id.work_reser_price_m); // 가격

        reser_work_image = findViewById(R.id.reser_work_image); // 사무실 이미지

        radioGroup1 = findViewById(R.id.radioGroup1); // 라디오그룹 1
        reser_reg_now_radio = findViewById(R.id.reser_reg_now_radio); // 즉시 사용
        reser_reg_reservation = findViewById(R.id.reser_reg_reservation); // 예약하기

        radioGroup2 = findViewById(R.id.radioGroup2);// 라디오그룹 2
        reser_date_pick = findViewById(R.id.reser_date_pick);
        reser_time_pick = findViewById(R.id.reser_time_pick);
        work_reser_use_day_radio = findViewById(R.id.work_reser_use_day_radio); // 이용일수: 직접입력 선택
        work_reser_use_day_input = findViewById(R.id.work_reser_use_day_input); // 이용일수: 직접입력
        work_reser_use_day_input_result = findViewById(R.id.work_reser_use_day_input_result); // 요금 입력 결과

        work_reser_use_week_radio = findViewById(R.id.work_reser_use_week_radio); // 이용일수: 일 주일 선택
        work_reger_use_week_price_result = findViewById(R.id.work_reger_use_week_price_result);  // 요금 선택 7일

        work_reser_use_month_radio = findViewById(R.id.work_reser_use_month_radio); // 이용일수: 28일 선택
        work_reger_use_month_price_result = findViewById(R.id.work_reger_use_month_price_result); // 요금 선택 28일

        work_reser_use_start_day = findViewById(R.id.work_reser_use_start_day); // 이용 시작일
        work_reser_use_end_day = findViewById(R.id.work_reser_use_end_day); // 이용 종료일
        work_reser_total_price = findViewById(R.id.work_reser_total_price); // 총 가격

        work_reser_address1 = findViewById(R.id.work_reser_address1); // 주소
        work_reser_address2 = findViewById(R.id.work_reser_address2); // 주소

        work_reser_host_name = findViewById(R.id.work_reser_host_name); // 호스트 이름
        work_reser_host_contact = findViewById(R.id.work_reser_host_contact); // 호스트 연락처

        work_reser_confirm = findViewById(R.id.work_reser_confirm); // 결제버튼

        // 세션생성
        sessionManager = new SessionManager(this);

        // 로그인 체크 메소드.
        // isLogin() 메소드의 값이 false면 (true가 아니면)
        // 로그인 액티비티로 이동하기
        sessionManager.checkLogin();

        // Map에 저장된 유저 정보 불러오기 (이름, 이메일, 유저번호)
        HashMap<String, String> user = sessionManager.getUserDetail();

        // 불러온 유저의 정보 중 '유저 번호'를 아래 getId에 담기
        getId = user.get(sessionManager.ID);

        Log.e(TAG, "onCreate: 예약할 유저의 인덱스: " + getId);

        // 현재날짜에서 종료날짜 구하기
//        doDateAdd();

        // 날짜 차이 구하기 (00일 차이납니다.)
//        doDiffOfDate();

        // 값 받아서 세팅하기
        ReservationWorkSpace();

        // 결제버튼 비활성화
        work_reser_confirm.setEnabled(false);
    }

    String CWorkPriceM;
    String CWorkPriceW;

    // 서버로 예약 요청하기
    private void ReservationWorkSpace()
    {
        Intent intent = getIntent();

        Work_reser_name = intent.getExtras().getString("WorkName"); // 사무실 이름
        Reser_work_image = intent.getExtras().getString("ResertumbImage"); // 이미지
        Work_reser_price_d = intent.getExtras().getString("WorkPriceD");  // 가격
        Work_reser_price_w = intent.getExtras().getString("WrokPriceW");  // 가격
        Work_reser_price_m = intent.getExtras().getString("WrokPriceM");  // 가격
        Work_reger_use_week_price_result = intent.getExtras().getString("WrokPriceW");   // 요금 선택 7일
        Work_reger_use_month_price_result = intent.getExtras().getString("WrokPriceM");  // 요금 선택 28일
        Work_reser_address1 = intent.getExtras().getString("WorkAddress1");  // 주소
        Work_reser_address2 = intent.getExtras().getString("WorkAddress2");  // 주소
        Work_reser_host_host_name = intent.getExtras().getString("WorkHostProfileName");  // 호스트 이름
        Work_reser_host_contact = intent.getExtras().getString("WorkHostProfileCall");  // 호스트 연락처
        WorkDBIndex = intent.getExtras().getString("WorkDBIndex"); // 사무실 DB 인덱스

        Log.e(TAG, "ReservationWorkSpace: Work_reser_name: " + Work_reser_name);
        Log.e(TAG, "ReservationWorkSpace: Work_reser_price_d: " + Work_reser_price_d);
        Log.e(TAG, "ReservationWorkSpace: Work_reser_price_w: " + Work_reser_price_w);
        Log.e(TAG, "ReservationWorkSpace: Work_reser_price_m: " + Work_reser_price_m);
        Log.e(TAG, "ReservationWorkSpace: Work_reger_use_week_price_result: " + Work_reger_use_week_price_result);
        Log.e(TAG, "ReservationWorkSpace: Work_reger_use_month_price_result: " + Work_reger_use_month_price_result);
        Log.e(TAG, "ReservationWorkSpace: Work_reser_address1: " + Work_reser_address1);
        Log.e(TAG, "ReservationWorkSpace: Work_reser_address2: " + Work_reser_address2);
        Log.e(TAG, "ReservationWorkSpace: Work_reser_host_host_name: " + Work_reser_host_host_name);
        Log.e(TAG, "ReservationWorkSpace: Work_reser_host_contact: " + Work_reser_host_contact);
        Log.e(TAG, "ReservationWorkSpace: WorkDBIndex: " + WorkDBIndex);


        work_reser_name.setText(Work_reser_name);
        Picasso.get().load(Reser_work_image)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .placeholder(R.drawable.logo_2)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .into(reser_work_image);

        long D = Long.parseLong(Work_reser_price_d);
        DecimalFormat formatD = new DecimalFormat("###,###");//콤마
        formatD.format(D);

        CWorkPriceDD = formatD.format(D);
        work_reser_price_d.setText("1일: " + CWorkPriceDD + "원");

        work_reser_use_day_input_result.setText(null);
        work_reser_use_day_input_result.setText("(하루: ￦ " + CWorkPriceDD + " )");

        long W = Long.parseLong(String.valueOf(Work_reser_price_w));
        DecimalFormat formatW = new DecimalFormat("###,###");//콤마
        formatW.format(W);
        CWorkPriceW = formatW.format(W);
        work_reser_price_w.setText("7일: " + CWorkPriceW + "원");

        long M = Long.parseLong(Work_reser_price_m);
        DecimalFormat formatM = new DecimalFormat("###,###");//콤마
        formatM.format(M);
        CWorkPriceM = formatM.format(M);
        work_reser_price_m.setText("28일: " + CWorkPriceM + "원");

        work_reger_use_week_price_result.setText("￦ " + CWorkPriceW);
        work_reger_use_month_price_result.setText("￦ " + CWorkPriceM);

        // 주소
        work_reser_address1.setText(Work_reser_address1);
        work_reser_address2.setText(Work_reser_address2);

        // 호스트 정보
        work_reser_host_name.setText(Work_reser_host_host_name);
        work_reser_host_contact.setText(Work_reser_host_contact);

        // 값을 넘겨받아서 세팅이 필요한 부분에 세팅을 해준다.
        // 이후에 라디오 이벤트를 실행한다.
        RadioChekListner();
    }


    /////////////////////////
    // 시간계산
    long mNow;
    Date mDate;
    String diffDay;
    long diffDays;

    int week = 7,
            month = 28;

    // 종료일
    String strDate;

    //    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    SimpleDateFormat mFormat = new SimpleDateFormat("MM월 dd일 / HH시 mm분");

    // 현재시간 구하기
    private String getTime()
    {
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        return mFormat.format(mDate);
    }


    // 바로 입장할 때 종료일 구하기
//    public void doDateAdd()
//    {
//        Calendar cal = new GregorianCalendar(Locale.KOREA);
//        cal.setTime(new Date());
//
//        cal.add(Calendar.DAY_OF_YEAR, 1); // 지정한 날만큼 더한다
//
//        SimpleDateFormat fm = new SimpleDateFormat("MM월 dd일 / HH시 mm분");
//        strDate = fm.format(cal.getTime());
//
//        Log.e(TAG, "doDateAdd: strDate" + strDate);
////        System.out.println(strDate);
//    }

    // 예약할 때 종료일 구하기
//    public void doDateAddReser()
//    {
//        Calendar cal = new GregorianCalendar(Locale.KOREA);
//        cal.setTime(new Date());
//        cal.add(Calendar.DAY_OF_YEAR, (int) diffDays); // 지정한 날만큼 더한다
//
//        SimpleDateFormat fm = new SimpleDateFormat("MM월 dd일 / HH시 mm분");
//        strDate = fm.format(cal.getTime());
//
//        Log.e(TAG, "doDateAdd: strDate" + strDate );
////        System.out.println(strDate);
//    }

    // 바로 입장할 때 두날짜의 차이 구하기 (00일)
    public String doDiffOfDate(String getDiffday)
    {
        // 선택한 날짜
        String start = getTime();

        // 종료일
        String end = getDiffday;

        Log.e(TAG, "doDiffOfDate: end = getDiffday" + end);

        try
        {
            SimpleDateFormat formatter = new SimpleDateFormat("MM월 dd일 / HH시 mm분");
            Date beginDate = formatter.parse(start);
            Date endDate = formatter.parse(end);

            // 시간차이를 시간,분,초를 곱한 값으로 나누면 하루 단위가 나옴
            long diff = endDate.getTime() - beginDate.getTime();
            diffDays = diff / (24 * 60 * 60 * 1000);

            Log.e(TAG, "doDiffOfDate: diffDays: " + diffDays);

            diffDay = String.valueOf(diffDays);

            Log.e(TAG, "doDiffOfDate: diffDay: " + diffDay);
        }

        catch (ParseException e)
        {
            e.printStackTrace();
        }
        return diffDay;
    }

    // 예약할 때 두 날짜의 차이 구하기 (00일)
    public String doDiffOfReserDate(String getDiffday)
    {
        // 선택한 날짜
        String start = ReserStartDay;

        // 종료일
        String end = getDiffday;

        Log.e(TAG, "doDiffOfDate: end = getDiffday: " + end);

        try
        {
            SimpleDateFormat formatter = new SimpleDateFormat("MM월 dd일 / HH시 mm분");
            Date beginDate = formatter.parse(start);
            Date endDate = formatter.parse(end);

            // 시간차이를 시간,분,초를 곱한 값으로 나누면 하루 단위가 나옴
            long diff = endDate.getTime() - beginDate.getTime();
            diffDays = diff / (24 * 60 * 60 * 1000);

            Log.e(TAG, "doDiffOfDate: diffDays: " + diffDays);

            diffDay = String.valueOf(diffDays);

            Log.e(TAG, "doDiffOfDate: diffDay: " + diffDay);

        } catch (ParseException e)
        {
            e.printStackTrace();
        }

        return diffDay;
    }


    // 시간계산
    /////////////////////////

    private String msg, // 예약날짜
            msg2, // 예약시간
            ReserStartDay; // 예약 시작일

    private DatePickerDialog dialog;

    private int getYear, getMonth, getDate, getHour, getMin; // 데이트피커, 타임피커의 연 월 일 시 분 구해서 담기

    private TimePickerDialog TimeDialog;

    private String
            UseNow_Time, // 바로 이용할 때의 시간을 담는다.
            UseNow_PriceD, // 바로 이용할 때의 하루가격
            UseNow_PriceW, // 바로 이용할 때의 7일 가격
            UseNow_PriceM, // 바로 이용할 때의 28일 가격
            UseNow_From_EndDay, // 바로 이용할 때의 퇴실 날짜
            UseNow_DifF_Day, // 바로 이용할 사용 일 수
    //
    UseReser_Time, // 예약 시간. 입장할 시간
            UseReser_PriceD, // 예약 가격
            UseReser_PriceW, // 예약 가격
            UseReser_PriceM, // 예약 가격
            UseReser_From_EndDay, // 예약 사용 종료일 구하기
            UseReser_Diff_Day; // 예약 할 때 입장 후 이용할 사용 일 수

    // 라디오 그룹 1
    // 바로이용 / 예약 선택
    private void RadioChekListner()
    {
        RadioGroup.OnCheckedChangeListener WorkPlaceUseType =
                new RadioGroup.OnCheckedChangeListener()
                {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId)
                    {
                        // 바로입장 할 때 날짜 구하기
                        if (checkedId == R.id.reser_reg_now_radio)
                        {
                            // 입장날짜 선택하면 아래 라디오그룹 활성화
                            work_reser_use_day_radio.setEnabled(true);
                            work_reser_use_week_radio.setEnabled(true);
                            work_reser_use_month_radio.setEnabled(true);

                            work_reser_use_day_input.setText(null); // 사용일 선택
                            work_reser_use_start_day.setText(null);
                            work_reser_use_end_day.setText(null);


                            reser_date_pick.setVisibility(View.GONE);
                            reser_date_pick.setText(null);
                            reser_date_pick.setText("입장할 날짜");

                            reser_time_pick.setVisibility(View.GONE);
                            reser_time_pick.setText(null);
                            reser_time_pick.setEnabled(false);
                            reser_time_pick.setText("입장할 시간");

                            // 현재 시간 구하기
                            work_reser_use_start_day.setText(getTime());
                            work_reser_use_end_day.setText(null);

                            // 현재시간.
                            // 결제 페이지로 전송할 값을 담는다.
                            UseNow_Time = getTime();

                            Log.e(TAG, "onCheckedChanged: getTime(): " + getTime());

                            // 바로이용 계산 로직으로 이동
                            // 계산 목록: 종료일, 총 가격
                            UseNow();
                        }

                        // 예약 할 때 날짜 구하기
                        else if (checkedId == R.id.reser_reg_reservation)
                        {
                            // 입장날짜 선택하면 아래 라디오그룹 활성화
                            work_reser_use_day_radio.setEnabled(false);
                            work_reser_use_week_radio.setEnabled(false);
                            work_reser_use_month_radio.setEnabled(false);

                            // 결제버튼 비활성화
                            work_reser_confirm.setEnabled(false);

                            // 입장시간 초기화
                            work_reser_use_start_day.setText(null);
                            reser_date_pick.setText("입장할 날짜");
                            reser_time_pick.setText("입장할 시간");

                            work_reser_use_day_input.setText(null); // 사용일 선택
                            work_reser_use_day_input.setEnabled(false); // 사용일 선택 비활성

                            reser_time_pick.setEnabled(false);
                            work_reser_total_price.setText(null);

                            // 데이트피커, 타임피커 보이기
                            reser_date_pick.setVisibility(View.VISIBLE);
                            reser_time_pick.setVisibility(View.VISIBLE);

                            // 타임피커 초기화
                            reser_time_pick.setText(null);
                            reser_time_pick.setText("입장할 시간");
                            reser_time_pick.setEnabled(false);

                            //Calendar를 이용하여 년, 월, 일, 시간, 분을 PICKER에 넣어준다.
                            final Calendar cal = Calendar.getInstance();

                            Log.e(TAG, cal.get(Calendar.YEAR) + "");
                            Log.e(TAG, cal.get(Calendar.MONTH) + 1 + "");
                            Log.e(TAG, cal.get(Calendar.DATE) + "");
                            Log.e(TAG, cal.get(Calendar.HOUR_OF_DAY) + "");
                            Log.e(TAG, cal.get(Calendar.MINUTE) + "");

                            //DATE PICKER DIALOG
                            findViewById(R.id.reser_date_pick).setOnClickListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View view)
                                {

                                    dialog = new DatePickerDialog(Activity_Work_Reservation.this,
                                            new DatePickerDialog.OnDateSetListener()
                                            {
                                                @Override
                                                public void onDateSet(DatePicker datePicker, int year, int month, int date)
                                                {
                                                    reser_time_pick.setText("입장할 시간");

                                                    // 입장날짜 선택하면 아래 라디오그룹 활성화
                                                    work_reser_use_day_radio.setEnabled(false);
                                                    work_reser_use_week_radio.setEnabled(false);
                                                    work_reser_use_month_radio.setEnabled(false);

                                                    // 입장시간 초기화
                                                    work_reser_use_start_day.setText(null);

                                                    work_reser_use_day_input.setText(null); // 사용일 선택
                                                    work_reser_use_day_input.setEnabled(false); // 사용일 선택 비활성

                                                    reser_time_pick.setEnabled(false);
                                                    work_reser_total_price.setText(null);

                                                    month = month + 1;
                                                    String formattedMonth = "" + month;
                                                    String formattedDayOfMonth = "" + date;

                                                    msg = String.format("%d월 %d일", month + 1, date);

//                                                    Toast.makeText(Activity_Work_Reservation.this, msg, Toast.LENGTH_SHORT).show();

                                                    // 숫자가 10 이하일 때 '1'을 '01'로 출력하기
                                                    if (month < 10)
                                                    {
                                                        formattedMonth = "0" + month;
                                                    }

                                                    if (date < 10)
                                                    {
                                                        formattedDayOfMonth = "0" + date;
                                                    }

//                                                    msg = formattedMonth + "월 " + formattedDayOfMonth + "일";

                                                    // 타임피커 활성화
                                                    reser_time_pick.setEnabled(true);

                                                    year = dialog.getDatePicker().getYear();
                                                    month = dialog.getDatePicker().getMonth();
                                                    date = dialog.getDatePicker().getDayOfMonth();

                                                    // 연월일 받아서 캘린더 객체에 담기
                                                    getYear = year;
                                                    getMonth = month;
                                                    getDate = date;

                                                    Calendar calendar = Calendar.getInstance();
                                                    calendar.set(year, month, date);

//                                                    SimpleDateFormat format = new SimpleDateFormat("MM월 dd일 / HH시 mm분");
                                                    SimpleDateFormat format = new SimpleDateFormat("MM월 dd일");
                                                    String strDatee = format.format(calendar.getTime());

                                                    Log.e(TAG, "onDateSet: strDatee" + strDatee);

                                                    // 데이트피커 버튼에 날짜 담기
                                                    msg = strDatee;
                                                    reser_date_pick.setText(msg);

                                                    // 입장, 퇴실시간
                                                    work_reser_use_day_input.setText(null);
                                                }
                                            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));

                                    // 날짜는 오늘 이후로만 선택하기
                                    dialog.getDatePicker().setMinDate(System.currentTimeMillis());

                                    // 최대 7일 이후까지만 예약 가능
                                    dialog.getDatePicker().setMaxDate(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(7));
                                    dialog.show();
                                }
                            });

                            //TIME PICKER DIALOG
                            findViewById(R.id.reser_time_pick).setOnClickListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View view)
                                {
                                    TimeDialog = new TimePickerDialog(Activity_Work_Reservation.this, new TimePickerDialog.OnTimeSetListener()
                                    {
                                        @Override
                                        public void onTimeSet(TimePicker timePicker, int hour, int min)
                                        {

                                            // 변 수 한 번 비워주기
                                            ReserStartDay = null;

                                            msg2 = String.format("%d시 %d분", hour, min);
                                            Toast.makeText(Activity_Work_Reservation.this, msg2, Toast.LENGTH_SHORT).show();

                                            String s;
                                            Format formatter;
                                            Calendar calendar = Calendar.getInstance();
                                            // tp = TimePicker
                                            calendar.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
                                            calendar.set(Calendar.MINUTE, timePicker.getCurrentMinute());
                                            calendar.clear(Calendar.SECOND); //reset seconds to zero

                                            formatter = new SimpleDateFormat("HH시 mm분");
                                            s = formatter.format(calendar.getTime()); // 08:00:00
                                            // 예약 계산 구하기

                                            getHour = timePicker.getCurrentHour();
                                            getMin = timePicker.getCurrentMinute();

                                            reser_time_pick.setText(s);

                                            // 사용 시작일과 시간을 더한다.
                                            // 아래 사용 시작일에 세팅한다.
                                            ReserStartDay = msg + " / " + s;

                                            // 사용 시작일 세팅
                                            work_reser_use_start_day.setText(null);
                                            work_reser_use_start_day.setText(ReserStartDay);

                                            UseReser();

                                            // 입장날짜 선택하면 아래 라디오그룹 활성화
                                            work_reser_use_day_radio.setEnabled(true);
                                            work_reser_use_week_radio.setEnabled(true);
                                            work_reser_use_month_radio.setEnabled(true);
                                            work_reser_use_day_input.setEnabled(true); // 사용일 선택 활성
                                        }
                                    }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true);  //마지막 boolean 값은 시간을 24시간으로 보일지 아닐지
                                    TimeDialog.show();
                                }
                            });
                        }
                    }
                };
        // 라디오그룹 활성화
        // 위에서 작성한 설정을 라디오 그룹으로 세팅한다.
        radioGroup1.setOnCheckedChangeListener(WorkPlaceUseType);
    }


    // 바로이용 로직
    public void UseNow()
    {
        Log.e(TAG, "UseNowTime()");
        // 라디오 그룹 2
        // radioGroup2;
        RadioGroup.OnCheckedChangeListener PaymentType = new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                // 직접입력
                if (checkedId == R.id.work_reser_use_day_radio)
                {
                    Toast.makeText(Activity_Work_Reservation.this, "직접입력", Toast.LENGTH_SHORT).show();

                    // 다른 라디오 선택할 때마다 총 가격 / 날짜 초기화 해주기
                    work_reser_total_price.setText(null);
                    work_reser_use_end_day.setText(null);

                    // EditText 활성화
                    work_reser_use_day_input.setEnabled(true);

                    // 가격 입력 감지
                    // 직접 선택한 비용을 계산해서 총 가격 부분에 세팅한다.
                    work_reser_use_day_input.addTextChangedListener(new TextWatcher()
                    {
                        // 변화 감지
                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count)
                        {
                            Log.e(TAG, "onTextChanged: ");
//                            work_reser_use_day_input_result.setText("￦ " + CWorkPriceD);
                        }

                        // 입력 후
                        @Override
                        public void afterTextChanged(Editable arg0)
                        {
                            // 값을 입력하면 아래 가격 계산 로직을 시작한다.
                            // 계산 완료 후 총 가격 부분에 세팅한다.
                            // 직접입력 선택한 값 받기
                            getInputUseDay = work_reser_use_day_input.getText().toString();

                            // 입력한 값이 비어 있거나 (아래에서 계속)
                            if (getInputUseDay == null)
                            {
                                // 다른 라디오 선택할 때마다 총 가격 / 날짜 초기화 해주기
                                work_reser_total_price.setText(null);
                                work_reser_use_end_day.setText(null);

                                work_reser_use_day_input_result.setText(null);
                                work_reser_use_day_input_result.setText("(하루: ￦ " + CWorkPriceDD + " )");
                                Log.e(TAG, "onCheckedChanged: work_reser_use_day_input: isEmpty");
                            }

                            // 입력한 값의 길이가 0이면 값을 초기화 한다.
                            else if (getInputUseDay.length() == 0)
                            {
                                work_reser_use_end_day.setText(null);
                                work_reser_use_day_input_result.setText(null);
                                work_reser_use_day_input_result.setText("(하루: ￦ " + CWorkPriceDD + " )");

                                Log.e(TAG, "onCheckedChanged: work_reser_use_day_input: isEmpty");
                            } else
                            {
                                work_reser_use_day_input_result.setText(null);
                                // 정수로 변환하기
                                int i = Integer.parseInt(getInputUseDay);
                                int j = Integer.parseInt(Work_reser_price_d);
                                Log.e(TAG, "onCheckedChanged: getInputUseDay: " + getInputUseDay);
                                Log.e(TAG, "onCheckedChanged: Work_reser_price_d: " + Work_reser_price_d);

                                // 하루 가격 * 입력값
                                int UseDayResult = j * i;
                                long D = Long.parseLong(String.valueOf(UseDayResult));
                                DecimalFormat formatD = new DecimalFormat("###,###");//콤마formatD.format(D);
                                String CWorkPriceD = formatD.format(D);
                                work_reser_use_day_input_result.setText("￦ " + CWorkPriceD);
                                Log.e(TAG, "afterTextChanged: " + CWorkPriceD);

                                // 총 비용에 세팅
                                work_reser_total_price.setText(CWorkPriceD + " 원");

                                // 날짜 세팅
                                Calendar cal = new GregorianCalendar(Locale.KOREA);
                                cal.setTime(new Date());
                                cal.add(Calendar.DAY_OF_YEAR, Integer.parseInt(getInputUseDay)); // 지정한 날만큼 더한다

                                SimpleDateFormat fm = new SimpleDateFormat("MM월 dd일 / HH시 mm분");
                                strDate = fm.format(cal.getTime());

                                // 종료일
                                work_reser_use_end_day.setText(strDate + " (" + doDiffOfDate(strDate) + " 일 후 퇴실)");

                                // POST로 전송할 값
                                UseNow_PriceD = CWorkPriceD; // 바로 이용할 때의 하루가격
                                UseNow_From_EndDay = strDate; // 바로 이용할 때의 퇴실 날짜
                                UseNow_DifF_Day = doDiffOfDate(strDate); // 바로 이용할 사용 일 수


                                // 결제버튼 활성화
                                work_reser_confirm.setEnabled(true);
                                // 결제 액티비티로 이동하기
                                work_reser_confirm.setOnClickListener(new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(View v)
                                    {
                                        Aviliability = "1";

                                        Send_Reser_Info(
                                                getId, // 유저가
                                                WorkDBIndex, // 사무실을 예약했다
                                                UseNow_Time, // 입장일은 언제고
                                                UseNow_From_EndDay, // 종료일은 언제다
                                                UseNow_DifF_Day, // 총 00일을 사용하며
                                                UseNow_PriceD, // 가격은 00원이다.
                                                Reser_work_image,
                                                Aviliability
                                        );
                                    }
                                });

//                          getId               // 유저가
//                          WorkDBIndex         // 사무실을 예약했다
//                          UseNow_Time         // 입장일은 언제고
//                          UseNow_From_EndDay  // 종료일은 언제다
//                          UseNow_DifF_Day     // 총 00일을 사용하며
//                          UseNow_PriceD       // 가격은 00원이다.

                                // 예약 버튼을 클릭하면
                                // 위에 값들을 다음 액티비티로 전송한다.
                            }
                        }

                        // 입력하기 전에
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after)
                        {
//                            work_reser_use_day_input_result.setText("￦ " + CWorkPriceD);
                            Log.e(TAG, "beforeTextChanged: ");
                        }
                    });
                }

                // 일 주일 예약하기
                else if (checkedId == R.id.work_reser_use_week_radio)
                {
                    // 다른 라디오 선택할 때마다 총 가격 초기화 해주기
                    work_reser_total_price.setText(null);
                    work_reser_use_end_day.setText(null);

                    // 직접입력 비활성화
                    work_reser_use_day_input.setEnabled(false);

                    // 직접입력 비활성화
//                    work_reser_use_day_input_result.setText(null);
                    work_reser_use_day_input.setText(null);
                    work_reser_use_day_input.setEnabled(false);
                    work_reser_use_day_input_result.setText("( 하루: ￦ " + CWorkPriceDD + " )");

                    work_reser_total_price.setText(CWorkPriceW + " 원");

                    Toast.makeText(Activity_Work_Reservation.this, "일 주일 예약 선택", Toast.LENGTH_SHORT).show();

                    Calendar cal = new GregorianCalendar(Locale.KOREA);
                    cal.setTime(new Date());
                    cal.add(Calendar.DAY_OF_YEAR, week); // 지정한 날만큼 더한다

                    SimpleDateFormat fm = new SimpleDateFormat("MM월 dd일 / HH시 mm분");
                    strDate = fm.format(cal.getTime());

                    // 종료일
                    work_reser_use_end_day.setText(strDate + " (" + doDiffOfDate(strDate) + " 일 후 퇴실)");

                    Log.e(TAG, "doDateAdd: strDate" + strDate);

                    // POST로 전송할 값
                    UseNow_PriceW = CWorkPriceW; // 바로 이용할 때의 하루가격
                    UseNow_From_EndDay = strDate; // 바로 이용할 때의 퇴실 날짜
                    UseNow_DifF_Day = doDiffOfDate(strDate); // 바로 이용할 사용 일 수
//                                        UseNow_PriceM, // 바로 이용할 때의 28일 가격
                    // 결제버튼 활성화
                    work_reser_confirm.setEnabled(true);
                    // 결제 액티비티로 이동하기
                    work_reser_confirm.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            Aviliability = "1";

                            Send_Reser_Info(
                                    getId, // 유저가
                                    WorkDBIndex, // 사무실을 예약했다
                                    UseNow_Time, // 입장일은 언제고
                                    UseNow_From_EndDay, // 종료일은 언제다
                                    UseNow_DifF_Day, // 총 00일을 사용하며
                                    UseNow_PriceW, // 가격은 00원이다.
                                    Reser_work_image,
                                    Aviliability
                            );
                        }
                    });
                }

                // 한 달 예약하기
                else if (checkedId == R.id.work_reser_use_month_radio)
                {
                    // 다른 라디오 선택할 때마다 총 가격 초기화 해주기
                    work_reser_total_price.setText(null);
                    work_reser_use_end_day.setText(null);

                    // 직접입력 비활성화
                    // work_reser_use_day_input_result.setText(null);
                    work_reser_use_day_input.setText(null);
                    work_reser_use_day_input.setEnabled(false);

                    work_reser_use_day_input_result.setText(null);
                    work_reser_use_day_input_result.setText("( 하루: ￦ " + CWorkPriceDD + " )");
                    Toast.makeText(Activity_Work_Reservation.this, "한 달 예약 선택", Toast.LENGTH_SHORT).show();

                    work_reser_total_price.setText(CWorkPriceM + " 원");

                    // 날짜 차이 계산
                    Calendar cal = new GregorianCalendar(Locale.KOREA);
                    cal.setTime(new Date());
                    cal.add(Calendar.DAY_OF_YEAR, month); // 지정한 날만큼 더한다

                    SimpleDateFormat fm = new SimpleDateFormat("MM월 dd일 / HH시 mm분");
                    strDate = fm.format(cal.getTime());

                    // 종료일
                    work_reser_use_end_day.setText(strDate + " (" + doDiffOfDate(strDate) + " 일 후 퇴실)");

                    Log.e(TAG, "onCheckedChanged: doDiffOfDate(strDate): " + doDiffOfDate(strDate));

                    UseNow_PriceM = CWorkPriceM; // 바로 이용할 때의 하루가격
                    UseNow_From_EndDay = strDate; // 바로 이용할 때의 퇴실 날짜
                    UseNow_DifF_Day = doDiffOfDate(strDate); // 바로 이용할 사용 일 수

                    // 결제버튼 활성화
                    work_reser_confirm.setEnabled(true);
                    // 결제 액티비티로 이동하기
                    work_reser_confirm.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            Aviliability = "1";

                            Send_Reser_Info(
                                    getId, // 유저가
                                    WorkDBIndex, // 사무실을 예약했다
                                    UseNow_Time, // 입장일은 언제고
                                    UseNow_From_EndDay, // 종료일은 언제다
                                    UseNow_DifF_Day, // 총 00일을 사용하며
                                    UseNow_PriceM, // 가격은 00원이다.
                                    Reser_work_image,
                                    Aviliability
                            );
                        }
                    });
                }
            }
        };
        // 라디오그룹 활성화
        // 위에서 작성한 설정을 아래 라디오 그룹에 세팅한다.
        radioGroup2.setOnCheckedChangeListener(PaymentType);
    }

    // 예약 로직
    public void UseReser()
    {
        Log.e(TAG, "UseReser:");
        // 라디오 그룹 2
        // radioGroup2;
        RadioGroup.OnCheckedChangeListener PaymentType = new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                // 직접입력
                if (checkedId == R.id.work_reser_use_day_radio)
                {
                    Toast.makeText(Activity_Work_Reservation.this, "직접입력", Toast.LENGTH_SHORT).show();

                    // 다른 라디오 선택할 때마다 총 가격 / 날짜 초기화 해주기
                    work_reser_total_price.setText(null); // 총 가격
                    work_reser_use_end_day.setText(null); // 입장 시간

                    work_reser_use_day_input.setText(null); // 사용일 입력
                    work_reser_use_end_day.setText(null); // 퇴실 시간


                    // EditText 활성화
                    work_reser_use_day_input.setEnabled(true);

                    // 가격 입력 감지
                    // 직접 선택한 비용을 계산해서 총 가격 부분에 세팅한다.
                    work_reser_use_day_input.addTextChangedListener(new TextWatcher()
                    {
                        // 변화 감지
                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count)
                        {
                            Log.e(TAG, "onTextChanged: ");
//                            work_reser_use_day_input_result.setText("￦ " + CWorkPriceD);
                        }

                        // 입력 후
                        @Override
                        public void afterTextChanged(Editable arg0)
                        {
                            // 값을 입력하면 아래 가격 계산 로직을 시작한다.
                            // 계산 완료 후 총 가격 부분에 세팅한다.
                            // 직접입력 선택한 값 받기
                            getInputUseDay = work_reser_use_day_input.getText().toString();

                            // 입력한 값이 비어 있거나 (아래에서 계속)
                            if (getInputUseDay == null)
                            {
                                // 다른 라디오 선택할 때마다 총 가격 / 날짜 초기화 해주기
                                work_reser_total_price.setText(null);
                                work_reser_use_end_day.setText(null);

                                work_reser_use_day_input_result.setText(null);
                                work_reser_use_day_input_result.setText("(하루: ￦ " + CWorkPriceDD + " )");
                                Log.e(TAG, "onCheckedChanged: work_reser_use_day_input: isEmpty");
                            }

                            // 입력한 값의 길이가 0이면 값을 초기화 한다.
                            else if (getInputUseDay.length() == 0)
                            {
                                work_reser_use_end_day.setText(null);
                                work_reser_use_day_input_result.setText(null);
                                work_reser_use_day_input_result.setText("(하루: ￦ " + CWorkPriceDD + " )");

                                Log.e(TAG, "onCheckedChanged: work_reser_use_day_input: isEmpty");
                            } else
                            {
                                work_reser_use_day_input_result.setText(null);
                                // 정수로 변환하기
                                int i = Integer.parseInt(getInputUseDay);
                                int j = Integer.parseInt(Work_reser_price_d);
                                Log.e(TAG, "onCheckedChanged: getInputUseDay: " + getInputUseDay);
                                Log.e(TAG, "onCheckedChanged: Work_reser_price_d: " + Work_reser_price_d);

                                // 하루 가격 * 입력값
                                int UseDayResult = j * i;
                                long D = Long.parseLong(String.valueOf(UseDayResult));
                                DecimalFormat formatD = new DecimalFormat("###,###");//콤마formatD.format(D);
                                String CWorkPriceD = formatD.format(D);
                                work_reser_use_day_input_result.setText("￦ " + CWorkPriceD);
                                Log.e(TAG, "afterTextChanged: " + CWorkPriceD);

                                // 총 비용에 세팅
                                work_reser_total_price.setText(CWorkPriceD + " 원");


                                Calendar cal = Calendar.getInstance();
//                                getYear, getMonth, getDate, getHour, getMin;
                                cal.set(getYear, getMonth, getDate, getHour, getMin);
                                cal.add(cal.DAY_OF_YEAR, Integer.parseInt(getInputUseDay)); // 지정한 날만큼 더한다

                                SimpleDateFormat fm = new SimpleDateFormat("MM월 dd일 / HH시 mm분");
                                strDate = fm.format(cal.getTime());

                                Log.e(TAG, "afterTextChanged: strDate : " + strDate);

                                // 예약 로직
                                // 종료일
                                work_reser_use_end_day.setText(strDate + " (" + doDiffOfReserDate(strDate) + " 일 후 퇴실)");

                                Log.e(TAG, "afterTextChanged: UseReser(): " + strDate);

                                UseReser_Time = ReserStartDay;
                                UseReser_PriceD = CWorkPriceD; // 바로 이용할 때의 하루가격
                                UseReser_From_EndDay = strDate; // 바로 이용할 때의 퇴실 날짜
                                UseReser_Diff_Day = doDiffOfReserDate(strDate); // 바로 이용할 사용 일 수

                                // 결제버튼 활성화
                                work_reser_confirm.setEnabled(true);
                                // 결제 액티비티로 이동하기
                                work_reser_confirm.setOnClickListener(new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(View v)
                                    {
                                        Aviliability = "0";

                                        Send_Reser_Info(
                                                getId, // 유저가
                                                WorkDBIndex, // 사무실을 예약했다
                                                UseReser_Time, // 입장일은 언제고
                                                UseReser_From_EndDay, // 종료일은 언제다
                                                UseReser_Diff_Day, // 총 00일을 사용하며
                                                UseReser_PriceD, // 가격은 00원이다.
                                                Reser_work_image,
                                                Aviliability
                                        );
                                    }
                                });


                            }
                        }

                        // 입력하기 전에
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after)
                        {
//                            work_reser_use_day_input_result.setText("￦ " + CWorkPriceD);
                            Log.e(TAG, "beforeTextChanged: ");
                        }
                    });
                }

                // 일 주일 예약하기
                else if (checkedId == R.id.work_reser_use_week_radio)
                {
                    // 다른 라디오 선택할 때마다 총 가격 초기화 해주기
                    work_reser_total_price.setText(null);
                    work_reser_use_end_day.setText(null);

                    // 직접입력 비활성화
                    work_reser_use_day_input.setEnabled(false);

                    // 직접입력 비활성화
//                    work_reser_use_day_input_result.setText(null);
                    work_reser_use_day_input.setText(null);
                    work_reser_use_day_input.setEnabled(false);
                    work_reser_use_day_input_result.setText("( 하루: ￦ " + CWorkPriceDD + " )");

                    work_reser_total_price.setText(CWorkPriceW + " 원");

                    Toast.makeText(Activity_Work_Reservation.this, "일 주일 예약 선택", Toast.LENGTH_SHORT).show();

                    Calendar cal = Calendar.getInstance();
//                                getYear, getMonth, getDate, getHour, getMin;
                    cal.set(getYear, getMonth, getDate, getHour, getMin);
                    cal.add(cal.DAY_OF_YEAR, week); // 지정한 날만큼 더한다

                    SimpleDateFormat fm = new SimpleDateFormat("MM월 dd일 / HH시 mm분");
                    strDate = fm.format(cal.getTime());

                    Log.e(TAG, "afterTextChanged: strDate : " + strDate);

                    // 예약 로직
                    // 종료일
                    work_reser_use_end_day.setText(strDate + " (" + doDiffOfReserDate(strDate) + " 일 후 퇴실)");

                    // 아래 값들을 다음 액티비티로 보내기
                    UseReser_Time = ReserStartDay;
                    UseReser_PriceW = CWorkPriceW; // 바로 이용할 때의 하루가격
                    UseReser_From_EndDay = strDate; // 바로 이용할 때의 퇴실 날짜
                    UseReser_Diff_Day = doDiffOfReserDate(strDate); // 바로 이용할 사용 일 수

                    // 결제버튼 활성화
                    work_reser_confirm.setEnabled(true);
                    // 결제 액티비티로 이동하기
                    work_reser_confirm.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            Aviliability = "0";

                            Send_Reser_Info(
                                    getId, // 유저가
                                    WorkDBIndex, // 사무실을 예약했다
                                    UseReser_Time, // 입장일은 언제고
                                    UseReser_From_EndDay, // 종료일은 언제다
                                    UseReser_Diff_Day, // 총 00일을 사용하며
                                    UseReser_PriceW, // 가격은 00원이다.
                                    Reser_work_image,
                                    Aviliability
                            );
                        }
                    });

                }

                // 한 달 예약하기
                else if (checkedId == R.id.work_reser_use_month_radio)
                {
                    // 다른 라디오 선택할 때마다 총 가격 초기화 해주기
                    work_reser_total_price.setText(null);
                    work_reser_use_end_day.setText(null);

                    // 직접입력 비활성화
                    // work_reser_use_day_input_result.setText(null);
                    work_reser_use_day_input.setText(null);
                    work_reser_use_day_input.setEnabled(false);

                    work_reser_use_day_input_result.setText(null);
                    work_reser_use_day_input_result.setText("( 하루: ￦ " + CWorkPriceDD + " )");
                    Toast.makeText(Activity_Work_Reservation.this, "한 달 예약 선택", Toast.LENGTH_SHORT).show();

                    work_reser_total_price.setText(CWorkPriceM + " 원");

                    // 날짜 차이 계산
                    Calendar cal = Calendar.getInstance();
                    cal.set(getYear, getMonth, getDate, getHour, getMin);
                    cal.add(cal.DAY_OF_YEAR, month); // 지정한 날만큼 더한다

                    SimpleDateFormat fm = new SimpleDateFormat("MM월 dd일 / HH시 mm분");
                    strDate = fm.format(cal.getTime());

                    Log.e(TAG, "afterTextChanged: strDate : " + strDate);

                    // 예약 로직
                    // 종료일
                    work_reser_use_end_day.setText(strDate + " (" + doDiffOfReserDate(strDate) + " 일 후 퇴실)");

                    Log.e(TAG, "onCheckedChanged: doDiffOfDate(strDate): " + doDiffOfDate(strDate));

                    // 아래 값들을 다음 액티비티로 보내기
                    UseReser_Time = ReserStartDay;
                    UseReser_PriceM = CWorkPriceM; // 바로 이용할 때의 하루가격
                    UseReser_From_EndDay = strDate; // 바로 이용할 때의 퇴실 날짜
                    UseReser_Diff_Day = doDiffOfReserDate(strDate); // 바로 이용할 사용 일 수

                    // 결제버튼 활성화
                    work_reser_confirm.setEnabled(true);
                    // 결제 액티비티로 이동하기
                    work_reser_confirm.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            Aviliability = "0";

                            Send_Reser_Info(
                                    getId, // 유저가
                                    WorkDBIndex, // 사무실을 예약했다
                                    UseReser_Time, // 입장일은 언제고
                                    UseReser_From_EndDay, // 종료일은 언제다
                                    UseReser_Diff_Day, // 총 00일을 사용하며
                                    UseReser_PriceM, // 가격은 00원이다.
                                    Reser_work_image,
                                    Aviliability
                            );
                        }
                    });
                }
            }
        };
        // 라디오그룹 활성화
        // 위에서 작성한 설정을 아래 라디오 그룹에 세팅한다.
        radioGroup2.setOnCheckedChangeListener(PaymentType);
    }

    // 예약 버튼을 클릭하면
    // 위에 값들을 다음 액티비티로 전송한다.
    public void Send_Reser_Info
    (
            String UserIndex, // 유저가
            String WorkIndex, // 사무실을 예약했다
            String StartTime, // 입장일은 언제고
            String EndTime, // 종료일은 언제다
            String DiffDay, // 총 00일을 사용하며
            String Pay, // 가격은 00원이다.
            String Image,
            String Aviliability
    )
    {
        Intent intent = new Intent(Activity_Work_Reservation.this, Activity_Reservation_Web_View.class);

        intent.putExtra("UserIndex", UserIndex);
        intent.putExtra("WorkIndex", WorkIndex);
        intent.putExtra("StartTime", StartTime);
        intent.putExtra("EndTime", EndTime);
        intent.putExtra("DiffDay", DiffDay);
        intent.putExtra("Pay", Pay);
        intent.putExtra("image", Image);
        intent.putExtra("Aviliability", Aviliability);

        // null 체크
        if (TextUtils.isEmpty(UserIndex) &&
                TextUtils.isEmpty(WorkIndex) &&
                TextUtils.isEmpty(StartTime) &&
                TextUtils.isEmpty(EndTime) &&
                TextUtils.isEmpty(DiffDay) &&
                TextUtils.isEmpty(Pay))
        {
            Toast.makeText(this, "양식을 모두 채워주세요", Toast.LENGTH_SHORT).show();
        }

        // null 이 아니면 값들을 가지고 다음 액티비티로 전송한다.
        else
        {
            // 결제 화면으로 이동합니다.
            startActivity(intent);
        }
    }

    // 맨 위 툴바 뒤로가기 눌렀을 때 동작
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
            { //toolbar의 back키 눌렀을 때 동작
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        finish();
        super.onBackPressed();
    }
}
