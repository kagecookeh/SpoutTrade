package net.ark3l.SpoutTrade.Config;

import java.io.File;
import java.util.List;

/**
 * @author Oliver Brown (Arkel)
 *         Date: 08/09/11
 */
public class LanguageManager extends ConfigClass {

    public enum Strings {OPTION, ONLINE, BUSY, REQUESTED, TOACCEPT, TODECLINE, CANCELLED, CONFIRMED, NOTYOURS, NOROOM, FINISHED, SURE, SENT, TIMED, DECLINED}

    private List<Object> stringList;

    public LanguageManager(File dataFolder) {
        super(dataFolder, new File(dataFolder, "language.yml"));
        stringList = config.getList("Language");
    }

    public String getString(Strings type) {
        return (String) stringList.get(type.ordinal());
    }
}
