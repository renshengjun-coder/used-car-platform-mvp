package com.usedcar.seed;

import com.usedcar.domain.Listing;
import com.usedcar.domain.Seller;
import com.usedcar.listing.ListingChangedEvent;
import com.usedcar.mapper.ListingMapper;
import com.usedcar.mapper.SellerMapper;
import com.usedcar.search.es.EsSyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** Seeds a demo seller and listings when the catalog is empty (demo convenience). */
@Component
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
public class DataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final SellerMapper sellerMapper;
    private final ListingMapper listingMapper;
    private final EsSyncService esSyncService;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(SellerMapper sellerMapper, ListingMapper listingMapper,
                      EsSyncService esSyncService, PasswordEncoder passwordEncoder) {
        this.sellerMapper = sellerMapper;
        this.listingMapper = listingMapper;
        this.esSyncService = esSyncService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (sellerMapper.findByEmail("demo@usedcar.dev") != null) {
            log.info("Seed skipped: demo data already present");
            return;
        }
        Seller seller = new Seller();
        seller.setEmail("demo@usedcar.dev");
        seller.setPasswordHash(passwordEncoder.encode("demo1234"));
        seller.setDisplayName("Demo Dealer");
        sellerMapper.insert(seller);

        String[][] cars = {
                {"Toyota", "Camry", "2019", "158000", "42000", "Beijing", "GASOLINE", "AUTOMATIC"},
                {"Honda", "Accord", "2020", "175000", "30000", "Shanghai", "GASOLINE", "AUTOMATIC"},
                {"Tesla", "Model 3", "2021", "229000", "25000", "Shenzhen", "EV", "AUTOMATIC"},
                {"Volkswagen", "Lavida", "2018", "98000", "60000", "Guangzhou", "GASOLINE", "AUTOMATIC"},
                {"BMW", "3 Series", "2019", "268000", "38000", "Beijing", "GASOLINE", "AUTOMATIC"},
                {"Audi", "A4L", "2020", "289000", "28000", "Hangzhou", "GASOLINE", "AUTOMATIC"},
                {"Toyota", "Corolla", "2021", "118000", "18000", "Chengdu", "HYBRID", "AUTOMATIC"},
                {"BYD", "Han", "2022", "215000", "12000", "Shenzhen", "EV", "AUTOMATIC"},
                {"Nissan", "Sylphy", "2018", "89000", "70000", "Wuhan", "GASOLINE", "AUTOMATIC"},
                {"Mercedes-Benz", "C-Class", "2019", "275000", "40000", "Shanghai", "GASOLINE", "AUTOMATIC"},
                {"Honda", "Civic", "2020", "135000", "26000", "Beijing", "GASOLINE", "MANUAL"},
                {"Toyota", "RAV4", "2021", "189000", "22000", "Nanjing", "HYBRID", "AUTOMATIC"},
        };

        for (String[] c : cars) {
            Listing l = new Listing();
            l.setSellerId(seller.getId());
            l.setTitle(c[2] + " " + c[0] + " " + c[1]);
            l.setMake(c[0]);
            l.setModel(c[1]);
            l.setYear(Integer.parseInt(c[2]));
            l.setPrice(new BigDecimal(c[3]));
            l.setMileage(Integer.parseInt(c[4]));
            l.setCity(c[5]);
            l.setFuelType(c[6]);
            l.setTransmission(c[7]);
            l.setDescription("Well-maintained " + c[0] + " " + c[1] + ", one owner, full service history.");
            l.setStatus("PUBLISHED");
            l.setPublishedAt(LocalDateTime.now());
            listingMapper.insert(l);
            listingMapper.insertPhotos(l.getId(),
                    List.of("https://picsum.photos/seed/car" + l.getId() + "/640/480"));
        }

        int indexed = esSyncService.reindexAll();
        log.info("Seeded {} demo listings, indexed {} into Elasticsearch", cars.length, indexed);
    }
}
