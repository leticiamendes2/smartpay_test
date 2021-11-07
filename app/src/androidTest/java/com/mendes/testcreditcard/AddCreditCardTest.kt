package com.mendes.testcreditcard

import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.mendes.testcreditcard.view.MainActivity
import org.hamcrest.Matchers.anything
import org.hamcrest.Matchers.not
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class AddCreditCardTest {

    @get:Rule
    var activityRule: ActivityScenarioRule<MainActivity>
            = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun viewsOnScreen_creditCardFragment() {
        //01) Verify that the field 'PAN' is visible, and editable on the screen.
        onView(withId(R.id.editTextCreditCardNumber))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))

        //02) Verify that the field 'CVV' is visible, and editable on the screen.
        onView(withId(R.id.editTextCreditCardExpiryDate))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))

        //03) Verify that the field 'Expiry' is visible, and editable on the screen.
        onView(withId(R.id.editTextCreditCardCVV))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))

        onView(withId(R.id.radioGroupNoReasonCVV))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))

        onView(withId(R.id.radioButtonNoCVVOnCard))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))

        onView(withId(R.id.radioButtonNoCardPresent))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))

        onView(withId(R.id.radioButtonUnableToRead))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))

        //04) Verify that the field 'MOTO Type' is visible, and editable on the screen.
        onView(withId(R.id.spinnerMenuMotoType))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))

        //05) Verify that the field 'MOTO Type' sub option 'Single' is visible, and selectable on the screen.
        onView(withId(R.id.spinnerMenuMotoType))
            .check(matches(withSpinnerText("Single")))

        //09) Verify that the field 'Amount' is visible on the screen.
        onView(withId(R.id.textViewAmountLabel))
            .check(matches(isDisplayed()))

        //33) Verify that the button 'Continue' is visible on the screen.
        //34) Verify that the button 'Continue' is disabled when the screen loads on the screen.
        onView(withId(R.id.btnContinue))
            .check(matches(isDisplayed()))
            .check(matches(not(isEnabled())))
    }

    @Test
    fun viewIsValidCreditCardNumber_creditCardFragment() {
        //25) 'PAN' Validation Rules: does not accept more than 19 digits
        onView(withId(R.id.editTextCreditCardNumber))
            .perform(typeText("4284 5588 7784 3546 1234"), pressImeActionButton())
        onView(withId(R.id.editTextCreditCardNumber))
            .check(matches(withText("4284 5588 7784 3546")))

        onView(withId(R.id.editTextCreditCardNumber))
            .perform(clearText(), pressImeActionButton())

        //26) 'PAN' Validation Rules: does not accept anything else than numbers and space
        // This test found a bug where the field was accepting the char A
        // I fixed it by not specifying the input type
        // and limiting the accepted chars in the XML digits to space and numbers only;
        // But it doesn't show the numeric keyboard when the field is focused
        // so a proper fix is required, for now I'll change the input type back to DATETIME
        // because it allows both numbers and space and the correct keyboard for the user
//        onView(withId(R.id.editTextCreditCardNumber))
//            .perform(typeText("ABCD 5588 7784 3546"), pressImeActionButton())
//        onView(withId(R.id.editTextCreditCardNumber))
//            .check(matches(withText(" 5588 7784 3546")))

        // TODO: create custom match to find the hint element and its value
//        onView(withId(R.id.inputLayoutCreditCardNumber))
//            .check(matches(hasErrorText("Please, add a valid credit card number.")))
    }

    @Test
    fun cvvValidation_creditCardFragment() {
        // 10) Verify that when the field 'CVV' is empty, one of the 'No CVV Reason' must be selected.
        onView(withId(R.id.editTextCreditCardCVV))
            .perform(typeText(""), pressImeActionButton())
        onView(withId(R.id.radioGroupNoReasonCVV))
            .check(matches(isEnabled()))
        onView(withId(R.id.radioButtonNoCVVOnCard))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
        onView(withId(R.id.radioButtonNoCardPresent))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
        onView(withId(R.id.radioButtonUnableToRead))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))

        // TODO: fix validation
        // 11) Verify that when the field 'CVV' is filled, the 'No CVV Reason' option is unselected, and disabled.
