package org.techtown.catsby;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.common.KakaoSdk;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    private SignInButton btn_google;
    private GoogleApiClient googleApiClient;
    private static final int REQ_SIGN_GOOGLE = 100;

    private CallbackManager callbackManager;
    // 파이어베이스 인증 객체 생성
    private FirebaseAuth firebaseAuth;

    private LoginButton buttonFacebook;
    private boolean loginSuccess;

    private ImageView buttonKakao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        KakaoSdk.init(this, getString(R.string.kakao_native_app_key));

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();

        //파이어베이스 인증 객체 선언
        firebaseAuth = FirebaseAuth.getInstance();
        btn_google = findViewById(R.id.btn_google);
        btn_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent, REQ_SIGN_GOOGLE);
            }
        });

        //페이스북 콜백 등록
        callbackManager = CallbackManager.Factory.create();

        //로그인 확인
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

        buttonFacebook = (LoginButton) findViewById(R.id.btn_facebook_login);
        buttonFacebook.setReadPermissions("email","public_profile");
        buttonFacebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                signInWithCredential(FacebookAuthProvider.getCredential(loginResult.getAccessToken().getToken()));
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }

        });

        buttonKakao = (ImageView) findViewById(R.id.btn_kakao_login);
        buttonKakao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(LoginActivity.this)) {
                    UserApiClient.getInstance().loginWithKakaoTalk(LoginActivity.this, kakaoCallback());
                } else {
                    UserApiClient.getInstance().loginWithKakaoAccount(LoginActivity.this, kakaoCallback());
                }
            }
        });
        updateKakaoLoginUi();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQ_SIGN_GOOGLE){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess()){
                GoogleSignInAccount account = result.getSignInAccount();
                signInWithCredential(GoogleAuthProvider.getCredential(account.getIdToken(), null));
            }
        }
    }

    // 페이스북, 구글 로그인 이벤트
    // 사용자가 정상적으로 로그인한 후 페이스북 로그인 버튼의 onSuccess 콜백 메소드에서 로그인한 사용자의
    // 액세스 토큰을 가져와서 Firebase 사용자 인증 정보로 교환하고,
    // Firebase 사용자 인증 정보를 사용해 Firebase에 인증.
    private void signInWithCredential(AuthCredential credential) {

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(LoginActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        }
                        else{
                            Toast.makeText(LoginActivity.this, "로그인 실패",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void signInWithKakaoToken(String customToken) {
        firebaseAuth.signInWithCustomToken(customToken)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("LoginActivity", "signInWithCustomToken:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            updateKakaoLoginUi();
                        } else {
                            Log.w("LoginActivity", "signInWithCustomToken:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull @NotNull ConnectionResult connectionResult) {

    }

    @NotNull
    private Function2<OAuthToken, Throwable, Unit> kakaoCallback() {
        return new Function2<OAuthToken, Throwable, Unit>() {
            @Override
            public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {
//                if (oAuthToken != null) {
//                    // firebase 연동 로그인은 서버에서 custom token 생성 후 구현 예정
//                    signInWithKakaoToken(customToken);
//                }

                if (throwable != null) {
                    // 로그인 실패시
                    Log.e("LoginActivity", throwable.getMessage());
                    Toast.makeText(getApplication(), "로그인 실패", Toast.LENGTH_LONG).show();
                }
                updateKakaoLoginUi();
                return null;
            }
        };
    }

    private void updateKakaoLoginUi() {
        UserApiClient.getInstance().me(new Function2<User, Throwable, Unit>() {
            @Override
            public Unit invoke(User user, Throwable throwable) {
                if (user != null) {
                    Intent intent = new Intent(getApplication(), MainActivity.class);
                    startActivity(intent);
                }
                return null;
            }
        });
    }
}