package org.jboss.pressgang.ccms.zanata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zanata.common.LocaleId;


/**
 * The LocaleManager handles what locales per project should be used to check against the zanata REST interface . All functions lock the list of locales to
 * ensure thread safety since the locales maybe accessed from multiple concurrent threads.
 */
public class ZanataLocaleManager
{

	private List<LocaleId> locales = new ArrayList<LocaleId>();

	private static final Map<String, ZanataLocaleManager> projectToLocales = new HashMap<String, ZanataLocaleManager>();

	public static ZanataLocaleManager getInstance(final String project)
	{
		if (!projectToLocales.containsKey(project))
			projectToLocales.put(project, new ZanataLocaleManager());
		return projectToLocales.get(project);
	}

	private ZanataLocaleManager()
	{

	}

	/**
	 * Get an Unmodifiable List of available Locales.
	 * 
	 * @return A new Unmodifiable List containing only valid Locales.
	 */
	public List<LocaleId> getLocales()
	{
		synchronized (locales)
		{
			/*
			 * return a read only copy of the list of locales as it stands now. we can't return a reference to the list, because it is possible that the
			 * returned list will be looped over outside of a synchonization block, and edited at the same time.
			 * 
			 * Note from Lee: The above statement isn't properly correct since the returned list is a new list that doesn't reference the locales object directly.
			 * However it is still handy to ensure that the list can't be modified as it makes sure that someone who wants to add a locale does it properly.
			 */
			return Collections.unmodifiableList(new ArrayList<LocaleId>(locales));
		}
	}

	public void setLocales(final List<LocaleId> locales)
	{
		synchronized (this.locales)
		{
			this.locales = locales;
		}
	}
	
	public void addLocale(final LocaleId locale)
	{
		synchronized (this.locales)
		{
			if (this.locales == null)
			{
				this.locales = new ArrayList<LocaleId>();
			}
			this.locales.add(locale);
		}
	}

	public void removeLocale(final LocaleId locale)
	{
		System.out.println("Removing " + locale + " from further sync requests.");
		synchronized (locales)
		{
			if (locales.contains(locale))
				locales.remove(locale);
		}
	}
}
