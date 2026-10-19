package com.khatawat.diyn.ui.debts

import android.Manifest
import android.app.DatePickerDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.khatawat.diyn.R
import com.khatawat.diyn.data.DebtDirection
import com.khatawat.diyn.databinding.FragmentDebtEditBinding
import com.khatawat.diyn.notification.DebtReminderWorker
import com.khatawat.diyn.repository.KhatawatRepository
import com.khatawat.diyn.sms.SmsHelper
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DebtEditFragment : Fragment() {
    private var _b: FragmentDebtEditBinding? = null
    private val b get() = _b!!
    private val vm: DebtEditViewModel by viewModels()
    private var debtId: Long = 0L
    private var dueDate: Long = defaultDueDate()
    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    private val requestSms =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (!granted) Toast.makeText(
                requireContext(),
                "لم يتم منح إذن الرسائل — سيتم فتح تطبيق الرسائل بدلاً من ذلك",
                Toast.LENGTH_LONG
            ).show()
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _b = FragmentDebtEditBinding.inflate(inflater, container, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        debtId = arguments?.getLong("debtId") ?: 0L
        b.tvDue.text = sdf.format(dueDate)
        b.btnPickDate.setOnClickListener { pickDate() }

        if (debtId != 0L) {
            vm.load(debtId)
            vm.currentDebt.observe(viewLifecycleOwner) { d -> if (d != null) bind(d) }
        }

        b.btnSave.setOnClickListener { saveAndExit() }
        b.btnCancel.setOnClickListener { findNavController().navigateUp() }

        b.btnDelete.visibility = if (debtId == 0L) View.GONE else View.VISIBLE
        b.btnDelete.setOnClickListener { deleteAndExit() }

        b.btnSendSms.setOnClickListener { sendSmsToDebtor() }
    }

    private fun bind(d: com.khatawat.diyn.data.Debt) {
        b.editPerson.setText(d.personName)
        b.editPhone.setText(d.phone)
        b.editAmount.setText(d.amount.toString())
        b.editDescription.setText(d.description)
        dueDate = d.dueDate
        b.tvDue.text = sdf.format(dueDate)
        b.chipOwedToMe.isChecked = d.direction == DebtDirection.OWED_TO_ME
        b.chipIOwe.isChecked = d.direction == DebtDirection.I_OWE
        b.checkboxPaid.isChecked = d.isPaid
    }

    private fun pickDate() {
        val cal = Calendar.getInstance().apply { timeInMillis = dueDate }
        DatePickerDialog(
            requireContext(),
            { _, y, m, day ->
                cal.set(y, m, day, 9, 0, 0)
                dueDate = cal.timeInMillis
                b.tvDue.text = sdf.format(dueDate)
            },
            cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun saveAndExit() {
        val name = b.editPerson.text.toString().trim()
        if (name.isEmpty()) { b.editPerson.error = "الاسم مطلوب"; return }
        val phone = b.editPhone.text.toString().trim()
        val amount = b.editAmount.text.toString().trim().toDoubleOrNull() ?: 0.0
        if (amount <= 0.0) { b.editAmount.error = "قيمة موجبة"; return }
        val desc = b.editDescription.text.toString().trim()
        val direction = if (b.chipOwedToMe.isChecked) DebtDirection.OWED_TO_ME else DebtDirection.I_OWE

        vm.save(
            id = debtId,
            personName = name,
            phone = phone,
            amount = amount,
            description = desc,
            direction = direction,
            dueDate = dueDate,
            isPaid = b.checkboxPaid.isChecked,
            onSaved = { id ->
                DebtReminderWorker.schedule(requireContext(), id, dueDate)
                requireActivity().runOnUiThread { findNavController().navigateUp() }
            }
        )
    }

    private fun deleteAndExit() {
        val d = vm.currentDebt.value ?: return
        val repo = KhatawatRepository(requireActivity().application)
        viewLifecycleOwner.lifecycleScope.launch {
            repo.deleteDebt(d)
            findNavController().navigateUp()
        }
    }

    private fun sendSmsToDebtor() {
        val phone = b.editPhone.text.toString().trim()
        if (phone.isEmpty()) { b.editPhone.error = "أدخل رقم الهاتف أولاً"; return }
        val name = b.editPerson.text.toString().trim().ifEmpty { "صديقي" }
        val amount = b.editAmount.text.toString().trim().ifEmpty { "0" }
        val desc = b.editDescription.text.toString().trim()
        val msg = getString(R.string.sms_template_debt_reminder, name, amount, desc)

        val granted = ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.SEND_SMS
        ) == PackageManager.PERMISSION_GRANTED

        if (granted) {
            val ok = SmsHelper.sendDirect(requireContext(), phone, msg)
            if (ok) Toast.makeText(requireContext(), "تم إرسال التذكير", Toast.LENGTH_SHORT).show()
        } else {
            requestSms.launch(Manifest.permission.SEND_SMS)
            SmsHelper.openSmsApp(requireContext(), phone, msg)
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _b = null }

    companion object {
        private fun defaultDueDate(): Long {
            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_YEAR, 7)
            cal.set(Calendar.HOUR_OF_DAY, 9)
            cal.set(Calendar.MINUTE, 0)
            return cal.timeInMillis
        }
    }
}
