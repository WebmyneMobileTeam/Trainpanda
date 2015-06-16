package Model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class FETCH_ORDER {

	@SerializedName("orders")
	public ArrayList<FETCH_SUB_ORDER> orders;


}
