package org.teamapps.application.api.versioning;

import java.util.Objects;

public class ApplicationVersion implements Comparable<ApplicationVersion> {

	public static ApplicationVersion create(int major, int minor) {
		return new ApplicationVersion(null, major, minor, 0);
	}

	public static ApplicationVersion create(String tag, int major, int minor, int patch) {
		return new ApplicationVersion(tag, major, minor, patch);
	}

	public static ApplicationVersion create(String tag, int major, int minor) {
		return new ApplicationVersion(tag, major, minor, 0);
	}

	public static ApplicationVersion create(int major, int minor, int patch) {
		return new ApplicationVersion(null, major, minor, patch);
	}

	private final String tag;

	private final int major;
	private final int minor;
	private final int patch;

	public ApplicationVersion(String tag, int major, int minor, int patch) {
		this.tag = tag == null ? "V" : tag;
		this.major = major;
		this.minor = minor;
		this.patch = patch;
	}

	public String getTag() {
		return tag;
	}

	public int getMajor() {
		return major;
	}

	public int getMinor() {
		return minor;
	}

	public int getPatch() {
		return patch;
	}

	public String getVersion() {
		return tag + "-" + major + "." + minor + "." + patch;
	}


	@Override
	public int compareTo(ApplicationVersion version) {
		if (major != version.getMajor()) {
			return major < version.getMajor() ? -1 : 1;
		}
		if (minor != version.getMinor()) {
			return minor < version.getMinor() ? -1 : 1;
		}
		if (patch != version.getPatch()) {
			return patch < version.getPatch() ? -1 : 1;
		}
		if (!tag.equals(version.getTag())) {
			return tag.compareTo(version.getTag());
		}
		return 0;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ApplicationVersion that = (ApplicationVersion) o;
		return major == that.major &&
				minor == that.minor &&
				patch == that.patch &&
				tag.equals(that.tag);
	}

	@Override
	public int hashCode() {
		return Objects.hash(tag, major, minor, patch);
	}
}
