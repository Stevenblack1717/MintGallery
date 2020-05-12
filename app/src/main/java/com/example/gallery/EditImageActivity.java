package com.example.gallery;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewpager.widget.ViewPager;

import com.example.gallery.Adapter.EmojiAdapter;
import com.example.gallery.Adapter.FilterViewPagerAdapter;
import com.example.gallery.Interface.AddStickerListener;
import com.example.gallery.Interface.BrushFragmentListener;
import com.example.gallery.Interface.EditImageFragmentListener;
import com.example.gallery.Interface.EmojiFragmentListener;
import com.example.gallery.Interface.FilterListFragmentListener;
import com.example.gallery.Utils.BitmapUtils;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubfilter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;

import ja.burhanrashid52.photoeditor.OnSaveBitmap;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;

public class EditImageActivity extends AppCompatActivity implements FilterListFragmentListener, EditImageFragmentListener, BrushFragmentListener, EmojiFragmentListener, AddStickerListener {
    public static final int PERMISSION_PICK_IMAGE = 100;
    public static String path = null;
    public static int CurPosition;
    ArrayList<ImageInformation> arrayList;

    PhotoEditorView imageView;
    PhotoEditor photoEditor;

    CardView btnFiltersList, btnEditList, btnBrush, btnEmoji, btnSticker;

    CoordinatorLayout coordinatorLayout;

    Bitmap originalBitmap, filteredBitmap, finalBitmap;

    FilterListFragment filterListFragment;
    EditImageFragment editImageFragment;

    int brightnessFinal = 0;
    float saturationFinal = 1.0f;
    float contrastFinal = 1.0f;
    OutputStream outputStream;
    Context context;
    static {
        System.loadLibrary("NativeImageProcessor");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        context =getApplicationContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_filter_main_layout);

        Toolbar toolbar =  findViewById(R.id.toolbarFilter);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit");

        arrayList = (ArrayList<ImageInformation>) getIntent().getSerializableExtra("array");
        path=getIntent().getExtras().getString("path");
        System.out.println("EditPath"+path);
        CurPosition=getIntent().getExtras().getInt("pos");

        imageView = (PhotoEditorView) findViewById(R.id.imageFilter);
        photoEditor =new PhotoEditor.Builder(this,imageView)
                .setPinchTextScalable(true)
                .setDefaultEmojiTypeface(Typeface.createFromAsset(getAssets(),"NotoColorEmoji.ttf"))
                .build();
        coordinatorLayout = (CoordinatorLayout)findViewById(R.id.coordinator);
        btnEditList =(CardView)findViewById(R.id.btnEditList);
        btnFiltersList=(CardView)findViewById(R.id.btnFiltersList);
        btnBrush=(CardView)findViewById(R.id.btnBrush);
        btnEmoji=(CardView)findViewById(R.id.btnEmoji);
        btnSticker=(CardView)findViewById(R.id.btnSticker);

