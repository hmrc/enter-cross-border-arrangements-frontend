var selectCountry = document.getElementById('country');

if(selectCountry) {
  accessibleAutocomplete.enhanceSelectElement({
    element: selectCountry,
    showAllValues: true,
    selectElement: selectCountry
  })

  autocompleteErrorStyle();
  document.getElementById('country').addEventListener('focusout', autocompleteErrorStyle);
  document.querySelector('.autocomplete__wrapper').classList.add('govuk-input--width-20');

  //======================================================
  // Fix CSS styling of errors (red outline) around the input dropdown
  //======================================================
  function autocompleteErrorStyle() {
    if(document.getElementById('country-error')) {
      document.getElementById('country').classList.add('govuk-input', 'govuk-input--error');
      document.getElementById('country').classList.remove('autocomplete__input', 'autocomplete__input--focused');
    }
  }
}
