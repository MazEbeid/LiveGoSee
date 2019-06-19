package mebeidcreations.apps.livegosee;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DrawerListItemAdapter extends ArrayAdapter<DrawerListItem> {

    Context context;
    int layoutResourceId;
    DrawerListItem data[] = null;

    public DrawerListItemAdapter(Context context, int layoutResourceId, DrawerListItem[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ListItemHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new ListItemHolder();
           holder.imgIcon = (ImageView)row.findViewById(R.id.list_item_image);
            holder.txtTitle = (TextView)row.findViewById(R.id.item_id);

            row.setTag(holder);
        }
        else
        {
            holder = (ListItemHolder) row.getTag();
        }

        DrawerListItem item = data[position];
        holder.txtTitle.setText(item.title);
      holder.imgIcon.setImageResource(item.icon);

        return row;
    }

    static class ListItemHolder
    {
        ImageView imgIcon;
        TextView txtTitle;
    }
}