package com.gamingcenter.repository;

import com.gamingcenter.entity.Device;
import com.gamingcenter.entity.Device.DeviceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DeviceRepository extends JpaRepository<Device, Long> {
    List<Device> findByStatus(DeviceStatus status);
    boolean existsByName(String name);
}
