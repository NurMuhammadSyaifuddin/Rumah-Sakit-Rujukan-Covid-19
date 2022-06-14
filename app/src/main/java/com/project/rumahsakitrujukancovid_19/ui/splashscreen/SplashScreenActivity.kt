package com.project.rumahsakitrujukancovid_19.ui.splashscreen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.google.firebase.auth.FirebaseAuth
import com.project.rumahsakitrujukancovid_19.databinding.ActivitySplashScreenBinding
import com.project.rumahsakitrujukancovid_19.ui.login.LoginActivity
import com.project.rumahsakitrujukancovid_19.utils.HOSPITAL_ADMIN
import com.project.rumahsakitrujukancovid_19.utils.USER_NORMAL
import com.project.rumahsakitrujukancovid_19.utils.visible
import org.koin.android.viewmodel.ext.android.viewModel

@Suppress("UNUSED_EXPRESSION")
class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding

    private val auth by lazy { FirebaseAuth.getInstance() }

    private val viewModel: SplashScreenViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onAction()
    }

    private fun onAction() {
        binding.apply {
            progressBar.visible()
            if (auth.currentUser != null) {
                viewModel.getLevelUser().observe(this@SplashScreenActivity) {
                    when (it) {
                        USER_NORMAL -> moveToHome(moveToUserNormalModule())
                        HOSPITAL_ADMIN -> moveToHome(moveToHospitalAdmin())
                    }
                }
            }else{
                Handler(Looper.getMainLooper())
                    .postDelayed({
                        Intent(this@SplashScreenActivity, LoginActivity::class.java).also { intent ->
                            startActivity(intent)
                            finishAffinity()
                        }
                    }, 400L)
            }
        }
    }

    private fun moveToHome(intent: Unit) {
        Handler(Looper.getMainLooper())
            .postDelayed({
                intent
            }, 200L)
    }

    private fun moveToHospitalAdmin() {
        Intent(this, Class.forName("com.project.hospital_admin.MainHospitalAdminActivity")).also {
            startActivity(it)
            finishAffinity()
        }
    }

    private fun moveToUserNormalModule() {
        Intent(this, Class.forName("com.project.user.MainUserActivity")).also {
            startActivity(it)
            finishAffinity()
        }
    }

}