package com.tron.ehguardian.ui.screens.homeScreens.healthDataScreen

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.util.Log
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.airbnb.lottie.compose.*
import com.tron.ehguardian.R

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewPage() {
    val context = LocalContext.current
    var loadUrl by remember { mutableStateOf("https://myheartcheck.org.nz") }  // Default URL
    var loading by remember { mutableStateOf(false) }
    var isOffline by remember { mutableStateOf(false) }

    // Lottie Animation for Loading State
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.heartbeat))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    // Connectivity manager to track network status
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    // Register and handle network callback
    val networkCallback = remember {
        object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: android.net.Network) {
                super.onAvailable(network)
                isOffline = false  // Online mode
            }

            override fun onLost(network: android.net.Network) {
                super.onLost(network)
                isOffline = true  // Switch to offline mode
            }
        }
    }

    // Register network callback on component launch
    LaunchedEffect(Unit) {
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }

    // Unregister network callback when the composable is removed from the view
    DisposableEffect(Unit) {
        onDispose {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }
    }

    // WebView Composable
    if(!isOffline || !loading)
        AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { ctx ->
            WebView(ctx).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true  // Enable DOM storage (localStorage)
                settings.loadWithOverviewMode = true

                webViewClient = object : WebViewClient() {
                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                        super.onPageStarted(view, url, favicon)
                        loading = true  // Start loading animation
                    }

                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        loading = false  // Stop loading animation
                    }

                    // Handle URL changes inside WebView (button clicks, redirects, etc.)
                    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                        val newUrl = request?.url.toString()
                        Log.d("WebView", "Navigating to: $newUrl")
                        loadUrl = newUrl // Optionally, update the state to reflect the new URL
                        return false // Let the WebView handle the URL
                    }

                    override fun onReceivedError(
                        view: WebView?,
                        errorCode: Int,
                        description: String?,
                        failingUrl: String?
                    ) {
                        super.onReceivedError(view, errorCode, description, failingUrl)
                        loading = false
                        isOffline = true  // Switch to offline mode on error
                    }
                }

                webChromeClient = object :
                    WebChromeClient() {
                    override fun onConsoleMessage(message: ConsoleMessage?): Boolean {
                        Log.d("WebView", "JavaScript Console: ${message?.message()} at ${message?.sourceId()}:${message?.lineNumber()}")
                        return super.onConsoleMessage(message)
                    }
                }
            }
        },
        update = { webView ->
            webView.loadUrl(loadUrl)  // Load the new URL when it changes
        }
    )


    // Display Lottie animation when loading
    if (loading) {
        Column(
            modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            LottieAnimation(
                composition = composition,
                progress = progress,
                modifier = Modifier.size(200.dp)
            )
        }
    }

    // Display offline screen if offline
    if (isOffline) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.disconnected),
                contentDescription = "No Internet Image"
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "No Internet Connection", style = MaterialTheme.typography.headlineMedium)
        }
    }


}
