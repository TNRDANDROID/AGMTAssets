package com.nic.AGMTAssets.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.nic.AGMTAssets.DataBase.DBHelper;
import com.nic.AGMTAssets.DataBase.dbData;
import com.nic.AGMTAssets.R;
import com.nic.AGMTAssets.Session.PrefManager;
import com.nic.AGMTAssets.Support.MyLocationListener;
import com.nic.AGMTAssets.Utils.CameraUtils;
import com.nic.AGMTAssets.Utils.FontCache;
import com.nic.AGMTAssets.Utils.Utils;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;

public class TakePhotoScreen extends AppCompatActivity {
    public static final int MEDIA_TYPE_IMAGE = 1;

    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 2500;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private static String imageStoragePath;
    public static final int BITMAP_SAMPLE_SIZE = 8;

    LocationManager mlocManager = null;
    LocationListener mlocListener;
    Double offlatTextValue, offlongTextValue;
    ImageView imageView, image_view_preview;
    TextView latitude_text, longtitude_text,text_start_end_middle;
    EditText myEditTextView;
    ScrollView scrollView;
    RelativeLayout mini_number_count_text_layout,maxi_number_count_text_layout;
    TextView mini_number_count_text,maxi_number_count_text;
    private List<View> viewArrayList = new ArrayList<>();

    String hab_code="";
    String hab_name="";
    String form_name="";
    String form_number="";
    String form_id="";
    String type_of_photos="";
    String no_of_photos="";
    String min_no_of_photos="";
    String max_no_of_photos="";
    String asset_id="";
    int last_position=0;

