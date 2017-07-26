package com.example.pc.resttest2;

import java.io.Serializable;

/**
 * Created by samsung on 2017-07-25.
 */

// 모든 class를 직렬화 왜 직렬화 시켜야 할까? 순서대로?
public class CommonBean implements Serializable {
    private String result;
    private  String resultMsg;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }
}
