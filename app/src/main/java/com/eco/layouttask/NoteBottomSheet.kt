package com.eco.layouttask

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.eco.layouttask.databinding.FragmentNoteBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.time.LocalDate


class NoteBottomSheet(
    private val date: LocalDate,
    private val onNoteSaved: (LocalDate, String) -> Unit
) : BottomSheetDialogFragment() {
    private var _binding: FragmentNoteBottomSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNoteBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.noteTitle.text = "Note for ${date}"

        binding.saveButton.setOnClickListener {
            val content = binding.noteEditText.text.toString()
            if (content.isNotBlank()) {
                onNoteSaved(date, content)
                dismiss()
            } else {
                Toast.makeText(requireContext(), "Note cannot be empty!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}