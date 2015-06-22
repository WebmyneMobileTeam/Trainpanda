package Model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class FETCH_SUB_ORDER {

	@SerializedName("orderId")
	public int orderId;

	@SerializedName("totalAmount")
	public int totalAmount;

	@SerializedName("customerId")
	public String customerId;

	@SerializedName("date")
	public String date;

	@SerializedName("status")
	public int status;

	@SerializedName("stationCode")
	public String stationCode;

	@SerializedName("restaurantId")
	public String restaurantId;

	@SerializedName("id")
	public String id;

	public String restaurantName;

	public String restaurantRating;


}
