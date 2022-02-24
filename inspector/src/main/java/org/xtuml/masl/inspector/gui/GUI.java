// 
// Filename : GUI.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui;

import javax.swing.InputMap;
import javax.swing.KeyStroke;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.text.DefaultEditorKit;

import org.xtuml.masl.inspector.Preferences;

public class GUI {

    public static void installUI() {
        System.setProperty("swing.plaf.metal.controlFont", Preferences.getFontName() + "-" + Preferences.getFontSize());
        System.setProperty("swing.plaf.metal.systemFont", Preferences.getFontName() + "-" + Preferences.getFontSize());
        System.setProperty("swing.plaf.metal.userFont", Preferences.getFontName() + "-" + Preferences.getFontSize());
        System.setProperty("swing.plaf.metal.smallFont",
                Preferences.getFontName() + "-" + (Preferences.getFontSize() - 2));

        if (UIManager.getLookAndFeel() instanceof javax.swing.plaf.metal.MetalLookAndFeel) {
            UIManager.put("List.font",
                    new UIDefaults.ProxyLazyValue("javax.swing.plaf.metal.MetalLookAndFeel", "getControlTextFont"));
            UIManager.put("Panel.font",
                    new UIDefaults.ProxyLazyValue("javax.swing.plaf.metal.MetalLookAndFeel", "getControlTextFont"));
            ((InputMap) UIManager.get("TextField.focusInputMap")).put(KeyStroke.getKeyStroke("DELETE"),
                    DefaultEditorKit.deletePrevCharAction);
            ((InputMap) UIManager.get("TextArea.focusInputMap")).put(KeyStroke.getKeyStroke("DELETE"),
                    DefaultEditorKit.deletePrevCharAction);
        }

        org.xtuml.masl.inspector.gui.modelView.ObjectDisplay.installUI();

    }

}
