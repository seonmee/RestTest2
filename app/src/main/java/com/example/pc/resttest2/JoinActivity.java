package com.example.pc.resttest2;

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

public class JoinActivity extends AppCompatActivity {

    EditText mName, mId, mPw, mHp;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        findViewById(R.id.btnJoin).setOnClickListener(btnJoinClick);

        mName= (EditText) findViewById(R.id.edtName);
        mId=(EditText)findViewById(R.id.edtID);
        mPw=(EditText)findViewById(R.id.edtPw);
        mHp=(EditText)findViewById(R.id.edtHp);

        mProgressBar=(ProgressBar)findViewById(R.id.progressBar2);
        mProgressBar.setVisibility(View.INVISIBLE);
    }
    // 회원가입 버튼 클릭시

    private View.OnClickListener btnJoinClick=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new JoinProcTask().execute();
        }
    };

    // 회원가입이 처리되는 과정
    private class JoinProcTask extends android.os.AsyncTask<String,Void,String>{
        //서버의 정보 불러오기
        public static final String URL_JOIN_PROC="http://172.16.15.204:8080/rest/joinProc.do";
        private String userId,userPw,name,hp;

        @Override
        protected void onPreExecute() {

            //프로그레스 다이얼로그 표시
            mProgressBar.setVisibility(View.VISIBLE);

            userId = mId.getText().toString();
            userPw=mPw.getText().toString();
            name=mName.getText().toString();
            hp=mHp.getText().toString();
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new FormHttpMessageConverter());

                MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
                map.add("userId", userId);
                map.add("userPw", userPw);
                map.add("name", name);
                map.add("hp", hp);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

                HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(map, headers);

                return restTemplate.postForObject(URL_JOIN_PROC, request, String.class);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            Gson gson =new Gson();
            mProgressBar.setProgress(View.INVISIBLE);
            try{
                MemberBean bean=gson.fromJson(s,MemberBean.class);
                if(bean!=null){
                    if(bean.getResult().equals("ok")){
                        Toast.makeText(JoinActivity.this, "회원가입돼땅", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(JoinActivity.this, bean.getResultMsg(), Toast.LENGTH_SHORT).show();
                    }
                }
            }catch(Exception e){

            }
        }
    };
}