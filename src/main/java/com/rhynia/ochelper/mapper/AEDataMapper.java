package com.rhynia.ochelper.mapper;

import com.rhynia.ochelper.var.AEFluidData;
import com.rhynia.ochelper.var.AEItemData;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface AEDataMapper {

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

    @Insert("INSERT INTO ${un} (id, size) VALUES (${id}, ${size})")
    void insertAEData(@Param("un") String un, @Param("id") long id, @Param("size") String size);

    @Delete("DELETE FROM ${un} WHERE id NOT IN (SELECT id FROM ${un} ORDER BY id DESC LIMIT ${keepSize});")
    void cleanupAEData(@Param("un") String un, @Param("keepSize") int keepSize);

    @Select("CREATE TABLE IF NOT EXISTS \" + un + \" (id INTEGER PRIMARY KEY NOT NULL, size TEXT, time TIMESTAMP DEFAULT CURRENT_TIMESTAMP)")
    void createIfNotExist(@Param("un") String un);

}
