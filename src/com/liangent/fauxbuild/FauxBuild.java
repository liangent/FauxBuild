package com.liangent.fauxbuild;

import static de.robv.android.xposed.XposedHelpers.setStaticObjectField;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.content.SharedPreferences;
import android.os.Build;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class FauxBuild implements IXposedHookLoadPackage {

	public static final String PACKAGE_NAME = FauxBuild.class.getPackage()
			.getName();

	@Override
	public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
		XposedBridge.log("FB: App: " + lpparam.packageName);

		SharedPreferences prefs = new XSharedPreferences(PACKAGE_NAME);
		Map<String, String> values;
		values = this.parseStringSet(prefs.getStringSet("-", null));
		values.putAll(this.parseStringSet(prefs.getStringSet(
				lpparam.packageName, null)));

		XposedBridge.log("FB: Build: " + values);

		for (Map.Entry<String, String> entry : values.entrySet()) {
			try {
				setStaticObjectField(Build.class, entry.getKey(),
						entry.getValue());
			} catch (NoSuchFieldError e) {
				XposedBridge.log("FB: Error: " + e.getMessage());
			}
		}
	}

	private Map<String, String> parseStringSet(Set<String> stringSet) {
		Map<String, String> map = new HashMap<String, String>();
		if (stringSet != null) {
			for (String string : stringSet) {
				String[] pieces = string.split("=", 2);
				if (pieces.length != 2) {
					continue;
				}
				map.put(pieces[0], pieces[1]);
			}
		}
		return map;
	}
}
