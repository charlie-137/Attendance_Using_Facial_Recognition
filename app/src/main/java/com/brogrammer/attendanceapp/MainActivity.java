package com.brogrammer.imageclassificationlivefeed;

//UsesMap
//import static com.example.imageclassificationlivefeed.Face_Recognition.TFLiteFaceRecognition.registeredHashMap;

import static com.brogrammer.imageclassificationlivefeed.BackEnd.DataManager.getRegisteredHashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.media.Image;
import android.media.ImageReader;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;
import android.util.TypedValue;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.brogrammer.imageclassificationlivefeed.BackEnd.DataManager;
import com.brogrammer.imageclassificationlivefeed.Drawing.BorderedText;
import com.brogrammer.imageclassificationlivefeed.Drawing.MultiBoxTracker;
import com.brogrammer.imageclassificationlivefeed.Drawing.OverlayView;
import com.brogrammer.imageclassificationlivefeed.Face_Recognition.FaceClassifier;
import com.brogrammer.imageclassificationlivefeed.Face_Recognition.TFLiteFaceRecognition;
import com.brogrammer.imageclassificationlivefeed.LiveFeed.CameraConnectionFragment;
import com.brogrammer.imageclassificationlivefeed.LiveFeed.ImageUtils;
import com.brogrammer.imageclassificationlivefeed.UserInterface.SharedViewModel;
import com.brogrammer.imageclassificationlivefeed.Utils.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import org.bson.Document;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.RealmResultTask;
import io.realm.mongodb.User;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.MongoDatabase;
import io.realm.mongodb.mongo.iterable.MongoCursor;
import io.realm.mongodb.mongo.options.UpdateOptions;


// Last task -  Working for the indexing in mondo db

public class MainActivity extends AppCompatActivity implements ImageReader.OnImageAvailableListener {


    String empCode = null;
    // Declare the ViewModel
    private SharedViewModel sharedViewModel;

    //S

    String dateNtime = String.valueOf(new Date().getTime());

    Bitmap bitmapToStoreInGallery;
    private ImageHelper imageHelper;

    public static final int REQUEST_CODE_PICK_IMAGE = 102;
    public static final int RESULT_OK = -1;


    boolean unknownFaceDetected = false;

    //E


    ///S

    boolean trackCapturedImage = false;

    private Image capturedImage;
    Button registerFaceButton;

    ImageView imageViewToCrop;
    private Bitmap processedBitmap;
    private Button submitButton;

    /////E


    Handler handler;
    private Matrix frameToCropTransform;
    private int sensorOrientation;
    private Matrix cropToFrameTransform;

    private static final boolean MAINTAIN_ASPECT = false;
    private static final float TEXT_SIZE_DIP = 10;
    OverlayView trackingOverlay;
    private BorderedText borderedText;
    private MultiBoxTracker tracker;
    private Integer useFacing = null;
    private static final String KEY_USE_FACING = "use_facing";
    private static final int CROP_SIZE = 1000;
    private static final int TF_OD_API_INPUT_SIZE2 = 160;
    private static final int TF_OD_API_INPUT_SIZE_WIDTH = 750;
    private static final int TF_OD_API_INPUT_SIZE_HEIGHT = 1000;

    private App app;
    String AppId = "application-0-wnbyu";
    String mClient = "mongodb-atlas";
    String mDatabase = "DemoDataDB";
    String mCollection = "TestData";
    User user;
    MongoDatabase mongoDatabase;
    MongoClient mongoClient;
    MongoCollection<Document> mongoCollection;
    private String name;

    //TODO declare face detector
    FaceDetector detector;

    private String tempResult;

    //TODO declare face recognizer
    private FaceClassifier faceClassifier;

    boolean registerFace = false;

    String employeeName;
    String employeeCode;
    Boolean isRegisterFace;

    Button switchCameraBtn;
    Button punchAttnBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the ViewModel using ViewModelProvider
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);

        // Retrieve the employeeName from the intent
        Intent intentTwo = getIntent();
        if (intentTwo != null) {
            employeeName = intentTwo.getStringExtra("employeeName");
            employeeCode = intentTwo.getStringExtra("employeeCode");
            isRegisterFace = intentTwo.getBooleanExtra("isRegisterFace", true);
            if (employeeName != null && employeeCode != null) {
                // Do whatever you want with the employeeName
//                Toast.makeText(this, "Received employeeName: " + employeeName+" :"+isRegisterFace, Toast.LENGTH_SHORT).show();
//                Toast.makeText(this, "Received Code: " + employeeCode+" :"+isRegisterFace, Toast.LENGTH_SHORT).show();
            }

            if (isRegisterFace) {
                // Do something specific for take attendance
//                Toast.makeText(this, "Face Registration "+isRegisterFace, Toast.LENGTH_SHORT).show();
            } else {
//                Toast.makeText(this, "Face Registration "+isRegisterFace, Toast.LENGTH_SHORT).show();
            }
        }

        switchCameraBtn = findViewById(R.id.flipCameraBtn);
        punchAttnBtn = findViewById(R.id.punchAttnBtn);
        registerFaceButton = findViewById(R.id.registerFaceBtn);

        if (isRegisterFace) {
            // If isRegisterFace is true, show the add face button
            punchAttnBtn.setVisibility(View.GONE);
            registerFaceButton.setVisibility(View.VISIBLE);

        } else {
            // If isRegisterFace is false, show the add database button
            punchAttnBtn.setVisibility(View.VISIBLE);
            registerFaceButton.setVisibility(View.GONE);
        }


        handler = new Handler();

        // ImageHelper.kt
        imageHelper = new ImageHelper(this);


        //TODO MongoDB Code
        Realm.init(this);
        app = new App(new AppConfiguration.Builder(AppId).build());

        //Pass the Realm app instance to the Data Manager
        DataManager.initializeRealm(app);

