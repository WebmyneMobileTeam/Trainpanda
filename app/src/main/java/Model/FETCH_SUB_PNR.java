package Model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class FETCH_SUB_PNR {


	@SerializedName("_id")
	public String id;

	@SerializedName("sNo")
	public long sNo;

	@SerializedName("pnr")
	public String pnr;

	@SerializedName("customer")
	public FETCH_PNR_CUSTOMER customer;

	@SerializedName("trainNumber")
	public int trainNumber;

	@SerializedName("startingStation")
	public FETCH_PNR_STATION_NAME startingStation;

	@SerializedName("onPnrStartTime")
	public String onPnrStartTime;

	@SerializedName("startTime")
	public String startTime;


	@SerializedName("startDate")
	public String startDate;

	@SerializedName("startPlatformNo")
	public int startPlatformNo;

	@SerializedName("endStation")
	public FETCH_PNR_STATION_NAME endStation;


	@SerializedName("onPnrEndTime")
	public String onPnrEndTime;

	@SerializedName("endTime")
	public String endTime;


	@SerializedName("endDate")
	public String endDate;

	@SerializedName("endPlatformNo")
	public int endPlatformNo;

	@SerializedName("passengersCount")
	public int passengersCount;

	@SerializedName("bookingClass")
	public String bookingClass;


	@SerializedName("fare")
	public int fare;

	@SerializedName("bookingQuota")
	public String bookingQuota;

	@SerializedName("addedOn")
	public String addedOn;

	@SerializedName("updatedOn")
	public String updatedOn;

	@SerializedName("bookingStatus")
	public String bookingStatus;

	@SerializedName("currentBookingStatus")
	public String currentBookingStatus;


}
