package Program.Tech;

import android.content.Intent;
import android.os.Bundle;
import android.transition.Slide;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class Slideview extends AppCompatActivity {

    private Toolbar toolBar;
    private DrawerLayout drawerLayout;
    private NavigationView nav;
    private BottomNavigationView bottomNavigationView;

    private TextView textView;
    private HomeFragment homeFragment;
    private SearchFragment searchFragment;

    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_slideview);

        // Initialize intent and get username
        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        toolBar = findViewById(R.id.toolBar);
        setSupportActionBar(toolBar);

        drawerLayout = findViewById(R.id.drawLayout);
        nav = findViewById(R.id.nav);
        bottomNavigationView = findViewById(R.id.bottomNavigation);

        View headerView = nav.getHeaderView(0);
        textView = headerView.findViewById(R.id.welcomeTextView);

        // 设置用户名到侧边栏头部布局
        textView.setText("Welcome " + username + " !");

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.home) {
                    selectedFragment(0);
                } else if (item.getItemId() == R.id.search) {
                    selectedFragment(1);
                } else if (item.getItemId() == R.id.donation) {
                    Intent intent = new Intent(Slideview.this, postPublish.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                }
                return true;
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolBar, R.string.open, R.string.close);
        toggle.syncState();
        drawerLayout.addDrawerListener(toggle);

        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.log_in || item.getItemId() == R.id.nav_out) {
                    Intent intent = new Intent(Slideview.this, Main.class);
                    startActivity(intent);
                    return true; // 表示已处理点击事件
                } else if (
                        item.getItemId() == R.id.nav_Intro )
                         {
                    Intent intent = new Intent(Slideview.this, Intro_App.class);
                    startActivity(intent);
                    return true; // 表示已处理点击事件
                }else if(item.getItemId()==R.id.nav_collection){
                        Intent intent = new Intent(Slideview.this, personalSupport.class);
                        intent.putExtra("username", username);
                        startActivity(intent);
                    return true;
                }
                // 如果未处理点击事件，返回 false
                return false;
            }
        });

        // Load the initial fragment
        if (savedInstanceState == null) {
            selectedFragment(0); // Load home fragment by default
        }
    }

    private void selectedFragment(int position) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        hideFragment(fragmentTransaction);

        if (position == 0) {
            if (homeFragment == null) {
                homeFragment = new HomeFragment();
                Bundle bundle = new Bundle();
                bundle.putString("username", username);
                homeFragment.setArguments(bundle);
                fragmentTransaction.add(R.id.fragment_container, homeFragment);
            } else {
                fragmentTransaction.show(homeFragment);
            }
        } else if (position == 1) {
            if (searchFragment == null) {
                searchFragment = new SearchFragment();
                fragmentTransaction.add(R.id.fragment_container, searchFragment);
            } else {
                fragmentTransaction.show(searchFragment);
            }
        }
        fragmentTransaction.commit();
    }

    private void hideFragment(FragmentTransaction fragmentTransaction) {
        if (homeFragment != null) {
            fragmentTransaction.hide(homeFragment);
        }
        if (searchFragment != null) {
            fragmentTransaction.hide(searchFragment);
        }
    }
}