//        String email="qutub137@gmail.com", password ="qutub_137";
//        Credentials credentials = Credentials.emailPassword(email,password);
        Credentials credentials = Credentials.anonymous();
        app.loginAsync(credentials, it -> {
            if (it.isSuccess()) {
                user = app.currentUser();
                mongoClient = user.getMongoClient("mongodb-atlas");
                mongoDatabase = mongoClient.getDatabase("DemoDataDB");
                mongoCollection = mongoDatabase.getCollection("TestData");
//                Toast.makeText(MainActivity.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
            } else {
//                Toast.makeText(MainActivity.this, "Failed to Log In", Toast.LENGTH_SHORT).show();
            }
        });


        //TODO handling permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED) {
                String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permission, 121);
            }
        }

//        registerFaceButton = (ImageView) findViewById(R.id.imageView4);

        Intent intent = getIntent();
        useFacing = intent.getIntExtra(KEY_USE_FACING, CameraCharacteristics.LENS_FACING_BACK);

        //TODO show live camera footage
        setFragment();


        //TODO initialize the tracker to draw rectangles
        tracker = new MultiBoxTracker(this);


        //TODO initalize face detector
        // Multiple object detection in static images
        FaceDetectorOptions highAccuracyOpts =
                new FaceDetectorOptions.Builder()
                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
                        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
                        .build();
        detector = FaceDetection.getClient(highAccuracyOpts);


        //TODO initialize FACE Recognition
        try {
            faceClassifier =
                    TFLiteFaceRecognition.create(
                            getAssets(),
                            "facenet.tflite",
                            TF_OD_API_INPUT_SIZE2,
                            false);

        } catch (final IOException e) {
            e.printStackTrace();
//            Toast toast = Toast.makeText(getApplicationContext(), "Classifier could not be initialized", Toast.LENGTH_SHORT);
//            toast.show();
            finish();
        }

        registerFaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerFace = true;
                performDetectionFlag = true;
            }
        });

//        findViewById(R.id.imageView4).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                registerFace = true;
//            }
//        });


        switchCameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchCamera();
            }
        });

        punchAttnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performDetectionFlag = true;
