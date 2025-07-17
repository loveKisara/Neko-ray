package com.neko.config

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.*
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.neko.v2ray.R
import com.neko.v2ray.ui.BaseActivity
import kotlinx.coroutines.*
import java.net.HttpURLConnection
import java.net.URL

class V2rayConfigActivity : BaseActivity() {

    private lateinit var textConfig: TextView
    private lateinit var textLoading: TextView
    private lateinit var btnGenerate: Button
    private lateinit var btnCopy: ImageView
    private lateinit var spinnerServer: Spinner

    private val serverUrls = mapOf(
        "Australia" to "https://raw.githubusercontent.com/Epodonios/bulk-xray-v2ray-vless-vmess-...-configs/refs/heads/main/sub/Australia/config.txt",
        "Austria" to "gg",
        "Bahrain" to "gg",
        "Brazil" to "gg",
        "Colombia" to "gg",
        "Costa Rica" to "gg",
        "Czech Republic" to "gg",
        "Finland" to "gg",
        "France" to "gg",
        "Germany" to "gg",
        "Hong Kong" to "gg",
        "India" to "gg",
        "Iran" to "gg",
        "Italy" to "gg",
        "Japan" to "gg",
        "Netherlands" to "gg",
        "Poland" to "gg",
        "Republic of Korea" to "gg",
        "Republic of Lithuania" to "gg",
        "Russia" to "gg",
        "Serbia" to "gg",
        "Singapore" to "gg",
        "Slovak Republic" to "gg",
        "Spain" to "gg",
        "Sweden" to "gg",
        "Switzerland" to "gg",
        "Turkey" to "gg",
        "United Arab Emirates" to "gg",
        "United Kingdom" to "gg",
        "United States" to "gg"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_v2ray_config)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        val toolbarLayout = findViewById<CollapsingToolbarLayout>(R.id.collapsing_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        textConfig = findViewById(R.id.textConfig)
        textLoading = findViewById(R.id.textLoading)
        btnGenerate = findViewById(R.id.btnGenerate)
        btnCopy = findViewById(R.id.btnCopy)
        spinnerServer = findViewById(R.id.spinnerServer)

        val serverNames = serverUrls.keys.toList()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, serverNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerServer.adapter = adapter

        btnGenerate.setOnClickListener {
            textLoading.text = "Loading..."
            textConfig.text = ""
            val selectedCountry = spinnerServer.selectedItem as String
            val configUrl = serverUrls[selectedCountry] ?: return@setOnClickListener
            fetchV2rayConfigFrom(configUrl)
        }

        btnCopy.setOnClickListener {
            val text = textConfig.text.toString()
            if (text.isNotBlank()) {
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("V2Ray Config", text)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "No config to copy", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchV2rayConfigFrom(rawUrl: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL(rawUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.connectTimeout = 10000
                connection.readTimeout = 10000

                val inputStream = connection.inputStream
                val content = inputStream.bufferedReader().use { it.readText() }

                val allowedPrefixes = listOf(
                    "vmess://", "trojan://", "vless://",
                    "ss://", "socks://", "http://",
                    "wireguard://", "hysteria2://"
                )

                val lines = content
                    .lines()
                    .map { it.trim() }
                    .filter { line -> allowedPrefixes.any { prefix -> line.startsWith(prefix, ignoreCase = true) } }

                val randomLine = lines.randomOrNull() ?: "No valid configuration available."

                delay(2000)

                runOnUiThread {
                    textConfig.text = randomLine
                    textLoading.text = ""
                }

            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    textConfig.text = "Failed to fetch configuration:\n${e.localizedMessage}"
                    textLoading.text = ""
                }
            }
        }
    }
}
