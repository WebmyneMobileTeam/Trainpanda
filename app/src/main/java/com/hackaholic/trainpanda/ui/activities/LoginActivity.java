package com.hackaholic.trainpanda.ui.activities;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.gson.GsonBuilder;
import com.hackaholic.trainpanda.R;
import com.hackaholic.trainpanda.ServiceHandler.ServiceHandler;
import com.hackaholic.trainpanda.helpers.EnumType;
import com.hackaholic.trainpanda.helpers.GetPostClass;
import com.hackaholic.trainpanda.utility.ExpandableLayout;

import Model.FETCH_PNR;
import Model.FETCH_SUB_PNR;


public class LoginActivity extends Activity implements OnClickListener, ConnectionCallbacks, OnConnectionFailedListener
{
	private static final int RC_SIGN_IN = 0;
	// Logcat tag

	private GoogleApiClient mGoogleApiClient;

	LoginButton iv_facebook;
	private SignInButton iv_google_plus;
	//private ImageView iv_google_plus;
	private ProgressDialog mDialog,pb;
	private static final String TAG = "LoginActivity";

	private static final String String = null;
	private int flag=0;

	// google plus
	private boolean mIntentInProgress;
	private boolean mSignInClicked;
	private ConnectionResult mConnectionResult;

	//Facebook
	private String arr_permissions[]={"public_profile","user_location","user_likes", "email","user_hometown"};
	private UiLifecycleHelper uiHelper;
	private SharedPreferences sharedPreferences;
	private Editor editor;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		uiHelper = new UiLifecycleHelper(LoginActivity.this, callback);
		uiHelper.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);
		sharedPreferences=getSharedPreferences("TrainPanda",MODE_PRIVATE);
		editor=sharedPreferences.edit();
		initializeImageViews();
		taskRelatedToFacebook(savedInstanceState);

		// Initializing google plus api client
		mGoogleApiClient = new GoogleApiClient.Builder(this)
		.addConnectionCallbacks(this)
		.addOnConnectionFailedListener(this).addApi(Plus.API)
		.addScope(Plus.SCOPE_PLUS_LOGIN).build();

	}


	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		mGoogleApiClient.connect();
	}


	protected void onStop() {
		super.onStop();
		if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}
	}

	private void taskRelatedToFacebook(Bundle savedInstanceState)
	{
		iv_facebook.setReadPermissions(Arrays.asList(arr_permissions));
	}

	//Facebook
	private Session.StatusCallback callback=new Session.StatusCallback()
	{
		@Override
		public void call(Session session, SessionState state, Exception exception) 
		{
			Log.e(TAG, "callback");
			onSessionStateChange(session, state, exception);
		}
	};

	private void initializeImageViews()
	{
		iv_facebook = (LoginButton)findViewById(R.id.iv_facebook);

		//iv_twitter=(ImageView)findViewById(R.id.iv_twitter);
		iv_google_plus=(SignInButton)findViewById(R.id.iv_google_plus);
		//iv_twitter.setOnClickListener(this);
		iv_google_plus.setOnClickListener(this);
	}


	private void onSessionStateChange(Session session, SessionState state, 
			Exception exception) 
	{
		if (state.isOpened()) 
		{

			getFacebookUserData(session);
		}
		else if (state.isClosed())
		{

			//Toast.makeText(getApplicationContext(), "Loggedout", Toast.LENGTH_SHORT).show();
		}
	}


	@SuppressWarnings({ "unused", "deprecation" })
	private void getFacebookUserData(Session session)
	{
		Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {
			private String user_id;

			@Override
			public void onCompleted(GraphUser user, Response response) {
				if (user != null) {
					if (flag == 0) {
						flag = 1;
						checkLoginStatus((String) user.getProperty("email"), user.getInnerJSONObject());
					}
				}
			}

			private void checkLoginStatus(String email, JSONObject innerJSONObject) {
				String url = "http://admin.trainpanda.com/api/customers?filter[where][email]=" + email+"&filter[where][loginType]=Facebook";
				//String url="http://admin.trainpanda.com/api/customers?filter[where][email]=vikas0dhar@gmail.com";
				new FBLoginStatusAsync().execute(new String[]{url, String.valueOf(innerJSONObject)});

				System.out.println("*&*&*&*&*&*&*&*&*&*&*&" + String.valueOf(innerJSONObject));
			}
		});
	}

	//LoginStatusAsync Class
	private class FBLoginStatusAsync extends AsyncTask<String, Void, String>
	{
		private ProgressDialog dialog;
		private String name;
		private String gender;
		private String email;
		private String id;
		private String state;
		@Override
		protected void onPreExecute() 
		{
			super.onPreExecute();
			dialog=new ProgressDialog(LoginActivity.this);
			dialog.setMessage("Please Wait...!");
			dialog.setCancelable(false);
			dialog.show();
		}

		@Override
		protected String doInBackground(String... params) 
		{
			final String res=MYhitServer(params[0]);
			int length=0;
			Log.e("$$$$$$ RESPONSE", res);
			try {
			JSONArray jsonArray = new JSONArray(res);
			length = jsonArray.length();
			}catch (Exception e){
				Log.e("exc parsing",e.toString());
			}

			Log.e("length : ", "" + length);
			if(length>=1)
			{

				Log.e("fb nam", "inside before");
				try {

					JSONArray jsonArray = new JSONArray(res);
					JSONObject CustomerOBJ = jsonArray.getJSONObject(0);

					String custID = CustomerOBJ.getString("id");
					String FBID = CustomerOBJ.getString("facebook");
					String custName = CustomerOBJ.getString("name");
					String custCode = CustomerOBJ.getString("code");

					String url = "https://graph.facebook.com/" + FBID + "/picture?type=small";
					System.out.println("URL :" + url);
					editor.putString("image_url", url);
					editor.putString("customer_id", custID);
					editor.putString("customerCode", custCode);
					editor.putString("idd", custID);
					editor.putString("name", "" + custName);
					editor.commit();

					Log.e("####image url", url);

					System.out.println(editor);
				}catch (Exception e) {
					Log.e("####exception", e.toString());
				}

				//Call Next Activity
				Intent intent=new Intent(LoginActivity.this,MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivity(intent);
				finish();


			}
			else
			{
				//Perform Registration
				String jsonResponse=params[1];
				//Log.e("Json Object : ",""+jsonResponse);
				try
				{
					JSONObject jsonObject=new JSONObject(jsonResponse);
					System.out.println("Response " + jsonResponse);
					id=jsonObject.getString("id");
					String first_name=jsonObject.getString("first_name");
					String timezone=jsonObject.getString("timezone");
					email=jsonObject.getString("email");
					String verified=jsonObject.getString("verified");
					name=jsonObject.getString("name");
					Log.e("###########", "" + name);
					String locale=jsonObject.getString("locale");
					String link=jsonObject.getString("link");
					String last_name=jsonObject.getString("last_name");
					gender=jsonObject.getString("gender");
					String updated_time=jsonObject.getString("updated_time");

					Log.e("fb nam", name);
					if(jsonObject.has("hometown")){
						
						String stateFetch = jsonObject.getString("hometown");
						JSONObject newJsonObj = jsonObject.getJSONObject("hometown");
						String statef = newJsonObj.getString("name");
						state = newJsonObj.getString("name");
						
						System.out.println("State "+statef);
					}

				}
				catch(Exception e)
				{
					Log.e("fb login", e.toString());
					e.printStackTrace();
				}


				//finallyPostData(main);
				try
				{
					HttpClient httpClient = new DefaultHttpClient();
					HttpPost httpPost = new HttpPost("http://admin.trainpanda.com/api/customers");
					List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
					//nameValuePair.add(new BasicNameValuePair("code",String.valueOf(code)));
					nameValuePair.add(new BasicNameValuePair("name",name));
					nameValuePair.add(new BasicNameValuePair("age","0" ));
					nameValuePair.add(new BasicNameValuePair("sex",gender));
					nameValuePair.add(new BasicNameValuePair("email",email));
					nameValuePair.add(new BasicNameValuePair("address","Unavailable"));
					
					nameValuePair.add(new BasicNameValuePair("[city][name]","Unavailable"));
					nameValuePair.add(new BasicNameValuePair("[state][name]",state));
					nameValuePair.add(new BasicNameValuePair("phone","0"));
					nameValuePair.add(new BasicNameValuePair("pinCode","0"));
					nameValuePair.add(new BasicNameValuePair("facebook",id));
					nameValuePair.add(new BasicNameValuePair("loginType","Facebook"));

					httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
					
					HttpResponse response = httpClient.execute(httpPost);

					// write response to log
					if (response.getStatusLine().getStatusCode() == 200)
					{
						HttpEntity entity = response.getEntity();
						String json = EntityUtils.toString(entity);
						Log.e("*****Http Post Login Response:"+response.getStatusLine().getStatusCode(),json);
						//Toast.makeText(getApplicationContext(), ""+json,Toast.LENGTH_SHORT).show();
						String customer_id=getCustomerId(json);
						System.out.println("ID "+customer_id);
						String customer_code=getCustomerCode(json);
						System.out.println("code "+customer_code);

						//Save Facebook Id to Shared Preference
						saveIdToSP(customer_id,customer_code);

						//Call Next Activity
						/*Intent intent=new Intent(LoginActivity.this,MainActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
						startActivity(intent);*/
						Intent intent=new Intent(LoginActivity.this,MainActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
						//intent.putExtra("customer_id",customer_id);
						startActivity(intent);
						finish();
					}
					else
					{
						Log.e("Server Error : "+response.getStatusLine().getStatusCode(),"Server Error");
					}

				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			return null;
		}

		private String getCustomerCode(String json)
		{
			String id=null;
			try
			{
				JSONObject jsonObject=new JSONObject(json);
				id=jsonObject.getString("code");
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return id;
		}

		private void saveIdToSP(String customer_id,String customerCode) 
		{
			String url= "https://graph.facebook.com/"+id+"/picture?type=small";
			System.out.println("URL :"+url);
			editor.putString("image_url", url);
			editor.putString("customer_id",customer_id);
			editor.putString("customerCode",customerCode);
			editor.putString("idd", id);
			editor.putString("name", ""+name);
			editor.commit();

			Log.e("####image url",url);

			System.out.println(editor);
		}



		private String getCustomerId(String json) 
		{
			String id=null;
			try
			{
				JSONObject jsonObject=new JSONObject(json);
				id=jsonObject.getString("id");
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return id;
		}

		private int getRandomNumberForCode(int i)
		{
			Random random=new Random();
			return random.nextInt(i);
		}

		private String MYhitServer(String url)
		{
			int length=0;
			String response="";
			try
			{
				ServiceHandler handler=new ServiceHandler();
				response=handler.makeServiceCall(url, ServiceHandler.GET);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return response;
		}




		private int hitServer(String url)
		{
			int length=0;
			try
			{
				ServiceHandler handler=new ServiceHandler();
				String response=handler.makeServiceCall(url, ServiceHandler.GET);

				Log.e("$$$$$$ RESPONSE",response);
				JSONArray jsonArray=new JSONArray(response);
				length=jsonArray.length();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return length;
		}

		@Override
		protected void onPostExecute(String result) 
		{
			super.onPostExecute(result);
			dialog.dismiss();
		}
	}



	@Override
	public void onClick(View v) 
	{
		switch (v.getId())
		{

		/*case R.id.iv_twitter:

			break;*/

		case R.id.iv_google_plus:
			/*Intent intent1=new Intent(LoginActivity.this,ConfirmMobileNumber.class);
			intent1.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			startActivity(intent1);*/

			if (!mGoogleApiClient.isConnecting()) {
				mSignInClicked = true;
				resolveSignInError();
			}
			/*Intent intent1=new Intent(LoginActivity.this,MainActivity.class);
			intent1.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			startActivity(intent1);*/
			break;

		default:
			break;
		}
	}

	private void printToast(String msg)
	{
		Toast.makeText(getApplicationContext(), ""+msg,Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onResume() 
	{
		super.onResume();

		Session session = Session.getActiveSession();
		if (session != null && (session.isOpened() || session.isClosed()) )
		{
			onSessionStateChange(session, session.getState(), null);
		}
		uiHelper.onResume();
		
		
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);

		if (requestCode == RC_SIGN_IN) {
			if (resultCode != RESULT_OK) {
				mSignInClicked = false;
			}

			mIntentInProgress = false;

			if (!mGoogleApiClient.isConnecting()) {
				mGoogleApiClient.connect();
			}
		}
	}

	@Override
	public void onPause() 
	{
		super.onPause();
		uiHelper.onPause();
		/*if (mGoogleApiClient.isConnected()) {
			Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
			mGoogleApiClient.disconnect();
			mGoogleApiClient.connect();
			
		}*/
	}

	
	@Override
	public void onDestroy() 
	{
		super.onDestroy();
		uiHelper.onDestroy();
		
		if (mGoogleApiClient.isConnected()) {
			Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
			mGoogleApiClient.disconnect();
			mGoogleApiClient.connect();

		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) 
	{
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		if (!arg0.hasResolution()) {
			GooglePlayServicesUtil.getErrorDialog(arg0.getErrorCode(), this,
					0).show();
			return;
		}
		if (!mIntentInProgress) {
			// Store the ConnectionResult for later usage
			mConnectionResult = arg0;

			if (mSignInClicked) {
				// The user has already clicked 'sign-in' so we attempt to
				// resolve all
				// errors until the user is signed in, or they cancel.
				resolveSignInError();
			}
		}
	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		mSignInClicked = false;
		//Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();

		// Get user's information
		getProfileInformation();

		/*Intent intent1=new Intent(LoginActivity.this,MainActivity.class);
		intent1.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		startActivity(intent1);*/
	


	}

	
	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub
		mGoogleApiClient.connect();
	}


	private void resolveSignInError() {
		if (mConnectionResult.hasResolution()) {
			try {
				mIntentInProgress = true;
				mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
			} catch (SendIntentException e) {
				mIntentInProgress = false;
				mGoogleApiClient.connect();
			}
		}
	}


	private void getProfileInformation() {
		try {
			if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
				Person currentPerson = Plus.PeopleApi
						.getCurrentPerson(mGoogleApiClient);
				String personName = currentPerson.getDisplayName();
				String personPhotoUrl = currentPerson.getImage().getUrl();
				String personGooglePlusProfile = currentPerson.getUrl();
				String email = Plus.AccountApi.getAccountName(mGoogleApiClient);

				Log.e("$$$$$$ GOOGLE RESPONSe", "Name: " + personName + ", plusProfile: "
						+ personGooglePlusProfile + ", email: " + email
						+ ", Image: " + personPhotoUrl);

				if(flag==0)
				{
					flag=1;
					checkGoogleLogin(email);
				}
				
		  
			
				//txtName.setText(personName);
				//txtEmail.setText(email);

				// by default the profile url gives 50x50 px image only
				// we can replace the value with whatever dimension we want by
				// replacing sz=X
				//personPhotoUrl = personPhotoUrl.substring(0,personPhotoUrl.length() - 2)+ PROFILE_PIC_SIZE;

				//new LoadProfileImage(imgProfilePic).execute(personPhotoUrl);

			} else {
				Toast.makeText(getApplicationContext(),
						"Person information is null", Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void checkGoogleLogin(String gmail) {
		// TODO Auto-generated method stub
		
		String url="http://admin.trainpanda.com/api/customers?filter[where][email]="+gmail+"&filter[where][loginType]=Google Plus";
		
		new GPLoginStatusAsync().execute(new String[]{url});
		
	}

	//LoginStatusAsync Class
		private class GPLoginStatusAsync extends AsyncTask<String, Void, String>
		{
			private ProgressDialog dialog;
			private String g_name;
			private String g_gender;
			private String g_email;
			private String id;
			private String state;
			@Override
			protected void onPreExecute() 
			{
				super.onPreExecute();
				dialog=new ProgressDialog(LoginActivity.this);
				dialog.setMessage("Please Wait...!");
				dialog.setCancelable(false);
				dialog.show();
			}

			@Override
			protected String doInBackground(String... params) 
			{
				final String res=MYhitServer(params[0]);
				int length=0;
				Log.e("$$$$$$ RESPONSE", res);
				try {
					JSONArray jsonArray = new JSONArray(res);
					length = jsonArray.length();
				}catch (Exception e){
					Log.e("exc parsing",e.toString());
				}

				Log.e("length : ", "" + length);
				if(length>=1)
				{

					Log.e("google nam", "inside before");
					try {

						JSONArray jsonArray = new JSONArray(res);
						JSONObject CustomerOBJ = jsonArray.getJSONObject(0);

						String custID = CustomerOBJ.getString("id");
						String googleProfileImage = CustomerOBJ.getString("google");
						String custName = CustomerOBJ.getString("name");
						String custCode = CustomerOBJ.getString("code");

						editor.putString("image_url", googleProfileImage);
						editor.putString("customer_id", custID);
						editor.putString("customerCode", custCode);
						editor.putString("idd", custID);
						editor.putString("name", "" + custName);
						editor.commit();

						Log.e("####image url", googleProfileImage);

						System.out.println(editor);
					}catch (Exception e) {
						Log.e("####exception", e.toString());
					}

					//Call Next Activity
					Intent intent=new Intent(LoginActivity.this,MainActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
					startActivity(intent);
					finish();


				}
				else
				{
					//Perform Registration
					//String jsonResponse=params[1];
					//Log.e("Json Object : ",""+jsonResponse);
					try
					{
						String personPhotoUrl="";
						if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
							Person currentPerson = Plus.PeopleApi
									.getCurrentPerson(mGoogleApiClient);
							 g_name = currentPerson.getDisplayName();


							personPhotoUrl = currentPerson.getImage().getUrl();
							String personGooglePlusProfile = currentPerson.getUrl();
							System.out.println("Profile :: "+personGooglePlusProfile );
							g_email = Plus.AccountApi.getAccountName(mGoogleApiClient);
							System.out.println("Gender :: "+currentPerson.getGender());
							editor.putString("name", g_name);
							editor.putString("image_url", personPhotoUrl);
							
						}



					//finallyPostData(main);

						HttpClient httpClient = new DefaultHttpClient();
						HttpPost httpPost = new HttpPost("http://admin.trainpanda.com/api/customers");
						List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
						//nameValuePair.add(new BasicNameValuePair("code",String.valueOf(code)));
						nameValuePair.add(new BasicNameValuePair("name",g_name));
						nameValuePair.add(new BasicNameValuePair("age","0" ));
						nameValuePair.add(new BasicNameValuePair("sex","Nil"));
						nameValuePair.add(new BasicNameValuePair("email",g_email));
						nameValuePair.add(new BasicNameValuePair("address","Unavailable"));

						//saving the profile image in this field
						nameValuePair.add(new BasicNameValuePair("google",personPhotoUrl));

						nameValuePair.add(new BasicNameValuePair("[city][name]","Unavailable"));
						nameValuePair.add(new BasicNameValuePair("[state][name]","Unavailable"));
						nameValuePair.add(new BasicNameValuePair("phone","0"));
						nameValuePair.add(new BasicNameValuePair("pinCode","0"));
						nameValuePair.add(new BasicNameValuePair("loginType","Google Plus"));
						//nameValuePair.add(new BasicNameValuePair("addedOn","2015-04-09T15:33:03.559Z"));
						//nameValuePair.add(new BasicNameValuePair("updatedOn","2015-03-10T15:33:03.559Z"));
						httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
						
						HttpResponse response = httpClient.execute(httpPost);

						// write response to log
						if (response.getStatusLine().getStatusCode() == 200)
						{
							HttpEntity entity = response.getEntity();
							String json = EntityUtils.toString(entity);
							Log.e("*****Http Post Response:"+response.getStatusLine().getStatusCode(),json);
							//Toast.makeText(getApplicationContext(), ""+json,Toast.LENGTH_SHORT).show();
							String customer_id=getCustomerId(json);
							System.out.println("ID "+customer_id);
							String customer_code=getCustomerCode(json);
							System.out.println("code "+customer_code);

							//Save Google Id to Shared Preference
							saveIdToSP(customer_id,customer_code);

							//Call Next Activity
							/*Intent intent=new Intent(LoginActivity.this,MainActivity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
							startActivity(intent);*/
							Intent intent=new Intent(LoginActivity.this,MainActivity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
							//intent.putExtra("customer_id",customer_id);
							startActivity(intent);
							finish();
						}
						else
						{
							Log.e("Server Error : "+response.getStatusLine().getStatusCode(),"Server Error");
						}

					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
				return null;
			}
			private String getCustomerCode(String json)
			{
				String id=null;
				try
				{
					JSONObject jsonObject=new JSONObject(json);
					id=jsonObject.getString("code");
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				return id;
			}

			private String MYhitServer(String url)
			{
				int length=0;
				String response="";
				try
				{
					ServiceHandler handler=new ServiceHandler();
					response=handler.makeServiceCall(url, ServiceHandler.GET);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				return response;
			}



			private void saveIdToSP(String customer_id,String customerCode) 
			{
								
				
				editor.putString("customer_id",customer_id);
				editor.putString("customerCode",customerCode);
				editor.putString("idd", id);
				editor.commit();

				System.out.println(editor);
			}

			private String getCustomerId(String json) 
			{
				String id=null;
				try
				{
					JSONObject jsonObject=new JSONObject(json);
					id=jsonObject.getString("id");
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				return id;
			}

			private int getRandomNumberForCode(int i)
			{
				Random random=new Random();
				return random.nextInt(i);
			}

			private int hitGPServer(String url)
			{
				int length=0;
				try
				{
					ServiceHandler handler=new ServiceHandler();
					String response=handler.makeServiceCall(url, ServiceHandler.GET);
					JSONArray jsonArray=new JSONArray(response);
					length=jsonArray.length();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				return length;
			}
			
		}

}
