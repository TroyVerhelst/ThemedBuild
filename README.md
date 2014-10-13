PlotManager
===========

Current Author(s): DonoA, Ivan1pl

Authors: DonoA, aaldim, Ivan1pl

The PlotManager is a plugin designed to aid staff members in the generation of free build plots. It also blocks any non staff from building/griefing other builder's plots. The only command in this plugin is '/theme'.

'Theme' - this command just on its own sends the executer to a new plot in the current theme for them to build on, it also provides them building permissions on that plot.

'Theme set [-m model] <theme name>' - this command should only be used by administrators to start off a chain of themes. The theme name cannot be the same as an old theme and will appear on every builders plot sign. Players who use '/theme' will be sent to this theme instead of any old ones. If model is not specified, default model will be used.

'Theme new [-m model] <theme name>' - this command should be used by a staff member to create a new theme after the last one. The theme name cannot be the same as an old theme and will appear on every builders plot sign. Players who use '/theme' will be sent to this theme instead of any old ones.  If model is not specified, default model will be used.

'Theme createmodel <name>' - this command should be used by a staff member to create a model, which can later be used as base model for all plots in a theme. Each theme has exactly one model. Default model is 48x48 empty area.

'Theme modelpos <1|2>' - this command should be used by a staff member to set points of a model. Selection tool also exists, wooden sword by default.

'Theme savemodel' - this command should be used by a staff member to save a model. Unsaved models can't be used.

'Theme listmodels' - this commands lists all saved models.

'Theme deletemodel <name>' - this command deletes a model.

'Theme setURL <url>' - this command should be used by a staff member to assign URL to theme. URL will be displayed when player claims a plot.

Features:
* custom plots based off a model
* limitations on the number of plots that can be claimed by one player
* plot size changing
* allowance for players to use lava/water inside their plot
* the insertion of a URL to the theme thread
