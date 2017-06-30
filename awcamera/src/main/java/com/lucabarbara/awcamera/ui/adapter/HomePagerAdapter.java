package com.lucabarbara.awcamera.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.lucabarbara.awcamera.ui.activity.AwCamera;
import com.lucabarbara.awcamera.ui.fragment.GalleryFragment;
import com.lucabarbara.awcamera.ui.fragment.PhotoFragment;
import com.lucabarbara.awcamera.ui.fragment.VideoFragment;

import java.util.ArrayList;

/**
 * Created by luca1897 on 23/06/17.
 */

public class HomePagerAdapter extends FragmentPagerAdapter {

    public GalleryFragment mGalleryFragment;
    public PhotoFragment mPhotoFragment;
    public VideoFragment mVideoFragment;
    private ArrayList<AwCamera.PAGE> listTabs;

    public HomePagerAdapter(FragmentManager fm,ArrayList<AwCamera.PAGE> listTabs) {

        super(fm);
        this.listTabs = listTabs;
        if(mGalleryFragment ==null)
            mGalleryFragment = GalleryFragment.newInstance(0);
        if(mPhotoFragment ==null)
            mPhotoFragment = PhotoFragment.newInstance(1);
        if(mVideoFragment ==null)
            mVideoFragment = VideoFragment.newInstance(2);
    }


    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        switch(listTabs.get(position))
        {
            case Gallery:
                if(mGalleryFragment ==null)
                    mGalleryFragment = GalleryFragment.newInstance(0);
                return mGalleryFragment;
            case Photo:
                if(mPhotoFragment==null)
                    mPhotoFragment = PhotoFragment.newInstance(1);
                return mPhotoFragment;
            case Video:
                if(mVideoFragment==null)
                    mVideoFragment = VideoFragment.newInstance(2);
                return mVideoFragment;
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return listTabs.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return listTabs.get(position).toString();
    }
}
