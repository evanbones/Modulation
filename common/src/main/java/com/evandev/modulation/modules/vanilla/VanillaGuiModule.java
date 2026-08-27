package com.evandev.modulation.modules.vanilla;

import com.evandev.modulation.api.AbstractModule;
import com.evandev.modulation.api.IModule;
import com.evandev.modulation.api.tweaks.BooleanTweak;
import com.evandev.modulation.api.tweaks.IntTweak;
import com.google.auto.service.AutoService;

@AutoService(IModule.class)
public class VanillaGuiModule extends AbstractModule {

    private final BooleanTweak ctrlDragToCraftingGrid = tweak(new BooleanTweak("ctrl_drag_to_crafting_grid", true));
    private final IntTweak chatMessageDuration = tweak(new IntTweak("chat_message_duration", 200));
    private final BooleanTweak disableCreativeInventory = tweak(new BooleanTweak("disable_creative_inventory", false));
    private final BooleanTweak creativeDeletionButton = tweak(new BooleanTweak("creative_deletion_button", true));
    private final IntTweak clearButtonX = tweak(new IntTweak("clear_button_x", 148));
    private final IntTweak clearButtonY = tweak(new IntTweak("clear_button_y", 162));
    private final BooleanTweak chatMarkdown = tweak(new BooleanTweak("chat_markdown", true));
    private final BooleanTweak cursorFeedback = tweak(new BooleanTweak("cursor_feedback", true));

    public VanillaGuiModule() {
        super("vanilla_gui");
    }

    public boolean isCtrlDragToCraftingGridEnabled() {
        return ctrlDragToCraftingGrid.getValue();
    }

    public int getChatMessageDuration() {
        return chatMessageDuration.getValue();
    }

    public boolean isDisableCreativeInventoryEnabled() {
        return disableCreativeInventory.getValue();
    }

    public boolean isCreativeDeletionButtonEnabled() {
        return creativeDeletionButton.getValue();
    }

    public int getClearButtonX() {
        return clearButtonX.getValue();
    }

    public int getClearButtonY() {
        return clearButtonY.getValue();
    }

    public boolean isChatMarkdownEnabled() {
        return chatMarkdown.getValue();
    }

    public boolean isCursorFeedbackEnabled() {
        return cursorFeedback.getValue();
    }
}
