package org.techtown.catsby;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import android.Manifest;

import org.techtown.catsby.home.adapter.BowlCheckListAdapter;
import org.techtown.catsby.home.model.Bowl;
import org.techtown.catsby.retrofit.RetrofitClient;
import org.techtown.catsby.retrofit.dto.BowlCommunity;
import org.techtown.catsby.retrofit.dto.BowlList;
import org.techtown.catsby.retrofit.service.BowlCommunityService;
import org.techtown.catsby.retrofit.service.BowlService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Writemain extends AppCompatActivity{

    private static final String TAG = "blackjin";

    ListView listview ;
    Button btn_photo;
    ImageView iv_photo;

    private Boolean isPermission = true;

    final static int PICK_FROM_ALBUM= 1;
    String mCurrentPhotoPath;
    final static int PICK_FROM_CAMERA= 1;

    BowlService bowlService = RetrofitClient.getBowlService();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    BowlCommunityService bowlCommunityService = RetrofitClient.getBowlCommunityService();

    int[] postImg = {R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground};

    ArrayList<String> bowlNameArray = new ArrayList<>();
    ArrayList<Integer> bowIdArray = new ArrayList<>();
    static ArrayList<Bowl> bowlList = new ArrayList<>();
    String allContext;
    BowlCheckListAdapter adapter;
    static int cPosition;

    Uri photoUri;

    File tempFile;
    File image;
    int[] bowlImg = {R.drawable.ic_baseline_favorite_red, R.drawable.ic_baseline_star_border_24, R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writemain);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("홈 화면 글쓰기");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (user != null) {
            loadBowls(user.getUid());
        }

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tedPermission();

        //카메라 시작

        iv_photo = findViewById(R.id.iv_photo);
        btn_photo = findViewById(R.id.btn_photo);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "권한 설정 완료");
            }
            else
                { Log.d(TAG, "권한 설정 요청");
                ActivityCompat.requestPermissions(Writemain.this, new String[]
                        {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
        }
        btn_photo.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_photo:
                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, PICK_FROM_CAMERA); break;
                }
            }
        });
    }


    private void savePost(File file, int id, String uid, String context) {

        RequestBody content = RequestBody.create(MediaType.parse("text/plain"), context);
        RequestBody filePath = RequestBody.create(MediaType.parse("text/plain"), image.toString());

        HashMap<String, RequestBody> map = new HashMap<String, RequestBody>();
        map.put("content", content);
        map.put("path", filePath);
        InputStream inputStream = null;
        try {
            inputStream = this.getContentResolver().openInputStream(photoUri);
        }catch(IOException e) {
            e.printStackTrace();
        }

        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream);

        RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpg"), byteArrayOutputStream.toByteArray());
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName() ,requestBody);
        bowlCommunityService.saveCommunity(body, id, uid, map).enqueue(new Callback<List<BowlCommunity>>() {
            @Override
            public void onResponse(Call<List<BowlCommunity>> call, Response<List<BowlCommunity>> response) {
                System.out.println(" success" );
            }

            @Override
            public void onFailure(Call<List<BowlCommunity>> call, Throwable t) {
                System.out.println("t.getMessage() = " + t.getMessage());
            }
        });
    }

    private void loadBowls(String uid) {
        bowlService.getBowls(uid).enqueue(new Callback<BowlList>() {
            @Override
            public void onResponse(Call<BowlList> call, Response<BowlList> response) {
                if(response.isSuccessful()) {
                    BowlList result = response.body();

                    for(int i =0; i < result.size(); i++){
                        bowlNameArray.add(result.getBowls().get(i).getName());
                        Bowl bowl = new Bowl(result.getBowls().get(i).getId(), bowlImg[i] , result.getBowls().get(i).getName(), result.getBowls().get(i).getInfo(), result.getBowls().get(i).getAddress(), result.getBowls().get(i).getUpdated_time());
                        bowlList.add(bowl);
                    }

                    adapter = new BowlCheckListAdapter(bowlList, allContext);
                    // 첫 번째 아이템 추가.
                    for (int i =0; i < bowlNameArray.size(); i++){
                        adapter.addItem(ContextCompat.getDrawable(getApplicationContext(), R.drawable.bg_indicator_active), bowlNameArray.get(i), i) ;
                    }

                    listview = (ListView) findViewById(R.id.listview1);
                    listview.setAdapter(adapter);
                }

            }

            @Override
            public void onFailure(Call<BowlList> call, Throwable t) {
                System.out.println("t.getMessage() loadBowls= " + t.getMessage());
            }
        });

    }

    public static void clickSave(int clickPosition){
        cPosition = clickPosition;
        //System.out.println("click ~~~ position = " + clickPosition);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PICK_FROM_CAMERA:
                if (resultCode == RESULT_OK && data.hasExtra("data")) {
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    if (bitmap != null) {
                        iv_photo.setImageBitmap(bitmap);
                    }
                }
                break;
        }

        if (requestCode == PICK_FROM_ALBUM) {
            photoUri = data.getData();
            Log.d(TAG, "PICK_FROM_ALBUM photoUri : " + photoUri);

            Cursor cursor = null;
            try {
                String[] proj = {MediaStore.Images.Media.DATA};
                assert photoUri != null;
                cursor = getContentResolver().query(photoUri, proj, null, null, null);
                assert cursor != null;
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();

                image = new File(cursor.getString(column_index));
                tempFile = new File(cursor.getString(column_index));
                //System.out.println("tempFile = " + tempFile);

                Log.d(TAG, "tempFile Uri : " + Uri.fromFile(tempFile));

            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
            setImage();

        } else if (requestCode == PICK_FROM_CAMERA) {
            setImage();
        }
    }

    /**
     *  앨범에서 이미지 가져오기
     */
    private void goToAlbum() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }


    /**
     *  카메라에서 이미지 가져오기
     */
    private void takePhoto() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            tempFile = createImageFile();

        } catch (IOException e) {
            System.out.println("e.getMessage() " + e.getMessage());
            Toast.makeText(this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            finish();
            e.printStackTrace();
        }
        if (tempFile != null) {

            Uri photoUri = Uri.fromFile(tempFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(intent, PICK_FROM_CAMERA);
        }
    }

    /**
     *  폴더 및 파일 만들기
     */
    private File createImageFile() throws IOException {

        // 이미지 파일 이름 ( blackJin_{시간}_ )
        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
        String imageFileName = "blackJin_" + timeStamp + "_";

        // 이미지가 저장될 폴더 이름 ( blackJin )
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/blackJin/");
        if (!storageDir.exists()) storageDir.mkdirs();

        // 파일 생성
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        Log.d(TAG, "createImageFile : " + image.getAbsolutePath());
        System.out.println("image = !!!!" + image);
        return image;
    }

    /**
     *  tempFile 을 bitmap 으로 변환 후 ImageView 에 설정한다.
     */
    private void setImage() {

        ImageView imageView = findViewById(R.id.imageView);
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap originalBm = BitmapFactory.decodeFile(tempFile.getAbsolutePath(), options);
        Log.d(TAG, "setImage : " + tempFile.getAbsolutePath());
        imageView.setImageBitmap(originalBm);

        /**
         *  tempFile 사용 후 null 처리를 해줘야 합니다.
         *  (resultCode != RESULT_OK) 일 때 tempFile 을 삭제하기 때문에
         *  기존에 데이터가 남아 있게 되면 원치 않은 삭제가 이뤄집니다.
         */
        tempFile = null;

    }

    /**
     *  권한 설정
     */
    private void tedPermission() {

        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                // 권한 요청 성공
                isPermission = true;
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                // 권한 요청 실패
                isPermission = false;
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage(getResources().getString(R.string.permission_2))
                .setDeniedMessage(getResources().getString(R.string.permission_1))
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}









