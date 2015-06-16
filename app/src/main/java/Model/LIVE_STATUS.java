package Model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class LIVE_STATUS {

	@SerializedName("position")
	public String position;

	@SerializedName("train_number")
	public String train_number;

	@SerializedName("total")
	public int total;

	@SerializedName("response_code")
	public int response_code;

	@SerializedName("route")
	public ArrayList<LIVE_STATUS_ROUTE> route;


}
