package com.example.hun73.seac_apply_ver2.Wowza;

public class Wowza_Chat_Item
{
    int mode;
    String senderId;
    String senderNick;
    String MType; // 타입 1 = 메시지 / 타입 2 = 사진
    String message;
    String time;
    String UserPhoto;
    int Message_views; // 읽음표시

    public Wowza_Chat_Item(int mode, String senderId, String senderNick, String MType, String message, String time, String userPhoto, int message_views)
    {
        this.mode = mode;
        this.senderId = senderId;
        this.senderNick = senderNick;
        this.MType = MType;
        this.message = message;
        this.time = time;
        UserPhoto = userPhoto;
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

    public String getUserPhoto()
    {
        return UserPhoto;
    }

    public void setUserPhoto(String userPhoto)
    {
        UserPhoto = userPhoto;
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
