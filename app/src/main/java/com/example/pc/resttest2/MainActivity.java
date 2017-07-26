package com.example.pc.resttest2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class MainActivity extends AppCompatActivity {

    private EditText mEdtUserId, mEdtUserPw;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEdtUserId=(EditText)findViewById(R.id.edtUserId);
        mEdtUserPw=(EditText)findViewById(R.id.edtUserPw);
        mProgressBar=(ProgressBar)findViewById(R.id.progressBar);

        findViewById(R.id.btnLogin).setOnClickListener(btnLoginClick);
        findViewById(R.id.btnGoJoin).setOnClickListener(btnGoJoinClick);
    }
    // 로그인버튼 클릭 처리
    private View.OnClickListener btnLoginClick=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            new LoginProcTask().execute();

        }
    };

    //회원가입버튼 클릭 처리
    private  View.OnClickListener btnGoJoinClick=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i= new Intent(MainActivity.this,JoinActivity.class);
            startActivity(i);
        }
    };


    // 로그인하는 과정
    // tread에 넘어가는 파라미터 onupdate onpost
    private class LoginProcTask extends android.os. AsyncTask<String,Void,String>{
        public static final String URL_LOGIN_PROC= Constants.BASE_URL+"/rest/loginProc.do";
        private String userId,userPw;

        @Override
        protected void onPreExecute() {
            //프로그레스 다이얼로그 표시
            mProgressBar.setVisibility(View.VISIBLE);

            userId =mEdtUserId.getText().toString();
            userPw=mEdtUserPw.getText().toString();
        }

        @Override
        protected String doInBackground(String... params) {
            try{
                RestTemplate restTemplate=new RestTemplate();
                // restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                restTemplate.getMessageConverters().add(new FormHttpMessageConverter());

                MultiValueMap<String, Object> map=new LinkedMultiValueMap<String,Object>();
                map.add("userId",userId);
                map.add("userPw",userPw);

                HttpHeaders headers= new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

                HttpEntity<MultiValueMap<String,Object>> request =new HttpEntity<>(map,headers);

                return  restTemplate.postForObject(URL_LOGIN_PROC,request,String.class);

            }
            catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }//end doInBack

        @Override
        protected void onPostExecute(String s) {
            // 완료후 사라짐
            mProgressBar.setVisibility(View.INVISIBLE);

            Gson gson =new Gson();
            try{
                MemberBean bean=gson.fromJson(s,MemberBean.class);
                if(bean!=null){
                    if(bean.getResult().equals("ok")){
                        //로그인 성공
                        Intent i =new Intent(MainActivity.this,MemberListActivity.class);
                        // putExtra는 직렬화 시켜야 class를 변수로 받을 수 있음
                        i.putExtra("memberBean",bean.getMemberBean());
                        startActivity(i);
                    }else{
                        Toast.makeText(MainActivity.this, bean.getResultMsg(), Toast.LENGTH_SHORT).show();
                    }
                }
            }catch(Exception e){

            }

        }
    };


}
