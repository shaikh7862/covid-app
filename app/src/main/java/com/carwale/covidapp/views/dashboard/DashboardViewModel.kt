package com.carwale.covidapp.views.dashboard

import android.os.Handler
import android.util.EventLog
import com.carwale.covidapp.base.BaseViewModel
import com.carwale.covidapp.models.DataResponse
import com.carwale.covidapp.room.AppDB
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardViewModel : BaseViewModel(){
    fun callApi(){
        Handler().postDelayed({
            CoroutineScope(Dispatchers.IO).launch {
                userService.getData().enqueue(object : Callback<DataResponse> {
                    override fun onFailure(call: Call<DataResponse>, t: Throwable) {
                    }

                    override fun onResponse(
                        call: Call<DataResponse>,
                        response: Response<DataResponse>
                    ) {
                        val res = response.body()

                        CoroutineScope(Dispatchers.IO).launch {
                            userDao.deleteGlobalData()
                            res?.countriesData?.map { it.toEntity() }?.let {
                                userDao.insertCountriesData(it)
                            }

                            res?.globalData?.toEntity()?.let { userDao.insertGlobalData(it) }
                        }
                    }
                })
            }
        }, 120 * 1000)
    }

}