package org.chatapp.entity;

public class Friend {
    private String phoneNum;
    private String name;
    private String nameContact;
    private String URLImage;
    private boolean actif;
    private String lastTimeActif;

    public Friend(String phoneNum, String name, String nameContact, String URLImage, boolean actif, String lastTimeActif) {
        this.phoneNum = phoneNum;
        this.name = name;
        this.nameContact = nameContact;
        this.URLImage = URLImage;
        this.actif = actif;
        this.lastTimeActif = lastTimeActif;
    }

    public String getLastTimeActif() {
        return lastTimeActif;
    }

    public void setLastTimeActif(String lastTimeActif) {
        this.lastTimeActif = lastTimeActif;
    }

    public String getNameContact() {
        return nameContact;
    }

    public void setNameContact(String nameContact) {
        this.nameContact = nameContact;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getURLImage() {
        return URLImage;
    }

    public void setURLImage(String URLImage) {
        this.URLImage = URLImage;
    }

    public boolean isActif() {
        return actif;
    }

    public void setStatu(boolean actif) {
        this.actif = actif;
    }
}