//                getAllUsersData();
//                Toast.makeText(MainActivity.this, "Data Fetched Successfully", Toast.LENGTH_SHORT).show();
            }
        });


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 121 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            setFragment();
        }
    }

    //TODO fragment which show live footage from camera
    int previewHeight = 0, previewWidth = 0;

    protected void setFragment() {
        final CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        String cameraId = null;
        try {
            cameraId = manager.getCameraIdList()[useFacing];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        Fragment fragment;
        CameraConnectionFragment camera2Fragment =
                CameraConnectionFragment.newInstance(
                        new CameraConnectionFragment.ConnectionCallback() {
                            @Override
                            public void onPreviewSizeChosen(final Size size, final int rotation) {
                                previewHeight = size.getHeight();
                                previewWidth = size.getWidth();

                                final float textSizePx =
                                        TypedValue.applyDimension(
                                                TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_DIP, getResources().getDisplayMetrics());
                                borderedText = new BorderedText(textSizePx);
                                borderedText.setTypeface(Typeface.MONOSPACE);


                                int cropSize = CROP_SIZE;

                                previewWidth = size.getWidth();
                                previewHeight = size.getHeight();

                                sensorOrientation = rotation - getScreenOrientation();

                                rgbFrameBitmap = Bitmap.createBitmap(previewWidth, previewHeight, Bitmap.Config.ARGB_8888);
                                croppedBitmap = Bitmap.createBitmap(cropSize, cropSize, Bitmap.Config.ARGB_8888);

                                frameToCropTransform =
                                        ImageUtils.getTransformationMatrix(
                                                previewWidth, previewHeight,
                                                cropSize, cropSize,
                                                sensorOrientation, MAINTAIN_ASPECT);

                                cropToFrameTransform = new Matrix();
                                frameToCropTransform.invert(cropToFrameTransform);

                                trackingOverlay = findViewById(R.id.tracking_overlay);
                                trackingOverlay.addCallback(
                                        new OverlayView.DrawCallback() {
                                            @Override
                                            public void drawCallback(final Canvas canvas) {
                                                tracker.draw(canvas);
                                                Log.d("tryDrawRect", "inside draw");
                                            }
                                        });
                                tracker.setFrameConfiguration(previewWidth, previewHeight, sensorOrientation);
                            }
                        },
                        this,
                        R.layout.camera_fragment,
                        new Size(640, 480));

        camera2Fragment.setCamera(cameraId);
        fragment = camera2Fragment;
        getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
    }


    //TODO getting frames of live camera footage and passing them to model
    private boolean isProcessingFrame = false;
    private final byte[][] yuvBytes = new byte[3][];
    private int[] rgbBytes = null;
    private int yRowStride;
    private Runnable postInferenceCallback;
    private Runnable imageConverter;
    private Bitmap rgbFrameBitmap;

    private boolean performDetectionFlag = false;

    @Override
    public void onImageAvailable(ImageReader reader) {
        // We need wait until we have some size from onPreviewSizeChosen
        if (previewWidth == 0 || previewHeight == 0) {
            return;
        }
        if (rgbBytes == null) {
            rgbBytes = new int[previewWidth * previewHeight];
        }
        try {
            final Image image = reader.acquireLatestImage();


            if (image == null) {
                return;
            }

            if (isProcessingFrame) {
                image.close();
                return;
            }

            isProcessingFrame = true;
            final Image.Plane[] planes = image.getPlanes();
            fillBytes(planes, yuvBytes);
            yRowStride = planes[0].getRowStride();
            final int uvRowStride = planes[1].getRowStride();
            final int uvPixelStride = planes[1].getPixelStride();

            imageConverter =
                    new Runnable() {
                        @Override
                        public void run() {
                            ImageUtils.convertYUV420ToARGB8888(
                                    yuvBytes[0],
                                    yuvBytes[1],
                                    yuvBytes[2],
                                    previewWidth,
                                    previewHeight,
                                    yRowStride,
                                    uvRowStride,
                                    uvPixelStride,
                                    rgbBytes);

                            //saving the image into the gallery
                            processedBitmap = Bitmap.createBitmap(previewWidth, previewHeight, Bitmap.Config.ARGB_8888);
                            processedBitmap.setPixels(rgbBytes, 0, previewWidth, 0, 0, previewWidth, previewHeight);
//                            saveBitmapToGallery(processedBitmap);

                        }
                    };


            postInferenceCallback =
                    new Runnable() {
                        @Override
                        public void run() {
                            image.close();
                            isProcessingFrame = false;
                        }
                    };

            performFaceDetection(performDetectionFlag);

        } catch (final Exception e) {
            Log.d("tryError", e.getMessage());
            e.printStackTrace();
        }

    }

    private void saveBitmapToGallery(Bitmap bitmap) {
        // Save the bitmap to the gallery
        String title = "Processed_Image"; // You can set a title for your image file
        String description = "Processed image description"; // Optional description

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, title);
        values.put(MediaStore.Images.Media.DESCRIPTION, description);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

        // Get the external storage directory
        File externalStorageDir = Environment.getExternalStorageDirectory();
        File imageFile = new File(externalStorageDir, title + ".jpg");

        Uri imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        if (imageUri != null) {
            try (OutputStream outputStream = getContentResolver().openOutputStream(imageUri)) {
                if (outputStream != null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected void fillBytes(final Image.Plane[] planes, final byte[][] yuvBytes) {
        // Because of the variable row stride it's not possible to know in
        // advance the actual necessary dimensions of the yuv planes.
        for (int i = 0; i < planes.length; ++i) {
            final ByteBuffer buffer = planes[i].getBuffer();
            if (yuvBytes[i] == null) {
                yuvBytes[i] = new byte[buffer.capacity()];
            }
            buffer.get(yuvBytes[i]);
        }
    }

    protected int getScreenOrientation() {
        switch (getWindowManager().getDefaultDisplay().getRotation()) {
            case Surface.ROTATION_270:
                return 270;
            case Surface.ROTATION_180:
                return 180;
            case Surface.ROTATION_90:
                return 90;
            default:
                return 0;
        }
    }

    Bitmap croppedBitmap, tempCroppedBitmap;
    List<FaceClassifier.Recognition> mappedRecognitions;

    // Declare a boolean variable to track whether the error dialog has been displayed
    private boolean errorDialogDisplayed = false;

    //TODO Perform face detection
    public void performFaceDetection(boolean performDetectionFlag) {
        imageConverter.run();
        rgbFrameBitmap.setPixels(rgbBytes, 0, previewWidth, 0, 0, previewWidth, previewHeight);

        final Canvas canvas = new Canvas(croppedBitmap);
        canvas.drawBitmap(rgbFrameBitmap, frameToCropTransform, null);

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                mappedRecognitions = new ArrayList<>();
                InputImage image = InputImage.fromBitmap(croppedBitmap, 0);


                detector.process(image)
                        .addOnSuccessListener(
                                new OnSuccessListener<List<Face>>() {
                                    @Override
                                    public void onSuccess(List<Face> faces) {

                                        if (faces.size() > 1 && !errorDialogDisplayed) {
                                            // Multiple faces detected, show error dialog
                                            errorDialogMultipleFace();
//                                            showErrorDialog("Error", "Multiple faces detected. Please ensure only one face is visible.");
                                            errorDialogDisplayed = true;  // Set the flag to true
                                        } else if (faces.size() == 0) {
                                            // No faces detected, reset the flag
                                            errorDialogDisplayed = false;
                                        }

                                        if (faces.size() == 1) {
                                            // Single face detected, proceed with face recognition
                                            for (Face face : faces) {
                                                final Rect bounds = face.getBoundingBox();
                                                performFaceRecognition(face);
                                            }
                                        }
//
//                                        // Single face detected, proceed with face recognition
//                                        if(faces.size()==1)
//                                        {
//                                            for(Face face:faces) {
//                                                final Rect bounds = face.getBoundingBox();
//                                                performFaceRecognition(face);
//                                            }
//                                        }else{
////                                            // Multiple faces detected, show error dialog
////                                            new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
////                                                    .setTitleText("Multiple Faces Detected")
////                                                    .setContentText("Please ensure only single face is present")
////                                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
////                                                        @Override
////                                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
////                                                            sweetAlertDialog.dismissWithAnimation();
////                                                        }
////                                                    })
////                                                    .show();
//
//                                            // Multiple faces detected, show error dialog
//                                            showErrorDialog("Error", "Multiple faces detected. Please ensure only one face is visible.");
//                                        }

                                        registerFace = false;
                                        tracker.trackResults(mappedRecognitions, 10);
                                        trackingOverlay.postInvalidate();
                                        postInferenceCallback.run();

                                    }
                                })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Task failed with an exception
                                        // ...
                                    }
                                });


            }
        });
    }

    // Method to show an error dialog
    private void showErrorDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // You can perform any action or leave it empty
                    }
                })
                .show();
    }

    private void errorDialogMultipleFace() {
        // Multiple faces detected, show error dialog
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Multiple Faces Detected")
                .setContentText("Please ensure only single face is present")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                    }
                })
                .show();
    }

    //TODO perform face recognition
    public void performFaceRecognition(Face face) {
        //TODO crop the face
        Rect bounds = face.getBoundingBox();
        if (bounds.top < 0) {
            bounds.top = 0;
        }
        if (bounds.left < 0) {
            bounds.left = 0;
        }
        if (bounds.left + bounds.width() > croppedBitmap.getWidth()) {
            bounds.right = croppedBitmap.getWidth() - 1;
        }
        if (bounds.top + bounds.height() > croppedBitmap.getHeight()) {
            bounds.bottom = croppedBitmap.getHeight() - 1;
        }

        // Display bounding boxes over the face regardless of performDetectionFlag value
        RectF location = new RectF(bounds);
        if (bounds != null) {
            if (useFacing == CameraCharacteristics.LENS_FACING_BACK) {
                location.right = croppedBitmap.getWidth() - location.right;
                location.left = croppedBitmap.getWidth() - location.left;
            }
            cropToFrameTransform.mapRect(location);
            FaceClassifier.Recognition recognition = new FaceClassifier.Recognition(String.valueOf(face.getTrackingId()), "Unknown", 0f, location);
            mappedRecognitions.add(recognition);
        }

        // Start face recognition process only if performDetectionFlag is true
        if (!performDetectionFlag) {
            return;
        }

        Bitmap crop = Bitmap.createBitmap(croppedBitmap,
                bounds.left,
                bounds.top,
                bounds.width(),
                bounds.height());
        crop = Bitmap.createScaledBitmap(crop, TF_OD_API_INPUT_SIZE2, TF_OD_API_INPUT_SIZE2, false);
        tempCroppedBitmap = Bitmap.createScaledBitmap(croppedBitmap, TF_OD_API_INPUT_SIZE_WIDTH, TF_OD_API_INPUT_SIZE_HEIGHT, true);


        // Assuming tempCroppedBitmap is your Bitmap object ++++++++++++ Mirroring the Bitmap ++++++++++++++++++

        Matrix matrix = new Matrix();
        matrix.preScale(-1.0f, 1.0f); // Horizontal mirror
        tempCroppedBitmap = Bitmap.createBitmap(tempCroppedBitmap, 0, 0, tempCroppedBitmap.getWidth(), tempCroppedBitmap.getHeight(), matrix, true);

        // Now you have the mirroredBitmap which is the horizontally mirrored version of tempCroppedBitmap


        final FaceClassifier.Recognition result = faceClassifier.recognizeImage(crop, registerFace);
        String title = "Unknown";


        float confidence = 0;
        if (result != null) {
            if (registerFace) {
                registerFaceDialogue(crop, result);
            } else if (!isRegisterFace) {
                if (result.getDistance() < 0.75f) {
                    confidence = result.getDistance();
                    title = result.getTitle();
                    empCode = result.getId();
                    Log.d("MyTAGLog", "performFaceRecognition: ABC : " + empCode);

                    markAttendance(title, empCode);
                    DataManager.updateAttendance(empCode, "P");

                    isRegisterFace = true;
                    new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Attendance Marked")
                            .setContentText("Attendance of " + result.getTitle() + " is marked successfully")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    isRegisterFace = false;
                                    performDetectionFlag = false;
                                    sweetAlertDialog.dismissWithAnimation();
                                }
                            })
                            .show();

                } else {
//                    isRegisterFace = true;
//                    // If unknown face detected and the dialog hasn't been shown yet
//                    if (!unknownFaceDetected) {
//                        unknownFaceDetected = true;
//                        // Show unknown face dialog
//                        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
//                                .setTitleText("Unknown Face Detected")
//                                .setContentText("Un-identified Person")
//                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                                    @Override
//                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
//                                        unknownFaceDetected = false;
//                                        sweetAlertDialog.dismissWithAnimation();
//                                    }
//                                })
//                                .show();
//                    }
//                    isRegisterFace = true;
                }
            }
        }

        // Add recognition result to mappedRecognitions if not null
        if (result != null) {
            RectF recognitionLocation = new RectF(bounds);
            if (bounds != null) {
                if (useFacing == CameraCharacteristics.LENS_FACING_BACK) {
                    recognitionLocation.right = croppedBitmap.getWidth() - recognitionLocation.right;
                    recognitionLocation.left = croppedBitmap.getWidth() - recognitionLocation.left;
                }
                cropToFrameTransform.mapRect(recognitionLocation);
                FaceClassifier.Recognition recognitionResult = new FaceClassifier.Recognition(String.valueOf(face.getTrackingId()), title, confidence, recognitionLocation);
                mappedRecognitions.add(recognitionResult);
            }
        }
    }



