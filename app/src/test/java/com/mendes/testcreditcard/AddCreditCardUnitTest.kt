package com.mendes.testcreditcard

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.mendes.testcreditcard.validator.ICardValidator
import com.mendes.testcreditcard.viewmodel.AddCreditCardViewModel
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import io.mockk.verify
import junit.framework.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner.Silent::class)
class AddCreditCardUnitTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @SpyK(recordPrivateCalls = true)
    private var viewModel = AddCreditCardViewModel()

    @MockK
    lateinit var cardValidator: ICardValidator

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)
    }

    @Test
    fun testValidCreditCardNumber_validateCreditCardNumber() {
        every { cardValidator.validateCardNumber(any()) } returns true
        viewModel.validateCreditCardNumber("5456 7890 1234 5670")
        viewModel.isCreditCardNumberValid.value?.let { assertTrue(it) }
        assertTrue(viewModel.isValidCreditCardNumber)
    }

    @Test
    fun testEmptyCreditCardNumber_validateCreditCardNumber() {
        every { cardValidator.validateCardNumber(any()) } returns true
        viewModel.validateCreditCardNumber("")
        viewModel.isCreditCardNumberValid.value?.let { assertFalse(it) }
        assertFalse(viewModel.isValidCreditCardNumber)
    }

    @Test
    fun testNullCreditCardNumber_validateCreditCardNumber() {
        every { cardValidator.validateCardNumber(any()) } returns true
        viewModel.validateCreditCardNumber(null)
        viewModel.isCreditCardNumberValid.value?.let { assertFalse(it) }
        assertFalse(viewModel.isValidCreditCardNumber)
    }

    @Test
    fun testInvalidCreditCardNumber_validateCreditCardNumber() {
        every { cardValidator.validateCardNumber(any()) } returns false
        viewModel.validateCreditCardNumber("0000 5588 7784 3546")
        viewModel.isCreditCardNumberValid.value?.let { assertFalse(it) }
        assertFalse(viewModel.isValidCreditCardNumber)
    }

    @Test
    fun testValidCVVNumber_validateCreditCardNumber() {
        every { cardValidator.validateCardNumber(any()) } returns true
        viewModel.validateCreditCardCVV("545", "5456 7890 1234 5670")
        viewModel.isCVVValid.value?.let { assertTrue(it) }
        assertEquals(AddCreditCardViewModel.ValidationResultOfOptionalInputText.VALID, viewModel.isValidCVV)
        viewModel.isCreditCardNumberValid.value?.let { assertTrue(it) }
        assertTrue(viewModel.isValidCreditCardNumber)
    }

    @Test
    fun testValidCVVNumberWithInvalidCreditCardNumber() {
        every { cardValidator.validateCVV(any(), any()) } returns false
        viewModel.validateCreditCardCVV("545", "")
        viewModel.isCVVValid.value?.let { assertFalse(it) }
        assertEquals(AddCreditCardViewModel.ValidationResultOfOptionalInputText.INVALID, viewModel.isValidCVV)
        viewModel.isCreditCardNumberValid.value?.let { assertFalse(it) }
        assertFalse(viewModel.isValidCreditCardNumber)
    }

    @Test
    fun testEmptyCVVNumber_validateCreditCardCVV() {
        every { cardValidator.validateCVV(any(), any()) } returns true
        viewModel.validateCreditCardCVV("", "5456 7890 1234 5670")
        assertEquals(AddCreditCardViewModel.ValidationResultOfOptionalInputText.EMPTY, viewModel.isValidCVV)
        viewModel.isCVVValid.value?.let { assertTrue(it) }
    }

    @Test
    fun testNullCVVNumber_validateCreditCardCVV() {
        every { cardValidator.validateCVV(any(), any()) } returns true
        viewModel.validateCreditCardCVV(null,"5456 7890 1234 5670")
        assertEquals(AddCreditCardViewModel.ValidationResultOfOptionalInputText.EMPTY, viewModel.isValidCVV)
        viewModel.isCVVValid.value?.let { assertTrue(it) }
    }

    // TODO: fix validation for 000 CVV
