package com.aigestudio.wheelpicker.demo;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.telecom.Call;
import android.util.Log;

import com.aigestudio.wheelpicker.model.City;
import com.aigestudio.wheelpicker.model.Province;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * CREATE BY ALUN
 * EMAIL: alunfeixue2011@gmail.com
 * DATE : 2017/08/28 11:21
 */

public class DataManager {

    public interface Callback{
        void onCall(boolean result);
    }

    public static List<Province> loadDataNoArea(Context context, Callback callback){
        AssetManager am = context.getResources().getAssets();
        try {
            InputStream is = am.open("source.text");
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader bufferedReader = new BufferedReader(isr);

            List<Province> provinces = new ArrayList<>();
            String str, id, value;
            Province province = null;
            City city = null;

            while ((str = bufferedReader.readLine()) != null){
                str = removeAllBlank(str);
                id = str.substring(0, 6);
                value = str.substring(6, str.length());

                if (id.substring(2, 6).equals("0000")){
                    province = new Province();
                    province.name = value;
                    province.city = new ArrayList<>();
                    provinces.add(province);
                    continue;
                }

                if (id.substring(4, 6).equals("00")){
                    city = new City();
                    city.name = value;
//                    city.area = new ArrayList<>();
                    province.city.add(city);
                    continue;
                }

//                city.area.add(value);
            }

            for (Province p : provinces){

                if (p.name.contains("自治州")){
                    p.name = p.name.replace("自治州", "州");
                } else if (p.name.contains("自治县")){
                    p.name = p.name.replace("自治县", "县");
                }else if (p.name.startsWith("香港")){
                    p.name = "香港";
                } else if (p.name.startsWith("澳门")){
                    p.name = "澳门";
                } else if (p.name.equals("北京市")){
                    p.city.clear();
                    City city1 = new City();
                    city1.name = "北京市";
                    p.city.add(city1);
                } else if (p.name.equals("重庆市")){
                    p.city.clear();
                    City city1 = new City();
                    city1.name = "重庆市";
                    p.city.add(city1);
                } else if (p.name.equals("上海市")){
                    p.city.clear();
                    City city1 = new City();
                    city1.name = "上海市";
                    p.city.add(city1);
                } else if (p.name.equals("天津市")){
                    p.city.clear();
                    City city1 = new City();
                    city1.name = "天津市";
                    p.city.add(city1);
                }

                if (p.city.size() == 0){
                    City city1 = new City();
                    city1.name = p.name;
//                    city1.area = new ArrayList<>();
//                    city1.area.add(p.name);
                    p.city.add(city1);
                    continue;
                }

                for (City c : p.city){
//                    if (c.area.size() == 0){
//                        c.area.add(c.name);
//                    }
                    if (c.name.length() > 4){
                        if (c.name.endsWith("市")){
                            c.name = c.name.replace("市", "");
                        }
                    }
                }
            }

            callback.onCall(true);

            return provinces;
        } catch (IOException e) {
            e.printStackTrace();
            callback.onCall(false);
        }
        return null;
    }

    public static List<Province> loadData(Context context, Callback callback){
        AssetManager am = context.getResources().getAssets();
        try {
            InputStream is = am.open("source.text");
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader bufferedReader = new BufferedReader(isr);

            List<Province> provinces = new ArrayList<>();
            String str, id, value;
            Province province = null;
            City city = null;

            while ((str = bufferedReader.readLine()) != null){
                str = removeAllBlank(str);
                id = str.substring(0, 6);
                value = str.substring(6, str.length());

                if (id.substring(2, 6).equals("0000")){
                    province = new Province();
                    province.name = value;
                    province.city = new ArrayList<>();
                    provinces.add(province);
                    continue;
                }

                if (id.substring(4, 6).equals("00")){
                    city = new City();
                    city.name = value;
                    city.area = new ArrayList<>();
                    province.city.add(city);
                    continue;
                }

                city.area.add(value);
            }

            for (Province p : provinces){

                if (p.name.contains("自治州")){
                    p.name = p.name.replace("自治州", "州");
                } else if (p.name.contains("自治县")){
                    p.name = p.name.replace("自治县", "县");
                }else if (p.name.startsWith("香港")){
                    p.name = "香港";
                } else if (p.name.startsWith("澳门")){
                    p.name = "澳门";
                }

                if (p.city.size() == 0){
                    City city1 = new City();
                    city1.name = p.name;
                    city1.area = new ArrayList<>();
                    city1.area.add(p.name);
                    p.city.add(city1);
                    continue;
                }

                for (City c : p.city){
                    if (c.area.size() == 0){
                        c.area.add(c.name);
                    }
                }
            }

            callback.onCall(true);

            return provinces;
        } catch (IOException e) {
            e.printStackTrace();
            callback.onCall(false);
        }
        return null;
    }

    /**
     * 去除字符串中所包含的空格（包括:空格(全角，半角)、制表符、换页符等）
     * @param s
     * @return
     */
    public static String removeAllBlank(String s){
        String result = "";
        if(null!=s && !"".equals(s)){
            result = s.replaceAll("[　*| *| *|//s*]*", "");
        }
        return result;
    }

    /**
     * 去除字符串中头部和尾部所包含的空格（包括:空格(全角，半角)、制表符、换页符等）
     * @param s
     * @return
     */
    public static String trim(String s){
        String result = "";
        if(null!=s && !"".equals(s)){
            result = s.replaceAll("^[　*| *| *|//s*]*", "").replaceAll("[　*| *| *|//s*]*$", "");
        }
        return result;
    }



    //写到/storage/emulated/0/data.dat
    public static void writeObjectToFile(List<Province> provinces)
    {
        File file =new File(Environment.getExternalStorageDirectory().getPath()+"/data.dat");
        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            ObjectOutputStream objOut=new ObjectOutputStream(out);
            objOut.writeObject(provinces);
            objOut.flush();
            objOut.close();
            System.out.println("write object success!");
        } catch (IOException e) {
            System.out.println("write object failed");
            e.printStackTrace();
        }
    }
}
