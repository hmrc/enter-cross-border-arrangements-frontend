var selectCurrency = document.getElementById('currency');

if(selectCurrency) {
  accessibleAutocomplete.enhanceSelectElement({
    element: selectCurrency,
    showAllValues: true,
    dropdownArrow: function (){return '<svg class="autocomplete__dropdown-arrow-down" style="top: 8px;" viewBox="0 0 512 512" ><path d="M256,298.3L256,298.3L256,298.3l174.2-167.2c4.3-4.2,11.4-4.1,15.8,0.2l30.6,29.9c4.4,4.3,4.5,11.3,0.2,15.5L264.1,380.9  c-2.2,2.2-5.2,3.2-8.1,3c-3,0.1-5.9-0.9-8.1-3L35.2,176.7c-4.3-4.2-4.2-11.2,0.2-15.5L66,131.3c4.4-4.3,11.5-4.4,15.8-0.2L256,298.3  z"/></svg>'},
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
