package com.hanium.catsby.domain.bowl.controller;

import com.hanium.catsby.domain.bowl.model.Bowl;
import com.hanium.catsby.domain.bowl.dto.BowlFeedDto;
import com.hanium.catsby.domain.bowl.service.BowlService;
import com.hanium.catsby.domain.common.sevice.S3Service;
import com.hanium.catsby.domain.notification.exception.DuplicateBowlInfoException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class BowlController {

    private final static String BOWL_QR_DIR_NAME = "image/bowl/qr/";
    private final static String BOWL_USER_DIR_NAME = "image/bowl/user/";

    private final BowlService bowlService;
    private final S3Service s3Service;

    @PostMapping("/bowl")
        public ResponseEntity<CreateBowlResponse> savaBowl(@RequestParam("info") String info, @RequestParam("name") String name, @RequestParam("address") String address, @RequestPart MultipartFile file){

        String qrUrl = s3Service.upload(file, BOWL_QR_DIR_NAME);

        Bowl bowl = new Bowl();
        bowl.setInfo(info);
        bowl.setName(name);
        bowl.setAddress(address);
        bowl.setQrImage(qrUrl);
        try {
            Long id = bowlService.enroll(bowl);
            return ResponseEntity.ok(new CreateBowlResponse(id));
        } catch (DuplicateBowlInfoException e) {
            return ResponseEntity.status(409).body(new CreateBowlResponse(null));
        }
    }

    @PutMapping("/bowl/{id}")
    public UpdateBowlResponse updateBowl(@PathVariable("id") Long id, @RequestBody UpdateBowlRequest request){
        bowlService.update(id, request.getName(), request.getInfo(), request.getAddress());
        Bowl findBowl = bowlService.findOne(id);
        return new UpdateBowlResponse(findBowl.getId(), findBowl.getInfo(), findBowl.getName(), findBowl.getAddress());
    }

    @DeleteMapping("/bowl/{id}")
    public void DeleteBowl(@PathVariable("id") Long id){
        bowlService.delete(id);
    }

    @GetMapping("/bowls/{uid}")
    public BowlResult userBowlList(@PathVariable("uid") String uid) {
        List<Bowl> findBowls = bowlService.findUserBowls(uid);
        List<BowlDto> collect = findBowls.stream()
                .map(b -> new BowlDto(b.getId(), b.getInfo(), b.getName(), b.getAddress(), b.getCreatedDate()))
                .collect(Collectors.toList());
        return new BowlResult(collect);
    }

    @PostMapping("/bowl/{uid}")
    public ResponseEntity<CreateBowlResponse> addUser(@PathVariable("uid") String uid, @RequestBody AddUserRequest request) {
        Long bowlId = bowlService.saveBowlUser(uid, request.getBowlInfo(), request.getLatitude(), request.getLongitude());
        return ResponseEntity.ok(new CreateBowlResponse(bowlId));
    }

    @GetMapping("/bowl/feed/{bowlId}")
    public ResponseEntity<BowlResult> bowlFeed(@PathVariable("bowlId") Long id) {
        return ResponseEntity.ok(new BowlResult<List<BowlFeedDto>>(bowlService.findBowlFeed(id)));
    }

    @GetMapping("/bowl/info/{bowlId}/{uid}")
    public com.hanium.catsby.domain.bowl.dto.BowlDto bowlInfo(@PathVariable("bowlId") Long id, @PathVariable("uid") String uid){
        return bowlService.getBowl(id, uid);
    }

    @PatchMapping("/bowl/image/{bowlId}/{uid}")
    public ResponseEntity<BowlResult<String>> updateImage(@PathVariable("bowlId") Long id, @PathVariable("uid") String uid, @RequestPart MultipartFile file) {
        String imgUrl = s3Service.upload(file, BOWL_USER_DIR_NAME, uid);
        bowlService.updateBowlImage(id, uid, imgUrl);
        return ResponseEntity.ok(new BowlResult<>("success"));
    }

    @Data
    static class BowlLocationResponse{
        Long id;
        String name;
        Double latitude;
        Double longitude;

        public BowlLocationResponse(Bowl bowl) {
            this.id = bowl.getId();
            this.name = bowl.getName();
            this.latitude = bowl.getLatitude();
            this.longitude = bowl.getLongitude();
        }
    }

    @Data
    @AllArgsConstructor
    static class BowlResult<T> {
        private T data;
    }

    @Data
    @AllArgsConstructor
    static class BowlDto{
        private Long id;
        private String info;
        private String name;
        private String address;
        private LocalDateTime createDate;
    }

    @Data
    static class CreateBowlRequest{
        private String info;
        private String name;
        private String address;
        private byte[] image;
    }

    @Data
    static class CreateBowlResponse{
        private Long id;

        public CreateBowlResponse(Long id) {
            this.id = id;
        }
    }

    @Data
    static class UpdateBowlRequest{
        private Long id;
        private String info;
        private String name;
        private String address;
    }

    @Data
    @AllArgsConstructor
    static class UpdateBowlResponse{
        private Long id;
        private String info;
        private String name;
        private String address;
    }

    @Data
    static class AddUserRequest {
        private String bowlInfo;
        private double latitude;
        private double longitude;
    }
}
