package com.developer.skylight.markdowncalender.model;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
/**
 * Awesome Pojo Generator
 * */
public class ResponseData {
  @SerializedName("calenderData")
  @Expose
  private ArrayList<CalenderData> calenderData;
  public void setCalenderData(ArrayList<CalenderData> calenderData){
   this.calenderData=calenderData;
  }
  public ArrayList<CalenderData> getCalenderData(){
   return calenderData;
  }
}