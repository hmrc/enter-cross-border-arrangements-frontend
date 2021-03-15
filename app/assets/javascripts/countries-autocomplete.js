var selectCountry = document.querySelector('#country')

if(selectCountry) {
  accessibleAutocomplete.enhanceSelectElement({
    showAllValues: true,
    selectElement: selectCountry
  })
}