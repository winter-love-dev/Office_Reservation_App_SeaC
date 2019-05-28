package com.example.hun73.seac_apply_ver2.RecyclerView;

import com.google.gson.annotations.SerializedName;

// 채팅 프래그먼트에 출력할 리스트 생성자
public class Chat_Item_User_List
{
    @SerializedName("id")
    String UserIndex;

    @SerializedName("name")
    String Name;

    @SerializedName("email")
    String Email;

    @SerializedName("type")
    String Type;

    @SerializedName("photo")
    String Photo;

    public Chat_Item_User_List(String userIndex, String name, String email, String type, String photo)
    {
        UserIndex = userIndex;
        Name = name;
        Email = email;
        Type = type;
        Photo = photo;
    }

    public String getUserIndex()
    {
        return UserIndex;
    }

    public void setUserIndex(String userIndex)
    {
        UserIndex = userIndex;
    }

    public String getName()
    {
        return Name;
    }

    public void setName(String name)
    {
        Name = name;
    }

    public String getEmail()
    {
        return Email;
    }

    public void setEmail(String email)
    {
        Email = email;
    }

    public String getType()
    {
        return Type;
    }

    public void setType(String type)
    {
        Type = type;
    }

    public String getPhoto()
    {
        return Photo;
    }

    public void setPhoto(String photo)
    {
        Photo = photo;
    }
}
