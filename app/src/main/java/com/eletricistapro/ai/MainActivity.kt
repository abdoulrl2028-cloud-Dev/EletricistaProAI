package com.eletricistapro.ai

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.eletricistapro.ai.ui.EletricistaApp
import com.eletricistapro.ai.ui.EletricistaTheme
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun attachBaseContext(newBase: Context) {
        val prefs = newBase.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val tag = prefs.getString(KEY_LANG, "pt-BR") ?: "pt-BR"
        val locale = Locale.forLanguageTag(tag)
        Locale.setDefault(locale)
        val config = Configuration(newBase.resources.configuration)
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        super.attachBaseContext(newBase.createConfigurationContext(config))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EletricistaTheme {
                EletricistaApp(
                    onLanguage = { tag ->
                        getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                            .edit()
                            .putString(KEY_LANG, tag)
                            .apply()
                        recreate()
                    },
                )
            }
        }
    }

    companion object {
        const val PREFS = "progress"
        const val KEY_LANG = "lang"
    }
}
