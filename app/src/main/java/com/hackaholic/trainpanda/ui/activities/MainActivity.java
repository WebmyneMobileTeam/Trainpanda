package com.hackaholic.trainpanda.ui.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hackaholic.trainpanda.R;
import com.hackaholic.trainpanda.adapter.ListAdapter;
import com.hackaholic.trainpanda.ui.fragments.BookHotel;
import com.hackaholic.trainpanda.ui.fragments.FareEnquiry;
import com.hackaholic.trainpanda.ui.fragments.MainFragment;
import com.hackaholic.trainpanda.ui.fragments.MyFoodOrder;
import com.hackaholic.trainpanda.ui.fragments.MySearchedPNR;
import com.hackaholic.trainpanda.ui.fragments.OrderFood;
import com.hackaholic.trainpanda.ui.fragments.PNRFragment;
import com.hackaholic.trainpanda.ui.fragments.RunningStatus;
import com.hackaholic.trainpanda.ui.fragments.SearchTrain;
import com.hackaholic.trainpanda.ui.fragments.SeatAvailability;
import com.hackaholic.trainpanda.ui.fragments.StationInfo;
import com.hackaholic.trainpanda.ui.fragments.TrainArraiving;
import com.hackaholic.trainpanda.utils.SlidingMenuLayout;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import android.util.Log;

public class MainActivity extends FragmentActivity {
    ListView Listview, more_Listview;
    static SlidingMenuLayout slidingmenu_layout;
    Context context;
    Button button_back;
    TextView title,txtprofileName;
    Button lk_profile_menu;

    private boolean doubleBackToExitPressedOnce;

    String[] web = {
            "HOME",
            "ORDER FOOD",
            "BOOK HOTEL",
            "STATION INFO",
            "MY SEARCHED PNR",
            "MY FOOD ORDER",
            "MORE"
    };

    String[] web2 = {
            "FARE ENQUIRY",
            "SEAT AVAILABILITY",
            "RUNNING STATUS",
            "TRAIN ARRIVING",
            "SEARCH TRAIN",

    };

    Integer[] imageId2 = {
            R.drawable.fare,
            R.drawable.seat,
            R.drawable.status,
            R.drawable.train_arriving,
            R.drawable.search_train,

    };

    Integer[] imageId = {
            R.drawable.home,
            R.drawable.order_food,
            R.drawable.book_hotel,
            R.drawable.info,
            R.drawable.search,
            R.drawable.food_menu,
            R.drawable.more

    };
    private TextView lk_profile_header_textview;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        slidingmenu_layout = (SlidingMenuLayout) this.getLayoutInflater().inflate(R.layout.activity_main, null);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(slidingmenu_layout);
        //setContentView(R.layout.activity_main);
        context = this;

        CircleImageView leftNavLogoImageView = (CircleImageView)findViewById(R.id.leftNavLogoImageView);


        txtprofileName = (TextView) findViewById(R.id.txtprofileName);
        title = (TextView) findViewById(R.id.lk_profile_header_textview);
        title.setText("TRAIN PANDA");

        sharedPreferences = getSharedPreferences("TrainPanda",MODE_PRIVATE);
        System.out.println(sharedPreferences.getString("name", "no name"));



        txtprofileName.setText(sharedPreferences.getString("name", "no name"));

        try {
            String url = sharedPreferences.getString("image_url", null).toString();
            System.out.println("Url Profile_image " + url);
            String id = sharedPreferences.getString("idd", "not found").toString();

            Picasso.with(getBaseContext()).load(url).placeholder(R.drawable.a6).into(leftNavLogoImageView);
        }catch (Exception e){
            Log.e("exc in profile image",e.toString());
        }

        button_back = (Button) findViewById(R.id.more_back_button);
        button_back.setVisibility(View.INVISIBLE);



        PNRFragment fragment = new PNRFragment();
        start_fragment(fragment);



