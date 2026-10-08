(function () {
    "use strict";

    /*
     * ============================================================
     * RATION PORTAL INTERNATIONALIZATION
     * Languages:
     *   en = English
     *   hi = Hindi
     *   te = Telugu
     *
     * One translation system only.
     * No automatic text-node scanning.
     * ============================================================
     */

    const STORAGE_KEY = "rationPortalLanguage";
    const DEFAULT_LANGUAGE = "en";

    const translations = {

        /* ========================================================
           ENGLISH
        ======================================================== */
        en: {

            "site.name": "Ration Portal",
            "site.subtitle": "Digital Public Distribution System",

            "access.government": "Government Service Portal",
            "access.digitalPds": "Digital Public Distribution System",
            "access.skip": "Skip to main content",

            "brand.gov": "Government-style citizen service portal",
            "brand.name": "Ration Portal",
            "brand.pds": "Public Distribution System",
            "brand.tagline": "Food Security â€¢ Empowerment â€¢ Prosperity",

            "header.slogan": "Har Ghar Ann, Har Ghar Suraksha",
            "header.service": "Serving citizens through digital public distribution services",

            "auth.login": "Citizen Login",
            "auth.register": "Register",

            "nav.home": "Home",
            "nav.services": "Services",
            "nav.rationCard": "Ration Card",
            "nav.fps": "FPS",
            "nav.help": "Help",
            "nav.transparency": "Transparency",

            "hero.kicker": "Citizen Digital Service Portal",
            "hero.rightFood": "Right Food",
            "hero.rightPeople": "Right People",
            "hero.rightTime": "Right Time",

            "hero.subtitle":
                "Public Distribution System â€“ Government-inspired digital service experience",

            "hero.tagline":
                "Ensuring Food Security for Every Citizen through accessible, transparent and citizen-centric digital services.",

            "hero.chip.food": "Food Security for All",
            "hero.chip.transparent": "Transparent System",
            "hero.chip.digital": "Digital India",
            "hero.chip.access": "Easy Access",

            "hero.familySmall": "Citizen-first public distribution",
            "hero.familyMera": "Mera Ration Mera Adhikar",
            "hero.familyMeraWord": "Mera",
            "hero.familyAdhikar": "Adhikar",

            "search.label":
                "Ration Card Number, Ration ID or Member ID",

            "search.placeholder":
                "Enter Ration Card Number / Ration ID / Member ID",

            "search.button": "Search",

            "services.kicker": "Citizen services",

            "services.title":
                "Essential Services at One Place",

            "services.description":
                "Simple digital access to ration card, grievance, fair price shop and entitlement services.",

            "services.applications":
                "My Applications",

            "service.apply.title":
                "Apply New Ration Card",

            "service.apply.description":
                "Get your new ration card online through a guided citizen application journey.",

            "service.download.title":
                "Download e-Ration Card",

            "service.download.description":
                "Access and download your digital ration card securely after authentication.",

            "service.fps.title":
                "Locate FPS",

            "service.fps.description":
                "Find Fair Price Shop information by district, area and available service details.",

            "service.grievance.title":
                "Lodge Grievance",

            "service.grievance.description":
                "Raise a service grievance and follow the progress of your citizen request.",

            "service.entitlement.title":
                "Know Your Entitlements",

            "service.entitlement.description":
                "Understand ration benefits, public distribution information and citizen entitlements.",

            "dashboard.kicker": "PDS dashboard",

            "dashboard.title":
                "Public Distribution System Overview",

            "dashboard.description":
                "Live Public Distribution System information and service statistics.",

            "dashboard.cards":
                "Total Ration Cards",

            "dashboard.rice":
                "Rice Distributed (Quintals)",

            "dashboard.fps":
                "Active FPS",

            "dashboard.cardsGrowth": "+4.8%",
            "dashboard.riceGrowth": "+7.2%",
            "dashboard.fpsGrowth": "+2.6%",

            "dashboard.note":
                "* Statistics shown above are illustrative values for this educational demonstration interface.",

            "family.kicker": "National food security",

            "family.title": "Food for a",

            "family.titleStrong": "Stronger India",

            "family.description":
                "A citizen-first digital experience connecting ration services, family information and public distribution workflows.",

            "family.slogan":
                "Food for a Stronger India",

            "announcement.title": "Announcements",

            "announcement.verify":
                "Citizens are encouraged to verify their family details before submitting service applications.",

            "announcement.check":
                "Check your ration entitlements and application status through the citizen portal.",

            "feature.transparent.title":
                "Transparent & Accountable",

            "feature.transparent.description":
                "Clear service journeys, status visibility and responsible digital workflows.",

            "feature.digital.title":
                "Digital India",

            "feature.digital.description":
                "Modern web services designed for simple and accessible citizen interactions.",

            "feature.access.title":
                "Easy Access for All",

            "feature.access.description":
                "Responsive, readable and accessibility-oriented interfaces for different devices.",

            "feature.garib.title":
                "Garib Kalyan Our Priority",

            "feature.garib.description":
                "The design emphasizes food security and citizen-centric public services.",

            "feature.bannerKicker":
                "Together for food security",

            "feature.jaiHind":
                "Jai Hind",

            "feature.bannerDescription":
                "A modern digital experience inspired by India's tricolour, rice cultivation and citizen-first public service values.",

            "feature.bannerBottom":
                "ðŸ‡®ðŸ‡³ Food â€¢ Family â€¢ Future",

            "notice.title":
                "Important Notice",

            "notice.text":
                "This portal is an educational software project designed to demonstrate a modern Public Distribution System experience using web technologies.",

            "notice.warning":
                "This project is not an official Government of India website. Official government emblems, seals and identities are not used as official representations in this project.",

            "footer.name":
                "Ration Portal",

            "footer.pds":
                "Public Distribution System",

            "footer.about":
                "About Us",

            "footer.privacy":
                "Privacy Policy",

            "footer.terms":
                "Terms & Conditions",

            "footer.contact":
                "Contact Us",

            "footer.transparency":
                "Transparency",

            "footer.educational":
                "Educational demonstration project.",

            "footer.designed":
                "ðŸ‡®ðŸ‡³ Designed for a Food Secure India",

            "language.english":
                "English",

            "language.hindi":
                "à¤¹à¤¿à¤¨à¥à¤¦à¥€",

            "language.telugu":
                "à°¤à±†à°²à±à°—à±",

            "language.select":
                "Select Language"
        },


        /* ========================================================
           HINDI
        ======================================================== */
        hi: {

            "site.name": "à¤°à¤¾à¤¶à¤¨ à¤ªà¥‹à¤°à¥à¤Ÿà¤²",
            "site.subtitle": "à¤¡à¤¿à¤œà¤¿à¤Ÿà¤² à¤¸à¤¾à¤°à¥à¤µà¤œà¤¨à¤¿à¤• à¤µà¤¿à¤¤à¤°à¤£ à¤ªà¥à¤°à¤£à¤¾à¤²à¥€",

            "access.government": "à¤¸à¤°à¤•à¤¾à¤°à¥€ à¤¸à¥‡à¤µà¤¾ à¤ªà¥‹à¤°à¥à¤Ÿà¤²",
            "access.digitalPds": "à¤¡à¤¿à¤œà¤¿à¤Ÿà¤² à¤¸à¤¾à¤°à¥à¤µà¤œà¤¨à¤¿à¤• à¤µà¤¿à¤¤à¤°à¤£ à¤ªà¥à¤°à¤£à¤¾à¤²à¥€",
            "access.skip": "à¤®à¥à¤–à¥à¤¯ à¤¸à¤¾à¤®à¤—à¥à¤°à¥€ à¤ªà¤° à¤œà¤¾à¤à¤",

            "brand.gov":
                "à¤¸à¤°à¤•à¤¾à¤°à¥€ à¤¶à¥ˆà¤²à¥€ à¤•à¤¾ à¤¨à¤¾à¤—à¤°à¤¿à¤• à¤¸à¥‡à¤µà¤¾ à¤ªà¥‹à¤°à¥à¤Ÿà¤²",

            "brand.name":
                "à¤°à¤¾à¤¶à¤¨ à¤ªà¥‹à¤°à¥à¤Ÿà¤²",

            "brand.pds":
                "à¤¸à¤¾à¤°à¥à¤µà¤œà¤¨à¤¿à¤• à¤µà¤¿à¤¤à¤°à¤£ à¤ªà¥à¤°à¤£à¤¾à¤²à¥€",

            "brand.tagline":
                "à¤–à¤¾à¤¦à¥à¤¯ à¤¸à¥à¤°à¤•à¥à¤·à¤¾ â€¢ à¤¸à¤¶à¤•à¥à¤¤à¤¿à¤•à¤°à¤£ â€¢ à¤¸à¤®à¥ƒà¤¦à¥à¤§à¤¿",

            "header.slogan":
                "à¤¹à¤° à¤˜à¤° à¤…à¤¨à¥à¤¨, à¤¹à¤° à¤˜à¤° à¤¸à¥à¤°à¤•à¥à¤·à¤¾",

            "header.service":
                "à¤¡à¤¿à¤œà¤¿à¤Ÿà¤² à¤¸à¤¾à¤°à¥à¤µà¤œà¤¨à¤¿à¤• à¤µà¤¿à¤¤à¤°à¤£ à¤¸à¥‡à¤µà¤¾à¤“à¤‚ à¤•à¥‡ à¤®à¤¾à¤§à¥à¤¯à¤® à¤¸à¥‡ à¤¨à¤¾à¤—à¤°à¤¿à¤•à¥‹à¤‚ à¤•à¥€ à¤¸à¥‡à¤µà¤¾",

            "auth.login":
                "à¤¨à¤¾à¤—à¤°à¤¿à¤• à¤²à¥‰à¤—à¤¿à¤¨",

            "auth.register":
                "à¤ªà¤‚à¤œà¥€à¤•à¤°à¤£",

            "nav.home":
                "à¤¹à¥‹à¤®",

            "nav.services":
                "à¤¸à¥‡à¤µà¤¾à¤à¤",

            "nav.rationCard":
                "à¤°à¤¾à¤¶à¤¨ à¤•à¤¾à¤°à¥à¤¡",

            "nav.fps":
                "à¤‰à¤šà¤¿à¤¤ à¤®à¥‚à¤²à¥à¤¯ à¤¦à¥à¤•à¤¾à¤¨",

            "nav.help":
                "à¤¸à¤¹à¤¾à¤¯à¤¤à¤¾",

            "nav.transparency":
                "à¤ªà¤¾à¤°à¤¦à¤°à¥à¤¶à¤¿à¤¤à¤¾",

            "hero.kicker":
                "à¤¨à¤¾à¤—à¤°à¤¿à¤• à¤¡à¤¿à¤œà¤¿à¤Ÿà¤² à¤¸à¥‡à¤µà¤¾ à¤ªà¥‹à¤°à¥à¤Ÿà¤²",

            "hero.rightFood":
                "à¤¸à¤¹à¥€ à¤­à¥‹à¤œà¤¨",

            "hero.rightPeople":
                "à¤¸à¤¹à¥€ à¤²à¥‹à¤—",

            "hero.rightTime":
                "à¤¸à¤¹à¥€ à¤¸à¤®à¤¯",

            "hero.subtitle":
                "à¤¸à¤¾à¤°à¥à¤µà¤œà¤¨à¤¿à¤• à¤µà¤¿à¤¤à¤°à¤£ à¤ªà¥à¤°à¤£à¤¾à¤²à¥€ â€“ à¤¸à¤°à¤•à¤¾à¤° à¤¸à¥‡ à¤ªà¥à¤°à¥‡à¤°à¤¿à¤¤ à¤¡à¤¿à¤œà¤¿à¤Ÿà¤² à¤¸à¥‡à¤µà¤¾ à¤…à¤¨à¥à¤­à¤µ",

            "hero.tagline":
                "à¤¸à¥à¤²à¤­, à¤ªà¤¾à¤°à¤¦à¤°à¥à¤¶à¥€ à¤”à¤° à¤¨à¤¾à¤—à¤°à¤¿à¤•-à¤•à¥‡à¤‚à¤¦à¥à¤°à¤¿à¤¤ à¤¡à¤¿à¤œà¤¿à¤Ÿà¤² à¤¸à¥‡à¤µà¤¾à¤“à¤‚ à¤•à¥‡ à¤®à¤¾à¤§à¥à¤¯à¤® à¤¸à¥‡ à¤ªà¥à¤°à¤¤à¥à¤¯à¥‡à¤• à¤¨à¤¾à¤—à¤°à¤¿à¤• à¤•à¥‡ à¤²à¤¿à¤ à¤–à¤¾à¤¦à¥à¤¯ à¤¸à¥à¤°à¤•à¥à¤·à¤¾ à¤¸à¥à¤¨à¤¿à¤¶à¥à¤šà¤¿à¤¤ à¤•à¤°à¤¨à¤¾à¥¤",

            "hero.chip.food":
                "à¤¸à¤­à¥€ à¤•à¥‡ à¤²à¤¿à¤ à¤–à¤¾à¤¦à¥à¤¯ à¤¸à¥à¤°à¤•à¥à¤·à¤¾",

            "hero.chip.transparent":
                "à¤ªà¤¾à¤°à¤¦à¤°à¥à¤¶à¥€ à¤ªà¥à¤°à¤£à¤¾à¤²à¥€",

            "hero.chip.digital":
                "à¤¡à¤¿à¤œà¤¿à¤Ÿà¤² à¤‡à¤‚à¤¡à¤¿à¤¯à¤¾",

            "hero.chip.access":
                "à¤†à¤¸à¤¾à¤¨ à¤ªà¤¹à¥à¤à¤š",

            "hero.familySmall":
                "à¤¨à¤¾à¤—à¤°à¤¿à¤•-à¤•à¥‡à¤‚à¤¦à¥à¤°à¤¿à¤¤ à¤¸à¤¾à¤°à¥à¤µà¤œà¤¨à¤¿à¤• à¤µà¤¿à¤¤à¤°à¤£",

            "hero.familyMera":
                "à¤®à¥‡à¤°à¤¾ à¤°à¤¾à¤¶à¤¨ à¤®à¥‡à¤°à¤¾ à¤…à¤§à¤¿à¤•à¤¾à¤°",

            "hero.familyMeraWord":
                "à¤®à¥‡à¤°à¤¾",

            "hero.familyAdhikar":
                "à¤…à¤§à¤¿à¤•à¤¾à¤°",

            "search.label":
                "à¤°à¤¾à¤¶à¤¨ à¤•à¤¾à¤°à¥à¤¡ à¤¨à¤‚à¤¬à¤°, à¤°à¤¾à¤¶à¤¨ à¤†à¤ˆà¤¡à¥€ à¤¯à¤¾ à¤¸à¤¦à¤¸à¥à¤¯ à¤†à¤ˆà¤¡à¥€",

            "search.placeholder":
                "à¤°à¤¾à¤¶à¤¨ à¤•à¤¾à¤°à¥à¤¡ à¤¨à¤‚à¤¬à¤° / à¤°à¤¾à¤¶à¤¨ à¤†à¤ˆà¤¡à¥€ / à¤¸à¤¦à¤¸à¥à¤¯ à¤†à¤ˆà¤¡à¥€ à¤¦à¤°à¥à¤œ à¤•à¤°à¥‡à¤‚",

            "search.button":
                "à¤–à¥‹à¤œà¥‡à¤‚",

            "services.kicker":
                "à¤¨à¤¾à¤—à¤°à¤¿à¤• à¤¸à¥‡à¤µà¤¾à¤à¤",

            "services.title":
                "à¤†à¤µà¤¶à¥à¤¯à¤• à¤¸à¥‡à¤µà¤¾à¤à¤ à¤à¤• à¤¹à¥€ à¤¸à¥à¤¥à¤¾à¤¨ à¤ªà¤°",

            "services.description":
                "à¤°à¤¾à¤¶à¤¨ à¤•à¤¾à¤°à¥à¤¡, à¤¶à¤¿à¤•à¤¾à¤¯à¤¤, à¤‰à¤šà¤¿à¤¤ à¤®à¥‚à¤²à¥à¤¯ à¤¦à¥à¤•à¤¾à¤¨ à¤”à¤° à¤²à¤¾à¤­ à¤¸à¥‡ à¤¸à¤‚à¤¬à¤‚à¤§à¤¿à¤¤ à¤¸à¥‡à¤µà¤¾à¤“à¤‚ à¤¤à¤• à¤¸à¤°à¤² à¤¡à¤¿à¤œà¤¿à¤Ÿà¤² à¤ªà¤¹à¥à¤à¤šà¥¤",

            "services.applications":
                "à¤®à¥‡à¤°à¥‡ à¤†à¤µà¥‡à¤¦à¤¨",

            "service.apply.title":
                "à¤¨à¤¯à¤¾ à¤°à¤¾à¤¶à¤¨ à¤•à¤¾à¤°à¥à¤¡ à¤†à¤µà¥‡à¤¦à¤¨ à¤•à¤°à¥‡à¤‚",

            "service.apply.description":
                "à¤¨à¤¿à¤°à¥à¤¦à¥‡à¤¶à¤¿à¤¤ à¤¨à¤¾à¤—à¤°à¤¿à¤• à¤†à¤µà¥‡à¤¦à¤¨ à¤ªà¥à¤°à¤•à¥à¤°à¤¿à¤¯à¤¾ à¤•à¥‡ à¤®à¤¾à¤§à¥à¤¯à¤® à¤¸à¥‡ à¤¨à¤¯à¤¾ à¤°à¤¾à¤¶à¤¨ à¤•à¤¾à¤°à¥à¤¡ à¤‘à¤¨à¤²à¤¾à¤‡à¤¨ à¤ªà¥à¤°à¤¾à¤ªà¥à¤¤ à¤•à¤°à¥‡à¤‚à¥¤",

            "service.download.title":
                "à¤ˆ-à¤°à¤¾à¤¶à¤¨ à¤•à¤¾à¤°à¥à¤¡ à¤¡à¤¾à¤‰à¤¨à¤²à¥‹à¤¡ à¤•à¤°à¥‡à¤‚",

            "service.download.description":
                "à¤ªà¥à¤°à¤®à¤¾à¤£à¥€à¤•à¤°à¤£ à¤•à¥‡ à¤¬à¤¾à¤¦ à¤…à¤ªà¤¨à¤¾ à¤¡à¤¿à¤œà¤¿à¤Ÿà¤² à¤°à¤¾à¤¶à¤¨ à¤•à¤¾à¤°à¥à¤¡ à¤¸à¥à¤°à¤•à¥à¤·à¤¿à¤¤ à¤°à¥‚à¤ª à¤¸à¥‡ à¤ªà¥à¤°à¤¾à¤ªà¥à¤¤ à¤”à¤° à¤¡à¤¾à¤‰à¤¨à¤²à¥‹à¤¡ à¤•à¤°à¥‡à¤‚à¥¤",

            "service.fps.title":
                "à¤‰à¤šà¤¿à¤¤ à¤®à¥‚à¤²à¥à¤¯ à¤¦à¥à¤•à¤¾à¤¨ à¤–à¥‹à¤œà¥‡à¤‚",

            "service.fps.description":
                "à¤œà¤¿à¤²à¥‡, à¤•à¥à¤·à¥‡à¤¤à¥à¤° à¤”à¤° à¤‰à¤ªà¤²à¤¬à¥à¤§ à¤¸à¥‡à¤µà¤¾ à¤µà¤¿à¤µà¤°à¤£ à¤•à¥‡ à¤…à¤¨à¥à¤¸à¤¾à¤° à¤‰à¤šà¤¿à¤¤ à¤®à¥‚à¤²à¥à¤¯ à¤¦à¥à¤•à¤¾à¤¨ à¤•à¥€ à¤œà¤¾à¤¨à¤•à¤¾à¤°à¥€ à¤ªà¥à¤°à¤¾à¤ªà¥à¤¤ à¤•à¤°à¥‡à¤‚à¥¤",

            "service.grievance.title":
                "à¤¶à¤¿à¤•à¤¾à¤¯à¤¤ à¤¦à¤°à¥à¤œ à¤•à¤°à¥‡à¤‚",

            "service.grievance.description":
                "à¤¸à¥‡à¤µà¤¾ à¤¸à¤‚à¤¬à¤‚à¤§à¥€ à¤¶à¤¿à¤•à¤¾à¤¯à¤¤ à¤¦à¤°à¥à¤œ à¤•à¤°à¥‡à¤‚ à¤”à¤° à¤…à¤ªà¤¨à¥‡ à¤¨à¤¾à¤—à¤°à¤¿à¤• à¤…à¤¨à¥à¤°à¥‹à¤§ à¤•à¥€ à¤ªà¥à¤°à¤—à¤¤à¤¿ à¤¦à¥‡à¤–à¥‡à¤‚à¥¤",

            "service.entitlement.title":
                "à¤…à¤ªà¤¨à¥‡ à¤…à¤§à¤¿à¤•à¤¾à¤° à¤œà¤¾à¤¨à¥‡à¤‚",

            "service.entitlement.description":
                "à¤°à¤¾à¤¶à¤¨ à¤²à¤¾à¤­, à¤¸à¤¾à¤°à¥à¤µà¤œà¤¨à¤¿à¤• à¤µà¤¿à¤¤à¤°à¤£ à¤œà¤¾à¤¨à¤•à¤¾à¤°à¥€ à¤”à¤° à¤¨à¤¾à¤—à¤°à¤¿à¤• à¤…à¤§à¤¿à¤•à¤¾à¤°à¥‹à¤‚ à¤•à¥‹ à¤¸à¤®à¤à¥‡à¤‚à¥¤",

            "dashboard.kicker":
                "à¤ªà¥€à¤¡à¥€à¤à¤¸ à¤¡à¥ˆà¤¶à¤¬à¥‹à¤°à¥à¤¡",

            "dashboard.title":
                "à¤¸à¤¾à¤°à¥à¤µà¤œà¤¨à¤¿à¤• à¤µà¤¿à¤¤à¤°à¤£ à¤ªà¥à¤°à¤£à¤¾à¤²à¥€ à¤•à¤¾ à¤…à¤µà¤²à¥‹à¤•à¤¨",

            "dashboard.description":
                "à¤‡à¤¸ à¤¶à¥ˆà¤•à¥à¤·à¤£à¤¿à¤• à¤ªà¤°à¤¿à¤¯à¥‹à¤œà¤¨à¤¾ à¤‡à¤‚à¤Ÿà¤°à¤«à¥‡à¤¸ à¤•à¥‡ à¤²à¤¿à¤ à¤ªà¥à¤°à¤¦à¤°à¥à¤¶à¤¿à¤¤ à¤‰à¤¦à¤¾à¤¹à¤°à¤£à¤¾à¤¤à¥à¤®à¤• à¤†à¤à¤•à¤¡à¤¼à¥‡à¥¤",

            "dashboard.cards":
                "à¤•à¥à¤² à¤°à¤¾à¤¶à¤¨ à¤•à¤¾à¤°à¥à¤¡",

            "dashboard.rice":
                "à¤µà¤¿à¤¤à¤°à¤¿à¤¤ à¤šà¤¾à¤µà¤² (à¤•à¥à¤µà¤¿à¤‚à¤Ÿà¤²)",

            "dashboard.fps":
                "à¤¸à¤•à¥à¤°à¤¿à¤¯ à¤‰à¤šà¤¿à¤¤ à¤®à¥‚à¤²à¥à¤¯ à¤¦à¥à¤•à¤¾à¤¨à¥‡à¤‚",

            "dashboard.cardsGrowth": "+4.8%",
            "dashboard.riceGrowth": "+7.2%",
            "dashboard.fpsGrowth": "+2.6%",

            "dashboard.note":
                "* à¤Šà¤ªà¤° à¤¦à¤¿à¤–à¤¾à¤ à¤—à¤ à¤†à¤à¤•à¤¡à¤¼à¥‡ à¤‡à¤¸ à¤¶à¥ˆà¤•à¥à¤·à¤£à¤¿à¤• à¤ªà¥à¤°à¤¦à¤°à¥à¤¶à¤¨ à¤‡à¤‚à¤Ÿà¤°à¤«à¥‡à¤¸ à¤•à¥‡ à¤²à¤¿à¤ à¤‰à¤¦à¤¾à¤¹à¤°à¤£à¤¾à¤¤à¥à¤®à¤• à¤¹à¥ˆà¤‚à¥¤",

            "family.kicker":
                "à¤°à¤¾à¤·à¥à¤Ÿà¥à¤°à¥€à¤¯ à¤–à¤¾à¤¦à¥à¤¯ à¤¸à¥à¤°à¤•à¥à¤·à¤¾",

            "family.title":
                "à¤à¤• à¤®à¤œà¤¬à¥‚à¤¤",

            "family.titleStrong":
                "à¤­à¤¾à¤°à¤¤ à¤•à¥‡ à¤²à¤¿à¤ à¤­à¥‹à¤œà¤¨",

            "family.description":
                "à¤¨à¤¾à¤—à¤°à¤¿à¤•-à¤•à¥‡à¤‚à¤¦à¥à¤°à¤¿à¤¤ à¤¡à¤¿à¤œà¤¿à¤Ÿà¤² à¤…à¤¨à¥à¤­à¤µ à¤œà¥‹ à¤°à¤¾à¤¶à¤¨ à¤¸à¥‡à¤µà¤¾à¤“à¤‚, à¤ªà¤°à¤¿à¤µà¤¾à¤° à¤•à¥€ à¤œà¤¾à¤¨à¤•à¤¾à¤°à¥€ à¤”à¤° à¤¸à¤¾à¤°à¥à¤µà¤œà¤¨à¤¿à¤• à¤µà¤¿à¤¤à¤°à¤£ à¤ªà¥à¤°à¤•à¥à¤°à¤¿à¤¯à¤¾à¤“à¤‚ à¤•à¥‹ à¤œà¥‹à¤¡à¤¼à¤¤à¤¾ à¤¹à¥ˆà¥¤",

            "family.slogan":
                "à¤à¤• à¤®à¤œà¤¬à¥‚à¤¤ à¤­à¤¾à¤°à¤¤ à¤•à¥‡ à¤²à¤¿à¤ à¤­à¥‹à¤œà¤¨",

            "announcement.title":
                "à¤˜à¥‹à¤·à¤£à¤¾à¤à¤",

            "announcement.verify":
                "à¤¸à¥‡à¤µà¤¾ à¤†à¤µà¥‡à¤¦à¤¨ à¤œà¤®à¤¾ à¤•à¤°à¤¨à¥‡ à¤¸à¥‡ à¤ªà¤¹à¤²à¥‡ à¤¨à¤¾à¤—à¤°à¤¿à¤•à¥‹à¤‚ à¤•à¥‹ à¤…à¤ªà¤¨à¥‡ à¤ªà¤°à¤¿à¤µà¤¾à¤° à¤•à¤¾ à¤µà¤¿à¤µà¤°à¤£ à¤¸à¤¤à¥à¤¯à¤¾à¤ªà¤¿à¤¤ à¤•à¤°à¤¨à¥‡ à¤•à¥‡ à¤²à¤¿à¤ à¤ªà¥à¤°à¥‹à¤¤à¥à¤¸à¤¾à¤¹à¤¿à¤¤ à¤•à¤¿à¤¯à¤¾ à¤œà¤¾à¤¤à¤¾ à¤¹à¥ˆà¥¤",

            "announcement.check":
                "à¤¨à¤¾à¤—à¤°à¤¿à¤• à¤ªà¥‹à¤°à¥à¤Ÿà¤² à¤•à¥‡ à¤®à¤¾à¤§à¥à¤¯à¤® à¤¸à¥‡ à¤…à¤ªà¤¨à¥‡ à¤°à¤¾à¤¶à¤¨ à¤²à¤¾à¤­ à¤”à¤° à¤†à¤µà¥‡à¤¦à¤¨ à¤•à¥€ à¤¸à¥à¤¥à¤¿à¤¤à¤¿ à¤¦à¥‡à¤–à¥‡à¤‚à¥¤",

            "feature.transparent.title":
                "à¤ªà¤¾à¤°à¤¦à¤°à¥à¤¶à¥€ à¤”à¤° à¤œà¤µà¤¾à¤¬à¤¦à¥‡à¤¹",

            "feature.transparent.description":
                "à¤¸à¥à¤ªà¤·à¥à¤Ÿ à¤¸à¥‡à¤µà¤¾ à¤ªà¥à¤°à¤•à¥à¤°à¤¿à¤¯à¤¾à¤à¤, à¤¸à¥à¤¥à¤¿à¤¤à¤¿ à¤•à¥€ à¤œà¤¾à¤¨à¤•à¤¾à¤°à¥€ à¤”à¤° à¤œà¤¿à¤®à¥à¤®à¥‡à¤¦à¤¾à¤° à¤¡à¤¿à¤œà¤¿à¤Ÿà¤² à¤•à¤¾à¤°à¥à¤¯à¤ªà¥à¤°à¤µà¤¾à¤¹à¥¤",

            "feature.digital.title":
                "à¤¡à¤¿à¤œà¤¿à¤Ÿà¤² à¤‡à¤‚à¤¡à¤¿à¤¯à¤¾",

            "feature.digital.description":
                "à¤¸à¤°à¤² à¤”à¤° à¤¸à¥à¤²à¤­ à¤¨à¤¾à¤—à¤°à¤¿à¤• à¤…à¤¨à¥à¤­à¤µ à¤•à¥‡ à¤²à¤¿à¤ à¤†à¤§à¥à¤¨à¤¿à¤• à¤µà¥‡à¤¬ à¤¸à¥‡à¤µà¤¾à¤à¤à¥¤",

            "feature.access.title":
                "à¤¸à¤­à¥€ à¤•à¥‡ à¤²à¤¿à¤ à¤†à¤¸à¤¾à¤¨ à¤ªà¤¹à¥à¤à¤š",

            "feature.access.description":
                "à¤µà¤¿à¤­à¤¿à¤¨à¥à¤¨ à¤‰à¤ªà¤•à¤°à¤£à¥‹à¤‚ à¤•à¥‡ à¤²à¤¿à¤ à¤‰à¤¤à¥à¤¤à¤°à¤¦à¤¾à¤¯à¥€, à¤ªà¤ à¤¨à¥€à¤¯ à¤”à¤° à¤¸à¥à¤²à¤­ à¤‡à¤‚à¤Ÿà¤°à¤«à¥‡à¤¸à¥¤",

            "feature.garib.title":
                "à¤—à¤°à¥€à¤¬ à¤•à¤²à¥à¤¯à¤¾à¤£ à¤¹à¤®à¤¾à¤°à¥€ à¤ªà¥à¤°à¤¾à¤¥à¤®à¤¿à¤•à¤¤à¤¾",

            "feature.garib.description":
                "à¤¡à¤¿à¤œà¤¼à¤¾à¤‡à¤¨ à¤–à¤¾à¤¦à¥à¤¯ à¤¸à¥à¤°à¤•à¥à¤·à¤¾ à¤”à¤° à¤¨à¤¾à¤—à¤°à¤¿à¤•-à¤•à¥‡à¤‚à¤¦à¥à¤°à¤¿à¤¤ à¤¸à¤¾à¤°à¥à¤µà¤œà¤¨à¤¿à¤• à¤¸à¥‡à¤µà¤¾à¤“à¤‚ à¤ªà¤° à¤œà¥‹à¤° à¤¦à¥‡à¤¤à¤¾ à¤¹à¥ˆà¥¤",

            "feature.bannerKicker":
                "à¤–à¤¾à¤¦à¥à¤¯ à¤¸à¥à¤°à¤•à¥à¤·à¤¾ à¤•à¥‡ à¤²à¤¿à¤ à¤¸à¤¾à¤¥-à¤¸à¤¾à¤¥",

            "feature.jaiHind":
                "à¤œà¤¯ à¤¹à¤¿à¤¨à¥à¤¦",

            "feature.bannerDescription":
                "à¤­à¤¾à¤°à¤¤ à¤•à¥‡ à¤¤à¤¿à¤°à¤‚à¤—à¥‡, à¤§à¤¾à¤¨ à¤•à¥€ à¤–à¥‡à¤¤à¥€ à¤”à¤° à¤¨à¤¾à¤—à¤°à¤¿à¤•-à¤•à¥‡à¤‚à¤¦à¥à¤°à¤¿à¤¤ à¤¸à¤¾à¤°à¥à¤µà¤œà¤¨à¤¿à¤• à¤¸à¥‡à¤µà¤¾ à¤®à¥‚à¤²à¥à¤¯à¥‹à¤‚ à¤¸à¥‡ à¤ªà¥à¤°à¥‡à¤°à¤¿à¤¤ à¤†à¤§à¥à¤¨à¤¿à¤• à¤¡à¤¿à¤œà¤¿à¤Ÿà¤² à¤…à¤¨à¥à¤­à¤µà¥¤",

            "feature.bannerBottom":
                "ðŸ‡®ðŸ‡³ à¤­à¥‹à¤œà¤¨ â€¢ à¤ªà¤°à¤¿à¤µà¤¾à¤° â€¢ à¤­à¤µà¤¿à¤·à¥à¤¯",

            "notice.title":
                "à¤®à¤¹à¤¤à¥à¤µà¤ªà¥‚à¤°à¥à¤£ à¤¸à¥‚à¤šà¤¨à¤¾",

            "notice.text":
                "à¤¯à¤¹ à¤ªà¥‹à¤°à¥à¤Ÿà¤² à¤µà¥‡à¤¬ à¤¤à¤•à¤¨à¥€à¤•à¥‹à¤‚ à¤•à¤¾ à¤‰à¤ªà¤¯à¥‹à¤— à¤•à¤°à¤•à¥‡ à¤†à¤§à¥à¤¨à¤¿à¤• à¤¸à¤¾à¤°à¥à¤µà¤œà¤¨à¤¿à¤• à¤µà¤¿à¤¤à¤°à¤£ à¤ªà¥à¤°à¤£à¤¾à¤²à¥€ à¤…à¤¨à¥à¤­à¤µ à¤•à¥‹ à¤ªà¥à¤°à¤¦à¤°à¥à¤¶à¤¿à¤¤ à¤•à¤°à¤¨à¥‡ à¤•à¥‡ à¤²à¤¿à¤ à¤¬à¤¨à¤¾à¤¯à¤¾ à¤—à¤¯à¤¾ à¤à¤• à¤¶à¥ˆà¤•à¥à¤·à¤£à¤¿à¤• à¤¸à¥‰à¤«à¥à¤Ÿà¤µà¥‡à¤¯à¤° à¤ªà¥à¤°à¥‹à¤œà¥‡à¤•à¥à¤Ÿ à¤¹à¥ˆà¥¤",

            "notice.warning":
                "à¤¯à¤¹ à¤ªà¤°à¤¿à¤¯à¥‹à¤œà¤¨à¤¾ à¤­à¤¾à¤°à¤¤ à¤¸à¤°à¤•à¤¾à¤° à¤•à¥€ à¤†à¤§à¤¿à¤•à¤¾à¤°à¤¿à¤• à¤µà¥‡à¤¬à¤¸à¤¾à¤‡à¤Ÿ à¤¨à¤¹à¥€à¤‚ à¤¹à¥ˆà¥¤ à¤†à¤§à¤¿à¤•à¤¾à¤°à¤¿à¤• à¤¸à¤°à¤•à¤¾à¤°à¥€ à¤šà¤¿à¤¹à¥à¤¨, à¤®à¥à¤¹à¤° à¤”à¤° à¤ªà¤¹à¤šà¤¾à¤¨ à¤†à¤§à¤¿à¤•à¤¾à¤°à¤¿à¤• à¤ªà¥à¤°à¤¤à¤¿à¤¨à¤¿à¤§à¤¿à¤¤à¥à¤µ à¤•à¥‡ à¤°à¥‚à¤ª à¤®à¥‡à¤‚ à¤‰à¤ªà¤¯à¥‹à¤— à¤¨à¤¹à¥€à¤‚ à¤•à¤¿à¤ à¤—à¤ à¤¹à¥ˆà¤‚à¥¤",

            "footer.name":
                "à¤°à¤¾à¤¶à¤¨ à¤ªà¥‹à¤°à¥à¤Ÿà¤²",

            "footer.pds":
                "à¤¸à¤¾à¤°à¥à¤µà¤œà¤¨à¤¿à¤• à¤µà¤¿à¤¤à¤°à¤£ à¤ªà¥à¤°à¤£à¤¾à¤²à¥€",

            "footer.about":
                "à¤¹à¤®à¤¾à¤°à¥‡ à¤¬à¤¾à¤°à¥‡ à¤®à¥‡à¤‚",

            "footer.privacy":
                "à¤—à¥‹à¤ªà¤¨à¥€à¤¯à¤¤à¤¾ à¤¨à¥€à¤¤à¤¿",

            "footer.terms":
                "à¤¨à¤¿à¤¯à¤® à¤”à¤° à¤¶à¤°à¥à¤¤à¥‡à¤‚",

            "footer.contact":
                "à¤¸à¤‚à¤ªà¤°à¥à¤• à¤•à¤°à¥‡à¤‚",

            "footer.transparency":
                "à¤ªà¤¾à¤°à¤¦à¤°à¥à¤¶à¤¿à¤¤à¤¾",

            "footer.educational":
                "à¤¶à¥ˆà¤•à¥à¤·à¤£à¤¿à¤• à¤ªà¥à¤°à¤¦à¤°à¥à¤¶à¤¨ à¤ªà¤°à¤¿à¤¯à¥‹à¤œà¤¨à¤¾à¥¤",

            "footer.designed":
                "ðŸ‡®ðŸ‡³ à¤–à¤¾à¤¦à¥à¤¯ à¤¸à¥à¤°à¤•à¥à¤·à¤¾ à¤µà¤¾à¤²à¥‡ à¤­à¤¾à¤°à¤¤ à¤•à¥‡ à¤²à¤¿à¤ à¤¬à¤¨à¤¾à¤¯à¤¾ à¤—à¤¯à¤¾",

            "language.english":
                "English",

            "language.hindi":
                "à¤¹à¤¿à¤¨à¥à¤¦à¥€",

            "language.telugu":
                "à°¤à±†à°²à±à°—à±",

            "language.select":
                "à¤­à¤¾à¤·à¤¾ à¤šà¥à¤¨à¥‡à¤‚"
        },


        /* ========================================================
           TELUGU
        ======================================================== */
        te: {

            "site.name":
                "à°°à±‡à°·à°¨à± à°ªà±‹à°°à±à°Ÿà°²à±",

            "site.subtitle":
                "à°¡à°¿à°œà°¿à°Ÿà°²à± à°ªà±à°°à°œà°¾ à°ªà°‚à°ªà°¿à°£à±€ à°µà±à°¯à°µà°¸à±à°¥",

            "access.government":
                "à°ªà±à°°à°­à±à°¤à±à°µ à°¸à±‡à°µà°¾ à°ªà±‹à°°à±à°Ÿà°²à±",

            "access.digitalPds":
                "à°¡à°¿à°œà°¿à°Ÿà°²à± à°ªà±à°°à°œà°¾ à°ªà°‚à°ªà°¿à°£à±€ à°µà±à°¯à°µà°¸à±à°¥",

            "access.skip":
                "à°ªà±à°°à°§à°¾à°¨ à°µà°¿à°·à°¯à°¾à°¨à°¿à°•à°¿ à°µà±†à°³à±à°²à°‚à°¡à°¿",

            "brand.gov":
                "à°ªà±à°°à°­à±à°¤à±à°µ à°¶à±ˆà°²à°¿ à°ªà±Œà°° à°¸à±‡à°µà°¾ à°ªà±‹à°°à±à°Ÿà°²à±",

            "brand.name":
                "à°°à±‡à°·à°¨à± à°ªà±‹à°°à±à°Ÿà°²à±",

            "brand.pds":
                "à°ªà±à°°à°œà°¾ à°ªà°‚à°ªà°¿à°£à±€ à°µà±à°¯à°µà°¸à±à°¥",

            "brand.tagline":
                "à°†à°¹à°¾à°° à°­à°¦à±à°°à°¤ â€¢ à°¸à°¾à°§à°¿à°•à°¾à°°à°¤ â€¢ à°…à°­à°¿à°µà±ƒà°¦à±à°§à°¿",

            "header.slogan":
                "à°ªà±à°°à°¤à°¿ à°‡à°‚à°Ÿà°¿à°•à°¿ à°…à°¨à±à°¨à°‚, à°ªà±à°°à°¤à°¿ à°‡à°‚à°Ÿà°¿à°•à°¿ à°­à°¦à±à°°à°¤",

            "header.service":
                "à°¡à°¿à°œà°¿à°Ÿà°²à± à°ªà±à°°à°œà°¾ à°ªà°‚à°ªà°¿à°£à±€ à°¸à±‡à°µà°² à°¦à±à°µà°¾à°°à°¾ à°ªà±Œà°°à±à°²à°•à± à°¸à±‡à°µà°²à±",

            "auth.login":
                "à°ªà±Œà°° à°²à°¾à°—à°¿à°¨à±",

            "auth.register":
                "à°¨à°®à±‹à°¦à±",

            "nav.home":
                "à°¹à±‹à°®à±",

            "nav.services":
                "à°¸à±‡à°µà°²à±",

            "nav.rationCard":
                "à°°à±‡à°·à°¨à± à°•à°¾à°°à±à°¡à±",

            "nav.fps":
                "à°¨à±à°¯à°¾à°¯ à°§à°°à°² à°¦à±à°•à°¾à°£à°‚",

            "nav.help":
                "à°¸à°¹à°¾à°¯à°‚",

            "nav.transparency":
                "à°ªà°¾à°°à°¦à°°à±à°¶à°•à°¤",

            "hero.kicker":
                "à°ªà±Œà°° à°¡à°¿à°œà°¿à°Ÿà°²à± à°¸à±‡à°µà°¾ à°ªà±‹à°°à±à°Ÿà°²à±",

            "hero.rightFood":
                "à°¸à°°à±ˆà°¨ à°†à°¹à°¾à°°à°‚",

            "hero.rightPeople":
                "à°¸à°°à±ˆà°¨ à°ªà±à°°à°œà°²à±",

            "hero.rightTime":
                "à°¸à°°à±ˆà°¨ à°¸à°®à°¯à°‚",

            "hero.subtitle":
                "à°ªà±à°°à°œà°¾ à°ªà°‚à°ªà°¿à°£à±€ à°µà±à°¯à°µà°¸à±à°¥ â€“ à°ªà±à°°à°­à±à°¤à±à°µ à°ªà±à°°à±‡à°°à°£à°¤à±‹ à°°à±‚à°ªà±Šà°‚à°¦à°¿à°‚à°šà°¿à°¨ à°¡à°¿à°œà°¿à°Ÿà°²à± à°¸à±‡à°µà°¾ à°…à°¨à±à°­à°µà°‚",

            "hero.tagline":
                "à°…à°‚à°¦à±à°¬à°¾à°Ÿà±à°²à±‹ à°‰à°‚à°¡à±‡, à°ªà°¾à°°à°¦à°°à±à°¶à°•à°®à±ˆà°¨ à°®à°°à°¿à°¯à± à°ªà±Œà°° à°•à±‡à°‚à°¦à±à°°à°¿à°¤ à°¡à°¿à°œà°¿à°Ÿà°²à± à°¸à±‡à°µà°² à°¦à±à°µà°¾à°°à°¾ à°ªà±à°°à°¤à°¿ à°ªà±Œà°°à±à°¡à°¿à°•à°¿ à°†à°¹à°¾à°° à°­à°¦à±à°°à°¤à°¨à± à°…à°‚à°¦à°¿à°‚à°šà°¡à°‚.",

            "hero.chip.food":
                "à°…à°‚à°¦à°°à°¿à°•à±€ à°†à°¹à°¾à°° à°­à°¦à±à°°à°¤",

            "hero.chip.transparent":
                "à°ªà°¾à°°à°¦à°°à±à°¶à°• à°µà±à°¯à°µà°¸à±à°¥",

            "hero.chip.digital":
                "à°¡à°¿à°œà°¿à°Ÿà°²à± à°‡à°‚à°¡à°¿à°¯à°¾",

            "hero.chip.access":
                "à°¸à±à°²à°­à°®à±ˆà°¨ à°ªà±à°°à°¾à°ªà±à°¯à°¤",

            "hero.familySmall":
                "à°ªà±Œà°° à°•à±‡à°‚à°¦à±à°°à°¿à°¤ à°ªà±à°°à°œà°¾ à°ªà°‚à°ªà°¿à°£à±€",

            "hero.familyMera":
                "à°¨à°¾ à°°à±‡à°·à°¨à± à°¨à°¾ à°¹à°•à±à°•à±",

            "hero.familyMeraWord":
                "à°¨à°¾ à°°à±‡à°·à°¨à±",

            "hero.familyAdhikar":
                "à°¨à°¾ à°¹à°•à±à°•à±",

            "search.label":
                "à°°à±‡à°·à°¨à± à°•à°¾à°°à±à°¡à± à°¨à°‚à°¬à°°à±, à°°à±‡à°·à°¨à± à°à°¡à°¿ à°²à±‡à°¦à°¾ à°¸à°­à±à°¯à±à°¨à°¿ à°à°¡à°¿",

            "search.placeholder":
                "à°°à±‡à°·à°¨à± à°•à°¾à°°à±à°¡à± à°¨à°‚à°¬à°°à± / à°°à±‡à°·à°¨à± à°à°¡à°¿ / à°¸à°­à±à°¯à±à°¨à°¿ à°à°¡à°¿ à°¨à°®à±‹à°¦à± à°šà±‡à°¯à°‚à°¡à°¿",

            "search.button":
                "à°µà±†à°¤à°•à°‚à°¡à°¿",

            "services.kicker":
                "à°ªà±Œà°° à°¸à±‡à°µà°²à±",

            "services.title":
                "à°…à°µà°¸à°°à°®à±ˆà°¨ à°¸à±‡à°µà°²à± à°’à°•à±‡ à°šà±‹à°Ÿ",

            "services.description":
                "à°°à±‡à°·à°¨à± à°•à°¾à°°à±à°¡à±, à°«à°¿à°°à±à°¯à°¾à°¦à±, à°¨à±à°¯à°¾à°¯ à°§à°°à°² à°¦à±à°•à°¾à°£à°‚ à°®à°°à°¿à°¯à± à°¹à°•à±à°•à±à°² à°¸à±‡à°µà°²à°•à± à°¸à±à°²à°­à°®à±ˆà°¨ à°¡à°¿à°œà°¿à°Ÿà°²à± à°ªà±à°°à°¾à°ªà±à°¯à°¤.",

            "services.applications":
                "à°¨à°¾ à°¦à°°à°–à°¾à°¸à±à°¤à±à°²à±",

            "service.apply.title":
                "à°•à±Šà°¤à±à°¤ à°°à±‡à°·à°¨à± à°•à°¾à°°à±à°¡à± à°•à±‹à°¸à°‚ à°¦à°°à°–à°¾à°¸à±à°¤à±",

            "service.apply.description":
                "à°®à°¾à°°à±à°—à°¦à°°à±à°¶à°• à°ªà±Œà°° à°¦à°°à°–à°¾à°¸à±à°¤à± à°ªà±à°°à°•à±à°°à°¿à°¯ à°¦à±à°µà°¾à°°à°¾ à°•à±Šà°¤à±à°¤ à°°à±‡à°·à°¨à± à°•à°¾à°°à±à°¡à±à°¨à± à°†à°¨à±â€Œà°²à±ˆà°¨à±â€Œà°²à±‹ à°ªà±Šà°‚à°¦à°‚à°¡à°¿.",

            "service.download.title":
                "à°ˆ-à°°à±‡à°·à°¨à± à°•à°¾à°°à±à°¡à± à°¡à±Œà°¨à±â€Œà°²à±‹à°¡à±",

            "service.download.description":
                "à°§à±ƒà°µà±€à°•à°°à°£ à°¤à°°à±à°µà°¾à°¤ à°®à±€ à°¡à°¿à°œà°¿à°Ÿà°²à± à°°à±‡à°·à°¨à± à°•à°¾à°°à±à°¡à±à°¨à± à°¸à±à°°à°•à±à°·à°¿à°¤à°‚à°—à°¾ à°ªà±Šà°‚à°¦à°‚à°¡à°¿ à°®à°°à°¿à°¯à± à°¡à±Œà°¨à±â€Œà°²à±‹à°¡à± à°šà±‡à°¯à°‚à°¡à°¿.",

            "service.fps.title":
                "à°¨à±à°¯à°¾à°¯ à°§à°°à°² à°¦à±à°•à°¾à°£à°¾à°¨à±à°¨à°¿ à°•à°¨à±à°—à±Šà°¨à°‚à°¡à°¿",

            "service.fps.description":
                "à°œà°¿à°²à±à°²à°¾, à°ªà±à°°à°¾à°‚à°¤à°‚ à°®à°°à°¿à°¯à± à°…à°‚à°¦à±à°¬à°¾à°Ÿà±à°²à±‹ à°‰à°¨à±à°¨ à°¸à±‡à°µà°² à°µà°¿à°µà°°à°¾à°² à°ªà±à°°à°•à°¾à°°à°‚ à°¨à±à°¯à°¾à°¯ à°§à°°à°² à°¦à±à°•à°¾à°£à°‚ à°¸à°®à°¾à°šà°¾à°°à°¾à°¨à±à°¨à°¿ à°ªà±Šà°‚à°¦à°‚à°¡à°¿.",

            "service.grievance.title":
                "à°«à°¿à°°à±à°¯à°¾à°¦à± à°¨à°®à±‹à°¦à± à°šà±‡à°¯à°‚à°¡à°¿",

            "service.grievance.description":
                "à°¸à±‡à°µà°•à± à°¸à°‚à°¬à°‚à°§à°¿à°‚à°šà°¿à°¨ à°«à°¿à°°à±à°¯à°¾à°¦à±à°¨à± à°¨à°®à±‹à°¦à± à°šà±‡à°¸à°¿ à°®à±€ à°ªà±Œà°° à°…à°­à±à°¯à°°à±à°¥à°¨ à°ªà±à°°à±‹à°—à°¤à°¿à°¨à°¿ à°…à°¨à±à°¸à°°à°¿à°‚à°šà°‚à°¡à°¿.",

            "service.entitlement.title":
                "à°®à±€ à°¹à°•à±à°•à±à°²à°¨à± à°¤à±†à°²à±à°¸à±à°•à±‹à°‚à°¡à°¿",

            "service.entitlement.description":
                "à°°à±‡à°·à°¨à± à°ªà±à°°à°¯à±‹à°œà°¨à°¾à°²à±, à°ªà±à°°à°œà°¾ à°ªà°‚à°ªà°¿à°£à±€ à°¸à°®à°¾à°šà°¾à°°à°‚ à°®à°°à°¿à°¯à± à°ªà±Œà°° à°¹à°•à±à°•à±à°²à°¨à± à°…à°°à±à°¥à°‚ à°šà±‡à°¸à±à°•à±‹à°‚à°¡à°¿.",

            "dashboard.kicker":
                "à°ªà±€à°¡à±€à°Žà°¸à± à°¡à±à°¯à°¾à°·à±â€Œà°¬à±‹à°°à±à°¡à±",

            "dashboard.title":
                "à°ªà±à°°à°œà°¾ à°ªà°‚à°ªà°¿à°£à±€ à°µà±à°¯à°µà°¸à±à°¥ à°…à°µà°²à±‹à°•à°¨à°‚",

            "dashboard.description":
                "à°ˆ à°µà°¿à°¦à±à°¯à°¾ à°ªà±à°°à°¾à°œà±†à°•à±à°Ÿà± à°‡à°‚à°Ÿà°°à±â€Œà°«à±‡à°¸à± à°•à±‹à°¸à°‚ à°šà±‚à°ªà°¬à°¡à°¿à°¨ à°‰à°¦à°¾à°¹à°°à°£ à°—à°£à°¾à°‚à°•à°¾à°²à±.",

            "dashboard.cards":
                "à°®à±Šà°¤à±à°¤à°‚ à°°à±‡à°·à°¨à± à°•à°¾à°°à±à°¡à±à°²à±",

            "dashboard.rice":
                "à°ªà°‚à°ªà°¿à°£à±€ à°šà±‡à°¸à°¿à°¨ à°¬à°¿à°¯à±à°¯à°‚ (à°•à±à°µà°¿à°‚à°Ÿà°¾à°³à±à°²à±)",

            "dashboard.fps":
                "à°•à±à°°à°¿à°¯à°¾à°¶à±€à°² à°¨à±à°¯à°¾à°¯ à°§à°°à°² à°¦à±à°•à°¾à°£à°¾à°²à±",

            "dashboard.cardsGrowth":
                "+4.8%",

            "dashboard.riceGrowth":
                "+7.2%",

            "dashboard.fpsGrowth":
                "+2.6%",

            "dashboard.note":
                "* à°ªà±ˆ à°—à°£à°¾à°‚à°•à°¾à°²à± à°ˆ à°µà°¿à°¦à±à°¯à°¾ à°ªà±à°°à°¦à°°à±à°¶à°¨ à°‡à°‚à°Ÿà°°à±â€Œà°«à±‡à°¸à± à°•à±‹à°¸à°‚ à°šà±‚à°ªà°¬à°¡à°¿à°¨ à°‰à°¦à°¾à°¹à°°à°£ à°µà°¿à°²à±à°µà°²à± à°®à°¾à°¤à±à°°à°®à±‡.",

            "family.kicker":
                "à°œà°¾à°¤à±€à°¯ à°†à°¹à°¾à°° à°­à°¦à±à°°à°¤",

            "family.title":
                "à°’à°• à°¬à°²à°®à±ˆà°¨",

            "family.titleStrong":
                "à°­à°¾à°°à°¤à°¦à±‡à°¶à°‚ à°•à±‹à°¸à°‚ à°†à°¹à°¾à°°à°‚",

            "family.description":
                "à°°à±‡à°·à°¨à± à°¸à±‡à°µà°²à±, à°•à±à°Ÿà±à°‚à°¬ à°¸à°®à°¾à°šà°¾à°°à°‚ à°®à°°à°¿à°¯à± à°ªà±à°°à°œà°¾ à°ªà°‚à°ªà°¿à°£à±€ à°ªà±à°°à°•à±à°°à°¿à°¯à°²à°¨à± à°•à°²à°¿à°ªà±‡ à°ªà±Œà°° à°•à±‡à°‚à°¦à±à°°à°¿à°¤ à°¡à°¿à°œà°¿à°Ÿà°²à± à°…à°¨à±à°­à°µà°‚.",

            "family.slogan":
                "à°’à°• à°¬à°²à°®à±ˆà°¨ à°­à°¾à°°à°¤à°¦à±‡à°¶à°‚ à°•à±‹à°¸à°‚ à°†à°¹à°¾à°°à°‚",

            "announcement.title":
                "à°ªà±à°°à°•à°Ÿà°¨à°²à±",

            "announcement.verify":
                "à°¸à±‡à°µà°¾ à°¦à°°à°–à°¾à°¸à±à°¤à±à°²à°¨à± à°¸à°®à°°à±à°ªà°¿à°‚à°šà±‡ à°®à±à°‚à°¦à± à°•à±à°Ÿà±à°‚à°¬ à°µà°¿à°µà°°à°¾à°²à°¨à± à°§à±ƒà°µà±€à°•à°°à°¿à°‚à°šà±à°•à±‹à°µà°¾à°²à°¨à°¿ à°ªà±Œà°°à±à°²à°¨à± à°ªà±à°°à±‹à°¤à±à°¸à°¹à°¿à°¸à±à°¤à±à°¨à±à°¨à°¾à°®à±.",

            "announcement.check":
                "à°ªà±Œà°° à°ªà±‹à°°à±à°Ÿà°²à± à°¦à±à°µà°¾à°°à°¾ à°®à±€ à°°à±‡à°·à°¨à± à°¹à°•à±à°•à±à°²à± à°®à°°à°¿à°¯à± à°¦à°°à°–à°¾à°¸à±à°¤à± à°¸à±à°¥à°¿à°¤à°¿à°¨à°¿ à°ªà°°à°¿à°¶à±€à°²à°¿à°‚à°šà°‚à°¡à°¿.",

            "feature.transparent.title":
                "à°ªà°¾à°°à°¦à°°à±à°¶à°•à°¤ à°®à°°à°¿à°¯à± à°¬à°¾à°§à±à°¯à°¤",

            "feature.transparent.description":
                "à°¸à±à°ªà°·à±à°Ÿà°®à±ˆà°¨ à°¸à±‡à°µà°¾ à°ªà±à°°à°•à±à°°à°¿à°¯à°²à±, à°¸à±à°¥à°¿à°¤à°¿ à°•à°¨à°¿à°ªà°¿à°‚à°šà±‡ à°µà°¿à°§à°¾à°¨à°‚ à°®à°°à°¿à°¯à± à°¬à°¾à°§à±à°¯à°¤à°¾à°¯à±à°¤à°®à±ˆà°¨ à°¡à°¿à°œà°¿à°Ÿà°²à± à°µà°°à±à°•à±â€Œà°«à±à°²à±‹à°²à±.",

            "feature.digital.title":
                "à°¡à°¿à°œà°¿à°Ÿà°²à± à°‡à°‚à°¡à°¿à°¯à°¾",

            "feature.digital.description":
                "à°¸à±à°²à°­à°®à±ˆà°¨ à°®à°°à°¿à°¯à± à°…à°‚à°¦à±à°¬à°¾à°Ÿà±à°²à±‹ à°‰à°‚à°¡à±‡ à°ªà±Œà°° à°ªà°°à°¸à±à°ªà°° à°šà°°à±à°¯à°² à°•à±‹à°¸à°‚ à°†à°§à±à°¨à°¿à°• à°µà±†à°¬à± à°¸à±‡à°µà°²à±.",

            "feature.access.title":
                "à°…à°‚à°¦à°°à°¿à°•à±€ à°¸à±à°²à°­à°®à±ˆà°¨ à°ªà±à°°à°¾à°ªà±à°¯à°¤",

            "feature.access.description":
                "à°µà°¿à°µà°¿à°§ à°ªà°°à°¿à°•à°°à°¾à°² à°•à±‹à°¸à°‚ à°¸à±à°ªà°‚à°¦à°¿à°‚à°šà±‡, à°šà°¦à°µà°—à°²à°¿à°—à±‡ à°®à°°à°¿à°¯à± à°ªà±à°°à°¾à°ªà±à°¯à°¤à°•à± à°…à°¨à±à°•à±‚à°²à°®à±ˆà°¨ à°‡à°‚à°Ÿà°°à±â€Œà°«à±‡à°¸à±â€Œà°²à±.",

            "feature.garib.title":
                "à°—à°°à±€à°¬à± à°•à°³à±à°¯à°¾à°£à± à°®à°¾ à°ªà±à°°à°¾à°§à°¾à°¨à±à°¯à°¤",

            "feature.garib.description":
                "à°ˆ à°¡à°¿à°œà±ˆà°¨à± à°†à°¹à°¾à°° à°­à°¦à±à°°à°¤ à°®à°°à°¿à°¯à± à°ªà±Œà°° à°•à±‡à°‚à°¦à±à°°à°¿à°¤ à°ªà±à°°à°œà°¾ à°¸à±‡à°µà°²à°•à± à°ªà±à°°à°¾à°§à°¾à°¨à±à°¯à°‚ à°‡à°¸à±à°¤à±à°‚à°¦à°¿.",

            "feature.bannerKicker":
                "à°†à°¹à°¾à°° à°­à°¦à±à°°à°¤ à°•à±‹à°¸à°‚ à°•à°²à°¿à°¸à°¿",

            "feature.jaiHind":
                "à°œà±ˆ à°¹à°¿à°‚à°¦à±",

            "feature.bannerDescription":
                "à°­à°¾à°°à°¤ à°¤à±à°°à°¿à°µà°°à±à°£ à°ªà°¤à°¾à°•à°‚, à°µà°°à°¿ à°¸à°¾à°—à± à°®à°°à°¿à°¯à± à°ªà±Œà°° à°•à±‡à°‚à°¦à±à°°à°¿à°¤ à°ªà±à°°à°œà°¾ à°¸à±‡à°µà°¾ à°µà°¿à°²à±à°µà°² à°¨à±à°‚à°¡à°¿ à°ªà±à°°à±‡à°°à°£ à°ªà±Šà°‚à°¦à°¿à°¨ à°†à°§à±à°¨à°¿à°• à°¡à°¿à°œà°¿à°Ÿà°²à± à°…à°¨à±à°­à°µà°‚.",

            "feature.bannerBottom":
                "ðŸ‡®ðŸ‡³ à°†à°¹à°¾à°°à°‚ â€¢ à°•à±à°Ÿà±à°‚à°¬à°‚ â€¢ à°­à°µà°¿à°·à±à°¯à°¤à±à°¤à±",

            "notice.title":
                "à°®à±à°–à±à°¯à°®à±ˆà°¨ à°—à°®à°¨à°¿à°•",

            "notice.text":
                "à°ˆ à°ªà±‹à°°à±à°Ÿà°²à± à°µà±†à°¬à± à°¸à°¾à°‚à°•à±‡à°¤à°¿à°•à°¤à°²à°¨à± à°‰à°ªà°¯à±‹à°—à°¿à°‚à°šà°¿ à°†à°§à±à°¨à°¿à°• à°ªà±à°°à°œà°¾ à°ªà°‚à°ªà°¿à°£à±€ à°µà±à°¯à°µà°¸à±à°¥ à°…à°¨à±à°­à°µà°¾à°¨à±à°¨à°¿ à°ªà±à°°à°¦à°°à±à°¶à°¿à°‚à°šà°¡à°¾à°¨à°¿à°•à°¿ à°°à±‚à°ªà±Šà°‚à°¦à°¿à°‚à°šà°¿à°¨ à°µà°¿à°¦à±à°¯à°¾ à°¸à°¾à°«à±à°Ÿà±â€Œà°µà±‡à°°à± à°ªà±à°°à°¾à°œà±†à°•à±à°Ÿà±.",

            "notice.warning":
                "à°ˆ à°ªà±à°°à°¾à°œà±†à°•à±à°Ÿà± à°­à°¾à°°à°¤ à°ªà±à°°à°­à±à°¤à±à°µ à°…à°§à°¿à°•à°¾à°°à°¿à°• à°µà±†à°¬à±â€Œà°¸à±ˆà°Ÿà± à°•à°¾à°¦à±. à°…à°§à°¿à°•à°¾à°°à°¿à°• à°ªà±à°°à°­à±à°¤à±à°µ à°šà°¿à°¹à±à°¨à°¾à°²à±, à°®à±à°¦à±à°°à°²à± à°®à°°à°¿à°¯à± à°—à±à°°à±à°¤à°¿à°‚à°ªà±à°²à± à°…à°§à°¿à°•à°¾à°°à°¿à°• à°ªà±à°°à°¤à°¿à°¨à°¿à°§à°¿à°¤à±à°µà°‚à°—à°¾ à°‰à°ªà°¯à±‹à°—à°¿à°‚à°šà°¬à°¡à°²à±‡à°¦à±.",

            "footer.name":
                "à°°à±‡à°·à°¨à± à°ªà±‹à°°à±à°Ÿà°²à±",

            "footer.pds":
                "à°ªà±à°°à°œà°¾ à°ªà°‚à°ªà°¿à°£à±€ à°µà±à°¯à°µà°¸à±à°¥",

            "footer.about":
                "à°®à°¾ à°—à±à°°à°¿à°‚à°šà°¿",

            "footer.privacy":
                "à°—à±‹à°ªà±à°¯à°¤à°¾ à°µà°¿à°§à°¾à°¨à°‚",

            "footer.terms":
                "à°¨à°¿à°¬à°‚à°§à°¨à°²à± à°®à°°à°¿à°¯à± à°·à°°à°¤à±à°²à±",

            "footer.contact":
                "à°®à°®à±à°®à°²à±à°¨à°¿ à°¸à°‚à°ªà±à°°à°¦à°¿à°‚à°šà°‚à°¡à°¿",

            "footer.transparency":
                "à°ªà°¾à°°à°¦à°°à±à°¶à°•à°¤",

            "footer.educational":
                "à°µà°¿à°¦à±à°¯à°¾ à°ªà±à°°à°¦à°°à±à°¶à°¨ à°ªà±à°°à°¾à°œà±†à°•à±à°Ÿà±.",

            "footer.designed":
                "ðŸ‡®ðŸ‡³ à°†à°¹à°¾à°° à°­à°¦à±à°°à°¤ à°•à°²à°¿à°—à°¿à°¨ à°­à°¾à°°à°¤à°¦à±‡à°¶à°‚ à°•à±‹à°¸à°‚ à°°à±‚à°ªà±Šà°‚à°¦à°¿à°‚à°šà°¬à°¡à°¿à°‚à°¦à°¿",

            "language.english":
                "English",

            "language.hindi":
                "à¤¹à¤¿à¤¨à¥à¤¦à¥€",

            "language.telugu":
                "à°¤à±†à°²à±à°—à±",

            "language.select":
                "à°­à°¾à°·à°¨à± à°Žà°‚à°šà±à°•à±‹à°‚à°¡à°¿"
        }
    };


    /* ============================================================
       LANGUAGE
    ============================================================ */

    function getLanguage() {

        const savedLanguage =
            localStorage.getItem(STORAGE_KEY);

        if (
            savedLanguage &&
            Object.prototype.hasOwnProperty.call(
                translations,
                savedLanguage
            )
        ) {
            return savedLanguage;
        }

        return DEFAULT_LANGUAGE;
    }


    function updateDocumentLanguage(language) {

        if (language === "hi") {
            document.documentElement.lang = "hi-IN";
        } else if (language === "te") {
            document.documentElement.lang = "te-IN";
        } else {
            document.documentElement.lang = "en-IN";
        }
    }


    function translate(key) {

        const language = getLanguage();

        if (
            translations[language] &&
            translations[language][key]
        ) {
            return translations[language][key];
        }

        if (
            translations[DEFAULT_LANGUAGE] &&
            translations[DEFAULT_LANGUAGE][key]
        ) {
            return translations[DEFAULT_LANGUAGE][key];
        }

        return key;
    }


    /* ============================================================
       APPLY TRANSLATIONS
    ============================================================ */

    function applyTranslations() {

        document
            .querySelectorAll("[data-i18n]")
            .forEach(function (element) {

                const key =
                    element.getAttribute("data-i18n");

                if (key) {
                    element.textContent =
                        translate(key);
                }
            });


        document
            .querySelectorAll("[data-i18n-placeholder]")
            .forEach(function (element) {

                const key =
                    element.getAttribute(
                        "data-i18n-placeholder"
                    );

                if (key) {
                    element.setAttribute(
                        "placeholder",
                        translate(key)
                    );
                }
            });


        document
            .querySelectorAll("[data-i18n-title]")
            .forEach(function (element) {

                const key =
                    element.getAttribute(
                        "data-i18n-title"
                    );

                if (key) {
                    element.setAttribute(
                        "title",
                        translate(key)
                    );
                }
            });


        document
            .querySelectorAll("[data-i18n-aria-label]")
            .forEach(function (element) {

                const key =
                    element.getAttribute(
                        "data-i18n-aria-label"
                    );

                if (key) {
                    element.setAttribute(
                        "aria-label",
                        translate(key)
                    );
                }
            });
    }


    /* ============================================================
       LANGUAGE SELECTOR
    ============================================================ */

    function initializeLanguageSelector() {

        const selector =
            document.getElementById(
                "languageSelector"
            );

        if (!selector) {
            return;
        }

        selector.value =
            getLanguage();

        selector.addEventListener(
            "change",
            function () {

                setLanguage(
                    this.value
                );
            }
        );
    }


    function setLanguage(language) {

        if (
            !Object.prototype.hasOwnProperty.call(
                translations,
                language
            )
        ) {
            return;
        }

        localStorage.setItem(
            STORAGE_KEY,
            language
        );

        updateDocumentLanguage(
            language
        );

        applyTranslations();

        const selector =
            document.getElementById(
                "languageSelector"
            );

        if (selector) {
            selector.value =
                language;
        }
    }


    /* ============================================================
       PUBLIC API
    ============================================================ */

    window.RationPortalI18n = {

        getLanguage:
            getLanguage,

        setLanguage:
            setLanguage,

        translate:
            translate,

        applyTranslations:
            applyTranslations
    };


    /* ============================================================
       INITIALIZATION
    ============================================================ */

    document.addEventListener(
        "DOMContentLoaded",
        function () {

            const language =
                getLanguage();

            updateDocumentLanguage(
                language
            );

            applyTranslations();

            initializeLanguageSelector();
        }
    );

})();