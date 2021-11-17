 package com.project.budgetku;

 import android.content.Intent;
 import android.os.Bundle;
 import android.view.WindowManager;
 import android.view.animation.Animation;
 import android.view.animation.AnimationUtils;
 import android.widget.ImageView;
 import android.widget.TextView;

 import androidx.annotation.NonNull;
 import androidx.appcompat.app.AppCompatActivity;

 import com.google.android.gms.tasks.OnCompleteListener;
 import com.google.android.gms.tasks.Task;
 import com.google.firebase.auth.AuthResult;
 import com.google.firebase.auth.FirebaseAuth;
 import com.google.firebase.auth.FirebaseUser;

 public class SplashScreenActivity extends AppCompatActivity {
    private static int SPLASH = 3000;
    Animation animation;
    private ImageView appLogo;
    private TextView appName;

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash_screen);

        animation = AnimationUtils.loadAnimation(this, R.anim.animation);
        appLogo = findViewById(R.id.appLogo);
        appName = findViewById(R.id.appName);

        appLogo.setAnimation(animation);
        appName.setAnimation(animation);

        if(mCurrentUser == null){

            mAuth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful()){
                        Intent intent = new Intent(SplashScreenActivity.this, com.project.budgetku.MainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                }
            });
        }else{
            Intent intent = new Intent(SplashScreenActivity.this, com.project.budgetku.MainActivity.class);
            startActivity(intent);
            finish();
        }


    }
}