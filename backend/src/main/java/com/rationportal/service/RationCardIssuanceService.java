package com.rationportal.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.rationportal.model.FamilyMember;
import com.rationportal.model.RationCard;
import com.rationportal.model.RationCardApplication;
import com.rationportal.repository.FamilyMemberRepository;
import com.rationportal.repository.RationCardRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class RationCardIssuanceService {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final ZoneId IST = ZoneId.of("Asia/Kolkata");
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
    private final RationCardRepository cards;
    private final FamilyMemberRepository members;
    private final ObjectMapper mapper;
    private final NotificationService notifications;
    private final BigDecimal riceKg;
    private final String apiBase;

    public RationCardIssuanceService(RationCardRepository cards, FamilyMemberRepository members,
            ObjectMapper mapper, NotificationService notifications,
            @Value("${ration.entitlement.kg-per-member:6}") BigDecimal riceKg,
            @Value("${app.public-api-base-url:https://ration-portal-backend.onrender.com}") String apiBase) {
        this.cards = cards; this.members = members; this.mapper = mapper;
        this.notifications = notifications; this.riceKg = riceKg;
        this.apiBase = apiBase.replaceAll("/+$", "");
    }

    @Transactional
    public void processApplicationStatus(RationCardApplication app) {
        if (app == null || app.getApplicationNumber() == null) throw new IllegalArgumentException("Application is required.");
        String status = app.getStatus() == null ? "SUBMITTED" : app.getStatus().trim().toUpperCase(Locale.ROOT);
        Optional<RationCard> found = findFor(app);
        if (!"APPROVED".equals(status)) {
            found.ifPresent(card -> {
                String cardStatus = "REJECTED".equals(status) ? "REJECTED" : "PENDING";
                card.setStatus(cardStatus); card.setApprovalStatus(cardStatus); cards.save(card);
            });
            if ("REJECTED".equals(status)) app.setCompletionStatus("NOT_APPLICABLE");
            return;
        }
        String appNo = app.getApplicationNumber().trim();
        RationCard card = found.orElseGet(RationCard::new);
        card.setApplicationNumber(appNo);
        if (card.getRationCardNumber() == null || !card.getRationCardNumber().matches("\\d{12}")) card.setRationCardNumber(newNumber());
        card.setCardType(required(app.getCardType(), "Application card type is missing."));
        card.setHeadOfFamily(required(app.getApplicantName(), "Application applicant name is missing."));
        card.setAddress(app.getAddress()); card.setDistrict(app.getDistrict()); card.setState(app.getState());
        card.setOwnerEmail(required(app.getEmail(), "Application email is missing.").trim().toLowerCase(Locale.ROOT));
        card.setMobile(app.getMobile()); card.setPincode(app.getPincode());
        card.setStatus("ACTIVE"); card.setApprovalStatus("ACTIVE");
        if (card.getIssuedAt() == null) card.setIssuedAt(LocalDateTime.now(IST));
        card = cards.saveAndFlush(card);
        List<FamilyMember> family = members.findByRationCardId(card.getId());
        if (family.isEmpty()) family = members.saveAll(makeMembers(app, card));
        app.setIssuedRationCardNumber(card.getRationCardNumber());
        app.setCompletionStatus("COMPLETED");
        if (card.getDocumentEmailedAt() == null) {
            try {
                byte[] pdf = makePdf(card, family);
                String html = "<div style='font-family:Arial;color:#17365f'><div style='height:6px;background:linear-gradient(90deg,#ff9933 0 33%,#fff 33% 66%,#138808 66%)'></div><h2>Ration Portal — Digital Ration Card</h2><p>Dear "
                    + html(card.getHeadOfFamily()) + ",</p><p>Your application is approved and your family's ration card has been issued.</p><p><b>Ration card number:</b> "
                    + card.getRationCardNumber() + "<br><b>Card type:</b> " + html(card.getCardType())
                    + "</p><p>Your digital ration card PDF is attached.</p><small>Automated message from Ration Portal.</small></div>";
                notifications.sendEmailWithAttachment(card.getOwnerEmail(), "Ration Portal - Your Digital Ration Card",
                    html, pdf, "ration-card-" + card.getRationCardNumber() + ".pdf");
                card.setDocumentEmailedAt(LocalDateTime.now(IST));
                cards.save(card);
            } catch (Exception ex) {
                System.err.println("RATION_CARD_EMAIL_FAILED application=" + appNo + " errorType=" + ex.getClass().getSimpleName());
            }
        }
    }

    @Transactional(readOnly = true)
    public byte[] downloadPdf(String number, String email, boolean admin) {
        RationCard card = cards.findByRationCardNumber(number).orElse(null);
        if (card == null || card.getStatus() == null
                || !("ACTIVE".equalsIgnoreCase(card.getStatus()) || "APPROVED".equalsIgnoreCase(card.getStatus()))) {
            throw new IllegalArgumentException("No active ration card was found.");
        }
        if (!admin && (email == null || card.getOwnerEmail() == null
                || !card.getOwnerEmail().equalsIgnoreCase(email))) {
            throw new SecurityException("You are not authorized to download this ration card.");
        }
        try {
            return makePdf(card, members.findByRationCardId(card.getId()));
        } catch (Exception ex) {
            throw new IllegalStateException("The ration-card PDF could not be generated.", ex);
        }
    }

    private Optional<RationCard> findFor(RationCardApplication app) {
        Optional<RationCard> linked = cards.findByApplicationNumber(app.getApplicationNumber());
        return linked.isPresent() ? linked : cards.findByRationCardNumber("RC-" + app.getApplicationNumber());
    }

    private String newNumber() {
        for (int attempt=0; attempt<30; attempt++) {
            char[] value = new char[12]; value[0]=(char)('1'+RANDOM.nextInt(9));
            for (int i=1;i<12;i++) value[i]=(char)('0'+RANDOM.nextInt(10));
            String number = new String(value);
            if (!cards.existsByRationCardNumber(number)) return number;
        }
        throw new IllegalStateException("Unable to allocate a unique ration-card number. Retry approval.");
    }

    private List<FamilyMember> makeMembers(RationCardApplication app, RationCard card) {
        try {
            List<Map<String,Object>> rows = mapper.readValue(app.getFamilyMembersData(), new TypeReference<>() {});
            if (rows.isEmpty()) throw new IllegalStateException("No family members were saved in the application.");
            List<FamilyMember> result = new ArrayList<>();
            for (Map<String,Object> row : rows) {
                String name = text(row,"name"), ageValue=text(row,"age");
                if (name.isBlank()) throw new IllegalStateException("A family member name is missing.");
                int age;
                try { age=Integer.parseInt(ageValue); } catch (NumberFormatException e) { throw new IllegalStateException("A family member age is invalid."); }
                if (age<0 || age>120) throw new IllegalStateException("A family member age is invalid.");
                FamilyMember m = new FamilyMember();
                m.setRationCard(card); m.setMemberId("MEM-"+UUID.randomUUID().toString().replace("-","").substring(0,16).toUpperCase(Locale.ROOT));
                m.setName(name); m.setAge(age); m.setGender(text(row,"gender")); m.setRelationship(text(row,"relationship")); m.setRiceQuotaKg(riceKg);
                result.add(m);
            }
            return result;
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new IllegalStateException("Saved family-member data could not be read.",e);
        }
    }

    private byte[] makePdf(RationCard card, List<FamilyMember> family) throws Exception {
        ByteArrayOutputStream bytes=new ByteArrayOutputStream();
        Document doc=new Document(PageSize.A4,36,36,32,32); PdfWriter.getInstance(doc,bytes); doc.open();
        PdfPTable bands=new PdfPTable(new float[]{1,1,1}); bands.setWidthPercentage(100); bands.setSpacingAfter(16);
        for (java.awt.Color c : List.of(new java.awt.Color(255,153,51),java.awt.Color.WHITE,new java.awt.Color(19,136,8))) {
            PdfPCell cell=new PdfPCell(new Phrase(" ")); cell.setBorder(Rectangle.NO_BORDER); cell.setBackgroundColor(c); cell.setFixedHeight(5); bands.addCell(cell);
        }
        doc.add(bands);
        Font heading=new Font(Font.HELVETICA,16,Font.BOLD,new java.awt.Color(8,44,92));
        Font normal=new Font(Font.HELVETICA,9,Font.NORMAL,new java.awt.Color(35,50,65));
        Font bold=new Font(Font.HELVETICA,9,Font.BOLD,new java.awt.Color(35,50,65));
        Paragraph title=new Paragraph("PUBLIC DISTRIBUTION SYSTEM — DIGITAL RATION CARD",heading); title.setAlignment(Element.ALIGN_CENTER); title.setSpacingAfter(16); doc.add(title);
        PdfPTable detail=new PdfPTable(new float[]{1,2}); detail.setWidthPercentage(100); detail.setSpacingAfter(12);
        detail(detail,"RATION CARD NUMBER",card.getRationCardNumber(),bold,normal);
        detail(detail,"CARD TYPE",card.getCardType(),bold,normal); detail(detail,"HEAD OF FAMILY",card.getHeadOfFamily(),bold,normal);
        detail(detail,"ADDRESS",card.getAddress(),bold,normal); detail(detail,"DISTRICT / STATE",dash(card.getDistrict())+" / "+dash(card.getState()),bold,normal);
        detail(detail,"PINCODE",card.getPincode(),bold,normal); detail(detail,"REGISTERED MOBILE",card.getMobile(),bold,normal);
        detail(detail,"ISSUE DATE (IST)",card.getIssuedAt()==null?"—":card.getIssuedAt().format(DATE),bold,normal); doc.add(detail);
        Paragraph ft=new Paragraph("REGISTERED FAMILY MEMBERS",new Font(Font.HELVETICA,11,Font.BOLD,new java.awt.Color(8,44,92))); ft.setSpacingBefore(8); ft.setSpacingAfter(7); doc.add(ft);
        PdfPTable table=new PdfPTable(new float[]{.5f,2, .6f,1,1.2f}); table.setWidthPercentage(100); table.setHeaderRows(1);
        for(String h:List.of("No.","Name","Age","Gender","Relationship")){PdfPCell c=new PdfPCell(new Phrase(h,new Font(Font.HELVETICA,8,Font.BOLD,java.awt.Color.WHITE)));c.setBackgroundColor(new java.awt.Color(8,44,92));c.setPadding(6);table.addCell(c);}
        int n=1; for(FamilyMember m:family){table.addCell(new Phrase(String.valueOf(n++),normal));table.addCell(new Phrase(dash(m.getName()),normal));table.addCell(new Phrase(m.getAge()==null?"—":m.getAge().toString(),normal));table.addCell(new Phrase(dash(m.getGender()),normal));table.addCell(new Phrase(dash(m.getRelationship()),normal));}
        doc.add(table);
        Paragraph quota=new Paragraph("Monthly family entitlement: "+riceKg.multiply(BigDecimal.valueOf(family.size())).stripTrailingZeros().toPlainString()+" kg (6 kg per member)",bold); quota.setSpacingBefore(10); doc.add(quota);
        ByteArrayOutputStream qr=new ByteArrayOutputStream(); String verify=apiBase+"/api/ration-cards/verify/"+card.getRationCardNumber();
        MatrixToImageWriter.writeToStream(new QRCodeWriter().encode(verify,BarcodeFormat.QR_CODE,180,180),"PNG",qr);
        Image image=Image.getInstance(qr.toByteArray()); image.scaleToFit(105,105); image.setAlignment(Element.ALIGN_CENTER); doc.add(image);
        Paragraph note=new Paragraph("Scan the QR code to verify this card online.\nComputer-generated document; no physical signature is required.",new Font(Font.HELVETICA,8,Font.ITALIC,java.awt.Color.GRAY)); note.setAlignment(Element.ALIGN_CENTER); note.setSpacingBefore(8); doc.add(note);
        doc.close(); return bytes.toByteArray();
    }

    private void detail(PdfPTable table,String label,String value,Font bold,Font normal){
        PdfPCell a=new PdfPCell(new Phrase(label,bold));a.setBackgroundColor(new java.awt.Color(240,245,249));a.setPadding(6);
        PdfPCell b=new PdfPCell(new Phrase(dash(value),normal));b.setPadding(6);table.addCell(a);table.addCell(b);
    }
    private String text(Map<String,Object> row,String key){Object v=row.get(key);return v==null?"":v.toString().trim();}
    private String required(String v,String msg){if(v==null||v.isBlank())throw new IllegalStateException(msg);return v.trim();}
    private String dash(String v){return v==null||v.isBlank()?"—":v.trim();}
    private String html(String v){if(v==null)return "";return v.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;").replace("\"","&quot;").replace("'","&#39;");}
}
