package com.hanium.catsby.user.domain;

import com.hanium.catsby.bowl.domain.BowlComment;
import com.hanium.catsby.town.domain.TownComment;
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
public class MyComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "myComment_id")
    private int myCommentId;


    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bowlComment_id")
    private BowlComment bowlComment;


    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "townComment_id")
    private TownComment townComment;
}