//    public void performFaceRecognition(Face face) {
//        //TODO crop the face
//        Rect bounds = face.getBoundingBox();
//        if (bounds.top < 0) {
//            bounds.top = 0;
//        }
//        if (bounds.left < 0) {
//            bounds.left = 0;
//        }
//        if (bounds.left + bounds.width() > croppedBitmap.getWidth()) {
//            bounds.right = croppedBitmap.getWidth() - 1;
//        }
//        if (bounds.top + bounds.height() > croppedBitmap.getHeight()) {
//            bounds.bottom = croppedBitmap.getHeight() - 1;
//        }
//
//        Bitmap crop = Bitmap.createBitmap(croppedBitmap,
//                bounds.left,
//                bounds.top,
//                bounds.width(),
//                bounds.height());
//        crop = Bitmap.createScaledBitmap(crop, TF_OD_API_INPUT_SIZE2, TF_OD_API_INPUT_SIZE2, false);
//        tempCroppedBitmap = Bitmap.createScaledBitmap(croppedBitmap, TF_OD_API_INPUT_SIZE_WIDTH, TF_OD_API_INPUT_SIZE_HEIGHT, true);
//
//
//        // Assuming tempCroppedBitmap is your Bitmap object ++++++++++++ Mirroring the Bitmap ++++++++++++++++++
//
//        Matrix matrix = new Matrix();
//        matrix.preScale(-1.0f, 1.0f); // Horizontal mirror
//        tempCroppedBitmap = Bitmap.createBitmap(tempCroppedBitmap, 0, 0, tempCroppedBitmap.getWidth(), tempCroppedBitmap.getHeight(), matrix, true);
//
//        // Now you have the mirroredBitmap which is the horizontally mirrored version of tempCroppedBitmap
//
//
//        final FaceClassifier.Recognition result = faceClassifier.recognizeImage(crop, registerFace);
//        String title = "Unknown";
//
//
//        float confidence = 0;
//        if (result != null) {
//            if (registerFace) {
//                registerFaceDialogue(crop, result);
//
//
////                Intent resultIntent = new Intent();
////                setResult(Activity.RESULT_OK, resultIntent);
////                finish();
//
//
//            } else if (!isRegisterFace) {
//                if (result.getDistance() < 0.75f) {
////                    Toast.makeText(this, "Attendance of "+result.getTitle().toString()+"is marked", Toast.LENGTH_SHORT).show();
//                    confidence = result.getDistance();
//                    title = result.getTitle();
//                    empCode = result.getId();
//                    Log.d("MyTAGLog", "performFaceRecognition: ABC : " + empCode);
//
//                    markAttendance(title, empCode);
//                    DataManager.updateAttendance(empCode, "P");
//
////                    Toast.makeText(this, "Attendance of "+ result.getTitle() +" is marked", Toast.LENGTH_SHORT).show();
//                    isRegisterFace = true;
//                    new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
//                            .setTitleText("Attendance Marked")
//                            .setContentText("Attendance of " + result.getTitle() + " is marked successfully")
//                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                                @Override
//                                public void onClick(SweetAlertDialog sweetAlertDialog) {
//                                    //UsesMap
//                                    getRegisteredHashMap().clear();
//                                    isRegisterFace = false;
//                                    sweetAlertDialog.dismissWithAnimation();
//                                }
//                            })
//                            .show();
//
//                } else {
////                    isRegisterFace=true;
////                    new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
////                            .setTitleText("Unknown Face Detected")
////                            .setContentText("Un-identified Person")
////                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
////                                @Override
////                                public void onClick(SweetAlertDialog sweetAlertDialog) {
////                                    isRegisterFace=false;
////                                    sweetAlertDialog.dismissWithAnimation();
////                                }
////                            })
////                            .show();
//
//                }
//            }
//        }
//
//        RectF location = new RectF(bounds);
//        if (bounds != null) {
//            if (useFacing == CameraCharacteristics.LENS_FACING_BACK) {
//                location.right = croppedBitmap.getWidth() - location.right;
//                location.left = croppedBitmap.getWidth() - location.left;
//            }
//            cropToFrameTransform.mapRect(location);
//            FaceClassifier.Recognition recognition = new FaceClassifier.Recognition(String.valueOf(face.getTrackingId()), title, confidence, location);
//            mappedRecognitions.add(recognition);
//        }
//
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        detector.close();
    }

    //TODO register face dialogue
    private void registerFaceDialogue(Bitmap croppedFace, FaceClassifier.Recognition rec) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.register_face_dialogue);
        ImageView ivFace = dialog.findViewById(R.id.dlg_image);
