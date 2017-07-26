package com.example.pc.resttest2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.net.URL;

public class MemUpdateActivity extends AppCompatActivity {

    private EditText mEdtName,mEdtId,mEdtPw,mEdtHp;
    private ImageView mImgProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mem_update);
        //데이터 받음
        MemberBean.MemberBeanSub memberBean=(MemberBean.MemberBeanSub)
                getIntent().getSerializableExtra("memberBean");

        mEdtName=(EditText)findViewById(R.id.edtName2);
        mEdtId=(EditText)findViewById(R.id.edtId2);
        mEdtPw=(EditText)findViewById(R.id.edtPw2);
        mEdtHp=(EditText)findViewById(R.id.edtHp2);
        mImgProfile=(ImageView)findViewById(R.id.imageView);

        // 정보수정 버튼 이벤트 등록
        findViewById(R.id.btnJoin1).setOnClickListener(btnJoinOnclick);

        mEdtName.setText(memberBean.getName());
        mEdtId.setText(memberBean.getUserId());
        mEdtPw.setText(memberBean.getUserPw());
        mEdtHp.setText(memberBean.getHp());
        new ImageLoaderTask(mImgProfile).execute(Constants.BASE_URL + memberBean.getProfileImg());

    }// end Oncreate

    // 정보수정
    private View.OnClickListener btnJoinOnclick=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
        new UdpateMemberTask().execute();
        }
    };

    class  UdpateMemberTask extends android.os.AsyncTask<String, Void, String>{

        private String URL_MEMBER_UPDATE= Constants.BASE_URL+"/rest/updateMember.do";
        String edtJoinId,edtJoinName,edtJoinPw,edtJoinHp;

        @Override
        protected void onPreExecute() {
            edtJoinName=mEdtName.getText().toString();
            edtJoinId=mEdtId.getText().toString();
            edtJoinPw=mEdtPw.getText().toString();
            edtJoinHp=mEdtHp.getText().toString();
        }

        @Override
        protected String doInBackground(String... strings) {
            try{
                RestTemplate restTemplate=new RestTemplate();
                // restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                restTemplate.getMessageConverters().add(new FormHttpMessageConverter());

                MultiValueMap<String, Object> map=new LinkedMultiValueMap<String,Object>();
                map.add("userId", edtJoinId); // 멤버빈의 변수값 , edt에서 받아온 데이터
                map.add("userPw", edtJoinPw);
                map.add("name", edtJoinName);
                map.add("hp", edtJoinHp);

                HttpHeaders headers= new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

                HttpEntity<MultiValueMap<String,Object>> request =new HttpEntity<>(map,headers);

                return  restTemplate.postForObject(URL_MEMBER_UPDATE,request,String.class);

            }
            catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            // gson으로 멤버빈을 파싱
            Gson gson =new Gson();
            try{
                MemberBean bean=gson.fromJson(s,MemberBean.class);
                if(bean!=null){
                    if(bean.getResult().equals("ok")){
                        // 현재 화면이 사라짐
                        finish();
                    }else{
                        Toast.makeText(MemUpdateActivity.this, bean.getResultMsg(), Toast.LENGTH_SHORT).show();
                    }
                }
            }catch(Exception e){
                Toast.makeText(MemUpdateActivity.this, "파싱 실패", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
    }

    //이미지 비동기 로딩 Task
    class ImageLoaderTask extends android.os.AsyncTask<String, Void, Bitmap> {

        private ImageView dispImageView;

        public ImageLoaderTask(ImageView dispImgView) {
            this.dispImageView =dispImgView;
        }

        @Override
        protected Bitmap doInBackground(String... params) {

            String imgUrl = params[0];

            Bitmap bmp = null;

            try {
                bmp = BitmapFactory.decodeStream((InputStream)new URL(imgUrl).getContent()  );
            } catch (Exception e) {
                e.printStackTrace();
            }

            return bmp;
        }//end doInBackground()

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(bitmap != null){
                //표시
                dispImageView.setImageBitmap(bitmap);
            }
        }
    };
    }
}