package com.lucabarbara.awcamera.ui.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;

import com.bm.library.PhotoView;
import com.lucabarbara.awcamera.R;
import com.lucabarbara.awcamera.ui.activity.AwCamera;
import com.lucabarbara.awcamera.ui.adapter.GalleryAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by luca1897 on 23/06/17.
 */

public class GalleryFragment extends Fragment {

    private GridView mGridGallery;
    private PhotoView mImageParallaxHeader;

    private GalleryAdapter galleryAdapter;
    private List<String> images = new ArrayList<>();

    private static final String ARG_SECTION_NUMBER = "section_number";

    private AwCamera awCamera;

    private final int RESULT_LOAD_IMAGE_FROM_GALLERY = 2;

    private Uri pathImageSelected = null;

    public GalleryFragment() {
    }

    public static GalleryFragment newInstance(int sectionNumber) {
        GalleryFragment fragment = new GalleryFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_gallery, container, false);

        mGridGallery = (GridView) rootView.findViewById(R.id.grid_gallery);
        mImageParallaxHeader = (PhotoView) rootView.findViewById(R.id.parallax_header_imageview);
        mImageParallaxHeader.enable();

        awCamera = (AwCamera) getActivity();


        initGridGallery();

        return rootView;
    }

    public void initGridGallery()
    {
        ViewCompat.setNestedScrollingEnabled(mGridGallery,true);

        galleryAdapter = new GalleryAdapter(this, images);
        try {
            setImagesOnAdapter();
        }catch(SecurityException e){
            awCamera.requestPermissionExternalStorage();
        }

        mGridGallery.setAdapter(galleryAdapter);
    }

    public void setImagesOnAdapter()
    {
        images.clear();
        images.add("ic_gallery");
        images.addAll(getCameraImages());
        if(images.size()>1)
        {
            galleryAdapter.setIndexImageSelected(1);
            setImageSelected(Uri.parse(images.get(1)));
        }
        galleryAdapter.notifyDataSetChanged();
    }

    public void setImageParallaxHeader(String image)
    {
        Picasso.with(getActivity()).load(image).fit().centerCrop()
                .into(mImageParallaxHeader);
    }

    /**
     * find last picture from gallery
     */
    public List<String> getCameraImages() {
        List<String> imgs = new ArrayList<>();
        String[] projection = new String[]{
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DATE_TAKEN,
                MediaStore.Images.ImageColumns.MIME_TYPE
        };
        final Cursor cursor = getActivity().getContentResolver()
                .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null,
                        null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");

        int i = 0;
        if(cursor.moveToFirst()) {
            final int dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            do {
                imgs.add("file:" + cursor.getString(dataColumn));
                i++;
            } while (cursor.moveToNext() && i < 30);
        }
        return imgs;
    }

    public void startIntentGallery() {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE_FROM_GALLERY);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_LOAD_IMAGE_FROM_GALLERY:
                if (data != null) {
                    setImageSelected(data.getData());
                    GalleryAdapter.setIndexImageSelected(-1);
                    galleryAdapter.notifyDataSetChanged();
                }
                break;
            default:
                break;
        }
    }

    public Uri getPathImageSelected() {
        return pathImageSelected;
    }

    public void setImageSelected(Uri pathImageSelected) {
        this.pathImageSelected = pathImageSelected;
        setImageParallaxHeader(pathImageSelected.toString());
    }

}
