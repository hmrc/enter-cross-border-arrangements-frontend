// ==UserScript==
// @name         DAC6 Manual Frontend
// @namespace    http://tampermonkey.net/
// @version      0.1
// @description  DAC6 Manual Frontend automation script
// @author       mauricio.de.castro@digital.hmrc.gov.uk
// @match        http*://*/enter-cross-border-arrangements/*
// @grant        none
// @updateURL    https://raw.githubusercontent.com/hmrc/enter-cross-border-arrangements-frontend/master/docs/enter-cross-border-arrangements-manual-frontend-auto-complete.js
// ==/UserScript==

(function() {
    'use strict';
    document.getElementsByTagName("body")[0].appendChild(createQuickButton());
})();

function createQuickButton() {
    let button = document.createElement('button');
    button.id="quickSubmit";
    if (!!document.getElementById('global-header')) {
        button.classList.add('button-start');
    } else {
        button.classList.add('govuk-button');
    }
    button.style.position = "absolute"
    button.style.top = "50px"
    button.innerHTML = 'Quick Submit';
    button.onclick = () => completePage();
    return button;
}

function selectFromAutoPredict(element, selected) {

    let index = typeof selected == "number" ? selected : 0;
    let selects = element.getElementsByTagName('select');
    let inputs = element.getElementsByTagName('input');

    for(let j = 0; j < selects.length; j++){
        let options = selects[j].getElementsByTagName('option');
        let option = options[index];
        if(typeof selected == "string"){
            for(let o = 0; o < options.length; o++) {
                if(options[o].value === selected) {
                    option = options[o];
                }
            }
        }
        option.selected = "selected";
        selects[j].value = option.value;
        inputs[j].value = option.value;
    }
}


function currentPageIs(path) {
    let matches = window.location.pathname.match(path);
    return matches && matches.length > 0
}

function completePage() {
    detailsJourney();
    hallmarksJourney();
    arrangementJourney();
    reporterJourney();
    organisationJourney();
    individualJourney();
    intermediariesJourney();
}

