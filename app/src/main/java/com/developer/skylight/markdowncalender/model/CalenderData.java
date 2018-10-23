package com.developer.skylight.markdowncalender.model;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * Awesome Pojo Generator
 * */
public class CalenderData{
  @SerializedName("Day")
  @Expose
  private Integer Day;
  @SerializedName("StatusCode")
  @Expose
  private Integer StatusCode;
  public void setDay(Integer Day){
   this.Day=Day;
  }
  public Integer getDay(){
   return Day;
  }
  public void setStatusCode(Integer StatusCode){
   this.StatusCode=StatusCode;
  }
  public Integer getStatusCode(){
   return StatusCode;
  }
}