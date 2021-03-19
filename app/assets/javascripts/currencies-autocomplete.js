var selectCurrency = document.getElementById('currency');

if(selectCurrency) {
  accessibleAutocomplete.enhanceSelectElement({
    element: selectCurrency,
    showAllValues: true,
    selectElement: selectCurrency
  })

  autocompleteErrorStyle();
  document.getElementById('currency').addEventListener('focusout', autocompleteErrorStyle);
  document.querySelector('.autocomplete__wrapper').classList.add('govuk-input--width-20');

  //======================================================
  // Fix CSS styling of errors (red outline) around the input dropdown
  //======================================================
  function autocompleteErrorStyle() {
    if(document.getElementById('currency-error')) {
      document.getElementById('currency').classList.add('govuk-input', 'govuk-input--error');
      document.getElementById('currency').classList.remove('autocomplete__input', 'autocomplete__input--focused');
    }
  }
}
