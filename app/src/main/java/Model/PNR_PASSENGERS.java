package Model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class PNR_PASSENGERS {

	@SerializedName("no")
	public long no;

	@SerializedName("booking_status")
	public String booking_status;

	@SerializedName("current_status")
	public String current_status;
}
