package cn.cleartv.icu.ui

import android.net.Uri
import android.widget.MediaController
import cn.cleartv.icu.BaseActivity
import cn.cleartv.icu.R
import kotlinx.android.synthetic.main.activity_video_player.*

/**
 * <pre>
 *     author : Lee
 *     e-mail : lipengfei@cleartv.cn
 *     time   : 2020/09/09
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class VideoPlayerActivity : BaseActivity() {
    override fun contentLayoutRes(): Int {
        return R.layout.activity_video_player
    }

    override fun afterSetContentView() {
        video_view.setMediaController(MediaController(this).apply {
            setAnchorView(video_view)
            keepScreenOn = true
        })
        video_view.setVideoURI(Uri.parse(intent.getStringExtra("url")))
        video_view.requestFocus()
        video_view.start()
        btn_back.setOnClickListener { finish() }
    }

    override fun onResume() {
        super.onResume()
        video_view.resume()
    }

    override fun onPause() {
        video_view.pause()
        super.onPause()

    }

    override fun onDestroy() {
        video_view.suspend()
        super.onDestroy()
    }
}