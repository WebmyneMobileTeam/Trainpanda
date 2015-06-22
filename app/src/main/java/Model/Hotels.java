package Model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Hotels {

	@SerializedName("name")
	public String name;

	@SerializedName("mobileNo")
	public long mobileNo;

	@SerializedName("email")
	public String email;

	@SerializedName("stationCode")
	public String stationCode;

	@SerializedName("priceRange")
	public String priceRange;


	@SerializedName("latitude")
	public String latitude;

	@SerializedName("longitude")
	public String longitude;

	@SerializedName("distanceFromStation")
	public long distanceFromStation;

	@SerializedName("avgCostPerDay")
	public long avgCostPerDay;

	@SerializedName("24HoursCheckIn")
	public boolean HoursCheckIn;

	@SerializedName("hotelCategory")
	public long hotelCategory;

	@SerializedName("id")
	public String id;

	@SerializedName("images")
	public ArrayList<ALLImages> images;



}
