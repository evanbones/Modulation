package com.evandev.modulation.modules.vanilla;

import com.evandev.modulation.api.AbstractModule;
import com.evandev.modulation.api.IModule;
import com.evandev.modulation.api.ModuleDef;
import com.evandev.modulation.api.tweaks.BooleanTweak;
import com.evandev.modulation.api.tweaks.IntTweak;
import com.google.auto.service.AutoService;

@AutoService(IModule.class)
public class VanillaGuiModule extends AbstractModule {

    private static final String PANORAMA_SCREENSHOT_MOD = "panorama_screenshot";

    private static final ModuleDef DEF = ModuleDef.of("vanilla_gui");

    public static final BooleanTweak CTRL_DRAG_TO_CRAFTING_GRID = DEF.bool("ctrl_drag_to_crafting_grid", true);
    public static final IntTweak CHAT_MESSAGE_DURATION = DEF.integer("chat_message_duration", 200);
    public static final BooleanTweak DISABLE_CREATIVE_INVENTORY = DEF.bool("disable_creative_inventory", false);
    public static final BooleanTweak CREATIVE_DELETION_BUTTON = DEF.bool("creative_deletion_button", true);
    public static final IntTweak CLEAR_BUTTON_X = DEF.integer("clear_button_x", 148);
    public static final IntTweak CLEAR_BUTTON_Y = DEF.integer("clear_button_y", 162);
    public static final BooleanTweak CHAT_MARKDOWN = DEF.bool("chat_markdown", true);
    public static final BooleanTweak CURSOR_FEEDBACK = DEF.bool("cursor_feedback", true);
    public static final BooleanTweak SLOT_HIGHLIGHT_BEHIND_ITEM = DEF.bool("slot_highlight_behind_item", true);
    public static final BooleanTweak PANORAMA_SCREENSHOT = DEF.bool("panorama_screenshot", true).conflicts(PANORAMA_SCREENSHOT_MOD);
    public static final IntTweak PANORAMA_RESOLUTION = DEF.integer("panorama_resolution", 1024).range(16, 8192).conflicts(PANORAMA_SCREENSHOT_MOD);

    public VanillaGuiModule() {
        super(DEF);
    }
}
