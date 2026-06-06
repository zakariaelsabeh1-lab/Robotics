package com.zakariaelsabeh.robofight

import android.app.Application
import com.zakariaelsabeh.robofight.data.models.PlayerProfile
import com.zakariaelsabeh.robofight.utils.PreferenceManager
import com.zakariaelsabeh.robofight.utils.SoundManager

class GameApplication : Application() {

    companion object {
        lateinit var instance: GameApplication
            private set
    }

    lateinit var prefManager: PreferenceManager
        private set
    lateinit var soundManager: SoundManager
        private set

    var currentProfile: PlayerProfile = PlayerProfile()

    override fun onCreate() {
        super.onCreate()
        instance = this
        prefManager = PreferenceManager(this)
        soundManager = SoundManager(this)

        val saved = prefManager.loadProfile()
        if (saved != null) currentProfile = saved
    }

    fun saveProfile() = prefManager.saveProfile(currentProfile)
}