function intermediariesJourney() {
    if(currentPageIs('/enter-cross-border-arrangements/intermediaries/update*')){
        document.getElementById("value_2").checked = true;
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

function detailsJourney() {
    if(currentPageIs('/enter-cross-border-arrangements/disclosure/name')){
        document.getElementById("disclosureName").value = 'Test';
        document.getElementsByClassName('govuk-button')[0].click()
    }
    if(currentPageIs('/enter-cross-border-arrangements/disclosure/type')){
        document.getElementById("value").checked = true;
        document.getElementsByClassName('govuk-button')[0].click()
    }
    if(currentPageIs('/enter-cross-border-arrangements/disclosure/marketable')){
        document.getElementById("value-no").checked = true;
        document.getElementsByClassName('govuk-button')[0].click()
    }
    if(currentPageIs('/enter-cross-border-arrangements/disclosure/check-your-answers')){
        document.getElementsByClassName('govuk-button')[0].click()
    }
    if(currentPageIs('/enter-cross-border-arrangements/your-disclosure-details*')){
        if(document.getElementById("hallmarks-notStarted")) {
            document.getElementsByClassName('app-task-list__task-name')[0].click()
        }
        else if(document.getElementById("arrangementDetails-notStarted")) {
            document.getElementsByClassName('app-task-list__task-name')[1].click()
        }
    }
}

function hallmarksJourney() {
    if(currentPageIs('/enter-cross-border-arrangements/hallmarks/*hallmark-category-d/*')){
        document.getElementById("value_1").checked = true;
        document.getElementsByClassName('govuk-button')[0].click()
    }
    if(currentPageIs('/enter-cross-border-arrangements/hallmarks/check-answers/*')){
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

function arrangementJourney() {
    if(currentPageIs('/enter-cross-border-arrangements/arrangement/*name')){
        document.getElementById("value").value = 'Arrangement';
        document.getElementsByClassName('govuk-button')[0].click()
    }
    if(currentPageIs('/enter-cross-border-arrangements/arrangement/*implementation-date')){
        document.getElementById("value").value = '25';
        document.getElementById("value_month").value = '06';
        document.getElementById("value_year").value = '2018';
        document.getElementsByClassName('govuk-button')[0].click()
    }
    if(currentPageIs('/enter-cross-border-arrangements/arrangement/*reporting-reason-known')){
        document.getElementById("value").checked = true;
        document.getElementsByClassName('govuk-button')[0].click()
    }
    if(currentPageIs('/enter-cross-border-arrangements/arrangement/*reporting-reason')){
        document.getElementById("value").checked = true;
        document.getElementsByClassName('govuk-button')[0].click()
    }
    if(currentPageIs('/enter-cross-border-arrangements/arrangement/*choose-countries-involved')){
        document.getElementById("value").checked = true;
        document.getElementById("value_1").checked = true;
        document.getElementsByClassName('govuk-button')[0].click()
    }
    if(currentPageIs('/enter-cross-border-arrangements/arrangement/*value')){
        document.getElementById("currency").value = 'British Pound Sterling (GBP)';
        document.getElementById("currency-select").value = 'GBP';
        //selectFromAutoPredict(document.getElementById("currency"), "GBP"); //"British Pound Sterling (GBP)"
        document.getElementById("amount").value = 10000;
        document.getElementsByClassName('govuk-button')[0].click()
    }
    if(currentPageIs('/enter-cross-border-arrangements/arrangement/*national-provisions')){
        document.getElementById("value").value = 'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Amet massa vitae tortor condimentum lacinia quis. Tristique et egestas quis ipsum suspendisse ultrices gravida. Tellus elementum sagittis vitae et leo. Felis bibendum ut tristique et. In aliquam sem fringilla ut morbi tincidunt. Eros in cursus turpis massa tincidunt. Phasellus egestas tellus rutrum tellus pellentesque eu tincidunt tortor aliquam. Ipsum nunc aliquet bibendum enim facilisis gravida neque. Convallis tellus id interdum velit laoreet. Sem integer vitae justo eget magna. Nunc mattis enim ut tellus elementum sagittis vitae et leo.\nUt diam quam nulla porttitor massa id neque aliquam. Sem viverra aliquet eget sit amet tellus cras adipiscing. Tristique risus nec feugiat in fermentum posuere urna nec tincidunt. Commodo ullamcorper a lacus vestibulum sed arcu non odio euismod. Egestas erat imperdiet sed euismod nisi. Eget lorem dolor sed viverra ipsum nunc aliquet bibendum. Nibh cras pulvinar mattis nunc sed blandit libero. Interdum velit euismod in pellentesque massa placerat duis ultricies lacus. Lacinia quis vel eros donec ac odio tempor orci. Faucibus vitae aliquet nec ullamcorper sit amet. Non arcu risus quis varius quam quisque id diam vel. Quisque sagittis purus sit amet. Tristique nulla aliquet enim tortor. At erat pellentesque adipiscing commodo. Congue eu consequat ac felis donec et. Tortor id aliquet lectus proin nibh nisl condimentum id.\nPellentesque adipiscing commodo elit at imperdiet dui accumsan. Enim ut sem viverra aliquet. Euismod elementum nisi quis eleifend quam adipiscing vitae proin sagittis. Nisl nunc mi ipsum faucibus vitae. Enim nulla aliquet porttitor lacus luctus accumsan tortor posuere ac. Ultrices gravida dictum fusce ut placerat orci nulla pellentesque. At lectus urna duis convallis convallis tellus id interdum. Vitae purus faucibus ornare suspendisse. Nulla posuere sollicitudin aliquam ultrices. Purus sit amet luctus venenatis lectus magna fringilla urna. Ullamcorper a lacus vestibulum sed. Sapien nec sagittis aliquam malesuada bibendum arcu vitae elementum. Orci eu lobortis elementum nibh. Vivamus arcu felis bibendum ut. Dui sapien eget mi proin sed libero enim sed faucibus. Eros in cursus turpis massa tincidunt. In vitae turpis massa sed elementum tempus egestas sed sed. Vel pharetra vel turpis nunc eget. Ut pharetra sit amet aliquam id diam maecenas ultricies mi.\nPosuere urna nec tincidunt praesent semper feugiat nibh sed. In aliquam sem fringilla ut morbi. Erat pellentesque adipiscing commodo elit at imperdiet dui accumsan. Pellentesque nec nam aliquam sem et tortor consequat id. Quis blandit turpis cursus in hac habitasse platea. Nam at lectus urna duis convallis. Ante in nibh mauris cursus mattis molestie a iaculis at. Lectus quam id leo in vitae turpis massa. Vel quam elementum pulvinar etiam non quam lacus. Suspendisse faucibus interdum posuere lorem ipsum dolor sit amet consectetur. Fermentum iaculis eu non diam phasellus vestibulum lorem sed risus.\nEtiam tempor orci eu lobortis elementum. Auctor augue mauris augue neque gravida in fermentum et sollicitudin. Ullamcorper eget nulla facilisi etiam dignissim. Cursus risus at ultrices mi tempus imperdiet nulla malesuada. Vitae et leo duis ut diam. Nec sagittis aliquam malesuada bibendum arcu vitae elementum. Tortor at risus viverra adipiscing at in tellus integer feugiat. Vitae et leo duis ut. Varius morbi enim nunc faucibus a pellentesque sit. Tincidunt tortor aliquam nulla facilisi cras fermentum odio eu feugiat. Aliquet nibh praesent tristique magna sit amet purus.\nUt aliquam purus sit amet. Posuere urna nec tincidunt praesent semper feugiat nibh. Tincidunt nunc pulvinar sapien et ligula ullamcorper malesuada proin libero. Commodo ullamcorper a lacus vestibulum sed arcu. Dictum non consectetur a erat nam at lectus urna. Vitae congue mauris rhoncus aenean vel. Aliquet eget sit amet tellus cras adipiscing. Posuere urna nec tincidunt praesent semper feugiat nibh. Scelerisque eleifend donec pretium vulputate sapien nec sagittis aliquam malesuada. Nunc faucibus a pellentesque sit amet porttitor. Sollicitudin aliquam ultrices sagittis orci a scelerisque. Sed libero enim sed faucibus. Egestas sed sed risus pretium quam vulputate dignissim suspendisse.\nTristique et egestas quis ipsum suspendisse. Ultrices vitae auctor eu augue ut lectus arcu bibendum at. Vulputate eu scelerisque felis imperdiet proin fermentum leo vel orci. Elit ut aliquam purus sit amet luctus. Nisi lacus sed viverra tellus. Scelerisque mauris pellentesque pulvinar pellentesque habitant morbi tristique. At imperdiet dui accumsan sit amet. Aliquet nec ullamcorper sit amet risus nullam eget felis. Non enim praesent elementum facilisis leo vel. Orci ac auctor augue mauris augue neque. Erat pellentesque adipiscing commodo elit at imperdiet dui. Nulla malesuada pellentesque elit eget.\nSem viverra aliquet eget sit amet. Donec adipiscing tristique risus nec feugiat in. Id eu nisl nunc mi. Pretium aenean pharetra magna ac placerat vestibulum lectus mauris ultrices. Et ligula ullamcorper malesuada proin libero nunc. Ultrices eros in cursus turpis massa tincidunt dui. Mi proin sed libero enim sed faucibus turpis. Tempus egestas sed sed risus pretium quam vulputate. Neque laoreet suspendisse interdum consectetur libero id. Dui faucibus in ornare quam viverra. Porttitor leo a diam sollicitudin tempor.\nHendrerit gravida rutrum quisque non tellus orci ac. Tellus cras adipiscing enim eu turpis egestas pretium aenean. Vel elit scelerisque mauris pellentesque pulvinar pellentesque habitant. Diam sit amet nisl suscipit adipiscing bibendum est ultricies. Leo integer malesuada nunc vel risus commodo. Non consectetur a erat nam at lectus urna duis. Sem nulla pharetra diam sit amet. Quam adipiscing vitae proin sagittis nisl. Viverra nibh cras pulvinar mattis nunc sed blandit. Gravida quis blandit turpis cursus in. Faucibus purus in massa tempor nec feugiat nisl. Elementum sagittis vitae et leo duis ut diam. Turpis egestas maecenas pharetra convallis posuere morbi leo. Quisque sagittis purus sit amet volutpat. Lacus luctus accumsan tortor posuere ac. Varius vel pharetra vel turpis nunc eget lorem dolor sed. Arcu cursus euismod quis viverra nibh cras. Aenean vel elit scelerisque mauris pellentesque pulvinar. Amet aliquam id diam maecenas ultricies mi. Urna cursus eget nunc scelerisque viverra mauris.\nMaecenas pharetra convallis posuere morbi leo urna molestie. Odio morbi quis commodo odio. Sed sed risus pretium quam vulputate dignissim suspendisse in. Leo integer malesuada nunc vel risus commodo. Cursus eget nunc scelerisque viverra. A iaculis at erat pellentesque adipiscing commodo. Enim nec dui nunc mattis. Ac tortor vitae purus faucibus ornare suspendisse sed. Justo donec enim diam vulputate ut pharetra. Massa vitae tortor condimentum lacinia quis vel eros. Massa enim nec dui nunc mattis enim. Commodo quis imperdiet massa tincidunt nunc pulvinar sapien et. Mauris pellentesque pulvinar pellentesque habitant morbi tristique senectus et netus. Id consectetur purus ut faucibus pulvinar elementum integer.';
        document.getElementsByClassName('govuk-button')[0].click()
    }
    if(currentPageIs('/enter-cross-border-arrangements/arrangement/*details')){
        document.getElementById("value").value = 'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Amet massa vitae tortor condimentum lacinia quis. Tristique et egestas quis ipsum suspendisse ultrices gravida. Tellus elementum sagittis vitae et leo. Felis bibendum ut tristique et. In aliquam sem fringilla ut morbi tincidunt. Eros in cursus turpis massa tincidunt. Phasellus egestas tellus rutrum tellus pellentesque eu tincidunt tortor aliquam. Ipsum nunc aliquet bibendum enim facilisis gravida neque. Convallis tellus id interdum velit laoreet. Sem integer vitae justo eget magna. Nunc mattis enim ut tellus elementum sagittis vitae et leo.\nUt diam quam nulla porttitor massa id neque aliquam. Sem viverra aliquet eget sit amet tellus cras adipiscing. Tristique risus nec feugiat in fermentum posuere urna nec tincidunt. Commodo ullamcorper a lacus vestibulum sed arcu non odio euismod. Egestas erat imperdiet sed euismod nisi. Eget lorem dolor sed viverra ipsum nunc aliquet bibendum. Nibh cras pulvinar mattis nunc sed blandit libero. Interdum velit euismod in pellentesque massa placerat duis ultricies lacus. Lacinia quis vel eros donec ac odio tempor orci. Faucibus vitae aliquet nec ullamcorper sit amet. Non arcu risus quis varius quam quisque id diam vel. Quisque sagittis purus sit amet. Tristique nulla aliquet enim tortor. At erat pellentesque adipiscing commodo. Congue eu consequat ac felis donec et. Tortor id aliquet lectus proin nibh nisl condimentum id.\nPellentesque adipiscing commodo elit at imperdiet dui accumsan. Enim ut sem viverra aliquet. Euismod elementum nisi quis eleifend quam adipiscing vitae proin sagittis. Nisl nunc mi ipsum faucibus vitae. Enim nulla aliquet porttitor lacus luctus accumsan tortor posuere ac. Ultrices gravida dictum fusce ut placerat orci nulla pellentesque. At lectus urna duis convallis convallis tellus id interdum. Vitae purus faucibus ornare suspendisse. Nulla posuere sollicitudin aliquam ultrices. Purus sit amet luctus venenatis lectus magna fringilla urna. Ullamcorper a lacus vestibulum sed. Sapien nec sagittis aliquam malesuada bibendum arcu vitae elementum. Orci eu lobortis elementum nibh. Vivamus arcu felis bibendum ut. Dui sapien eget mi proin sed libero enim sed faucibus. Eros in cursus turpis massa tincidunt. In vitae turpis massa sed elementum tempus egestas sed sed. Vel pharetra vel turpis nunc eget. Ut pharetra sit amet aliquam id diam maecenas ultricies mi.\nPosuere urna nec tincidunt praesent semper feugiat nibh sed. In aliquam sem fringilla ut morbi. Erat pellentesque adipiscing commodo elit at imperdiet dui accumsan. Pellentesque nec nam aliquam sem et tortor consequat id. Quis blandit turpis cursus in hac habitasse platea. Nam at lectus urna duis convallis. Ante in nibh mauris cursus mattis molestie a iaculis at. Lectus quam id leo in vitae turpis massa. Vel quam elementum pulvinar etiam non quam lacus. Suspendisse faucibus interdum posuere lorem ipsum dolor sit amet consectetur. Fermentum iaculis eu non diam phasellus vestibulum lorem sed risus.\nEtiam tempor orci eu lobortis elementum. Auctor augue mauris augue neque gravida in fermentum et sollicitudin. Ullamcorper eget nulla facilisi etiam dignissim. Cursus risus at ultrices mi tempus imperdiet nulla malesuada. Vitae et leo duis ut diam. Nec sagittis aliquam malesuada bibendum arcu vitae elementum. Tortor at risus viverra adipiscing at in tellus integer feugiat. Vitae et leo duis ut. Varius morbi enim nunc faucibus a pellentesque sit. Tincidunt tortor aliquam nulla facilisi cras fermentum odio eu feugiat. Aliquet nibh praesent tristique magna sit amet purus.\nUt aliquam purus sit amet. Posuere urna nec tincidunt praesent semper feugiat nibh. Tincidunt nunc pulvinar sapien et ligula ullamcorper malesuada proin libero. Commodo ullamcorper a lacus vestibulum sed arcu. Dictum non consectetur a erat nam at lectus urna. Vitae congue mauris rhoncus aenean vel. Aliquet eget sit amet tellus cras adipiscing. Posuere urna nec tincidunt praesent semper feugiat nibh. Scelerisque eleifend donec pretium vulputate sapien nec sagittis aliquam malesuada. Nunc faucibus a pellentesque sit amet porttitor. Sollicitudin aliquam ultrices sagittis orci a scelerisque. Sed libero enim sed faucibus. Egestas sed sed risus pretium quam vulputate dignissim suspendisse.\nTristique et egestas quis ipsum suspendisse. Ultrices vitae auctor eu augue ut lectus arcu bibendum at. Vulputate eu scelerisque felis imperdiet proin fermentum leo vel orci. Elit ut aliquam purus sit amet luctus. Nisi lacus sed viverra tellus. Scelerisque mauris pellentesque pulvinar pellentesque habitant morbi tristique. At imperdiet dui accumsan sit amet. Aliquet nec ullamcorper sit amet risus nullam eget felis. Non enim praesent elementum facilisis leo vel. Orci ac auctor augue mauris augue neque. Erat pellentesque adipiscing commodo elit at imperdiet dui. Nulla malesuada pellentesque elit eget.\nSem viverra aliquet eget sit amet. Donec adipiscing tristique risus nec feugiat in. Id eu nisl nunc mi. Pretium aenean pharetra magna ac placerat vestibulum lectus mauris ultrices. Et ligula ullamcorper malesuada proin libero nunc. Ultrices eros in cursus turpis massa tincidunt dui. Mi proin sed libero enim sed faucibus turpis. Tempus egestas sed sed risus pretium quam vulputate. Neque laoreet suspendisse interdum consectetur libero id. Dui faucibus in ornare quam viverra. Porttitor leo a diam sollicitudin tempor.\nHendrerit gravida rutrum quisque non tellus orci ac. Tellus cras adipiscing enim eu turpis egestas pretium aenean. Vel elit scelerisque mauris pellentesque pulvinar pellentesque habitant. Diam sit amet nisl suscipit adipiscing bibendum est ultricies. Leo integer malesuada nunc vel risus commodo. Non consectetur a erat nam at lectus urna duis. Sem nulla pharetra diam sit amet. Quam adipiscing vitae proin sagittis nisl. Viverra nibh cras pulvinar mattis nunc sed blandit. Gravida quis blandit turpis cursus in. Faucibus purus in massa tempor nec feugiat nisl. Elementum sagittis vitae et leo duis ut diam. Turpis egestas maecenas pharetra convallis posuere morbi leo. Quisque sagittis purus sit amet volutpat. Lacus luctus accumsan tortor posuere ac. Varius vel pharetra vel turpis nunc eget lorem dolor sed. Arcu cursus euismod quis viverra nibh cras. Aenean vel elit scelerisque mauris pellentesque pulvinar. Amet aliquam id diam maecenas ultricies mi. Urna cursus eget nunc scelerisque viverra mauris.\nMaecenas pharetra convallis posuere morbi leo urna molestie. Odio morbi quis commodo odio. Sed sed risus pretium quam vulputate dignissim suspendisse in. Leo integer malesuada nunc vel risus commodo. Cursus eget nunc scelerisque viverra. A iaculis at erat pellentesque adipiscing commodo. Enim nec dui nunc mattis. Ac tortor vitae purus faucibus ornare suspendisse sed. Justo donec enim diam vulputate ut pharetra. Massa vitae tortor condimentum lacinia quis vel eros. Massa enim nec dui nunc mattis enim. Commodo quis imperdiet massa tincidunt nunc pulvinar sapien et. Mauris pellentesque pulvinar pellentesque habitant morbi tristique senectus et netus. Id consectetur purus ut faucibus pulvinar elementum integer.';
        document.getElementsByClassName('govuk-button')[0].click()
    }
    if(currentPageIs('/enter-cross-border-arrangements/arrangement/check-answers/*')){
        document.getElementsByClassName('govuk-button')[0].click()
    }
}
function reporterJourney() {
    if(currentPageIs('/enter-cross-border-arrangements/reporter/*organisation-or-individual/*')){
        document.getElementById("value").checked = true;
        document.getElementsByClassName('govuk-button')[0].click()
    }
    if(currentPageIs('/enter-cross-border-arrangements/reporter/organisation/name/*')){
        document.getElementById("organisationName").value = 'My organisation';
        document.getElementsByClassName('govuk-button')[0].click()
    }
    if(currentPageIs('/enter-cross-border-arrangements/reporter/organisation/main-address-in-uk/*')){
        document.getElementById("value").checked = true;
        document.getElementsByClassName('govuk-button')[0].click()
    }
    if(currentPageIs('/enter-cross-border-arrangements/reporter/organisation/*postcode/*')){
        document.getElementById("postcode").value = 'ZZ1Z 7AB';
        document.getElementsByClassName('govuk-button')[0].click()
    }
    if(currentPageIs('/enter-cross-border-arrangements/reporter/organisation/*select-address/*')){
        document.getElementById("value").checked = true;
        document.getElementsByClassName('govuk-button')[0].click()
    }
    if(currentPageIs('/enter-cross-border-arrangements/reporter/organisation/*address/*')){
        document.getElementById("addressLine1").value = 'North East Bank';
        document.getElementById("addressLine2").value = '5-6 High Street';
        document.getElementById("addressLine3").value = '(optional)';
        document.getElementById("city").value = 'Newcastle upon Tyne';
        document.getElementById("postCode").value = 'NE1 1AB';
        document.getElementsByClassName('govuk-button')[0].click()
    }
    if(currentPageIs('/enter-cross-border-arrangements/reporter/organisation/*email-address/*')){
        document.getElementById("value").checked = true;
        document.getElementsByClassName('govuk-button')[0].click()
    }
    if(currentPageIs('/enter-cross-border-arrangements/reporter/organisation/*what-is-email-address/*')){
        document.getElementById("value").value = 'john.smith@somedomain.co.uk';
        document.getElementsByClassName('govuk-button')[0].click()
    }
    if(currentPageIs('/enter-cross-border-arrangements/reporter/*resident-tax-country/*')){
        document.getElementById("country").value = 'United Kingdom';
        document.getElementById("country-select").value = 'GB';
        document.getElementsByClassName('govuk-button')[0].click()
    }
    if(currentPageIs('/enter-cross-border-arrangements/reporter/*uk-tin-known*')){
        document.getElementById("value").checked = true;
        document.getElementsByClassName('govuk-button')[0].click()
    }
    if(currentPageIs('/enter-cross-border-arrangements/reporter/*non-uk-tin-known*')){
        document.getElementById("value-no").checked = true;
        document.getElementsByClassName('govuk-button')[0].click()
    }
    if(currentPageIs('/enter-cross-border-arrangements/reporter/*uk-tax-numbers*')){
        document.getElementById("firstTaxNumber").value = '1234567890';
        document.getElementsByClassName('govuk-button')[0].click()
    }
    if(currentPageIs('/enter-cross-border-arrangements/reporter/*tax-resident-countries*')){
        document.getElementById("value-no").checked = true;
        document.getElementsByClassName('govuk-button')[0].click()
    }
    if(currentPageIs('/enter-cross-border-arrangements/reporter/*role*')){
        document.getElementById("value_1").checked = true;
        document.getElementsByClassName('govuk-button')[0].click()
    }
    if(currentPageIs('/enter-cross-border-arrangements/reporter/taxpayer/*why-report-in-uk*')){
        document.getElementById("value_4").checked = true;
        document.getElementsByClassName('govuk-button')[0].click()
    }
    if(currentPageIs('/enter-cross-border-arrangements/reporter/check-answers/*')){
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

function organisationJourney() {
    if(currentPageIs('/enter-cross-border-arrangements/organisation/name')){
        document.getElementById("organisataionName").value = 'My organisation';
        document.getElementsByClassName('govuk-button')[0].click()
    }

    if(currentPageIs('/enter-cross-border-arrangements/organisation/*do-you-know-address')){
        document.getElementById("value").checked = true;
        document.getElementsByClassName('govuk-button')[0].click()
    }
    if(currentPageIs('/enter-cross-border-arrangements/organisation/*main-address-in-uk')){
        document.getElementById("value").checked = true;
        document.getElementsByClassName('govuk-button')[0].click()
    }
    if(currentPageIs('/enter-cross-border-arrangements/organisation/*postcode')){
        document.getElementById("postcode").value = 'ZZ1Z 7AB';
        document.getElementsByClassName('govuk-button')[0].click()
    }
    if(currentPageIs('/enter-cross-border-arrangements/organisation/*select-address')){
        document.getElementById("value").checked = true;
        document.getElementsByClassName('govuk-button')[0].click()
    }
    if(currentPageIs('/enter-cross-border-arrangements/organisation/*address')){
        document.getElementById("addressLine1").value = 'North East Bank';
        document.getElementById("addressLine2").value = '5-6 High Street';
        document.getElementById("addressLine3").value = '(optional)';
        document.getElementById("city").value = 'Newcastle upon Tyne';
        document.getElementById("postCode").value = 'NE1 1AB';
        document.getElementsByClassName('govuk-button')[0].click()
    }
    if(currentPageIs('/enter-cross-border-arrangements/organisation/*email-address')){
        document.getElementById("confirm").checked = true;
        document.getElementsByClassName('govuk-button')[0].click()
    }
    if(currentPageIs('/enter-cross-border-arrangements/organisation/*what-is-email-address')){
        document.getElementById("email").value = 'john.smith@somedomain.co.uk';
        document.getElementsByClassName('govuk-button')[0].click()
    }
    if(currentPageIs('/enter-cross-border-arrangements/organisation/*which-country-tax')){
        document.getElementById("country").value = 'United Kingdom';
        document.getElementById("country-select").value = 'GB';
        document.getElementsByClassName('govuk-button')[0].click()
    }
    if(currentPageIs('/enter-cross-border-arrangements/organisation/*uk-tin-known*')){
        document.getElementById("confirm").checked = true;
        document.getElementsByClassName('govuk-button')[0].click()
    }
    if(currentPageIs('/enter-cross-border-arrangements/organisation/*uk-tax-numbers*')){
        document.getElementById("firstTaxNumber").value = '1234567890';
        document.getElementsByClassName('govuk-button')[0].click()
    }
    if(currentPageIs('/enter-cross-border-arrangements/organisation/*tax-resident-countries*')){
        document.getElementById("confirm-no").checked = true;
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

function individualJourney() {
    if(currentPageIs('/enter-cross-border-arrangements/individual/name')){
        document.getElementById("firstName").value = 'John';
        document.getElementById("lastName").value = 'Smith';
        document.getElementsByClassName('govuk-button')[0].click()
    }
    if(currentPageIs('/enter-cross-border-arrangements/individual/*date-of-birth')){
        document.getElementById("value").value = '01';
        document.getElementById("value_month").value = '01';
        document.getElementById("value_year").value = '2001';
        document.getElementsByClassName('govuk-button')[0].click()
    }
    if(currentPageIs('/enter-cross-border-arrangements/individual/*do-you-know-birthplace')){
        document.getElementById("confirm").checked = true;
        document.getElementsByClassName('govuk-button')[0].click()
    }
    if(currentPageIs('/enter-cross-border-arrangements/individual/*birthplace')){
        document.getElementById("value").value = 'Newcastle, UK';
        document.getElementsByClassName('govuk-button')[0].click()
    }
    if(currentPageIs('/enter-cross-border-arrangements/individual/*do-you-know-address')){
        document.getElementById("confirm").checked = true;
        document.getElementsByClassName('govuk-button')[0].click()
    }
    if(currentPageIs('/enter-cross-border-arrangements/individual/*live-in-uk')){
        document.getElementById("value").checked = true;
        document.getElementsByClassName('govuk-button')[0].click()
    }
    if(currentPageIs('/enter-cross-border-arrangements/individual/*postcode')){
        document.getElementById("postcode").value = 'ZZ1Z 7AB';
        document.getElementsByClassName('govuk-button')[0].click()
    }
    if(currentPageIs('/enter-cross-border-arrangements/individual/*select-address')){
        document.getElementById("value").checked = true;
        document.getElementsByClassName('govuk-button')[0].click()
    }
    if(currentPageIs('/enter-cross-border-arrangements/individual/*address')){
        document.getElementById("addressLine1").value = 'North East Bank';
        document.getElementById("addressLine2").value = '5-6 High Street';
        document.getElementById("addressLine3").value = '(optional)';
        document.getElementById("city").value = 'Newcastle upon Tyne';
        document.getElementById("postCode").value = 'NE1 1AB';
        document.getElementsByClassName('govuk-button')[0].click()
    }
    if(currentPageIs('/enter-cross-border-arrangements/individual/*email-address')){
        document.getElementById("confirm").checked = true;
        document.getElementsByClassName('govuk-button')[0].click()
    }
    if(currentPageIs('/enter-cross-border-arrangements/individual/*what-is-email-address')){
        document.getElementById("email").value = 'john.smith@somedomain.co.uk';
        document.getElementsByClassName('govuk-button')[0].click()
    }
    if(currentPageIs('/enter-cross-border-arrangements/individual/*which-country-tax')){
        document.getElementById("country").value = 'United Kingdom';
        document.getElementById("country-select").value = 'GB';
        document.getElementsByClassName('govuk-button')[0].click()
    }
    if(currentPageIs('/enter-cross-border-arrangements/individual/*uk-tin-known*')){
        document.getElementById("confirm").checked = true;
        document.getElementsByClassName('govuk-button')[0].click()
    }
    if(currentPageIs('/enter-cross-border-arrangements/individual/*uk-tax-numbers*')){
        document.getElementById("firstTaxNumber").value = '1234567890';
        document.getElementsByClassName('govuk-button')[0].click()
    }
    if(currentPageIs('/enter-cross-border-arrangements/individual/*tax-resident-countries*')){
        document.getElementById("confirm-no").checked = true;
        document.getElementsByClassName('govuk-button')[0].click()
    }
}