package com.mendes.testcreditcard.validator

interface ICardValidator {
    fun validateCardNumber(num: String): Boolean
    fun validateExpiryDate(month: String, year: String): Boolean
    fun validateCVV(cvv: String, cardNumber: String): Boolean
}