package com.khatawat.diyn.ui.debts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.khatawat.diyn.R
import com.khatawat.diyn.databinding.FragmentListBinding
import com.khatawat.diyn.sms.SmsHelper

class DebtsListFragment : Fragment() {
    private var _b: FragmentListBinding? = null
    private val b get() = _b!!
    private val vm: DebtsListViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _b = FragmentListBinding.inflate(inflater, container, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        b.toolbar.title = getString(R.string.debts_title)

        val adapter = DebtsAdapter(
            onClick = { d ->
                val bundle = Bundle().apply { putLong("debtId", d.id) }
                findNavController().navigate(R.id.action_debts_to_edit, bundle)
            },
            onToggle = { d -> vm.togglePaid(d) },
            onSendSms = { d ->
                val msg = getString(
                    R.string.sms_template_debt_reminder,
                    d.amount.toString(),
                    d.description
                )
                SmsHelper.openSmsApp(requireContext(), d.phone, msg)
            }
        )
        b.recycler.layoutManager = LinearLayoutManager(requireContext())
        b.recycler.adapter = adapter
        vm.debts.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
            b.empty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }
        b.fab.setOnClickListener {
            val bundle = Bundle().apply { putLong("debtId", 0L) }
            findNavController().navigate(R.id.action_debts_to_edit, bundle)
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _b = null }
}