//        TextView nameEd = dialog.findViewById(R.id.dlg_input);
        Button register = dialog.findViewById(R.id.button2);
        ivFace.setImageBitmap(tempCroppedBitmap);
//        nameEd.setText(employeeName);

        //onClickListener on the dialog image to crop it
        ivFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewToCrop = dialog.findViewById(R.id.dlg_image);
                File tempFile = new File(getCacheDir(), "temp.jpg");
                imageHelper.selectAndCropImage(imageViewToCrop, tempFile);
            }
        });
        //---------------End----------


//        Drawable drawable = ivFace.getDrawable();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = employeeName;
//                name = nameEd.getText().toString();
//                if (name.isEmpty()) {
//                    nameEd.setError("Enter Name");
//                    return;
//                }

//               --------   Store the facial data and name temporarily for immediate recognition -----------

                faceClassifier.register(name, rec);

//                -----------------------  End  ---------------


                Drawable drawable = ivFace.getDrawable();


//                //Saving the Image to the gallery
//                if(processedBitmap!=null)
//                {
//                    saveBitmapToGallery(tempCroppedBitmap);
//                }


                Bitmap imageBitmap = ((BitmapDrawable) drawable).getBitmap();

                //Saving the Image to the gallery
                if (drawable != null) {
                    Bitmap Imagebitmap = ((BitmapDrawable) drawable).getBitmap();
                    saveBitmapToGallery(Imagebitmap);
                }
                if (imageBitmap != null) {
                    Log.d("BitmapInfoTAGONE", "Bitmap dimensions: " + (imageBitmap != null ? imageBitmap.getWidth() + " x " + imageBitmap.getHeight() : "null"));

                    // Set the bitmap in the SharedViewModel
                    sharedViewModel.setBitmap(imageBitmap);
                    Log.d("ImageHandleTag", "Done");
                }


