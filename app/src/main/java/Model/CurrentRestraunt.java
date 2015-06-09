package Model;

import com.google.gson.annotations.SerializedName;

public class CurrentRestraunt {

	@SerializedName("morningClosingTime")
	public String morningClosingTime;

	@SerializedName("stationCode")
	public String stationCode;

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
	public String pizza;

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


}
