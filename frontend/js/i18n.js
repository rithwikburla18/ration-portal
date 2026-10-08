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
            "brand.tagline": "Food Security • Empowerment • Prosperity",

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
                "Public Distribution System – Government-inspired digital service experience",

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
                "🇮🇳 Food • Family • Future",

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
                "🇮🇳 Designed for a Food Secure India",

            "language.english":
                "English",

            "language.hindi":
                "हिन्दी",

            "language.telugu":
                "తెలుగు",

            "language.select":
                "Select Language"
        },


        /* ========================================================
           HINDI
        ======================================================== */
        hi: {

            "site.name": "राशन पोर्टल",
            "site.subtitle": "डिजिटल सार्वजनिक वितरण प्रणाली",

            "access.government": "सरकारी सेवा पोर्टल",
            "access.digitalPds": "डिजिटल सार्वजनिक वितरण प्रणाली",
            "access.skip": "मुख्य सामग्री पर जाएँ",

            "brand.gov":
                "सरकारी शैली का नागरिक सेवा पोर्टल",

            "brand.name":
                "राशन पोर्टल",

            "brand.pds":
                "सार्वजनिक वितरण प्रणाली",

            "brand.tagline":
                "खाद्य सुरक्षा • सशक्तिकरण • समृद्धि",

            "header.slogan":
                "हर घर अन्न, हर घर सुरक्षा",

            "header.service":
                "डिजिटल सार्वजनिक वितरण सेवाओं के माध्यम से नागरिकों की सेवा",

            "auth.login":
                "नागरिक लॉगिन",

            "auth.register":
                "पंजीकरण",

            "nav.home":
                "होम",

            "nav.services":
                "सेवाएँ",

            "nav.rationCard":
                "राशन कार्ड",

            "nav.fps":
                "उचित मूल्य दुकान",

            "nav.help":
                "सहायता",

            "nav.transparency":
                "पारदर्शिता",

            "hero.kicker":
                "नागरिक डिजिटल सेवा पोर्टल",

            "hero.rightFood":
                "सही भोजन",

            "hero.rightPeople":
                "सही लोग",

            "hero.rightTime":
                "सही समय",

            "hero.subtitle":
                "सार्वजनिक वितरण प्रणाली – सरकार से प्रेरित डिजिटल सेवा अनुभव",

            "hero.tagline":
                "सुलभ, पारदर्शी और नागरिक-केंद्रित डिजिटल सेवाओं के माध्यम से प्रत्येक नागरिक के लिए खाद्य सुरक्षा सुनिश्चित करना।",

            "hero.chip.food":
                "सभी के लिए खाद्य सुरक्षा",

            "hero.chip.transparent":
                "पारदर्शी प्रणाली",

            "hero.chip.digital":
                "डिजिटल इंडिया",

            "hero.chip.access":
                "आसान पहुँच",

            "hero.familySmall":
                "नागरिक-केंद्रित सार्वजनिक वितरण",

            "hero.familyMera":
                "मेरा राशन मेरा अधिकार",

            "hero.familyMeraWord":
                "मेरा",

            "hero.familyAdhikar":
                "अधिकार",

            "search.label":
                "राशन कार्ड नंबर, राशन आईडी या सदस्य आईडी",

            "search.placeholder":
                "राशन कार्ड नंबर / राशन आईडी / सदस्य आईडी दर्ज करें",

            "search.button":
                "खोजें",

            "services.kicker":
                "नागरिक सेवाएँ",

            "services.title":
                "आवश्यक सेवाएँ एक ही स्थान पर",

            "services.description":
                "राशन कार्ड, शिकायत, उचित मूल्य दुकान और लाभ से संबंधित सेवाओं तक सरल डिजिटल पहुँच।",

            "services.applications":
                "मेरे आवेदन",

            "service.apply.title":
                "नया राशन कार्ड आवेदन करें",

            "service.apply.description":
                "निर्देशित नागरिक आवेदन प्रक्रिया के माध्यम से नया राशन कार्ड ऑनलाइन प्राप्त करें।",

            "service.download.title":
                "ई-राशन कार्ड डाउनलोड करें",

            "service.download.description":
                "प्रमाणीकरण के बाद अपना डिजिटल राशन कार्ड सुरक्षित रूप से प्राप्त और डाउनलोड करें।",

            "service.fps.title":
                "उचित मूल्य दुकान खोजें",

            "service.fps.description":
                "जिले, क्षेत्र और उपलब्ध सेवा विवरण के अनुसार उचित मूल्य दुकान की जानकारी प्राप्त करें।",

            "service.grievance.title":
                "शिकायत दर्ज करें",

            "service.grievance.description":
                "सेवा संबंधी शिकायत दर्ज करें और अपने नागरिक अनुरोध की प्रगति देखें।",

            "service.entitlement.title":
                "अपने अधिकार जानें",

            "service.entitlement.description":
                "राशन लाभ, सार्वजनिक वितरण जानकारी और नागरिक अधिकारों को समझें।",

            "dashboard.kicker":
                "पीडीएस डैशबोर्ड",

            "dashboard.title":
                "सार्वजनिक वितरण प्रणाली का अवलोकन",

            "dashboard.description":
                "इस शैक्षणिक परियोजना इंटरफेस के लिए प्रदर्शित उदाहरणात्मक आँकड़े।",

            "dashboard.cards":
                "कुल राशन कार्ड",

            "dashboard.rice":
                "वितरित चावल (क्विंटल)",

            "dashboard.fps":
                "सक्रिय उचित मूल्य दुकानें",

            "dashboard.cardsGrowth": "+4.8%",
            "dashboard.riceGrowth": "+7.2%",
            "dashboard.fpsGrowth": "+2.6%",

            "dashboard.note":
                "* ऊपर दिखाए गए आँकड़े इस शैक्षणिक प्रदर्शन इंटरफेस के लिए उदाहरणात्मक हैं।",

            "family.kicker":
                "राष्ट्रीय खाद्य सुरक्षा",

            "family.title":
                "एक मजबूत",

            "family.titleStrong":
                "भारत के लिए भोजन",

            "family.description":
                "नागरिक-केंद्रित डिजिटल अनुभव जो राशन सेवाओं, परिवार की जानकारी और सार्वजनिक वितरण प्रक्रियाओं को जोड़ता है।",

            "family.slogan":
                "एक मजबूत भारत के लिए भोजन",

            "announcement.title":
                "घोषणाएँ",

            "announcement.verify":
                "सेवा आवेदन जमा करने से पहले नागरिकों को अपने परिवार का विवरण सत्यापित करने के लिए प्रोत्साहित किया जाता है।",

            "announcement.check":
                "नागरिक पोर्टल के माध्यम से अपने राशन लाभ और आवेदन की स्थिति देखें।",

            "feature.transparent.title":
                "पारदर्शी और जवाबदेह",

            "feature.transparent.description":
                "स्पष्ट सेवा प्रक्रियाएँ, स्थिति की जानकारी और जिम्मेदार डिजिटल कार्यप्रवाह।",

            "feature.digital.title":
                "डिजिटल इंडिया",

            "feature.digital.description":
                "सरल और सुलभ नागरिक अनुभव के लिए आधुनिक वेब सेवाएँ।",

            "feature.access.title":
                "सभी के लिए आसान पहुँच",

            "feature.access.description":
                "विभिन्न उपकरणों के लिए उत्तरदायी, पठनीय और सुलभ इंटरफेस।",

            "feature.garib.title":
                "गरीब कल्याण हमारी प्राथमिकता",

            "feature.garib.description":
                "डिज़ाइन खाद्य सुरक्षा और नागरिक-केंद्रित सार्वजनिक सेवाओं पर जोर देता है।",

            "feature.bannerKicker":
                "खाद्य सुरक्षा के लिए साथ-साथ",

            "feature.jaiHind":
                "जय हिन्द",

            "feature.bannerDescription":
                "भारत के तिरंगे, धान की खेती और नागरिक-केंद्रित सार्वजनिक सेवा मूल्यों से प्रेरित आधुनिक डिजिटल अनुभव।",

            "feature.bannerBottom":
                "🇮🇳 भोजन • परिवार • भविष्य",

            "notice.title":
                "महत्वपूर्ण सूचना",

            "notice.text":
                "यह पोर्टल वेब तकनीकों का उपयोग करके आधुनिक सार्वजनिक वितरण प्रणाली अनुभव को प्रदर्शित करने के लिए बनाया गया एक शैक्षणिक सॉफ्टवेयर प्रोजेक्ट है।",

            "notice.warning":
                "यह परियोजना भारत सरकार की आधिकारिक वेबसाइट नहीं है। आधिकारिक सरकारी चिह्न, मुहर और पहचान आधिकारिक प्रतिनिधित्व के रूप में उपयोग नहीं किए गए हैं।",

            "footer.name":
                "राशन पोर्टल",

            "footer.pds":
                "सार्वजनिक वितरण प्रणाली",

            "footer.about":
                "हमारे बारे में",

            "footer.privacy":
                "गोपनीयता नीति",

            "footer.terms":
                "नियम और शर्तें",

            "footer.contact":
                "संपर्क करें",

            "footer.transparency":
                "पारदर्शिता",

            "footer.educational":
                "शैक्षणिक प्रदर्शन परियोजना।",

            "footer.designed":
                "🇮🇳 खाद्य सुरक्षा वाले भारत के लिए बनाया गया",

            "language.english":
                "English",

            "language.hindi":
                "हिन्दी",

            "language.telugu":
                "తెలుగు",

            "language.select":
                "भाषा चुनें"
        },


        /* ========================================================
           TELUGU
        ======================================================== */
        te: {

            "site.name":
                "రేషన్ పోర్టల్",

            "site.subtitle":
                "డిజిటల్ ప్రజా పంపిణీ వ్యవస్థ",

            "access.government":
                "ప్రభుత్వ సేవా పోర్టల్",

            "access.digitalPds":
                "డిజిటల్ ప్రజా పంపిణీ వ్యవస్థ",

            "access.skip":
                "ప్రధాన విషయానికి వెళ్లండి",

            "brand.gov":
                "ప్రభుత్వ శైలి పౌర సేవా పోర్టల్",

            "brand.name":
                "రేషన్ పోర్టల్",

            "brand.pds":
                "ప్రజా పంపిణీ వ్యవస్థ",

            "brand.tagline":
                "ఆహార భద్రత • సాధికారత • అభివృద్ధి",

            "header.slogan":
                "ప్రతి ఇంటికి అన్నం, ప్రతి ఇంటికి భద్రత",

            "header.service":
                "డిజిటల్ ప్రజా పంపిణీ సేవల ద్వారా పౌరులకు సేవలు",

            "auth.login":
                "పౌర లాగిన్",

            "auth.register":
                "నమోదు",

            "nav.home":
                "హోమ్",

            "nav.services":
                "సేవలు",

            "nav.rationCard":
                "రేషన్ కార్డు",

            "nav.fps":
                "న్యాయ ధరల దుకాణం",

            "nav.help":
                "సహాయం",

            "nav.transparency":
                "పారదర్శకత",

            "hero.kicker":
                "పౌర డిజిటల్ సేవా పోర్టల్",

            "hero.rightFood":
                "సరైన ఆహారం",

            "hero.rightPeople":
                "సరైన ప్రజలు",

            "hero.rightTime":
                "సరైన సమయం",

            "hero.subtitle":
                "ప్రజా పంపిణీ వ్యవస్థ – ప్రభుత్వ ప్రేరణతో రూపొందించిన డిజిటల్ సేవా అనుభవం",

            "hero.tagline":
                "అందుబాటులో ఉండే, పారదర్శకమైన మరియు పౌర కేంద్రిత డిజిటల్ సేవల ద్వారా ప్రతి పౌరుడికి ఆహార భద్రతను అందించడం.",

            "hero.chip.food":
                "అందరికీ ఆహార భద్రత",

            "hero.chip.transparent":
                "పారదర్శక వ్యవస్థ",

            "hero.chip.digital":
                "డిజిటల్ ఇండియా",

            "hero.chip.access":
                "సులభమైన ప్రాప్యత",

            "hero.familySmall":
                "పౌర కేంద్రిత ప్రజా పంపిణీ",

            "hero.familyMera":
                "నా రేషన్ నా హక్కు",

            "hero.familyMeraWord":
                "నా రేషన్",

            "hero.familyAdhikar":
                "నా హక్కు",

            "search.label":
                "రేషన్ కార్డు నంబర్, రేషన్ ఐడి లేదా సభ్యుని ఐడి",

            "search.placeholder":
                "రేషన్ కార్డు నంబర్ / రేషన్ ఐడి / సభ్యుని ఐడి నమోదు చేయండి",

            "search.button":
                "వెతకండి",

            "services.kicker":
                "పౌర సేవలు",

            "services.title":
                "అవసరమైన సేవలు ఒకే చోట",

            "services.description":
                "రేషన్ కార్డు, ఫిర్యాదు, న్యాయ ధరల దుకాణం మరియు హక్కుల సేవలకు సులభమైన డిజిటల్ ప్రాప్యత.",

            "services.applications":
                "నా దరఖాస్తులు",

            "service.apply.title":
                "కొత్త రేషన్ కార్డు కోసం దరఖాస్తు",

            "service.apply.description":
                "మార్గదర్శక పౌర దరఖాస్తు ప్రక్రియ ద్వారా కొత్త రేషన్ కార్డును ఆన్‌లైన్‌లో పొందండి.",

            "service.download.title":
                "ఈ-రేషన్ కార్డు డౌన్‌లోడ్",

            "service.download.description":
                "ధృవీకరణ తర్వాత మీ డిజిటల్ రేషన్ కార్డును సురక్షితంగా పొందండి మరియు డౌన్‌లోడ్ చేయండి.",

            "service.fps.title":
                "న్యాయ ధరల దుకాణాన్ని కనుగొనండి",

            "service.fps.description":
                "జిల్లా, ప్రాంతం మరియు అందుబాటులో ఉన్న సేవల వివరాల ప్రకారం న్యాయ ధరల దుకాణం సమాచారాన్ని పొందండి.",

            "service.grievance.title":
                "ఫిర్యాదు నమోదు చేయండి",

            "service.grievance.description":
                "సేవకు సంబంధించిన ఫిర్యాదును నమోదు చేసి మీ పౌర అభ్యర్థన పురోగతిని అనుసరించండి.",

            "service.entitlement.title":
                "మీ హక్కులను తెలుసుకోండి",

            "service.entitlement.description":
                "రేషన్ ప్రయోజనాలు, ప్రజా పంపిణీ సమాచారం మరియు పౌర హక్కులను అర్థం చేసుకోండి.",

            "dashboard.kicker":
                "పీడీఎస్ డ్యాష్‌బోర్డ్",

            "dashboard.title":
                "ప్రజా పంపిణీ వ్యవస్థ అవలోకనం",

            "dashboard.description":
                "ఈ విద్యా ప్రాజెక్ట్ ఇంటర్‌ఫేస్ కోసం చూపబడిన ఉదాహరణ గణాంకాలు.",

            "dashboard.cards":
                "మొత్తం రేషన్ కార్డులు",

            "dashboard.rice":
                "పంపిణీ చేసిన బియ్యం (క్వింటాళ్లు)",

            "dashboard.fps":
                "క్రియాశీల న్యాయ ధరల దుకాణాలు",

            "dashboard.cardsGrowth":
                "+4.8%",

            "dashboard.riceGrowth":
                "+7.2%",

            "dashboard.fpsGrowth":
                "+2.6%",

            "dashboard.note":
                "* పై గణాంకాలు ఈ విద్యా ప్రదర్శన ఇంటర్‌ఫేస్ కోసం చూపబడిన ఉదాహరణ విలువలు మాత్రమే.",

            "family.kicker":
                "జాతీయ ఆహార భద్రత",

            "family.title":
                "ఒక బలమైన",

            "family.titleStrong":
                "భారతదేశం కోసం ఆహారం",

            "family.description":
                "రేషన్ సేవలు, కుటుంబ సమాచారం మరియు ప్రజా పంపిణీ ప్రక్రియలను కలిపే పౌర కేంద్రిత డిజిటల్ అనుభవం.",

            "family.slogan":
                "ఒక బలమైన భారతదేశం కోసం ఆహారం",

            "announcement.title":
                "ప్రకటనలు",

            "announcement.verify":
                "సేవా దరఖాస్తులను సమర్పించే ముందు కుటుంబ వివరాలను ధృవీకరించుకోవాలని పౌరులను ప్రోత్సహిస్తున్నాము.",

            "announcement.check":
                "పౌర పోర్టల్ ద్వారా మీ రేషన్ హక్కులు మరియు దరఖాస్తు స్థితిని పరిశీలించండి.",

            "feature.transparent.title":
                "పారదర్శకత మరియు బాధ్యత",

            "feature.transparent.description":
                "స్పష్టమైన సేవా ప్రక్రియలు, స్థితి కనిపించే విధానం మరియు బాధ్యతాయుతమైన డిజిటల్ వర్క్‌ఫ్లోలు.",

            "feature.digital.title":
                "డిజిటల్ ఇండియా",

            "feature.digital.description":
                "సులభమైన మరియు అందుబాటులో ఉండే పౌర పరస్పర చర్యల కోసం ఆధునిక వెబ్ సేవలు.",

            "feature.access.title":
                "అందరికీ సులభమైన ప్రాప్యత",

            "feature.access.description":
                "వివిధ పరికరాల కోసం స్పందించే, చదవగలిగే మరియు ప్రాప్యతకు అనుకూలమైన ఇంటర్‌ఫేస్‌లు.",

            "feature.garib.title":
                "గరీబ్ కళ్యాణ్ మా ప్రాధాన్యత",

            "feature.garib.description":
                "ఈ డిజైన్ ఆహార భద్రత మరియు పౌర కేంద్రిత ప్రజా సేవలకు ప్రాధాన్యం ఇస్తుంది.",

            "feature.bannerKicker":
                "ఆహార భద్రత కోసం కలిసి",

            "feature.jaiHind":
                "జై హింద్",

            "feature.bannerDescription":
                "భారత త్రివర్ణ పతాకం, వరి సాగు మరియు పౌర కేంద్రిత ప్రజా సేవా విలువల నుండి ప్రేరణ పొందిన ఆధునిక డిజిటల్ అనుభవం.",

            "feature.bannerBottom":
                "🇮🇳 ఆహారం • కుటుంబం • భవిష్యత్తు",

            "notice.title":
                "ముఖ్యమైన గమనిక",

            "notice.text":
                "ఈ పోర్టల్ వెబ్ సాంకేతికతలను ఉపయోగించి ఆధునిక ప్రజా పంపిణీ వ్యవస్థ అనుభవాన్ని ప్రదర్శించడానికి రూపొందించిన విద్యా సాఫ్ట్‌వేర్ ప్రాజెక్ట్.",

            "notice.warning":
                "ఈ ప్రాజెక్ట్ భారత ప్రభుత్వ అధికారిక వెబ్‌సైట్ కాదు. అధికారిక ప్రభుత్వ చిహ్నాలు, ముద్రలు మరియు గుర్తింపులు అధికారిక ప్రతినిధిత్వంగా ఉపయోగించబడలేదు.",

            "footer.name":
                "రేషన్ పోర్టల్",

            "footer.pds":
                "ప్రజా పంపిణీ వ్యవస్థ",

            "footer.about":
                "మా గురించి",

            "footer.privacy":
                "గోప్యతా విధానం",

            "footer.terms":
                "నిబంధనలు మరియు షరతులు",

            "footer.contact":
                "మమ్మల్ని సంప్రదించండి",

            "footer.transparency":
                "పారదర్శకత",

            "footer.educational":
                "విద్యా ప్రదర్శన ప్రాజెక్ట్.",

            "footer.designed":
                "🇮🇳 ఆహార భద్రత కలిగిన భారతదేశం కోసం రూపొందించబడింది",

            "language.english":
                "English",

            "language.hindi":
                "हिन्दी",

            "language.telugu":
                "తెలుగు",

            "language.select":
                "భాషను ఎంచుకోండి"
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
