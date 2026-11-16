package com.example.tagihanairpdam;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

public class InformationAdapter extends ArrayAdapter<InformationModel>{

    public InformationAdapter(Context context, List<InformationModel> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        InformationModel item = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_information, parent, false);
        }

        TextView tvTitle = convertView.findViewById(R.id.tvTitle);
        TextView tvMessage = convertView.findViewById(R.id.tvMessage);
//        TextView tvAuthor = convertView.findViewById(R.id.tvAuthor);
        TextView tvDate = convertView.findViewById(R.id.tvDate);

        tvTitle.setText(item.getTitle());
        tvMessage.setText(item.getMessage());
//        tvAuthor.setText("Oleh: " + item.getCreatedBy());
        tvDate.setText(item.getCreatedAt());

        return convertView;
    }
}