package Program.Tech;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import org.w3c.dom.Text;

import java.text.BreakIterator;

public class Slideview extends AppCompatActivity {
    private  Toolbar toolBar;
    private DrawerLayout drawerLayout;

    private TextView textView;

    private NavigationView nav;
    private BottomNavigationView bottomNavigationView;

    private Toolbar add;
    private HomeFragment homeFragment;
    private SearchFragment searchFragment;
    private ActFragment actFragment;

    private void selectedFragment(int position){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        hideFragment(fragmentTransaction);
        if(position==0) {
            if (homeFragment == null) {
                homeFragment = new HomeFragment();
                fragmentTransaction.add(R.id.drawLayout, homeFragment);
            } else {
                fragmentTransaction.show(homeFragment);
            }
        }else if(position==1){
            if (searchFragment == null) {
                searchFragment = new SearchFragment();
                fragmentTransaction.add(R.id.drawLayout, searchFragment);
            } else {
                fragmentTransaction.show(searchFragment);
            }
        }else if(position==2){
            if(actFragment==null){
                actFragment = new ActFragment();
                fragmentTransaction.add(R.id.drawLayout,actFragment);
            }else{
                fragmentTransaction.show(actFragment);
            }
        }
        fragmentTransaction.commit();
    }
    private void hideFragment(FragmentTransaction fragmentTransaction){
        if(homeFragment!=null){
            fragmentTransaction.hide(homeFragment);
        }
        if(searchFragment!=null){
            fragmentTransaction.hide(searchFragment);
        }
        if(actFragment!=null){
            fragmentTransaction.hide(actFragment);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_slideview);

        toolBar = findViewById(R.id.toolBar);
        drawerLayout = findViewById(R.id.drawLayout);
        nav = findViewById(R.id.nav);
        bottomNavigationView = findViewById(R.id.bottomNavigation);

        //selectedFragment(0);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId()==R.id.home){
                    selectedFragment(0);
                }else if(item.getItemId()==R.id.search){
                    selectedFragment(1);
                }else if(item.getItemId()==R.id.donation){
                    selectedFragment(2);
                }
                return false;
            }
        });
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolBar,R.string.open,R.string.close);
        //initialization
        toggle.syncState();

        drawerLayout.addDrawerListener(toggle);


        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.log_in) {
                    Intent intent = new Intent(Slideview.this, Main.class);
                    startActivity(intent);
                    return true; // 表示已处理点击事件
                } else if (item.getItemId() == R.id.nav_about ||
                        item.getItemId() == R.id.nav_out ||
                        item.getItemId() == R.id.nav_collection ||
                        item.getItemId() == R.id.nav_Intro) {
                    finish();
                    return true; // 表示已处理点击事件
                }
                // 如果未处理点击事件，返回 false
                return false;
            }
        });


    }
}