package com.android.cndd.tripsmanager.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.cndd.tripsmanager.modules.JsonUrlLoader;
import com.android.cndd.tripsmanager.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.ExecutionException;

/**
 * Created by Minh Nhi on 3/11/2018.
 */

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    private static final String URL = "https://www.googleapis.com/plus/v1/people/";
    private static final String API_KEY = "AIzaSyBehRrJCJXU07UgGl_kxvdi63xmK9kaWxU";
    private View view;
    private boolean isUpdate = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.main_profile_fragment, container, false);
        loadUser();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(isUpdate) {
            loadUser();
            isUpdate = false;
        }
    }

    public void loadUser(){
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
        if(account == null) return;

        TextView name, email;
        name = view.findViewById(R.id.name);
        name.setText(account.getDisplayName());
        email = view.findViewById(R.id.email);
        email.setText(account.getEmail());
        ImageView icon = view.findViewById(R.id.icon_user);
        Bitmap bitmap = getBitmapFromInternalStorage();
        if (bitmap == null) {
            Log.e(TAG, "onCreateView: " + "No bitmap in cache file");
            bitmap = loadBitmapFromUrl(URL + account.getId() + "?fields=image&key=" + API_KEY);
            saveBitmapToInternalStorage(bitmap);
        }
        icon.setImageBitmap(bitmap);

        ImageButton sign_out = view.findViewById(R.id.logout);
        sign_out.setOnClickListener((v) ->{
            //logout
            isUpdate = true;
            deleteBitmapFromInternalStorage();
            Intent intent = new Intent(getActivity(),SignInActivity.class);
            startActivity(intent);
        });
    }

    private Bitmap loadBitmapFromUrl(String url){
        Bitmap bitmap = null;
        try {
            String json = new JsonUrlLoader().execute(url).get();
            JSONObject jsonObject = new JSONObject(json);
            JSONObject image = jsonObject.getJSONObject("image");
            bitmap = new LoadProfileImage().execute(image.getString("url")).get();
        } catch (InterruptedException | ExecutionException | JSONException e) {
            Log.e(TAG, "loadBitmapFromUrl: ", e);
        }
        if(bitmap == null){
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icons8_user);
            Log.e(TAG, "loadBitmapFromUrl: " + bitmap.toString());
        }
        return bitmap;
    }

    public boolean saveBitmapToInternalStorage(Bitmap image) {
        try {
            // Use the compress method on the Bitmap object to write image to
            // the OutputStream
            FileOutputStream fos = getContext().openFileOutput("icon.png", Context.MODE_PRIVATE);
            // Writing the bitmap to the output stream
            image.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            return true;
        } catch (Exception e) {
            Log.e("saveToInternalStorage()", e.getMessage());
            return false;
        }
    }

    public void deleteBitmapFromInternalStorage(){
        getContext().deleteFile("icon.png");
    }

    public Bitmap getBitmapFromInternalStorage(){
        Bitmap bitmap = null;
        try {
            File filePath = getContext().getFileStreamPath("icon.png");
            FileInputStream fi = new FileInputStream(filePath);
            bitmap = BitmapFactory.decodeStream(fi);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private static class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {
        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }
    }
}
