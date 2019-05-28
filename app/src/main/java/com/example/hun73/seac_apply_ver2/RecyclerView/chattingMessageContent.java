package com.example.hun73.seac_apply_ver2.RecyclerView;

import com.google.gson.annotations.SerializedName;

public class chattingMessageContent
{

    int mode;

    @SerializedName("senderId")
    String senderId;

    @SerializedName("senderNick")
    String senderNick;

    @SerializedName("MR_Message_Type")
    String MType; // 타입 1 = 메시지 / 타입 2 = 사진

    @SerializedName("MR_Message")
    String message;

    @SerializedName("MR_Send_Time")
    String time;

    @SerializedName("MR_Message_views")
    int Message_views; // 읽음표시

    public chattingMessageContent(int mode, String senderId, String senderNick, String MType, String message, String time, int message_views)
    {
        this.mode = mode;
        this.senderId = senderId;
        this.senderNick = senderNick;
        this.MType = MType;
        this.message = message;
        this.time = time;
        Message_views = message_views;
    }

    public int getMode()
    {
        return mode;
    }

    public void setMode(int mode)
    {
        this.mode = mode;
    }

    public String getSenderId()
    {
        return senderId;
    }

    public void setSenderId(String senderId)
    {
        this.senderId = senderId;
    }

    public String getSenderNick()
    {
        return senderNick;
    }

    public void setSenderNick(String senderNick)
    {
        this.senderNick = senderNick;
    }

    public String getMType()
    {
        return MType;
    }

    public void setMType(String MType)
    {
        this.MType = MType;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public String getTime()
    {
        return time;
    }

    public void setTime(String time)
    {
        this.time = time;
    }

    public int getMessage_views()
    {
        return Message_views;
    }

    public void setMessage_views(int message_views)
    {
        Message_views = message_views;
    }
}
