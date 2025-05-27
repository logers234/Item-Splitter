package net.logangwin.itemsplitter.gui;

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import net.minecraft.text.Text;

public class Gui extends LightweightGuiDescription {
    public Gui(){
        WGridPanel root = new WGridPanel();
        setRootPanel(root);
        root.setSize(300, 200);

        WLabel label = new WLabel(Text.of("Split Stack"));
        root.add(label, 1, 1);
    }
}
