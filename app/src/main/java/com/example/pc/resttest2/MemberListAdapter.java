package com.example.pc.resttest2;

import android.content.Context;
import android.content.Intent;
import android.preference.Preference;
import android.provider.SyncStateContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by samsung on 2017-07-25.
 */

public class MemberListAdapter extends BaseAdapter {

    private Context mContext;
    private List<MemberBean.MemberBeanSub> mMemberList=new ArrayList<MemberBean.MemberBeanSub>();

    public MemberListAdapter(Context context){
        mContext=context;
        updateMemberListTask();
    }

    @Override
    public int getCount() {
        return mMemberList.size();
    }

    @Override
    public Object getItem(int i) {
        return mMemberList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void updateMemberListTask(){
        new MemberListTask().execute();
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        // 인플레이트 하기
        LayoutInflater li=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //인플레이터 한 레이아웃에서 찾기, 컴포너트들을
        view=li.inflate(R.layout.view_member_list,null);

        ImageView imgFrofile = (ImageView)view.findViewById(R.id.imgProfile);
        TextView txtName=(TextView)view.findViewById(R.id.txtName);
        TextView txtId=(TextView)view.findViewById(R.id.txtId);


        //데이터를 가져온다
        final MemberBean.MemberBeanSub bean=mMemberList.get(i);

        //찾은 컴퍼넌트에게 데이터 대입
        new ImageLoaderTask(imgFrofile).execute(Constants.BASE_URL+bean.getProfileImg());
        txtName.setText(bean.getName());
        txtId.setText(bean.getUserId());

        //리스트 클릭 이벤트 등록
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(mContext,MemUpdateActivity.class);
                //이너클래스에서 바깥의 변수를 접근하기 위해서는
                // 그냥접근해서는 안됨 변수가 변하지 않게 final붙여주거나 멤버변수로
                intent.putExtra("memberBean",bean);
                mContext.startActivity(intent);

            }
        });

        return view;
    }

    //회원정보를 가져오는 task
    class MemberListTask extends android.os.AsyncTask<String,Void,String>{
        private String URL_MEMBER_LIST= Constants.BASE_URL+"/rest/selectMemberList.do";

        @Override
        protected String doInBackground(String... strings) {
            try{
                RestTemplate restTemplate=new RestTemplate();
                // restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                restTemplate.getMessageConverters().add(new FormHttpMessageConverter());

                MultiValueMap<String, Object> map=new LinkedMultiValueMap<String,Object>();

                HttpHeaders headers= new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

                HttpEntity<MultiValueMap<String,Object>> request =new HttpEntity<>(map,headers);

                return  restTemplate.postForObject(URL_MEMBER_LIST,request,String.class);

            }
            catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            Gson gson =new Gson();
            try{
                MemberBean bean=gson.fromJson(s,MemberBean.class);
                if(bean!=null){
                    if(bean.getResult().equals("ok")){
                        mMemberList=bean.getMemberList();
                        //리스트뷰 갱신
                        MemberListAdapter.this.notifyDataSetInvalidated();
                    }
                }
            }catch(Exception e){
                Toast.makeText(mContext, "파싱 실패", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    };
}
