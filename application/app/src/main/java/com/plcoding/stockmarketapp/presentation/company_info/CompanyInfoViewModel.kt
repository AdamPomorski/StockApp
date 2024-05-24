package com.plcoding.stockmarketapp.presentation.company_info

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.stockmarketapp.domain.repository.StockRepository
import com.plcoding.stockmarketapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompanyInfoViewModel@Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: StockRepository
): ViewModel() {

    var state by mutableStateOf(CompanyInfoState())

    init {
        viewModelScope.launch {
            val symbol = savedStateHandle.get<String>("symbol") ?: return@launch
            state = state.copy(isLoading = true)
            val companyInfoResult = async {  repository.getCompanyInfo(symbol)}
            async {  repository.getMonthlyData(symbol).collect{ result ->
                 when(result){
                     is Resource.Success ->{
                         state = state.copy(
                             stockInfos = result.data ?: emptyList(),
                             error = null
                         )
                     }
                     is Resource.Error ->{
                         state = state.copy(
                             stockInfos = emptyList(),
                             error = result.message
                         )

                     }
                     is Resource.Loading ->{
                         state = state.copy(isLoading = result.isLoading)
                     }
                 }

             } }
            when(val result = companyInfoResult.await()){
                is Resource.Success ->{
                        state = state.copy(
                            company = result.data,
                            isLoading = false,
                            error = null
                        )
                }
                is Resource.Error ->{
                    state = state.copy(
                        company = null,
                        isLoading = false,
                        error = result.message
                    )

                }
                else ->Unit
            }



        }
    }
}