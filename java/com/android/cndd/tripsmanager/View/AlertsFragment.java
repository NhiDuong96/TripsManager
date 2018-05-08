package com.android.cndd.tripsmanager.view;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.cndd.tripsmanager.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.LoginException;

/**
 * Created by Minh Nhi on 3/11/2018.
 */

public class AlertsFragment extends Fragment
        implements RadioGroup.OnCheckedChangeListener, AdapterView.OnItemClickListener {
    private static final String TAG = "AlertsFragment";
    private AudioManager audioManager;
    private List<String> stringList;
    private HashMap<String, Uri> ringTones;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_alerts_fragment,container,false);
        RadioGroup mode1 = view.findViewById(R.id.mode);
        RadioButton silent = view.findViewById(R.id.silent);
        RadioButton ringing = view.findViewById(R.id.ringing);
        RadioButton vibrate = view.findViewById(R.id.vibrate);
        ListView listRingtones = view.findViewById(R.id.list_item);

        //fetch current Ringtone
        Uri currentRintoneUri = RingtoneManager.getActualDefaultRingtoneUri(getActivity(), RingtoneManager.TYPE_RINGTONE);
        Ringtone currentRingtone = RingtoneManager.getRingtone(getActivity(), currentRintoneUri);
        Toast.makeText(getContext(), currentRingtone.getTitle(getContext()), Toast.LENGTH_SHORT).show();

        ringTones = getNotifications();

        stringList = new ArrayList<>(ringTones.keySet());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, stringList);

        listRingtones.setAdapter(adapter);
        listRingtones.setOnItemClickListener(this);


        mode1.setOnCheckedChangeListener(this);
        audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        int mode = audioManager.getRingerMode();
        switch (mode){
            case AudioManager.RINGER_MODE_SILENT:
                silent.setChecked(true);
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
                vibrate.setChecked(true);
                break;
            case  AudioManager.RINGER_MODE_NORMAL:
                ringing.setChecked(true);
                break;
        }
        return view;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        try {
            switch (checkedId) {
                case R.id.silent:
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    Toast.makeText(getContext(), "Now in Silent Mode", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.vibrate:
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                    Toast.makeText(getContext(), "Now in Vibrate Mode", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.ringing:
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    Toast.makeText(getContext(), "Now in Ringing Mode", Toast.LENGTH_SHORT).show();
                    break;
            }
        }catch (SecurityException ex){
            Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public HashMap<String, Uri> getNotifications() {
        RingtoneManager manager = new RingtoneManager(getContext());

        manager.setType(RingtoneManager.TYPE_NOTIFICATION);
        Cursor cursor = manager.getCursor();

        HashMap<String, Uri> list = new HashMap<>();
        while (cursor.moveToNext()) {
            String notificationTitle = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX);
            list.put(notificationTitle, cursor.getNotificationUri());
        }

        return list;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        youDesirePermissionCode(getActivity());
        RingtoneManager.setActualDefaultRingtoneUri(getContext(),
                RingtoneManager.TYPE_NOTIFICATION, ringTones.get(stringList.get(position)));
        Toast.makeText(getContext(),"Set: " + stringList.get(position), Toast.LENGTH_SHORT).show();
    }

    public static void youDesirePermissionCode(Activity context){
        boolean permission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permission = Settings.System.canWrite(context);
        } else {
            permission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_SETTINGS) == PackageManager.PERMISSION_GRANTED;
        }
        if (permission) {
            //do your code
        }  else {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + context.getPackageName()));
                context.startActivityForResult(intent, 1000);
            } else {
                ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.WRITE_SETTINGS}, 1000);
            }
        }
    }
}
