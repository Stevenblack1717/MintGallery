package com.chocomint.mintery;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class AlbumDetailFragment extends Fragment  {
    Context context;
    RecyclerView recyclerView;
    ArrayList<Media> arrayList;
    MediaAdapter adapter;
    ChooseFileAdapter chooseFileAdapter;
    String from;
    ArrayList<String> fileChoose;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout_photo = null;

        fileChoose = new ArrayList<>();
        Bundle bundle = getArguments();
        if (bundle != null) {
            from = bundle.getString("from", "");
            arrayList = (ArrayList<Media>) bundle.getSerializable("list");
            if (arrayList != null && arrayList.size() > 0) {
                layout_photo = (LinearLayout) inflater.inflate(R.layout.fragment_detail_album_layout, null);
                recyclerView = layout_photo.findViewById(R.id.media_reycle);
                if (from.compareTo("DELETE") == 0) {
//                    chooseFileAdapter = new ChooseFileAdapter(getActivity(), arrayList, this);
//                    recyclerView.setAdapter(chooseFileAdapter);
                } else {
                    adapter = new MediaAdapter(getActivity(), arrayList);
                    recyclerView.setAdapter(adapter);
                }

                recyclerView.setLayoutManager(new GridLayoutManager(this.getActivity(), 4));
            } else {
                layout_photo = (ConstraintLayout) inflater.inflate(R.layout.no_item, null);
            }
        }

        return layout_photo;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }
//
//    @Override
//    public void chooseFile(int position, boolean add) {
//        if (add) {
//            fileChoose.add(String.valueOf(arrayList.get(position).id));
//        } else {
//            fileChoose.remove(String.valueOf(arrayList.get(position)));
//        }
//    }

    public void SharePhoto() {
        new AlbumDetailFragment.ShareThread().execute();
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
                        files.add(Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id));
                    }
                    shareIntent.putExtra(Intent.EXTRA_STREAM, files);
                    shareIntent.setType("image/*");
                    startActivity(Intent.createChooser(shareIntent, "Share images"));
                } else {
                    Toast.makeText(getContext(), "You did not choose any photo", Toast.LENGTH_LONG).show();
                }
            } catch (ActivityNotFoundException e) {
                return false;
            }
            return true;
        }
    }

    public void DeletePhotos() {
        new AlbumDetailFragment.DeleteThread().execute();
    }

    private class DeleteThread extends AsyncTask <Void, Boolean, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                for (String id : fileChoose) {
                    getActivity().getContentResolver().delete(Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id), null, null);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
