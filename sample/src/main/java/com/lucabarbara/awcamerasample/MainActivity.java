package com.lucabarbara.awcamerasample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.lucabarbara.awcamera.ui.activity.AwCamera;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private final int RESULT_IMAGE = 0;

    @BindView(R.id.imageView)
    ImageView mImageView;
    @BindView(R.id.button)
    Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //AwCamera.setGalleryEnabled(true);
                //AwCamera.setPhotoEnabled(true);
                //AwCamera.setVideoEnabled(false);

                Intent i = new Intent(MainActivity.this,AwCamera.class);
                startActivityForResult(i, RESULT_IMAGE);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_IMAGE :
                if (data != null) {
                    Picasso.with(this).load(data.getData()).fit().centerCrop().into(mImageView);
                }
                break;
            default:
                break;
        }
    }
}
