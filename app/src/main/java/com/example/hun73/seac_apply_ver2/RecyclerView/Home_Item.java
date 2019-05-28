package com.example.hun73.seac_apply_ver2.RecyclerView;

import com.google.gson.annotations.SerializedName;

public class Home_Item
{

    @SerializedName("work_No")
    private String work_No;

    @SerializedName("work_image")
    private String thum_Image_Url;

    @SerializedName("work_name")
    private String work_Name;

    @SerializedName("work_table")
    private String Work_Table;

    @SerializedName("work_table_current")
    private String work_table_current;

    @SerializedName("work_address1")
    private String Work_Address1;

    @SerializedName("work_price")
    private String Work_Price;

    public Home_Item(String work_No,
                     String thum_Image_Url,
                     String work_Name,
                     String work_Table,
                     String work_table_current,
                     String work_Address1,
                     String work_Price)
    {
        this.work_No = work_No;
        this.thum_Image_Url = thum_Image_Url;
        this.work_Name = work_Name;
        Work_Table = work_Table;
        this.work_table_current = work_table_current;
        Work_Address1 = work_Address1;
        Work_Price = work_Price;
    }

    public String getWork_No()
    {
        return work_No;
    }

    public void setWork_No(String work_No)
    {
        this.work_No = work_No;
    }

    public String getThum_Image_Url()
    {
        return thum_Image_Url;
    }

    public void setThum_Image_Url(String thum_Image_Url)
    {
        this.thum_Image_Url = thum_Image_Url;
    }

    public String getWork_Name()
    {
        return work_Name;
    }

    public void setWork_Name(String work_Name)
    {
        this.work_Name = work_Name;
    }

    public String getWork_Table()
    {
        return Work_Table;
    }

    public void setWork_Table(String work_Table)
    {
        Work_Table = work_Table;
    }

    public String getWork_table_current()
    {
        return work_table_current;
    }

    public void setWork_table_current(String work_table_current)
    {
        this.work_table_current = work_table_current;
    }

    public String getWork_Address1()
    {
        return Work_Address1;
    }

    public void setWork_Address1(String work_Address1)
    {
        Work_Address1 = work_Address1;
    }

    public String getWork_Price()
    {
        return Work_Price;
    }

    public void setWork_Price(String work_Price)
    {
        Work_Price = work_Price;
    }
}