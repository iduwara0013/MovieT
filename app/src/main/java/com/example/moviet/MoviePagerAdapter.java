package com.example.moviet;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

/**
 * Adapter for ViewPager2 to display a list of movies using MovieDetailFragment.
 */
public class MoviePagerAdapter extends FragmentStateAdapter {

    private final List<Movie> movieList;

    /**
     * Constructor.
     * @param fragmentActivity The hosting FragmentActivity.
     * @param movies List of Movie objects to display.
     */
    public MoviePagerAdapter(@NonNull FragmentActivity fragmentActivity, List<Movie> movies) {
        super(fragmentActivity);
        this.movieList = movies;
    }

    /**
     * Creates a new fragment for the movie at the given position.
     * @param position The position in the list.
     * @return A new MovieDetailFragment instance.
     */
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return MovieDetailFragment.newInstance(movieList.get(position));
    }

    /**
     * Returns the total number of movies.
     * @return The size of the movie list.
     */
    @Override
    public int getItemCount() {
        return movieList.size();
    }
}
