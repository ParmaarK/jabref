package org.jabref.collab;

import javax.swing.JComponent;
import javax.swing.JScrollPane;

import org.jabref.gui.BasePanel;
import org.jabref.gui.undo.NamedCompound;
import org.jabref.gui.undo.UndoableInsertString;
import org.jabref.logic.l10n.Localization;
import org.jabref.model.database.BibDatabase;
import org.jabref.model.database.KeyCollisionException;
import org.jabref.model.entry.BibtexString;

import com.jcabi.log.Logger;

class StringAddChange extends Change {


    private final BibtexString string;
    private final InfoPane tp = new InfoPane();

    private final JScrollPane sp = new JScrollPane(tp);


    public StringAddChange(BibtexString string) {
        super(Localization.lang("Added string") + ": '" + string.getName() + '\'');
        this.string = string;
        tp.setText("<HTML><H2>" + Localization.lang("Added string") + "</H2><H3>" +
                Localization.lang("Label") + ":</H3>" + string.getName() + "<H3>" +
                Localization.lang("Content") + ":</H3>" + string.getContent() + "</HTML>");
    }

    @Override
    public boolean makeChange(BasePanel panel, BibDatabase secondary, NamedCompound undoEdit) {

        if (panel.getDatabase().hasStringLabel(string.getName())) {
            // The name to change to is already in the database, so we can't comply.
            Logger.info(this, "Cannot add string '" + string.getName() + "' because the name "
                    + "is already in use.");
        }

        try {
            panel.getDatabase().addString(string);
            undoEdit.addEdit(new UndoableInsertString(panel, panel.getDatabase(), string));
        } catch (KeyCollisionException ex) {
            Logger.info(this, "Error: could not add string '" + string.getName() + "': %[exception]s", ex);
        }
        try {
            secondary.addString(new BibtexString(string.getName(), string.getContent()));
        } catch (KeyCollisionException ex) {
            Logger.info(this, "Error: could not add string '" + string.getName() + "' to tmp database: %[exception]s", ex);
        }
        return true;
    }

    @Override
    public JComponent description() {
        return sp;
    }

}
