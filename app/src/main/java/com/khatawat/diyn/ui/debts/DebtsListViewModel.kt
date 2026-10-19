package com.khatawat.diyn.ui.debts

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.khatawat.diyn.data.Debt
import com.khatawat.diyn.data.DebtDirection
import com.khatawat.diyn.repository.KhatawatRepository
import kotlinx.coroutines.launch

data class DebtSummary(val owedToMeTotal: Double, val iOweTotal: Double)

class DebtsListViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = KhatawatRepository(app)
    val debts: LiveData<List<Debt>> = repo.observeDebts()
    val summary = MutableLiveData(DebtSummary(0.0, 0.0))

    init {
        debts.observeForever { list ->
            var owed = 0.0; var owes = 0.0
            list?.forEach { d ->
                if (!d.isPaid) {
                    if (d.direction == DebtDirection.OWED_TO_ME) owed += d.amount else owes += d.amount
                }
            }
            summary.value = DebtSummary(owed, owes)
        }
    }

    fun togglePaid(debt: Debt) = viewModelScope.launch {
        repo.updateDebt(debt.copy(isPaid = !debt.isPaid))
    }

    fun delete(debt: Debt) = viewModelScope.launch {
        repo.deleteDebt(debt)
    }
}
