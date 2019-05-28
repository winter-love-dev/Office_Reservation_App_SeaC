package com.example.hun73.seac_apply_ver2.RecyclerView;

import com.google.gson.annotations.SerializedName;

public class Chat_Item_Room_List
{
    @SerializedName("CRm_Room_No")
    String CRm_Room_No;

    @SerializedName("CRm_Out_Member")
    String CRm_Out_Member;

    @SerializedName("CRm_Last_Seen_Time")
    String CRm_Last_Seen_Time;

    @SerializedName("MemberIndex")
    String MemberIndex;

    @SerializedName("MemberName")
    String MemberName;

    @SerializedName("MemberPhoto")
    String MemberPhoto;

    @SerializedName("MR_Message")
    String MR_Message;

    @SerializedName("MR_Message_Type")
    String MR_Message_Type;

    public Chat_Item_Room_List(String CRm_Room_No,
                               String CRm_Out_Member,
                               String CRm_Last_Seen_Time,
                               String memberIndex,
                               String memberName,
                               String memberPhoto,
                               String MR_Message,
                               String MR_Message_Type)
    {
        this.CRm_Room_No = CRm_Room_No;
        this.CRm_Out_Member = CRm_Out_Member;
        this.CRm_Last_Seen_Time = CRm_Last_Seen_Time;
        MemberIndex = memberIndex;
        MemberName = memberName;
        MemberPhoto = memberPhoto;
        this.MR_Message = MR_Message;
        this.MR_Message_Type = MR_Message_Type;
    }

    public String getCRm_Room_No()
    {
        return CRm_Room_No;
    }

    public void setCRm_Room_No(String CRm_Room_No)
    {
        this.CRm_Room_No = CRm_Room_No;
    }

    public String getCRm_Out_Member()
    {
        return CRm_Out_Member;
    }

    public void setCRm_Out_Member(String CRm_Out_Member)
    {
        this.CRm_Out_Member = CRm_Out_Member;
    }

    public String getCRm_Last_Seen_Time()
    {
        return CRm_Last_Seen_Time;
    }

    public void setCRm_Last_Seen_Time(String CRm_Last_Seen_Time)
    {
        this.CRm_Last_Seen_Time = CRm_Last_Seen_Time;
    }

    public String getMemberIndex()
    {
        return MemberIndex;
    }

    public void setMemberIndex(String memberIndex)
    {
        MemberIndex = memberIndex;
    }

    public String getMemberName()
    {
        return MemberName;
    }

    public void setMemberName(String memberName)
    {
        MemberName = memberName;
    }

    public String getMemberPhoto()
    {
        return MemberPhoto;
    }

    public void setMemberPhoto(String memberPhoto)
    {
        MemberPhoto = memberPhoto;
    }

    public String getMR_Message()
    {
        return MR_Message;
    }

    public void setMR_Message(String MR_Message)
    {
        this.MR_Message = MR_Message;
    }

    public String getMR_Message_Type()
    {
        return MR_Message_Type;
    }

    public void setMR_Message_Type(String MR_Message_Type)
    {
        this.MR_Message_Type = MR_Message_Type;
    }
}
