# AwCamera
Instagram-like photo browser
## Screen
![alt text](http://www.lucabarbara.com/awcamera/screen3.png)
## Features

## Docs
### Methods:
```java
// enables/disables gallery tab. (default: true)
AwCamera.setGalleryEnabled(true); 
// enables/disables photo tab. (default: true)
AwCamera.setPhotoEnabled(true);   

// set default flash mode (default: 0)
//0 = OFF
//1 = ON
//2 = AUTO
AwCamera.setDefaultFlashMode(0); 

// set default camera type (default: 0)
//0 = BACK
//1 = FRONT
AwCamera.setDefaultCameraMode(0); 

```
### Example:
```java
    //[...]
    private final int RESULT_IMAGE = 0;
    //[...]

    Intent i = new Intent(MainActivity.this,AwCamera.class);
    startActivityForResult(i, RESULT_IMAGE);

    //[...]

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
```

## Thanks to
#### CameraKit-Android: https://github.com/gogopop/CameraKit-Android

## License
AwCamera is released under the MIT license.