    public com.nic.AGMTAssets.DataBase.dbData dbData = new dbData(this);
    private PrefManager prefManager;
    private SQLiteDatabase db;
    public static DBHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo_screen);
        scrollView =findViewById(R.id.scroll_view);
        mini_number_count_text_layout =findViewById(R.id.mini_number_count_text_layout);
        maxi_number_count_text_layout =findViewById(R.id.maxi_number_count_text_layout);
        mini_number_count_text =findViewById(R.id.mini_number_count_text);
        maxi_number_count_text =findViewById(R.id.maxi_number_count_text);
        try {
            dbHelper = new DBHelper(this);
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }

        prefManager = new PrefManager(this);

        getIntentData();
        /*if(!type_of_photos.equals("2")) {
            imageWithDescription("", scrollView);
        }
        else {

        }
*/
        maxi_number_count_text_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                no_of_photos = max_no_of_photos;
                imageWithDescription("", scrollView);
            }
        });
        mini_number_count_text_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                no_of_photos = min_no_of_photos;
                imageWithDescription("", scrollView);
            }
        });
    }

    public void getIntentData(){
        hab_code = String.valueOf(Integer.parseInt(getIntent().getStringExtra("hab_code")));
        hab_name = String.valueOf(getIntent().getStringExtra("hab_name"));
        form_id = (getIntent().getStringExtra("form_id"));
        form_number = (getIntent().getStringExtra("form_number"));
        form_name = (getIntent().getStringExtra("form_name"));
        min_no_of_photos = (getIntent().getStringExtra("min_no_of_photos"));
        max_no_of_photos = (getIntent().getStringExtra("max_no_of_photos"));
        type_of_photos = (getIntent().getStringExtra("type_of_photos"));
        asset_id = (getIntent().getStringExtra("asset_id"));

        mini_number_count_text.setText(min_no_of_photos);
        maxi_number_count_text.setText(max_no_of_photos);
    }

    public void imageWithDescription(final String type, final ScrollView scrollView) {
        //imageboolean = true;
        //dataset = new JSONObject();

        final Dialog dialog = new Dialog(this,
                R.style.AppTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.add_photo);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.dimAmount = 0.7f;
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();


        final LinearLayout mobileNumberLayout = (LinearLayout) dialog.findViewById(R.id.mobile_number_layout);
        TextView cancel = (TextView) dialog.findViewById(R.id.close);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Button done = (Button) dialog.findViewById(R.id.btn_save_inspection);
        TextView tv_create_asset_title = (TextView) dialog.findViewById(R.id.tv_create_asset_title);
        tv_create_asset_title.setText("You Must Capture "+no_of_photos+" Photos");
        done.setGravity(Gravity.CENTER);
        done.setVisibility(View.VISIBLE);
        done.setTypeface(FontCache.getInstance(this).getFont(FontCache.Font.HEAVY));

        done.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(viewArrayList.size()==Integer.parseInt(no_of_photos)) {
                    JSONArray imageJson = new JSONArray();
                    long rowInserted = 0;
                    int childCount = mobileNumberLayout.getChildCount();
                    int count = 0;
                    int sl_no = 0;
                    if (childCount > 0) {
                        for (int i = 0; i < childCount; i++) {
                            JSONArray imageArray = new JSONArray();

                            View vv = mobileNumberLayout.getChildAt(i);
                            imageView = vv.findViewById(R.id.image_view);
                            myEditTextView = vv.findViewById(R.id.description);
                            latitude_text = vv.findViewById(R.id.latitude);
                            longtitude_text = vv.findViewById(R.id.longtitude);


                            if (imageView.getDrawable() != null) {
                                //if (!myEditTextView.getText().toString().equals("")) {
                                    count = count + 1;
                                    sl_no = sl_no + 1;
                                    byte[] imageInByte = new byte[0];
                                    String image_str = "";
                                    String description = "";
                                    try {
                                        description = myEditTextView.getText().toString();
                                        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
                                        imageInByte = baos.toByteArray();
                                        image_str = Base64.encodeToString(imageInByte, Base64.DEFAULT);

                                    } catch (Exception e) {
                                        //imageboolean = false;
                                        Utils.showAlert(TakePhotoScreen.this, getResources().getString(R.string.at_least_capture_one_photo));
                                        //e.printStackTrace();
                                    }

                                    if (MyLocationListener.latitude > 0) {
                                        offlatTextValue = MyLocationListener.latitude;
                                        offlongTextValue = MyLocationListener.longitude;
                                    }

                                    // Toast.makeText(getApplicationContext(),str,Toast.LENGTH_LONG).show();

                                    ContentValues imageValue = new ContentValues();

                                    imageValue.put("form_id", form_id);
                                    imageValue.put("asset_id", asset_id);
                                    imageValue.put("hab_code", hab_code);
                                    imageValue.put("sl_no", sl_no);
                                    imageValue.put("latitude", latitude_text.getText().toString());
                                    imageValue.put("longitude", longtitude_text.getText().toString());
                                    imageValue.put("photograph_remark", description);
                                    imageValue.put("image", image_str.trim());

                                    rowInserted = db.insert(DBHelper.AGMT_SAVE_IMAGE_TABLE, null, imageValue);

                                    if (count == childCount) {
                                        if (rowInserted > 0) {

                                            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                                            dialog.dismiss();
                                            Toast.makeText(TakePhotoScreen.this, getResources().getString(R.string.inserted_success), Toast.LENGTH_SHORT).show();
                                            //Toasty.success(TakePhotoScreen.this, getResources().getString(R.string.inserted_success), Toasty.LENGTH_SHORT);
                                            onBackPressed();
                                        }

                                    }

                                /*}
                            else {
                                    Utils.showAlert(TakePhotoScreen.this, getResources().getString(R.string.please_enter_description));
                                }*/
                            }
                            else {
                                Utils.showAlert(TakePhotoScreen.this, getResources().getString(R.string.please_capture_image));
                            }


                        }


                    }

                    focusOnView(scrollView);
                }
                else {
                    Utils.showAlert(TakePhotoScreen.this, "Take Required Number of Photos");
                }

            }
        });
        Button btnAddMobile = (Button) dialog.findViewById(R.id.btn_add);
        btnAddMobile.setTypeface(FontCache.getInstance(this).getFont(FontCache.Font.MEDIUM));
        viewArrayList.clear();
        btnAddMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewArrayList.size()<Integer.parseInt(no_of_photos)) {
                    if (imageView.getDrawable() != null && viewArrayList.size() > 0) {
                        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                        updateView(TakePhotoScreen.this, mobileNumberLayout, "", type);
                    } else {
                        Utils.showAlert(TakePhotoScreen.this, getResources().getString(R.string.first_capture_image_add_another));
                    }
                }
                else {
                    Utils.showAlert(TakePhotoScreen.this, getResources().getString(R.string.maximum_photos_reached));

                }
            }
        });
        updateView(this, mobileNumberLayout, "", type);

    }

    private final void focusOnView(final ScrollView your_scrollview) {
        your_scrollview.post(new Runnable() {
            @Override
            public void run() {
                your_scrollview.fullScroll(View.FOCUS_DOWN);
                //your_scrollview.scrollTo(0, your_EditBox.getY());
            }
        });
    }
    //Method for update single view based on email or mobile type
    public View updateView(final Activity activity, final LinearLayout emailOrMobileLayout, final String values, final String type) {
        final View hiddenInfo = activity.getLayoutInflater().inflate(R.layout.image_with_description, emailOrMobileLayout, false);
        final ImageView imageView_close = (ImageView) hiddenInfo.findViewById(R.id.imageView_close);
        imageView = (ImageView) hiddenInfo.findViewById(R.id.image_view);
        image_view_preview = (ImageView) hiddenInfo.findViewById(R.id.image_view_preview);
        myEditTextView = (EditText) hiddenInfo.findViewById(R.id.description);
        latitude_text = hiddenInfo.findViewById(R.id.latitude);
        longtitude_text = hiddenInfo.findViewById(R.id.longtitude);
        text_start_end_middle = hiddenInfo.findViewById(R.id.start_middle_end);

        if(type_of_photos.equals("2")) {
            imageView_close.setVisibility(View.GONE);
            text_start_end_middle.setVisibility(View.VISIBLE);
            if (0 == viewArrayList.size()) {
                text_start_end_middle.setBackgroundColor(getResources().getColor(R.color.subscription_type_red_color));
                text_start_end_middle.setText(getResources().getString(R.string.start));
            }
            else if(Integer.parseInt(no_of_photos)-1==viewArrayList.size()){
                text_start_end_middle.setBackgroundColor(getResources().getColor(R.color.green));
                text_start_end_middle.setText(getResources().getString(R.string.end));
            }
            else {
                text_start_end_middle.setBackgroundColor(getResources().getColor(R.color.primary_text_color));
                text_start_end_middle.setText(getResources().getString(R.string.middle));
            }
        }
        else {
            imageView_close.setVisibility(View.VISIBLE);
            text_start_end_middle.setVisibility(View.GONE);
            text_start_end_middle.setText("");
        }


        imageView_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    imageView.setVisibility(View.VISIBLE);
                    if (viewArrayList.size() != 1) {
                        ((LinearLayout) hiddenInfo.getParent()).removeView(hiddenInfo);
                        viewArrayList.remove(hiddenInfo);
                    }

                } catch (IndexOutOfBoundsException a) {
                    a.printStackTrace();
                }
            }
        });
        image_view_preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(type_of_photos.equals("2")){
                    if(viewArrayList.size()==0){
                        showAlert("1");
                    }
                    else if(viewArrayList.size()==Integer.parseInt(no_of_photos)){
                        showAlert("2");
                    }
                    else {
                        getLatLong();
                    }
                }


            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(type_of_photos.equals("2")) {
                    showAlert("3");
                }
                else {
                    getLatLong();
                }
            }
        });
        emailOrMobileLayout.addView(hiddenInfo);

        View vv = emailOrMobileLayout.getChildAt(viewArrayList.size());
        EditText myEditTextView1 = (EditText) vv.findViewById(R.id.description);
        //myEditTextView1.setSelection(myEditTextView1.length());
        myEditTextView1.requestFocus();
        viewArrayList.add(hiddenInfo);
        return hiddenInfo;
    }

    public void getLatLong() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setAltitudeRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);
        criteria.setBearingRequired(false);

