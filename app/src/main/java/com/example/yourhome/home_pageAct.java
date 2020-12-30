package com.example.yourhome;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;

public class home_pageAct extends AppCompatActivity {
    Button logout;
    FirebaseAuth mAuth;
      ImageButton add,btn_choisir ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);
        btn_choisir= findViewById(R.id.choisir);

        add = findViewById(R.id.img_add);
        mAuth=FirebaseAuth.getInstance();
        logout=(Button)findViewById(R.id.log_out);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent i=new Intent(home_pageAct.this,MainActivity.class);
                startActivity(i);
                finish();
            }
        });
add.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        addd();

    }
    public void addd(){
        Intent i=new Intent(home_pageAct.this,add_homeAct.class);
        startActivity(i);
        finish();
    }
});
        btn_choisir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choisir();

            }
            public void choisir(){
                Intent i=new Intent(home_pageAct.this,catalogueAct.class);
                startActivity(i);
                finish();
            }
        });
    }
    }

