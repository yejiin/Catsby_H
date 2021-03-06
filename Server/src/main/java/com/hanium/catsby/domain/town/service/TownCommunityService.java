package com.hanium.catsby.domain.town.service;

import com.hanium.catsby.domain.town.model.TownCommunity;
import com.hanium.catsby.domain.town.repository.TownCommunityRepository;
import com.hanium.catsby.domain.user.model.MyPost;
import com.hanium.catsby.domain.user.repository.MyPostRepository;

import com.hanium.catsby.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class TownCommunityService {

    @Autowired
    TownCommunityRepository townCommunityRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    MyPostRepository myPostRepository;


    public String currentTime(){
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    @Transactional
    public TownCommunity writeTownCommunity(TownCommunity townCommunity, String uid) {//글 쓰기
        townCommunity.setUser(userRepository.findUserByUid(uid));
        townCommunity.setDate(currentTime());
        townCommunityRepository.save(townCommunity);

        //myPost
        MyPost myPost = new MyPost();
        myPost.setTownCommunity(townCommunity);
        myPostRepository.save(myPost);

        return townCommunity;
    }

    @Transactional(readOnly = true)
    public List<TownCommunity> listTownCommunity(){//글 목록
        return townCommunityRepository.findAll();
    }


    @Transactional
    public void deleteTownCommunity(int id) {//글 삭제
        townCommunityRepository.deleteById(id);
    }

    @Transactional
    public TownCommunity updateTownCommunity(int id, TownCommunity requestTownCommunity) {//글 수정
        TownCommunity townCommunity = townCommunityRepository.findById(id)
                .orElseThrow(()->{
                    return new IllegalArgumentException("글 찾기 실패 : 게시글 id를 찾을 수 없습니다.");
                }); //영속화 완료
        townCommunity.setTitle(requestTownCommunity.getTitle());
        townCommunity.setContent(requestTownCommunity.getContent());
        townCommunity.setAnonymous(requestTownCommunity.isAnonymous());
//        townCommunity.setImage(requestTownCommunity.getImage());
        //해당 함수로 종료시(Service가 종료될 때) 트랜잭션이 종료된다. 이때 더티체킹이 일어남 - 자동 업데이트됨. db쪽으로 flush

        return townCommunity;
    }
}
