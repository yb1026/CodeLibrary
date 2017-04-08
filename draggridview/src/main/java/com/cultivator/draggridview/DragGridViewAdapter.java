package com.cultivator.draggridview;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.List;

public class DragGridViewAdapter extends BaseAdapter implements DragAdapterInterface {


    private List<HomeGridItem> list;
    private Context context;

    public DragGridViewAdapter(Context context) {
        this.context = context;
    }

    public void setlist(List<HomeGridItem> list) {
        this.list = list;
    }

    public List<HomeGridItem> getList(){
        return list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            holder = new Holder();
            Resources resources = context.getResources();
            String packageName = context.getPackageName();
            convertView = LayoutInflater.from(context).inflate(resources.
                    getIdentifier("view_gridview_item", "layout", packageName), null);
            holder.deleteImg = (ImageView) convertView.findViewById(resources.
                    getIdentifier("delete_img", "id", packageName));
            holder.iconImg = (ImageView) convertView.findViewById(resources.
                    getIdentifier("icon_img", "id", packageName));
            holder.nameTv = (TextView) convertView.findViewById(resources.
                    getIdentifier("name_tv", "id", packageName));
            holder.container = convertView.findViewById(resources.
                    getIdentifier("item_container", "id", packageName));

            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        HomeGridItem item = list.get(position);


        if (item.getMipmapId() == 0) {
            return convertView;
        }
        holder.deleteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.remove(position);
                notifyDataSetChanged();
            }
        });
        holder.deleteImg.setVisibility(View.GONE);
        holder.iconImg.setImageResource(item.getMipmapId());
        holder.nameTv.setText(item.getName());
//        holder.container.setBackgroundColor(Color.WHITE);
        return convertView;
    }

    class Holder {
        public ImageView deleteImg;
        public ImageView iconImg;
        public TextView nameTv;
        public View container;
    }

    @Override
    public void reOrder(int startPosition, int endPosition) {
        if (endPosition < list.size()) {
            Object object = list.remove(startPosition);
            list.add(endPosition, (HomeGridItem) object);
            notifyDataSetChanged();
        }
    }
}