        btnEditList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditImageFragment editImageFragment = EditImageFragment.getInstance();
                editImageFragment.setListener(EditImageActivity.this);
                editImageFragment.show(getSupportFragmentManager(),editImageFragment.getTag());
            }
        });
        btnFiltersList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FilterListFragment filterListFragment = FilterListFragment.getInstance();
                filterListFragment.setListener(EditImageActivity.this);
                filterListFragment.show(getSupportFragmentManager(),filterListFragment.getTag());
            }
        });
        btnBrush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Enable brush mode
                photoEditor.setBrushDrawingMode(true);

                BrushFragment brushFragment = BrushFragment.getInstance();
                brushFragment.setListener(EditImageActivity.this);
                brushFragment.show(getSupportFragmentManager(),brushFragment.getTag());
            }
        });
        btnEmoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EmojiFragment emojiFragment = EmojiFragment.getInstance();
                emojiFragment.setListner(EditImageActivity.this);
                emojiFragment.show(getSupportFragmentManager(),emojiFragment.getTag());
            }
        });
        btnSticker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StickerFragment stickerFragment = StickerFragment.getInstance();
                stickerFragment.setListener(EditImageActivity.this);
                stickerFragment.show(getSupportFragmentManager(),stickerFragment.getTag());
            }
        });
        loadImg();

    }

    private void loadImg() {
        originalBitmap = BitmapUtils.getBitmapFromGallery(this,path,300,300);
        filteredBitmap =originalBitmap.copy(Bitmap.Config.ARGB_8888,true);
        finalBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888,true);
        imageView.getSource().setImageBitmap(originalBitmap);
    }

    private void setupViewPager(ViewPager viewPager) {
        FilterViewPagerAdapter adapter = new FilterViewPagerAdapter(getSupportFragmentManager(),1);

        filterListFragment = new FilterListFragment();
        filterListFragment.setListener(this);

        editImageFragment=new EditImageFragment();
        editImageFragment.setListener(this);

        adapter.addFragment(filterListFragment,"FILTER");
        adapter.addFragment(editImageFragment,"EDIT");

        viewPager.setAdapter(adapter);
    }

    @Override
    public void onBrightnessChanged(int brightness) {
        brightnessFinal =brightness;
        Filter myFilter =new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(brightness));
        imageView.getSource().setImageBitmap(myFilter.processFilter(finalBitmap.copy(Bitmap.Config.ARGB_8888,true)));
    }

    @Override
    public void onSaturationChanged(float saturation) {
        saturationFinal =saturation;
        Filter myFilter =new Filter();
        myFilter.addSubFilter(new SaturationSubfilter(saturation));
        imageView.getSource().setImageBitmap(myFilter.processFilter(finalBitmap.copy(Bitmap.Config.ARGB_8888,true)));
    }

    @Override
    public void onContrastChanged(float contrast) {
        saturationFinal =contrast;
        Filter myFilter =new Filter();
        myFilter.addSubFilter(new ContrastSubFilter(contrast));
        imageView.getSource().setImageBitmap(myFilter.processFilter(finalBitmap.copy(Bitmap.Config.ARGB_8888,true)));
    }

    @Override
    public void onEditStarted() {

    }

    @Override
    public void onEditCompleted() {
        Bitmap bitmap = filteredBitmap.copy(Bitmap.Config.ARGB_8888,true);
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(brightnessFinal));
        myFilter.addSubFilter(new ContrastSubFilter(contrastFinal));
        myFilter.addSubFilter(new SaturationSubfilter(saturationFinal));

        finalBitmap=myFilter.processFilter(bitmap);
    }

    @Override
    public void onFilterSelected(Filter filter) {
        resetControl();
        filteredBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888,true);
        imageView.getSource().setImageBitmap(filter.processFilter(filteredBitmap));
        finalBitmap=filteredBitmap.copy(Bitmap.Config.ARGB_8888,true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_image_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id =item.getItemId();
        switch (id){
            case R.id.action_save:
                saveImageToGallery();
                return true;
            case R.id.action_redo:
                photoEditor.redo();
                return true;
            case R.id.action_undo:
                photoEditor.undo();
                return true;
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveImageToGallery(){
        photoEditor.saveAsBitmap(new OnSaveBitmap() {
            @Override
            public void onBitmapReady(Bitmap saveBitmap) {

                imageView.getSource().setImageBitmap(saveBitmap);
                BitmapDrawable drawable = (BitmapDrawable) imageView.getSource().getDrawable();
                saveBitmap = drawable.getBitmap();
                File dir = new File(
                        Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_DCIM
                        ), "MintGallery"
                );
                //File filepath = Environment.getExternalStorageDirectory();
                //File dir = new File("file://"+"/DCIM/MintGallery/");
                //MediaStore.Images.Media.insertImage(getContentResolver(), saveBitmap,"MintEditor","Photo By Mint");
                //String dir = Environment.getExternalStorageDirectory() + "/MintGallery";
                //final File test = new File (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/MintGallery/");
                //File direct = new File("file://"+"/DCIM/MintGallery/");
                //File file = new File(test,+System.currentTimeMillis()+".png");
                File storageDir = Environment.getExternalStorageDirectory();
                File direct = new File(storageDir.getAbsolutePath() + "/DCIM/MintGallery/");

                if (!direct.exists()) {
                    direct.mkdirs();
                } else {
                    File file = new File(direct, +System.currentTimeMillis() + ".jpeg");
                    if (!file.exists()) {
                        try {
                            outputStream = new FileOutputStream(file);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        for (int i = 0; i < arrayList.size(); i++) {
                            if (arrayList.get(i).getPath().equals(file.getPath())) {
                                CurPosition = i;
                            }
                        }

                        ImageInformation information = new ImageInformation();
                        information.setSelected(false);
                        information.setName(file.getName());
                        information.setPath(file.getAbsolutePath());
                        information.setThumb(file.getAbsolutePath());
                        information.setSize(file.length());
                        Date date = new Date();
                        information.setDateTaken(date);
                        arrayList.add(0, information);
                        System.out.println(arrayList);

                    }
                    saveBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    MediaScannerConnection.scanFile(context,
                            new String[] { file.toString() }, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String path, Uri uri) {
                                    Log.i("ExternalStorage", "Scanned " + path + ":");
                                    Log.i("ExternalStorage", "-> uri=" + uri);
                                }
                            });
                    Toast.makeText(getApplicationContext(), "Save successfully", Toast.LENGTH_SHORT).show();
                    try {
                        outputStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        outputStream.close();
                        System.out.println("sagr");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    openImage(file);
                }
            }

            @Override
            public void onFailure(Exception e) {

            }
        });

    }

    private void openImage(File file) {
        Intent i= new Intent(EditImageActivity.this,FullImageActivity.class);
        i.putExtra("list",arrayList);
        i.putExtra("id",arrayList.get(0).getPath());
        i.putExtra("position",0);
        startActivity(i);
    }

    private void resetControl() {
        if(editImageFragment!=null){
            editImageFragment.resetControls();
        }
        brightnessFinal=0;
        saturationFinal=1.0f;
        contrastFinal=1.0f;
    }

    @Override
    public void onBrushSizeChangedListener(float size) {
        photoEditor.setBrushSize(size);
    }

    @Override
    public void onBrushOpacityChangedListener(int opacity) {
        photoEditor.setOpacity(opacity);
    }

    @Override
    public void onBrushColorChangedListener(int color) {
        photoEditor.setBrushColor(color);
    }

    @Override
    public void onBrushStateChangedListener(boolean isErazer) {
        if(isErazer){
            photoEditor.brushEraser();
        }
        else
            photoEditor.setBrushDrawingMode(true);
    }

    @Override
    public void onEmojiSelected(String emoji) {
        photoEditor.addEmoji(emoji);
    }


    @Override
    public void onAddSticker(int sticker) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),sticker);
        photoEditor.addImage(Bitmap.createScaledBitmap(bitmap, 300, 300, false));
    }
}
