package com.mobileoptima.tarkieform;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.codepan.callback.Interface.OnFragmentCallback;
import com.codepan.database.SQLiteAdapter;
import com.codepan.widget.CodePanLabel;
import com.mobileoptima.adapter.OptionsAdapter;
import com.mobileoptima.callback.Interface.OnOptionSelectedCallback;
import com.mobileoptima.object.ChoiceObj;

import java.util.ArrayList;

public class OptionsFragment extends Fragment implements OnClickListener {

	private OnOptionSelectedCallback optionSelectedCallback;
	private OnFragmentCallback fragmentCallback;
	private CodePanLabel tvTitleOptions;
	private ArrayList<ChoiceObj> items;
	private RelativeLayout rlOptions;
	private OptionsAdapter adapter;
	private ListView lvOptions;
	private SQLiteAdapter db;
	private String title;

	@Override
	public void onStart() {
		super.onStart();
		setOnBackStack(true);
	}

	@Override
	public void onStop() {
		super.onStop();
		setOnBackStack(false);
	}

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
		lvOptions = (ListView) view.findViewById(R.id.lvOptions);
		tvTitleOptions = (CodePanLabel) view.findViewById(R.id.tvTitleOptions);
		tvTitleOptions.setText(title);
		rlOptions.setOnClickListener(this);
		if(items != null) {
			adapter = new OptionsAdapter(getActivity(), items);
			lvOptions.setAdapter(adapter);
			lvOptions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
					ChoiceObj choice = items.get(i);
					if(optionSelectedCallback != null) {
						optionSelectedCallback.onOptionSelected(choice);
					}
					getActivity().getSupportFragmentManager().popBackStack();
				}
			});
		}
		return view;
	}

	public void setItems(ArrayList<ChoiceObj> options, String title) {
		this.items = options;
		this.title = title;
	}

	public void setOnOptionSelectedCallback(OnOptionSelectedCallback optionSelectedCallback) {
		this.optionSelectedCallback = optionSelectedCallback;
	}

	public void setOnFragmentCallback(OnFragmentCallback fragmentCallback) {
		this.fragmentCallback = fragmentCallback;
	}

	public void setOnBackStack(boolean isOnBackStack) {
		if(fragmentCallback != null) {
			fragmentCallback.onFragment(isOnBackStack);
		}
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
