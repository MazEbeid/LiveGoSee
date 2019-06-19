package mebeidcreations.apps.livegosee;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Admin on 11-12-2015.
 */



public class ExploreFragment extends Fragment {

    TabLayout.Tab main_explore_tab, friends_explore_tab;
    MyFragmentPagerAdapter fragmentStatelistAdapter;
    ViewPager mViewPager;
    TabLayout tabLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_explore, container, false);


        fragmentStatelistAdapter = new MyFragmentPagerAdapter(getFragmentManager());
        mViewPager = (ViewPager)rootView.findViewById(R.id.explore_fragment_pager);
        mViewPager.setAdapter(fragmentStatelistAdapter);



        tabLayout = (TabLayout)rootView.findViewById(R.id.explore_fragment_tabLL);
        tabLayout.setupWithViewPager(mViewPager);

        main_explore_tab = tabLayout.getTabAt(0);
        friends_explore_tab =  tabLayout.getTabAt(1);


        View view1 = getActivity().getLayoutInflater().inflate(R.layout.custom_tab_layout, null);
        view1.findViewById(R.id.icon).setBackgroundResource(R.drawable.main_feed_selector);
        main_explore_tab.setCustomView(view1);

        View view2 = getActivity().getLayoutInflater().inflate(R.layout.custom_tab_layout, null);
        view2.findViewById(R.id.icon).setBackgroundResource(R.drawable.explore_tab_selector);
        friends_explore_tab.setCustomView(view2);


        return rootView;
    }

    /////////////Fragment code
    public class MyFragmentPagerAdapter extends FragmentStatePagerAdapter {

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {


            if(position==1)
            {
             return    new ExploreInspirationsFragment();
            }
            else
            {
             return    new ExploreFriendsFragment();
            }



        }

        @Override
        public int getCount() {

            return 2;
        }

    }



    public static ExploreFragment newInstance(String title) {
        ExploreFragment frag = new ExploreFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d("MAIN_FEED", "PAUSED");
        mViewPager.setEnabled(false);

    }

    @Override
    public void onResume() {
        super.onResume();
        mViewPager.setEnabled(false);
        Log.d("MAIN_FEED", "RESUMED");
    }
}
