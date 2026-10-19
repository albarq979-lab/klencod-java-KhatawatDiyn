package com.khatawat.diyn.ui.notes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.khatawat.diyn.databinding.FragmentNoteEditBinding
import com.khatawat.diyn.repository.KhatawatRepository
import kotlinx.coroutines.launch

class NoteEditFragment : Fragment() {
    private var _b: FragmentNoteEditBinding? = null
    private val b get() = _b!!
    private val vm: NoteEditViewModel by viewModels()
    private var noteId: Long = 0L

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _b = FragmentNoteEditBinding.inflate(inflater, container, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        noteId = arguments?.getLong("noteId") ?: 0L

        if (noteId != 0L) {
            vm.load(noteId)
            vm.currentNote.observe(viewLifecycleOwner) { n ->
                if (n != null) {
                    b.editTitle.setText(n.title)
                    b.editBody.setText(n.body)
                }
            }
            b.btnDelete.visibility = View.VISIBLE
        }

        b.btnSave.setOnClickListener {
            val title = b.editTitle.text.toString().trim()
            if (title.isEmpty()) {
                b.editTitle.error = "العنوان مطلوب"
                return@setOnClickListener
            }
            val body = b.editBody.text.toString().trim()
            vm.save(noteId, title, body)
            findNavController().navigateUp()
        }
        b.btnCancel.setOnClickListener { findNavController().navigateUp() }

        b.btnDelete.setOnClickListener {
            val existing = vm.currentNote.value ?: return@setOnClickListener
            val repo = KhatawatRepository(requireActivity().application)
            viewLifecycleOwner.lifecycleScope.launch {
                repo.deleteNote(existing)
                findNavController().navigateUp()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView(); _b = null
    }
}
