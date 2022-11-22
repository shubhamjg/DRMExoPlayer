package com.example.drmexoplayer

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.drmexoplayer.databinding.ActivityMainBinding
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.dash.DashChunkSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource
import com.google.android.exoplayer2.source.hls.DefaultHlsDataSourceFactory
import com.google.android.exoplayer2.source.hls.DefaultHlsExtractorFactory
import com.google.android.exoplayer2.source.hls.HlsDataSourceFactory
import com.google.android.exoplayer2.source.hls.HlsExtractorFactory
import com.google.android.exoplayer2.source.hls.HlsMediaChunkExtractor
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.util.MimeTypes

class MainActivity : AppCompatActivity() {

    private lateinit var playerView: ExoPlayer
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        initializePlayer()
    }

    private fun initializePlayer(){
        val defaultHttpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setUserAgent(USER_AGENT)
            .setTransferListener(
                DefaultBandwidthMeter.Builder(this)
                    .setResetOnNetworkTypeChange(false)
                    .build()
            )
        val dashChunkSourceFactory: DashChunkSource.Factory = DefaultDashChunkSource.Factory(
            defaultHttpDataSourceFactory
        )

        val manifestDataSourceFactory = DefaultHttpDataSource.Factory().setUserAgent(USER_AGENT)
        val dashMediaSource = DashMediaSource.Factory(dashChunkSourceFactory,manifestDataSourceFactory)
            .createMediaSource(
                MediaItem.Builder()
                    .setUri(Uri.parse(URL))
                    .setDrmConfiguration(
                        MediaItem.DrmConfiguration.Builder(drmSchemeUuid)
                            .setLicenseUri(DRM_LICENSE_URL).build()
                    )
                    .setMimeType(MimeTypes.APPLICATION_MPD)
                    .setTag(null)
                    .build()
            )

//        val hlsMediaSource = HlsMediaSource.Factory(manifestDataSourceFactory)
//            .createMediaSource(
//                MediaItem.Builder()
//                    .setUri(Uri.parse(URL))
////                    .setDrmConfiguration(
////                        MediaItem.DrmConfiguration.Builder(drmSchemeUuid)
////                            .setLicenseUri(DRM_LICENSE_URL).build()
////                    )
//                    .setMimeType(MimeTypes.APPLICATION_M3U8)
//                    .setTag(null)
//                    .build()
//            )


        ExoPlayer.Builder(this)
            .setSeekForwardIncrementMs(10000)
            .setSeekBackIncrementMs(10000)
            .build().also {
                playerView = it
            }
        playerView.playWhenReady = true
        binding.playerView.player = playerView
        playerView.setMediaSource(dashMediaSource, true)
        playerView.prepare()
    }

    override fun onPause() {
        super.onPause()
        playerView.playWhenReady = false
    }

    companion object{
        const val URL = "https://bitmovin-a.akamaihd.net/content/art-of-motion_drm/mpds/11331.mpd"
        const val DRM_LICENSE_URL = "https://proxy.uat.widevine.com/proxy?provider=widevine_test"
        const val USER_AGENT = "ExoPlayer-Drm"
        val drmSchemeUuid = C.WIDEVINE_UUID

//        const val URL = "https://demo.unified-streaming.com/k8s/features/no-handler-origin/multi-format-drm/master.m3u8"
//        const val DRM_LICENSE_URL = "https://proxy.uat.widevine.com/proxy"
//        const val USER_AGENT = "ExoPlayer-Hls"
//        val drmSchemeUuid = C.WIDEVINE_UUID // DRM Type

    }
}