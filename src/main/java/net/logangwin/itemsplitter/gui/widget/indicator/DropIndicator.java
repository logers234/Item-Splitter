package net.logangwin.itemsplitter.gui.widget.indicator;

import net.minecraft.util.Identifier;

public class DropIndicator extends Indicator {

    DropIndicator(int x, int y) {
        super(x, y, Identifier.of("item-splitter", "resources/indicator.item-splitter/drop_indicator"));
    }
}
