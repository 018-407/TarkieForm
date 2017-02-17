package com.mobileoptima.tarkieform;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.codepan.database.SQLiteAdapter;
import com.codepan.widget.CodePanLabel;
import com.mobileoptima.callback.Interface.OnOptionSelectedCallback;
import com.mobileoptima.object.OptionObj;

import java.util.ArrayList;

public class OptionsFragment extends Fragment implements OnClickListener {

	private OnOptionSelectedCallback optionSelectedCallback;
	private CodePanLabel tvTitleOptions;
	private ArrayList<OptionObj> options;
	private RelativeLayout rlOptions;
	private LinearLayout llOptions;
	private SQLiteAdapter db;
	private String title;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		db = ((MainActivity) getActivity()).getDatabase();
		db.openConnection();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.options_layout, container, false);
		rlOptions = (RelativeLayout) view.findViewById(R.id.rlOptions);
		llOptions = (LinearLayout) view.findViewById(R.id.llOptions);
		tvTitleOptions = (CodePanLabel) view.findViewById(R.id.tvTitleOptions);
		tvTitleOptions.setText(title);
		rlOptions.setOnClickListener(this);
		if(options != null) {
			for(final OptionObj obj : options) {
				View v = inflater.inflate(R.layout.options_list_row, container, false);
				CodePanLabel tvItemOptions = (CodePanLabel) v.findViewById(R.id.tvItemOptions);
				View vDividerOptions = v.findViewById(R.id.vDividerOptions);
				tvItemOptions.setText(obj.dDesc);
				tvItemOptions.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						getActivity().getSupportFragmentManager().popBackStack();
						if(optionSelectedCallback != null) {
							optionSelectedCallback.onOptionSelected(obj);
						}
					}
				});
				if(options.indexOf(obj) == options.size() - 1) {
					vDividerOptions.setVisibility(View.GONE);
					tvItemOptions.setBackgroundResource(R.drawable.state_rect_trans_rad_five_bot);
				}
				llOptions.addView(v);
			}
		}
		return view;
	}

	public void setItems(ArrayList<OptionObj> options, String title) {
		this.options = options;
		this.title = title;
	}

	public void setOnOptionSelectedCallback(OnOptionSelectedCallback optionSelectedCallback) {
		this.optionSelectedCallback = optionSelectedCallback;
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.rlOptions:
				getActivity().getSupportFragmentManager().popBackStack();
				break;
		}
	}
}