//API level 9 and up
        criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);
        Integer gpsFreqInMillis = 1000;
        Integer gpsFreqInDistance = 1;
        mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mlocListener = new MyLocationListener();

        // permission was granted, yay! Do the
        // location-related task you need to do.
        if (ContextCompat.checkSelfPermission(TakePhotoScreen.this,
                ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            //Request location updates:

            //mlocManager.addGpsStatusListener((GpsStatus.Listener) this);
            mlocManager.requestLocationUpdates(gpsFreqInMillis, gpsFreqInDistance, criteria, mlocListener, null);

            //mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener);

        }

        if (mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(TakePhotoScreen.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(TakePhotoScreen.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    requestPermissions(new String[]{CAMERA, ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
            } else {
                if (ActivityCompat.checkSelfPermission(TakePhotoScreen.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(TakePhotoScreen.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(TakePhotoScreen.this, new String[]{ACCESS_FINE_LOCATION}, 1);

                }
            }
            if (MyLocationListener.latitude > 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (CameraUtils.checkPermissions(TakePhotoScreen.this)) {
                        captureImage();
                    } else {
                        requestCameraPermission(MEDIA_TYPE_IMAGE);
                    }
//                            checkPermissionForCamera();
                } else {
                    captureImage();
                }
            } else {
                Utils.showAlert(TakePhotoScreen.this, getResources().getString(R.string.satellite));
            }
        } else {
            Utils.showAlert(TakePhotoScreen.this, getResources().getString(R.string.gps_is_not_turned_on));
        }
    }

    private void requestCameraPermission(final int type) {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {

                            if (type == MEDIA_TYPE_IMAGE) {
                                // capture picture
                                captureImage();
                            } else {
//                                captureVideo();
                            }

                        } else if (report.isAnyPermissionPermanentlyDenied()) {
                            showPermissionsAlert();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }


    private void showPermissionsAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.permission_required))
                .setMessage(getResources().getString(R.string.camera_need_permission))
                .setPositiveButton(getResources().getString(R.string.goto_settings), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        CameraUtils.openSettings(TakePhotoScreen.this);
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }

    public void previewCapturedImage() {
        try {
            // hide video preview
            Bitmap bitmap = CameraUtils.optimizeBitmap(BITMAP_SAMPLE_SIZE, imageStoragePath);
            image_view_preview.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            ExifInterface ei = null;
            try {
                ei = new ExifInterface(imageStoragePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

            Bitmap rotatedBitmap = null;
            switch (orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(bitmap, 90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(bitmap, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(bitmap, 270);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    rotatedBitmap = bitmap;
            }
            imageView.setImageBitmap(rotatedBitmap);
            latitude_text.setText(""+offlatTextValue);
            longtitude_text.setText(""+offlongTextValue);
//            cameraScreenBinding.imageView.showImage((getImageUri(rotatedBitmap)));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Bitmap photo= (Bitmap) data.getExtras().get("data");
                    imageView.setImageBitmap(photo);
                    image_view_preview.setVisibility(View.GONE);
                    imageView.setVisibility(View.VISIBLE);
                    latitude_text.setText(""+offlatTextValue);
                    longtitude_text.setText(""+offlongTextValue);
                }
                else {
                    // Refreshing the gallery
                    CameraUtils.refreshGallery(getApplicationContext(), imageStoragePath);

                    // successfully captured the image
                    // display it in image view
                    previewCapturedImage();
                }
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.user_canceled_image_capture), Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.sorry_failed_to_capture), Toast.LENGTH_SHORT)
                        .show();
            }
        } else if (requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Refreshing the gallery
                CameraUtils.refreshGallery(getApplicationContext(), imageStoragePath);

                // video successfully recorded
                // preview the recorded video
//                previewVideo();
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled recording
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.user_canceled_video), Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to record video
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.sorry_failed_capture_video), Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    private void captureImage() {
        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {

            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);

        }
        else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            File file = CameraUtils.getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (file != null) {
                imageStoragePath = file.getAbsolutePath();
            }

            Uri fileUri = CameraUtils.getOutputMediaFileUri(this, file);

            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

            // start the image capture Intent
            startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
        }
        if (MyLocationListener.latitude > 0) {
            offlatTextValue = MyLocationListener.latitude;
            offlongTextValue = MyLocationListener.longitude;
            Log.d("Locations",""+offlatTextValue+" "+offlongTextValue);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(TakePhotoScreen.this,HabitationClass.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }

    public void showAlert(String pointType) {
        String startAlert = "Capturing of Starting GPS-co-ordinate is allowed only once.So stand on the correct starting point of this Road and capture Gps co-ordinate";
        String endAlert = "Capturing of  Ending GPS-co-ordinate is allowed only once.So stand on the correct ending point of this Road and capture Gps co-ordinate";
        String restart_alert = "Are you sure you want to re-capture All Gps co-ordinates ?";
        String alert = "";
        if(pointType.equalsIgnoreCase("1")){
            alert= startAlert;
        }
        else if(pointType.equalsIgnoreCase("2")){
            alert = endAlert;
        }
        else {
            alert = restart_alert;
        }

        new AlertDialog.Builder(this)
                .setTitle("Alert")
                .setMessage(alert)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if(!pointType.equalsIgnoreCase("3")){
                            getLatLong();
                        }
                        else {
                            Intent intent = getIntent();
                            finish();
                            startActivity(intent);
                        }

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                }).show();

    }

}
