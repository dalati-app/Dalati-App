package com.dalati.ui.models;

public class Type {
    String id, nameAr, NameEn, categoryId;

    public Type(String id, String nameAr, String nameEn, String categoryId) {
        this.id = id;
        this.nameAr = nameAr;
        NameEn = nameEn;
        this.categoryId = categoryId;
    }

    public String getId() {
        return id;
    }

    public Type() {
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

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }
}
