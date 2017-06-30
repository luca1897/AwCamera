package com.lucabarbara.awcamera.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.lucabarbara.awcamera.R;
import com.lucabarbara.awcamera.ui.adapter.HomePagerAdapter;
import com.lucabarbara.awcamera.utils.PermissionsDelegate;

import java.util.ArrayList;

public class AwCamera extends AppCompatActivity {


    private HomePagerAdapter mPagerAdapter;

    private final PermissionsDelegate permissionsDelegate = new PermissionsDelegate(this);
    public final int REQUEST_PERMISSION_EXTERNAL_STORAGE = 0;

    private final String galleryPath = Environment.getExternalStorageDirectory() + "/" + android.os.Environment.DIRECTORY_DCIM;



    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private TextView mToolbarTitle;
    private ImageButton mContinue;
    private ImageButton mBack;

    public enum PAGE {
        Gallery,
        Photo,
        Video
    }

    private static boolean PHOTO_ENABLED = true;
    private static boolean GALLERY_ENABLED = true;
    private static boolean VIDEO_ENABLED = true;

    private ArrayList<PAGE> listTabs;
    private boolean hasCameraPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aw_camera);

        listTabs = new ArrayList<>();
        if(GALLERY_ENABLED)
            listTabs.add(PAGE.Gallery);
        if(PHOTO_ENABLED)
            listTabs.add(PAGE.Photo);
        if(VIDEO_ENABLED)
            listTabs.add(PAGE.Video);



        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mViewPager = (ViewPager) findViewById(R.id.container);
        mContinue = (ImageButton) findViewById(R.id.toolbar_continue);
        mBack = (ImageButton) findViewById(R.id.toolbar_back);
        mToolbarTitle = (TextView) findViewById(R.id.toolbar_title);

        mPagerAdapter = new HomePagerAdapter(getSupportFragmentManager(), listTabs);
        //setSupportActionBar(mToolbar);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);

        mViewPager.setAdapter(mPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mToolbarTitle.setText(mPagerAdapter.getPageTitle(tab.getPosition()));
                String path = getImageSelected();
                if(path == null)
                {
                    setContinueButtonVisibility(View.GONE);
                }else{
                    setContinueButtonVisibility(View.VISIBLE);
                }

                if(tab.getPosition()==1)
                {
                    hasCameraPermission = getPermissionsDelegate().hasCameraPermission();
                    getPermissionsDelegate().requestCameraPermission();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        mContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = getImageSelected();
                if(path != null)
                    setActivityResult(path);
            }
        });

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public String getImageSelected()
    {
        Uri path = null;

        switch(listTabs.get(mTabLayout.getSelectedTabPosition()))
        {
            case Photo:
                path = mPagerAdapter.mPhotoFragment.getPathImage();
                break;
            case Gallery:
                path = mPagerAdapter.mGalleryFragment.getPathImageSelected();
                break;
            default:
                path = null;
                break;
        }
        if(path != null)
            return path.toString();
        return null;
    }

    public void setContinueButtonVisibility(final int visibility)
    {
        mContinue.setVisibility(visibility);
    }

    public void setActivityResult(String filename)
    {
        //Toast.makeText(this,filename,Toast.LENGTH_SHORT).show();
        Intent data = new Intent();
        data.setData(Uri.parse(filename));
        setResult(Activity.RESULT_OK, data);
        finish();
    }



    /**
     * check external storage and camera permission
     */
    public void requestPermissionExternalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_EXTERNAL_STORAGE);
            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (mPagerAdapter.mGalleryFragment != null)
                    mPagerAdapter.mGalleryFragment.initGridGallery();
            } else {
                // User refused to grant permission.
            }
        }else if (permissionsDelegate.resultGranted(requestCode, permissions, grantResults)) {
            if(mPagerAdapter.mPhotoFragment != null)
            {
                hasCameraPermission = true;
                mPagerAdapter.mPhotoFragment.initCamera();
            }
        }
    }


    public PermissionsDelegate getPermissionsDelegate()
    {
        return permissionsDelegate;
    }

    public String getGalleryPath() {
        return galleryPath;
    }

    public boolean getCameraPermissionGranted() {
        return hasCameraPermission;
    }

    public static boolean isPhotoEnabled() {
        return PHOTO_ENABLED;
    }

    public static void setPhotoEnabled(boolean photoEnabled) {
        PHOTO_ENABLED = photoEnabled;
    }

    public static boolean isGalleryEnabled() {
        return GALLERY_ENABLED;
    }

    public static void setGalleryEnabled(boolean galleryEnabled) {
        GALLERY_ENABLED = galleryEnabled;
    }

    public static boolean isVideoEnabled() {
        return VIDEO_ENABLED;
    }

    public static void setVideoEnabled(boolean videoEnabled) {
        VIDEO_ENABLED = videoEnabled;
    }
}
