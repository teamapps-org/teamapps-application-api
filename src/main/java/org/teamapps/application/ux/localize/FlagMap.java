/*-
 * ========================LICENSE_START=================================
 * TeamApps Application API
 * ---
 * Copyright (C) 2020 - 2025 TeamApps.org
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
package org.teamapps.application.ux.localize;

import org.teamapps.application.api.application.ApplicationInstanceData;
import org.teamapps.application.api.localization.Country;
import org.teamapps.application.api.localization.Language;

import java.util.HashMap;
import java.util.Map;

public class FlagMap {

	private static final Map<String, String> flagMap = new HashMap<>();
	private static final Map<String, String> flagByLanguage = new HashMap<>();

	static {
		flagMap.put("AC", "🇦🇨");
		flagMap.put("AD", "🇦🇩");
		flagMap.put("AE", "🇦🇪");
		flagMap.put("AF", "🇦🇫");
		flagMap.put("AG", "🇦🇬");
		flagMap.put("AI", "🇦🇮");
		flagMap.put("AL", "🇦🇱");
		flagMap.put("AM", "🇦🇲");
		flagMap.put("AO", "🇦🇴");
		flagMap.put("AQ", "🇦🇶");
		flagMap.put("AR", "🇦🇷");
		flagMap.put("AS", "🇦🇸");
		flagMap.put("AT", "🇦🇹");
		flagMap.put("AU", "🇦🇺");
		flagMap.put("AW", "🇦🇼");
		flagMap.put("AX", "🇦🇽");
		flagMap.put("AZ", "🇦🇿");
		flagMap.put("BA", "🇧🇦");
		flagMap.put("BB", "🇧🇧");
		flagMap.put("BD", "🇧🇩");
		flagMap.put("BE", "🇧🇪");
		flagMap.put("BF", "🇧🇫");
		flagMap.put("BG", "🇧🇬");
		flagMap.put("BH", "🇧🇭");
		flagMap.put("BI", "🇧🇮");
		flagMap.put("BJ", "🇧🇯");
		flagMap.put("BL", "🇧🇱");
		flagMap.put("BM", "🇧🇲");
		flagMap.put("BN", "🇧🇳");
		flagMap.put("BO", "🇧🇴");
		flagMap.put("BQ", "🇧🇶");
		flagMap.put("BR", "🇧🇷");
		flagMap.put("BS", "🇧🇸");
		flagMap.put("BT", "🇧🇹");
		flagMap.put("BV", "🇧🇻");
		flagMap.put("BW", "🇧🇼");
		flagMap.put("BY", "🇧🇾");
		flagMap.put("BZ", "🇧🇿");
		flagMap.put("CA", "🇨🇦");
		flagMap.put("CC", "🇨🇨");
		flagMap.put("CD", "🇨🇩");
		flagMap.put("CF", "🇨🇫");
		flagMap.put("CG", "🇨🇬");
		flagMap.put("CH", "🇨🇭");
		flagMap.put("CI", "🇨🇮");
		flagMap.put("CK", "🇨🇰");
		flagMap.put("CL", "🇨🇱");
		flagMap.put("CM", "🇨🇲");
		flagMap.put("CN", "🇨🇳");
		flagMap.put("CO", "🇨🇴");
		flagMap.put("CP", "🇨🇵");
		flagMap.put("CR", "🇨🇷");
		flagMap.put("CU", "🇨🇺");
		flagMap.put("CV", "🇨🇻");
		flagMap.put("CW", "🇨🇼");
		flagMap.put("CX", "🇨🇽");
		flagMap.put("CY", "🇨🇾");
		flagMap.put("CZ", "🇨🇿");
		flagMap.put("DE", "🇩🇪");
		flagMap.put("DG", "🇩🇬");
		flagMap.put("DJ", "🇩🇯");
		flagMap.put("DK", "🇩🇰");
		flagMap.put("DM", "🇩🇲");
		flagMap.put("DO", "🇩🇴");
		flagMap.put("DZ", "🇩🇿");
		flagMap.put("EA", "🇪🇦");
		flagMap.put("EC", "🇪🇨");
		flagMap.put("EE", "🇪🇪");
		flagMap.put("EG", "🇪🇬");
		flagMap.put("EH", "🇪🇭");
		flagMap.put("ER", "🇪🇷");
		flagMap.put("ES", "🇪🇸");
		flagMap.put("ET", "🇪🇹");
		flagMap.put("EU", "🇪🇺");
		flagMap.put("FI", "🇫🇮");
		flagMap.put("FJ", "🇫🇯");
		flagMap.put("FK", "🇫🇰");
		flagMap.put("FM", "🇫🇲");
		flagMap.put("FO", "🇫🇴");
		flagMap.put("FR", "🇫🇷");
		flagMap.put("GA", "🇬🇦");
		flagMap.put("GB", "🇬🇧");
		flagMap.put("GD", "🇬🇩");
		flagMap.put("GE", "🇬🇪");
		flagMap.put("GF", "🇬🇫");
		flagMap.put("GG", "🇬🇬");
		flagMap.put("GH", "🇬🇭");
		flagMap.put("GI", "🇬🇮");
		flagMap.put("GL", "🇬🇱");
		flagMap.put("GM", "🇬🇲");
		flagMap.put("GN", "🇬🇳");
		flagMap.put("GP", "🇬🇵");
		flagMap.put("GQ", "🇬🇶");
		flagMap.put("GR", "🇬🇷");
		flagMap.put("GS", "🇬🇸");
		flagMap.put("GT", "🇬🇹");
		flagMap.put("GU", "🇬🇺");
		flagMap.put("GW", "🇬🇼");
		flagMap.put("GY", "🇬🇾");
		flagMap.put("HK", "🇭🇰");
		flagMap.put("HM", "🇭🇲");
		flagMap.put("HN", "🇭🇳");
		flagMap.put("HR", "🇭🇷");
		flagMap.put("HT", "🇭🇹");
		flagMap.put("HU", "🇭🇺");
		flagMap.put("IC", "🇮🇨");
		flagMap.put("ID", "🇮🇩");
		flagMap.put("IE", "🇮🇪");
		flagMap.put("IL", "🇮🇱");
		flagMap.put("IM", "🇮🇲");
		flagMap.put("IN", "🇮🇳");
		flagMap.put("IO", "🇮🇴");
		flagMap.put("IQ", "🇮🇶");
		flagMap.put("IR", "🇮🇷");
		flagMap.put("IS", "🇮🇸");
		flagMap.put("IT", "🇮🇹");
		flagMap.put("JE", "🇯🇪");
		flagMap.put("JM", "🇯🇲");
		flagMap.put("JO", "🇯🇴");
		flagMap.put("JP", "🇯🇵");
		flagMap.put("KE", "🇰🇪");
		flagMap.put("KG", "🇰🇬");
		flagMap.put("KH", "🇰🇭");
		flagMap.put("KI", "🇰🇮");
		flagMap.put("KM", "🇰🇲");
		flagMap.put("KN", "🇰🇳");
		flagMap.put("KP", "🇰🇵");
		flagMap.put("KR", "🇰🇷");
		flagMap.put("KW", "🇰🇼");
		flagMap.put("KY", "🇰🇾");
		flagMap.put("KZ", "🇰🇿");
		flagMap.put("LA", "🇱🇦");
		flagMap.put("LB", "🇱🇧");
		flagMap.put("LC", "🇱🇨");
		flagMap.put("LI", "🇱🇮");
		flagMap.put("LK", "🇱🇰");
		flagMap.put("LR", "🇱🇷");
		flagMap.put("LS", "🇱🇸");
		flagMap.put("LT", "🇱🇹");
		flagMap.put("LU", "🇱🇺");
		flagMap.put("LV", "🇱🇻");
		flagMap.put("LY", "🇱🇾");
		flagMap.put("MA", "🇲🇦");
		flagMap.put("MC", "🇲🇨");
		flagMap.put("MD", "🇲🇩");
		flagMap.put("ME", "🇲🇪");
		flagMap.put("MF", "🇲🇫");
		flagMap.put("MG", "🇲🇬");
		flagMap.put("MH", "🇲🇭");
		flagMap.put("MK", "🇲🇰");
		flagMap.put("ML", "🇲🇱");
		flagMap.put("MM", "🇲🇲");
		flagMap.put("MN", "🇲🇳");
		flagMap.put("MO", "🇲🇴");
		flagMap.put("MP", "🇲🇵");
		flagMap.put("MQ", "🇲🇶");
		flagMap.put("MR", "🇲🇷");
		flagMap.put("MS", "🇲🇸");
		flagMap.put("MT", "🇲🇹");
		flagMap.put("MU", "🇲🇺");
		flagMap.put("MV", "🇲🇻");
		flagMap.put("MW", "🇲🇼");
		flagMap.put("MX", "🇲🇽");
		flagMap.put("MY", "🇲🇾");
		flagMap.put("MZ", "🇲🇿");
		flagMap.put("NA", "🇳🇦");
		flagMap.put("NC", "🇳🇨");
		flagMap.put("NE", "🇳🇪");
		flagMap.put("NF", "🇳🇫");
		flagMap.put("NG", "🇳🇬");
		flagMap.put("NI", "🇳🇮");
		flagMap.put("NL", "🇳🇱");
		flagMap.put("NO", "🇳🇴");
		flagMap.put("NP", "🇳🇵");
		flagMap.put("NR", "🇳🇷");
		flagMap.put("NU", "🇳🇺");
		flagMap.put("NZ", "🇳🇿");
		flagMap.put("OM", "🇴🇲");
		flagMap.put("PA", "🇵🇦");
		flagMap.put("PE", "🇵🇪");
		flagMap.put("PF", "🇵🇫");
		flagMap.put("PG", "🇵🇬");
		flagMap.put("PH", "🇵🇭");
		flagMap.put("PK", "🇵🇰");
		flagMap.put("PL", "🇵🇱");
		flagMap.put("PM", "🇵🇲");
		flagMap.put("PN", "🇵🇳");
		flagMap.put("PR", "🇵🇷");
		flagMap.put("PS", "🇵🇸");
		flagMap.put("PT", "🇵🇹");
		flagMap.put("PW", "🇵🇼");
		flagMap.put("PY", "🇵🇾");
		flagMap.put("QA", "🇶🇦");
		flagMap.put("RE", "🇷🇪");
		flagMap.put("RO", "🇷🇴");
		flagMap.put("RS", "🇷🇸");
		flagMap.put("RU", "🇷🇺");
		flagMap.put("RW", "🇷🇼");
		flagMap.put("SA", "🇸🇦");
		flagMap.put("SB", "🇸🇧");
		flagMap.put("SC", "🇸🇨");
		flagMap.put("SD", "🇸🇩");
		flagMap.put("SE", "🇸🇪");
		flagMap.put("SG", "🇸🇬");
		flagMap.put("SH", "🇸🇭");
		flagMap.put("SI", "🇸🇮");
		flagMap.put("SJ", "🇸🇯");
		flagMap.put("SK", "🇸🇰");
		flagMap.put("SL", "🇸🇱");
		flagMap.put("SM", "🇸🇲");
		flagMap.put("SN", "🇸🇳");
		flagMap.put("SO", "🇸🇴");
		flagMap.put("SR", "🇸🇷");
		flagMap.put("SS", "🇸🇸");
		flagMap.put("ST", "🇸🇹");
		flagMap.put("SV", "🇸🇻");
		flagMap.put("SX", "🇸🇽");
		flagMap.put("SY", "🇸🇾");
		flagMap.put("SZ", "🇸🇿");
		flagMap.put("TA", "🇹🇦");
		flagMap.put("TC", "🇹🇨");
		flagMap.put("TD", "🇹🇩");
		flagMap.put("TF", "🇹🇫");
		flagMap.put("TG", "🇹🇬");
		flagMap.put("TH", "🇹🇭");
		flagMap.put("TJ", "🇹🇯");
		flagMap.put("TK", "🇹🇰");
		flagMap.put("TL", "🇹🇱");
		flagMap.put("TM", "🇹🇲");
		flagMap.put("TN", "🇹🇳");
		flagMap.put("TO", "🇹🇴");
		flagMap.put("TR", "🇹🇷");
		flagMap.put("TT", "🇹🇹");
		flagMap.put("TV", "🇹🇻");
		flagMap.put("TW", "🇹🇼");
		flagMap.put("TZ", "🇹🇿");
		flagMap.put("UA", "🇺🇦");
		flagMap.put("UG", "🇺🇬");
		flagMap.put("UM", "🇺🇲");
		flagMap.put("UN", "🇺🇳");
		flagMap.put("US", "🇺🇸");
		flagMap.put("UY", "🇺🇾");
		flagMap.put("UZ", "🇺🇿");
		flagMap.put("VA", "🇻🇦");
		flagMap.put("VC", "🇻🇨");
		flagMap.put("VE", "🇻🇪");
		flagMap.put("VG", "🇻🇬");
		flagMap.put("VI", "🇻🇮");
		flagMap.put("VN", "🇻🇳");
		flagMap.put("VU", "🇻🇺");
		flagMap.put("WF", "🇼🇫");
		flagMap.put("WS", "🇼🇸");
		flagMap.put("XK", "🇽🇰");
		flagMap.put("YE", "🇾🇪");
		flagMap.put("YT", "🇾🇹");
		flagMap.put("ZA", "🇿🇦");
		flagMap.put("ZM", "🇿🇲");
		flagMap.put("ZW", "🇿🇼");

		flagByLanguage.put("aa", "🇪🇷");
		flagByLanguage.put("af", "🇿🇦");
		flagByLanguage.put("ak", "🇬🇭");
		flagByLanguage.put("am", "🇪🇹");
		flagByLanguage.put("an", "🇪🇸");
		flagByLanguage.put("ar", "🇪🇬");
		flagByLanguage.put("as", "🇮🇳");
		flagByLanguage.put("av", "🇷🇺");
		flagByLanguage.put("ay", "🇧🇴");
		flagByLanguage.put("az", "🇦🇿");
		flagByLanguage.put("ba", "🇷🇺");
		flagByLanguage.put("be", "🇧🇾");
		flagByLanguage.put("bg", "🇧🇬");
		flagByLanguage.put("bh", "🇮🇳");
		flagByLanguage.put("bi", "🇻🇺");
		flagByLanguage.put("bm", "🇲🇱");
		flagByLanguage.put("bn", "🇧🇩");
		flagByLanguage.put("bo", "🇨🇳");
		flagByLanguage.put("br", "🇫🇷");
		flagByLanguage.put("bs", "🇧🇦");
		flagByLanguage.put("ca", "🇦🇩");
		flagByLanguage.put("ce", "🇷🇺");
		flagByLanguage.put("ch", "🇬🇺");
		flagByLanguage.put("co", "🇫🇷");
		flagByLanguage.put("cr", "🇨🇦");
		flagByLanguage.put("cs", "🇨🇿");
		flagByLanguage.put("cv", "🇷🇺");
		flagByLanguage.put("cy", "🇬🇧");
		flagByLanguage.put("da", "🇩🇰");
		flagByLanguage.put("de", "🇩🇪");
		flagByLanguage.put("dv", "🇲🇻");
		flagByLanguage.put("dz", "🇧🇹");
		flagByLanguage.put("ee", "🇬🇭");
		flagByLanguage.put("el", "🇬🇷");
		flagByLanguage.put("en", "🇬🇧");
		flagByLanguage.put("eo", "🇪🇺");
		flagByLanguage.put("es", "🇪🇸");
		flagByLanguage.put("et", "🇪🇪");
		flagByLanguage.put("eu", "🇪🇸");
		flagByLanguage.put("fa", "🇮🇷");
		flagByLanguage.put("ff", "🇨🇲");
		flagByLanguage.put("fi", "🇫🇮");
		flagByLanguage.put("fj", "🇫🇯");
		flagByLanguage.put("fo", "🇫🇴");
		flagByLanguage.put("fr", "🇫🇷");
		flagByLanguage.put("ga", "🇮🇪");
		flagByLanguage.put("gd", "🇮🇪");
		flagByLanguage.put("gn", "🇵🇾");
		flagByLanguage.put("gu", "🇮🇳");
		flagByLanguage.put("ha", "🇳🇪");
		flagByLanguage.put("he", "🇮🇱");
		flagByLanguage.put("hi", "🇮🇳");
		flagByLanguage.put("ho", "🇵🇬");
		flagByLanguage.put("hr", "🇭🇷");
		flagByLanguage.put("ht", "🇭🇹");
		flagByLanguage.put("hu", "🇭🇺");
		flagByLanguage.put("hy", "🇦🇲");
		flagByLanguage.put("hz", "🇳🇦");
		flagByLanguage.put("id", "🇮🇩");
		flagByLanguage.put("ig", "🇳🇬");
		flagByLanguage.put("ii", "🇨🇳");
		flagByLanguage.put("ik", "🇺🇸");
		flagByLanguage.put("is", "🇮🇸");
		flagByLanguage.put("it", "🇮🇹");
		flagByLanguage.put("iu", "🇨🇦");
		flagByLanguage.put("ja", "🇯🇵");
		flagByLanguage.put("jv", "🇮🇩");
		flagByLanguage.put("ka", "🇬🇪");
		flagByLanguage.put("kg", "🇨🇬");
		flagByLanguage.put("ki", "🇰🇪");
		flagByLanguage.put("kj", "🇦🇴");
		flagByLanguage.put("kk", "🇰🇿");
		flagByLanguage.put("kl", "🇬🇱");
		flagByLanguage.put("km", "🇰🇭");
		flagByLanguage.put("kn", "🇮🇳");
		flagByLanguage.put("ko", "🇰🇷");
		flagByLanguage.put("kr", "🇳🇬");
		flagByLanguage.put("ks", "🇮🇳");
		flagByLanguage.put("ku", "🇹🇷");
		flagByLanguage.put("kv", "🇷🇺");
		flagByLanguage.put("kw", "🇬🇧");
		flagByLanguage.put("ky", "🇰🇬");
		flagByLanguage.put("lg", "🇺🇬");
		flagByLanguage.put("ln", "🇨🇬");
		flagByLanguage.put("lo", "🇱🇦");
		flagByLanguage.put("lt", "🇱🇹");
		flagByLanguage.put("lu", "🇱🇺");
		flagByLanguage.put("lv", "🇱🇻");
		flagByLanguage.put("mg", "🇲🇬");
		flagByLanguage.put("mh", "🇲🇭");
		flagByLanguage.put("mk", "🇲🇰");
		flagByLanguage.put("ml", "🇲🇱");
		flagByLanguage.put("mn", "🇲🇳");
		flagByLanguage.put("mr", "🇮🇳");
		flagByLanguage.put("ms", "🇲🇾");
		flagByLanguage.put("mt", "🇲🇹");
		flagByLanguage.put("my", "🇲🇲");
		flagByLanguage.put("na", "🇳🇷");
		flagByLanguage.put("nd", "🇿🇼");
		flagByLanguage.put("ne", "🇳🇵");
		flagByLanguage.put("ng", "🇳🇦");
		flagByLanguage.put("nl", "🇳🇱");
		flagByLanguage.put("no", "🇳🇴");
		flagByLanguage.put("nr", "🇳🇷");
		flagByLanguage.put("nv", "🇺🇸");
		flagByLanguage.put("ny", "🇲🇼");
		flagByLanguage.put("oj", "🇨🇦");
		flagByLanguage.put("om", "🇪🇹");
		flagByLanguage.put("or", "🇮🇳");
		flagByLanguage.put("os", "🇬🇪");
		flagByLanguage.put("pa", "🇵🇰");
		flagByLanguage.put("pl", "🇵🇱");
		flagByLanguage.put("ps", "🇦🇫");
		flagByLanguage.put("pt", "🇵🇹");
		flagByLanguage.put("qu", "🇵🇪");
		flagByLanguage.put("rm", "🇨🇭");
		flagByLanguage.put("rn", "🇧🇮");
		flagByLanguage.put("ro", "🇷🇴");
		flagByLanguage.put("ru", "🇷🇺");
		flagByLanguage.put("rw", "🇷🇼");
		flagByLanguage.put("sa", "🇮🇳");
		flagByLanguage.put("sc", "🇮🇹");
		flagByLanguage.put("sd", "🇵🇰");
		flagByLanguage.put("sg", "🇨🇫");
		flagByLanguage.put("si", "🇱🇰");
		flagByLanguage.put("sk", "🇸🇰");
		flagByLanguage.put("sl", "🇸🇮");
		flagByLanguage.put("sm", "🇦🇸");
		flagByLanguage.put("sn", "🇿🇼");
		flagByLanguage.put("so", "🇸🇴");
		flagByLanguage.put("sq", "🇦🇱");
		flagByLanguage.put("sr", "🇷🇸");
		flagByLanguage.put("ss", "🇸🇿");
		flagByLanguage.put("st", "🇦🇱");
		flagByLanguage.put("su", "🇮🇩");
		flagByLanguage.put("sv", "🇸🇪");
		flagByLanguage.put("sw", "🇰🇪");
		flagByLanguage.put("ta", "🇮🇳");
		flagByLanguage.put("te", "🇮🇳");
		flagByLanguage.put("tg", "🇹🇯");
		flagByLanguage.put("th", "🇹🇭");
		flagByLanguage.put("ti", "🇪🇷");
		flagByLanguage.put("tk", "🇹🇲");
		flagByLanguage.put("tl", "🇵🇭");
		flagByLanguage.put("tn", "🇧🇼");
		flagByLanguage.put("to", "🇹🇴");
		flagByLanguage.put("tr", "🇹🇷");
		flagByLanguage.put("ts", "🇿🇼");
		flagByLanguage.put("tt", "🇷🇺");
		flagByLanguage.put("tw", "🇬🇭");
		flagByLanguage.put("ty", "🇵🇫");
		flagByLanguage.put("ug", "🇨🇳");
		flagByLanguage.put("uk", "🇺🇦");
		flagByLanguage.put("ur", "🇵🇰");
		flagByLanguage.put("uz", "🇺🇿");
		flagByLanguage.put("ve", "🇿🇦");
		flagByLanguage.put("vi", "🇻🇳");
		flagByLanguage.put("wa", "🇧🇪");
		flagByLanguage.put("wo", "🇸🇳");
		flagByLanguage.put("xh", "🇿🇦");
		flagByLanguage.put("yi", "🇮🇱");
		flagByLanguage.put("yo", "🇳🇬");
		flagByLanguage.put("za", "🇨🇳");
		flagByLanguage.put("zh", "🇨🇳");
		flagByLanguage.put("zu", "🇿🇦");
		flagByLanguage.put("pap", "🇨🇼");
	}

	public static String getFlagByCountryCode(String isoCountryCode) {
		return flagMap.get(isoCountryCode);
	}

	public static String getFlagByCountry(Country country) {
		return country != null ? getFlagByCountryCode(country.getIsoCode()) : null;
	}

	public static String getFlagByLanguageCode(String isoCountryCode) {
		return flagByLanguage.get(isoCountryCode);
	}

	public static String getFlagByLanguage(Language language) {
		return language != null ? getFlagByLanguageCode(language.getIsoCode()) : null;
	}

	public static String getTranslatedLanguageWithFlag(String languageIso, ApplicationInstanceData applicationInstanceData) {
		return getTranslatedLanguageWithFlag(languageIso, false, applicationInstanceData);
	}

	public static String getTranslatedLanguageWithFlag(String languageIso, boolean htmlBold, ApplicationInstanceData applicationInstanceData) {
		Language language = Language.getLanguageByIsoCode(languageIso);
		if (language == null) {
			return null;
		} else {
			return getTranslatedLanguageWithFlag(language, htmlBold, applicationInstanceData);
		}
	}

	public static String getTranslatedLanguageWithFlag(Language language, ApplicationInstanceData applicationInstanceData) {
		return getTranslatedLanguageWithFlag(language, false, applicationInstanceData);
	}

	public static String getTranslatedLanguageWithFlag(Language language, boolean htmlBold, ApplicationInstanceData applicationInstanceData) {
		String flag = FlagMap.getFlagByLanguageCode(language.getIsoCode());
		String caption = language.getLanguageLocalized(applicationInstanceData);
		String flagData = flag == null ? "" : flag + " ";
		return flagData + getHtmlBold(caption, htmlBold);
	}

	private static String getHtmlBold(String value, boolean bold) {
		return bold ? "<b>" + value + "</b>" : value;
	}
}
