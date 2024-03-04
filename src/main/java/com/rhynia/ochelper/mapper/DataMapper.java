package com.rhynia.ochelper.mapper;

import com.rhynia.ochelper.var.AEFluidData;
import com.rhynia.ochelper.var.AEItemData;
import com.rhynia.ochelper.var.EnergyData;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface DataMapper {

    @Select("SELECT name FROM sqlite_master WHERE type='table' ORDER BY name;")
    List<String> getAllNamesInDataBase();

    @Select("SELECT * FROM ${un} ORDER BY id DESC LIMIT 1")
    AEItemData getAEItemDataLatest(@Param("un") String un);

    @Select("SELECT * FROM ${un} ORDER BY id DESC LIMIT 1")
    AEFluidData getAEFluidDataLatest(@Param("un") String un);

    @Select("SELECT * FROM ${un} ORDER BY id DESC LIMIT 5")
    List<AEItemData> getAEItemDataLate5(@Param("un") String un);

    @Select("SELECT * FROM ${un} ORDER BY id DESC LIMIT 5")
    List<AEFluidData> getAEFluidDataLate5(@Param("un") String un);

    @Select("SELECT * FROM ${un} ORDER BY id DESC LIMIT ${n}")
    List<AEItemData> getAEItemDataLateN(@Param("un") String un, @Param("n") int n);

    @Select("SELECT * FROM ${un} ORDER BY id DESC LIMIT ${n}")
    List<AEFluidData> getAEFluidDataLateN(@Param("un") String un, @Param("n") int n);

    @Select("SELECT * FROM energy$wireless ORDER BY id DESC LIMIT ${n}")
    List<EnergyData> getEnergyInfoLateN(@Param("n") int n);

    @Insert("INSERT INTO energy$wireless (id, size) VALUES (${id}, ${size})")
    void updateEnergyInfo(@Param("id") long id, @Param("size") String size);

}
