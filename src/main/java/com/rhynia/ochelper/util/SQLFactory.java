package com.rhynia.ochelper.util;

import cn.hutool.core.lang.Snowflake;
import com.rhynia.ochelper.var.AEFluid;
import com.rhynia.ochelper.var.AEItem;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;

@Component
public class SQLFactory {
    private final Snowflake sid = new Snowflake(1);

    public String[] generateBatchCheck(List<String> un) {
        HashSet<String> set = new HashSet<>();
        for (String s : un) {
            String l = "CREATE TABLE IF NOT EXISTS " +
                    s +
                    " (id INTEGER PRIMARY KEY NOT NULL, size TEXT, time TIMESTAMP DEFAULT CURRENT_TIMESTAMP);";
            set.add(l);
        }
        return set.toArray(new String[0]);
    }

    public String[] generateCleanup(List<String> un, int keepSize) {
        HashSet<String> set = new HashSet<>();
        for (String s : un) {
            String l = "DELETE FROM " + s + " WHERE id NOT IN (SELECT id FROM " + s + " ORDER BY id DESC LIMIT " + keepSize + ");";
            set.add(l);
        }
        return set.toArray(new String[0]);
    }

    public String[] generateItemBatchInsert(List<AEItem> rl) {
        String[] s = new String[rl.size()];
        for (int i = 0; i < rl.size(); i++) {
            AEItem item = rl.get(i);
            String l = "INSERT INTO " +
                    item.getUn() +
                    " (id, size) VALUES ('" +
                    sid.nextIdStr() + "', '" +
                    item.getSizeString() +
                    "'); ";
            s[i] = l;
        }
        return s;
    }

    public String[] generateFluidBatchInsert(List<AEFluid> rl) {
        String[] s = new String[rl.size()];
        for (int i = 0; i < rl.size(); i++) {
            AEFluid fluid = rl.get(i);
            String l = "INSERT INTO " +
                    fluid.getUn() +
                    " (id, size) VALUES ('" +
                    sid.nextIdStr() + "', '" +
                    fluid.getSizeString() +
                    "'); ";
            s[i] = l;
        }
        return s;
    }

}
