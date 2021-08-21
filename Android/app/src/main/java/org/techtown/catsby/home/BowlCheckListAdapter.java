package org.techtown.catsby.home;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.techtown.catsby.R;
import org.techtown.catsby.Writemain;
import org.techtown.catsby.home.model.Bowl;
import org.techtown.catsby.retrofit.RetrofitClient;
import org.techtown.catsby.retrofit.service.BowlCommunityService;

import java.util.ArrayList;

public class BowlCheckListAdapter extends BaseAdapter {

    private ArrayList<BowlCheck> listViewItemList = new ArrayList<BowlCheck>() ;
    private ArrayList<Bowl> bowlData;
    private String conText;

    BowlCommunityService bowlCommunityService = RetrofitClient.getBowlCommunityService();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    int[] postImg = {R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground};


    public BowlCheckListAdapter(ArrayList<Bowl> bowlData, String conText) {
        this.bowlData = bowlData;
        this.conText = conText;
    }

    @Override
    public int getCount() {
        return listViewItemList.size() ;
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.write_main_bowl, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        ImageView iconImageView = (ImageView) convertView.findViewById(R.id.imageView1) ;
        TextView textTextView = (TextView) convertView.findViewById(R.id.textView1) ;

        CheckBox cb = (CheckBox) convertView.findViewById(R.id.bowlCheckBox);
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                System.out.println("compoundButton = " + compoundButton);
                Writemain.clickSave(position);

                //

            }
        });

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        BowlCheck listViewItem = listViewItemList.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        iconImageView.setImageDrawable(listViewItem.getIcon());
        textTextView.setText(listViewItem.getText());


        return convertView;
    }




    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position ;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position) ;
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(Drawable icon, String text, int i) {
        BowlCheck item = new BowlCheck();

        item.setIcon(icon);
        item.setText(text);
        item.setPosition(i);

        listViewItemList.add(item);
    }
}
