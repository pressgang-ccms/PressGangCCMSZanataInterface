/*
  Copyright 2011-2014 Red Hat

  This file is part of PresGang CCMS.

  PresGang CCMS is free software: you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  PresGang CCMS is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with PresGang CCMS.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.jboss.pressgang.ccms.zanata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zanata.common.LocaleId;

/**
 * The LocaleManager handles what locales per project should be used to check against the zanata REST interface . All functions
 * lock the list of locales to ensure thread safety since the locales maybe accessed from multiple concurrent threads.
 */
public class ZanataLocaleManager {
    private static final Logger LOG = LoggerFactory.getLogger(ZanataLocaleManager.class);

    private List<LocaleId> locales = new ArrayList<LocaleId>();

    private static final Map<String, ZanataLocaleManager> projectToLocales = new HashMap<String, ZanataLocaleManager>();

    public static ZanataLocaleManager getInstance(final String project) {
        if (!projectToLocales.containsKey(project))
            projectToLocales.put(project, new ZanataLocaleManager());
        return projectToLocales.get(project);
    }

    private ZanataLocaleManager() {

    }

    /**
     * Get an Unmodifiable List of available Locales.
     * 
     * @return A new Unmodifiable List containing only valid Locales.
     */
    public List<LocaleId> getLocales() {
        synchronized (locales) {
            /*
             * return a read only copy of the list of locales as it stands now. we can't return a reference to the list, because
             * it is possible that the returned list will be looped over outside of a synchonization block, and edited at the
             * same time.
             * 
             * Note from Lee: The above statement isn't properly correct since the returned list is a new list that doesn't
             * reference the locales object directly. However it is still handy to ensure that the list can't be modified as it
             * makes sure that someone who wants to add a locale does it properly.
             */
            return Collections.unmodifiableList(new ArrayList<LocaleId>(locales));
        }
    }

    /**
     * Set the list of locales that should be managed.
     * 
     * @param locales A list of locales to be managed.
     */
    public void setLocales(final List<LocaleId> locales) {
        synchronized (this.locales) {
            this.locales = locales;
        }
    }

    /**
     * Add a locale to the list of managed locales.
     * 
     * @param locale The new locale to added.
     */
    public void addLocale(final LocaleId locale) {
        synchronized (this.locales) {
            if (this.locales == null) {
                this.locales = new ArrayList<LocaleId>();
            }
            this.locales.add(locale);
        }
    }

    /**
     * Remove a locale from being managed.
     * 
     * @param locale The locale to be removed.
     */
    public void removeLocale(final LocaleId locale) {
        LOG.info("Removing " + locale + " from further sync requests.");
        synchronized (locales) {
            if (locales.contains(locale))
                locales.remove(locale);
        }
    }
}
