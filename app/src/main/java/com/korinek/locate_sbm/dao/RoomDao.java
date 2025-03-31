package com.korinek.locate_sbm.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.korinek.locate_sbm.model.Room;
import com.korinek.locate_sbm.model.RoomWithWifiFingerprints;
import com.korinek.locate_sbm.model.WifiFingerprint;

import java.util.List;

@Dao
public interface RoomDao {
    // Room
    @Insert
    void insert(Room room);

    @Update
    void update(Room room);

    @Delete
    void delete(Room room);

    @Query("UPDATE rooms SET lastCalibrationTimestamp = :timestamp WHERE id = :roomId")
    void setRoomLastCalibrationDate(int roomId, long timestamp);


    // WifiFingerprint
    @Insert
    void insertWifiFingerprint(WifiFingerprint fingerprint);

    @Insert
    void insertWifiFingerprints(List<WifiFingerprint> fingerprints);

    @Query("DELETE FROM wifi_fingerprints WHERE roomId = :roomId")
    void deleteWifiFingerprints(int roomId);


    // RoomWithWifiFingerprints
    @Transaction
    default void insertRoomWithWifiFingerprints(RoomWithWifiFingerprints roomWithFingerprints) {
        insert(roomWithFingerprints.getRoom());
        if(!roomWithFingerprints.getWifiFingerprints().isEmpty()) {
            insertWifiFingerprints(roomWithFingerprints.getWifiFingerprints());
        }
    }

    @Transaction
    @Query("SELECT * FROM rooms WHERE id = :roomId")
    RoomWithWifiFingerprints getRoomWithWifiFingerprints(int roomId);

    @Transaction
    @Query("SELECT * FROM rooms WHERE buildingId = :buildingId")
    List<RoomWithWifiFingerprints> getRoomsWithWifiFingerprintsForBuilding(int buildingId);

    @Transaction
    default void saveWifiFingerprintsList(int roomId, List<WifiFingerprint> wifiFingerprintList) {
        // delete previous fingerprints and insert new
        deleteWifiFingerprints(roomId);
        insertWifiFingerprints(wifiFingerprintList);

        // set last calibration date
        long timestamp = System.currentTimeMillis();
        setRoomLastCalibrationDate(roomId, timestamp);
    }
}
