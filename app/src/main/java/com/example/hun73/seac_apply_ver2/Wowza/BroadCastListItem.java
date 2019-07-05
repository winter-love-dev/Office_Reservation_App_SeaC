package com.example.hun73.seac_apply_ver2.Wowza;


import com.google.gson.annotations.SerializedName;

// todo: 와우자 방송목록
public class BroadCastListItem
{
    /**

     흠... 무슨 데이터를 받아오지...?

     1. 송출자 id
     2. 송출자 닉네임
     3. 송출자 프로필 사진
     4. 방송 제목
     5. 방송 시작시간
     6. 데이터 인덱스

    */

    @SerializedName("HostId")
    private String HostId;

    @SerializedName("HostName")
    private String HostName;

    @SerializedName("HostPhoto")
    private String HostPhoto;

    @SerializedName("BroadCastTitle")
    private String BroadCastTitle;

    @SerializedName("BroadCastStartTime")
    private String BroadCastStartTime;

    @SerializedName("BIndex")
    private String BIndex;

    public BroadCastListItem(String hostId, String hostName, String hostPhoto, String broadCastTitle, String broadCastStartTime, String BIndex)
    {
        HostId = hostId;
        HostName = hostName;
        HostPhoto = hostPhoto;
        BroadCastTitle = broadCastTitle;
        BroadCastStartTime = broadCastStartTime;
        this.BIndex = BIndex;
    }

    public String getHostId()
    {
        return HostId;
    }

    public void setHostId(String hostId)
    {
        HostId = hostId;
    }

    public String getHostName()
    {
        return HostName;
    }

    public void setHostName(String hostName)
    {
        HostName = hostName;
    }

    public String getHostPhoto()
    {
        return HostPhoto;
    }

    public void setHostPhoto(String hostPhoto)
    {
        HostPhoto = hostPhoto;
    }

    public String getBroadCastTitle()
    {
        return BroadCastTitle;
    }

    public void setBroadCastTitle(String broadCastTitle)
    {
        BroadCastTitle = broadCastTitle;
    }

    public String getBroadCastStartTime()
    {
        return BroadCastStartTime;
    }

    public void setBroadCastStartTime(String broadCastStartTime)
    {
        BroadCastStartTime = broadCastStartTime;
    }

    public String getBIndex()
    {
        return BIndex;
    }

    public void setBIndex(String BIndex)
    {
        this.BIndex = BIndex;
    }
}
