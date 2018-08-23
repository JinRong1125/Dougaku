package shinei.com.dougaku

import android.app.Activity
import android.app.Application
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
//import com.squareup.leakcanary.LeakCanary
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import shinei.com.dougaku.di.AppInjector
import javax.inject.Inject

class App : Application(), HasActivityInjector {

    @Inject lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    override fun activityInjector() = dispatchingAndroidInjector

    var defaultDataSourceFactory: DefaultDataSourceFactory? = null

    override fun onCreate() {
        super.onCreate()
//        if (LeakCanary.isInAnalyzerProcess(this))
//            return
//        LeakCanary.install(this)

        AppInjector.init(this)

        defaultDataSourceFactory = DefaultDataSourceFactory(
                this, Util.getUserAgent(this, resources.getString(R.string.app_name)), null)
    }
}