        lk_profile_menu = (Button) findViewById(R.id.button_menu);
        lk_profile_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show/hide the menu
                Listview.smoothScrollToPosition(0);
                toggleMenu(v);
            }
        });

        Listview = (ListView) findViewById(R.id.list);
        Listview.setVisibility(View.VISIBLE);
        //Listview.setDividerHeight(2);
        Listview.setClickable(true);
        //Listview.setBackgroundColor(Color.GRAY);
        ListAdapter adapter = new ListAdapter(MainActivity.this, web, imageId);
        Listview.setAdapter(adapter);

        Listview.setOnItemClickListener(mainListview);

        button_back.setOnClickListener(buttonClick);
    }






    AdapterView.OnItemClickListener mainListview = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            //Toast.makeText(MainActivity.this, "You Clicked at " + web[+i], Toast.LENGTH_SHORT).show();

            switch (i) {

                case 0:
                    toggleMenu(view);
                    title.setText("TRAIN PANDA");
                    PNRFragment home = new PNRFragment();
                    start_fragment(home);
                    //Toast.makeText(MainActivity.this, "You Clicked at " + web[+i], Toast.LENGTH_SHORT).show();
                    break;

                case 1:
                    toggleMenu(view);
                    title.setText("ORDER FOOD");
                    OrderFood orderFood = new OrderFood();
                    start_fragment(orderFood);
                    //Toast.makeText(MainActivity.this, "You Clicked at " + web[+i], Toast.LENGTH_SHORT).show();
                    break;

                case 2:
                    toggleMenu(view);
                    title.setText("BOOK HOTEL");
                    BookHotel bookHotel = new BookHotel();
                    start_fragment(bookHotel);
                    //Toast.makeText(MainActivity.this, "You Clicked at " + web[+i], Toast.LENGTH_SHORT).show();
                    break;

                case 3:
                    toggleMenu(view);
                    title.setText("STATION INFO");
                    StationInfo stationInfo = new StationInfo();
                    start_fragment(stationInfo);
                    //Toast.makeText(MainActivity.this, "You Clicked at " + web[+i], Toast.LENGTH_SHORT).show();
                    break;

                case 4:
                    toggleMenu(view);
                    //Toast.makeText(MainActivity.this, "You Clicked at " + web[+i], Toast.LENGTH_SHORT).show();
                    title.setText("MY SEARCHED PNR");
                    MySearchedPNR mySearchedPNR = new MySearchedPNR();
                    start_fragment(mySearchedPNR);
                    break;

                case 5:
                    toggleMenu(view);
                    title.setText("MY FOOD ORDER");
                    MyFoodOrder myFoodOrder = new MyFoodOrder();
                    start_fragment(myFoodOrder);
                    //Toast.makeText(MainActivity.this, "You Clicked at " + web[+i], Toast.LENGTH_SHORT).show();
                    break;

                case 6:
                    //Toast.makeText(MainActivity.this, "You Clicked at " + web[+i], Toast.LENGTH_SHORT).show();
                    button_back.setVisibility(View.VISIBLE);
                    more_Listview = (ListView) findViewById(R.id.list);
                    //Listview.setDividerHeight(2);
                    more_Listview.setClickable(true);
                    //Listview.setBackgroundColor(Color.GRAY);
                    ListAdapter adapter = new ListAdapter(MainActivity.this, web2, imageId2);
                    more_Listview.setAdapter(adapter);

                    more_Listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            //Toast.makeText(MainActivity.this, "You Clicked at " + web2[+i], Toast.LENGTH_SHORT).show();
                            //toggleMenu(view);

                            switch (i) {

                                case 0:
                                    toggleMenu(view);
                                    title.setText("FARE ENQUIRY");
                                    FareEnquiry fareEnquiry = new FareEnquiry();
                                    start_fragment(fareEnquiry);
                                    //Toast.makeText(MainActivity.this, "You Clicked at " + web[+i], Toast.LENGTH_SHORT).show();
                                    break;

                                case 1:
                                    toggleMenu(view);
                                    title.setText("SEAT AVAILABILITY");
                                    SeatAvailability seatAvailability = new SeatAvailability();
                                    start_fragment(seatAvailability);
                                    //Toast.makeText(MainActivity.this, "You Clicked at " + web[+i], Toast.LENGTH_SHORT).show();
                                    break;

                                case 2:
                                    toggleMenu(view);
                                    title.setText("RUNNING STATUS");
                                    RunningStatus runningStatus = new RunningStatus();
                                    start_fragment(runningStatus);
                                    //Toast.makeText(MainActivity.this, "You Clicked at " + web[+i], Toast.LENGTH_SHORT).show();
                                    break;

                                case 3:
                                    toggleMenu(view);
                                    title.setText("TRAIN ARRIVING");
                                    TrainArraiving trainArraiving = new TrainArraiving();
                                    start_fragment(trainArraiving);
                                    //Toast.makeText(MainActivity.this, "You Clicked at " + web[+i], Toast.LENGTH_SHORT).show();
                                    break;

                                case 4:
                                    toggleMenu(view);
                                    //Toast.makeText(MainActivity.this, "You Clicked at " + web[+i], Toast.LENGTH_SHORT).show();
                                    title.setText("SEARCH TRAIN");
                                    SearchTrain searchTrain = new SearchTrain();
                                    start_fragment(searchTrain);
                                    break;
                            }
                        }
                    });
                    break;
            }

        }
    };

    View.OnClickListener buttonClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            Listview = (ListView) findViewById(R.id.list);
            Listview.setVisibility(View.VISIBLE);
            //Listview.setDividerHeight(2);
            Listview.setClickable(true);
            //Listview.setBackgroundColor(Color.GRAY);
            ListAdapter adapter = new ListAdapter(MainActivity.this, web, imageId);
            Listview.setAdapter(adapter);
            button_back.setVisibility(View.INVISIBLE);
            Listview.setOnItemClickListener(mainListview);
        }
    };

    public static void toggleMenu(View v) {
        slidingmenu_layout.toggleMenu();
    }

    private void start_fragment(Fragment frag) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.lk_profile_fragment, frag);
        //fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            finish();
            return;
        } else {

            title.setText("TRAIN PANDA");
            Fragment fragment = new PNRFragment();
            start_fragment(fragment);
        }

        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        this.doubleBackToExitPressedOnce = true;


        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

}
