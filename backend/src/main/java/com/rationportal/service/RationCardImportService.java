package com.rationportal.service;

import com.rationportal.model.RationCard;
import com.rationportal.repository.RationCardRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

@Service
public class RationCardImportService {
    private static final long DEFAULT_MAX_FILE_BYTES = 5L * 1024 * 1024;
    private static final int MAX_ROWS = 10_000;
    private static final Pattern CARD_NUMBER = Pattern.compile("^[0-9]{12}$");
    private static final Pattern EMAIL = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private static final Pattern MOBILE = Pattern.compile("^(?:\\+91)?[6-9][0-9]{9}$");
    private static final Set<String> ALLOWED_STATUSES = Set.of("PENDING", "APPROVED", "ACTIVE", "REJECTED");

    private final RationCardRepository repository;
    private final long maxFileBytes;

    public RationCardImportService(
            RationCardRepository repository,
            @Value("${ration.import.max-file-bytes:5242880}") long maxFileBytes) {
        this.repository = repository;
        this.maxFileBytes = maxFileBytes > 0 ? maxFileBytes : DEFAULT_MAX_FILE_BYTES;
    }

    @Transactional
    public int importCsv(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Choose a non-empty CSV file.");
        }
        if (file.getSize() > maxFileBytes) {
            throw new IllegalArgumentException("The CSV file exceeds the allowed size limit.");
        }
        String filename = file.getOriginalFilename();
        if (filename != null && !filename.isBlank() && !filename.toLowerCase(Locale.ROOT).endsWith(".csv")) {
            throw new IllegalArgumentException("Only CSV files are supported.");
        }

        final String csv;
        try {
            csv = new String(file.getBytes(), StandardCharsets.UTF_8);
        } catch (Exception ex) {
            throw new IllegalArgumentException("The CSV file could not be read.");
        }

        List<List<String>> records = parseCsv(csv);
        if (records.isEmpty()) {
            throw new IllegalArgumentException("The CSV file is empty.");
        }
        List<String> header = records.get(0);
        Map<String, Integer> columns = indexHeaders(header);
        for (String required : List.of("rationcardnumber", "cardtype", "headoffamily", "address", "district", "state")) {
            if (!columns.containsKey(required)) {
                throw new IllegalArgumentException("CSV is missing required column: " + required + ".");
            }
        }

        List<RationCard> cards = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        for (int index = 1; index < records.size(); index++) {
            List<String> row = records.get(index);
            if (row.size() == 1 && row.get(0).isBlank()) continue;
            int rowNumber = index + 1;
            if (cards.size() >= MAX_ROWS) {
                throw new IllegalArgumentException("The CSV exceeds the maximum of " + MAX_ROWS + " data rows.");
            }
            if (row.size() > header.size()) {
                throw rowError(rowNumber, "contains more fields than the header.");
            }

            String number = value(row, columns, "rationcardnumber");
            String type = value(row, columns, "cardtype");
            String head = value(row, columns, "headoffamily");
            String address = value(row, columns, "address");
            String district = value(row, columns, "district");
            String state = value(row, columns, "state");
            String status = value(row, columns, "status");
            String ownerEmail = value(row, columns, "owneremail").toLowerCase(Locale.ROOT);
            String mobile = value(row, columns, "mobile");
            String pincode = value(row, columns, "pincode");

            if (!CARD_NUMBER.matcher(number).matches()) throw rowError(rowNumber, "must contain a 12-digit ration card number.");
            if (!seen.add(number)) throw rowError(rowNumber, "duplicates a ration card number in this file.");
            if (repository.existsByRationCardNumber(number)) throw rowError(rowNumber, "contains a ration card number that already exists.");
            if (type.isBlank() || type.length() > 20) throw rowError(rowNumber, "has an invalid card type.");
            if (head.isBlank() || head.length() > 120) throw rowError(rowNumber, "has an invalid head-of-family name.");
            if (address.isBlank()) throw rowError(rowNumber, "requires an address.");
            if (district.isBlank() || district.length() > 100) throw rowError(rowNumber, "has an invalid district.");
            if (state.isBlank() || state.length() > 100) throw rowError(rowNumber, "has an invalid state.");

            if (status.isBlank()) status = "PENDING";
            status = status.toUpperCase(Locale.ROOT);
            if (!ALLOWED_STATUSES.contains(status)) throw rowError(rowNumber, "has an unsupported status.");

            if (!ownerEmail.isBlank() && !EMAIL.matcher(ownerEmail).matches()) throw rowError(rowNumber, "has an invalid owner email.");
            if (!mobile.isBlank() && !MOBILE.matcher(mobile).matches()) throw rowError(rowNumber, "has an invalid mobile number.");
            if (!pincode.isBlank() && !pincode.matches("^[0-9]{6}$")) throw rowError(rowNumber, "has an invalid six-digit pincode.");

            RationCard card = new RationCard();
            card.setRationCardNumber(number);
            card.setCardType(type);
            card.setHeadOfFamily(head);
            card.setAddress(address);
            card.setDistrict(district);
            card.setState(state);
            card.setStatus(status);
            card.setApprovalStatus(status);
            if (!ownerEmail.isBlank()) card.setOwnerEmail(ownerEmail);
            if (!mobile.isBlank()) card.setMobile(mobile);
            if (!pincode.isBlank()) card.setPincode(pincode);
            cards.add(card);
        }

