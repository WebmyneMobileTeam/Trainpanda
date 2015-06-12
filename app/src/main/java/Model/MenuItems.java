package Model;

import com.google.gson.annotations.SerializedName;

public class MenuItems {

	@SerializedName("restaurantId")
	public String restaurantId;

	@SerializedName("categoryId")
	public String categoryId;

	@SerializedName("subCategoryId")
	public String subCategoryId;

	@SerializedName("menuItem")
	public String menuItem;

	@SerializedName("listPrice")
	public int listPrice;

	@SerializedName("morningAvailabilityOpenTime")
	public String morningAvailabilityOpenTime;


	@SerializedName("morningAvailabilityCloseTime")
	public String morningAvailabilityCloseTime;

	@SerializedName("eveningAvailabilityOpenTime")
	public String eveningAvailabilityOpenTime;

	@SerializedName("eveningAvailabilityCloseTime")
	public String eveningAvailabilityCloseTime;

	@SerializedName("addedOn")
	public String addedOn;

	@SerializedName("updatedOn")
	public String updatedOn;

	@SerializedName("id")
	public String id;

	@SerializedName("restaurantCode")
	public String restaurantCode;

	@SerializedName("categoryCode")
	public String categoryCode;

	@SerializedName("subcategoryCode")
	public String subcategoryCode;


}
