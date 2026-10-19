package com.khatawat.diyn.ui.notes

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

class NotesListFragment : Fragment() {
    private var _b: FragmentListBinding? = null
    private val b get() = _b!!
    private val vm: NotesListViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _b = FragmentListBinding.inflate(inflater, container, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        b.toolbar.title = getString(R.string.notes_title)
        val adapter = NotesAdapter { note ->
            val bundle = Bundle().apply { putLong("noteId", note.id) }
            findNavController().navigate(R.id.action_notes_to_edit, bundle)
        }
        b.recycler.layoutManager = LinearLayoutManager(requireContext())
        b.recycler.adapter = adapter
        vm.notes.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
            b.empty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }
        b.fab.setOnClickListener {
            val bundle = Bundle().apply { putLong("noteId", 0L) }
            findNavController().navigate(R.id.action_notes_to_edit, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView(); _b = null
    }
}
