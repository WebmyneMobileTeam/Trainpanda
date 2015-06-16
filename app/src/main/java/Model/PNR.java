package Model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class PNR {

	@SerializedName("response_code")
	public long response_code;

	@SerializedName("pnr")
	public String pnr;

	@SerializedName("error")
	public boolean error;

	@SerializedName("train_num")
	public String train_num;

	@SerializedName("train_name")
	public String train_name;

	@SerializedName("chart_prepared")
	public String chart_prepared;

	@SerializedName("total_passengers")
	public String total_passengers;

	@SerializedName("doj")
	public String doj;


	@SerializedName("reservation_upto")
	public PNR_STATION reservation_upto;

	@SerializedName("from_station")
	public PNR_STATION from_station;

	@SerializedName("to_station")
	public PNR_STATION to_station;

	@SerializedName("boarding_point")
	public PNR_STATION boarding_point;

	@SerializedName("passengers")
	public ArrayList<PNR_PASSENGERS> passengers;


}
