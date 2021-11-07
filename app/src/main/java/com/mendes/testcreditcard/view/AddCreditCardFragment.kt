package com.mendes.testcreditcard.view

import android.os.Bundle
import android.text.InputType
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.mendes.testcreditcard.R
import com.mendes.testcreditcard.extensions.setMaxLength
import com.mendes.testcreditcard.viewmodel.AddCreditCardViewModel

class AddCreditCardFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private lateinit var viewModel: AddCreditCardViewModel

    private lateinit var inputLayoutCreditCardNumber: TextInputLayout
    private lateinit var inputLayoutCreditCardExpiryDate: TextInputLayout
    private lateinit var inputLayoutCreditCardCVV: TextInputLayout
    private lateinit var radioGroupNoReasonCVV: RadioGroup
    private lateinit var textViewCardStoredLabel: AppCompatTextView
    private lateinit var radioGroupCardStoredOnFile: RadioGroup
    private lateinit var btnContinue: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AddCreditCardViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_credit_card, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initObservers()
    }

    private fun initViews() {
        initAllViews()
        setupAmountView()
        setupCreditCardNumberView()
        setupCreditCardExpiryDateView()
        setupCreditCardCVVView()
        setupNoReasonCVVView()
        setupMotoTypeMenuView()
        setupCardStoredView()
        setupContinueButton()
    }

    private fun initAllViews() {
        inputLayoutCreditCardNumber = requireView().findViewById(R.id.inputLayoutCreditCardNumber)
        inputLayoutCreditCardExpiryDate = requireView().findViewById(R.id.inputLayoutCreditCardExpiryDate)
        inputLayoutCreditCardCVV = requireView().findViewById(R.id.inputLayoutCreditCardCVV)
        radioGroupNoReasonCVV = requireView().findViewById(R.id.radioGroupNoReasonCVV)
        textViewCardStoredLabel = requireView().findViewById(R.id.textViewCardStoredLabel)
        radioGroupCardStoredOnFile = requireView().findViewById(R.id.radioGroupCardStoredOnFile)
        btnContinue = requireView().findViewById(R.id.btnContinue)
    }

    private fun initObservers() {
        viewModel.isCreditCardNumberValid.observe(viewLifecycleOwner, { isValid ->
            inputValidated(inputLayoutCreditCardNumber, isValid, getString(R.string.credit_card_error))
        })

        viewModel.isExpiryDateValid.observe(viewLifecycleOwner, { isValid ->
            inputValidated(inputLayoutCreditCardExpiryDate, isValid, getString(R.string.expiry_date_error))
        })

        viewModel.isCVVValid.observe(viewLifecycleOwner, { isValid ->
            inputValidated(inputLayoutCreditCardCVV, isValid, getString(R.string.cvv_error))
            viewModel.onCVVValidated()
        })

        viewModel.isNoReasonCVVOptionRequired.observe(viewLifecycleOwner, { isRequired ->
            updateNoReasonCVVFieldOnInfoRequiredChange(isRequired)
        })

        viewModel.isStoreCredentialsOptionRequired.observe(viewLifecycleOwner, { isRequired ->
            updateCardStoredFieldOnInfoRequiredChange(isRequired)
        })

        viewModel.enableContinueButton.observe(viewLifecycleOwner, { enable ->
            btnContinue.isEnabled = enable
        })
    }

    private fun setupAmountView() {
        val textViewAmountLabel = requireView().findViewById<AppCompatTextView>(R.id.textViewAmountLabel)
        val amountValue = getString(R.string.amount_value)
        textViewAmountLabel.text = String.format(amountValue, viewModel.getFormattedAmount())
    }

    private fun setupCreditCardNumberView() {
        inputLayoutCreditCardNumber.hint = getString(R.string.credit_card_hint)

        val editTextCreditCardNumber =
            inputLayoutCreditCardNumber.findViewById<TextInputEditText>(R.id.editTextCreditCardNumber)

        editTextCreditCardNumber.apply {
            setMaxLength(19)
            inputType = InputType.TYPE_CLASS_DATETIME
            imeOptions = EditorInfo.IME_ACTION_NEXT
            setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) removeErrorState(inputLayoutCreditCardNumber)
                else viewModel.validateCreditCardNumber(editTextCreditCardNumber.text.toString())
            }
        }
    }

    private fun setupCreditCardExpiryDateView() {
        inputLayoutCreditCardExpiryDate.hint = getString(R.string.expiry_date_hint)

        val editTextCreditCardExpiryDate =
            inputLayoutCreditCardExpiryDate.findViewById<TextInputEditText>(R.id.editTextCreditCardExpiryDate)

        editTextCreditCardExpiryDate.apply {
            setMaxLength(5)
            inputType = InputType.TYPE_CLASS_DATETIME
            imeOptions = EditorInfo.IME_ACTION_NEXT
            setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) removeErrorState(inputLayoutCreditCardExpiryDate)
                else viewModel.validateCreditCardExpiryDate(editTextCreditCardExpiryDate.text.toString())
            }
        }
    }

    private fun setupCreditCardCVVView() {
        inputLayoutCreditCardCVV.hint = getString(R.string.cvv_hint)

        val editTextCreditCardCVV =
            inputLayoutCreditCardCVV.findViewById<TextInputEditText>(R.id.editTextCreditCardCVV)

        editTextCreditCardCVV.apply {
            setMaxLength(3)
            inputType = InputType.TYPE_CLASS_NUMBER
            imeOptions = EditorInfo.IME_ACTION_DONE
            setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) removeErrorState(inputLayoutCreditCardCVV)
                else viewModel.validateCreditCardCVV(
                    editTextCreditCardCVV.text.toString(),
                    inputLayoutCreditCardNumber.editText?.text.toString())
            }
            setOnEditorActionListener { _: TextView?, actionId: Int, _: KeyEvent? ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    editTextCreditCardCVV.clearFocus()
                }
                false
            }
        }
    }

    private fun setupNoReasonCVVView() {
        radioGroupNoReasonCVV.setOnCheckedChangeListener { _: RadioGroup, checkedId: Int ->
            when (checkedId) {
                R.id.radioButtonNoCVVOnCard,
                R.id.radioButtonNoCardPresent,
                R.id.radioButtonUnableToRead -> {
                    viewModel.onNoReasonCVVOptionSelected(true)
                }
            }
        }
    }

    private fun setupMotoTypeMenuView() {
        val spinner: Spinner = requireView().findViewById(R.id.spinnerMenuMotoType)
        spinner.onItemSelectedListener = this

        val items = listOf(
            getString(R.string.select_moto_type_option_single),
            getString(R.string.select_moto_type_option_recurring))
        val adapter = ArrayAdapter(requireContext(), R.layout.menu_type_list_item, items)
        spinner.adapter = adapter
    }

    private fun setupCardStoredView() {
        radioGroupCardStoredOnFile.setOnCheckedChangeListener { _: RadioGroup, checkedId: Int ->
            when (checkedId) {
                R.id.radioButtonCardStoredYes,
                R.id.radioButtonCardStoredNo -> {
                    viewModel.onCardStoredOnFileOptionSelected(true)
                }
            }
        }
    }

    private fun setupContinueButton() {
        btnContinue.setOnClickListener {
            Toast.makeText(
                requireContext(),
                getString(R.string.information_received),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun inputValidated(
        textInputLayout: TextInputLayout,
        isValid: Boolean,
        errorMessage: String
    ) {
        if (!isValid) { textInputLayout.error = errorMessage }
        textInputLayout.isErrorEnabled = !isValid
        viewModel.updateContinueButtonState()
    }

    private fun removeErrorState(textInputLayout: TextInputLayout) {
        textInputLayout.isErrorEnabled = false
    }

    private fun updateCardStoredFieldOnInfoRequiredChange(isRequired: Boolean) {
        if (isRequired) {
            textViewCardStoredLabel.visibility = View.VISIBLE
            radioGroupCardStoredOnFile.visibility = View.VISIBLE
        } else {
            textViewCardStoredLabel.visibility = View.GONE
            radioGroupCardStoredOnFile.visibility = View.GONE
            radioGroupCardStoredOnFile.clearCheck()
        }
    }

    private fun updateNoReasonCVVFieldOnInfoRequiredChange(isRequired: Boolean) {
        for (i in 0 until radioGroupNoReasonCVV.childCount) {
            (radioGroupNoReasonCVV.getChildAt(i) as RadioButton).isEnabled = isRequired
        }

        if (!isRequired) {
            radioGroupNoReasonCVV.clearCheck()
            viewModel.onNoReasonCVVOptionSelected(false)
        }
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        viewModel.onMotoTypeSelected(pos)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {  }
}
