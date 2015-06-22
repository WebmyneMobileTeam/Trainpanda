package Model;

import com.google.gson.annotations.SerializedName;

public class Rating {


	@SerializedName("restaurantId")
	public String restaurantId;

	@SerializedName("userId")
	public String userId;

	@SerializedName("orderId")
	public String orderId;

	@SerializedName("feedback")
	public String feedback;

	@SerializedName("rating")
	public int rating;

	@SerializedName("id")
	public String id;


}
