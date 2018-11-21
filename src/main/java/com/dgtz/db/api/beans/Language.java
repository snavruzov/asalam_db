package com.dgtz.db.api.beans;

import com.dgtz.db.api.factory.GsonInsta;
import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Created by Sardor Navruzov on 9/17/15.
 * Copyrights Digitizen Co.
 */
public class Language implements Serializable {
    private static final long serialVersionUID = 1L;

    /*{
        "code": 200,
        "lang": "en-ru",
        "text": [
            "Быть или не быть?",
            "Вот в чем вопрос."
         ]
    }*/
    public Language() {
    }

    private Integer code;
    private String lang;
    private String[] text;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String[] getText() {
        return text;
    }

    public void setText(String[] text) {
        this.text = text;
    }

    @Override
    public String toString() {
        Gson gson = GsonInsta.getInstance();
        return gson.toJson(this);

    }
}
