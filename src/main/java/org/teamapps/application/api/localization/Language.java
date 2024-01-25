/*-
 * ========================LICENSE_START=================================
 * TeamApps Application API
 * ---
 * Copyright (C) 2020 - 2024 TeamApps.org
 * ---
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
package org.teamapps.application.api.localization;

import org.teamapps.application.api.application.ApplicationInstanceData;
import org.teamapps.data.extract.PropertyExtractor;
import org.teamapps.icon.flags.FlagIcon;
import org.teamapps.icons.Icon;
import org.teamapps.ux.component.field.combobox.ComboBox;
import org.teamapps.ux.component.field.combobox.TagComboBox;
import org.teamapps.ux.component.template.BaseTemplate;
import org.teamapps.ux.model.ComboBoxModel;

import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;

public enum Language {

	AA_AFAR("aa", "Afar", FlagIcon.ER_ERITREA),
	AF_AFRIKAANS("af", "Afrikaans", FlagIcon.ZA_SOUTH_AFRICA),
	AK_AKAN("ak", "Akan", FlagIcon.GH_GHANA),
	AM_AMHARIC("am", "Amharic", FlagIcon.ET_ETHIOPIA),
	AN_ARAGONESE("an", "Aragonese", FlagIcon.ES_SPAIN),
	AR_ARABIC("ar", "Arabic", FlagIcon.EG_EGYPT),
	AS_ASSAMESE("as", "Assamese", FlagIcon.IN_INDIA),
	AV_AVARIC("av", "Avaric", FlagIcon.RU_RUSSIA),
	AY_AYMARA("ay", "Aymara", FlagIcon.BO_BOLIVIA),
	AZ_AZERBAIJANI("az", "Azerbaijani", FlagIcon.AZ_AZERBAIJAN),
	BA_BASHKIR("ba", "Bashkir", FlagIcon.RU_RUSSIA),
	BE_BELARUSIAN("be", "Belarusian", FlagIcon.BY_BELARUS),
	BG_BULGARIAN("bg", "Bulgarian", FlagIcon.BG_BULGARIA),
	BH_BIHARI("bh", "Bihari", FlagIcon.IN_INDIA),
	BI_BISLAMA("bi", "Bislama", FlagIcon.VU_VANUATU),
	BM_BAMBARA("bm", "Bambara", FlagIcon.ML_MALI),
	BN_BENGALI("bn", "Bengali", FlagIcon.BD_BANGLADESH),
	BO_TIBETAN("bo", "Tibetan", FlagIcon.CN_CHINA),
	BR_BRETON("br", "Breton", FlagIcon.FR_FRANCE),
	BS_BOSNIAN("bs", "Bosnian", FlagIcon.BA_BOSNIA),
	CA_CATALAN("ca", "Catalan", FlagIcon.AD_ANDORRA),
	CE_CHECHEN("ce", "Chechen", FlagIcon.RU_RUSSIA),
	CH_CHAMORRO("ch", "Chamorro", FlagIcon.GU_GUAM),
	CO_CORSICAN("co", "Corsican", FlagIcon.FR_FRANCE),
	CR_CREE("cr", "Cree", FlagIcon.CA_CANADA),
	CS_CZECH("cs", "Czech", FlagIcon.CZ_CZECHIA),
	CV_CHUVASH("cv", "Chuvash", FlagIcon.RU_RUSSIA),
	CY_WELSH("cy", "Welsh", FlagIcon.GB_GREAT_BRITAIN),
	DA_DANISH("da", "Danish", FlagIcon.DK_DENMARK),
	DE_GERMAN("de", "German", FlagIcon.DE_GERMANY),
	DV_DHIVEHI_MALDIVIAN("dv", "Dhivehi", FlagIcon.MV_MALDIVES),
	DZ_DZONGKHA("dz", "Dzongkha", FlagIcon.BT_BHUTAN),
	EE_EWE("ee", "Ewe", FlagIcon.GH_GHANA),
	EL_GREEK("el", "Greek", FlagIcon.GR_GREECE),
	EN_ENGLISH("en", "English", FlagIcon.GB_GREAT_BRITAIN),
	EO_ESPERANTO("eo", "Esperanto", FlagIcon.EU_EUROPEAN_UNION),
	ES_SPANISH("es", "Spanish", FlagIcon.ES_SPAIN),
	ET_ESTONIAN("et", "Estonian", FlagIcon.EE_ESTONIA),
	EU_BASQUE("eu", "Basque", FlagIcon.ES_SPAIN),
	FA_PERSIAN("fa", "Persian", FlagIcon.IR_IRAN),
	FF_FULAH("ff", "Fulah", FlagIcon.CM_CAMEROON),
	FI_FINNISH("fi", "Finnish", FlagIcon.FI_FINLAND),
	FJ_FIJIAN("fj", "Fijian", FlagIcon.FJ_FIJI),
	FO_FAROESE("fo", "Faroese", FlagIcon.FO_FAROE_ISLANDS),
	FR_FRENCH("fr", "French", FlagIcon.FR_FRANCE),
	GA_IRISH("ga", "Irish", FlagIcon.IE_IRELAND),
	GD_GAELIC("gd", "Gaelic", FlagIcon.IE_IRELAND),
	GL_GALICIAN("gl", "Galician", null),
	GN_GUARANI("gn", "Guarani", FlagIcon.PY_PARAGUAY),
	GU_GUJARATI("gu", "Gujarati", FlagIcon.IN_INDIA),
	HA_HAUSA("ha", "Hausa", FlagIcon.NE_NIGER),
	HE_HEBREW("he", "Hebrew", FlagIcon.IL_ISRAEL),
	HI_HINDI("hi", "Hindi", FlagIcon.IN_INDIA),
	HO_HIRI_MOTU("ho", "Hiri Motu", FlagIcon.PG_PAPUA_NEW_GUINEA),
	HR_CROATIAN("hr", "Croatian", FlagIcon.HR_CROATIA),
	HT_HAITIAN_HAITIAN_CREOLE("ht", "Haitian", FlagIcon.HT_HAITI),
	HU_HUNGARIAN("hu", "Hungarian", FlagIcon.HU_HUNGARY),
	HY_ARMENIAN("hy", "Armenian", FlagIcon.AM_ARMENIA),
	HZ_HERERO("hz", "Herero", FlagIcon.NA_NAMIBIA),
	ID_INDONESIAN("id", "Indonesian", FlagIcon.ID_INDONESIA),
	IG_IGBO("ig", "Igbo", FlagIcon.NG_NIGERIA),
	II_SICHUAN_YI_NUOSU("ii", "Sichuan Yi", FlagIcon.CN_CHINA),
	IK_INUPIAQ("ik", "Inupiaq", FlagIcon.US_UNITED_STATES),
	IS_ICELANDIC("is", "Icelandic", FlagIcon.IS_ICELAND),
	IT_ITALIAN("it", "Italian", FlagIcon.IT_ITALY),
	IU_INUKTITUT("iu", "Inuktitut", FlagIcon.CA_CANADA),
	JA_JAPANESE("ja", "Japanese", FlagIcon.JP_JAPAN),
	JV_JAVANESE("jv", "Javanese", FlagIcon.ID_INDONESIA),
	KA_GEORGIAN("ka", "Georgian", FlagIcon.GE_GEORGIA),
	KG_KONGO("kg", "Kongo", FlagIcon.CG_CONGO__BRAZZAVILLE),
	KI_KIKUYU_GIKUYU("ki", "Kikuyu", FlagIcon.KE_KENYA),
	KJ_KUANYAMA_KWANYAMA("kj", "Kuanyama", FlagIcon.AO_ANGOLA),
	KK_KAZAKH("kk", "Kazakh", FlagIcon.KZ_KAZAKHSTAN),
	KL_KALAALLISUT_GREENLANDIC("kl", "Kalaallisut", FlagIcon.GL_GREENLAND),
	KM_CENTRAL_KHMER("km", "Khmer", FlagIcon.KH_CAMBODIA),
	KN_KANNADA("kn", "Kannada", FlagIcon.IN_INDIA),
	KO_KOREAN("ko", "Korean", FlagIcon.KR_SOUTH_KOREA),
	KR_KANURI("kr", "Kanuri", FlagIcon.NG_NIGERIA),
	KS_KASHMIRI("ks", "Kashmiri", FlagIcon.IN_INDIA),
	KU_KURDISH("ku", "Kurdish", FlagIcon.TR_TURKEY),
	KV_KOMI("kv", "Komi", FlagIcon.RU_RUSSIA),
	KW_CORNISH("kw", "Cornish", FlagIcon.GB_GREAT_BRITAIN),
	KY_KIRGHIZ_KYRGYZ("ky", "Kirghiz", FlagIcon.KG_KYRGYZSTAN),
	LG_GANDA("lg", "Ganda", FlagIcon.UG_UGANDA),
	LN_LINGALA("ln", "Lingala", FlagIcon.CG_CONGO__BRAZZAVILLE),
	LO_LAO("lo", "Lao", FlagIcon.LA_LAOS),
	LT_LITHUANIAN("lt", "Lithuanian", FlagIcon.LT_LITHUANIA),
	LU_LUBA_KATANGA("lu", "Luba-Katanga", FlagIcon.LU_LUXEMBOURG),
	LV_LATVIAN("lv", "Latvian", FlagIcon.LV_LATVIA),
	MG_MALAGASY("mg", "Malagasy", FlagIcon.MG_MADAGASCAR),
	MH_MARSHALLESE("mh", "Marshallese", FlagIcon.MH_MARSHALL_ISLANDS),
	MI_MAORI("mi", "Maori", null),
	MK_MACEDONIAN("mk", "Macedonian", FlagIcon.MK_MACEDONIA),
	ML_MALAYALAM("ml", "Malayalam", FlagIcon.ML_MALI),
	MN_MONGOLIAN("mn", "Mongolian", FlagIcon.MN_MONGOLIA),
	MR_MARATHI("mr", "Marathi", FlagIcon.IN_INDIA),
	MS_MALAY("ms", "Malay", FlagIcon.MY_MALAYSIA),
	MT_MALTESE("mt", "Maltese", FlagIcon.MT_MALTA),
	MY_BURMESE("my", "Burmese", FlagIcon.MM_MYANMAR),
	NA_NAURU("na", "Nauru", FlagIcon.NR_NAURU),
	ND_NDEBELE_NORTH("nd", "Ndebele", FlagIcon.ZW_ZIMBABWE),
	NE_NEPALI("ne", "Nepali", FlagIcon.NP_NEPAL),
	NG_NDONGA("ng", "Ndonga", FlagIcon.NA_NAMIBIA),
	NL_DUTCH("nl", "Dutch", FlagIcon.NL_NETHERLANDS),
	NO_NORWEGIAN("no", "Norwegian", FlagIcon.NO_NORWAY),
	NR_NDEBELE_SOUTH("nr", "Ndebele", FlagIcon.NR_NAURU),
	NV_NAVAJO_NAVAHO("nv", "Navajo", FlagIcon.US_UNITED_STATES),
	NY_CHICHEWA_CHEWA_NYANJA("ny", "Chichewa", FlagIcon.MW_MALAWI),
	OJ_OJIBWA("oj", "Ojibwa", FlagIcon.CA_CANADA),
	OM_OROMO("om", "Oromo", FlagIcon.ET_ETHIOPIA),
	OR_ORIYA("or", "Oriya", FlagIcon.IN_INDIA),
	OS_OSSETIAN_OSSETIC("os", "Ossetian", FlagIcon.GE_GEORGIA),
	PA_PANJABI_PUNJABI("pa", "Panjabi", FlagIcon.PK_PAKISTAN),
	PL_POLISH("pl", "Polish", FlagIcon.PL_POLAND),
	PS_PUSHTO_PASHTO("ps", "Pashto", FlagIcon.AF_AFGHANISTAN),
	PT_PORTUGUESE("pt", "Portuguese", FlagIcon.PT_PORTUGAL),
	QU_QUECHUA("qu", "Quechua", FlagIcon.PE_PERU),
	RM_ROMANSH("rm", "Romansh", FlagIcon.CH_SWITZERLAND),
	RN_RUNDI("rn", "Rundi", FlagIcon.BI_BURUNDI),
	RO_ROMANIAN("ro", "Romanian", FlagIcon.RO_ROMANIA),
	RU_RUSSIAN("ru", "Russian", FlagIcon.RU_RUSSIA),
	RW_KINYARWANDA("rw", "Kinyarwanda", FlagIcon.RW_RWANDA),
	SA_SANSKRIT("sa", "Sanskrit", FlagIcon.IN_INDIA),
	SC_SARDINIAN("sc", "Sardinian", FlagIcon.IT_ITALY),
	SD_SINDHI("sd", "Sindhi", FlagIcon.PK_PAKISTAN),
	SG_SANGO("sg", "Sango", FlagIcon.CF_CENTRAL_AFRICAN_REPUBLIC),
	SI_SINHALA_SINHALESE("si", "Sinhalese", FlagIcon.LK_SRI_LANKA),
	SK_SLOVAK("sk", "Slovak", FlagIcon.SK_SLOVAKIA),
	SL_SLOVENIAN("sl", "Slovenian", FlagIcon.SI_SLOVENIA),
	SM_SAMOAN("sm", "Samoan", FlagIcon.AS_AMERICAN_SAMOA),
	SN_SHONA("sn", "Shona", FlagIcon.ZW_ZIMBABWE),
	SO_SOMALI("so", "Somali", FlagIcon.SO_SOMALIA),
	SQ_ALBANIAN("sq", "Albanian", FlagIcon.AL_ALBANIA),
	SR_SERBIAN("sr", "Serbian", FlagIcon.RS_SERBIA),
	SS_SWATI("ss", "Swati", FlagIcon.SZ_SWAZILAND),
	ST_SOTHO_SOUTHERN("st", "Sotho", FlagIcon.AL_ALBANIA),
	SU_SUNDANESE("su", "Sundanese", FlagIcon.ID_INDONESIA),
	SV_SWEDISH("sv", "Swedish", FlagIcon.SE_SWEDEN),
	SW_SWAHILI("sw", "Swahili", FlagIcon.KE_KENYA),
	TA_TAMIL("ta", "Tamil", FlagIcon.IN_INDIA),
	TE_TELUGU("te", "Telugu", FlagIcon.IN_INDIA),
	TG_TAJIK("tg", "Tajik", FlagIcon.TJ_TAJIKISTAN),
	TH_THAI("th", "Thai", FlagIcon.TH_THAILAND),
	TI_TIGRINYA("ti", "Tigrinya", FlagIcon.ER_ERITREA),
	TK_TURKMEN("tk", "Turkmen", FlagIcon.TM_TURKMENISTAN),
	TL_TAGALOG("tl", "Tagalog", FlagIcon.PH_PHILIPPINES),
	TN_TSWANA("tn", "Tswana", FlagIcon.BW_BOTSWANA),
	TO_TONGA("to", "Tonga", FlagIcon.TO_TONGA),
	TR_TURKISH("tr", "Turkish", FlagIcon.TR_TURKEY),
	TS_TSONGA("ts", "Tsonga", FlagIcon.ZW_ZIMBABWE),
	TT_TATAR("tt", "Tatar", FlagIcon.RU_RUSSIA),
	TW_TWI("tw", "Twi", FlagIcon.GH_GHANA),
	TY_TAHITIAN("ty", "Tahitian", FlagIcon.PF_FRENCH_POLYNESIA),
	UG_UIGHUR_UYGHUR("ug", "Uighur", FlagIcon.CN_CHINA),
	UK_UKRAINIAN("uk", "Ukrainian", FlagIcon.UA_UKRAINE),
	UR_URDU("ur", "Urdu", FlagIcon.PK_PAKISTAN),
	UZ_UZBEK("uz", "Uzbek", FlagIcon.UZ_UZBEKISTAN),
	VE_VENDA("ve", "Venda", FlagIcon.ZA_SOUTH_AFRICA),
	VI_VIETNAMESE("vi", "Vietnamese", FlagIcon.VN_VIETNAM),
	WA_WALLOON("wa", "Walloon", FlagIcon.BE_BELGIUM),
	WO_WOLOF("wo", "Wolof", FlagIcon.SN_SENEGAL),
	XH_XHOSA("xh", "Xhosa", FlagIcon.ZA_SOUTH_AFRICA),
	YI_YIDDISH("yi", "Yiddish", FlagIcon.IL_ISRAEL),
	YO_YORUBA("yo", "Yoruba", FlagIcon.NG_NIGERIA),
	ZA_ZHUANG_CHUANG("za", "Zhuang", FlagIcon.CN_CHINA),
	ZH_CHINESE("zh", "Chinese", FlagIcon.CN_CHINA),
	ZU_ZULU("zu", "Zulu", FlagIcon.ZA_SOUTH_AFRICA),
	PAP_PAPIAMENTO("pap", "Papiamento", FlagIcon.CW_CURACAO),
	;

	private final static Map<String, Language> entryByLanguage = new HashMap<>();
	private final String isoCode;
	private final String value;
	private final FlagIcon icon;

	static {
		for (Language value : values()) {
			entryByLanguage.put(value.getIsoCode(), value);
		}
	}

	public static Language getLanguageByIsoCode(String isoCode) {
		return entryByLanguage.get(isoCode);
	}

	Language(String isoCode, String value, FlagIcon icon) {
		this.isoCode = isoCode;
		this.value = value;
		this.icon = icon;
	}

	public FlagIcon getIcon() {
		return icon;
	}

	public String getIsoCode() {
		return isoCode;
	}

	public String getValue() {
		return value;
	}

	public String getKey() {
		return "org.teamapps.dictionary.language." + name();
	}

	public String getLanguageLocalized(ApplicationLocalizationProvider localizationProvider) {
		return localizationProvider.getLocalized(getKey());
	}

	public static FlagIcon getLanguageIconByIsoCode(String isoCode) {
		Language entry = entryByLanguage.get(isoCode);
		if (entry != null) {
			return entry.getIcon();
		} else {
			return null;
		}
	}

	public static Icon getLanguageIcon(String language, Icon defaultIcon) {
		FlagIcon languageIcon = getLanguageIconByIsoCode(language);
		return languageIcon != null ? languageIcon : defaultIcon;
	}

	private boolean matchLanguage(String query, ApplicationLocalizationProvider localizationProvider) {
		query = normalize(query);
		String localized = normalize(getLanguageLocalized(localizationProvider));
		if ((localized != null && localized.contains(query)) || query.equals(getIsoCode().toLowerCase())) {
			return true;
		} else {
			return false;
		}
	}

	private static String normalize(String s) {
		return s == null ? null : Normalizer.normalize(s, Normalizer.Form.NFD)
				.replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
				.replace("ß", "ss")
				.toLowerCase()
				.replace("—", "-")
				.replace("œ", "oe")
				.replace("æ", "ae")
				.replace('đ', 'd')
				.replace('ø', 'o');
	}

	public static String getLocalizedLanguageNameByIsoCode(String isoCode, ApplicationLocalizationProvider localizationProvider) {
		Language entry = entryByLanguage.get(isoCode);
		if (entry != null) {
			return entry.getLanguageLocalized(localizationProvider);
		} else {
			return isoCode;
		}
	}

	public static PropertyExtractor<Language> getPropertyExtractor(ApplicationLocalizationProvider localizationProvider) {
		return (language, s) -> {
			switch (s) {
				case BaseTemplate.PROPERTY_ICON:
					return language.getIcon();
				case BaseTemplate.PROPERTY_CAPTION:
					return language.getLanguageLocalized(localizationProvider);
				case BaseTemplate.PROPERTY_DESCRIPTION:
					return language.getIsoCode();
			}
			return null;
		};
	}

	public static ComboBox<Language> createComboBox(ApplicationLocalizationProvider localizationProvider) {
		return createComboBox(localizationProvider, null);
	}

	public static ComboBox<Language> createComboBox(ApplicationLocalizationProvider localizationProvider, Set<Language> allowedLanguages) {
		ComboBox<Language> comboBox = new ComboBox<>(BaseTemplate.LIST_ITEM_SMALL_ICON_SINGLE_LINE);
		comboBox.setDropDownTemplate(BaseTemplate.LIST_ITEM_MEDIUM_ICON_TWO_LINES);
		comboBox.setPropertyExtractor(getPropertyExtractor(localizationProvider));
		List<Language> languages = Arrays.stream(Language.values())
				.filter(language -> allowedLanguages == null || allowedLanguages.contains(language))
				.collect(Collectors.toList());
		comboBox.setModel(getComboBoxModel(localizationProvider, languages));
		comboBox.setRecordToStringFunction(language -> language.getLanguageLocalized(localizationProvider));
		return comboBox;
	}

	public static TagComboBox<Language> createTagComboBox(ApplicationLocalizationProvider applicationInstanceData) {
		return createTagComboBox(applicationInstanceData, null);
	}

	public static TagComboBox<Language> createTagComboBox(ApplicationLocalizationProvider localizationProvider, Set<Language> allowedLanguages) {
		TagComboBox<Language> tagComboBox = new TagComboBox<>();
		tagComboBox.setTemplate(BaseTemplate.LIST_ITEM_SMALL_ICON_SINGLE_LINE);
		tagComboBox.setDropDownTemplate(BaseTemplate.LIST_ITEM_MEDIUM_ICON_TWO_LINES);
		List<Language> languages = Arrays.stream(Language.values())
				.filter(language -> allowedLanguages == null || allowedLanguages.contains(language))
				.collect(Collectors.toList());
		tagComboBox.setModel(getComboBoxModel(localizationProvider, languages));
		tagComboBox.setPropertyExtractor(getPropertyExtractor(localizationProvider));
		tagComboBox.setRecordToStringFunction(language -> language.getLanguageLocalized(localizationProvider));
		return tagComboBox;
	}

	private static ComboBoxModel<Language> getComboBoxModel(ApplicationLocalizationProvider localizationProvider, List<Language> languages) {
				return s -> {
			if (s == null || s.isBlank()) {
				return languages;
			} else {
				final String query = s.toLowerCase();
				return languages.stream()
						.filter(language -> language.matchLanguage(query, localizationProvider))
						.collect(Collectors.toList());
			}
		};
	}


}
