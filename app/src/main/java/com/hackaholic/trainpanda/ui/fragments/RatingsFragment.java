package com.hackaholic.trainpanda.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.hackaholic.trainpanda.R;


public class RatingsFragment extends Fragment
{
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		View row=inflater.inflate(R.layout.ratings_fragment, container,false);
		
		/*final TextView text = (TextView) row.findViewById(R.id.textView);

		final RatingBar ratingBar_default = (RatingBar)row.findViewById(R.id.ratingbar_default);

		final Button button = (Button)row.findViewById(R.id.button1);
		button.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{ 
				ratingBar_default.setRating(5);
				text.setText("Rating: "+ String.valueOf(ratingBar_default.getRating()));
			}
		});

		ratingBar_default.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener(){
			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating,
					boolean fromUser)
			{
				text.setText("Rating: "+ String.valueOf(rating));
			}}); 

		*/
		return row;
	}
}
