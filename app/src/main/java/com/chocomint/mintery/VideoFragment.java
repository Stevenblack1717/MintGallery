package com.chocomint.mintery;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.function.Predicate;

public class VideoFragment extends Fragment implements ChooseFileCallback {
    Context context;
    RecyclerView recyclerView;
    ArrayList<Media> videoList;
    ArrayList<String> fileChoose;
    ImageAdapter adapter;
    ChooseFileAdapter chooseFileAdapter;
    String from;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout_photo = null;

        fileChoose = new ArrayList<>();
        Bundle bundle = getArguments();
        if (bundle != null) {
            from = bundle.getString("from", "");
            videoList = (ArrayList<Media>) bundle.getSerializable("list");
            if (videoList != null && videoList.size() > 0) {
                layout_photo = (LinearLayout) inflater.inflate(R.layout.fragment_video_layout, null);
                recyclerView = layout_photo.findViewById(R.id.video_recycle);
                if (from.compareTo("DELETE") == 0) {
                    chooseFileAdapter = new ChooseFileAdapter(getActivity(), videoList, this);
                    recyclerView.setAdapter(chooseFileAdapter);

                    final GridLayoutManager manager = new GridLayoutManager(this.getActivity(), 4);
                    recyclerView.setLayoutManager(manager);
                    manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                        @Override
                        public int getSpanSize(int position) {
                            return chooseFileAdapter.isHeader(position) ? manager.getSpanCount() : 1;
                        }
                    });
                } else {
                    adapter = new ImageAdapter(getActivity(), videoList);
                    recyclerView.setAdapter(adapter);

                    final GridLayoutManager manager = new GridLayoutManager(this.getActivity(), 4);
                    recyclerView.setLayoutManager(manager);
                    manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                        @Override
                        public int getSpanSize(int position) {
                            return adapter.isHeader(position) ? manager.getSpanCount() : 1;

                        }
                    });
                }
            } else {
                layout_photo = (ConstraintLayout) inflater.inflate(R.layout.video_no_item, null);
            }
        }

        return layout_photo;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void chooseFile(int position, boolean add) {
        if (add) {
            if (fileChoose.indexOf(String.valueOf(videoList.get(position).id)) < 0) {
                fileChoose.add(String.valueOf(videoList.get(position).id));
            }
        } else {
            fileChoose.remove(String.valueOf(videoList.get(position).id));
        }
    }

    @Override
    public int findChooseFile(int id) {
        return fileChoose.indexOf(String.valueOf(id));
    }

    public void ShareVideo() {
        new VideoFragment.ShareThread().execute();
    }

    private class ShareThread extends AsyncTask <Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                if (fileChoose != null && fileChoose.size() > 0) {
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
                    ArrayList<Uri> files = new ArrayList<>();
                    for (String id : fileChoose) {
                        files.add(Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id));
                    }
                    shareIntent.putExtra(Intent.EXTRA_STREAM, files);
                    shareIntent.setType("video/*");
                    startActivity(Intent.createChooser(shareIntent, "Share videos"));
                } else {
                    return false;
                }
            } catch (ActivityNotFoundException e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (!aBoolean && fileChoose.size() <= 0) {
                Toast.makeText(getContext(), "You did not choose any video", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void DeleteVideos() {
        if (fileChoose.size() > 0) {
            AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(getContext());
            myAlertDialog.setTitle("Delete videos");
            myAlertDialog.setMessage("Do you want to delete all of them?");
            myAlertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    new VideoFragment.DeleteThread().execute();
                }
            });
            myAlertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) { }
            });
            myAlertDialog.show();
        } else {
            Toast.makeText(getContext(), "You didn't choose any videos.", Toast.LENGTH_LONG).show();
        }
    }

    private class DeleteThread extends AsyncTask <Void, Boolean, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                for (String id : fileChoose) {
                    getActivity().getContentResolver().delete(Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id), null, null);
                }
            } catch (Throwable e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                getActivity().onBackPressed();
            } else {
                Toast.makeText(getContext(), "Error. Try again later", Toast.LENGTH_LONG);
            }
        }
    }
}

