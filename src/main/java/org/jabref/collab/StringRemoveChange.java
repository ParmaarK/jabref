package org.jabref.collab;

import javax.swing.JComponent;
import javax.swing.JScrollPane;

import org.jabref.gui.BasePanel;
import org.jabref.gui.undo.NamedCompound;
import org.jabref.gui.undo.UndoableRemoveString;
import org.jabref.logic.l10n.Localization;
import org.jabref.model.database.BibDatabase;
import org.jabref.model.entry.BibtexString;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

class StringRemoveChange extends Change {
    private static final Log LOGGER = LogFactory.getLog(StringRemoveChange.class);
    private final BibtexString string;

    private final BibtexString inMem;
    private final InfoPane tp = new InfoPane();
    private final JScrollPane sp = new JScrollPane(tp);

    private final BibtexString tmpString;


    public StringRemoveChange(BibtexString string, BibtexString tmpString, BibtexString inMem) {
        super(Localization.lang("Removed string") + ": '" + string.getName() + '\'');
        this.tmpString = tmpString;
        this.string = string;
        this.inMem = inMem; // Holds the version in memory. Check if it has been modified...?

        tp.setText("<HTML><H2>" + Localization.lang("Removed string") + "</H2><H3>" +
                Localization.lang("Label") + ":</H3>" + string.getName() + "<H3>" +
                Localization.lang("Content") + ":</H3>" + string.getContent() + "</HTML>");
    }

    @Override
    public boolean makeChange(BasePanel panel, BibDatabase secondary, NamedCompound undoEdit) {

        try {
            panel.getDatabase().removeString(inMem.getId());
            undoEdit.addEdit(new UndoableRemoveString(panel, panel.getDatabase(), string));
        } catch (Exception ex) {
            LOGGER.info("Error: could not add string '" + string.getName() + "': " + ex.getMessage(), ex);
        }

        // Update tmp database:
        secondary.removeString(tmpString.getId());

        return true;
    }

    @Override
    public JComponent description() {
        return sp;
    }

}
