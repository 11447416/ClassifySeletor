package com.example.jie.classifyseletor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.jie.classifyseletor.view.ClassifySeletorItem;
import com.example.jie.classifyseletor.view.ClassifySeletorView;
import com.example.jie.classifyseletor.view.ItemAdapter;

import java.util.ArrayList;
import java.util.List;

import static com.example.jie.classifyseletor.view.ClassifySeletorView.*;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";
    private List<ClassifySeletorItem> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        ClassifySeletorView classifySeletorView = (ClassifySeletorView) findViewById(R.id.test_1);
//        classifySeletorView.setClassifySeletorListener(new ClassifySeletorListener() {
//            @Override
//            public List<ClassifySeletorItem> getData(int level, ClassifySeletorItem item) {
//                Log.i("MainActivity", "main->getData: " + item.getName());
//                List<ClassifySeletorItem> d = new ArrayList<>();
//                for (ClassifySeletorItem i : data) {
//                    if (i.getParentId().equals(item.getId())) {
//                        d.add(i);
//                    }
//                }
//                return d;
//            }
//
//            @Override
//            public ClassifySeletorItem getFirstData() {
//                Log.i(TAG, "getFirstData: ");
//                for (ClassifySeletorItem classifySeletorItem : data) {
//                    if (classifySeletorItem.isRoot) {
//                        return classifySeletorItem;
//                    }
//                }
//                return new ClassifySeletorItem("0", "无分类", "1", true, 1, "safsdf");
//            }
//
//            @Override
//            public Boolean isFinal(ClassifySeletorItem item) {
//                for (int i = 0; i < data.size(); i++) {
//                    if (data.get(i).getLevel() == item.getLevel() + 1) {
//                        if (data.get(i).getParentId().equals(item.getId())) {
//                            return false;
//                        }
//                    }
//                }
//                return true;
//            }
//        });
        classifySeletorView.setup(data, new SeletorListener() {
            @Override
            public void clickItem(boolean isSelected, ItemAdapter.ItemViewHolder holder, int position, ClassifySeletorItem item) {
                Log.d(TAG, "clickItem() called with: isSelected = [" + isSelected + "], holder = [" + holder + "], position = [" + position + "], item = [" + item + "]");
            }

            @Override
            public void clickReset() {
                Log.i(TAG, "clickReset: ");
            }

            @Override
            public void clickOk(List<ClassifySeletorItem> selectItem) {
                Log.d(TAG, "clickOk() called with: selectItem = [" + selectItem.get(0).getName()+ "]");
            }
        });
        classifySeletorView.setSingleSelete(true);
    }

    private void init() {
        data.add(new ClassifySeletorItem("0", "全部分类", "1", true, 1, "safsdf"));//1
        for (int i = 0; i < 4; i++) {
            data.add(new ClassifySeletorItem(i + "A", "A:" + i, "1", false, 2, "0"));//2
            for (int j = 0; j < 4; j++) {
                data.add(new ClassifySeletorItem(j + "B", "B:" + j, "1", false, 3, j + "A"));//3
                for (int k = 0; k < 4; k++) {
                    data.add(new ClassifySeletorItem(k + "C", "C:" + k, "1", false, 4, k + "B"));//4
                    for (int l = 0; l < 4; l++) {
                        data.add(new ClassifySeletorItem(l + "D", "D:" + l, "1", false, 5, l + "C"));//5
                    }
                }
            }
        }
    }
}
