package Model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class TRAIN_NAMES {

	@SerializedName("station")
	public ArrayList<STATION_NAME> station;

	@SerializedName("total")
	public int total;

	@SerializedName("response_code")
	public int response_code;

}
