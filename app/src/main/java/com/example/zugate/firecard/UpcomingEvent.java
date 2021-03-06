package com.example.zugate.firecard;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zugate.firecard.Play.HomeScreen;
import com.example.zugate.firecard.TeamDevAchi.Blog;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.flaviofaria.kenburnsview.RandomTransitionGenerator;
import com.flaviofaria.kenburnsview.Transition;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.io.FileOutputStream;
import java.io.IOException;

public class UpcomingEvent extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DatabaseReference mDatabase;
    static DatabaseReference zref;
    RecyclerView.LayoutManager layoutManager;
    KenBurnsView kenBurnsView;
    private Boolean moving=true;
    public static char ch;
    public static FileOutputStream outputStream;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Intent intent=new Intent(UpcomingEvent.this,MainActivity.class);
                    startActivity(intent);
                    break;
                case R.id.navigation_dashboard:
                    Intent intent2=new Intent(UpcomingEvent.this,HomeScreen.class);
                    startActivity(intent2);
                    break;
                case R.id.navigation_notifications:
                    Intent intent3=new Intent(UpcomingEvent.this,Notification.class);
                    startActivity(intent3);
                    break;
            }
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming_event);

        try

        {
            outputStream = openFileOutput("file", Context.MODE_PRIVATE);
        }
        catch(IOException e){

        }

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        kenBurnsView=(KenBurnsView)findViewById(R.id.image);
        AccelerateDecelerateInterpolator ACCELERATE_DECELERATE = new AccelerateDecelerateInterpolator();
        RandomTransitionGenerator generator = new RandomTransitionGenerator(10000, ACCELERATE_DECELERATE);
        kenBurnsView.setTransitionGenerator(generator);

        kenBurnsView.setTransitionListener(onTransittionListener());

        kenBurnsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(moving){
                    kenBurnsView.pause();
                    moving=false;
                }
                else{
                    kenBurnsView.resume();;
                    moving=true;
                }
            }
        });


        mDatabase = FirebaseDatabase.getInstance().getReference().child("Upcoming");
        mDatabase.keepSynced(true);

        recyclerView =
                (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    private KenBurnsView.TransitionListener onTransittionListener() {
        return new KenBurnsView.TransitionListener() {

            @Override
            public void onTransitionStart(Transition transition) {

                // Toast.makeText(MainActivity.this, "start", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                // Toast.makeText(MainActivity.this, "end", Toast.LENGTH_SHORT).show();
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Blog, UpcomingEvent.BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Blog, UpcomingEvent.BlogViewHolder>
                (Blog.class, R.layout.cardview, UpcomingEvent.BlogViewHolder.class, mDatabase) {
            @Override
            protected void populateViewHolder(UpcomingEvent.BlogViewHolder viewHolder, Blog model, int position) {
                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDesc());
                viewHolder.setImage(getApplicationContext(), model.getImage());
            }
        };
                    recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder {
        View mview;

        public BlogViewHolder(final View itemView) {
            super(itemView);
            mview = itemView;
           itemView.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {

                  try {
//                      save(getPosition());
                      Intent intent = new Intent(itemView.getContext(), EventDetail.class);

//            zref = FirebaseDatabase.getInstance().getReference().child("up").child("clickpos");
//            zref.setValue(getPosition());
                      intent.putExtra("posclicked",getPosition());

                      String fileContents = getPosition()+"";


                      try {
                          outputStream.write(fileContents.getBytes());

                      }

                      catch (Exception e) {
                          e.printStackTrace();

                      }

                      itemView.getContext().startActivity(intent);

                  }
                  catch (Exception e)
                  {
                      Intent i=new Intent(itemView.getContext(),MainActivity.class);
                      Toast.makeText(itemView.getContext(),"Can't load",Toast.LENGTH_SHORT).show();
                      itemView.getContext().startActivity(i);

                      e.printStackTrace();
                  }
                }
            });
        }

        public void save(int pos)
        {


        }
        public void setTitle(String title) {
            TextView post_title = (TextView) mview.findViewById(R.id.item_title);
            post_title.setText(title);
        }

        public void setDesc(String desc) {
            TextView post_desc = (TextView) mview.findViewById(R.id.item_desc);
            post_desc.setText(desc);
        }

        public void setImage(Context ctx, String image) {
            ImageView post_Image = (ImageView)mview.findViewById(R.id.item_image);
            Picasso.get().load(image).placeholder(R.drawable.back).into(post_Image);
        }
    }
}
