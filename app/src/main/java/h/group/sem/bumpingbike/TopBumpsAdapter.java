package h.group.sem.bumpingbike;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by maria on 13/11/2017.
 */


public class TopBumpsAdapter extends BaseAdapter
{
    ArrayList<Position> positionList;
    Context mContext;

    public TopBumpsAdapter(Context context, ArrayList<Position> positionArrayList) {
        this.positionList = positionArrayList;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return positionList.size();
    }
    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Position pos = positionList.get(position);

        convertView = LayoutInflater.from(mContext).inflate(R.layout.activity_top_bumps_row_layout,null);

        TextView latitudeTextView = (TextView) convertView.findViewById(R.id.latitude);
        TextView longitudeTextView = (TextView) convertView.findViewById(R.id.longitude);
        TextView countTextView = (TextView) convertView.findViewById(R.id.count);

        latitudeTextView.setText(String.valueOf(pos.getLatitude()));
        longitudeTextView.setText(String.valueOf(pos.getLongitude()));
        countTextView.setText("Count: 0");


        return convertView;
    }
}