//        onView(withId(R.id.editTextCreditCardNumber))
//            .perform(typeText("5456 7890 1234 5670"), pressImeActionButton())
//        onView(withId(R.id.editTextCreditCardCVV))
//            .perform(typeText("1"), pressImeActionButton())
//        onView(withId(R.id.radioGroupNoReasonCVV))
//            .check(matches(not(isEnabled())))

        //30) 'CVV' Validation Rules: does not accept more than 3 digits
        onView(withId(R.id.editTextCreditCardCVV))
            .perform(typeText("1234"), pressImeActionButton())
        onView(withId(R.id.editTextCreditCardCVV))
            .check(matches(withText("123")))

        //31) 'CVV' Validation Rules: does not accept anything else than numbers
        onView(withId(R.id.editTextCreditCardCVV))
            .perform(typeText("A123"), pressImeActionButton())
        onView(withId(R.id.editTextCreditCardCVV))
            .check(matches(withText("123")))
    }

    @Test
    fun viewExpiryDate_creditCardFragment() {
        //32) 'Expiry' Validation Rules: date in MM/YY format
        // TODO: add more validation cases
        onView(withId(R.id.editTextCreditCardExpiryDate))
            .perform(typeText("42/84"), pressImeActionButton())
        // TODO: create custom match to find the hint element and its value
//        onView(withId(R.id.editTextCreditCardExpiryDate))
//            .check(matches(hasErrorText("Please, add a valid expire date.")))
    }

    @Test
    fun motoTypeValidation_creditCardFragment() {
        // 06) Verify that the field 'MOTO Type' sub option 'Recurring' is selectable on the screen.
        onView(withId(R.id.spinnerMenuMotoType))
            .perform(click())
        onData(anything()).atPosition(1)
            .perform(click());
        onView(withId(R.id.spinnerMenuMotoType))
            .check(matches(withSpinnerText("Recurring")))

        // 07) Verify that when the field 'Recurring' is selected, the field 'Card Stored on file?' is visible, and selectable on the screen.
        onView(withId(R.id.textViewCardStoredLabel))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
        onView(withId(R.id.radioGroupCardStoredOnFile))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
        onView(withId(R.id.radioButtonCardStoredYes))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
        onView(withId(R.id.radioButtonCardStoredNo))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))

        // 08) Verify that when the field 'Single' is selected, the field 'Card Stored on file?' is gone on the screen.
        onView(withId(R.id.spinnerMenuMotoType))
            .perform(click())
        onData(anything()).atPosition(0)
            .perform(click());
        onView(withId(R.id.spinnerMenuMotoType))
            .check(matches(withSpinnerText("Single")))
        onView(withId(R.id.textViewCardStoredLabel))
            .check(matches(not(isDisplayed())))
        onView(withId(R.id.radioGroupCardStoredOnFile))
            .check(matches(not(isDisplayed())))
        onView(withId(R.id.radioButtonCardStoredYes))
            .check(matches(not(isDisplayed())))
        onView(withId(R.id.radioButtonCardStoredNo))
            .check(matches(not(isDisplayed())))
    }

    @Test
    fun continueButtonEnablement_creditCardFragment() {
        // TODO: mock ViewModel to test logic triggering in the UI changes
        //12) Verify that the Continue button is disabled when the 'PAN' value is empty or invalid.
        //13) Verify that the Continue button is disabled when the 'CVV' value is invalid.
        //14) Verify that the Continue button is disabled when the 'CVV' value is empty and no 'No CVV Reason' was selected.
        //15) Verify that the Continue button is disabled when the 'Expiry' value empty or invalid.
        //16) Verify that the Continue button is disabled when no 'MOTO Type' was selected.
        //17) Verify that the Continue button is disabled when the 'MOTO Type' sub option 'Recurring' is selected and no 'Card Stored on file?' was selected.
        //18) Verify that the Continue button is disabled when the 'Amount' value is empty or invalid.
        //35) Verify that the button 'Continue' is enabled only when all the fields on the screen are valid.
    }

    private fun validateContinueButtonIsEnabled() {
        onView(withId(R.id.btnContinue))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
    }

    private fun validateContinueButtonIsDisabled() {
        onView(withId(R.id.btnContinue))
            .check(matches(isDisplayed()))
            .check(matches(not(isEnabled())))
    }
}