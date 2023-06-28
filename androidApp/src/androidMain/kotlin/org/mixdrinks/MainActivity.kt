package org.mixdrinks

import MainView
import NewToken
import android.content.Intent
import android.os.Bundle
import android.util.Log
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

        val appelProvider = OAuthProvider.newBuilder("apple.com")


        setAppleAuthStart {
            val pending = firebaseAuth.pendingAuthResult
            if (pending != null) {
                pending.addOnSuccessListener { authResult ->
                    Log.d("Main", "checkPending:onSuccess:$authResult")
                    authResult.user.sendNewToken()
                }.addOnFailureListener { e ->
                    Log.w("Main", "checkPending:onFailure", e)
                }
            } else {
                Log.d("Main", "pending: null")
                firebaseAuth.startActivityForSignInWithProvider(this, appelProvider.build())
                    .addOnSuccessListener { authResult ->
                        Log.d("Main", "activitySignIn:onSuccess:${authResult.user}")
                        authResult.user.sendNewToken()
                    }
                    .addOnFailureListener { e ->
                        Log.w("Main", "activitySignIn:onFailure", e)
                    }
            }

        }

        setLogout {
            FirebaseAuth.getInstance().signOut()
        }
    }

    override fun onStart() {
        super.onStart()
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
                    Log.e("Main", "token $token")
                    NewToken(token)
                }
            }
            .addOnFailureListener {
                Log.e("Main", "token error $it")
            }
    }
}
