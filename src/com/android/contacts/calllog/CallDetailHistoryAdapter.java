/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.contacts.calllog;

import com.android.contacts.PhoneCallDetails;
import com.android.contacts.R;

import android.content.Context;
import android.provider.CallLog.Calls;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Adapter for a ListView containing history items from the details of a call.
 */
public class CallDetailHistoryAdapter extends BaseAdapter {
    private final Context mContext;
    private final LayoutInflater mLayoutInflater;
    private final CallTypeHelper mCallTypeHelper;
    private final PhoneCallDetails[] mPhoneCallDetails;

    public CallDetailHistoryAdapter(Context context, LayoutInflater layoutInflater,
            CallTypeHelper callTypeHelper, PhoneCallDetails[] phoneCallDetails) {
        mContext = context;
        mLayoutInflater = layoutInflater;
        mCallTypeHelper = callTypeHelper;
        mPhoneCallDetails = phoneCallDetails;
    }

    @Override
    public int getCount() {
        return mPhoneCallDetails.length;
    }

    @Override
    public Object getItem(int position) {
        return mPhoneCallDetails[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Make sure we have a valid convertView to start with
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.call_detail_history_item, parent, false);
        }

        PhoneCallDetails details = mPhoneCallDetails[position];
        FrameLayout callTypeIconView = (FrameLayout) convertView.findViewById(R.id.call_type_icon);
        TextView callTypeTextView = (TextView) convertView.findViewById(R.id.call_type_text);
        TextView dateView = (TextView) convertView.findViewById(R.id.date);
        TextView durationView = (TextView) convertView.findViewById(R.id.duration);

        int callType = details.callTypes[0];
        callTypeIconView.removeAllViews();
        mCallTypeHelper.inflateCallTypeIcon(callType, callTypeIconView);
        callTypeTextView.setText(mCallTypeHelper.getCallTypeText(callType));
        // Set the date.
        CharSequence dateValue = DateUtils.formatDateRange(mContext, details.date, details.date,
                DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE |
                DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_SHOW_YEAR);
        dateView.setText(dateValue);
        // Set the duration
        if (callType == Calls.MISSED_TYPE || callType == Calls.VOICEMAIL_TYPE) {
            durationView.setVisibility(View.GONE);
        } else {
            durationView.setVisibility(View.VISIBLE);
            durationView.setText(formatDuration(details.duration));
        }

        return convertView;
    }

    private String formatDuration(long elapsedSeconds) {
        long minutes = 0;
        long seconds = 0;

        if (elapsedSeconds >= 60) {
            minutes = elapsedSeconds / 60;
            elapsedSeconds -= minutes * 60;
        }
        seconds = elapsedSeconds;

        return mContext.getString(R.string.callDetailsDurationFormat, minutes, seconds);
    }
}
