package com.example.hun73.seac_apply_ver2.RecyclerView;

import com.google.gson.annotations.SerializedName;

public class Review_List_Item
{
    @SerializedName("upload_date")
    String Review_upload_date;

    @SerializedName("Review")
    String Review_content;

    @SerializedName("photo")
    String Review_wsImage;

    @SerializedName("review_index")
    String Review_index;

    @SerializedName("name")
    String Review_name;

    @SerializedName("wsImage")
    String Review_user_photo;

    public Review_List_Item(String review_upload_date,
                            String review_content,
                            String review_wsImage,
                            String review_index,
                            String review_name,
                            String review_user_photo)
    {
        Review_upload_date = review_upload_date;
        Review_content = review_content;
        Review_wsImage = review_wsImage;
        Review_index = review_index;
        Review_name = review_name;
        Review_user_photo = review_user_photo;
    }

    public String getReview_upload_date()
    {
        return Review_upload_date;
    }

    public void setReview_upload_date(String review_upload_date)
    {
        Review_upload_date = review_upload_date;
    }

    public String getReview_content()
    {
        return Review_content;
    }

    public void setReview_content(String review_content)
    {
        Review_content = review_content;
    }

    public String getReview_wsImage()
    {
        return Review_wsImage;
    }

    public void setReview_wsImage(String review_wsImage)
    {
        Review_wsImage = review_wsImage;
    }

    public String getReview_index()
    {
        return Review_index;
    }

    public void setReview_index(String review_index)
    {
        Review_index = review_index;
    }

    public String getReview_name()
    {
        return Review_name;
    }

    public void setReview_name(String review_name)
    {
        Review_name = review_name;
    }

    public String getReview_user_photo()
    {
        return Review_user_photo;
    }

    public void setReview_user_photo(String review_user_photo)
    {
        Review_user_photo = review_user_photo;
    }
}