//                if(bitmapToStoreInGallery==null)
//                {
//                    Toast.makeText(MainActivity.this, "Null Value in the BitmapToStore", Toast.LENGTH_SHORT).show();
//                }
//
//                if(bitmapToStoreInGallery!=null)
//                {
//                    storeBitmap(bitmapToStoreInGallery);
//                }


                Toast.makeText(MainActivity.this, "Face Registered Successfully", Toast.LENGTH_SHORT).show();
                Log.v("EmbeddingsLog", rec.getEmbeeding().toString());
                dialog.dismiss();


                //S

                // Embeddings
                tempResult = TFLiteFaceRecognition.staticResult;
                //Log.d("TempResult",name + " " + tempResult.toString());


//========================================================================================================================================
//                Document query = new Document("Name",name);
//                Document update = new Document("$set", new Document("Embeddings",tempResult));

                User user = app.currentUser();
                mongoClient = user.getMongoClient("mongodb-atlas");
                mongoDatabase = mongoClient.getDatabase("DemoDataDB");
                MongoCollection<Document> mongoCollection = mongoDatabase.getCollection("TestData");

                Document Obj = new Document();
                Obj.append("Name", name);
                Obj.append("Embeddings", tempResult);
                Obj.append("EmployeeID", employeeCode);
                Obj.append("userid", user.getId());
                Log.v("DataInsert", Obj.toString());

                mongoCollection.updateOne(new Document("EmployeeID", employeeCode),
                        new Document("$set", Obj), new UpdateOptions().upsert(true)).getAsync(result -> {
                    if (result.isSuccess()) {
                        Log.v("DataInsert", "Data Inserted/Updated Successfully");
//                        Toast.makeText(MainActivity.this, "Data Inserted/Updated Successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.v("DataInsert", result.getError().toString());
//                        Toast.makeText(MainActivity.this, result.getError().toString(), Toast.LENGTH_SHORT).show();
                    }
                });


