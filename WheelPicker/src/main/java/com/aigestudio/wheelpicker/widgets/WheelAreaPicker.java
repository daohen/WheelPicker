package com.aigestudio.wheelpicker.widgets;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.aigestudio.wheelpicker.IWheelPicker;
import com.aigestudio.wheelpicker.WheelPicker;
import com.aigestudio.wheelpicker.model.City;
import com.aigestudio.wheelpicker.model.Province;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * WheelAreaPicker
 * Created by Administrator on 2016/9/14 0014.
 */
public class WheelAreaPicker extends LinearLayout implements IWheelAreaPicker, IWheelPicker {
    private static final float ITEM_TEXT_SIZE = 18;
    private static final String SELECTED_ITEM_COLOR = "#353535";
    private static final int PROVINCE_INITIAL_INDEX = 0;

    private Context mContext;

    private List<Province> mProvinceList;
    private List<City> mCityList;
    private List<String> mProvinceName, mCityName;

    private AssetManager mAssetManager;

    private LayoutParams mLayoutParams;

    private WheelPicker mWPProvince, mWPCity/*, mWPArea*/;

    public WheelAreaPicker(Context context, AttributeSet attrs) {
        super(context, attrs);

        initLayoutParams();

        initView(context);

        mProvinceList = getJsonDataFromAssets(mAssetManager);

        obtainProvinceData();

        addListenerToWheelPicker();
    }

