/*
 * This file is part of ThemedBuild.
 *
 * ThemedBuild is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ThemedBuild is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ThemedBuild.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */
package com.mcmiddleearth.themedbuild;

import com.mcmiddleearth.themedbuild.domain.DBmanager;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * The type Load scheduler.
 */
class LoadScheduler extends BukkitRunnable {

    @Override
    public void run() {
        ThemedBuildPlugin.getPluginInstance().getLogger().info("Trying load data...");
        DBmanager.loadAll();
        if (!DBmanager.loaded) {
            ThemedBuildPlugin.getPluginInstance().getLogger().info("Load data failed, retrying in 100 ticks");
        } else {
            ThemedBuildPlugin.getPluginInstance().getLogger().info("Plugin data loaded");
            this.cancel();
        }
    }
}
