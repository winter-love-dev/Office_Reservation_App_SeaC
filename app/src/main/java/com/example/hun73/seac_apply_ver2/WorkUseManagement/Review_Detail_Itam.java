package com.example.hun73.seac_apply_ver2.WorkUseManagement;

import com.google.gson.annotations.SerializedName;

public class Review_Detail_Itam
{
    @SerializedName("wsImage")
    String Review_wsImage; // 사무실 사진 하나

    @SerializedName("upload_date")
    String Review_upload_date; // 리뷰 등록일

    @SerializedName("wsPlaceName")
    String Review_wsPlaceName; // 사무실 이름

    @SerializedName("ReserStartDay")
    String Review_StartDay; // 이용 시작일

    @SerializedName("ReserEndDay")
    String Review_ReserEndDay; // 이용 종료일

    @SerializedName("UseDayTotal")
    String Review_UseDayTotal; // 총 이용일

    @SerializedName("ReserPay")
    int Review_ReserPay; // 이용금액

    @SerializedName("photo")
    String Review_photo; // 유저 프로필 이미지

    @SerializedName("name")
    String Review_name; // 유저 이름

    @SerializedName("Review")
    String Review_content; // 리뷰

    @SerializedName("ReserWorkNo")
    String Review_ReserWorkNo; // 사무실 인덱스 (사무실 상세페이지로 이동하기 용도)

    @SerializedName("ReserUserID")
    String Review_ReserUserID; // 유저 인덱스 (혹시 유저 상세정보 불러올수도 있으니까 미리 대비)

    @SerializedName("review_index")
    String review_index; // 후기 인덱스

    public Review_Detail_Itam(String review_wsImage, String review_upload_date, String review_wsPlaceName, String review_StartDay, String review_ReserEndDay, String review_UseDayTotal, int review_ReserPay, String review_photo, String review_name, String review_content, String review_ReserWorkNo, String review_ReserUserID, String review_index)
    {
        Review_wsImage = review_wsImage;
        Review_upload_date = review_upload_date;
        Review_wsPlaceName = review_wsPlaceName;
        Review_StartDay = review_StartDay;
        Review_ReserEndDay = review_ReserEndDay;
        Review_UseDayTotal = review_UseDayTotal;
        Review_ReserPay = review_ReserPay;
        Review_photo = review_photo;
        Review_name = review_name;
        Review_content = review_content;
        Review_ReserWorkNo = review_ReserWorkNo;
        Review_ReserUserID = review_ReserUserID;
        this.review_index = review_index;
    }

    public String getReview_wsImage()
    {
        return Review_wsImage;
    }

    public void setReview_wsImage(String review_wsImage)
    {
        Review_wsImage = review_wsImage;
    }

    public String getReview_upload_date()
    {
        return Review_upload_date;
    }

    public void setReview_upload_date(String review_upload_date)
    {
        Review_upload_date = review_upload_date;
    }

    public String getReview_wsPlaceName()
    {
        return Review_wsPlaceName;
    }

    public void setReview_wsPlaceName(String review_wsPlaceName)
    {
        Review_wsPlaceName = review_wsPlaceName;
    }

    public String getReview_StartDay()
    {
        return Review_StartDay;
    }

    public void setReview_StartDay(String review_StartDay)
    {
        Review_StartDay = review_StartDay;
    }

    public String getReview_ReserEndDay()
    {
        return Review_ReserEndDay;
    }

    public void setReview_ReserEndDay(String review_ReserEndDay)
    {
        Review_ReserEndDay = review_ReserEndDay;
    }

    public String getReview_UseDayTotal()
    {
        return Review_UseDayTotal;
    }

    public void setReview_UseDayTotal(String review_UseDayTotal)
    {
        Review_UseDayTotal = review_UseDayTotal;
    }

    public int getReview_ReserPay()
    {
        return Review_ReserPay;
    }

    public void setReview_ReserPay(int review_ReserPay)
    {
        Review_ReserPay = review_ReserPay;
    }

    public String getReview_photo()
    {
        return Review_photo;
    }

    public void setReview_photo(String review_photo)
    {
        Review_photo = review_photo;
    }

    public String getReview_name()
    {
        return Review_name;
    }

    public void setReview_name(String review_name)
    {
        Review_name = review_name;
    }

    public String getReview_content()
    {
        return Review_content;
    }

    public void setReview_content(String review_content)
    {
        Review_content = review_content;
    }

    public String getReview_ReserWorkNo()
    {
        return Review_ReserWorkNo;
    }

    public void setReview_ReserWorkNo(String review_ReserWorkNo)
    {
        Review_ReserWorkNo = review_ReserWorkNo;
    }

    public String getReview_ReserUserID()
    {
        return Review_ReserUserID;
    }

    public void setReview_ReserUserID(String review_ReserUserID)
    {
        Review_ReserUserID = review_ReserUserID;
    }

    public String getReview_index()
    {
        return review_index;
    }

    public void setReview_index(String review_index)
    {
        this.review_index = review_index;
    }
}
