package com.mendes.testcreditcard.viewmodel

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mendes.testcreditcard.validator.CardValidatorImpl
import com.mendes.testcreditcard.validator.ICardValidator
import java.math.BigDecimal
import java.text.DecimalFormat

class AddCreditCardViewModel : ViewModel(), ICardValidator by CardValidatorImpl() {

    enum class ValidationResultOfOptionalInputText {
        EMPTY,
        VALID,
        INVALID
    }

    enum class MotoType(val value: Int) {
        Single(0),
        Recurring(1);

        companion object {
            fun fromInt(value: Int) = values().first { it.value == value }
        }
    }

    var isCreditCardNumberValid = MutableLiveData<Boolean>()
    var isExpiryDateValid = MutableLiveData<Boolean>()
    var isCVVValid = MutableLiveData<Boolean>()
    var isNoReasonCVVOptionRequired = MutableLiveData<Boolean>()
    var isStoreCredentialsOptionRequired = MutableLiveData<Boolean>()
    var enableContinueButton = MutableLiveData<Boolean>()

    @VisibleForTesting
    var isValidCreditCardNumber = false
    @VisibleForTesting
    var isValidExpiryDate = false
    @VisibleForTesting
    var isValidCVV = ValidationResultOfOptionalInputText.EMPTY
    @VisibleForTesting
    var isNoCVVReasonProvided = false
    @VisibleForTesting
    var selectedMotoType = MotoType.Single
    @VisibleForTesting
    var isCardStoredOnFileSelected = false

    fun validateCreditCardNumber(text: String?) {
        if (text.isNullOrEmpty()) {
            isValidCreditCardNumber = false
            isCreditCardNumberValid.value = false
            return
        }

        isValidCreditCardNumber = validateCardNumber(text)
        isCreditCardNumberValid.value = isValidCreditCardNumber
    }

    fun validateCreditCardExpiryDate(text: String?) {
        if (text.isNullOrEmpty() || !text.contains(EXPIRY_DATE_SEPARATOR)) {
            isValidExpiryDate = false
            isExpiryDateValid.value = false
            return
        }

        val month = text.substringBefore(EXPIRY_DATE_SEPARATOR)
        val year = text.substringAfter(EXPIRY_DATE_SEPARATOR)

        isValidExpiryDate = validateExpiryDate(month, year)
        isExpiryDateValid.value = isValidExpiryDate
    }

    fun validateCreditCardCVV(cvv: String?, cardNumber: String?) {
        // Added extra credit card number validation because the CVV is validated against card number;
        // TODO: A proper validation handling is required to cover alternative scenarios such as
        //  a change of the credit card number once CVV was validated
        validateCreditCardNumber(cardNumber)

        if (cvv.isNullOrEmpty()) {
            isValidCVV = ValidationResultOfOptionalInputText.EMPTY
            isCVVValid.value = true
            return
        }

        val cvvIsValid = cardNumber?.let { validateCVV(cvv, it) } == true
        isValidCVV = if (cvvIsValid) {
            ValidationResultOfOptionalInputText.VALID
        } else {
            ValidationResultOfOptionalInputText.INVALID
        }

        isCVVValid.value = cvvIsValid
    }

    fun onCVVValidated() {
        isNoReasonCVVOptionRequired.value = isValidCVV == ValidationResultOfOptionalInputText.EMPTY
    }

    @VisibleForTesting
    fun isCVVInfoValid(): Boolean {
        return isValidCVV == ValidationResultOfOptionalInputText.VALID ||
                (isValidCVV == ValidationResultOfOptionalInputText.EMPTY && isNoCVVReasonProvided)
    }

    fun onNoReasonCVVOptionSelected(isSelected: Boolean) {
        isNoCVVReasonProvided = isSelected
        updateContinueButtonState()
    }

    fun onMotoTypeSelected(pos: Int) {
        selectedMotoType = MotoType.fromInt(pos)
        when (selectedMotoType) {
            MotoType.Single -> {
                isStoreCredentialsOptionRequired.value = false
                isCardStoredOnFileSelected = false
            }
            MotoType.Recurring -> isStoreCredentialsOptionRequired.value = true
        }

        updateContinueButtonState()
    }

    @VisibleForTesting
    fun isMotoTypeInfoValid(): Boolean {
        if (selectedMotoType == MotoType.Single) {
            return true
        }

        if (selectedMotoType == MotoType.Recurring) {
            return isCardStoredOnFileSelected
        }

        return false
    }

    fun onCardStoredOnFileOptionSelected(isSelected: Boolean) {
        isCardStoredOnFileSelected = isSelected
        updateContinueButtonState()
    }

    fun getFormattedAmount(): String {
        val amountUnformatted = BigDecimal(234567.89)

        return try {
            val dec = DecimalFormat("#,###.00")
            dec.format(amountUnformatted)
        } catch (ex: Exception) {
            // TODO: handle amount error
            amountUnformatted.toPlainString()
        }
    }

    fun updateContinueButtonState() {
        enableContinueButton.value =
            isValidCreditCardNumber && isValidExpiryDate && isCVVInfoValid() && isMotoTypeInfoValid()
    }

    companion object {
        private const val EXPIRY_DATE_SEPARATOR = "/"
    }
}