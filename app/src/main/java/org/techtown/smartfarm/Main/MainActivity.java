package org.techtown.smartfarm.Main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.techtown.smartfarm.Chatting;
import org.techtown.smartfarm.Full.FullScreen;
import org.techtown.smartfarm.Login.Login;
import org.techtown.smartfarm.Login.Register;
import org.techtown.smartfarm.Notification;
import org.techtown.smartfarm.R;

public class MainActivity extends AppCompatActivity {

    DatabaseReference databaseReference;
    FirebaseDatabase database;
    RecyclerView recyclerView;
    String name, url;

    FirebaseUser user;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 툴바 설정
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        // 파이어베이스 데이터베이스에서 동영상 불러서 리스트로 띄워주기
        recyclerView = findViewById(R.id.main_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("video");

        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
    }

    @Override
    protected void onStart() {  // Firebase 데이터베이스에서 동영상 불러오기
        super.onStart();

        Query database = databaseReference.orderByChild("time");  // 시간을 기준으로 정렬
        FirebaseRecyclerOptions<Data> options =
                new FirebaseRecyclerOptions.Builder<Data>()
                        .setQuery(database, Data.class)
                        .build();

        FirebaseRecyclerAdapter<Data, ViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Data, ViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Data model) {
                        holder.setExoplayer(getApplication(), model.getName(), model.getVideoUrl(), model.getTime(), model.getAbaction());

                        holder.setOnClickListener(new ViewHolder.ClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {  // 리스트 한 행 클릭하면 영상보는 화면으로 이동
                                name = getItem(position).getName();
                                url = getItem(position).getVideoUrl();
                                Intent intent = new Intent(MainActivity.this, FullScreen.class);
                                intent.putExtra("name", name);
                                intent.putExtra("url", url);
                                startActivity(intent);
                            }

                            @Override
                            public void onItemLongClick(View view, int position) {
                                name = getItem(position).getName();
                                showDeleteDialog(name);  // 리스트 꾹 눌러서 삭제하기
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_pigpen, parent, false);

                        return new ViewHolder(view);
                    }
                };

        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    private void firebaseSearch(String searchText) {  // 동영상 이름을 검색하여 찾기
        String query = searchText.toLowerCase();
        Query firebaseQuery = databaseReference.orderByChild("search").startAt(query).endAt(query + "\uf8ff");

        FirebaseRecyclerOptions<Data> options =
                new FirebaseRecyclerOptions.Builder<Data>()
                        .setQuery(firebaseQuery, Data.class)
                        .build();

        FirebaseRecyclerAdapter<Data, ViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Data, ViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Data model) {
                        holder.setExoplayer(getApplication(), model.getName(), model.getVideoUrl(), model.getTime(), model.getAbaction());

                        holder.setOnClickListener(new ViewHolder.ClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {  // 리스트 한 행 클릭하면 영상보는 화면으로 이동
                                name = getItem(position).getName();
                                url = getItem(position).getVideoUrl();
                                Intent intent = new Intent(MainActivity.this, FullScreen.class);
                                intent.putExtra("name", name);
                                intent.putExtra("url", url);
                                startActivity(intent);
                            }

                            @Override
                            public void onItemLongClick(View view, int position) {
                                name = getItem(position).getName();
                                showDeleteDialog(name);  // 리스트 꾹 눌러서 삭제하기
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_pigpen, parent, false);

                        return new ViewHolder(view);
                    }
                };

        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    @Override  // 툴바에 아이콘 추가 및 화면 전환
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_chat:
                Intent weeklyIntent = new Intent(this, Chatting.class);
                startActivity(weeklyIntent);
                break;

            case R.id.menu_notification:
                Intent settingIntent = new Intent(this, Notification.class);
                startActivity(settingIntent);
                break;

            case R.id.menu_logout:
                logout();
        }
        return true;
    }

    public void logout() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                .setTitle("로그아웃")
                .setMessage("정말로 로그아웃하시겠습니까?")
                .setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(getApplicationContext(), Login.class));
                        finish();
                    }
                }).setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);  // 메뉴 설정

        // Search 메뉴 설정
        MenuItem item = menu.findItem(R.id.search_firebase);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                firebaseSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                firebaseSearch(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void showDeleteDialog(String name) {  // 리스트 꾹 눌러서 삭제하기
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("삭제");
        builder.setMessage("동영상을 삭제하시겠습니까?");
        builder.setPositiveButton("네", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Query query = databaseReference.orderByChild("name").equalTo(name);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                            dataSnapshot1.getRef().removeValue();
                        }
                        Toast.makeText(MainActivity.this, "동영상이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}