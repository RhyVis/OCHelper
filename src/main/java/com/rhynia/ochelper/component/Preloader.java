package com.rhynia.ochelper.component;

import com.csvreader.CsvReader;
import com.rhynia.ochelper.config.Path;
import com.rhynia.ochelper.dao.SwitchFluidDao;
import com.rhynia.ochelper.var.SwitchFluid;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.rhynia.ochelper.util.LocalizationMap.NAME_MAP_FLUID;
import static com.rhynia.ochelper.util.LocalizationMap.NAME_MAP_ITEM;
import static com.rhynia.ochelper.util.LocalizationMap.NAME_MAP_FLUID_SWITCH;

@Component
public class Preloader implements CommandLineRunner {

    private final Path path;
    private final SwitchFluidDao sf;

    public Preloader(Path path, SwitchFluidDao sf) {
        this.path = path;
        this.sf = sf;
    }

    @Override
    public void run(String... args) throws Exception {

        // CSV Loader
        CsvReader csvItem = new CsvReader(path.getCsvPath() + "itempanel.csv", ',', StandardCharsets.UTF_8);
        csvItem.readHeaders();
        while (csvItem.readRecord()) {
            Pair<String, Integer> subject = Pair.of(csvItem.get(0), Integer.valueOf(csvItem.get(2)));
            NAME_MAP_ITEM.put(subject, csvItem.get(4));
        }
        CsvReader fluidCSV = new CsvReader(path.getCsvPath() + "fluid.csv", ',', StandardCharsets.UTF_8);
        fluidCSV.readHeaders();
        while (fluidCSV.readRecord()) {
            NAME_MAP_FLUID.put(fluidCSV.get(1), fluidCSV.get(2));
        }

        // JSON Loader
        List<SwitchFluid> sf_List = sf.getSFList();
        for (SwitchFluid sf : sf_List) {
            NAME_MAP_FLUID_SWITCH.put(sf.getPre(), sf.getAlt());
        }

    }
}
