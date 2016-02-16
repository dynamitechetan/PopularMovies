package dynamitechetan.nanodegree.popularmovies;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Movie> movieList;

    public ImageAdapter(Context c, ArrayList<Movie> movieList) {
        mContext = c;
        this.movieList = movieList;
    }

    public int getCount() {
        return movieList.size();
    }

    public Movie getItem(int position) {
        return movieList.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null)
        {
            Resources r = Resources.getSystem();
            float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, r.getDisplayMetrics());

            imageView = new ImageView(mContext);
//            imageView.setLayoutParams(new GridView.LayoutParams(250, 400));
//            imageView.setLayoutParams(new GridView.LayoutParams((int)px, (int)px));
            imageView.setLayoutParams(new GridView.LayoutParams(
                    (int)mContext.getResources().getDimension(R.dimen.width),
                    (int)mContext.getResources().getDimension(R.dimen.height)));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        } else
        {
            imageView = (ImageView) convertView;
        }

        String url = getItem(position).getImageUrl();
        Picasso.with(mContext).load(url).into(imageView);
        return imageView;
    }
}