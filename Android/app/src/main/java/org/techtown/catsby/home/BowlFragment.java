package org.techtown.catsby.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.techtown.catsby.R;
import org.techtown.catsby.home.adapter.BowlAdapter;
import org.techtown.catsby.home.adapter.FeedAdapter;
import org.techtown.catsby.home.model.Bowl;
import org.techtown.catsby.home.model.Feed;
import org.techtown.catsby.qrcode.LoadingActivity;
import org.techtown.catsby.retrofit.RetrofitClient;
import org.techtown.catsby.retrofit.dto.BowlCommunity;
import org.techtown.catsby.retrofit.dto.BowlList;
import org.techtown.catsby.retrofit.service.BowlCommunityService;
import org.techtown.catsby.retrofit.service.BowlService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.time.LocalDateTime.now;

public class BowlFragment extends Fragment implements BowlAdapter.BowlAdapterClickListener {
    private Context mContext;
    private FragmentManager fragmentManager;

/*
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }
*/
    BowlAdapter bowlAdapter;
    ArrayList<byte[]> bowlImageArray = new ArrayList<>();
    BowlService bowlService = RetrofitClient.getBowlService();
    BowlCommunityService bowlCommunityService = RetrofitClient.getBowlCommunityService();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    View view;
    Intent intent;
    ArrayList<Bowl> bowlList;

    //HashMap bowlMap = new HashMap();
    ArrayList<Feed> feedList= new ArrayList<>();


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        view = null;
        view = inflater.inflate(R.layout.fragment_home, container, false);
        setHasOptionsMenu(true);

        if (user != null) {
            loadBowls(user.getUid());
            bowlList = new ArrayList<>();
            feedList= new ArrayList<>();
            bowlAdapter = new BowlAdapter(bowlList);
        }

        bowlAdapter.setOnClickListener(this);
        LinearLayout reFresh = view.findViewById(R.id.refreshView);
        reFresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view = null;
                feedList= new ArrayList<>();
                bowlList = new ArrayList<>();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(BowlFragment.this).attach(BowlFragment.this).commit();
            }
        });

    return view;
}

    public void fresh(){
        view = null;
        feedList= new ArrayList<>();
        bowlList = new ArrayList<>();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(BowlFragment.this).attach(BowlFragment.this).commit();
    }

    private void loadBowls(String uid) {
        bowlService.getBowls(uid).enqueue(new Callback<BowlList>() {
            @Override
            public void onResponse(Call<BowlList> call, Response<BowlList> response) {
                if(response.isSuccessful()) {
                    BowlList result = response.body();
                    HashSet<Integer> bowlUniId = new HashSet<Integer>();

                    for(int i =0; i < result.size(); i++){
                        Bowl bowl = new Bowl(result.getBowls().get(i).getBowl_id(), R.drawable.pic_001, result.getBowls().get(i).getName(), result.getBowls().get(i).getInfo(), result.getBowls().get(i).getAddress(), result.getBowls().get(i).getUpdated_time());
                        loadCommunity(result.getBowls().get(i).getBowl_id());
                        bowlList.add(bowl);
                        bowlUniId.add(result.getBowls().get(i).getBowl_id());
                    }

                    RecyclerView bowlRecyclerView = (RecyclerView)view.findViewById(R.id.horizontal_recyclerview);
                    RecyclerView.LayoutManager bowlLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                    bowlRecyclerView.setLayoutManager(bowlLayoutManager);
                    bowlRecyclerView.setAdapter(bowlAdapter);
                }

            }

            @Override
            public void onFailure(Call<BowlList> call, Throwable t) {
                System.out.println("t.getMessage() = " + t.getMessage());
            }
        });
    }

    private void loadCommunity(int bowlId) {
        bowlCommunityService.getCommunitiesByBowl(bowlId).enqueue(new Callback<List<BowlCommunity>>() {
            @Override
            public void onResponse(Call<List<BowlCommunity>> call, Response<List<BowlCommunity>> response) {
                if(response.isSuccessful()) {
                    ArrayList<BowlCommunity> BowlCommunityResult = (ArrayList<BowlCommunity>) response.body();

                    DateDescending dateAscending = new DateDescending();
                    Collections.sort(BowlCommunityResult, dateAscending);

                    for (int i=0; i < BowlCommunityResult.size(); i++) {
                        Feed feed = new Feed(BowlCommunityResult.get(i).getId(), R.drawable.pic_001, BowlCommunityResult.get(i).getUser().getId(), BowlCommunityResult.get(i).getUser().getNickname(), BowlCommunityResult.get(i).getImage().getBytes(), BowlCommunityResult.get(i).getContent(), BowlCommunityResult.get(i).getUid(), BowlCommunityResult.get(i).getCreatedDate());

                        if (!feedList.contains(feed)){
                            feedList.add(feed);
                        }

                        DateDescending2 dateAscending2 = new DateDescending2();
                        Collections.sort(feedList, dateAscending2);

                    }
                    RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.recyclerview);
                    recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), 1));
                    RecyclerView.LayoutManager feedLayoutManager = new LinearLayoutManager(getActivity());
                    recyclerView.setLayoutManager(feedLayoutManager);
                    FeedAdapter feedAdapter = new FeedAdapter(feedList);
                    recyclerView.setAdapter(feedAdapter);
                }
            }
            @Override
            public void onFailure(Call<List<BowlCommunity>> call, Throwable t) {
                System.out.println("t.getMessage() loadCommunity = " + t.getMessage());
            }
        });
    }

    @Override
    public void onItemClicked(int position) {
        Toast.makeText(getActivity(), "Item : "+position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.actionbar_createqr, menu);
        inflater.inflate(R.menu.actionbar_write, menu);
        inflater.inflate(R.menu.actionbar_qrscan, menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_write:
                Intent intent = new Intent(getActivity(), BowlWrite.class);
                startActivity(intent);
                break;

            case R.id.action_qrscan:
                Intent intent2 = new Intent(getActivity(), LoadingActivity.class);
                startActivity(intent2);
        }
        return super.onOptionsItemSelected(item);
    }
}


class DateDescending implements Comparator<BowlCommunity> {
    @Override
    public int compare(BowlCommunity bc1, BowlCommunity bc2) {
        String temp1 = bc1.getCreatedDate();
        String temp2 = bc2.getCreatedDate();
        return temp2.compareTo(temp1);
    }
}

class DateDescending2 implements Comparator<Feed> {
    @Override
    public int compare(Feed feed1, Feed feed2) {
        String temp1 = feed1.getCreateDate();
        String temp2 = feed2.getCreateDate();
        return temp2.compareTo(temp1);
    }
}