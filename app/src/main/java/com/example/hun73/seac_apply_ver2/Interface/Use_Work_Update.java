package com.example.hun73.seac_apply_ver2.Interface;

import com.google.gson.annotations.SerializedName;

public class Use_Work_Update
{
    @SerializedName("ReserNo")
    String ReserNo; // '예약중'에서 '이용중'으로 업데이트

    @SerializedName("update1")
    String update1; // '예약중'에서 '이용중'으로 업데이트

    @SerializedName("update2")
    String update2; // '이용중'에서 '이용완료'로 업데이트

    @SerializedName("update3")
    String update3; // '이용완료'에서 '리뷰작성'으로 업데이트

    public Use_Work_Update(String reserNo, String update1, String update2, String update3)
    {
        ReserNo = reserNo;
        this.update1 = update1;
        this.update2 = update2;
        this.update3 = update3;
    }

    public String getReserNo()
    {
        return ReserNo;
    }

    public void setReserNo(String reserNo)
    {
        ReserNo = reserNo;
    }

    public String getUpdate1()
    {
        return update1;
    }

    public void setUpdate1(String update1)
    {
        this.update1 = update1;
    }

    public String getUpdate2()
    {
        return update2;
    }

    public void setUpdate2(String update2)
    {
        this.update2 = update2;
    }

    public String getUpdate3()
    {
        return update3;
    }

    public void setUpdate3(String update3)
    {
        this.update3 = update3;
    }
}