//                mongoCollection.updateOne(query, update, new UpdateOptions().upsert(true)).getAsync(result -> {
//                    if (result.isSuccess()) {
//                        Log.v("DataInsert", "Data Inserted/Updated Successfully");
//                        Toast.makeText(MainActivity.this, "Data Inserted/Updated Successfully", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Log.v("DataInsert", result.getError().toString());
//                        Toast.makeText(MainActivity.this, result.getError().toString(), Toast.LENGTH_SHORT).show();
//                    }
//                });


//                mongoCollection.insertOne(Obj).getAsync(result -> {
//                    if(result.isSuccess())
//                    {
//                        Log.v("DataInsert","Data Inserted Successfully");
//                        Toast.makeText(MainActivity.this, "Data Inserted Successfully", Toast.LENGTH_SHORT).show();
//                    }
//                    else
//                    {
//                        Log.v("DataInsert",result.getError().toString());
//                        Toast.makeText(MainActivity.this, result.getError().toString(), Toast.LENGTH_SHORT).show();
//
//                    }
//                });

                DataManager.updateImageBitmap(employeeCode, imageBitmap);


                //E


                // Set the result and finish the activity
                setResult(Activity.RESULT_OK);
//                sharedViewModel.getBitmapLiveData().value = imageBitmap;
                if (imageBitmap != null) {
                    Log.d("BitmapInfoTAGONE", "Bitmap dimensions: " + (imageBitmap != null ? imageBitmap.getWidth() + " x " + imageBitmap.getHeight() : "null"));

                    // Set the bitmap in the SharedViewModel
                    sharedViewModel.setBitmap(imageBitmap);
                    Log.d("ImageHandleTag", "Done");
                }


                // Finish the activity to return to the fragment
                finish();

            }
        });
        dialog.show();
    }


    //TODO Mark the attendance of the employee
    private void markAttendance(String name, String empId) {

        String timeStamp = String.valueOf(System.currentTimeMillis());


//        // Get the current date
//        LocalDate currentDate = null;
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            currentDate = LocalDate.now();
//        }
//
//        // Define a format for the date
//        DateTimeFormatter formatter = null;
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        }
//
//        // Format the current date using the defined format
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            String formattedDate = currentDate.format(formatter);
//        }

        // Get the current date
        Date currentDate = new Date();

        // Define a format for the date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());

        // Format the current date using the defined format
        String formattedDate = dateFormat.format(currentDate);
        int dateAsInteger = Integer.parseInt(formattedDate);


        Log.d("Formatted Data", String.valueOf(dateAsInteger));


        User user = app.currentUser();
        mongoClient = user.getMongoClient("mongodb-atlas");
        mongoDatabase = mongoClient.getDatabase("DemoDataDB");
        MongoCollection<Document> mongoCollection = mongoDatabase.getCollection("AttendanceHistory");


        int id = DataManager.getEmployeeId(empId);

        Document Obj = new Document();
        Obj.append("Date", dateAsInteger);
        Obj.append("Id", id);
        Obj.append("Name", name);
        Obj.append("EmployeeId", empId);
        Obj.append("Status", "P");
        Obj.append("TimeStamp", timeStamp);
        Obj.append("userid", user.getId());
        Log.v("DataInsert", Obj.toString());

//        mongoCollection.updateOne(new Document("Name", name),
//                new Document("$set", Obj), new UpdateOptions().upsert(true)).getAsync(result -> {
//            if (result.isSuccess()) {
//                Log.v("DataInsert", "Data Inserted/Updated Successfully");
//                Toast.makeText(MainActivity.this, "Data Inserted/Updated Successfully", Toast.LENGTH_SHORT).show();
//            } else {
//                Log.v("DataInsert", result.getError().toString());
//                Toast.makeText(MainActivity.this, result.getError().toString(), Toast.LENGTH_SHORT).show();
//            }
//        });


