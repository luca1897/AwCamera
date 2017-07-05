package com.lucabarbara.awcamera.ui.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bm.library.PhotoView;
import com.flurgle.camerakit.CameraKit;
import com.flurgle.camerakit.CameraListener;
import com.flurgle.camerakit.CameraView;
import com.lucabarbara.awcamera.R;
import com.lucabarbara.awcamera.ui.activity.AwCamera;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by luca1897 on 23/06/17.
 */

public class PhotoFragment extends Fragment {

    private AwCamera awCamera;


    private CameraView mCameraView;
    private ImageButton mTakePhoto;
    private ImageButton mSwitchCamera;
    private ImageButton mToggleFlash;
    private PhotoView mImageView;

    private Uri pathImage = null;


    private static final String ARG_SECTION_NUMBER = "section_number";

    public PhotoFragment() {
    }

    public static PhotoFragment newInstance(int sectionNumber) {
        PhotoFragment fragment = new PhotoFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_photo, container, false);

        mCameraView = (CameraView)rootView.findViewById(R.id.camera);
        mTakePhoto = (ImageButton)rootView.findViewById(R.id.btn_take_photo);
        mSwitchCamera = (ImageButton) rootView.findViewById(R.id.btn_switch_camera);
        mToggleFlash = (ImageButton) rootView.findViewById(R.id.btn_toggle_flash);
        mImageView = (PhotoView)rootView.findViewById(R.id.image_view);
        mImageView.enable();

        awCamera = (AwCamera)getActivity();

        initCamera();
        return rootView;
    }

    public void initCamera()
    {
        mCameraView.setCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(byte[] picture) {
                super.onPictureTaken(picture);
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
                Date now = new Date();
                String fileName = "IMG_" + formatter.format(now) + ".png";

                final File file = new File(
                        awCamera.getGalleryPath(),
                        fileName
                );

                Bitmap result = BitmapFactory.decodeByteArray(picture, 0, picture.length);

                saveBitmapToPng(result,file);

                pathImage = Uri.fromFile(file);
                Picasso.with(getActivity()).load(file).fit().centerCrop()
                        .into(mImageView);
                mCameraView.setVisibility(View.INVISIBLE);
                mSwitchCamera.setVisibility(View.GONE);
                mToggleFlash.setVisibility(View.GONE);
                awCamera.setContinueButtonVisibility(View.VISIBLE);
            }
        });

        mTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCameraView.captureImage();
            }
        });

        mToggleFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFlash();
            }
        });

        mSwitchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchCamera();
            }
        });

        mCameraView.start();
        mCameraView.setFlash(CameraKit.Constants.FLASH_OFF);
    }

    private void saveBitmapToPng(Bitmap bmp, File file)
    {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void switchCamera() {
        if(mCameraView != null)
        {
            int f = mCameraView.toggleFacing();
            if(f == CameraKit.Constants.FACING_BACK)
            {
                mSwitchCamera.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_camera_rear_white));
            }else{
                mSwitchCamera.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_camera_front_white));
            }
        }
    }

    private void toggleFlash()
    {
        if(mCameraView != null)
        {
            int f = mCameraView.toggleFlash();
            if(f == CameraKit.Constants.FLASH_OFF)
            {
                mToggleFlash.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_flash_off_white));
            }else if(f == CameraKit.Constants.FLASH_ON)
            {
                mToggleFlash.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_flash_on_white));
            }else if(f == CameraKit.Constants.FLASH_AUTO)
            {
                mToggleFlash.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_flash_auto_white));
            }else{
                mToggleFlash.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_flash_on_white));
            }
        }
    }

    public void setCameraViewVisibility(int visible) {
        if(mCameraView != null)
            mCameraView.setVisibility(visible);
    }


    public Uri getPathImage() {
        return pathImage;
    }

    public void startCamera()
    {
        if(mCameraView != null)
        {
            mCameraView.start();
        }
    }

    public void stopCamera()
    {
        if(mCameraView != null)
        {
            mCameraView.stop();
        }
    }

}
