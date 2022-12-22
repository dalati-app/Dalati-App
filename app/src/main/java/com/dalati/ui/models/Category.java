package com.dalati.ui.models;

public class Category {
    String id, nameAr, NameEn;

    public Category(String id, String nameAr, String nameEn) {
        this.id = id;
        this.nameAr = nameAr;
        NameEn = nameEn;
    }

    public Category() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNameAr() {
        return nameAr;
    }

    public void setNameAr(String nameAr) {
        this.nameAr = nameAr;
    }

    public String getNameEn() {
        return NameEn;
    }

    public void setNameEn(String nameEn) {
        NameEn = nameEn;
    }
}