    @SuppressWarnings("unchecked")
    private List<Province> getJsonDataFromAssets(AssetManager assetManager) {
        List<Province> provinceList = null;
        try {
            InputStream inputStream = assetManager.open("data.dat");
//            InputStream inputStream = assetManager.open("RegionJsonData.dat");
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            provinceList = (List<Province>) objectInputStream.readObject();
            objectInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return provinceList;
    }

    private void initLayoutParams() {
        mLayoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mLayoutParams.setMargins(10, 5, 10, 5);
        mLayoutParams.width = 0;
    }

    private void initView(Context context) {
        setOrientation(HORIZONTAL);

        mContext = context;

        mAssetManager = mContext.getAssets();

        mProvinceName = new ArrayList<>();
        mCityName = new ArrayList<>();

        mWPProvince = new WheelPicker(context);
        mWPCity = new WheelPicker(context);
//        mWPArea = new WheelPicker(context);

        mWPProvince.setMaximumWidthText("##########");
        mWPCity.setMaximumWidthText("##########");
//        mWPArea.setMaximumWidthText("##############");

        mWPProvince.setItemAlign(WheelPicker.ALIGN_CENTER);
        mWPCity.setItemAlign(WheelPicker.ALIGN_CENTER);
//        mWPArea.setItemAlign(WheelPicker.ALIGN_CENTER);

        initWheelPicker(mWPProvince);
        initWheelPicker(mWPCity);
//        initWheelPicker(mWPArea);
    }

    private void initWheelPicker(WheelPicker wheelPicker) {
//        mLayoutParams.weight = weight;
        wheelPicker.setItemTextSize(dip2px(mContext, ITEM_TEXT_SIZE));
        wheelPicker.setSelectedItemTextColor(Color.parseColor(SELECTED_ITEM_COLOR));
        wheelPicker.setCurved(true);
//        wheelPicker.setItemAlign(WheelPicker.ALIGN_LEFT);
//        wheelPicker.setLayoutParams(mLayoutParams);
        addView(wheelPicker);
    }

    private void obtainProvinceData() {
        for (Province province : mProvinceList) {
            mProvinceName.add(province.getName());
        }
        mWPProvince.setData(mProvinceName);
        setCityAndAreaData(PROVINCE_INITIAL_INDEX);
    }

    private void addListenerToWheelPicker() {
        //监听省份的滑轮,根据省份的滑轮滑动的数据来设置市跟地区的滑轮数据
        mWPProvince.setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
            @Override
            public void onItemSelected(WheelPicker picker, Object data, int position) {
                //获得该省所有城市的集合
                setCityAndAreaData(position);
            }
        });

//        mWPCity.setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(WheelPicker picker, Object data, int position) {
//                //获取城市对应的城区的名字
//                mWPArea.setData(mCityList.get(position).getArea());
//                mWPArea.setSelectedItemPosition(0, false);
//            }
//        });
    }

    private void setCityAndAreaData(int position) {
        //获得该省所有城市的集合
        mCityList = mProvinceList.get(position).getCity();
        //获取所有city的名字
        //重置先前的城市集合数据
        mCityName.clear();
        for (City city : mCityList)
            mCityName.add(city.getName());
        mWPCity.setData(mCityName);
        mWPCity.setSelectedItemPosition(0, false);
        //获取第一个城市对应的城区的名字
        //重置先前的城区集合的数据
//        mWPArea.setData(mCityList.get(0).getArea());
//        mWPArea.setSelectedItemPosition(0, false);
    }

    @Override
    public String getProvince() {
        return mProvinceList.get(mWPProvince.getCurrentItemPosition()).getName();
    }

    @Override
    public String getCity() {
        return mCityList.get(mWPCity.getCurrentItemPosition()).getName();
    }

    @Override
    public String getArea() {
//        return mCityList.get(mWPCity.getCurrentItemPosition()).getArea().get(mWPArea.getCurrentItemPosition());
        return null;
    }

    @Override
    public void hideArea() {
        this.removeViewAt(2);
    }

    private int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    public int getVisibleItemCount() {
        if (mWPProvince.getVisibleItemCount() == mWPCity.getVisibleItemCount() /*&&
                mWPCity.getVisibleItemCount() == mWPArea.getVisibleItemCount()*/)
            return mWPProvince.getVisibleItemCount();
        throw new ArithmeticException("Can not get visible item count correctly from" +
                "WheelDatePicker!");
    }

    @Override
    public void setVisibleItemCount(int count) {
        mWPProvince.setVisibleItemCount(count);
        mWPCity.setVisibleItemCount(count);
//        mWPArea.setVisibleItemCount(count);
    }

    @Override
    public boolean isCyclic() {
        return mWPProvince.isCyclic() && mWPCity.isCyclic() /*&& mWPArea.isCyclic()*/;
    }

    @Override
    public void setCyclic(boolean isCyclic) {
        mWPProvince.setCyclic(isCyclic);
        mWPCity.setCyclic(isCyclic);
//        mWPArea.setCyclic(isCyclic);
    }

    @Deprecated
    @Override
    public void setOnItemSelectedListener(WheelPicker.OnItemSelectedListener listener) {
        throw new UnsupportedOperationException("You can not set OnItemSelectedListener for" +
                "WheelDatePicker");
    }

    @Deprecated
    @Override
    public int getSelectedItemPosition() {
        throw new UnsupportedOperationException("You can not get position of selected item from" +
                "WheelDatePicker");
    }

    @Deprecated
    @Override
    public void setSelectedItemPosition(int position) {
        throw new UnsupportedOperationException("You can not set position of selected item for" +
                "WheelDatePicker");
    }

    @Deprecated
    @Override
    public int getCurrentItemPosition() {
        throw new UnsupportedOperationException("You can not get position of current item from" +
                "WheelDatePicker");
    }

    @Deprecated
    @Override
    public List getData() {
        throw new UnsupportedOperationException("You can not get data source from WheelDatePicker");
    }

    @Deprecated
    @Override
    public void setData(List data) {
        throw new UnsupportedOperationException("You don't need to set data source for" +
                "WheelDatePicker");
    }

    @Deprecated
    @Override
    public void setSameWidth(boolean hasSameSize) {
        throw new UnsupportedOperationException("You don't need to set same width for" +
                "WheelDatePicker");
    }

    @Deprecated
    @Override
    public boolean hasSameWidth() {
        throw new UnsupportedOperationException("You don't need to set same width for" +
                "WheelDatePicker");
    }

    @Deprecated
    @Override
    public void setOnWheelChangeListener(WheelPicker.OnWheelChangeListener listener) {
        throw new UnsupportedOperationException("WheelDatePicker unsupport set" +
                "OnWheelChangeListener");
    }

    @Deprecated
    @Override
    public String getMaximumWidthText() {
        throw new UnsupportedOperationException("You can not get maximum width text from" +
                "WheelDatePicker");
    }

    @Deprecated
    @Override
    public void setMaximumWidthText(String text) {
        throw new UnsupportedOperationException("You don't need to set maximum width text for" +
                "WheelDatePicker");
    }

    @Deprecated
    @Override
    public int getMaximumWidthTextPosition() {
        throw new UnsupportedOperationException("You can not get maximum width text position" +
                "from WheelDatePicker");
    }

    @Deprecated
    @Override
    public void setMaximumWidthTextPosition(int position) {
        throw new UnsupportedOperationException("You don't need to set maximum width text" +
                "position for WheelDatePicker");
    }

    @Override
    public int getSelectedItemTextColor() {
        if (mWPProvince.getSelectedItemTextColor() == mWPCity.getSelectedItemTextColor()/* &&
                mWPCity.getSelectedItemTextColor() == mWPArea.getSelectedItemTextColor()*/)
            return mWPProvince.getSelectedItemTextColor();
        throw new RuntimeException("Can not get color of selected item text correctly from" +
                "WheelDatePicker!");
    }

    @Override
    public void setSelectedItemTextColor(int color) {
        mWPProvince.setSelectedItemTextColor(color);
        mWPCity.setSelectedItemTextColor(color);
//        mWPArea.setSelectedItemTextColor(color);
    }

    @Override
    public int getItemTextColor() {
        if (mWPProvince.getItemTextColor() == mWPCity.getItemTextColor()/* &&
                mWPCity.getItemTextColor() == mWPArea.getItemTextColor()*/)
            return mWPProvince.getItemTextColor();
        throw new RuntimeException("Can not get color of item text correctly from" +
                "WheelDatePicker!");
    }

    @Override
    public void setItemTextColor(int color) {
        mWPProvince.setItemTextColor(color);
        mWPCity.setItemTextColor(color);
//        mWPArea.setItemTextColor(color);
    }

    @Override
    public int getItemTextSize() {
        if (mWPProvince.getItemTextSize() == mWPCity.getItemTextSize()/* &&
                mWPCity.getItemTextSize() == mWPArea.getItemTextSize()*/)
            return mWPProvince.getItemTextSize();
        throw new RuntimeException("Can not get size of item text correctly from" +
                "WheelDatePicker!");
    }

    @Override
    public void setItemTextSize(int size) {
        mWPProvince.setItemTextSize(size);
        mWPCity.setItemTextSize(size);
//        mWPArea.setItemTextSize(size);
    }

    @Override
    public int getItemSpace() {
        if (mWPProvince.getItemSpace() == mWPCity.getItemSpace() /*&&
                mWPCity.getItemSpace() == mWPArea.getItemSpace()*/)
            return mWPProvince.getItemSpace();
        throw new RuntimeException("Can not get item space correctly from WheelDatePicker!");
    }

    @Override
    public void setItemSpace(int space) {
        mWPProvince.setItemSpace(space);
        mWPCity.setItemSpace(space);
//        mWPArea.setItemSpace(space);
    }

    @Override
    public void setIndicator(boolean hasIndicator) {
        mWPProvince.setIndicator(hasIndicator);
        mWPCity.setIndicator(hasIndicator);
//        mWPArea.setIndicator(hasIndicator);
    }

    @Override
    public boolean hasIndicator() {
        return mWPProvince.hasIndicator() && mWPCity.hasIndicator() /*&&
                mWPArea.hasIndicator()*/;
    }

    @Override
    public int getIndicatorSize() {
        if (mWPProvince.getIndicatorSize() == mWPCity.getIndicatorSize()/* &&
                mWPCity.getIndicatorSize() == mWPArea.getIndicatorSize()*/)
            return mWPProvince.getIndicatorSize();
        throw new RuntimeException("Can not get indicator size correctly from WheelDatePicker!");
    }

    @Override
    public void setIndicatorSize(int size) {
        mWPProvince.setIndicatorSize(size);
        mWPCity.setIndicatorSize(size);
//        mWPArea.setIndicatorSize(size);
    }

    @Override
    public int getIndicatorColor() {
        if (mWPProvince.getCurtainColor() == mWPCity.getCurtainColor()/* &&
                mWPCity.getCurtainColor() == mWPArea.getCurtainColor()*/)
            return mWPProvince.getCurtainColor();
        throw new RuntimeException("Can not get indicator color correctly from WheelDatePicker!");
    }

    @Override
    public void setIndicatorColor(int color) {
        mWPProvince.setIndicatorColor(color);
        mWPCity.setIndicatorColor(color);
//        mWPArea.setIndicatorColor(color);
    }

    @Override
    public void setCurtain(boolean hasCurtain) {
        mWPProvince.setCurtain(hasCurtain);
        mWPCity.setCurtain(hasCurtain);
//        mWPArea.setCurtain(hasCurtain);
    }

    @Override
    public boolean hasCurtain() {
        return mWPProvince.hasCurtain() && mWPCity.hasCurtain()/* &&
                mWPArea.hasCurtain()*/;
    }

    @Override
    public int getCurtainColor() {
        if (mWPProvince.getCurtainColor() == mWPCity.getCurtainColor()/* &&
                mWPCity.getCurtainColor() == mWPArea.getCurtainColor()*/)
            return mWPProvince.getCurtainColor();
        throw new RuntimeException("Can not get curtain color correctly from WheelDatePicker!");
    }

    @Override
    public void setCurtainColor(int color) {
        mWPProvince.setCurtainColor(color);
        mWPCity.setCurtainColor(color);
//        mWPArea.setCurtainColor(color);
    }

    @Override
    public void setAtmospheric(boolean hasAtmospheric) {
        mWPProvince.setAtmospheric(hasAtmospheric);
        mWPCity.setAtmospheric(hasAtmospheric);
//        mWPArea.setAtmospheric(hasAtmospheric);
    }

    @Override
    public boolean hasAtmospheric() {
        return mWPProvince.hasAtmospheric() && mWPCity.hasAtmospheric() /*&& mWPArea.hasAtmospheric()*/;
    }

    @Override
    public boolean isCurved() {
        return mWPProvince.isCurved() && mWPCity.isCurved()/* && mWPArea.isCurved()*/;
    }

    @Override
    public void setCurved(boolean isCurved) {
        mWPProvince.setCurved(isCurved);
        mWPCity.setCurved(isCurved);
//        mWPArea.setCurved(isCurved);
    }

    @Override
    public int getItemAlign() {
        throw new UnsupportedOperationException("You can not get item align from WheelDatePicker");
    }

    @Override
    public void setItemAlign(int align) {
        throw new UnsupportedOperationException("You don't need to set item align for" +
                "WheelDatePicker");
    }

    @Override
    public Typeface getTypeface() {
        if (mWPProvince.getTypeface().equals(mWPCity.getTypeface())/* &&
                mWPCity.getTypeface().equals(mWPArea.getTypeface())*/)
            return mWPProvince.getTypeface();
        throw new RuntimeException("Can not get typeface correctly from WheelDatePicker!");
    }

    @Override
    public void setTypeface(Typeface tf) {
        mWPProvince.setTypeface(tf);
        mWPCity.setTypeface(tf);
//        mWPArea.setTypeface(tf);
    }

}
