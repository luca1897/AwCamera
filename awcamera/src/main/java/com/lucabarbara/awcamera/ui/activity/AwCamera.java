package com.lucabarbara.awcamera.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.flurgle.camerakit.CameraKit;
import com.lucabarbara.awcamera.R;
import com.lucabarbara.awcamera.ui.adapter.HomePagerAdapter;

import java.util.ArrayList;

public class AwCamera extends AppCompatActivity {


    private HomePagerAdapter mPagerAdapter;

    public final int REQUEST_PERMISSION_EXTERNAL_STORAGE = 0;
    public final int REQUEST_PERMISSION_CAMERA = 16;

    private final String galleryPath = Environment.getExternalStorageDirectory() + "/" + android.os.Environment.DIRECTORY_DCIM;

    private PAGE oldPage = PAGE.Gallery;

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private TextView mToolbarTitle;
    private ImageButton mContinue;
    private ImageButton mBack;

    public enum PAGE {
        Gallery,
        Photo,
        Effects
    }

    private static boolean PHOTO_ENABLED = true;
    private static boolean GALLERY_ENABLED = true;
    private static boolean EFFECTS_ENABLED = true;


    private static int DEFAULT_FLASH_MODE = 0;
    private static int DEFAULT_CAMERA_MODE = 0;

    private ArrayList<PAGE> listTabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aw_camera);

        listTabs = new ArrayList<>();
        if(GALLERY_ENABLED)
            listTabs.add(PAGE.Gallery);
        if(PHOTO_ENABLED)
            listTabs.add(PAGE.Photo);
        if(EFFECTS_ENABLED)
            listTabs.add(PAGE.Effects);


        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mViewPager = (ViewPager) findViewById(R.id.container);
        mContinue = (ImageButton) findViewById(R.id.toolbar_continue);
        mBack = (ImageButton) findViewById(R.id.toolbar_back);
        mToolbarTitle = (TextView) findViewById(R.id.toolbar_title);

        mPagerAdapter = new HomePagerAdapter(getSupportFragmentManager(), listTabs);
        //setSupportActionBar(mToolbar);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);

        mViewPager.setAdapter(mPagerAdapter);

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mToolbarTitle.setText(mPagerAdapter.getPageTitle(position));
                String path = getImageSelected();
                if(path == null)
                {
                    setContinueButtonVisibility(View.GONE);
                }else{
                    setContinueButtonVisibility(View.VISIBLE);
                }

                if(position==1)
                {
                    startCamera();
                }
                else {
                    stopCamera();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        mContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldPage = PAGE.values()[mViewPager.getCurrentItem()];
                if(oldPage != PAGE.Effects)
                {
                    showEffectsFragment();
                }else{
                    String path = "file:" + mPagerAdapter.mEffectsFragment.saveImage();
                    setActivityResult(path);
                }
            }
        });

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
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
        if(filename == null)
            return;
        //Toast.makeText(this,filename,Toast.LENGTH_SHORT).show();
        Intent data = new Intent();
        data.setData(Uri.parse(filename));
        setResult(Activity.RESULT_OK, data);
        finish();
    }

    @Override
    public void onBackPressed() {
        if(mViewPager.getCurrentItem() == PAGE.Effects.ordinal())
        {
            mViewPager.setCurrentItem(oldPage.ordinal(),false);
            mTabLayout.setVisibility(View.VISIBLE);
        }else{
            super.onBackPressed();
        }
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
        }else if(requestCode == REQUEST_PERMISSION_CAMERA)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (mPagerAdapter.mPhotoFragment != null)
                    mPagerAdapter.mPhotoFragment.startCamera();
            } else {
                // User refused to grant permission.
            }
        }
    }

    private void showEffectsFragment()
    {
        mViewPager.setCurrentItem(2,false);
        mTabLayout.setVisibility(View.GONE);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if(mPagerAdapter.mEffectsFragment != null)
                mPagerAdapter.mEffectsFragment.initUI();
            }
        }, 150);
    }


    private void startCamera()
    {
        if(mPagerAdapter.mPhotoFragment != null)
            mPagerAdapter.mPhotoFragment.startCamera();
    }

    private void stopCamera()
    {
        if(mPagerAdapter.mPhotoFragment != null)
            mPagerAdapter.mPhotoFragment.stopCamera();
    }


    public String getGalleryPath() {
        return galleryPath;
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

    public static int getDefaultFlashMode() {
        return DEFAULT_FLASH_MODE;
    }

    public static void setDefaultFlashMode(int defaultFlashMode) {
        DEFAULT_FLASH_MODE = defaultFlashMode;
    }

    public static int getDefaultCameraMode() {
        return DEFAULT_CAMERA_MODE;
    }

    public static void setDefaultCameraMode(int defaultCameraMode) {
        DEFAULT_CAMERA_MODE = defaultCameraMode;
    }
}
