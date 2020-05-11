package com.example.chrysallis.components;

import android.content.Context;
import android.os.StrictMode;
import android.widget.TextView;

import com.detectlanguage.DetectLanguage;
import com.detectlanguage.errors.APIError;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.concurrent.ExecutionException;

public class Traductor {
    public static String traducir(String textToTranslate, Context context){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        DetectLanguage.apiKey = "d34e9ae5714f2afea8e477b9023e1583";
        String lang = languageManager.getLocale(context);
        String result = Translate(textToTranslate, lang, context);
        return result;
    }

    public static String Translate(String textToBeTranslated,String lang, Context context){
        TranslatorBackgroundTask translatorBackgroundTask= new TranslatorBackgroundTask(context);
        String result = textToBeTranslated;
        String language;
        try {
            language = DetectLanguage.simpleDetect(textToBeTranslated);
            if(!language.equals(lang)){
                String translationResult = translatorBackgroundTask.execute(textToBeTranslated,lang).get();
                JsonObject jsonObject = (JsonObject) new JsonParser().parse(translationResult);
                result = jsonObject.get("text").getAsString();
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (APIError apiError) {
            apiError.printStackTrace();
        }
        return result;
    }


}
