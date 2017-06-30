package com.lucabarbara.awcamera.ui.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.lucabarbara.awcamera.R;
import com.lucabarbara.awcamera.ui.activity.AwCamera;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.fotoapparat.Fotoapparat;
import io.fotoapparat.FotoapparatSwitcher;
import io.fotoapparat.parameter.LensPosition;
import io.fotoapparat.preview.Frame;
import io.fotoapparat.preview.FrameProcessor;
import io.fotoapparat.result.PendingResult;
import io.fotoapparat.result.PhotoResult;
import io.fotoapparat.view.CameraView;

import static io.fotoapparat.log.Loggers.fileLogger;
import static io.fotoapparat.log.Loggers.logcat;
import static io.fotoapparat.log.Loggers.loggers;
import static io.fotoapparat.parameter.selector.AspectRatioSelectors.standardRatio;
import static io.fotoapparat.parameter.selector.FlashSelectors.autoFlash;
import static io.fotoapparat.parameter.selector.FlashSelectors.autoRedEye;
import static io.fotoapparat.parameter.selector.FlashSelectors.off;
import static io.fotoapparat.parameter.selector.FlashSelectors.torch;
import static io.fotoapparat.parameter.selector.FocusModeSelectors.autoFocus;
import static io.fotoapparat.parameter.selector.FocusModeSelectors.continuousFocus;
import static io.fotoapparat.parameter.selector.FocusModeSelectors.fixed;
import static io.fotoapparat.parameter.selector.LensPositionSelectors.lensPosition;
import static io.fotoapparat.parameter.selector.Selectors.firstAvailable;
import static io.fotoapparat.parameter.selector.SizeSelectors.biggestSize;

/**
 * Created by luca1897 on 23/06/17.
 */

public class PhotoFragment extends Fragment {

    private AwCamera awCamera;


    private CameraView mCameraView;
    private ImageButton mTakePhoto;
    private ImageButton mSwitchCamera;
    private ImageView mImageView;

    private Uri pathImage = null;
    private Fotoapparat frontFotoapparat;
    private Fotoapparat backFotoapparat;
    public FotoapparatSwitcher fotoapparatSwitcher;


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

        mCameraView = (CameraView)rootView.findViewById(R.id.camera_view);
        mTakePhoto = (ImageButton)rootView.findViewById(R.id.btn_take_photo);
        mSwitchCamera = (ImageButton) rootView.findViewById(R.id.btn_switch_camera);
        mImageView = (ImageView)rootView.findViewById(R.id.image_view);


        awCamera = (AwCamera)getActivity();

        initCamera();
        return rootView;
    }

    public void initCamera()
    {
        if(awCamera.getCameraPermissionGranted())
        {
            mCameraView.setVisibility(View.VISIBLE);
        }else{
            return;
        }

        frontFotoapparat = createFotoapparat(LensPosition.FRONT);
        backFotoapparat = createFotoapparat(LensPosition.BACK);
        fotoapparatSwitcher = FotoapparatSwitcher.withDefault(backFotoapparat);

        mTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!fotoapparatSwitcher.getCurrentFotoapparat().isAvailable())
                    return;
                PhotoResult photoResult = fotoapparatSwitcher.getCurrentFotoapparat().takePicture();

                SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
                Date now = new Date();
                String fileName = "IMG_" + formatter.format(now) + ".jpg";

                final File file = new File(
                        awCamera.getGalleryPath(),
                        fileName
                );
                photoResult.saveToFile(file).whenAvailable(new PendingResult.Callback<Void>() {
                    @Override
                    public void onResult(Void v) {
                        pathImage = Uri.fromFile(file);
                        Picasso.with(getActivity()).load(file).fit().centerCrop()
                                .into(mImageView);
                        mCameraView.setVisibility(View.INVISIBLE);
                        mSwitchCamera.setVisibility(View.GONE);
                        fotoapparatSwitcher.stop();
                        awCamera.setContinueButtonVisibility(View.VISIBLE);
                    }
                });
            }
        });

        mSwitchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchCamera();
            }
        });

        fotoapparatSwitcher.start();

    }

    private Fotoapparat createFotoapparat(LensPosition position) {
        return Fotoapparat
                .with(awCamera)
                .into(mCameraView)
                .photoSize(standardRatio(biggestSize()))
                .lensPosition(lensPosition(position))
                .focusMode(firstAvailable(
                        continuousFocus(),
                        autoFocus(),
                        fixed()
                ))
                .flash(firstAvailable(
                        autoRedEye(),
                        autoFlash(),
                        torch(),
                        off()
                ))
                .frameProcessor(new SampleFrameProcessor())
                .logger(loggers(
                        logcat(),
                        fileLogger(awCamera)
                ))
                .build();
    }

    private void switchCamera() {
        if (fotoapparatSwitcher.getCurrentFotoapparat() == frontFotoapparat) {
            fotoapparatSwitcher.switchTo(backFotoapparat);
        } else {
            fotoapparatSwitcher.switchTo(frontFotoapparat);
        }
    }

    public void setCameraViewVisibility(int visible) {
        if(mCameraView != null)
            mCameraView.setVisibility(visible);
    }

    public void startPhotoapparatSwitcher()
    {
        if(fotoapparatSwitcher != null)
            fotoapparatSwitcher.start();
    }

    public Uri getPathImage() {
        return pathImage;
    }

    private class SampleFrameProcessor implements FrameProcessor {

        @Override
        public void processFrame(Frame frame) {
            // Perform frame processing, if needed
        }

    }
}
