package com.khatawat.diyn.ui.debts

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.khatawat.diyn.R
import com.khatawat.diyn.data.Debt
import com.khatawat.diyn.data.DebtDirection
import com.khatawat.diyn.databinding.ItemDebtBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DebtsAdapter(
    private val onClick: (Debt) -> Unit,
    private val onToggle: (Debt) -> Unit,
    private val onSendSms: (Debt) -> Unit
) : ListAdapter<Debt, DebtsAdapter.VH>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemDebtBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

    inner class VH(private val b: ItemDebtBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(d: Debt) {
            val ctx = b.root.context
            b.tvPerson.text = d.personName
            b.tvAmount.text = "%.2f".format(Locale.getDefault(), d.amount)
            b.tvDescription.text = if (d.description.isBlank()) "—" else d.description
            b.tvDue.text = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(d.dueDate))
            b.chipDirection.text = when (d.direction) {
                DebtDirection.OWED_TO_ME -> ctx.getString(R.string.dir_owed_to_me)
                DebtDirection.I_OWE -> ctx.getString(R.string.dir_i_owe)
            }
            b.chipDirection.setIconResource(
                when (d.direction) {
                    DebtDirection.OWED_TO_ME -> R.drawable.ic_arrow_down
                    DebtDirection.I_OWE -> R.drawable.ic_arrow_up
                }
            )
            b.tvAmount.setTextColor(
                ctx.getColor(
                    when (d.direction) {
                        DebtDirection.OWED_TO_ME -> R.color.amount_in
                        DebtDirection.I_OWE -> R.color.amount_out
                    }
                )
            )
            b.checkboxPaid.setOnCheckedChangeListener(null)
            b.checkboxPaid.isChecked = d.isPaid
            b.checkboxPaid.setOnCheckedChangeListener { _, _ -> onToggle(d) }
            b.root.alpha = if (d.isPaid) 0.55f else 1f
            b.tvPerson.paintFlags = if (d.isPaid)
                b.tvPerson.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            else
                b.tvPerson.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            b.root.setOnClickListener { onClick(d) }
            b.btnSendSms.visibility = if (d.phone.isNotBlank()) ViewGroup.VISIBLE else ViewGroup.GONE
            b.btnSendSms.setOnClickListener { onSendSms(d) }
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Debt>() {
            override fun areItemsTheSame(a: Debt, b: Debt) = a.id == b.id
            override fun areContentsTheSame(a: Debt, b: Debt) = a == b
        }
    }
}
