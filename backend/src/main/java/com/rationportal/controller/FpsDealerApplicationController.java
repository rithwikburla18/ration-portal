package com.rationportal.controller;

import com.rationportal.model.FpsDealerApplication;
import com.rationportal.model.User;
import com.rationportal.repository.FpsDealerApplicationRepository;
import com.rationportal.repository.UserRepository;
import com.rationportal.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/fps-dealer-applications")
public class FpsDealerApplicationController {
    private static final Pattern EMAIL = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private final FpsDealerApplicationRepository applications;
    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notifications;
    private final JdbcTemplate jdbc;

    public FpsDealerApplicationController(FpsDealerApplicationRepository applications, UserRepository users,
            PasswordEncoder passwordEncoder, NotificationService notifications, JdbcTemplate jdbc) {
        this.applications=applications; this.users=users; this.passwordEncoder=passwordEncoder;
        this.notifications=notifications; this.jdbc=jdbc;
    }

    @PostMapping("/register")
    @Transactional
    public ResponseEntity<?> register(@RequestBody Map<String,Object> body)
        if (body == null) return ResponseEntity.badRequest().body(Map.of("message", "Dealer registration details are required.")); {
        String dealerName=value(body,"dealerName"), shopName=value(body,"shopName");
        String license=value(body,"licenseNumber").toUpperCase(Locale.ROOT);
        String mobile=value(body,"mobile"), email=value(body,"email").toLowerCase(Locale.ROOT);
        String password=value(body,"password"), district=value(body,"district");
        String mandal=value(body,"mandalVillage"), address=value(body,"shopAddress");
        if (ListFields.blank(dealerName,shopName,license,mobile,email,password,district,mandal,address))
            return ResponseEntity.badRequest().body(Map.of("message","Complete all required ration-shop registration fields."));
        if (!EMAIL.matcher(email).matches()) return ResponseEntity.badRequest().body(Map.of("message","Enter a valid email address."));
        if (!mobile.matches("^(?:\\+91)?[6-9][0-9]{9}$")) return ResponseEntity.badRequest().body(Map.of("message","Enter a valid Indian mobile number."));
        if (password.length()<8 || password.getBytes(StandardCharsets.UTF_8).length>72) return ResponseEntity.badRequest().body(Map.of("message","Password must contain 8 to 72 UTF-8 bytes."));
        if (!"true".equalsIgnoreCase(value(body,"termsAccepted"))) return ResponseEntity.badRequest().body(Map.of("message","Please accept the declaration before registering."));
        Double lat=number(body.get("latitude")), lon=number(body.get("longitude"));
        if (lat==null||lon==null||lat < -90||lat > 90||lon < -180||lon > 180)
            return ResponseEntity.badRequest().body(Map.of("message","Enter valid shop latitude and longitude."));
        if (users.findByEmail(email).isPresent()||applications.findByLicenseNumberIgnoreCase(license).isPresent())
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message","This email or FPS licence number is already registered."));
        FpsDealerApplication app=new FpsDealerApplication();
        app.setDealerName(dealerName); app.setShopName(shopName); app.setLicenseNumber(license);
        app.setEmail(email); app.setMobile(mobile); app.setDistrict(district); app.setMandalVillage(mandal);
        app.setShopAddress(address); app.setLatitude(lat); app.setLongitude(lon); app.setStatus("PENDING");
        applications.save(app);
        User user=new User(); user.setFullName(dealerName); user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password)); user.setRole("FPS_DEALER"); user.setStatus("PENDING");
        users.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message","Ration-shop dealer application submitted. Your account will be enabled after administrator approval.",
                "status","PENDING","email",email));
    }

    @GetMapping
    public ResponseEntity<?> list(Authentication auth) {
        if (!isAdmin(auth)) return ResponseEntity.status(403).body(Map.of("message","Administrator access required."));
        return ResponseEntity.ok(applications.findAll());
    }

    @PutMapping("/{id}/decision")
    @Transactional
    public ResponseEntity<?> decide(@PathVariable long id,@RequestBody Map<String,Object> body,Authentication auth) {
        if (!isAdmin(auth)) return ResponseEntity.status(403).body(Map.of("message","Only administrators can review dealer applications."));
        String decision=value(body,"status").toUpperCase(Locale.ROOT);
        String reason=value(body,"rejectionReason");
        if (!decision.equals("APPROVED")&&!decision.equals("REJECTED"))
            return ResponseEntity.badRequest().body(Map.of("message","Decision must be APPROVED or REJECTED."));
        if (decision.equals("REJECTED")&&(reason.isBlank()||reason.length()>500))
            return ResponseEntity.badRequest().body(Map.of("message","Provide a rejection reason of 1 to 500 characters."));
        FpsDealerApplication app=applications.findById(id).orElse(null);
        if (app==null) return ResponseEntity.notFound().build();
        if (decision.equals(app.getStatus())) return ResponseEntity.ok(Map.of("message","This decision has already been recorded.","status",app.getStatus(),"fpsId",app.getFpsId()==null?"":app.getFpsId()));
        if ("APPROVED".equals(app.getStatus())||"REJECTED".equals(app.getStatus()))
            return ResponseEntity.status(409).body(Map.of("message","A final dealer decision has already been recorded."));
        User user=users.findByEmail(app.getEmail()).orElse(null);
        if (user==null) return ResponseEntity.status(409).body(Map.of("message","Dealer login account was not found; the application cannot be decided."));
        app.setStatus(decision); app.setDecidedAt(LocalDateTime.now()); app.setRejectionReason(decision.equals("REJECTED")?reason:null);
        if (decision.equals("APPROVED")) {
            app.setFpsId("FPS-"+UUID.randomUUID().toString().replace("-","").substring(0,12).toUpperCase(Locale.ROOT));
            user.setStatus("ACTIVE");
        } else user.setStatus("REJECTED");
        applications.save(app); users.save(user);
        try {
            String message=decision.equals("APPROVED")
                ? "<p>Dear "+escape(app.getDealerName())+",</p><p>Your ration-shop dealer application has been approved.</p><p><b>Shop:</b> "+escape(app.getShopName())+"<br><b>FPS ID:</b> "+app.getFpsId()+"</p><p>You may now sign in through the Ration Shop Login page.</p>"
                : "<p>Dear "+escape(app.getDealerName())+",</p><p>Your ration-shop dealer application was not approved.</p><p><b>Reason:</b> "+escape(reason)+"</p>";
            notifications.sendEmail(app.getEmail(),"Ration Portal - Dealer Application "+decision,message);
        } catch(Exception ex) { System.err.println("FPS_DEALER_DECISION_EMAIL_FAILED applicationId="+id+" errorType="+ex.getClass().getSimpleName()); }
        try { jdbc.update("INSERT INTO audit_logs(event_type,actor_email,target_reference,outcome,details) VALUES (?,?,?,?,?)",
                "FPS_DEALER_"+decision,auth.getName(),"FPS-APP-"+id,"SUCCESS","Dealer application decision recorded."); }
        catch(Exception ex){ System.err.println("AUDIT_LOG_WRITE_FAILED event=FPS_DEALER_"+decision); }
        Map<String,Object> response=new LinkedHashMap<>(); response.put("message","Dealer application decision recorded.");
        response.put("status",app.getStatus()); response.put("fpsId",app.getFpsId()); response.put("rejectionReason",app.getRejectionReason());
        return ResponseEntity.ok(response);
    }

    private boolean isAdmin(Authentication auth){return auth!=null&&auth.isAuthenticated()&&auth.getAuthorities().stream().anyMatch(a->"ROLE_ADMIN".equals(a.getAuthority()));}
    private String value(Map<String,Object> body,String key){if(body==null)return "";Object v=body.get(key);return v==null?"":v.toString().trim();}
    private Double number(Object value){try{return value==null?null:Double.valueOf(value.toString());}catch(NumberFormatException ex){return null;}}
    private String escape(String s){return s==null?"":s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;").replace("\"","&quot;").replace("'","&#39;");}
    private static final class ListFields { static boolean blank(String... values){for(String value:values)if(value==null||value.isBlank())return true;return false;} }
}
