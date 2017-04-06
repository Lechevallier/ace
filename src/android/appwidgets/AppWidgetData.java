//-------------------------------------------------------------------------------------------------------
// Copyright (C) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE.txt file in the project root for full license information.
//-------------------------------------------------------------------------------------------------------
package run.ace;

import android.appwidget.AppWidgetManager;
import java.util.ArrayList;
import org.json.*;

public class AppWidgetData {
    static ArrayList<String> _actions = new ArrayList<String>(); //TODO be lazy
    static ArrayList<String> _cibles = new ArrayList<String>(); //TODO be lazy
    static ArrayList<Integer> _states = new ArrayList<Integer>(); //TODO be lazy
    static ArrayList<UpdateEntry> _entries = new ArrayList<UpdateEntry>(); //TODO be lazy

	public static void add(String actionData, android.content.Context context) {
		try {
			JSONObject actionDataJson = new JSONObject(actionData);
			_actions.add(actionDataJson.getString("name"));
			_cibles.add(actionDataJson.getString("target"));
			_states.add(actionDataJson.getInt("state"));
		} catch (JSONException e) {
				System.out.println(e);
		}
        AppWidgetManager mgr = AppWidgetManager.getInstance(context);
		for (int i = 0; i < _entries.size(); i++) {
			mgr.notifyAppWidgetViewDataChanged(_entries.get(i).widgetId, _entries.get(i).viewId);
		}
	}

	public static void addWidget(int widgetId, int viewId) {
		_entries.add(new UpdateEntry(widgetId, viewId));
	}

	public static String getAction(int index) {
		if (index >= _actions.size())
			return "";
		else
			return _actions.get(index);
	}

	public static String getCible(int index) {
		if (index >= _cibles.size())
			return "";
		else
			return _cibles.get(index);
	}

	public static Integer getState(int index) {
		if (index >= _states.size())
			return 0;
		else
			return _states.get(index);
	}

	public static void changeState(int index) {
		if (_states.get(index) == 0)
			_states.set(index, 1);
		else
			_states.set(index, 0);
	}

	public static void clear() {
		_actions.clear();
	}

	public static int getCount() {
		if (_actions == null)
			return 0;
		else
			return _actions.size();
	}
}

class UpdateEntry {
	public UpdateEntry(int widgetId, int viewId) {
		this.widgetId = widgetId;
		this.viewId = viewId;
	}

	public int widgetId;
	public int viewId;
}
