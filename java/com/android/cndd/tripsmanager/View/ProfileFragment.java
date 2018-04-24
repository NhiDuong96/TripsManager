package com.android.cndd.tripsmanager.View;

import android.app.Activity;
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
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.cndd.tripsmanager.Modules.JsonUrlLoader;
import com.android.cndd.tripsmanager.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

/**
 * Created by Minh Nhi on 3/11/2018.
 */

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    private static final String URL = "https://www.googleapis.com/plus/v1/people/";
    private static final String API_KEY = "AIzaSyBehRrJCJXU07UgGl_kxvdi63xmK9kaWxU";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_profile_fragment, container, false);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
        if(account == null) return view;

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

        Button button = view.findViewById(R.id.logout);
        button.setOnClickListener((v) ->{
            //logout
            deleteBitmapFromInternalStorage();
            Intent intent = new Intent(getActivity(),SignInActivity.class);
            startActivityForResult(intent, 1000);
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000){
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout,new ProfileFragment())
                    .commit();
        }
    }

    private Bitmap loadBitmapFromUrl(String url){
        Bitmap bitmap = null;
        try {
            String json = new JsonUrlLoader().execute(url).get();
            JSONObject jsonObject = new JSONObject(json);
            JSONObject image = jsonObject.getJSONObject("image");
            bitmap = new LoadProfileImage().execute(image.getString("url")).get();
        } catch (InterruptedException | ExecutionException | JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "loadBitmapFromUrl: ", e);
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_background);
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
