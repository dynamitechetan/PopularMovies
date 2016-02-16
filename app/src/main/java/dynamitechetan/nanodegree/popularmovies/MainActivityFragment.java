package dynamitechetan.nanodegree.popularmovies;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class MainActivityFragment extends Fragment {

    private static final String LOG_TAG = "MainActivityFragment";

    public MainActivityFragment() {
    }


    @Override
    public void onStart() {
        super.onStart();

        updateData();

    }

    @Override
    public void onResume() {
        super.onResume();
        updateData();

    }


    private void setAdapter( final ArrayList<Movie> movieList )
    {
        GridView gridview;
        if ( getView() != null ) {



            gridview = (GridView) getView().findViewById(R.id.gridview);

            gridview.setAdapter(new ImageAdapter(getActivity(), movieList));

            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                    Context context = MainActivityFragment.this.getActivity();
                    Intent startMovieDetailActivity = new Intent(context, DetailActivity.class);
                    Movie extraMovie = movieList.get(position);
                    startMovieDetailActivity.putExtra( getString(R.string.serializable_movie), extraMovie);
                    MainActivityFragment.this.startActivity(startMovieDetailActivity);
                }
            });
        }
    }


    private View mContentView;
    private View mLoadingView;
    private int mShortAnimationDuration;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mContentView = rootView.findViewById(R.id.gridview);
        mLoadingView = rootView.findViewById(R.id.loading_spinner);

        mShortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);


        return rootView;
    }

    private static String LAST_TEST_ORDER = "";
    public void updateData()
    {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String ORDER = sharedPref.getString(getString(R.string.sort_order_key), getString(R.string.order_def_value));

        /**
         * Update data only if order type changed
         */
//        if( orderChanged( ORDER ) )
//        {
            LAST_TEST_ORDER = ORDER;

            FetchMovieDataTask fetchMovieDataTask = new FetchMovieDataTask();
            fetchMovieDataTask.execute( ORDER );
//        }

    }

    private boolean orderChanged(String order) {
        return order != null && !order.equals(LAST_TEST_ORDER);
    }


    public class FetchMovieDataTask extends AsyncTask<String, Void, ArrayList<Movie> >
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MainActivityFragment.this.showLoadingIndicator();
        }

        @Override
        protected void onPostExecute( ArrayList<Movie> movieList ) {
            MainActivityFragment.this.crossfade();
            MainActivityFragment.this.setAdapter( movieList );

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        public FetchMovieDataTask() {
            super();
        }

        @Override
        protected ArrayList<Movie> doInBackground(String... params) {

            String sortOrder;
            if(params.length != 0)
            {
                sortOrder = params[0];
            }
            else
            {
                return null;
            }

            String apiRequest =  buildApiRequest( sortOrder );


            String moviesJsonString = fetchJSONData(apiRequest);
            if(moviesJsonString != null) {
                try {
                    return Movie.createMovieListFromJson(moviesJsonString);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


            return null;
        }




        private String buildApiRequest(String sortOrder)
        {
            final String MOVIEDB_BASE_URL = "http://api.themoviedb.org/3/movie/";
            Uri.Builder apiRequest = Uri.parse(MOVIEDB_BASE_URL).buildUpon();

            apiRequest.appendEncodedPath( sortOrder );

            final String KEY_PARAM = "api_key";
            final String API_KEY = getString( R.string.api_key );
            apiRequest.appendQueryParameter(KEY_PARAM, API_KEY);

            apiRequest.build();

            return apiRequest.toString();
        }

        private String fetchJSONData(String apiRequest)
        {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String moviesJsonString = null;

            try {
                URL url = new URL( apiRequest );
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                if (inputStream == null) {
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));


                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }

                moviesJsonString = buffer.toString();
            }
            catch (IOException e) {
                Log.e(LOG_TAG, " IOException!", e);
                e.printStackTrace();
            }
            finally {

                if (urlConnection != null) {
                    urlConnection.disconnect();
                }

                if (reader != null) {
                    try {
                        reader.close();
                    }
                    catch (IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            return moviesJsonString;
        }
    }


    private void crossfade() {

        mContentView.setAlpha(0f);
        mContentView.setVisibility(View.VISIBLE);

        mContentView.animate()
                .alpha(1f)
                .setDuration(mShortAnimationDuration)
                .setListener(null);


        mLoadingView.animate()
                .alpha(0f)
                .setDuration(mShortAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mLoadingView.setVisibility(View.GONE);
                    }
                });
    }

    private void showLoadingIndicator(){
        mContentView.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.VISIBLE);
    }


}
