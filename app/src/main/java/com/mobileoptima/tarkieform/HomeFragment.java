package com.mobileoptima.tarkieform;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.codepan.database.SQLiteAdapter;
import com.codepan.widget.CodePanTextField;
import com.mobileoptima.adapter.FormAdapter;
import com.mobileoptima.callback.Interface.OnOverrideCallback;
import com.mobileoptima.constant.Tag;
import com.mobileoptima.core.Data;
import com.mobileoptima.object.FormObj;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

	private final long IDLE_TIME = 500;
	private OnOverrideCallback overrideCallback;
	private FragmentTransaction transaction;
	private CodePanTextField etSearchHome;
	private ArrayList<FormObj> formList;
	private Handler inputFinishHandler;
	private FormAdapter adapter;
	private SQLiteAdapter db;
	private ListView lvHome;
	private String search;
	private long lastEdit;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		inputFinishHandler = new Handler();
		db = ((MainActivity) getActivity()).getDatabase();
		db.openConnection();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.home_layout, container, false);
		etSearchHome = (CodePanTextField) view.findViewById(R.id.etSearchHome);
		lvHome = (ListView) view.findViewById(R.id.lvHome);
		lvHome.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				FormObj obj = formList.get(i);
				FormFragment form = new FormFragment();
				form.setForm(obj);
				form.setOnOverrideCallback(overrideCallback);
				transaction = getActivity().getSupportFragmentManager().beginTransaction();
				transaction.setCustomAnimations(R.anim.slide_in_rtl, R.anim.slide_out_rtl,
						R.anim.slide_in_ltr, R.anim.slide_out_ltr);
				transaction.replace(R.id.rlMain, form, Tag.FORM);
				transaction.addToBackStack(null);
				transaction.commit();
			}
		});
		etSearchHome.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				search = s.toString();
				lastEdit = System.currentTimeMillis();
				inputFinishHandler.removeCallbacks(inputFinishChecker);
				inputFinishHandler.postDelayed(inputFinishChecker, IDLE_TIME);
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		loadForms(db);
		return view;
	}

	public void loadForms(final SQLiteAdapter db) {
		Thread bg = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					formList = Data.loadForms(db, search);
					handler.sendMessage(handler.obtainMessage());
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		bg.start();
	}

	Handler handler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message message) {
			adapter = new FormAdapter(getActivity(), formList);
			lvHome.setAdapter(adapter);
			return true;
		}
	});

	public void setOnOverrideCallback(OnOverrideCallback overrideCallback) {
		this.overrideCallback = overrideCallback;
	}

	private Runnable inputFinishChecker = new Runnable() {
		@Override
		public void run() {
			long current = System.currentTimeMillis();
			if(current > lastEdit + IDLE_TIME - 500) {
				loadForms(db);
			}
		}
	};
}
