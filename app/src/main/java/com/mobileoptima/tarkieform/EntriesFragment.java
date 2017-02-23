package com.mobileoptima.tarkieform;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.codepan.database.SQLiteAdapter;
import com.mobileoptima.adapter.EntriesAdapter;
import com.mobileoptima.callback.Interface;
import com.mobileoptima.constant.Tag;
import com.mobileoptima.core.Data;
import com.mobileoptima.object.EntryObj;

import java.util.ArrayList;

public class EntriesFragment extends Fragment {

	private Interface.OnOverrideCallback overrideCallback;
	private FragmentTransaction transaction;
	private ArrayList<EntryObj> entryList;
	private EntriesAdapter adapter;
	private ListView lvEntries;
	private SQLiteAdapter db;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		db = ((MainActivity) getActivity()).getDatabase();
		db.openConnection();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.entries_layout, container, false);
		lvEntries = (ListView) view.findViewById(R.id.lvEntries);
		lvEntries.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				EntryObj obj = entryList.get(i);
				FormFragment form = new FormFragment();
				form.setEntry(obj);
				form.setOnOverrideCallback(overrideCallback);
				transaction = getActivity().getSupportFragmentManager().beginTransaction();
				transaction.setCustomAnimations(R.anim.slide_in_rtl, R.anim.slide_out_rtl,
						R.anim.slide_in_ltr, R.anim.slide_out_ltr);
				transaction.replace(R.id.rlMain, form, Tag.FORM);
				transaction.addToBackStack(null);
				transaction.commit();
			}
		});
		loadEntries(db);
		return view;
	}

	public void loadEntries(final SQLiteAdapter db) {
		Thread bg = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					entryList = Data.loadEntries(db);
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
			adapter = new EntriesAdapter(getActivity(), entryList);
			lvEntries.setAdapter(adapter);
			return true;
		}
	});

	public void setOnOverrideCallback(Interface.OnOverrideCallback overrideCallback) {
		this.overrideCallback = overrideCallback;
	}
}
