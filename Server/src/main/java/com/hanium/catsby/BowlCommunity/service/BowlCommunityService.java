package com.hanium.catsby.BowlCommunity.service;

import com.hanium.catsby.Bowl.domain.Bowl;
import com.hanium.catsby.Bowl.repository.BowlRepository;
import com.hanium.catsby.BowlCommunity.domain.BowlCommunity;
import com.hanium.catsby.BowlCommunity.repository.BowlCommunityRepository;
import com.hanium.catsby.User.domain.Users;
import com.hanium.catsby.User.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BowlCommunityService {

    private final BowlCommunityRepository bowlCommunityRepository;
    private final UserRepository userRepository;
    private final BowlRepository bowlRepository;

    @Transactional
    public Long savaCommunity(BowlCommunity bowlCommunity, Long userId, Long bowlId) {

        Users users = userRepository.findUser(userId);
        Bowl bowl = bowlRepository.findBowl(bowlId);

        bowlCommunity.setUser(users);
        bowlCommunity.setBowl(bowl);
        bowlCommunity.setCreateDate();

        bowlCommunityRepository.save(bowlCommunity);
        return bowlCommunity.getId();
    }

    @Transactional(readOnly = true)
    public List<BowlCommunity> findCommunities() {
        return bowlCommunityRepository.findAllBowlCommunity();
    }

    @Transactional(readOnly = true)
    public BowlCommunity findCommunity(Long bowlId) {
        return bowlCommunityRepository.findBowlCommunity(bowlId);
    }

    @Transactional
    public void delete(Long id) {
        bowlCommunityRepository.deleteById(id);
    }

    @Transactional
    public void update(Long id, byte[] image, String content){
        BowlCommunity bowlCommunity = bowlCommunityRepository.findBowlCommunity(id);
        bowlCommunity.setImage(image);
        bowlCommunity.setContent(content);
        bowlCommunity.setUpdateDate();
    }
}
