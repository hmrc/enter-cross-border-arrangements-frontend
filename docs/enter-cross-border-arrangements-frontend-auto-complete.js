// ==UserScript==
// @name         DAC6 Manual Frontend
// @namespace    http://tampermonkey.net/
// @version      0.1
// @description  DAC6 Manual Frontend automation script
// @author       mauricio.de.castro@digital.hmrc.gov.uk
// @match        http*://*/enter-cross-border-arrangements/*
// @grant        none
// @updateURL    https://raw.githubusercontent.com/hmrc/enter-cross-border-arrangements-frontend/master/docs/enter-cross-border-arrangements-frontend-auto-complete.js
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
    button.style.position = "absolute";
    button.style.top = "50px";
    button.innerHTML = 'Quick Submit';
    button.onclick = () => completePage();
    return button;
}

function currentPageIs(path) {
    let matches = window.location.pathname.match(path);
    return matches && matches.length > 0
}

function completePage() {
    individualJourney();
}

function individualJourney() {
    if(currentPageIs('/enter-cross-border-arrangements/individual/name')){ // 1
        document.getElementById("firstName").value = 'John';
        document.getElementById("lastName").value = 'Smith';
        document.getElementsByClassName('govuk-button')[0].click()
    }
    if(currentPageIs('/enter-cross-border-arrangements/individual/date-of-birth')){ // 2
        document.getElementById("value").value = '01';
        document.getElementById("value_month").value = '01';
        document.getElementById("value_year").value = '2001';
        document.getElementsByClassName('govuk-button')[0].click()
    }
    if(currentPageIs('/enter-cross-border-arrangements/individual/do-you-know-birthplace')){ // 3
        document.getElementById("confirm").checked = true;
        document.getElementsByClassName('govuk-button')[0].click()
    }
    if(currentPageIs('/enter-cross-border-arrangements/individual/birthplace')){ // 4
        document.getElementById("value").value = 'Newcastle, UK';
        document.getElementsByClassName('govuk-button')[0].click()
    }
    if(currentPageIs('/enter-cross-border-arrangements/individual/do-you-know-address')){ // 5
        document.getElementById("confirm").checked = true;
        document.getElementsByClassName('govuk-button')[0].click()
    }
    if(currentPageIs('/enter-cross-border-arrangements/individual/live-in-uk')){ // 6
        document.getElementById("value").checked = true;
        document.getElementsByClassName('govuk-button')[0].click()
    }
    if(currentPageIs('/enter-cross-border-arrangements/individual/postcode')){ // 7
        document.getElementById("postcode").value = 'NE1 1AB';
        document.getElementsByClassName('govuk-button')[0].click()
    }
    if(currentPageIs('/enter-cross-border-arrangements/individual/select-address')){ // 8
        document.getElementById("confirm").checked = true;
        document.getElementsByClassName('govuk-button')[0].click()
    }
    if(currentPageIs('/enter-cross-border-arrangements/individual/address')){ // 9
        document.getElementById("addressLine1").value = 'North East Bank';
        document.getElementById("addressLine2").value = '5-6 High Street';
        document.getElementById("addressLine3").value = '(optional)';
        document.getElementById("city").value = 'Newcastle upon Tyne';
        document.getElementById("postCode").value = 'NE1 1AB';
        document.getElementsByClassName('govuk-button')[0].click()
    }
    if(currentPageIs('/enter-cross-border-arrangements/individual/email-address')){ // 10
        document.getElementById("confirm").checked = true;
        document.getElementsByClassName('govuk-button')[0].click()
    }
    if(currentPageIs('/enter-cross-border-arrangements/individual/what-is-email-address')){ // 11
        document.getElementById("email").value = 'john.smith@somedomain.co.uk';
        document.getElementsByClassName('govuk-button')[0].click()
    }
    if(currentPageIs('/enter-cross-border-arrangements/individual/which-country-tax')){ // 12
        document.getElementById("country").value = 'United Kingdom';
        document.getElementsByClassName('govuk-button')[0].click()
    }
}