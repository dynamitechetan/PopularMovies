package dynamitechetan.nanodegree.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MovieDetailFragment())
                    .commit();
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }


    public static class MovieDetailFragment extends Fragment {

        public MovieDetailFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

            Intent onStartDetailActivity = getActivity().getIntent();
            if(onStartDetailActivity != null && onStartDetailActivity.hasExtra( getString(R.string.serializable_movie) ))
            {
                Movie extraMovie = (Movie) onStartDetailActivity.getSerializableExtra(getString(R.string.serializable_movie));

                TextView title = (TextView) rootView.findViewById(R.id.movie_title);
                title.setText( extraMovie.getOriginalTitle() );

                TextView releaseDate = (TextView) rootView.findViewById(R.id.release_date);
                releaseDate.setText( extraMovie.getReleaseDate().substring(0,4) ); // only year

                TextView userRating = (TextView) rootView.findViewById(R.id.rating);
                userRating.setText( extraMovie.getUserRating() + "/10" );

                TextView plotSynopsis = (TextView) rootView.findViewById(R.id.plot_synopsis);
                plotSynopsis.setText( extraMovie.getPlotSynopsis());

                ImageView poster = (ImageView) rootView.findViewById(R.id.poster);
                Picasso.with( getActivity() ).load( extraMovie.getImageUrl() ).into( poster );


            }


            return rootView;
        }
    }
}