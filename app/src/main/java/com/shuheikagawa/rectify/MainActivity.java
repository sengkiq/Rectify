package com.shuheikagawa.rectify;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;


public class MainActivity extends Activity {
    private final static String DEBUG_TAG = "MainActivity";
    private boolean openCVLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(DEBUG_TAG, "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Initialize OpenCV.
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, this, openCVLoaderCallback);
    }

    public void onRectifyButtonClick(View view) {
        if (!openCVLoaded) {
            Toast.makeText(this, "OpenCV is not yet loaded.", Toast.LENGTH_LONG).show();
            return;
        }

        // Find image views.
        ImageView sourceImageView = (ImageView) findViewById(R.id.source_image_view);
        ImageView destinationImageView = (ImageView) findViewById(R.id.destination_image_view);

        // Get the bitmap from the image view.
        Drawable drawable = sourceImageView.getDrawable();
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

        // Create an OpenCV mat from the bitmap.
        Mat srcMat = bitmapToMat(bitmap);

        // Find and draw rectangles.
        RectFinder rectFinder = new RectFinder();
        Mat dstMat = rectFinder.drawRectangles(srcMat);

        // Create a bitmap from the result mat.
        Bitmap resultBitmap = matToBitmap(dstMat);
        Utils.matToBitmap(dstMat, resultBitmap);

        // Show the result bitmap on the destination image view.
        destinationImageView.setImageBitmap(resultBitmap);
    }

    private Mat bitmapToMat(Bitmap bitmap) {
        Mat mat = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8U, new Scalar(4));
        Bitmap bitmap32 = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Utils.bitmapToMat(bitmap32, mat);
        return mat;
    }

    private Bitmap matToBitmap(Mat mat) {
        Bitmap bitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, bitmap);
        return bitmap;
    }

    private BaseLoaderCallback openCVLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            if (status != LoaderCallbackInterface.SUCCESS) {
                Log.e(DEBUG_TAG, "Failed to load OpenCV.");
                super.onManagerConnected(status);
                return;
            }

            openCVLoaded = true;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(DEBUG_TAG, "onCreateOptionsMenu");

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(DEBUG_TAG, "onOptionsItemSelected");

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