        if (cards.isEmpty()) {
            throw new IllegalArgumentException("The CSV contains no valid data rows to import.");
        }
        repository.saveAll(cards);
        return cards.size();
    }

    private Map<String, Integer> indexHeaders(List<String> header) {
        Map<String, Integer> columns = new HashMap<>();
        for (int i = 0; i < header.size(); i++) {
            String normalized = normalizeHeader(header.get(i));
            if (normalized.isBlank()) throw new IllegalArgumentException("CSV contains an empty column header.");
            String canonical = canonicalHeader(normalized);
            if (canonical == null) {
                throw new IllegalArgumentException("CSV contains an unsupported column header.");
            }
            if (columns.putIfAbsent(canonical, i) != null) {
                throw new IllegalArgumentException("CSV contains a duplicate column header.");
            }
        }
        return columns;
    }

    private String canonicalHeader(String header) {
        return switch (header) {
            case "rationcardnumber", "rationnumber", "cardnumber" -> "rationcardnumber";
            case "cardtype" -> "cardtype";
            case "headoffamily", "applicantname", "nameofhead" -> "headoffamily";
            case "address", "shopaddress" -> "address";
            case "district" -> "district";
            case "state" -> "state";
            case "status", "approvalstatus" -> "status";
            case "owneremail", "registeredemail", "email" -> "owneremail";
            case "mobile", "mobilenumber", "contactnumber" -> "mobile";
            case "pincode", "postalcode", "zipcode" -> "pincode";
            default -> null;
        };
    }

    private String normalizeHeader(String value) {
        if (value == null) return "";
        return value.replace("\uFEFF", "").trim().toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
    }

    private String value(List<String> row, Map<String, Integer> columns, String key) {
        Integer index = columns.get(key);
        if (index == null || index >= row.size() || row.get(index) == null) return "";
        return row.get(index).trim();
    }

    private IllegalArgumentException rowError(int row, String message) {
        return new IllegalArgumentException("CSV row " + row + " " + message);
    }

    /**
     * Small RFC-4180-compatible parser supporting quoted commas, escaped
     * double quotes, CRLF/LF line endings, and newlines inside quoted fields.
     */
    private List<List<String>> parseCsv(String input) {
        List<List<String>> records = new ArrayList<>();
        List<String> row = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean quoted = false;
        boolean closedQuote = false;
        boolean rowHasData = false;

        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            if (quoted) {
                if (ch == '"') {
                    if (i + 1 < input.length() && input.charAt(i + 1) == '"') {
                        field.append('"');
                        i++;
                    } else {
                        quoted = false;
                        closedQuote = true;
                    }
                } else {
                    field.append(ch);
                }
                continue;
            }

            if (closedQuote && ch != ',' && ch != '\r' && ch != '\n' && ch != ' ' && ch != '\t') {
                throw new IllegalArgumentException("CSV contains invalid characters after a quoted field.");
            }
            if (ch == '"' && field.length() == 0 && !closedQuote) {
                quoted = true;
                rowHasData = true;
            } else if (ch == ',') {
                row.add(field.toString());
                field.setLength(0);
                closedQuote = false;
                rowHasData = true;
            } else if (ch == '\r' || ch == '\n') {
                if (ch == '\r' && i + 1 < input.length() && input.charAt(i + 1) == '\n') i++;
                row.add(field.toString());
                records.add(row);
                row = new ArrayList<>();
                field.setLength(0);
                closedQuote = false;
                rowHasData = false;
            } else if (!closedQuote || ch != ' ') {
                field.append(ch);
                if (!Character.isWhitespace(ch)) rowHasData = true;
            }
        }

        if (quoted) throw new IllegalArgumentException("CSV contains an unterminated quoted field.");
        if (rowHasData || field.length() > 0 || !row.isEmpty() || closedQuote) {
            row.add(field.toString());
            records.add(row);
        }
        return records;
    }
}
