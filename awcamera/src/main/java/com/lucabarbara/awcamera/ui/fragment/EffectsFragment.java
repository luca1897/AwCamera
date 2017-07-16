package com.lucabarbara.awcamera.ui.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bm.library.PhotoView;
import com.flurgle.camerakit.CameraView;
import com.lucabarbara.awcamera.R;
import com.lucabarbara.awcamera.model.ThumbnailItem;
import com.lucabarbara.awcamera.ui.activity.AwCamera;
import com.lucabarbara.awcamera.ui.adapter.FiltersAdapter;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.zomato.photofilters.SampleFilters;
import com.zomato.photofilters.imageprocessors.Filter;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * Created by luca1897 on 23/06/17.
 */

public class EffectsFragment extends Fragment {

    static
    {
        System.loadLibrary("NativeImageProcessor");
    }

    private static final String ARG_SECTION_NUMBER = "section_number";
    private PhotoView mImageView;
    private RecyclerView mRecyclerView;
    private FiltersAdapter filtersAdapter;
    private ArrayList<ThumbnailItem> images = new ArrayList<>();
    private AwCamera awCamera;
    private Bitmap originaBitmap;

    public EffectsFragment() {
    }

    public static EffectsFragment newInstance(int sectionNumber) {
        EffectsFragment fragment = new EffectsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_effects, container, false);
        awCamera = (AwCamera)getActivity();

        mImageView = (PhotoView) rootView.findViewById(R.id.image_view);
        mRecyclerView = (RecyclerView)rootView.findViewById(R.id.recyclerView);



        filtersAdapter = new FiltersAdapter(this,images);

        LinearLayoutManager llFilters = new LinearLayoutManager(rootView.getContext());
        llFilters.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(llFilters);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(filtersAdapter);


        return rootView;
    }

    private void bindDataToAdapter(final Bitmap bmp) {
        Handler handler = new Handler();
        Runnable r = new Runnable() {
            public void run() {

                ThumbnailItem t1 = new ThumbnailItem("Original",bmp,null);
                ThumbnailItem t2 = new ThumbnailItem("Star Lit", bmp,SampleFilters.getStarLitFilter());
                ThumbnailItem t3 = new ThumbnailItem("Blue Mess", bmp,SampleFilters.getBlueMessFilter());
                ThumbnailItem t4 = new ThumbnailItem("Awe Struck Vibe", bmp,SampleFilters.getAweStruckVibeFilter());
                ThumbnailItem t5 = new ThumbnailItem("Lime Stutter", bmp, SampleFilters.getLimeStutterFilter());
                ThumbnailItem t6 = new ThumbnailItem("Night Wisper", bmp,SampleFilters.getNightWhisperFilter());

                images.clear();
                images.add(t1); // Original Image
                images.add(t2);
                images.add(t3);
                images.add(t4);
                images.add(t5);
                images.add(t6);

                filtersAdapter.notifyDataSetChanged();
            }
        };
        handler.post(r);
    }

    public void setImage(Bitmap bmp) {
        mImageView.setImageBitmap(bmp);
    }

    public String saveImage()
    {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        Date now = new Date();
        String fileName = "IMG_" + formatter.format(now) + ".png";

        final File file = new File(
                awCamera.getGalleryPath(),
                fileName
        );
        String path = awCamera.getImageSelected();
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inMutable = true;
        Bitmap bmp = BitmapFactory.decodeFile(Uri.parse(path).getPath(),opts);
        Filter fSel = filtersAdapter.getFilterSelected();
        if(fSel != null)
        {
            bmp = fSel.processFilter(bmp);
        }
        saveBmpToFile(file,bmp);

        return file.getAbsolutePath();
    }

    private void saveBmpToFile(File file, Bitmap bmp)
    {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
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

    public void initUI() {
        String path = awCamera.getImageSelected();
        path = Uri.parse(path).getPath();
        if(path != null) {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inMutable = true;
            opts.inJustDecodeBounds = false;



            originaBitmap = BitmapFactory.decodeFile(path,opts);
            opts.inSampleSize = calculateInSampleSize(opts,300,300);
            originaBitmap = BitmapFactory.decodeFile(path,opts);


            try {
                ExifInterface exif = new ExifInterface(path);
                int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                int rotationInDegrees = exifToDegrees(rotation);
                Matrix matrix = new Matrix();
                if (rotation != 0f) {
                    matrix.preRotate(rotationInDegrees);
                    originaBitmap = Bitmap.createBitmap(originaBitmap,0,0,opts.outWidth,opts.outHeight,matrix,true);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            setImage(originaBitmap);
            bindDataToAdapter(originaBitmap);
        }

    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {  return 180; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {  return 270; }
        return 0;
    }
}