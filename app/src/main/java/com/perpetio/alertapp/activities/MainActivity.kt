package com.perpetio.alertapp.activities

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.perpetio.alertapp.data_models.StatesInfoModel
import com.perpetio.alertapp.databinding.ActivityMainBinding
import com.perpetio.alertapp.receivers.WidgetRefreshReminder
import com.perpetio.alertapp.receivers.WidgetUpdateReceiver
import com.perpetio.alertapp.repository.Repository
import com.perpetio.alertapp.repository.getAlertApiService
import com.perpetio.alertapp.utils.Formatter
import com.perpetio.alertapp.utils.MapDrawer
import com.perpetio.alertapp.view_models.MainViewModel
import com.perpetio.alertapp.view_models.ViewModelState
import java.util.*

class MainActivity : BaseActivity<ActivityMainBinding>() {

    override fun getViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = Repository(getAlertApiService())
        val viewModel = ViewModelProvider(
            this, MainViewModel.FACTORY(repository)
        ).get(MainViewModel::class.java)

        setupObservers(viewModel)
        setupListeners(viewModel)

        viewModel.refreshMap()
        app.storage.repeatInterval?.let { interval ->
            WidgetRefreshReminder.setReminder(interval, this)
        }
        Log.d("123", "MainActivity onCreate end")
    }

    private fun setupObservers(viewModel: MainViewModel) {
        viewModel.state.observe(this) { state ->
            Log.d("123", "viewModel.state.observe $state")
            when (state) {
                ViewModelState.Loading -> showProgress()
                is ViewModelState.MapLoaded -> updateMap(state.statesInfo)
                is ViewModelState.Error -> showError(state.message)
                is ViewModelState.Completed -> hideProgress()
            }
        }
    }

    private fun setupListeners(
        viewModel: MainViewModel
    ) {
        binding.apply {
            refreshLayout.setOnRefreshListener {
                viewModel.refreshMap()
            }
        }
    }

    private fun updateMap(statesInfo: StatesInfoModel) {
        Log.d("123", "updateMap: $statesInfo")
        binding.apply {
            tvRefreshDate.text = Formatter.getShortFormat(Date())
            imgMapHolder.setImageBitmap(
                MapDrawer.drawMap(
                    statesInfo.states, this@MainActivity
                )
            )
        }
        WidgetUpdateReceiver.checkUpdate(
            statesInfo.states, this@MainActivity
        )
    }

    private fun showProgress() {
        binding.refreshLayout.isRefreshing = true
    }

    private fun hideProgress() {
        binding.refreshLayout.isRefreshing = false
    }
}