//                mongoCollection.updateOne(query, update, new UpdateOptions().upsert(true)).getAsync(result -> {
//                    if (result.isSuccess()) {
//                        Log.v("DataInsert", "Data Inserted/Updated Successfully");
//                        Toast.makeText(MainActivity.this, "Data Inserted/Updated Successfully", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Log.v("DataInsert", result.getError().toString());
//                        Toast.makeText(MainActivity.this, result.getError().toString(), Toast.LENGTH_SHORT).show();
//                    }
//                });

        mongoCollection.insertOne(Obj).getAsync(result -> {
            if (result.isSuccess()) {
                Log.v("DataInsert", "Data Inserted Successfully");
//                        Toast.makeText(MainActivity.this, "Today's Attendance Marked", Toast.LENGTH_SHORT).show();
            } else {
                Log.v("DataInsert", result.getError().toString());
//                        Toast.makeText(MainActivity.this, result.getError().toString(), Toast.LENGTH_SHORT).show();

            }
        });
    }


    //TODO Fetching the stored data from the MongoDB like name and embedding of the face
    private void getAllUsersData() {
        User user = app.currentUser();
        mongoClient = user.getMongoClient("mongodb-atlas");
        mongoDatabase = mongoClient.getDatabase("DemoDataDB");
        MongoCollection<Document> mongoCollection = mongoDatabase.getCollection("TestData");
        Document Obj = new Document();
        Obj.append("Name", name);
        Obj.append("Embeddings", tempResult);
        Obj.append("userid", user.getId());
        Log.v("DataInsert", Obj.toString());
        //Document queryFilter = new Document("userid", user.getId());
        // Document queryFilter = new Document();


        //TODO Fetching all the data from the MongoDB
        RealmResultTask<MongoCursor<Document>> resultTask = mongoCollection.find().iterator();

        // Now, let's try to extract the cursor from the result task
        resultTask.getAsync(cursor -> {
            if (cursor.isSuccess()) {
                MongoCursor<Document> mongoCursor = cursor.get(); // Extracting the cursor

                try {
                    while (mongoCursor.hasNext()) {
                        Document foundDocument = mongoCursor.next();

                        if (foundDocument != null) {
                            String name = foundDocument.getString("Name");
                            String embeddings = foundDocument.getString("Embeddings");
                            String empCode = foundDocument.getString("EmployeeID");

                            if (name != null && embeddings != null && empCode != null) {

                                Utils.showToast(this, "Name and Embedding Fetched from the mongo");
                                String[] stringArray = embeddings.split(",");
                                final float[][] knownEmb = new float[1][stringArray.length];

                                for (int i = 0; i < stringArray.length; i++) {
                                    Log.d("MYTag", "Results : " + stringArray[i]);
                                    knownEmb[0][i] = Float.parseFloat(stringArray[i]);
                                }


                                // Create a new instance of FaceClassifier.Recognition
                                FaceClassifier.Recognition rec = new FaceClassifier.Recognition(
                                        empCode, // Generate a unique ID for each recognition
                                        name,
                                        null, // You might want to set distance to null initially
                                        null // Location can be null initially or set as needed
                                );

                                // Set the embedding for the recognition
                                rec.setEmbeeding(knownEmb);
                                Log.d("MAJID", rec.getId());

                                // Store the recognition in the registered hashmap with name as key
                                //UsesMap
                                getRegisteredHashMap().put(name, rec);


                                Log.v("RetrievedData", name + " " + embeddings);
                            } else {
                                Log.v("RetrievedData", "One or both fields are null");
                            }
                        } else {
                            Log.v("DataInsert", "No document found for the provided filter");
                        }

                        // Your processing logic here
                    }

                } finally {
                    Log.d("DataReadyTrack", "Data Ready value: ");
                    mongoCursor.close(); // Always close the cursor
                }
            } else {
                Log.v("DataInsert", cursor.getError().toString());
//                Toast.makeText(MainActivity.this, cursor.getError().toString(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    //TODO switch camera
    public void switchCamera() {

        Intent intent = getIntent();

        if (useFacing == CameraCharacteristics.LENS_FACING_FRONT) {
            useFacing = CameraCharacteristics.LENS_FACING_BACK;
        } else {
            useFacing = CameraCharacteristics.LENS_FACING_FRONT;
        }

        intent.putExtra(KEY_USE_FACING, useFacing);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

        restartWith(intent);

    }

    private void restartWith(Intent intent) {
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imageHelper.handleActivityResult(requestCode, resultCode, data);
    }

    public void handleImageBitmap(Bitmap bitmap) {
        imageViewToCrop.setImageBitmap(bitmap);
    }

    public void storeBitmap(Bitmap bitmap) {
        imageHelper.saveImageToGallery(bitmap);
    }


    public Bitmap getBitmap(Bitmap bitmap) {
        return bitmap;
    }


    public void initializeBitmap(Bitmap bitmap) {
        bitmapToStoreInGallery = bitmap;
    }

}