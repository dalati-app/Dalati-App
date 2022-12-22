package com.dalati.ui.models;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class Report {
    String id, publisher_id, claimer_id, founder_id, category_id, type_id, date, publishing_date, place, description;
    int status, report_type;
    boolean isEnabled;
    Calendar calForDate = Calendar.getInstance();
    SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd/MM/yyy HH:mm");
    List<String> images;
    String loser_id;

    public Report() {
    }

    public Report(String id, String publisher_id, String claimer_id, String loser_id, String founder_id, String category_id,
                  String type_id, String date, String place,
                  String description, int status, int report_type, boolean isEnabled, List<String> images) {
        this.id = id;
        this.publisher_id = publisher_id;
        this.claimer_id = claimer_id;
        this.founder_id = founder_id;
        this.category_id = category_id;
        this.type_id = type_id;
        this.date = date;
        this.publishing_date = currentDateFormat.format(calForDate.getTime());
        this.place = place;
        this.description = description;
        this.status = status;
        this.report_type = report_type;
        this.isEnabled = isEnabled;
        this.images = images;
        this.loser_id = loser_id;
    }

    public String getLoser_id() {
        return loser_id;
    }

    public void setLoser_id(String loser_id) {
        this.loser_id = loser_id;
    }

    public int getReport_type() {
        return report_type;
    }

    public void setReport_type(int report_type) {
        this.report_type = report_type;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPublisher_id() {
        return publisher_id;
    }

    public void setPublisher_id(String publisher_id) {
        this.publisher_id = publisher_id;
    }

    public String getClaimer_id() {
        return claimer_id;
    }

    public void setClaimer_id(String claimer_id) {
        this.claimer_id = claimer_id;
    }

    public String getFounder_id() {
        return founder_id;
    }

    public void setFounder_id(String founder_id) {
        this.founder_id = founder_id;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getType_id() {
        return type_id;
    }

    public void setType_id(String type_id) {
        this.type_id = type_id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPublishing_date() {
        return publishing_date;
    }

    public void setPublishing_date(String publishing_date) {
        this.publishing_date = publishing_date;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }
}
