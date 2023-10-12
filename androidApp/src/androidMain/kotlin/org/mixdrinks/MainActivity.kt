package org.mixdrinks

import MainView
import NewToken
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.Keep
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import setAppleAuthStart
import setGoogleAuthStart
import setLogout
import trackAnalyticsCallback


@Keep
class MainActivity : AppCompatActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var register: ActivityResultLauncher<Intent>
    private lateinit var firebaseAuth: FirebaseAuth

    enum class AuthProvider(val trackName: String) {
        GOOGLE("google"), APPLE("apple")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        trackAnalyticsCallback = { action, data ->
            FirebaseAnalytics.getInstance(applicationContext)
                .logEvent(
                    action,
                    Bundle().apply {
                        data.forEach { (key, value) ->
                            putString(key, value)
                        }
                    })
        }

        val deepLink = intent?.data?.toString()
        window.statusBarColor = android.graphics.Color.parseColor("#FF2B4718")
        setContent {
            MainView(deepLink)
        }

        FirebaseApp.initializeApp(this)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        firebaseAuth = FirebaseAuth.getInstance()

        setGoogleAuthStart {
            signInGoogle()
        }

        val appleProvider = OAuthProvider.newBuilder("apple.com")

        setAppleAuthStart {
            signInApple(appleProvider)
        }

        setLogout {
            FirebaseAuth.getInstance().signOut()
        }
    }

    override fun onStart() {
        super.onStart()
        firebaseAuth.currentUser?.sendNewToken()
        register = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleResult(task)
            }
        }
    }

    private fun signInApple(appleProvider: OAuthProvider.Builder) {
        authStart(AuthProvider.APPLE)
        val pending = firebaseAuth.pendingAuthResult
        if (pending != null) {
            pending.addOnSuccessListener { authResult ->
                authResult.user.sendNewToken()
            }.addOnFailureListener { e ->
                authFail(AuthProvider.APPLE, e)
            }
        } else {
            firebaseAuth.startActivityForSignInWithProvider(this, appleProvider.build())
                .addOnSuccessListener { authResult ->
                    authResult.user.sendNewToken()
                }
                .addOnFailureListener { e ->
                    authFail(AuthProvider.APPLE, e)
                }
        }
    }

    private fun signInGoogle() {
        val signInIntent: Intent = googleSignInClient.signInIntent
        register.launch(signInIntent)
    }

    private fun handleResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount? = completedTask.getResult(ApiException::class.java)
            if (account != null) {
                update(account)
            }
        } catch (e: ApiException) {
            authFail(AuthProvider.GOOGLE, e)
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun update(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { _ ->
                FirebaseAuth.getInstance().currentUser?.sendNewToken()
            }
            .addOnFailureListener { e ->
                authFail(AuthProvider.GOOGLE, e)
            }
    }

    private fun FirebaseUser?.sendNewToken() {
        if (this == null) {
            Firebase.crashlytics.recordException(Exception("User is null"))
            return
        }

        getIdToken(true)
            .addOnCompleteListener {
                it.result?.token?.let { token ->
                    NewToken(token)
                }
            }
            .addOnFailureListener {
                Firebase.crashlytics.recordException(it)
            }
    }

    private fun authStart(provider: AuthProvider) {
        Firebase.analytics.logEvent("auth_start", Bundle().apply {
            putString("provider", provider.trackName)
        })
    }

    private fun authFail(provider: AuthProvider, exception: Exception? = null) {
        Firebase.analytics.logEvent("auth_fail", Bundle().apply {
            putString("provider", provider.trackName)
        })
        if (exception != null) {
            Firebase.crashlytics.recordException(exception)
        }
    }
}
