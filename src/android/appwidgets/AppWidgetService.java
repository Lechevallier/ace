//-------------------------------------------------------------------------------------------------------
// Copyright (C) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE.txt file in the project root for full license information.
//-------------------------------------------------------------------------------------------------------
package run.ace;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.graphics.Color;
import org.json.*;

public class AppWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new SampleRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}

class SampleRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    Context _context;
    int _appWidgetId;
    int _itemResourceId;
    int _viewResourceId;
    int _itemTextResourceId;
    int _itemImageResourceId;
    int _itemLayoutResourceId;
    int _itemBackgroundResourceId;
    int _itemBackground2ResourceId;
    int _itemBackground3ResourceId;
    int _itemBackground4ResourceId;

    public SampleRemoteViewsFactory(Context context, Intent intent) {
        _context = context;
        _appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        _itemResourceId = intent.getIntExtra("widgetItemId", -1);
        _viewResourceId = intent.getIntExtra("widgetViewId", -1);
        _itemTextResourceId = intent.getIntExtra("widgetItemTextId", -1);
        _itemImageResourceId = intent.getIntExtra("widgetItemImageId", -1);
        _itemLayoutResourceId = intent.getIntExtra("widgetItemLayoutId", -1);
        _itemBackgroundResourceId = intent.getIntExtra("widgetItemBackgroundId", -1);
        _itemBackground2ResourceId = intent.getIntExtra("widgetItemBackground2Id", -1);
        _itemBackground3ResourceId = intent.getIntExtra("widgetItemBackground3Id", -1);
        _itemBackground4ResourceId = intent.getIntExtra("widgetItemBackground4Id", -1);
    }

    public void onCreate() {
        /*try {
            JSONObject actionData = new JSONObject();
            actionData.put("name", "Open the app to populate the widget");
            actionData.put("target", "none");
            actionData.put("state", 0);
    		AppWidgetData.add(actionData.toString(), _context);
        } catch (JSONException e) {
            System.out.println(e);
        }*/
		// TODO: Should we refresh data on app resume in some cases?
    }

    public void onDestroy() {
        AppWidgetData.clear();
    }

    public int getCount() {
        return AppWidgetData.getCount();
    }

    public RemoteViews getViewAt(int position) {
        RemoteViews rv = new RemoteViews(_context.getPackageName(), _itemLayoutResourceId);
        String action = AppWidgetData.getAction(position);
        String cible = AppWidgetData.getCible(position);
        int state = AppWidgetData.getState(position);

        int backgroundId =  _itemBackgroundResourceId;
        if(action.equals("light")){
            backgroundId = (state == 0) ? _itemBackgroundResourceId : _itemBackground2ResourceId;
        }else if(action.equals("gate")){
            backgroundId = (state == 0) ? _itemBackground3ResourceId : _itemBackground4ResourceId;
        }
        rv.setInt(_itemImageResourceId, "setImageResource", backgroundId);
        int color = (state == 0) ? Color.argb(255, 255, 200, 60) : Color.argb(255, 0, 0, 0);
        rv.setInt(_itemImageResourceId, "setColorFilter",  color);
        rv.setTextViewText(_itemTextResourceId, cible);

        // Bundle extras = new Bundle();
        // extras.putInt(AppWidgetProvider.EXTRA_ITEM, position);
        Intent fillInIntent = new Intent();
        fillInIntent.putExtra("position", position);
        fillInIntent.putExtra("action", action);
        fillInIntent.putExtra("cible", cible);
        fillInIntent.putExtra("state", state);
        fillInIntent.putExtra("appWidgetId", _appWidgetId);
        fillInIntent.putExtra("viewResourceId", _viewResourceId);
        rv.setOnClickFillInIntent(_itemResourceId, fillInIntent);

        return rv;
    }

    public RemoteViews getLoadingView() {
		// A custom loading view
        return null;
    }

    public int getViewTypeCount() {
        return 1;
    }

    public long getItemId(int position) {
        return position;
    }

    public boolean hasStableIds() {
        return true;
    }

    public void onDataSetChanged() {
        // For when AppWidgetManager.notifyAppWidgetViewDataChanged is called
    }
}
