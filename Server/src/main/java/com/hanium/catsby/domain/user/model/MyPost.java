package com.hanium.catsby.domain.user.model;

import com.hanium.catsby.domain.bowl.model.BowlCommunity;
import com.hanium.catsby.domain.town.model.TownCommunity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor //빈생성자
@AllArgsConstructor //전체 생성자
@Builder
@Entity
public class MyPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "myPost_id")
    private int myPostId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bowlCommunity_id")
    private BowlCommunity bowlCommunity;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "townCommunity_id")
    private TownCommunity townCommunity;
}
