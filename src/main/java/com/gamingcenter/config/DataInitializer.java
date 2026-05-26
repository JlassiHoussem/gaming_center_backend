package com.gamingcenter.config;

import com.gamingcenter.entity.Device;
import com.gamingcenter.entity.Product;
import com.gamingcenter.entity.Settings;
import com.gamingcenter.repository.DeviceRepository;
import com.gamingcenter.repository.ProductRepository;
import com.gamingcenter.repository.SettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final DeviceRepository deviceRepository;
    private final ProductRepository productRepository;
    private final SettingsRepository settingsRepository;

    @Override
    public void run(String... args) {
        if (deviceRepository.count() == 0) {
            deviceRepository.save(Device.builder().name("PC-01").type(Device.DeviceType.PC).hourlyRate(5.0).status(Device.DeviceStatus.LIBRE).build());
            deviceRepository.save(Device.builder().name("PC-02").type(Device.DeviceType.PC).hourlyRate(5.0).status(Device.DeviceStatus.LIBRE).build());
            deviceRepository.save(Device.builder().name("PC-03").type(Device.DeviceType.PC).hourlyRate(5.0).status(Device.DeviceStatus.LIBRE).build());
            deviceRepository.save(Device.builder().name("PS5-01").type(Device.DeviceType.PS5).hourlyRate(7.0).status(Device.DeviceStatus.LIBRE).build());
            deviceRepository.save(Device.builder().name("PS5-02").type(Device.DeviceType.PS5).hourlyRate(7.0).status(Device.DeviceStatus.LIBRE).build());
            deviceRepository.save(Device.builder().name("PS4-01").type(Device.DeviceType.PS4).hourlyRate(6.0).status(Device.DeviceStatus.LIBRE).build());
            deviceRepository.save(Device.builder().name("Xbox-01").type(Device.DeviceType.XBOX).hourlyRate(6.0).status(Device.DeviceStatus.LIBRE).build());
            deviceRepository.save(Device.builder().name("Simulateur-01").type(Device.DeviceType.SIMULATEUR).hourlyRate(10.0).status(Device.DeviceStatus.LIBRE).build());
        }

        if (productRepository.count() == 0) {
            productRepository.save(Product.builder().name("Coca-Cola").category("Boissons").price(2.5).active(true).build());
            productRepository.save(Product.builder().name("Fanta").category("Boissons").price(2.5).active(true).build());
            productRepository.save(Product.builder().name("Eau Minérale").category("Boissons").price(1.5).active(true).build());
            productRepository.save(Product.builder().name("Red Bull").category("Boissons").price(3.5).active(true).build());
            productRepository.save(Product.builder().name("Chips").category("Snacks").price(2.0).active(true).build());
            productRepository.save(Product.builder().name("Chocolate").category("Snacks").price(1.5).active(true).build());
            productRepository.save(Product.builder().name("Sandwich").category("Repas").price(5.0).active(true).build());
            productRepository.save(Product.builder().name("Pizza").category("Repas").price(7.0).active(true).build());
        }

        if (settingsRepository.count() == 0) {
            settingsRepository.save(Settings.builder()
                    .establishmentName("Gaming Center Pro")
                    .currency("EUR")
                    .timezone("Europe/Paris")
                    .deviceTypes("PC,PS4,PS5,Xbox,Simulateur")
                    .buffetCategories("Boissons,Snacks,Repas")
                    .build());
        }
    }
}
