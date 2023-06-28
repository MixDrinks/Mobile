package org.mixdrinks

import MainView
import NewToken
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import setAppleAuthStart
import setGoogleAuthStart
import setLogout


class MainActivity : AppCompatActivity() {

    lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var register: ActivityResultLauncher<Intent>
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val deepLink = intent?.data?.toString()
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
            val pending = firebaseAuth.pendingAuthResult
            if (pending != null) {
                pending.addOnSuccessListener { authResult ->
                    authResult.user.sendNewToken()
                }.addOnFailureListener { e ->
                    Firebase.crashlytics.recordException(e)
                }
            } else {
                firebaseAuth.startActivityForSignInWithProvider(this, appleProvider.build())
                    .addOnSuccessListener { authResult ->
                        authResult.user.sendNewToken()
                    }
                    .addOnFailureListener { e ->
                        Firebase.crashlytics.recordException(e)
                    }
            }
        }

        setLogout {
            FirebaseAuth.getInstance().signOut()
        }
    }

    override fun onStart() {
        super.onStart()
        firebaseAuth.currentUser?.getIdToken(true)
            ?.addOnCompleteListener {
                it.result?.token?.let { token ->
                    NewToken(token)
                }
            }
            ?.addOnFailureListener {
                Firebase.crashlytics.recordException(it)
            }
        register = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleResult(task)
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
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun update(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                FirebaseAuth.getInstance().currentUser?.sendNewToken()
            }
    }

    private fun FirebaseUser?.sendNewToken() {
        this ?: return
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
}
