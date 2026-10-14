package com.rationportal.controller;

import com.rationportal.model.FpsDealerApplication;
import com.rationportal.repository.FpsDealerApplicationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/fair-price-shops")
public class FairPriceShopController {
    private final FpsDealerApplicationRepository dealers;
    public FairPriceShopController(FpsDealerApplicationRepository dealers){this.dealers=dealers;}

    @GetMapping
    public ResponseEntity<?> search(@RequestParam(required=false) String district,
            @RequestParam(required=false,name="search") String locality,
            @RequestParam(required=false) Double latitude,
            @RequestParam(required=false) Double longitude) {
        if (district!=null&&district.length()>100 || locality!=null&&locality.length()>100)
            return ResponseEntity.badRequest().body(Map.of("message","Search terms must be 100 characters or fewer."));
        if ((latitude == null) != (longitude == null))
            return ResponseEntity.badRequest().body(Map.of("message", "A valid latitude and longitude pair is required."));
        if (latitude != null && longitude != null
                && (latitude.doubleValue() < -90.0 || latitude.doubleValue() > 90.0 || longitude.doubleValue() < -180.0 || longitude.doubleValue() > 180.0))
            return ResponseEntity.badRequest().body(Map.of("message", "A valid latitude and longitude pair is required."));
        String d=clean(district), q=clean(locality);
        List<Map<String,Object>> results=new ArrayList<>();
        for(FpsDealerApplication shop:dealers.findByStatusIgnoreCase("APPROVED")) {
            if(!d.isBlank()&&!contains(shop.getDistrict(),d))continue;
            if(!q.isBlank()&&!contains(shop.getShopName(),q)&&!contains(shop.getShopAddress(),q)&&!contains(shop.getMandalVillage(),q))continue;
            Map<String,Object> item=new LinkedHashMap<>();
            item.put("fpsId",shop.getFpsId()); item.put("shopName",shop.getShopName());
            item.put("dealerName",shop.getDealerName()); item.put("address",shop.getShopAddress());
            item.put("district",shop.getDistrict()); item.put("mandalVillage",shop.getMandalVillage());
            item.put("phone",shop.getMobile()); item.put("latitude",shop.getLatitude()); item.put("longitude",shop.getLongitude());
            if (latitude != null && longitude != null) {
                if (shop.getLatitude() == null || shop.getLongitude() == null) continue;
                double km = distance(latitude.doubleValue(), longitude.doubleValue(),
                        shop.getLatitude().doubleValue(), shop.getLongitude().doubleValue());
                item.put("distanceKm", Math.round(km * 100.0) / 100.0);
            }
            results.add(item);
        }
        if(latitude!=null)results.sort(Comparator.comparingDouble(x->((Number)x.get("distanceKm")).doubleValue()));
        else results.sort(Comparator.comparing(x->String.valueOf(x.get("shopName")),String.CASE_INSENSITIVE_ORDER));
        return ResponseEntity.ok(results);
    }
    private String clean(String s){return s==null?"":s.trim();}
    private boolean contains(String value,String query){return value!=null&&value.toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT));}
    private double distance(double lat1,double lon1,double lat2,double lon2){
        double earth=6371.0, dLat=Math.toRadians(lat2-lat1), dLon=Math.toRadians(lon2-lon1);
        double a=Math.sin(dLat/2)*Math.sin(dLat/2)+Math.cos(Math.toRadians(lat1))*Math.cos(Math.toRadians(lat2))*Math.sin(dLon/2)*Math.sin(dLon/2);
        return earth*2*Math.atan2(Math.sqrt(a),Math.sqrt(1-a));
    }
}
