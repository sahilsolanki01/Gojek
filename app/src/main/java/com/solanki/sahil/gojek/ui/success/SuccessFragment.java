package com.solanki.sahil.gojek.ui.success;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.solanki.sahil.gojek.R;
import com.solanki.sahil.gojek.data.adapter.MyRecyclerViewAdapter;
import com.solanki.sahil.gojek.databinding.SuccessFragmentBinding;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class SuccessFragment extends Fragment {

    private SuccessViewModel mViewModel;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<SuccessViewModel> arrayList;

    public static SuccessFragment newInstance() {
        return new SuccessFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mViewModel = ViewModelProviders.of(this).get(SuccessViewModel.class);
        SuccessFragmentBinding bindingUtil = DataBindingUtil.inflate(inflater, R.layout.success_fragment, container, false);
        bindingUtil.setModel(mViewModel);
       // bindingUtil.getModel().mainListener = this;
        bindingUtil.setLifecycleOwner(this);
        mRecyclerView = bindingUtil.recyclerView;

        return bindingUtil.getRoot();
        
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        arrayList = new ArrayList<>();
        mAdapter = new MyRecyclerViewAdapter(arrayList);

        setData();

        //APIXU FUTURE DAYS API COMES WITH THE PAID VERSION, HENCE SHOWING STATIC TEMPERATURES FROM XML FILE TEMP.XML
        parseXML();
        mRecyclerView.setAdapter(mAdapter);


    }

    private void setData() {

        String response = getArguments().getString("response");
        JSONObject jsonObject1 = null;
        try {
            jsonObject1 = new JSONObject(response);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject jsonObject2 = jsonObject1.optJSONObject("location");
        JSONObject jsonObject3 = jsonObject1.optJSONObject("current");

        String location = " ", temp = " ";
        if(jsonObject2.length() != 0){
            location = jsonObject2.optString("name");
        }

        if(jsonObject3.length() != 0){
            temp = jsonObject3.optString("temperature");
        }


        mViewModel.city = location;
        mViewModel.temp_today = temp +(char) 0x00B0;

    }


    private void parseXML() {
        XmlPullParserFactory parserFactory;
        try {
            parserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = parserFactory.newPullParser();
            InputStream is = getActivity().getAssets().open("temp.xml");
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(is, null);

            processParsing(parser);

        } catch (XmlPullParserException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }

        mAdapter.notifyDataSetChanged();
    }


    private void processParsing(XmlPullParser parser) throws IOException, XmlPullParserException{
        int eventType = parser.getEventType();
        SuccessViewModel viewModel = null;

        while (eventType != XmlPullParser.END_DOCUMENT) {
            String eltName;

            switch (eventType) {
                case XmlPullParser.START_TAG:
                    eltName = parser.getName();

                    if ("Temp".equals(eltName)) {

                        viewModel = new SuccessViewModel();
                        viewModel.temp = parser.getAttributeValue(null,"number");
                        String count = parser.getAttributeValue(null,"count");

                        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
                        Date d = new Date();
                        Calendar c = Calendar.getInstance();
                        c.setTime(d);
                        c.add(Calendar.DATE, Integer.parseInt(count));
                        d = c.getTime();
                        String dayOfTheWeek = sdf.format(d);
                        viewModel.day = dayOfTheWeek;

                    }
                    break;

                case XmlPullParser.END_TAG:
                    eltName = parser.getName();
                    if (eltName.equalsIgnoreCase("Temp")){
                        arrayList.add(viewModel);
                    }
            }


            eventType = parser.next();
        }

    }

    }
