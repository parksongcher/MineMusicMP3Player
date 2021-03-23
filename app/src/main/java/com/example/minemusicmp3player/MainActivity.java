package com.example.minemusicmp3player;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.FrameLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //activity_main id
    private DrawerLayout drawerLayout;
    private FrameLayout frameLayout;
    private RecyclerView recyclerView;
    private RecyclerView recyclerLike;

    //musicData musicArrayList
    private ArrayList<MusicData> musicDataArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //find id activity_main
        findViewByIdFunc();

        //sd카드(외부접근) 권한 설정
        requestPermissionsFunc();

        //어뎁터 생성
        MusicAdapter musicAdapter = new MusicAdapter(getApplicationContext());

        //recyclerView에서 LinearLayoutManager를 적용시켜야 된다.
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());

        //recyclerView에 LinearLayoutManager를 적용
        recyclerView.setAdapter(musicAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);

        //ArrayList<MusicData>를 가져와서 musicAdater에 적용시켜야 된다.
        musicDataArrayList = findMusic();
        musicAdapter.setMusicList(musicDataArrayList);
        musicAdapter.notifyDataSetChanged();

        //현재 액티비티에 있는 프레임레잉아웃에 프래그먼트 지정
        replaceFrag();
    }

    // sdCard 안의 음악을 검색한다
    public ArrayList<MusicData> findMusic() {
        ArrayList<MusicData> sdCardList = new ArrayList<>();

        String[] data = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.DURATION};

        // 특정 폴더에서 음악 가져오기
        // String selection = MediaStore.Audio.Media.DATA + " like ? ";
        // String selectionArqs = new String[]{"%MusicList%"}

        // 전체 영역에서 음악 가져오기
        Cursor cursor = getApplicationContext().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                data, null, null, data[2] + " ASC");

        if (cursor != null) {
            while (cursor.moveToNext()) {

                // 음악 데이터 가져오기
                String id       = cursor.getString(cursor.getColumnIndex(data[0]));
                String artist   = cursor.getString(cursor.getColumnIndex(data[1]));
                String title    = cursor.getString(cursor.getColumnIndex(data[2]));
                String albumArt = cursor.getString(cursor.getColumnIndex(data[3]));
                String duration = cursor.getString(cursor.getColumnIndex(data[4]));

                MusicData mData = new MusicData(id, artist, title, albumArt, duration, 0, 0);

                sdCardList.add(mData);
            }
        }

        return sdCardList;
    }

    //sd카드(외부파일)를 접근하려고 하는데 허용하시겠습니까?
    private void requestPermissionsFunc() {
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                MODE_PRIVATE);
    }

    //현재 액티비티에 있는 프레임레잉아웃에 프래그먼트 지정
    private void replaceFrag() {
        //플래그먼트 생성
        Player player = new Player();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayout, player);
        ft.commit();
    }

    private void findViewByIdFunc() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        frameLayout = (FrameLayout) findViewById(R.id.frameLayout);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerLike = (RecyclerView) findViewById(R.id.recyclerLike);

    }
}