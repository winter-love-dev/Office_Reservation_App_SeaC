package com.example.hun73.seac_apply_ver2.RecyclerView;

import android.util.Log;

import com.example.hun73.seac_apply_ver2.R;
import com.google.gson.annotations.SerializedName;

public class Use_Work_Now_Item implements Comparable<Use_Work_Now_Item>
{
    @SerializedName("image")
    private String use_work_now_image; // 사무실 이미지

    @SerializedName("Use_Now_place_name")
    private String use_work_now_place_name; // 사무실 이름

    @SerializedName("ReserStartDay")
    private String use_work_now_start_day; // 시작일

    @SerializedName("ReserEndDay")
    private String use_work_now_end_day; // 종료일
//    private String use_work_now_date_count; // 남은 날짜 (이건 클라에서 직접 구하는 것!!)

    @SerializedName("ReserPay")
    private String use_work_now_pay; // 지불한 금액

    @SerializedName("UseDayTotal")
    private String use_work_now_total_use_day; // 총 사용일 수

    @SerializedName("ReserWorkNo")
    private String use_work_now_place_number; // 사무실 번호

    @SerializedName("Aviliability")
    private String Aviliability; // 이용상태

    @SerializedName("ReserNo")
    private String ReserNo; // 예약테이블 인덱스

    private int Count; // 남은 날짜

    public Use_Work_Now_Item(String use_work_now_image, String use_work_now_place_name, String use_work_now_start_day, String use_work_now_end_day, String use_work_now_pay, String use_work_now_total_use_day, String use_work_now_place_number, String aviliability, String reserNo, int count)
    {
        this.use_work_now_image = use_work_now_image;
        this.use_work_now_place_name = use_work_now_place_name;
        this.use_work_now_start_day = use_work_now_start_day;
        this.use_work_now_end_day = use_work_now_end_day;
        this.use_work_now_pay = use_work_now_pay;
        this.use_work_now_total_use_day = use_work_now_total_use_day;
        this.use_work_now_place_number = use_work_now_place_number;
        Aviliability = aviliability;
        ReserNo = reserNo;
        Count = count;
    }

    public String getUse_work_now_image()
    {
        return use_work_now_image;
    }

    public void setUse_work_now_image(String use_work_now_image)
    {
        this.use_work_now_image = use_work_now_image;
    }

    public String getUse_work_now_place_name()
    {
        return use_work_now_place_name;
    }

    public void setUse_work_now_place_name(String use_work_now_place_name)
    {
        this.use_work_now_place_name = use_work_now_place_name;
    }

    public String getUse_work_now_start_day()
    {
        return use_work_now_start_day;
    }

    public void setUse_work_now_start_day(String use_work_now_start_day)
    {
        this.use_work_now_start_day = use_work_now_start_day;
    }

    public String getUse_work_now_end_day()
    {
        return use_work_now_end_day;
    }

    public void setUse_work_now_end_day(String use_work_now_end_day)
    {
        this.use_work_now_end_day = use_work_now_end_day;
    }

    public String getUse_work_now_pay()
    {
        return use_work_now_pay;
    }

    public void setUse_work_now_pay(String use_work_now_pay)
    {
        this.use_work_now_pay = use_work_now_pay;
    }

    public String getUse_work_now_total_use_day()
    {
        return use_work_now_total_use_day;
    }

    public void setUse_work_now_total_use_day(String use_work_now_total_use_day)
    {
        this.use_work_now_total_use_day = use_work_now_total_use_day;
    }

    public String getUse_work_now_place_number()
    {
        return use_work_now_place_number;
    }

    public void setUse_work_now_place_number(String use_work_now_place_number)
    {
        this.use_work_now_place_number = use_work_now_place_number;
    }

    public String getAviliability()
    {
        return Aviliability;
    }

    public void setAviliability(String aviliability)
    {
        Aviliability = aviliability;
    }

    public String getReserNo()
    {
        return ReserNo;
    }

    public void setReserNo(String reserNo)
    {
        ReserNo = reserNo;
    }

    public int getCount()
    {
        return Count;
    }

    public void setCount(int count)
    {
        Count = count;
    }

    @Override
    public int compareTo(Use_Work_Now_Item o)
    {
        if (this.Count > o.Count)
        {
            return 1;
        }
        else if (this.Count < o.Count)
        {
            return -1;
        }
        else
        {
            return -0;
        }
    }
}
