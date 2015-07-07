package Model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class CurrentRestraunt {

	@SerializedName("morningClosingTime")
	public String morningClosingTime;

	@SerializedName("rating")
	public int rating;

	@SerializedName("stationCode")
	public String stationCode;

	@SerializedName("text")
	public String text;

	@SerializedName("railwayVendor")
	public boolean railwayVendor;

	@SerializedName("eveningClosingTime")
	public String eveningClosingTime;

	@SerializedName("eveningOpeningTime")
	public String eveningOpeningTime;

	@SerializedName("deliveryCharges")
	public long deliveryCharges;

	@SerializedName("thai")
	public long thai;

	@SerializedName("addedOn")
	public String addedOn;

	@SerializedName("morningOpeningTime")
	public String morningOpeningTime;

	@SerializedName("minOrderAmount")
	public long minOrderAmount;

	@SerializedName("code")
	public String code;

	@SerializedName("id")
	public String id;

	@SerializedName("jainFoodAvailable")
	public boolean jainFoodAvailable;

	@SerializedName("northIndia")
	public boolean northIndia;

	@SerializedName("pizza")
	public boolean pizza;

	@SerializedName("name")
	public String name;

	@SerializedName("vegItems")
	public boolean vegItems;

	@SerializedName("updatedOn")
	public String updatedOn;

	@SerializedName("southIndia")
	public boolean southIndia;

	@SerializedName("mobileNo")
	public long mobileNo;

	@SerializedName("images")
	public ArrayList<ALLImages> images;


}
