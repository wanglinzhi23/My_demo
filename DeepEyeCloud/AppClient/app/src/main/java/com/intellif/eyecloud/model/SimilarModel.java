package com.intellif.eyecloud.model;

import android.content.Context;

import com.intellif.eyecloud.R;
import com.intellif.eyecloud.utils.SPContent;
import com.intellif.eyecloud.view.ISimilarContact;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by intellif on 2017/9/18.
 */

public class SimilarModel  implements ISimilarContact.ISimilarModel{
    @Override
    public List<String> getList(Context context) {
        List<String> list = new ArrayList<>();
        String[] array = context.getResources().getStringArray(R.array.similar);
        for (int i = 0; i <array.length ; i++) {
            list.add(array[i]);
        }
        return list;
    }

    @Override
    public boolean setSimilar(Context context, int data) {
        boolean flag = false;
        SPContent.saveSimilar(context,data);
        return true;
    }
}
