package com.khatawat.diyn.ui.debts

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.khatawat.diyn.data.Debt
import com.khatawat.diyn.data.DebtDirection
import com.khatawat.diyn.repository.KhatawatRepository
import kotlinx.coroutines.launch

class DebtEditViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = KhatawatRepository(app)
    val currentDebt = MutableLiveData<Debt?>(null)

    fun load(id: Long) = viewModelScope.launch {
        if (id == 0L) { currentDebt.value = null; return@launch }
        currentDebt.value = repo.getDebt(id)
    }

    fun save(
        id: Long,
        personName: String,
        phone: String,
        amount: Double,
        description: String,
        direction: DebtDirection,
        dueDate: Long,
        isPaid: Boolean,
        onSaved: (Long) -> Unit
    ) = viewModelScope.launch {
        val debt = Debt(
            id = id,
            personName = personName,
            phone = phone,
            amount = amount,
            description = description,
            direction = direction,
            dueDate = dueDate,
            isPaid = isPaid
        )
        val newId = repo.addDebt(debt)
        onSaved(if (id == 0L) newId else id)
    }
}