//    @Test
//    fun testInvalidCVVNumber_validateCreditCardCVV() {
//        every { cardValidator.validateCVV(any(), any()) } returns false
//        viewModel.validateCreditCardCVV("000","5456 7890 1234 5670")
//        assertEquals(AddCreditCardViewModel.ValidationResultOfOptionalInputText.INVALID, viewModel.isValidCVV)
//        viewModel.isCVVValid.value?.let { assertFalse(it) }
//    }

    @Test
    fun testValidExpiryDate_validateCreditCardExpiryDate() {
        every { cardValidator.validateExpiryDate(any(), any()) } returns true
        viewModel.validateCreditCardExpiryDate("12/32")
        viewModel.isExpiryDateValid.value?.let { assertTrue(it) }
        assertTrue(viewModel.isValidExpiryDate)
    }

    @Test
    fun testEmptyExpiryDate_validateCreditCardExpiryDate() {
        every { cardValidator.validateExpiryDate(any(), any()) } returns true
        viewModel.validateCreditCardExpiryDate("")
        viewModel.isExpiryDateValid.value?.let { assertFalse(it) }
        assertFalse(viewModel.isValidExpiryDate)
    }

    @Test
    fun testWrongFormatExpiryDate_validateCreditCardExpiryDate() {
        every { cardValidator.validateExpiryDate(any(), any()) } returns true
        viewModel.validateCreditCardExpiryDate("1232")
        viewModel.isExpiryDateValid.value?.let { assertFalse(it) }
        assertFalse(viewModel.isValidExpiryDate)
    }

    @Test
    fun testNullExpiryDate_validateCreditCardExpiryDate() {
        every { cardValidator.validateExpiryDate(any(), any()) } returns true
        viewModel.validateCreditCardExpiryDate(null)
        viewModel.isExpiryDateValid.value?.let { assertFalse(it) }
        assertFalse(viewModel.isValidExpiryDate)
    }

    @Test
    fun testInvalidExpiryDate_validateCreditCardExpiryDate() {
        every { cardValidator.validateCVV(any(), any()) } returns false
        viewModel.validateCreditCardExpiryDate("34/11")
        viewModel.isExpiryDateValid.value?.let { assertFalse(it) }
        assertFalse(viewModel.isValidExpiryDate)
    }

    @Test
    fun testEmptyCVVWithNoReasonCVVNotProvided_isCVVInfoValid() {
        viewModel.isNoCVVReasonProvided = false
        viewModel.isValidCVV = AddCreditCardViewModel.ValidationResultOfOptionalInputText.EMPTY
        assertFalse(viewModel.isCVVInfoValid())
    }

    @Test
    fun testEmptyCVVWithNoReasonCVVProvided_isCVVInfoValid() {
        viewModel.isNoCVVReasonProvided = true
        viewModel.isValidCVV = AddCreditCardViewModel.ValidationResultOfOptionalInputText.EMPTY
        assertTrue(viewModel.isCVVInfoValid())
    }

    @Test
    fun testValidCVVWithNoReasonCVVNotProvided_isCVVInfoValid() {
        viewModel.isNoCVVReasonProvided = false
        viewModel.isValidCVV = AddCreditCardViewModel.ValidationResultOfOptionalInputText.VALID
        assertTrue(viewModel.isCVVInfoValid())
    }

    @Test
    fun testInvalidCVVWithNoReasonCVVProvided_isCVVInfoValid() {
        viewModel.isNoCVVReasonProvided = true
        viewModel.isValidCVV = AddCreditCardViewModel.ValidationResultOfOptionalInputText.INVALID
        assertFalse(viewModel.isCVVInfoValid())
    }

    @Test
    fun testSingleMotoType_onMotoTypeSelected() {
        viewModel.onMotoTypeSelected(0)
        assertEquals(AddCreditCardViewModel.MotoType.Single, viewModel.selectedMotoType)
        viewModel.isStoreCredentialsOptionRequired.value?.let { assertFalse(it) }
        assertFalse(viewModel.isCardStoredOnFileSelected)
        verify { viewModel.updateContinueButtonState() }
    }

    @Test
    fun testRecurringMotoType_onMotoTypeSelected() {
        viewModel.onMotoTypeSelected(1)
        assertEquals(AddCreditCardViewModel.MotoType.Recurring, viewModel.selectedMotoType)
        viewModel.isStoreCredentialsOptionRequired.value?.let { assertTrue(it) }
        verify { viewModel.updateContinueButtonState() }
    }

    @Test
    fun testSingleMotoType_isMotoTypeInfoValid() {
        viewModel.selectedMotoType = AddCreditCardViewModel.MotoType.Single
        assertTrue(viewModel.isMotoTypeInfoValid())
    }

    @Test
    fun testRecurringMotoTypeWithStorageSelected_isMotoTypeInfoValid() {
        viewModel.selectedMotoType = AddCreditCardViewModel.MotoType.Recurring
        viewModel.isCardStoredOnFileSelected = true
        assertTrue(viewModel.isMotoTypeInfoValid())
    }

    @Test
    fun testRecurringMotoTypeWithoutStorageSelected_isMotoTypeInfoValid() {
        viewModel.selectedMotoType = AddCreditCardViewModel.MotoType.Recurring
        viewModel.isCardStoredOnFileSelected = false
        assertFalse(viewModel.isMotoTypeInfoValid())
    }
}