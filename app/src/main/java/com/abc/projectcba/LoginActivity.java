package com.abc.projectcba;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.abc.projectcba.ui.UserInfoSP;
import com.abc.projectcba.ui.webapisvr;


import org.json.JSONObject;



public class LoginActivity extends AppCompatActivity {

    EditText txtuserid;
    EditText txtPassword;
    Button btnsignin;
    TextView tverrormsg;

    private UserInfoSP userinfo;
    private webapisvr p;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        userinfo = UserInfoSP.getInstance(this);

        tverrormsg = findViewById(R.id.errormsg);
        txtuserid = findViewById(R.id.txtuserid);
        txtPassword = findViewById(R.id.txtPassword);

        //retrieve login data if any
        txtuserid.setText(userinfo.getUserID());
        txtPassword.setText(userinfo.getUserPwd());

        btnsignin = findViewById(R.id.btnsignin);

        btnsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userid = txtuserid.getText().toString();
                String userpwd = txtPassword.getText().toString();

                tverrormsg.setText("Sedang Login");
                p.AppLogin(userid,userpwd);


            }
        });


        p = new webapisvr();
        p.setListener(new webapisvr.IAsyncFetchListener() {
            @Override
            public void onInit() {

            }

            @Override
            public void onComplete(String item) {
                String errmsg = "";
                tverrormsg.setText("receiving");
                if(item.length()==0){
                    errmsg=p.errormsg;
                }
                else{
                    JSONObject json = null;
                    String return_message ="";

                    try{
                        json = new JSONObject(item);
                        return_message = json.getString("return_message");
                    }catch (Exception e){
                        errmsg = e.getMessage();
                    }
                    if(return_message.indexOf("SUCCESS")>=0){




                        tverrormsg.setText("signing...");
                        String UserID= txtuserid.getText().toString();
                        String UserPwd= txtPassword.getText().toString();
                        userinfo.setLoginSuccess(UserID, UserPwd);
                        Toast.makeText(getApplicationContext(), "Success Login2", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(getApplicationContext(), abcd.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);



                        //close the login
                        finish();
                    }
                    else{
                        errmsg = return_message;
                        Toast.makeText(getApplicationContext(), errmsg, Toast.LENGTH_SHORT).show();
                    }

                }

                if(errmsg.length() > 0){
                    tverrormsg.setText("error: "+ errmsg);
                }


            }

            @Override
            public void onError(Throwable error) {

            }

            @Override
            public void onCancelled() {

            }
        });
    }

    @Override
    public void onBackPressed (){
        try {

        }catch (Exception e){

        }
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setMessage("Confirm exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id){
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }
}